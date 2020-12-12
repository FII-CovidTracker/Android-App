/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.fii.covidtracker;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.fii.covidtracker.bluethoot.controls.ControlsFragment;
import com.fii.covidtracker.ui.article.ArticleListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupNavigationView();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.main_fragment_container, new ControlsFragment().newInstance())
					.commit();
		}
	}

	private void setupNavigationView() {
		BottomNavigationView navigationView = findViewById(R.id.main_navigation_view);
		navigationView.inflateMenu(R.menu.menu_navigation_main);


		float radius = 64.0f;
		MaterialShapeDrawable bottomNavigationViewBackground = (MaterialShapeDrawable) navigationView.getBackground();
		bottomNavigationViewBackground.setShapeAppearanceModel(
				bottomNavigationViewBackground.getShapeAppearanceModel().toBuilder()
						.setTopRightCorner(CornerFamily.ROUNDED, radius)
						.setTopLeftCorner(CornerFamily.ROUNDED, radius)
						.build());

		navigationView.setOnNavigationItemSelectedListener(item -> {
			switch (item.getItemId()) {
				case R.id.action_bluetooth_controls:
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.main_fragment_container, ControlsFragment.newInstance())
							.commit();
					break;
				case R.id.action_articles:
					Log.d(TAG, "setupNavigationView: pula");
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.main_fragment_container, ArticleListFragment.newInstance())
							.commit();
					break;
			}
			return true;
		});
	}

}
