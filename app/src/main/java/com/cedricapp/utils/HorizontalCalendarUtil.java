package com.cedricapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.cedricapp.R;
import com.cedricapp.adapters.HorizontalCalendarAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.SnapToBlock;
import com.cedricapp.interfaces.CalendarInterface;
import com.cedricapp.model.CalendarModel;
import com.google.android.material.textview.MaterialTextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class HorizontalCalendarUtil {
    Context context;

    String startDate;
    String endDate;

    View view;

    RecyclerView recyclerView;

    String TAG = "HorizontalCalendar_LOG";
    int todayDateIndex;


    int scrollTodayDateIndex;
    int scrollPreviousDateIndex;

    public static int selectedViewIndexForScroll;

    MaterialTextView monthTV;

    CalendarInterface calendarInterface;

    static int scrollIndex;

    static int myDX = 0;

    String lang;

    public HorizontalCalendarUtil(Context context, String startDate, String endDate, View view, CalendarInterface calendarInterface) {
        this.context = context;
        this.startDate = startDate;
        this.endDate = endDate;
        this.view = view;
        this.calendarInterface = calendarInterface;
        lang = SessionUtil.getlangCode(context);

        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Subscription start date: " + startDate);
            Log.d(TAG, "Subscription end date: " + endDate);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setCalendarAdapter();
            }
        }, 0);


    }

    ArrayList<CalendarModel> makeListForCalendar() {
        ArrayList<CalendarModel> calendarModelArrayList = new ArrayList<>();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(Common.DATE_FORMAT);
        Date startDateOfsubscription = null;
        Date endDateOfsubscription = null;
        try {
            startDateOfsubscription = dateFormat.parse(startDate);
            endDateOfsubscription = dateFormat.parse(endDate);
        } catch (Exception ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }
        if (startDateOfsubscription != null && endDateOfsubscription != null) {
            Calendar calendarStartDate = Calendar.getInstance();
            calendarStartDate.setTime(startDateOfsubscription);
            calendarStartDate.setFirstDayOfWeek(Calendar.MONDAY);
            int startDayOfWeek = calendarStartDate.get(Calendar.DAY_OF_WEEK) - 2;
            calendarStartDate.add(Calendar.DATE, startDayOfWeek - (startDayOfWeek * 2));

            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Start Subscription day of week: " + startDayOfWeek + ", first day of week is " + calendarStartDate.getFirstDayOfWeek());
            }

            Calendar calendarEndDate = Calendar.getInstance();
            calendarEndDate.setTime(endDateOfsubscription);
            calendarEndDate.setFirstDayOfWeek(Calendar.MONDAY);
            int endDayOfWeek = calendarEndDate.get(Calendar.DAY_OF_WEEK);
            calendarEndDate.add(Calendar.DATE, 8 - endDayOfWeek);
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "End subscription day of week is " + endDayOfWeek + " and End date is " + calendarEndDate.toString());
            }

            Calendar startSubscriptionInCalendar = Calendar.getInstance();
            startSubscriptionInCalendar.setTime(startDateOfsubscription);

            Calendar endSubscriptionInCalendar = Calendar.getInstance();
            endSubscriptionInCalendar.setTime(endDateOfsubscription);
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Subscription end date: " + endDateOfsubscription.toString());
            }

            Calendar currentDate = Calendar.getInstance();
            currentDate.setFirstDayOfWeek(Calendar.MONDAY);
            int index = 0;
            todayDateIndex = 0;
            scrollTodayDateIndex = 0;
            scrollPreviousDateIndex = 0;
            selectedViewIndexForScroll = 0;
            String selectedDate = SessionUtil.getSelectedDate(context);

            while (!calendarStartDate.after(calendarEndDate)) {
                /*if(Common.isLoggingEnabled){
                    Log.d(TAG,"Start Date Calender "+startDateCalendar.getTime().toString()+" and current date "+currentDate.getTime().toString()+" and compare to "+startDateCalendar.getTime().compareTo(currentDate.getTime()));
                }*/
                if (!selectedDate.matches("")) {
                    SimpleDateFormat sdf;
                    String datePatternRegex = "([0-9]{4})-([0-9]{2})-([0-9]{2})";

                    if (selectedDate.matches(datePatternRegex)) {
                        sdf = new SimpleDateFormat(Common.DATE_FORMAT, Locale.ENGLISH);
                        if (Common.isLoggingEnabled) {
                            //Log.d(TAG, "selectedDate: " + selectedDate.toString());
                        }
                    } else {
                        sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    }
                    //sdf = new SimpleDateFormat(Common.DATE_FORMAT, Locale.ENGLISH);
                    try {
                        currentDate.setTime(Objects.requireNonNull(sdf.parse(selectedDate)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (isSameDay(calendarStartDate, currentDate)) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "date in calendar list " + calendarStartDate.getTime().toString() + " and current date " + currentDate.getTime().toString() + " and compare to " + calendarStartDate.getTime().compareTo(currentDate.getTime()));
                        Log.d(TAG, "Current date: " + currentDate.toString());
                    }
                    todayDateIndex = index;
                    scrollTodayDateIndex = calendarStartDate.get(Calendar.DAY_OF_WEEK) - 2;
                    scrollPreviousDateIndex = todayDateIndex;

                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "today date index is " + todayDateIndex);
                    }
                    if (calendarStartDate.before(startSubscriptionInCalendar) || calendarStartDate.after(endSubscriptionInCalendar)) {
                        calendarModelArrayList.add(new CalendarModel(calendarStartDate.get(Calendar.DATE),
                                calendarStartDate.get(Calendar.MONTH), calendarStartDate.get(Calendar.YEAR),
                                calendarStartDate.getTime(), true, true, false));
                    } else {
                        calendarModelArrayList.add(new CalendarModel(calendarStartDate.get(Calendar.DATE),
                                calendarStartDate.get(Calendar.MONTH), calendarStartDate.get(Calendar.YEAR),
                                calendarStartDate.getTime(), true, true, true));
                    }

                } else {
                    if (calendarStartDate.before(startSubscriptionInCalendar) || calendarStartDate.after(endSubscriptionInCalendar)) {
                        calendarModelArrayList.add(new CalendarModel(calendarStartDate.get(Calendar.DATE),
                                calendarStartDate.get(Calendar.MONTH), calendarStartDate.get(Calendar.YEAR),
                                calendarStartDate.getTime(), false, false, false));
                    } else {
                        calendarModelArrayList.add(new CalendarModel(calendarStartDate.get(Calendar.DATE),
                                calendarStartDate.get(Calendar.MONTH), calendarStartDate.get(Calendar.YEAR),
                                calendarStartDate.getTime(), false, false, true));
                    }
                }
                calendarStartDate.add(Calendar.DATE, 1);
                index++;
            }
        }
        return calendarModelArrayList;
    }

    boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null)
            return false;
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    void setCalendarAdapter() {
        try {
            monthTV = view.findViewById(R.id.monthTV);
            recyclerView = view.findViewById(R.id.horizontalCalendarRV);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            //linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // set Horizontal Orientation
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setNestedScrollingEnabled(true);
            SnapHelper snapHelper = new SnapToBlock(7);
            ArrayList<CalendarModel> calendarList = makeListForCalendar();
            HorizontalCalendarAdapter adapter = new HorizontalCalendarAdapter(context, calendarList, calendarInterface);
            recyclerView.setAdapter(adapter);
            snapHelper.attachToRecyclerView(recyclerView);

            if (calendarList.size() > 0) {
                Calendar myCal = Calendar.getInstance();

                myCal.setTime(calendarList.get(todayDateIndex).getDate());
                int calendarVisibleIndex = 0;
                if (myCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    calendarVisibleIndex = (todayDateIndex - (myCal.get(Calendar.DAY_OF_WEEK) - 2)) - 7;
                } else {
                    calendarVisibleIndex = todayDateIndex - (myCal.get(Calendar.DAY_OF_WEEK) - 2);
                }
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "todayDateIndex: " + todayDateIndex + ", DAY_OF_WEEK: " + myCal.get(Calendar.DAY_OF_WEEK) + " and calendar visible index = " + calendarVisibleIndex);
                }
                if (calendarVisibleIndex > -1 && calendarList.size() > calendarVisibleIndex)
                    recyclerView.scrollToPosition(calendarVisibleIndex);


                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "onScrollStateChanged::newState: " + newState);
                        }
                        if (newState == 0) {
                            int visiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
                            if (scrollPreviousDateIndex > -1)
                                calendarList.get(scrollPreviousDateIndex).setSelected(false);
                            if (calendarList.size() > (visiblePosition + scrollTodayDateIndex)) {
                                if (visiblePosition + scrollTodayDateIndex != scrollPreviousDateIndex) {
                                    calendarList.get(selectedViewIndexForScroll).setSelected(false);
                                    scrollPreviousDateIndex = visiblePosition + scrollTodayDateIndex;
                                    if (scrollPreviousDateIndex > -1) {
                                        calendarList.get(scrollPreviousDateIndex).setSelected(true);
                                        calendarInterface.onDateSelection(calendarList.get(scrollPreviousDateIndex).getDate());
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        myDX = dx;
                        int visiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
                        if (visiblePosition > -1 && visiblePosition < calendarList.size() - 1) {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "DX: " + dx + " Scroll Index: " + scrollIndex + ", visible index is " + visiblePosition + ", Scrolled index is " + scrollTodayDateIndex);
                            }
                            if (lang.matches("sv")) {
                                monthTV.setText(new SimpleDateFormat("MMMM", new Locale("sv")).format(calendarList.get(visiblePosition).getDate()).concat(", " + new SimpleDateFormat("yyyy", new Locale("sv")).format(calendarList.get(visiblePosition).getDate())));
                            } else {
                                monthTV.setText(new SimpleDateFormat("MMMM", new Locale("en")).format(calendarList.get(visiblePosition).getDate()).concat(", " + new SimpleDateFormat("yyyy", new Locale("en")).format(calendarList.get(visiblePosition).getDate())));
                            }
                        }

                    }
                });
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }
    }

}
