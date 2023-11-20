package com.cedricapp.fragment;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cedricapp.R;
import com.cedricapp.utils.CustomBarChartRender;
import com.cedricapp.utils.WeekDaysHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthlyStepAnalyticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyStepAnalyticsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    BarChart barChart;

    View view;

    XAxis xAxis;

    List<String> monthNameList;

    String startDate, endDate;

    List<String> dateList;

    List<String> caloriesList;

    TextView stepTV, summaryTV, caloriesTV,txt_step,txt_calories,txt_distance,txt_step1,txt_calories1,txt_distance1,txt_total;;

    Resources resources;
    public MonthlyStepAnalyticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonthlyStepAnalyticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthlyStepAnalyticsFragment newInstance(String param1, String param2) {
        MonthlyStepAnalyticsFragment fragment = new MonthlyStepAnalyticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_monthly_step_analytics, container, false);
        view = inflater.inflate(R.layout.fragment_monthly_step_analytics, container, false);
        monthNameList = new ArrayList<>();
        startDate =new WeekDaysHelper().getPrevious7thDayDate(WeekDaysHelper.getDateTimeNow_yyyyMMdd());
        endDate = WeekDaysHelper.getDateTimeNow_yyyyMMdd();
        dateList = WeekDaysHelper.getDates(startDate,endDate);
        stepTV = view.findViewById(R.id.stepTV);
        summaryTV = view.findViewById(R.id.summaryTV);
        caloriesTV = view.findViewById(R.id.caloriesTV);
        txt_step=view.findViewById(R.id.monthly_step);
        txt_calories=view.findViewById(R.id.monthly_calories);
        txt_distance=view.findViewById(R.id.monthly_distance);
        txt_step1=view.findViewById(R.id.monthly_step1);
        txt_calories1=view.findViewById(R.id.monthly_calories1);
        txt_distance1=view.findViewById(R.id.monthly_distance1);
        txt_total=view.findViewById(R.id.monthly_total);
        //resources = Localization.setLanguage(getContext(),getResources());
        resources = getResources();
        setlanguageToWidget();
        //for(String date:dateList){
        monthNameList.add(resources.getString(R.string.january));
        monthNameList.add(resources.getString(R.string.february));
        monthNameList.add(resources.getString(R.string.march));
        monthNameList.add(resources.getString(R.string.april));
        monthNameList.add(resources.getString(R.string.may));
        monthNameList.add(resources.getString(R.string.june));
        monthNameList.add(resources.getString(R.string.july));
        monthNameList.add(resources.getString(R.string.august));
        monthNameList.add(resources.getString(R.string.september));
        monthNameList.add(resources.getString(R.string.october));
        monthNameList.add(resources.getString(R.string.november));
        monthNameList.add(resources.getString(R.string.december));
        //}
        drawChart();
        return view;
    }

    private void setlanguageToWidget() {
        txt_step.setText(resources.getString(R.string.steps));
        txt_calories.setText(resources.getString(R.string.calories));
        txt_distance.setText(resources.getString(R.string.distance));
        txt_step1.setText(resources.getString(R.string.steps));
        txt_calories1.setText(resources.getString(R.string.calories));
        txt_distance1.setText(resources.getString(R.string.distance));
        txt_total.setText(resources.getString(R.string.total));
    }

    private void drawChart(/*ActivitiesModel activitiesModel*/) {
        barChart = view.findViewById(R.id.barChart);
        barChart.setScaleEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setPinchZoom(false);


// Create a list of BarEntry objects
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f,70000));
        barEntries.add(new BarEntry(1f,800));
        barEntries.add(new BarEntry(2f,50000));
        barEntries.add(new BarEntry(3f,98000));
        barEntries.add(new BarEntry(4f,56000));
        barEntries.add(new BarEntry(5f,28000));
        barEntries.add(new BarEntry(6f,26820));
        barEntries.add(new BarEntry(7f,10000));
        barEntries.add(new BarEntry(8f,65372));
        barEntries.add(new BarEntry(9f,1232));
        barEntries.add(new BarEntry(10f,3412));
        barEntries.add(new BarEntry(11f,4243));

        caloriesList = new ArrayList();

        caloriesList.add("3782");
        caloriesList.add("43.23");
        caloriesList.add("2701.7");
        caloriesList.add("5295");
        caloriesList.add("3026");
        caloriesList.add("1513");
        caloriesList.add("1449.2");
        caloriesList.add("540.3");
        caloriesList.add("3532");
        caloriesList.add("66.57");
        caloriesList.add("184.36");
        caloriesList.add("229.27");
        /*for (int i = 0; i < activitiesModel.getData().size(); i++) {
            if (activitiesModel.getData().get(i).getStepsCount() != null && !activitiesModel.getData().get(i).getStepsCount().matches("")) {
                barEntries.add(new BarEntry(Float.intBitsToFloat(i), Float.parseFloat(activitiesModel.getData().get(i).getStepsCount())));
            } else {
                barEntries.add(new BarEntry(Float.intBitsToFloat(i), 0f));
            }
        }*/

