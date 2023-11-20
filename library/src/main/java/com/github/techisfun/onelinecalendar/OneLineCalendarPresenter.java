package com.github.techisfun.onelinecalendar;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Andrea Maglie
 */
public class OneLineCalendarPresenter implements OneLineCalendarContract.Presenter  {
    public static  int MAX_DAYS = 0;
    private static final int MAX_SIZE = MAX_DAYS + 12;
    public static  Date subscriptionEndDate ;
    private List<SimpleDate> mSimpleDateList = new ArrayList<>(MAX_SIZE);
    private OneLineCalendarContract.View mView;

    public static String appLang = "sv";

    public OneLineCalendarPresenter(Calendar today) {
        buildSimpleDateList(today);
    }

    private void buildSimpleDateList(Calendar today) {
        /*Calendar calendar;
        if(appLang.matches("sv")) {
            calendar = Calendar.getInstance(*//*new Locale("sv_SE")*//*Locale.CHINA);
        }else{
            calendar = Calendar.getInstance(Locale.ENGLISH);
        }*/
        Calendar calendar = Calendar.getInstance(Locale.FRENCH);
        Date todayTime = today.getTime();
        calendar.setTime(todayTime);
//        Fri Sep 16 15:32:03 GMT+05:00 2022

        for (int i = 0; i < MAX_DAYS; i++) {
            if (calendar.get(Calendar.DATE) == 1) {
                mSimpleDateList.add(SimpleDate.monthFrom(calendar));
            } else if (i == 0) {
                mSimpleDateList.add(SimpleDate.monthFrom(calendar));
            }
            mSimpleDateList.add(SimpleDate.dateFrom(calendar, today));

            calendar.add(Calendar.DATE, 1);
        }
    }

    @Override
    public void dropView() {
        mView = null;
    }

    @Override
    public void takeView(OneLineCalendarContract.View view) {
        mView = view;
        populateView();
    }

    private void populateView() {
        mView.populateWithItems(mSimpleDateList);
    }

    List<SimpleDate> getSimpleDateList() {
        return mSimpleDateList;
    }

    @Override
    public RecyclerView.OnScrollListener buildOnScrollListener() {
        return new RecyclerView.OnScrollListener() {

            private LinearLayoutManager mLayoutManager = null;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mLayoutManager == null) {
                    mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                }

                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition == 0) {
                    return;
                }

                String text = null;
                boolean isRightScrolling = (dx >= 0);
                SimpleDate simpleDate = mSimpleDateList.get(firstVisibleItemPosition);

                if (simpleDate.getType() == SimpleDate.MONTH_TYPE) {
                    if (isRightScrolling) {
                        text = simpleDate.toString();
                    } else {
                        text = simpleDate.getPreviousMonthFormatted();
                    }
                }

                if (mView != null && text != null) {
                    mView.setStickyHeaderText(text);
                }
            }
        };
    }
}
