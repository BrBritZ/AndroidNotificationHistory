package com.project.level4.watchnotificationtray;

/**
 * Created by Rob on 20/12/2015.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

public class WearableAdapter extends WearableListView.Adapter {

    private final LayoutInflater mInflater;
    private LinkedList<NotificationObject> notificationLL;

    public WearableAdapter(Context context, LinkedList<NotificationObject> notificationLinkedList) {
        this.mInflater = LayoutInflater.from(context);
        this.notificationLL = notificationLinkedList;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int i) {
        return new ItemViewHolder(mInflater.inflate(R.layout.listview_item, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position) {
        System.out.println("trying to onBindViewHolder");
        if (position < notificationLL.size()) {
            if (!notificationLL.isEmpty() && notificationLL != null) {
                NotificationObject n0 = notificationLL.get(position);
                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                CircledImageView circledView = itemViewHolder.mCircledImageView;
                if (n0.getIcon() != null){
                    circledView.setImageDrawable(n0.getIcon());
                } else{
                    circledView.setImageResource(R.drawable.ic_action_mail);
                }
                TextView textView = itemViewHolder.mItemTextView;
                textView.setText(n0.getTitle());
                itemViewHolder.itemView.setTag(position);
                System.out.println("Successfuly set onBindViewHolder");
            }
        }
    }

    @Override
    public int getItemCount() {
        return notificationLL.size();
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
