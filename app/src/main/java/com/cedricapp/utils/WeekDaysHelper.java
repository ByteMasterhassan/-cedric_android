package com.cedricapp.utils;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONDAY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.cedricapp.common.Common;
import com.cedricapp.model.DateModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WeekDaysHelper {
    final String DATE_FORMAT = "MMM dd YYYY";
    static final String SERVER_DATE_FORMAT = "yyyy-MM-dd";

    private Calendar calendar;

    public ArrayList<DateModel> getCurrentWeek() {
        this.calendar = Calendar.getInstance();
        /*this.calendar.setFirstDayOfWeek(Calendar.MONDAY);
        this.calendar.set(DAY_OF_WEEK, Calendar.MONDAY);*/
        return getNextWeek();
    }

    public ArrayList<DateModel> getNextWeek() {
       /* DateFormat format = new SimpleDateFormat("MMM dd", Locale.US);
        DateFormat formatDay = new SimpleDateFormat("dd", Locale.US);
        DateFormat dateNameFormat = new SimpleDateFormat("EEEE", Locale.US);
        DateFormat formatMonth = new SimpleDateFormat("MM", Locale.US);
        DateFormat formatLikeServer = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);*/
        DateFormat format;
        DateFormat formatDay;
        DateFormat dateNameFormat;
        DateFormat formatMonth;
        DateFormat formatLikeServer;

        Locale currentLocale = Locale.getDefault();

        if (currentLocale.getLanguage().equals("sv")) {
            format = new SimpleDateFormat("MMM dd", new Locale("sv", "SE")); // Swedish language in Sweden
            formatDay = new SimpleDateFormat("dd", new Locale("sv", "SE"));
            dateNameFormat = new SimpleDateFormat("EEEE", new Locale("sv", "SE"));
            formatMonth = new SimpleDateFormat("MM", new Locale("sv", "SE"));
            formatLikeServer = new SimpleDateFormat(SERVER_DATE_FORMAT, new Locale("sv", "SE"));
        } else {
            format = new SimpleDateFormat("MMM dd", Locale.US); // Default to English
            formatDay = new SimpleDateFormat("dd", Locale.US);
            dateNameFormat = new SimpleDateFormat("EEEE", Locale.US);
            formatMonth = new SimpleDateFormat("MM", Locale.US);
            formatLikeServer = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
        }
        ArrayList<DateModel> days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            days.add(new DateModel(format.format(this.calendar.getTime()),
                    Integer.parseInt(formatDay.format(this.calendar.getTime()))
                    , Integer.parseInt(formatMonth.format(this.calendar.getTime())),
                    dateNameFormat.format(this.calendar.getTime()),
                    formatLikeServer.format(this.calendar.getTime())));
            this.calendar.add(Calendar.DATE, 1);
        }
        return days;
    }

    public ArrayList<DateModel> getPreviousWeek() {
        this.calendar.add(Calendar.DATE, -14);
        return getNextWeek();
    }

    public ArrayList<DateModel> getFurtherWeek(int day, int month) {
        // this.calendar = Calendar.getInstance();
        this.calendar.set(DAY_OF_MONTH, day);
        this.calendar.set(Calendar.MONTH, month);
        this.calendar.setFirstDayOfWeek(Calendar.MONDAY);
        this.calendar.set(DAY_OF_WEEK, Calendar.MONDAY);
        return getNextWeek();
    }

    public ArrayList<String> getWeeksOfMonth(int dday, int month, int year) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.US);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(DAY_OF_MONTH, dday);
        int ndays = cal.getActualMaximum(DAY_OF_MONTH);
        System.out.println(ndays + "<<<ff");
        ArrayList<String> dayy = new ArrayList<>();

        while (cal.get(DAY_OF_WEEK) != MONDAY) {
            cal.add(DAY_OF_MONTH, 1);
            ndays--;
        }
        int remainingDays = ndays % 7;
        if (remainingDays == 0)
            ndays += 7;
        else
            ndays = ndays + 7 - remainingDays;

        int inc = 1;
        for (int i = 1; i <= ndays; i += 7) {

            String day = sdf.format(cal.getTime());
            dayy.add(sdf.format(cal.getTime()));
            System.out.println(day + "<<<");
            inc++;

            cal.add(Calendar.DATE, 1);
            if (inc >= 1) {
                for (int j = 2; j <= 7; j++) {
                    String day1 = sdf.format(cal.getTime());

                    System.out.println(day1 + "<<<");
                    dayy.add(sdf.format(cal.getTime()));
                    System.out.println(dayy + "<<<");
                    cal.add(Calendar.DATE, 1);
                }
                System.out.println("<<<END");

                inc = 0;
            }

        }
        return dayy;
    }

    public String get7thDayDate(String date) {
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US).parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DATE, 7);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return "";
    }

    public String get6thDayDate(String date) {
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US).parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DATE, 6);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return "";
    }

    public static String getNextYear(String year) {
        try {
            Date startDate = new SimpleDateFormat("yyyy", Locale.US).parse(year);

            Calendar c = Calendar.getInstance();
            assert startDate != null;
            c.setTime(startDate);
            c.add(Calendar.YEAR, 1);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat("yyyy", Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return "";
    }

    public static String getPreviousYear(String year) {
        try {
            Date startDate = new SimpleDateFormat("yyyy", Locale.US).parse(year);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.YEAR, -1);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat("yyyy", Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return "";
    }

    public static String getPreviousDayDate(String date) {
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US).parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DATE, -1);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return "";
    }

    public static String getNextDayDate(String date) {
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US).parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DATE, 1);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return "";
    }

    public static boolean isDateAfterToday(String date) {
        Date enteredDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
            enteredDate = sdf.parse(date);
            Date currentDate = new Date();
            if (enteredDate.after(currentDate)) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean isYearAfterCurrentYear(String year) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.US);
            Date enteredDate = sdf.parse(year);
            Log.e(Common.LOG,"Entered Year : "+enteredDate.toString());
            Date currentDate = new Date();
            if (enteredDate.after(currentDate)) {
                return true;
            } else
                return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String getPreviousYearDate(String date) {
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US).parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DATE, -365);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return "";
    }

    public String getPrevious7thDayDate(String date) {
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US).parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DATE, -7);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String getPrevious6thDayDate(String date) {
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US).parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DATE, -6);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String get15thDayDate(String date) {
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US).parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DATE, 15);
        Date expDate = c.getTime();
        DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

        // return  expDate.toString();
        return format.format(expDate);
    }

    public String get31stDayDate(String date) {
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US).parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DATE, 31);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static int getCountOfDays(String date1, String date2) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "Get Count of Days dates are: " + date1 + " and " + date2);
        }
        long noOfDays = 0;
        try {
            Date date_1 = myFormat.parse(date1);
            Date date_2 = myFormat.parse(date2);
            long diff = (date_2.getTime() - date_1.getTime());
            noOfDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) noOfDays + 1;
    }

    public static int getCountOfDaysBtwTwoDates(String date1, String date2) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        long noOfDays = 0;
        try {
            Date date_1 = myFormat.parse(date1);
            Date date_2 = myFormat.parse(date2);
            long diff = (date_2.getTime() - date_1.getTime());
            noOfDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) noOfDays + 1;
    }

    public String getPreviousOneDayDate(String date) {
        Date startDate = null;

        try {
            DateFormat setFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
            startDate = setFormat.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DATE, -1);
        Date expDate = c.getTime();
        DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

        // return  expDate.toString();
        return format.format(expDate);
    }

    public String getOneNextDayDate(String date) {
        Date startDate = null;

        try {
            DateFormat setFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
            startDate = setFormat.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DATE, 1);
            Date expDate = c.getTime();
            DateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);

            // return  expDate.toString();
            return format.format(expDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public int getMyDay(Context context, String currentDate) {
        String startTrail = SessionUtil.getTrailStarts(context);
        if (!startTrail.matches("")) {
            //return ((getCountOfDays(startTrail, currentDate)%7)+1);
            int totalDays = getCountOfDays(startTrail, currentDate);
            int myDay = 0;
            if (totalDays > 0) {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "total days in week_days_helper class is " + totalDays);
                }

                if ((totalDays % 7) == 0) {
                    myDay = 7;
                } else {
                    myDay = totalDays % 7;
                }
            } else {
                myDay = 1;
            }
            return myDay;
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "WeekDayHelper in getMyDay(): Start trail date is not saved in Session Utils");
        }
        return 0;
    }

    public int totalDaysFromTrial(Context context, String currentDate) {
        String startTrail = SessionUtil.getTrailStarts(context);
        if (!startTrail.matches("")) {
            //return ((getCountOfDays(startTrail, currentDate)%7)+1);

            return getCountOfDays(startTrail, currentDate);
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "WeekDayHelper in getMyDay(): Start trail date is not saved in Session Utils");
        }
        return 0;
    }

    public int getMyWeek(Context context, String currentDate) {
        String startTrail = SessionUtil.getTrailStarts(context);
        if (!startTrail.matches("")) {
            int totalDays = getCountOfDays(startTrail, currentDate);
            if (totalDays > 0) {
                if (totalDays % 7 == 0) {
                    return (((7 + totalDays) / 7) - 1);
                } else {
                    return (7 + totalDays) / 7;
                }
            } else {
                return 1;
            }

        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "WeekDayHelper in getMyWeek(): Start trail date is not saved in Session Utils");
        }
        return 0;
    }

    public String getCurrentDateLikeServer() {
        Date cd = android.icu.util.Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
        return df.format(cd);
    }

    public Calendar getTrialStartDateInCalendarFormat(String trialStartDate) {
        Calendar calendar = getCalendarDate(getPrevious7thDayDate(trialStartDate));
        if (calendar != null)
            return calendar;
        else
            return null;
    }

    public Calendar getCalendarDate(String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
            Date date = sdf.parse(strDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (Exception ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }
        return null;
    }

    public static String getUTC_Time() {
        TimeZone tz = TimeZone.getDefault();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    public static String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getDisplayName(false, TimeZone.SHORT);
    }

    public static String getTimeZoneID() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getID();
    }

    public static String getDateTimeNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTimeNow_yyyyMMdd() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getToDateYear_yyyy() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static boolean isSubscriptionAvailable(String toDate, String endDate) {
        try {
            String datePatternRegex = "([0-9]{2})-([0-9]{2})-([0-9]{4})";
            String endDatePattern = "yyyy-MM-dd HH:mm:ss";
            if(endDatePattern.matches(datePatternRegex)){
                endDatePattern = "yyyy-MM-dd";
            }
            if(Common.isLoggingEnabled){
                Log.d("SUBSCRIPTION_TAG","Date regex is "+endDatePattern);
            }
            String outputPattern = "yyyy-MM-dd";
            SimpleDateFormat currentDateFormat = new SimpleDateFormat(outputPattern, Locale.US);
            SimpleDateFormat endDateFormat = new SimpleDateFormat(endDatePattern, Locale.US);
            Date date1 = new Date();
            Date date2 = new Date();

            date1 = currentDateFormat.parse(toDate);
            date2 = endDateFormat.parse(endDate);

            if(Common.isLoggingEnabled){
                Log.d("SUBSCRIPTION_TAG","Today Date is "+date1.toString()+" and subscription end date is "+date2.toString());
            }

            if (date1.after(date2)) {
                if(Common.isLoggingEnabled){
                    Log.e("SUBSCRIPTION_TAG","Subscription not available");
                }
                return false;

            } else {
                if(Common.isLoggingEnabled){
                    Log.d("SUBSCRIPTION_TAG","Subscription is available");
                }
                return true;
            }

        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
            return false;
        }

    }

    public static String parseDateToYYYYmmDD(String time) {
        String datePatternRegex = "([0-9]{4})-([0-9]{2})-([0-9]{2})";
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        if(inputPattern.matches(datePatternRegex)){
            inputPattern = "yyyy-MM-dd";
        }
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.US);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getDayNameByDate(String strDate) {
        String dayName = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {

            Date date = df.parse(strDate.trim());
            SimpleDateFormat outFormat = new SimpleDateFormat("EE", Locale.getDefault());
            dayName = outFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayName;
    }

    public static String getMonthByDate(String strDate) {
        String dayName = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {

            Date date = df.parse(strDate.trim());
            SimpleDateFormat outFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            dayName = outFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayName;
    }

    public static List<String> getDates(String dateString1, String dateString2) {
        ArrayList<String> dates = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dates.add(dateFormat.format(cal1.getTime()));
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

}