// Add more BarEntry objects for each hour

// Create a BarDataSet with the BarEntry objects
        BarDataSet dataSet = new BarDataSet(barEntries, "Step Count Data");
        int mainColor=0, colorhightlighted=0, linecolor=0, yAxistextcolor=0;
        if (isAdded() && getContext() != null) {
            mainColor = ContextCompat.getColor(getContext(), R.color.yellow);
            colorhightlighted = ContextCompat.getColor(getContext(), R.color.high);
            linecolor = ContextCompat.getColor(getContext(), R.color.lineColor);
            yAxistextcolor = ContextCompat.getColor(getContext(), R.color.yaxis);
        }
        dataSet.setColor(colorhightlighted);
        // Create a custom BarHighlighter

        dataSet.setHighLightColor(mainColor);

        dataSet.setHighlightEnabled(true);


// Create a BarData object from the BarDataSet
        BarData data = new BarData(dataSet);

// Set the data to the BarChart
        // barChart.setData(data);

// Customize the X-axis
        xAxis = barChart.getXAxis();
        xAxis.setSpaceMin(0);
        xAxis.setEnabled(true);
        /*Drawable background = ContextCompat.getDrawable(getContext(), R.drawable.labelshape);
        int[] labelBackgrounds = {R.drawable.labelshape, R.drawable.labelshape, R.drawable.labelshape};*/
// Set custom shape backgrounds for each label
        xAxis.setValueFormatter(/*new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;

                //return WeekDaysHelper.getDayNameByDate(activitiesModel.getData().get(index).getUserActivityDate());
                //return String.valueOf(value);

            }}*/
                new IndexAxisValueFormatter(monthNameList)
        );

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        xAxis.setGranularity(0f);
        xAxis.setDrawLabels(true);
        xAxis.setAxisMaximum(12f);
        xAxis.setAxisMinimum(0f);
        // Set up the OnChartValueSelectedListener
        // xAxis.setRenderer(new CustomXAxisRenderer(barChart.getViewPortHandler(), xAxis, barChart.getTransformer(YAxis.AxisDependency.LEFT), backgroundDrawable));
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(true);
        // barChart.setXAxisRenderer(new ColoredLabelXAxisRenderer(barChart.getViewPortHandler(), barChart.getXAxis(), barChart.getTransformer(YAxis.AxisDependency.LEFT),R.color.lineColor));
        YAxis rightAxis = barChart.getAxisLeft();
        // rightAxis.setAxisMinimum(5f);
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
        //yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setAxisMinimum(0f);
        yAxis.setGranularity(1f);
        //yAxis.setAxisMaximum(13000f);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                /*int hour = (int) value;
                if (hour == 0) {
                    return "0";
                } else {
                    return hour + " lbs";
                }*/
                return String.valueOf((int)value);
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
                stepTV.setText(String.valueOf(e.getY()));
                summaryTV.setText(monthNameList.get((int)e.getX())+" Activity Summary");
                caloriesTV.setText(caloriesList.get((int)e.getX()));
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
        barChart.getBarData().setBarWidth(0.2f);


        barChart.invalidate();


    }
}