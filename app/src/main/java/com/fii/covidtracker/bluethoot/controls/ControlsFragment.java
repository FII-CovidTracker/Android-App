/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package com.fii.covidtracker.bluethoot.controls;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.fii.covidtracker.CovidTrackerApp;
import com.fii.covidtracker.R;
import com.fii.covidtracker.bluethoot.util.DialogUtil;
import com.fii.covidtracker.bluethoot.util.RequirementsUtil;
import com.fii.covidtracker.network.ResourceStatus;
import com.fii.covidtracker.repositories.models.regions.Region;
import com.fii.covidtracker.viewmodels.ViewModelProviderFactory;
import com.fii.covidtracker.viewmodels.region.RegionViewModel;

import org.dpppt.android.sdk.DP3T;
import org.dpppt.android.sdk.InfectionStatus;
import org.dpppt.android.sdk.TracingStatus;
import org.dpppt.android.sdk.backend.ResponseCallback;
import org.dpppt.android.sdk.backend.models.ExposeeAuthMethodJson;
import org.dpppt.android.sdk.internal.database.Database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class ControlsFragment extends DaggerFragment implements AdapterView.OnItemSelectedListener {

	private static final String TAG = ControlsFragment.class.getCanonicalName();

	@Inject
	ViewModelProviderFactory providerFactory;

	private RegionViewModel regionViewModel;
	private List<Region> regions;

	private SharedPreferences prefs;
	private static final int REQUEST_CODE_PERMISSION_LOCATION = 1;
	private static final int REQUEST_CODE_REPORT_EXPOSED = 2;

	private static final DateFormat DATE_FORMAT_SYNC = SimpleDateFormat.getDateTimeInstance();

	private static final String REGEX_VALIDITY_AUTH_CODE = "\\w+";
	private static final int EXPOSED_MIN_DATE_DIFF = -21;

	private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				checkPermissionRequirements();
				updateSdkStatus();
			}
		}
	};

	private BroadcastReceiver sdkReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateSdkStatus();
		}
	};

	public static ControlsFragment newInstance() {
		return new ControlsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		regionViewModel = new ViewModelProvider((ViewModelStoreOwner) this, providerFactory)
				.get(RegionViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setupUi(view);

		prefs = getActivity().getSharedPreferences(
				"com.fii.covidtracker.app", Context.MODE_PRIVATE);

		subscribeToRegions(false);
	}

	private void subscribeToRegions(boolean forceFetch) {
		regionViewModel.getRegionsResource(forceFetch).removeObservers((LifecycleOwner) getActivity());
		regionViewModel.getRegionsResource(forceFetch).observe((LifecycleOwner) getActivity(), regionsResource -> {
			if (regionsResource != null) {
				switch (regionsResource.getStatus()) {
					case LOADING:
						if (regionsResource.getData() != null) {
							processRegionList(regionsResource.getData());
						} else {
							processRegionList(ResourceStatus.LOADING);
						}
						break;
					case SUCCESS:
						if (regionsResource.getData() != null) {
							processRegionList(regionsResource.getData());
						} else {
							processRegionList(ResourceStatus.EMPTY);
						}
						break;
					case ERROR:
						Toast.makeText(getContext(), "Can't connect to our server!", Toast.LENGTH_SHORT).show();
						if (regionsResource.getData() == null) {
							processRegionList(ResourceStatus.ERROR);
						}
						break;
				}
			}
		});
	}

	private void processRegionList(List<Region> regions) {
		this.regions = regions;
		Spinner spinner = (Spinner) getActivity().findViewById(R.id.home_region_spinner);

		ArrayAdapter<Region> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, this.regions);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		int regionId = prefs.getInt("regionId", 0);
		Log.d(TAG, "processRegionList: " + regionId);
		Optional<Region> region = regions.stream()
				.filter(r -> r.id == regionId)
				.findFirst();
		Log.d(TAG, "processRegionList: " + region);
		if(region.isPresent()){
			Log.d(TAG, "processRegionList: " + regions.indexOf(region.get()));
			spinner.setSelection(regions.indexOf(region.get()));
		}
		else{
			spinner.setSelection(0);
		}
		spinner.setOnItemSelectedListener(this);
	}

	private void processRegionList(ResourceStatus status) {
		//TODO: show this better
		Toast.makeText(getContext(), status.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		getContext().registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		getContext().registerReceiver(sdkReceiver, DP3T.getUpdateIntentFilter());
		checkPermissionRequirements();
		updateSdkStatus();
	}

	@Override
	public void onPause() {
		super.onPause();
		getContext().unregisterReceiver(bluetoothReceiver);
		getContext().unregisterReceiver(sdkReceiver);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == REQUEST_CODE_REPORT_EXPOSED) {
			if (resultCode == Activity.RESULT_OK) {
				long onsetDate = data.getLongExtra(ExposedDialogFragment.RESULT_EXTRA_DATE_MILLIS, -1);
				String authCodeBase64 = data.getStringExtra(ExposedDialogFragment.RESULT_EXTRA_AUTH_CODE_INPUT_BASE64);
				sendInfectedUpdate(getContext(), new Date(onsetDate), authCodeBase64);
			}
		}
	}

	private void setupUi(View view) {
		Button locationButton = view.findViewById(R.id.home_button_location);
		locationButton.setOnClickListener(
				v -> requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
						REQUEST_CODE_PERMISSION_LOCATION));

		Button batteryButton = view.findViewById(R.id.home_button_battery_optimization);
		batteryButton.setOnClickListener(
				v -> startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
						Uri.parse("package:" + getContext().getPackageName()))));

		Button bluetoothButton = view.findViewById(R.id.home_button_bluetooth);
		bluetoothButton.setOnClickListener(v -> {
			if (BluetoothAdapter.getDefaultAdapter() != null) {
				BluetoothAdapter.getDefaultAdapter().enable();
			} else {
				Toast.makeText(getContext(), "No BluetoothAdapter found!", Toast.LENGTH_LONG).show();
			}
		});

		Button refreshButton = view.findViewById(R.id.home_button_sync);
		refreshButton.setOnClickListener(v -> resyncSdk());

		Button buttonClearData = view.findViewById(R.id.home_button_clear_data);
		buttonClearData.setOnClickListener(v -> {
			DialogUtil.showConfirmDialog(v.getContext(), R.string.dialog_clear_data_title,
					(dialog, which) -> {
						DP3T.clearData(v.getContext(), () ->
								new Handler(getContext().getMainLooper()).post(this::updateSdkStatus));
						CovidTrackerApp.initDP3T(v.getContext());
					});
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
			checkPermissionRequirements();
			updateSdkStatus();
		}
	}

	private void checkPermissionRequirements() {
		View view = getView();
		Context context = getContext();
		if (view == null || context == null) return;

		boolean locationGranted = RequirementsUtil.isLocationPermissionGranted(context);
		Button locationButton = view.findViewById(R.id.home_button_location);
		locationButton.setEnabled(!locationGranted);
		locationButton.setText(locationGranted ? R.string.req_location_permission_granted
											   : R.string.req_location_permission_ungranted);

		boolean batteryOptDeactivated = RequirementsUtil.isBatteryOptimizationDeactivated(context);
		Button batteryButton = view.findViewById(R.id.home_button_battery_optimization);
		batteryButton.setEnabled(!batteryOptDeactivated);
		batteryButton.setText(batteryOptDeactivated ? R.string.req_battery_deactivated
													: R.string.req_battery_deactivated);

		boolean bluetoothActivated = RequirementsUtil.isBluetoothEnabled();
		Button bluetoothButton = view.findViewById(R.id.home_button_bluetooth);
		bluetoothButton.setEnabled(!bluetoothActivated);
		bluetoothButton.setText(bluetoothActivated ? R.string.req_bluetooth_active
												   : R.string.req_bluetooth_inactive);
	}

	private void resyncSdk() {
		new Thread(() -> {
			DP3T.sync(getContext());
			new Handler(getContext().getMainLooper()).post(this::updateSdkStatus);
		}).start();
	}

	private void updateSdkStatus() {
		View view = getView();
		Context context = getContext();
		if (context == null || view == null) return;

		TracingStatus status = DP3T.getStatus(context);

		formatStatusString(status);

		Button buttonStartStopTracking = view.findViewById(R.id.home_button_start_stop_tracking);
		boolean isRunning = status.isAdvertising() || status.isReceiving();
		buttonStartStopTracking.setSelected(isRunning);
		buttonStartStopTracking.setText(getString(isRunning ? R.string.button_tracking_stop
															: R.string.button_tracking_start));
		buttonStartStopTracking.setOnClickListener(v -> {
			if (isRunning) {
				DP3T.stop(v.getContext());
			} else {
				DP3T.start(v.getContext());
			}
			updateSdkStatus();
		});

		Button buttonReportInfected = view.findViewById(R.id.home_button_report_infected);
		buttonReportInfected.setEnabled(status.getInfectionStatus() != InfectionStatus.INFECTED);
		buttonReportInfected.setText(R.string.button_report_infected);
		buttonReportInfected.setOnClickListener(
				v -> {
					Calendar minCal = Calendar.getInstance();
					minCal.add(Calendar.DAY_OF_YEAR, EXPOSED_MIN_DATE_DIFF);
					DialogFragment exposedDialog =
							ExposedDialogFragment.newInstance(minCal.getTimeInMillis(), REGEX_VALIDITY_AUTH_CODE);
					exposedDialog.setTargetFragment(this, REQUEST_CODE_REPORT_EXPOSED);
					exposedDialog.show(getParentFragmentManager(), ExposedDialogFragment.class.getCanonicalName());
				});
	}

	private void formatStatusString(TracingStatus status) {

		boolean isTracking = status.isAdvertising() || status.isReceiving();
		TextView trackingStatusTW = getActivity().findViewById(R.id.home_tracking_status);
		if(trackingStatusTW != null) {
			trackingStatusTW.setText(getString(isTracking ?
					R.string.status_tracking_active : R.string.status_tracking_inactive));
			if(isTracking) {
				trackingStatusTW.setTextColor(getResources().getColor(R.color.green));
			}
			else {
				trackingStatusTW.setTextColor(getResources().getColor(R.color.red));
			}
		}

		long lastSyncDateUTC = status.getLastSyncDate();
		String lastSyncDateString =
				lastSyncDateUTC > 0 ? DATE_FORMAT_SYNC.format(new Date(lastSyncDateUTC)) : "n/a";
		TextView syncDateTW = getActivity().findViewById(R.id.home_sync_date);
		if (syncDateTW != null) {
			syncDateTW.setText(getString(R.string.status_last_synced, lastSyncDateString));
		}

		boolean isInfected = status.getInfectionStatus() == InfectionStatus.INFECTED;
		boolean hasBeenExposed = status.getInfectionStatus() == InfectionStatus.EXPOSED;

		TextView infectedStatusTW = getActivity().findViewById(R.id.home_infected_status);
		if(infectedStatusTW != null) {
			if (isInfected) {
				infectedStatusTW.setVisibility(View.VISIBLE);
				infectedStatusTW.setText("INFECTED");
			} else {
				if (hasBeenExposed) {
					infectedStatusTW.setVisibility(View.VISIBLE);
					infectedStatusTW.setText("EXPOSED");
				} else {
					infectedStatusTW.setVisibility(View.GONE);
				}
			}
		}
		TextView contactsTW = getActivity().findViewById(R.id.home_contacts);
		if (contactsTW != null) {
			contactsTW.setText(getString(R.string.status_number_contacts, status.getNumberOfContacts()));
		}

		TextView handshakesTW = getActivity().findViewById(R.id.home_handshakes);
		if (handshakesTW != null) {
			handshakesTW.setText(getString(R.string.status_number_handshakes,
					new Database(getContext()).getHandshakes().size()));
		}
	}

	private void sendInfectedUpdate(Context context, Date onsetDate, String codeInputBase64) {
		setExposeLoadingViewVisible(true);

		DP3T.sendIAmInfected(context, onsetDate, new ExposeeAuthMethodJson(codeInputBase64), new ResponseCallback<Void>() {
			@Override
			public void onSuccess(Void response) {
				DialogUtil.showMessageDialog(context, getString(R.string.dialog_title_success),
						getString(R.string.dialog_message_request_success));
				setExposeLoadingViewVisible(false);
				updateSdkStatus();
			}

			@Override
			public void onError(Throwable throwable) {
				DialogUtil.showMessageDialog(context, getString(R.string.dialog_title_error),
						throwable.getLocalizedMessage());
				Log.e(TAG, throwable.getMessage(), throwable);
				setExposeLoadingViewVisible(false);
			}
		});
	}

	private void setExposeLoadingViewVisible(boolean visible) {
		View view = getView();
		if (view != null) {
			view.findViewById(R.id.home_loading_view_exposed).setVisibility(visible ? View.VISIBLE : View.GONE);
			view.findViewById(R.id.home_button_report_infected).setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		prefs.edit().putInt("regionId", regions.get(position).id).apply();
		Log.d(TAG, "onItemSelected: " + regions.get(position).id);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
