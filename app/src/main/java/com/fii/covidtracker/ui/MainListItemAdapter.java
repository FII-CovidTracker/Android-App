package com.fii.covidtracker.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fii.covidtracker.R;
import com.fii.covidtracker.repositories.models.MainListItem;

import org.apache.commons.text.WordUtils;

public class MainListItemAdapter<T extends MainListItem> extends ListAdapter<T, MainListItemAdapter.ViewHolder> {
    public MainListItemAdapter() {
        super(new DiffUtil.ItemCallback<T>() {
                    @Override
                    public boolean areItemsTheSame(
                            @NonNull MainListItem oldT, @NonNull MainListItem newT) {
                        // User properties may have changed if reloaded from the DB, but ID is fixed
                        return oldT.getId() == newT.getId();
                    }
                    @Override
                    public boolean areContentsTheSame(
                            @NonNull MainListItem oldT, @NonNull MainListItem newT) {
                        return oldT.equals(newT);
                    }
                });
    }

    @NonNull
    @Override
    public MainListItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);

        return new MainListItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainListItemAdapter.ViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }
    public

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView leftSubtitle;
        private TextView rightSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            leftSubtitle = itemView.findViewById(R.id.leftSubtitle);
            rightSubtitle = itemView.findViewById(R.id.rightSubtitle);
        }

        void bindTo(MainListItem item) {
            title.setText(WordUtils.capitalizeFully(item.getTitle()));
            leftSubtitle.setText(WordUtils.capitalizeFully(item.getLeftSubtitle()));
            rightSubtitle.setText(WordUtils.capitalizeFully(item.getRightSubtitle()));
        }

    }
}