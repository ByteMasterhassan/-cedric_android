package com.cedricapp.fragment;

import static com.cedricapp.common.Common.ANALYTICS_FOR;
import static com.cedricapp.common.Common.ANALYTICS_TYPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.StepsAnalytics;
import com.cedricapp.interfaces.StepsAnalyticsInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.AnalyticsModel;
import com.cedricapp.R;
import com.cedricapp.model.StepCountModel;
import com.cedricapp.utils.CalculatorUtil;
import com.cedricapp.utils.CustomBarChartRender;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepAnalyticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepAnalyticsFragment extends Fragment implements StepsAnalyticsInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // variable for our bar chart
    XAxis xAxis;

    View view;

    String TAG = "StepAnalyticsFragment_TAG";

    BarChart barChart;

    List<String> dateList;
    List<String> daysNameList;
    List<String> monthsNameList;
    List<String> caloriesList;

    AnalyticsModel analytics;

    TextView totalStepsTV, totalCaloriesTV, totalDistanceTV, stepTV, caloriesTV, dateTV, totalSummaryHeadingTV, barSummaryTV, distanceTV, txt_step, txt_calories, txt_distance, txt_step1, txt_calories1, txt_distance1;

    ImageButton leftarrow, rightarrow;

    String analyticsType, analyticsFor;

    LottieAnimationView loading_lav;

    BlurView blurView;
    Resources resources;

    List<AnalyticsModel.Data> weeklyAnalyticsModel;

    String date, startDate, endData, year;

    Context context;

    DBHelper dbHelper;
    String userHeight;


    public StepAnalyticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepAnalyticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepAnalyticsFragment newInstance(String param1, String param2) {
        StepAnalyticsFragment fragment = new StepAnalyticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*analyticsFor = "steps";
        analyticsType = "daily";*/
        if (getArguments() != null) {

            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            analyticsFor = getArguments().getString(ANALYTICS_FOR);
            analyticsType = getArguments().getString(ANALYTICS_TYPE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.daily_step_analytics, container, false);
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        //initialize all widgets
        init();

        return view;
    }

    @SuppressLint("SetTextI18n")
    void init() {
        daysNameList = new ArrayList<>();
        caloriesList = new ArrayList<>();
        totalSummaryHeadingTV = view.findViewById(R.id.totalSummaryHeadingTV);
        barSummaryTV = view.findViewById(R.id.barSummaryTV);
        totalStepsTV = view.findViewById(R.id.totalStepsTV);
        totalCaloriesTV = view.findViewById(R.id.totalCaloriesTV);
        totalDistanceTV = view.findViewById(R.id.totalDistanceTV);
        caloriesTV = view.findViewById(R.id.caloriesTV);
        stepTV = view.findViewById(R.id.stepTV);
        distanceTV = view.findViewById(R.id.distanceTV);
        leftarrow = view.findViewById(R.id.leftarrow);
        rightarrow = view.findViewById(R.id.rightarrow);
        dateTV = view.findViewById(R.id.dateTV);
        txt_step = view.findViewById(R.id.steps);
        txt_calories = view.findViewById(R.id.calories);
        txt_distance = view.findViewById(R.id.distance);
        txt_step1 = view.findViewById(R.id.ste);
        txt_calories1 = view.findViewById(R.id.calo);
        txt_distance1 = view.findViewById(R.id.dist);

        loading_lav = view.findViewById(R.id.loading_lav);
        blurView = view.findViewById(R.id.blurView);
        dbHelper = new DBHelper(context);

        userHeight = SessionUtil.getUserHeight(context);


        setlanguageToWidget();
        setDataAccordingToActivityType();

        leftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (analyticsType.matches("daily")) {
                    if (date != null && !date.matches("")) {
                        date = WeekDaysHelper.getPreviousDayDate(date);
                        getDailyData(date);
                    }
                } else if (analyticsType.matches("weekly")) {
                    if (startDate != null && endData != null
                            && !startDate.matches("") && !endData.matches("")) {
                        WeekDaysHelper weekDaysHelper = new WeekDaysHelper();
                        endData = WeekDaysHelper.getPreviousDayDate(startDate);
                        startDate = weekDaysHelper.getPrevious6thDayDate(endData);
                        getWeeklyData(startDate, endData);
                    }

                } else if (analyticsType.matches("yearly")) {
                    if (year != null && !year.matches("")) {
                        year = WeekDaysHelper.getPreviousYear(year);
                        getYearlyData(year);
                    }
                }

            }
        });

        rightarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (analyticsType.matches("daily")) {
                    if (date != null && !date.matches("")) {
                        String nextDate = WeekDaysHelper.getNextDayDate(date);
                        if (!WeekDaysHelper.isDateAfterToday(nextDate)) {
                            date = nextDate;
                            getDailyData(date);
                        }
                    }

                } else if (analyticsType.matches("weekly")) {
                    if (startDate != null && endData != null
                            && !startDate.matches("") && !endData.matches("")) {
                        WeekDaysHelper weekDaysHelper = new WeekDaysHelper();
                        String dt = WeekDaysHelper.getNextDayDate(endData);
                        if (!WeekDaysHelper.isDateAfterToday(dt)) {
                            startDate = dt;
                            endData = weekDaysHelper.get6thDayDate(startDate);
                            getWeeklyData(startDate, endData);
                        }

                    }

                } else if (analyticsType.matches("yearly")) {
                    if (year != null && !year.matches("")) {
                        String yr = WeekDaysHelper.getNextYear(year);
                        if (!WeekDaysHelper.isYearAfterCurrentYear(yr)) {
                            year = yr;
                            getYearlyData(year);
                        }
                    }
                }
            }
        });
        //drawChart();
    }

    private void setlanguageToWidget() {
        txt_step.setText(resources.getString(R.string.steps));
        txt_calories.setText(resources.getString(R.string.calories));
        txt_distance.setText(resources.getString(R.string.distance));
        txt_step1.setText(resources.getString(R.string.steps));
        txt_calories1.setText(resources.getString(R.string.calories));
        txt_distance1.setText(resources.getString(R.string.distance));
    }


    void setDataAccordingToActivityType() {
        try {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                if ((analyticsFor.matches("steps")) && analyticsType.matches("daily")) {
                    totalSummaryHeadingTV.setText((resources.getString(R.string.todays_summary)));
                    barSummaryTV.setText(resources.getString(R.string._21st_hourly_summary));
                    date = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
                    getDailyData(date);
                } else if (analyticsFor.matches("steps") && analyticsType.matches("weekly")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.weekly_summary));
                    barSummaryTV.setText(resources.getString(R.string.mon_activity_summary));
                    startDate = new WeekDaysHelper().getPrevious6thDayDate(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                    endData = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
                    getWeeklyData(startDate, endData);
                } else if (analyticsFor.matches("steps") && analyticsType.matches("yearly")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.yearly_summary));
                    barSummaryTV.setText(resources.getString(R.string.mon_activity_summary));
                    year = WeekDaysHelper.getToDateYear_yyyy();
                    getYearlyData(year);
                } else if ((analyticsFor.matches("calories")) && analyticsType.matches("daily")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.todays_summary));
                    barSummaryTV.setText(resources.getString(R.string._21st_hourly_summary));
                    date = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
                    getDailyData(date);
                } else if (analyticsFor.matches("calories") && analyticsType.matches("weekly")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.weekly_summary));
                    barSummaryTV.setText(resources.getString(R.string.mon_activity_summary));
                    startDate = new WeekDaysHelper().getPrevious6thDayDate(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                    endData = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
                    getWeeklyData(startDate, endData);
                } else if (analyticsFor.matches("calories") && analyticsType.matches("yearly")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.yearly_summary));
                    barSummaryTV.setText(resources.getString(R.string.mon_activity_summary));
                    year = WeekDaysHelper.getToDateYear_yyyy();
                    getYearlyData(year);
                } else if ((analyticsFor.matches("distance")) && analyticsType.matches("daily")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.todays_summary));
                    barSummaryTV.setText(resources.getString(R.string._21st_hourly_summary));
                    date = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
                    getDailyData(date);
                } else if (analyticsFor.matches("distance") && analyticsType.matches("weekly")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.weekly_summary));
                    barSummaryTV.setText(resources.getString(R.string.mon_activity_summary));
                    startDate = new WeekDaysHelper().getPrevious6thDayDate(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                    endData = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
                    getWeeklyData(startDate, endData);
                } else if (analyticsFor.matches("distance") && analyticsType.matches("yearly")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.yearly_summary));
                    barSummaryTV.setText(resources.getString(R.string.mon_activity_summary));
                    year = WeekDaysHelper.getToDateYear_yyyy();
                    getYearlyData(year);
                } else if ((analyticsFor.matches("water")) && analyticsType.matches("daily")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.todays_summary));
                    barSummaryTV.setText(resources.getString(R.string._21st_hourly_summary));
                    date = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
                    getDailyData(date);
                } else if (analyticsFor.matches("water") && analyticsType.matches("weekly")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.weekly_summary));
                    barSummaryTV.setText(resources.getString(R.string.mon_activity_summary));
                    startDate = new WeekDaysHelper().getPrevious6thDayDate(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                    endData = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
                    getWeeklyData(startDate, endData);
                } else if (analyticsFor.matches("water") && analyticsType.matches("yearly")) {
                    totalSummaryHeadingTV.setText(resources.getString(R.string.yearly_summary));
                    barSummaryTV.setText(resources.getString(R.string.mon_activity_summary));
                    year = WeekDaysHelper.getToDateYear_yyyy();
                    getYearlyData(year);
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "No Internet connection");
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }
    }

    void getDailyData(String dt) {
        if (getContext() != null) {
            if (!dt.equals(WeekDaysHelper.getDateTimeNow_yyyyMMdd())) {
                dateTV.setText(dt);
                startLoading();
                StepsAnalytics.getAnalytics(getContext(), dt, dt, "daily", "", StepAnalyticsFragment.this);
            } else {
                if(Common.isLoggingEnabled){
                    Log.d(TAG,"Today");
                }
                dateTV.setText(dt);
                getDailyDataFromDB(dt);
            }
        }
    }

    void getWeeklyData(String startDt, String endDt) {
        if (getContext() != null) {
            makeDayListForWeekly(startDt, endDt);
            dateTV.setText(startDt.concat("  -  " + endDt));
            startLoading();
            StepsAnalytics.getAnalytics(getContext(), startDt, endDt, "weekly", "", StepAnalyticsFragment.this);
        }
    }

    void getYearlyData(String yr) {
        if (getContext() != null) {
            dateTV.setText(yr);
            startLoading();
            StepsAnalytics.getAnalytics(getContext(), "", "", "yearly", yr, StepAnalyticsFragment.this);
        }
    }

    void getDailyDataFromDB(String date) {
        try {
            List<StepCountModel.Data> dailySteps = dbHelper.getUserActivityByUserID_ActivityDate(SessionUtil.getUserID(context), date);
            if(Common.isLoggingEnabled){
                Log.d(TAG,"daily Steps from db: "+dailySteps.toString());
            }
            if (dailySteps.size() > 0) {
                AnalyticsModel analytic = new AnalyticsModel();
                analytic.setTotalSteps(Integer.valueOf(dailySteps.get(0).getStepsCount()));
                analytic.setTotalCalories(Double.valueOf(dailySteps.get(0).getCalories()));
                analytic.setTotalDistance(Double.valueOf(dailySteps.get(0).getDistance()));
                ArrayList<AnalyticsModel.Data> analyticsData = new ArrayList<>();
                AnalyticsModel.Data datum = new AnalyticsModel.Data();
                datum.setActivityLat(dailySteps.get(0).getActivityLat());
                datum.setActivityLong(dailySteps.get(0).getActivityLong());
                datum.setActivityLocation(dailySteps.get(0).getActivityLocation());
                datum.setCalories(Double.valueOf(dailySteps.get(0).getCalories()));
                datum.setDistance(Double.valueOf(dailySteps.get(0).getDistance()));
                datum.setStepsCount(Integer.valueOf(dailySteps.get(0).getStepsCount()));
                datum.setUserActivityDate(dailySteps.get(0).getUserActivityDate());
                datum.setWaterCount(Integer.parseInt(dailySteps.get(0).getWaterCount()));
                datum.setUserTimeZone(dailySteps.get(0).getUserTimeZone());
                datum.setStepCountID(dailySteps.get(0).getStepCountID());
                datum.setUserID(Integer.parseInt(SessionUtil.getUserID(context)));
                analyticsData.add(datum);
                analytic.setData(analyticsData);
                //analytics = analytic;
                dailyAnalytics(analytic);
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    void makeDayListForWeekly(String startDate, String endDate) {
        dateList = WeekDaysHelper.getDates(startDate, endDate);
        weeklyAnalyticsModel = new ArrayList<>();
        for (String date : dateList) {
            daysNameList.add(WeekDaysHelper.getDayNameByDate(date));
            AnalyticsModel.Data analyticsModel = new AnalyticsModel.Data();
            analyticsModel.setUserActivityDate(date);
            weeklyAnalyticsModel.add(analyticsModel);
        }
    }

    @SuppressLint("DefaultLocale")
    private void drawChart() {
        try {
            BarChart barChart = view.findViewById(R.id.barChart);
            barChart.setScaleEnabled(true);
            barChart.setDragEnabled(true);
            barChart.setPinchZoom(true);
            // barChart.setScaleXEnabled(true);

            if (analyticsType.matches("weekly")) {
                for (int i = 0; i < weeklyAnalyticsModel.size(); i++) {
                    for (int j = 0; j < analytics.getData().size(); j++) {
                        if (weeklyAnalyticsModel.get(i).getUserActivityDate().equals(analytics.getData().get(j).getUserActivityDate())) {
                            AnalyticsModel.Data data = analytics.getData().get(j);
                            weeklyAnalyticsModel.set(i, data);
                        }
                    }

                }
            }


// Create a list of BarEntry objects
            List<BarEntry> barEntries = new ArrayList<>();

            if (analyticsFor.matches("steps")) {
                if (analyticsType.matches("weekly")) {
                    for (int i = 0; i < weeklyAnalyticsModel.size(); i++) {
                        if (weeklyAnalyticsModel.get(i).getStepsCount() != null /*&& !weeklyAnalyticsModel.get(i).getStepsCount().matches("")*/) {
                            barEntries.add(new BarEntry(i, /*Float.parseFloat(*/(float) weeklyAnalyticsModel.get(i).getStepsCount())/*)*/);
                        } else {
                            barEntries.add(new BarEntry(i, 0f));
                        }
                    }
                } else {
                    if (analytics.getData().size() > 0) {
                        for (int i = 0; i < analytics.getData().size(); i++) {
                            if (analytics.getData().get(i).getStepsCount() != null /*&& !analytics.getData().get(i).getStepsCount().matches("")*/) {
                                barEntries.add(new BarEntry(i, /*Float.parseFloat(*/(float) analytics.getData().get(i).getStepsCount())/*)*/);
                            } else {
                                barEntries.add(new BarEntry(i, 0f));
                            }
                        }
                    } else {
                        barEntries.add(new BarEntry(0, 0f));
                    }
                }

            } else if (analyticsFor.matches("calories")) {
                if (analyticsType.matches("weekly")) {
                    for (int i = 0; i < weeklyAnalyticsModel.size(); i++) {
                        if (weeklyAnalyticsModel.get(i).getCalories() != null /*&& !weeklyAnalyticsModel.get(i).getCalories().matches("")*/) {
                            barEntries.add(new BarEntry(i, weeklyAnalyticsModel.get(i).getCalories().floatValue()));
                        } else {
                            barEntries.add(new BarEntry(i, 0f));
                        }
                    }
                } else {
                    if (analytics.getData().size() > 0) {
                        for (int i = 0; i < analytics.getData().size(); i++) {
                            if (analytics.getData().get(i).getCalories() != null/* && !analytics.getData().get(i).getCalories().matches("")*/) {
                                barEntries.add(new BarEntry(i, analytics.getData().get(i).getCalories().floatValue()));
                            } else {
                                barEntries.add(new BarEntry(i, 0f));
                            }
                        }
                    } else {
                        barEntries.add(new BarEntry(0, 0f));
                    }
                }

            } else if (analyticsFor.matches("distance")) {
                if (analyticsType.matches("weekly")) {
                    for (int i = 0; i < weeklyAnalyticsModel.size(); i++) {
                        if (weeklyAnalyticsModel.get(i).getStepsCount() != null /*&& !weeklyAnalyticsModel.get(i).getDistance().matches("")*/) {
                            float distanceValue =  Math.abs(CalculatorUtil.getDistanceNow(context, weeklyAnalyticsModel.get(i).getStepsCount().longValue(), Double.parseDouble(userHeight)))/1000;
                            barEntries.add(new BarEntry(i,  Float.parseFloat(String.format("%.2f",distanceValue))));
                        } else {
                            barEntries.add(new BarEntry(i, 0f));
                        }
                    }
                } else {
                    if (analytics.getData().size() > 0) {
                        for (int i = 0; i < analytics.getData().size(); i++) {
                            if (analytics.getData().get(i).getStepsCount() != null) {
                                float distanceValue =  Math.abs(CalculatorUtil.getDistanceNow(context, analytics.getData().get(i).getStepsCount(), Double.parseDouble(userHeight)))/1000;
                                barEntries.add(new BarEntry(i,  Float.parseFloat(String.format("%.2f",distanceValue))));
                            } else {
                                barEntries.add(new BarEntry(i, 0f));
                            }
                        }
                    } else {
                        barEntries.add(new BarEntry(0, 0f));
                    }
                }

            } else if (analyticsFor.matches("water")) {
                if (analyticsType.matches("weekly")) {
                    for (int i = 0; i < weeklyAnalyticsModel.size(); i++) {
                        if (weeklyAnalyticsModel.get(i).getWaterCount() != null /*&& !weeklyAnalyticsModel.get(i).getWaterCount().matches("")*/) {
                            barEntries.add(new BarEntry(i, weeklyAnalyticsModel.get(i).getWaterCount().floatValue()));
                        } else {
                            barEntries.add(new BarEntry(i, 0f));
                        }
                    }
                } else {
                    if (analytics.getData().size() > 0) {
                        for (int i = 0; i < analytics.getData().size(); i++) {
                            if (analytics.getData().get(i).getWaterCount() != null /*&& !analytics.getData().get(i).getWaterCount().matches("")*/) {
                                barEntries.add(new BarEntry(i, /*Float.parseFloat(*/analytics.getData().get(i).getWaterCount().floatValue()));
                            } else {
                                barEntries.add(new BarEntry(i, 0f));
                            }
                        }
                    } else {
                        barEntries.add(new BarEntry(0, 0f));
                    }
                }

            }

// Add more BarEntry objects for each hour

// Create a BarDataSet with the BarEntry objects

            int mainColor = 0, colorhightlighted = 0, linecolor = 0, yAxistextcolor = 0;
            if (isAdded() && getContext() != null) {
                if (analyticsFor.matches("water")) {
                    mainColor = ContextCompat.getColor(getContext(), R.color.water);
                    colorhightlighted = ContextCompat.getColor(getContext(), R.color.water_high);
                } else {
                    mainColor = ContextCompat.getColor(getContext(), R.color.yellow);
                    colorhightlighted = ContextCompat.getColor(getContext(), R.color.high);
                }
                linecolor = ContextCompat.getColor(getContext(), R.color.lineColor);
                yAxistextcolor = ContextCompat.getColor(getContext(), R.color.yaxis);
            }

            BarDataSet dataSet = new BarDataSet(barEntries, "Step Count Data");
            dataSet.setColor(colorhightlighted);
            // Create a custom BarHighlighter
            dataSet.setHighLightColor(mainColor);
            dataSet.setHighlightEnabled(true);
            // Create a BarData object from the BarDataSet
            BarData data = new BarData(dataSet);
            //data.setBarWidth(1f);

            // Customize the X-axis
            xAxis = barChart.getXAxis();
            xAxis.setSpaceMin(0f);
            xAxis.setEnabled(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setDrawLabels(true);
            xAxis.setAxisMinimum(0f);
            xAxis.setCenterAxisLabels(false);
            xAxis.setDrawGridLines(true);
            xAxis.setDrawLabels(true);
            if (analyticsType.matches("yearly")) {
                xAxis.setLabelCount(12);
            }
            // Set custom shape backgrounds for each label
            xAxis.setValueFormatter(new ValueFormatter() {
                                        @Override
                                        public String getFormattedValue(float value) {
                                            int index = (int) value;
                                            if (analyticsType.matches("weekly")) {
                                                //return WeekDaysHelper.getDayNameByDate(analytics.getData().get(index).getUserActivityDate());
                                                if (weeklyAnalyticsModel != null && weeklyAnalyticsModel.size() > 0) {
                                                    if (index != -1 && index < weeklyAnalyticsModel.size()) {
                                                        return WeekDaysHelper.getDayNameByDate(weeklyAnalyticsModel.get(index).getUserActivityDate());
                                                    } else {
                                                        return "";
                                                    }
                                                } else {
                                                    return "";
                                                }
                                            } else if (analyticsType.matches("yearly")) {
                                                return analytics.getData().get(index).getUserActivityDate();
                                            } else {
                                                return String.valueOf(index);
                                            }
                                        }
                                    }
                    //new IndexAxisValueFormatter(daysNameList)
            );

            //Righht ac customization
            YAxis rightAxis = barChart.getAxisLeft();
            rightAxis.setAxisMinimum(0f);
            rightAxis.setEnabled(false);
            // Customize the Y-axis
            YAxis yAxis = barChart.getAxisRight();
            CustomBarChartRender barChartRender = new CustomBarChartRender(barChart, barChart.getAnimator(), barChart.getViewPortHandler());
            //barChartRender.setRadius(activitiesModel.getData().size());
            yAxis.setEnabled(true);
            yAxis.setDrawAxisLine(true);
            yAxis.setAxisLineColor(linecolor);
            yAxis.setTextColor(yAxistextcolor);
            yAxis.setGridColor(yAxistextcolor);
            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            yAxis.setAxisMinimum(0f);
            yAxis.setGranularity(1f);
            //yAxis.setAxisMaximum(13000f);
            yAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    /*if (analyticsFor.matches("distance")) {
                        return String.valueOf(value/1000);
                    } else*/
                    return String.valueOf(value);
                }
            });
            float margin = 0.5f;
            float startValue = 0 - margin;
            float endValue = 11 + margin;

            // Set space value
            float space = 5f;

// Set axisMinimum
            //barChart.getAxisLeft().setAxisMinimum(0);
            //float initialVisibleRange = 0;
            //barChart.setVisibleXRangeMaximum(initialVisibleRange);
// Set axisMinimum and axisMaximum
            //barChart.getXAxis().setAxisMinimum(0);
            //barChart.getXAxis().setAxisMaximum(7);
            ;
            barChart.setData(data);
            barChart.getLegend().setEnabled(false);
            barChart.setDrawBarShadow(false);

            barChart.setDrawBorders(false);
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getDescription().setEnabled(false);
            barChart.setDrawGridBackground(false);
            dataSet.setDrawValues(true);
            dataSet.setHighlightEnabled(true);


/*
        LimitLine limitLine = new LimitLine(lineValue, "Limit");
        limitLine.setLineColor(ContextCompat.getColor(getContext(), R.color.lineColor));
        limitLine.setLineWidth(2f);
        yAxis.addLimitLine(limitLine);*/

            barChart.setSelected(true);

            int finalMainColor = mainColor;
            barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    int datasetIndex = h.getDataSetIndex();

                    // Change the text color of x-labels when a dataset is selected
                    //xAxis.setTextColor(datasetIndex == 0 ? finalMainColor : Color.BLACK);

                    // Refresh the chart to apply the text color changes
                    //barChart.invalidate();
                    if (analytics != null && analytics.getData() != null
                            && analytics.getData().size() > 0) {
                    /*if(analyticsFor.matches("steps")){

                    }else if(analyticsFor.matches("calories")){
                        caloriesTV.setText(String.valueOf((int) e.getY()));
                    }*/

                        if (analyticsType.matches("weekly")) {
                            if (weeklyAnalyticsModel.get((int) e.getX()).getStepsCount() != null /*&&
                                !weeklyAnalyticsModel.get((int) e.getX()).getStepsCount().matches("")*/)
                                stepTV.setText("" + weeklyAnalyticsModel.get((int) e.getX()).getStepsCount());
                            else
                                stepTV.setText("0");

                            if (weeklyAnalyticsModel.get((int) e.getX()).getCalories() != null /*&&
                                !weeklyAnalyticsModel.get((int) e.getX()).getCalories().matches("")*/)
                                caloriesTV.setText("" + weeklyAnalyticsModel.get((int) e.getX()).getCalories().intValue());
                            else
                                caloriesTV.setText("0");

                            if (weeklyAnalyticsModel.get((int) e.getX()).getDistance() != null /*&&
                                weeklyAnalyticsModel.get((int) e.getX()).getDistance().matches("")*/) {
                                float distanceValue =  Math.abs(CalculatorUtil.getDistanceNow(context, weeklyAnalyticsModel.get((int) e.getX()).getStepsCount(), Double.parseDouble(userHeight)))/1000;
                                distanceTV.setText(String.format("%.2f",distanceValue));
                            }
                            else
                                distanceTV.setText("0");

                            barSummaryTV.setText(WeekDaysHelper.getDayNameByDate(weeklyAnalyticsModel.get((int) e.getX()).getUserActivityDate()) + " " + getString(R.string.mon_activity_summary));
                        } else if (analyticsType.matches("yearly")) {
                            stepTV.setText("" + analytics.getData().get((int) e.getX()).getStepsCount());
                            caloriesTV.setText(String.valueOf(analytics.getData().get((int) e.getX()).getCalories()));
                            float distanceValue =  Math.abs(CalculatorUtil.getDistanceNow(context, analytics.getData().get((int) e.getX()).getStepsCount(), Double.parseDouble(userHeight)))/1000;
                            distanceTV.setText(String.format("%.2f",distanceValue));
                            //distanceTV.setText("" + analytics.getData().get((int) e.getX()).getDistance() / 1000);
                            barSummaryTV.setText(WeekDaysHelper.getMonthByDate(analytics.getData().get((int) e.getX()).getUserActivityDate()) + " " + getString(R.string.mon_activity_summary));
                        } else {
                            stepTV.setText("" + analytics.getData().get((int) e.getX()).getStepsCount());
                            caloriesTV.setText(String.valueOf(analytics.getData().get((int) e.getX()).getCalories()));
                            float distanceValue =  Math.abs(CalculatorUtil.getDistanceNow(context, analytics.getData().get((int) e.getX()).getStepsCount(), Double.parseDouble(userHeight)))/1000;
                            distanceTV.setText(String.format("%.2f",distanceValue));
                           // distanceTV.setText("" + analytics.getData().get((int) e.getX()).getDistance() / 1000);
                            barSummaryTV.setText(resources.getString(R.string._21st_hourly_summary));
                        }
                    }
                }

                @Override
                public void onNothingSelected() {
                    // Reset the text color of x-labels when no dataset is selected
                    // xAxis.setTextColor(Color.BLACK);

                    // Refresh the chart to apply the text color changes
                    //barChart.invalidate();
                }
            });


            // barChart.setRenderer(barChartRender);
            barChart.setData(data);


            //barChart.groupBars(0f + marginStart, groupSpace, barSpace);
            if (analyticsType.matches("weekly")) {
                barChart.getBarData().setBarWidth(0.5f);
            } else if (analyticsType.matches("yearly")) {
                barChart.getBarData().setBarWidth(0.8f);
            } else {
                barChart.getBarData().setBarWidth(0.1f);
            }


            barChart.invalidate();
            stopLoading();
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }


    }

    void startLoading() {
        if (getContext() != null) {
            String filter = "analytics_loader";
            Intent intent = new Intent(filter); //If you need extra, add: intent.putExtra("extra","something");
            intent.putExtra("load", true);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        }
    }

    void stopLoading() {
        if (getContext() != null) {
            String filter = "analytics_loader";
            Intent intent = new Intent(filter); //If you need extra, add: intent.putExtra("extra","something");
            intent.putExtra("load", false);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void dailyAnalytics(AnalyticsModel analytics) {
        try {
            if (analytics != null) {

                //if (analytics.getData().size() > 0) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Daily Analytics: " + analytics.toString());
                }
                this.analytics = analytics;
                if (analytics.getTotalSteps() != null) {
                    totalStepsTV.setText(String.valueOf(analytics.getTotalSteps()));

                    double height_in_cm  = Double.parseDouble(userHeight);
                    float distanceValue =  Math.abs(CalculatorUtil.getDistanceNow(context, analytics.getTotalSteps().longValue(), height_in_cm))/1000;
                    totalDistanceTV.setText(String.format("%.2f",distanceValue));
                } else {
                    totalStepsTV.setText("0");
                    totalDistanceTV.setText("0");
                }
                if (analytics.getTotalCalories() != null) {
                    totalCaloriesTV.setText(String.valueOf(analytics.getTotalCalories()));
                } else {
                    totalCaloriesTV.setText("0");
                }

                if (analytics.getData() != null) {
                    drawChart();
               /* } else {
                    stopLoading();
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "activitiesModel.getData().size()==0");
                    }
                }*/
                } else {
                    stopLoading();
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "activitiesModel.getData()==null");
                    }
                }
            } else {
                stopLoading();
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "activitiesModel==null");
                }
            }
        } catch (Exception e) {
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stepAnalyticsFailed(int responseCode, String errorMessage) {
        stopLoading();

    }

}