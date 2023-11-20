package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.R;
import com.cedricapp.common.Common;
import com.cedricapp.interfaces.CalendarInterface;
import com.cedricapp.model.CalendarModel;
import com.cedricapp.utils.HorizontalCalendarUtil;
import com.cedricapp.utils.SessionUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.CalendarViewHolder> {

    Context context;
    ArrayList<CalendarModel> calendarModelArrayList;

    String TAG = "HorizontalCalendar_LOG";

    CalendarInterface calendarInterface;
    int width;

    public HorizontalCalendarAdapter(Context context, ArrayList<CalendarModel> calendarModelArrayList, CalendarInterface calendarInterface) {
        this.context = context;
        this.calendarModelArrayList = calendarModelArrayList;
        this.calendarInterface = calendarInterface;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        width = screenWidth / 7;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_horizontal, parent, false);
        return new CalendarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (SessionUtil.getlangCode(context).matches("sv")) {
            holder.dateTV.setText(new SimpleDateFormat("EEE", new Locale("sv")).format(calendarModelArrayList.get(position).getDate()));
        } else {
            holder.dateTV.setText(new SimpleDateFormat("EEE", new Locale("en")).format(calendarModelArrayList.get(position).getDate()));
        }//holder.dayTV.setText(String.valueOf(calendarModelArrayList.get(position).getDay()));
        holder.dayTV.setText(String.valueOf(calendarModelArrayList.get(position).getDay()));

        if (calendarModelArrayList.get(position).isSelected() || SessionUtil.getSelectedDate(context).equals(calendarModelArrayList.get(position).getDate().toString())) {
            calendarModelArrayList.get(position).setSelected(true);
            holder.dayTV.setBackground(ContextCompat.getDrawable(context, R.drawable.square_horizontal_calendar_selection));
        } else {
            calendarModelArrayList.get(position).setSelected(false);
            holder.dayTV.setBackground(ContextCompat.getDrawable(context, R.drawable.square_horizontal_calendar_un_selection));
        }

        holder.calendarViewLLC.getLayoutParams().width = width;
        if (calendarModelArrayList.get(position).isActivated()) {
            holder.calendarViewLLC.setEnabled(true);
            holder.dateTV.setTextColor(context.getColor(R.color.black));
            holder.dayTV.setTextColor(context.getColor(R.color.black));
        } else {
            holder.calendarViewLLC.setEnabled(false);
            holder.dateTV.setTextColor(context.getColor(R.color.grey_color));
            holder.dayTV.setTextColor(context.getColor(R.color.grey_color));
        }

        /*if (calendarModelArrayList.get(position).isToday()) {
            holder.dayTV.setTextColor(context.getColor(R.color.pure_red));
        } else {
            if (calendarModelArrayList.get(position).isActivated())
                holder.dayTV.setTextColor(context.getColor(R.color.black));
            else
                holder.dayTV.setTextColor(context.getColor(R.color.grey_color));
        }*/


        holder.calendarViewLLC.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                if (calendarModelArrayList.get(position).isActivated()) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Selected date is " + calendarModelArrayList.get(position).getDate().toString());
                    }

                    for (int i = 0; i < calendarModelArrayList.size(); i++) {
                        if (position != i) {
                            calendarModelArrayList.get(i).setSelected(false);
                        }
                    }

                    calendarModelArrayList.get(position).setSelected(true);
                    HorizontalCalendarUtil.selectedViewIndexForScroll = position;
                    SessionUtil.setSelectedDate(context, calendarModelArrayList.get(position).getDate().toString());
                    holder.dayTV.setBackground(ContextCompat.getDrawable(context, R.drawable.square_horizontal_calendar_selection));
                    calendarInterface.onDateSelection(calendarModelArrayList.get(position).getDate());
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return calendarModelArrayList.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        LinearLayoutCompat calendarViewLLC;
        TextView dayTV;
        TextView dateTV;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            calendarViewLLC = itemView.findViewById(R.id.dateSelectLLC);
            dateTV = itemView.findViewById(R.id.dayTV);
            dayTV = itemView.findViewById(R.id.dateTV);
        }
    }
}
