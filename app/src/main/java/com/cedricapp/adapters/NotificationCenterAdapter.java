package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.interfaces.NotificationItemClickListener;
import com.cedricapp.model.NotificationModel;
import com.cedricapp.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class NotificationCenterAdapter extends RecyclerView.Adapter<NotificationCenterAdapter.ViewHolder>{
    ArrayList<NotificationModel> notificationModels;
    NotificationItemClickListener notificationItemClickListener;

    Context context;
    public NotificationCenterAdapter(Context context, ArrayList<NotificationModel> notificationModels, NotificationItemClickListener notificationItemClickListener){
        this.notificationModels = notificationModels;
        this.context = context;
        this.notificationItemClickListener = notificationItemClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.notificationTimeTV.setText(notificationModels.get(position).getTime());
        holder.notificationTV.setText(notificationModels.get(position).getDescription());
        if(notificationModels.get(position).isRead()){
            holder.notification_view.setBackgroundColor(ContextCompat.getColor(context,R.color.notification_read));
        }else {
            holder.notification_view.setBackgroundColor(ContextCompat.getColor(context,R.color.notification_unread));
        }

        holder.notification_view.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                notificationItemClickListener.onNotificationSelection(notificationModels.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final MaterialTextView notificationTV;
        private final MaterialTextView notificationTimeTV;

        private final LinearLayout notification_view;
        public ViewHolder(View view){
            super(view);
            notification_view = view.findViewById(R.id.notification_view);
            notificationTV = view.findViewById(R.id.notificationTV);
            notificationTimeTV = view.findViewById(R.id.notificationTimeTV);
        }

    }
}
