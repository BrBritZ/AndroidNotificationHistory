package com.project.level4.watchnotificationtray;

/**
 * Created by Rob on 20/12/2015.
 */

import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class WearableAdapter extends WearableListView.Adapter {

    private final LayoutInflater mInflater;
    private String mTitle;
    private String mText;

    public WearableAdapter(Context context, String title, String text) {
        mInflater = LayoutInflater.from(context);

        this.mTitle = title;
        this.mText = text;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int i) {
        return new ItemViewHolder(mInflater.inflate(R.layout.listview_item, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder,
                                 int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        CircledImageView circledView = itemViewHolder.mCircledImageView;
        circledView.setImageResource(position);
        TextView textView = itemViewHolder.mItemTextView;
        textView.setText(String.format(mTitle + "\n\n" + mText, position + 1));
    }

    @Override
    public int getItemCount() {
        return 0;
        //mItems.size();
    }

    private static class ItemViewHolder extends WearableListView.ViewHolder {
        private CircledImageView mCircledImageView;
        private TextView mItemTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCircledImageView = (CircledImageView)
                    itemView.findViewById(R.id.circle);
            mItemTextView = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
