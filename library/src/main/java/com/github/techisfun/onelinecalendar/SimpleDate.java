package com.github.techisfun.onelinecalendar;

import static com.github.techisfun.onelinecalendar.OneLineCalendarPresenter.subscriptionEndDate;

import android.content.Context;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Andrea Maglie
 */
class SimpleDate {
    static final int MONTH_TYPE = 1;
    static final int DATE_TYPE = 2;
    private static SimpleDateFormat sMonthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
    private int mDay;
    private int mMonth;
    private int mYear;
    private boolean mIsToday;
    private int mType;
    private Date mDate;

    static SimpleDate monthFrom(@NonNull Calendar calendar) {
        SimpleDate simpleDate = new SimpleDate();
        simpleDate.mDay = 0;
        simpleDate.mMonth = calendar.get(Calendar.MONTH);
        simpleDate.mYear = calendar.get(Calendar.YEAR);
        simpleDate.mType = MONTH_TYPE;
        simpleDate.mDate = calendar.getTime();
        return simpleDate;
    }

    static SimpleDate dateFrom(@NonNull Calendar dayToDisplay, Calendar today) {
        SimpleDate simpleDate = new SimpleDate();
        simpleDate.mDay = dayToDisplay.get(Calendar.DATE);
        simpleDate.mMonth = dayToDisplay.get(Calendar.MONTH);
        simpleDate.mYear = dayToDisplay.get(Calendar.YEAR);
        simpleDate.mType = DATE_TYPE;
        simpleDate.mDate = dayToDisplay.getTime();
        simpleDate.mIsToday = equals(dayToDisplay, today);
        return simpleDate;
    }

    private static boolean equals(@NonNull Calendar c1, @NonNull Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DATE) == c2.get(Calendar.DATE);
    }

    int getType() {
        return mType;
    }

    @Override
    public String toString() {
        if (mType == DATE_TYPE) {
            return String.valueOf(mDay);
        } else if (mType == MONTH_TYPE) {
            return sMonthFormat.format(mDate);
        } else {
            return super.toString();
        }
    }

    CharSequence getDayNameFormatted(Context context) {
        if (mIsToday) {
            return context.getString(R.string.today);
        }
        if (OneLineCalendarPresenter.appLang.matches("sv"))
            return new SimpleDateFormat("EEE", new Locale("sv")).format(mDate);
        else
            return new SimpleDateFormat("EEE",Locale.ENGLISH).format(mDate);
        /*return DateUtils.formatDateTime(context,
                mDate.getTime(),
                DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_WEEKDAY);*/
    }

    String getDayNumberFormatted() {
        return String.valueOf(mDay);
    }

    Date getDate() {
        return mDate;
    }

    int getDay() {
        return mDay;
    }

    int getMonth() {
        return mMonth;
    }

    int getYear() {
        return mYear;
    }

    String getPreviousMonthFormatted() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        calendar.add(Calendar.MONTH, -1);
        return sMonthFormat.format(calendar.getTime());
    }

    public boolean isPastEndDate(SimpleDate simpleDate) {
        long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
        Date currentDate = new Date();
        System.out.println(subscriptionEndDate + "subscritionEndDate");
        System.out.println(currentDate + "subscritionEndDate");
        if (subscriptionEndDate != null) {
            Date nextDayDate = new Date(subscriptionEndDate.getTime() + MILLIS_IN_A_DAY);
            return simpleDate.getDate().after(nextDayDate);
        }
        return false;

    }
}
