package com.cedricapp.adapters;

import static com.cedricapp.R.drawable.textview_circular_gradient_shape;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.fragment.BestProgramExercises;
import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.model.ProgressDataModel;
import com.cedricapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.MyViewHolder> {
    // private static List<ProgressDataModel> progressDataList;
    ProgressDataModel progressDataModel;
    private ArrayList<String> daysCheckedList = null;
    private ArrayList<String> selectedWeekList = null;

    private Context context;
    private MaterialTextView mTextViewDay1, mTextViewDay2, mTextViewDay3, mTextViewDay4,
            mTextViewDay5, mTextViewDay6, mTextViewDay7;
    MaterialCardView mCardViewNext;
    String selectedDay;
    int seletedWeek, month, year, today, day, level_id, goal_id;


    //SharedPreferences preferences;
    //DaysCheckedListInterface daysCheckedListInterface;
    //private WeekDaysHelper weekDaysHelper;
    /*ArrayList<DateModel> currentWeekDate;
    ArrayList<DateModel> secondWeekDate;
    ArrayList<DateModel> thirdWeekDate;
    ArrayList<DateModel> fourthWeekDate;
    ArrayList<String> weekDaysList;*/
    private boolean mStateChanged = false;
    private String weekNumber, formattedDate;
    private HashMap<String, String> weekListCompleted = new HashMap<>();
    private Calendar calendar;
    private CircleImageView circleImageViewDay5, circleImageViewDay6;
    private MyViewHolder viewHolder;
    View.OnClickListener myOnClickListener;
    private int formateDay;
    int week;
    int totalWeeks, unlockDays, unLockWeeks, currentUserId, programId;
    List<Integer> unlockDaysList;
    String programName, programDescription;


   /* public ProgressAdapter(Context context, ProgressDataModel progressDataModel, int week, int today,
                           String programName, String programDescription) {
        this.context = context;
        this.progressDataModel = progressDataModel;
        //this.daysCheckedListInterface = daysCheckedListInterface;
        this.today = today;
        this.week = week;
        this.programName = programName;
        this.programDescription = programDescription;

        //get user Id


        *//*calendar = Calendar.getInstance();

        this.weekDaysHelper = new WeekDaysHelper();

        weekDaysList = this.weekDaysHelper.getWeeksOfMonth(day, month, year);
        System.out.println(weekDaysList.toString() + "fff");
*//*

        *//*currentWeekDate = this.weekDaysHelper.getCurrentWeek();
        secondWeekDate = this.weekDaysHelper.getFurtherWeek(currentWeekDate.get(6).getDay() + 1, currentWeekDate.get(6).getMonth() - 1);
        thirdWeekDate = this.weekDaysHelper.getFurtherWeek(secondWeekDate.get(6).getDay() + 1, secondWeekDate.get(6).getMonth() - 1);
        fourthWeekDate = this.weekDaysHelper.getFurtherWeek(thirdWeekDate.get(6).getDay() + 1, thirdWeekDate.get(6).getMonth() - 1);

           LocalDate now = LocalDate.now();*//*
        //String jjj = now.format(DateTimeFormatter.ofPattern("MM dd YYYY"));
        *//*formattedDate = now.format(DateTimeFormatter.ofPattern("MMM dd YYYY"))*//*
        ;
        //  Log.d("firstday", String.valueOf(jjj));
       *//* Log.d("firstday", formattedDate);
        Log.d("firstday", currentUserId);*//*

        //checkSignupDate();
        *//*String signupDate=SharedData.signupDate;
        Log.d("datee", signupDate.toString());


        Log.d("datee", String.valueOf(month));
        Log.d("datee", String.valueOf(year));*//*


    }*/

    public ProgressAdapter(Context context, int totalWeeks, int unlockDays, int unLockWeeks, int programId, int user_id, String programName) {
        this.context = context;
        this.totalWeeks = totalWeeks;
        this.unlockDays = unlockDays;
        this.unLockWeeks = unLockWeeks;
        this.currentUserId = user_id;
        this.programId = programId;
        level_id = Integer.parseInt(SharedData.level_id);
        goal_id = Integer.parseInt(SharedData.goal_id);
        this.programName = programName;
    }


    @Override
    public ProgressAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_cardview, parent, false);

        //view.setOnClickListener(ProgressAdapter.myOnClickListener);

        ProgressAdapter.MyViewHolder myViewHolder = new ProgressAdapter.MyViewHolder(view);
        return myViewHolder;
    }


    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int listPosition) {

        viewHolder = holder;
        currentUserId = Integer.parseInt(SharedData.id);
        holder.weekName.setText(context.getString(R.string.week)+" " + (listPosition + 1));
        /*unLockWeeks=3;*/
        if ((listPosition + 1) < unLockWeeks) {
            unlockAllDays(holder);
        } else {
            if (listPosition + 1 == unLockWeeks) {
                for (int j = 1; j <= unlockDays; j++) {
                    if (j == 1) {
                        holder.circleImageViewDay1.setVisibility(View.GONE);
                        holder.mTextViewDay1.setVisibility(View.VISIBLE);
                        holder.mTextViewDay1.setText("M");

                    } else if (j == 2) {
                        holder.circleImageViewDay2.setVisibility(View.GONE);
                        holder.mTextViewDay2.setVisibility(View.VISIBLE);
                        holder.mTextViewDay2.setText("T");

                        // first day
                        holder.circleImageViewDay1.setVisibility(View.GONE);
                        holder.mTextViewDay1.setVisibility(View.VISIBLE);
                        holder.mTextViewDay1.setText("M");
                    } else if (j == 3) {
                        holder.circleImageViewDay3.setVisibility(View.GONE);
                        holder.mTextViewDay3.setVisibility(View.VISIBLE);
                        holder.mTextViewDay3.setText("W");

                        //first day
                        holder.circleImageViewDay1.setVisibility(View.GONE);
                        holder.mTextViewDay1.setVisibility(View.VISIBLE);
                        holder.mTextViewDay1.setText("M");
                        //second day
                        holder.circleImageViewDay2.setVisibility(View.GONE);
                        holder.mTextViewDay2.setVisibility(View.VISIBLE);
                        holder.mTextViewDay2.setText("T");

                    } else if (j == 4) {
                        holder.circleImageViewDay4.setVisibility(View.GONE);
                        holder.mTextViewDay4.setVisibility(View.VISIBLE);
                        holder.mTextViewDay4.setText("T");

                        //first day
                        holder.circleImageViewDay1.setVisibility(View.GONE);
                        holder.mTextViewDay1.setVisibility(View.VISIBLE);
                        holder.mTextViewDay1.setText("M");
                        //second day
                        holder.circleImageViewDay2.setVisibility(View.GONE);
                        holder.mTextViewDay2.setVisibility(View.VISIBLE);
                        holder.mTextViewDay2.setText("T");

                        //third day
                        holder.circleImageViewDay3.setVisibility(View.GONE);
                        holder.mTextViewDay3.setVisibility(View.VISIBLE);
                        holder.mTextViewDay3.setText("W");

                    } else if (j == 5) {
                        holder.circleImageViewDay5.setVisibility(View.GONE);
                        holder.mTextViewDay5.setVisibility(View.VISIBLE);
                        holder.mTextViewDay5.setText("F");

                        //first day
                        holder.circleImageViewDay1.setVisibility(View.GONE);
                        holder.mTextViewDay1.setVisibility(View.VISIBLE);
                        holder.mTextViewDay1.setText("M");
                        //second day
                        holder.circleImageViewDay2.setVisibility(View.GONE);
                        holder.mTextViewDay2.setVisibility(View.VISIBLE);
                        holder.mTextViewDay2.setText("T");

                        //third day
                        holder.circleImageViewDay3.setVisibility(View.GONE);
                        holder.mTextViewDay3.setVisibility(View.VISIBLE);
                        holder.mTextViewDay3.setText("W");

                        //fourth day
                        holder.circleImageViewDay4.setVisibility(View.GONE);
                        holder.mTextViewDay4.setVisibility(View.VISIBLE);
                        holder.mTextViewDay4.setText("T");

                    } else if (j == 6) {
                        holder.circleImageViewDay6.setVisibility(View.GONE);
                        holder.mTextViewDay6.setVisibility(View.VISIBLE);
                        holder.mTextViewDay6.setText("S");

                        //first day
                        holder.circleImageViewDay1.setVisibility(View.GONE);
                        holder.mTextViewDay1.setVisibility(View.VISIBLE);
                        holder.mTextViewDay1.setText("M");
                        //second day
                        holder.circleImageViewDay2.setVisibility(View.GONE);
                        holder.mTextViewDay2.setVisibility(View.VISIBLE);
                        holder.mTextViewDay2.setText("T");

                        //third day
                        holder.circleImageViewDay3.setVisibility(View.GONE);
                        holder.mTextViewDay3.setVisibility(View.VISIBLE);
                        holder.mTextViewDay3.setText("W");

                        //fourth day
                        holder.circleImageViewDay4.setVisibility(View.GONE);
                        holder.mTextViewDay4.setVisibility(View.VISIBLE);
                        holder.mTextViewDay4.setText("T");

                        //fifth day
                        holder.circleImageViewDay5.setVisibility(View.GONE);
                        holder.mTextViewDay5.setVisibility(View.VISIBLE);
                        holder.mTextViewDay5.setText("F");

                    } else if (j == 7) {
                        holder.circleImageViewDay7.setVisibility(View.GONE);
                        holder.mTextViewDay7.setVisibility(View.VISIBLE);
                        holder.mTextViewDay7.setText("S");

                        //first day
                        holder.circleImageViewDay1.setVisibility(View.GONE);
                        holder.mTextViewDay1.setVisibility(View.VISIBLE);
                        holder.mTextViewDay1.setText("M");
                        //second day
                        holder.circleImageViewDay2.setVisibility(View.GONE);
                        holder.mTextViewDay2.setVisibility(View.VISIBLE);
                        holder.mTextViewDay2.setText("T");

                        //third day
                        holder.circleImageViewDay3.setVisibility(View.GONE);
                        holder.mTextViewDay3.setVisibility(View.VISIBLE);
                        holder.mTextViewDay3.setText("W");

                        //fourth day
                        holder.circleImageViewDay4.setVisibility(View.GONE);
                        holder.mTextViewDay4.setVisibility(View.VISIBLE);
                        holder.mTextViewDay4.setText("T");

                        //fifth day
                        holder.circleImageViewDay5.setVisibility(View.GONE);
                        holder.mTextViewDay5.setVisibility(View.VISIBLE);
                        holder.mTextViewDay5.setText("F");

                        //6th day
                        holder.circleImageViewDay6.setVisibility(View.GONE);
                        holder.mTextViewDay6.setVisibility(View.VISIBLE);
                        holder.mTextViewDay6.setText("S");


                    }
                }
            }

        }

        myOnClickListener = new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (context != null) {
                    //TODO commit internet connection
                    /*   if (ConnectionDetector.isConnectedWithInternet(context)) {*/
                    if (v.getId() == R.id.textDay1) {
                        selectedDay = "1";
                        //  ProgressDataModel.Data.Workouts myWorkouts = getWorkoutsByWeekAndDay(selectedDay, (listPosition + 1), progressDataModel.getData().getWorkouts());
                        if (unlockDays != 0 && programId != 0 && currentUserId != 0 && level_id != 0 && goal_id != 0) {
                            startFragment(Integer.parseInt(selectedDay), (listPosition + 1), programId, currentUserId, level_id, goal_id);
                        } else {
                            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Selected day is " + selectedDay + " in week " + (listPosition + 1) + " myWorkouts are null");
                        }
                    } else if (v.getId() == R.id.textDay2) {
                        selectedDay = "2";
                        // ProgressDataModel.Data.Workouts myWorkouts = getWorkoutsByWeekAndDay(selectedDay, (listPosition + 1), progressDataModel.getData().getWorkouts());
                        if (unlockDays != 0 && programId != 0 && currentUserId != 0 && level_id != 0 && goal_id != 0) {
                            startFragment(Integer.parseInt(selectedDay), (listPosition + 1), programId, currentUserId, level_id, goal_id);
                        } else {
                            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Selected day is " + selectedDay + " in week " + (listPosition + 1) + " myWorkouts are null");
                        }
                    } else if (v.getId() == R.id.textDay3) {
                        selectedDay = "3";
                        //ProgressDataModel.Data.Workouts myWorkouts = getWorkoutsByWeekAndDay(selectedDay, (listPosition + 1), progressDataModel.getData().getWorkouts());
                        if (unlockDays != 0 && programId != 0 && currentUserId != 0 && level_id != 0 && goal_id != 0) {
                            startFragment(Integer.parseInt(selectedDay), (listPosition + 1), programId, currentUserId, level_id, goal_id);
                        } else {
                            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Selected day is " + selectedDay + " in week " + (listPosition + 1) + " myWorkouts are null");
                        }
                    } else if (v.getId() == R.id.textDay4) {
                        selectedDay = "4";
                        // ProgressDataModel.Data.Workouts myWorkouts = getWorkoutsByWeekAndDay(selectedDay, (listPosition + 1), progressDataModel.getData().getWorkouts());
                        if (unlockDays != 0 && programId != 0 && currentUserId != 0 && level_id != 0 && goal_id != 0) {
                            startFragment(Integer.parseInt(selectedDay), (listPosition + 1), programId, currentUserId, level_id, goal_id);
                        } else {
                            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Selected day is " + selectedDay + " in week " + (listPosition + 1) + " myWorkouts are null");
                        }
                    } else if (v.getId() == R.id.textDay5) {
                        selectedDay = "5";
                        //  ProgressDataModel.Data.Workouts myWorkouts = getWorkoutsByWeekAndDay(selectedDay, (listPosition + 1), progressDataModel.getData().getWorkouts());
                        if (unlockDays != 0 && programId != 0 && currentUserId != 0 && level_id != 0 && goal_id != 0) {
                            startFragment(Integer.parseInt(selectedDay), (listPosition + 1), programId, currentUserId, level_id, goal_id);
                        } else {
                            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Selected day is " + selectedDay + " in week " + (listPosition + 1) + " myWorkouts are null");
                        }
                    } else if (v.getId() == R.id.textDay6) {
                        selectedDay = "6";
                        // ProgressDataModel.Data.Workouts myWorkouts = getWorkoutsByWeekAndDay(selectedDay, (listPosition + 1), progressDataModel.getData().getWorkouts());
                        if (unlockDays != 0 && programId != 0 && currentUserId != 0 && level_id != 0 && goal_id != 0) {
                            startFragment(Integer.parseInt(selectedDay), (listPosition + 1), programId, currentUserId, level_id, goal_id);
                        } else {
                            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Selected day is " + selectedDay + " in week " + (listPosition + 1) + " myWorkouts are null");
                        }
                    } else if (v.getId() == R.id.textDay7) {
                        selectedDay = "7";
                        // ProgressDataModel.Data.Workouts myWorkouts = getWorkoutsByWeekAndDay(selectedDay, (listPosition + 1), progressDataModel.getData().getWorkouts());
                        if (unlockDays != 0 && programId != 0 && currentUserId != 0 && level_id != 0 && goal_id != 0) {
                            startFragment(Integer.parseInt(selectedDay), (listPosition + 1), programId, currentUserId, level_id, goal_id);
                        } else {
                            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Selected day is " + selectedDay + " in week " + (listPosition + 1) + " myWorkouts are null");
                        }
                    }

                    /*} else {
                        Toast.makeText(context, context.getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "No Internet Connection detected");
                        }
                    }*/
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Progress Adapter: Context is null");
                    }

                }


            }

        };


        holder.circleImageViewDay1.setOnClickListener(myOnClickListener);
        holder.circleImageViewDay2.setOnClickListener(myOnClickListener);
        holder.circleImageViewDay3.setOnClickListener(myOnClickListener);
        holder.circleImageViewDay4.setOnClickListener(myOnClickListener);
        holder.circleImageViewDay5.setOnClickListener(myOnClickListener);
        holder.circleImageViewDay6.setOnClickListener(myOnClickListener);
        holder.circleImageViewDay7.setOnClickListener(myOnClickListener);
        holder.mTextViewDay1.setOnClickListener(myOnClickListener);
        holder.mTextViewDay2.setOnClickListener(myOnClickListener);
        holder.mTextViewDay3.setOnClickListener(myOnClickListener);
        holder.mTextViewDay4.setOnClickListener(myOnClickListener);
        holder.mTextViewDay5.setOnClickListener(myOnClickListener);
        holder.mTextViewDay6.setOnClickListener(myOnClickListener);
        holder.mTextViewDay7.setOnClickListener(myOnClickListener);




    }

    void unlockAllDays(final MyViewHolder holder) {
        holder.circleImageViewDay1.setVisibility(View.GONE);
        holder.mTextViewDay1.setVisibility(View.VISIBLE);
        holder.mTextViewDay1.setText("M");

        holder.circleImageViewDay2.setVisibility(View.GONE);
        holder.mTextViewDay2.setVisibility(View.VISIBLE);
        holder.mTextViewDay2.setText("T");

        holder.circleImageViewDay3.setVisibility(View.GONE);
        holder.mTextViewDay3.setVisibility(View.VISIBLE);
        holder.mTextViewDay3.setText("W");

        holder.circleImageViewDay4.setVisibility(View.GONE);
        holder.mTextViewDay4.setVisibility(View.VISIBLE);
        holder.mTextViewDay4.setText("T");

        holder.circleImageViewDay5.setVisibility(View.GONE);
        holder.mTextViewDay5.setVisibility(View.VISIBLE);
        holder.mTextViewDay5.setText("F");

        holder.circleImageViewDay6.setVisibility(View.GONE);
        holder.mTextViewDay6.setVisibility(View.VISIBLE);
        holder.mTextViewDay6.setText("S");

        holder.circleImageViewDay7.setVisibility(View.GONE);
        holder.mTextViewDay7.setVisibility(View.VISIBLE);
        holder.mTextViewDay7.setText("S");
    }


    @Override
    public int getItemCount() {
        return totalWeeks;
    }


    private void startFragment(int day, int week, int programId, int currentUserId, int level_id, int goal_id) {

        Fragment fragment = new BestProgramExercises();
        FragmentTransaction mFragmentTransaction = ((FragmentActivity) context)
                .getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("selectedDay", day); //key and value
        bundle.putInt("selectedWeek", week); //key and value
        bundle.putInt("user_id", currentUserId); //key and value
        bundle.putInt("programId", programId); //key and value
        bundle.putInt("level_id", level_id); //key and value
        bundle.putInt("goal_id", goal_id); //key and value
        //bundle.putParcelable("workout", workout);
        bundle.putString("programName", programName);
        bundle.putString("programDescription", programDescription);

//        bundle.putString("ProgramId", String.valueOf(uploadCurrent.getProgramId()));
//        bundle.putString("noOfWeeks", (uploadCurrent.getTotalWeeks()));

        fragment.setArguments(bundle);
        mFragmentTransaction.replace(R.id.navigation_container, fragment);
        mFragmentTransaction.addToBackStack("ProgressFragment");
        mFragmentTransaction.commit();

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView weekName;
        ImageView progressImage;
        MaterialTextView days;
        Calendar calendar;
        private MaterialTextView mTextViewDay1, mTextViewDay2, mTextViewDay3, mTextViewDay4,
                mTextViewDay5, mTextViewDay6, mTextViewDay7;
        ImageView mImageViewDay8;
        MaterialCardView mCardViewDay1, mCardViewDay2, mCardViewDay3, mCardViewDay4,
                mCardViewDay5, mCardViewDay6, mCardViewDay7, mCardViewDay8;
        MaterialCardView mCardViewNext;
        CircleImageView circleImageViewDay1, circleImageViewDay2, circleImageViewDay3, circleImageViewDay4,
                circleImageViewDay5, circleImageViewDay6, circleImageViewDay7;


        public MyViewHolder(View itemView) {
            super(itemView);


            this.weekName = (MaterialTextView) itemView.findViewById(R.id.textViewWeek);
            this.progressImage = (ImageView) itemView.findViewById(R.id.progress_cardView_image);
            this.mTextViewDay1 = itemView.findViewById(R.id.textDay1);
            this.mTextViewDay2 = itemView.findViewById(R.id.textDay2);
            this.mTextViewDay3 = itemView.findViewById(R.id.textDay3);
            this.mTextViewDay4 = itemView.findViewById(R.id.textDay4);
            this.mTextViewDay5 = itemView.findViewById(R.id.textDay5);
            this.mTextViewDay6 = itemView.findViewById(R.id.textDay6);
            this.mTextViewDay7 = itemView.findViewById(R.id.textDay7);
            this.circleImageViewDay1 = itemView.findViewById(R.id.lockTextDay1);
            this.circleImageViewDay2 = itemView.findViewById(R.id.lockTextDay2);
            this.circleImageViewDay3 = itemView.findViewById(R.id.lockTextDay3);
            this.circleImageViewDay4 = itemView.findViewById(R.id.lockTextDay4);
            this.circleImageViewDay5 = itemView.findViewById(R.id.lockTextDay5);
            this.circleImageViewDay6 = itemView.findViewById(R.id.lockTextDay6);
            this.circleImageViewDay7 = itemView.findViewById(R.id.lockTextDay7);


        }


    }

    private void checkSignupDate() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);
        SharedData.signupDate = sharedPreferences.getString("signupDate", "");
        Log.d("firstday", SharedData.signupDate);
        //get day from current date
        String[] currentDateParts = formattedDate.split(" ");
        formateDay = Integer.parseInt(currentDateParts[1]);
        Log.d("dat", String.valueOf(formateDay));

        if (formattedDate.equals(SharedData.signupDate)) {
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
            day = calendar.get(Calendar.DATE);

            Log.d("dateed", String.valueOf(month));
            Log.d("dateed", String.valueOf(day));
            Log.d("dateed", String.valueOf(year));

        } else {
            if (SharedData.signupDate.isEmpty()) {
                SharedData.signupDate = formattedDate;
            }

            String[] dateParts = SharedData.signupDate.split(" ");
            day = Integer.parseInt(dateParts[1]);
            String monthe = (dateParts[0]);
            year = Integer.parseInt(dateParts[2]);
            if (monthe.equals("Jan")) {
                month = 00;
            } else if (monthe.equals("Feb")) {
                month = 01;
            } else if (monthe.equals("Mar")) {
                month = 02;
            } else if (monthe.equals("Apr")) {
                month = 03;
            } else if (monthe.equals("May")) {
                month = 04;
            } else if (monthe.equals("Jun")) {
                month = 05;
            } else if (monthe.equals("Jul")) {
                month = 06;
            } else if (monthe.equals("Aug")) {
                month = 07;
            } else if (monthe.equals("Sep")) {
                month = 8;
            } else if (monthe.equals("Oct")) {
                month = 9;
            } else if (monthe.equals("Nov")) {
                month = 10;
            } else if (monthe.equals("Dec")) {
                month = 11;
            }


            Log.d("dateed", String.valueOf(month));
            Log.d("dateed", String.valueOf(day));
            Log.d("dateed", String.valueOf(year));
        }
    }


    public void checkDataForSharedPreference() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SaveButton", Context.MODE_PRIVATE);
        int selectedDay = sharedPreferences.getInt("selectedDay", 0);
        if (selectedDay == 1) {
            mTextViewDay1.setBackgroundResource(textview_circular_gradient_shape);
        }

    }

    private void checkForDaysOpened() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(currentUserId), Context.MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.

     /*   String json = sharedPreferences.getString("daysCheckedList", "null");
        String json1 = sharedPreferences.getString("weekDaysList", "null");*/
        String json2 = sharedPreferences.getString("weekDaysCompleted", "null");
        Log.d("list", json2);

        // below line is to get the type of our array list.
      /*  Type type = new TypeToken<ArrayList<String>>() {
        }.getType();*/

        Type type1 = new TypeToken<HashMap<String, String>>() {
        }.getType();

        Log.d("list", type1.toString());

        // in below line we are getting data from gson
        // and saving it to our array list
       /* daysCheckedList = gson.fromJson(json, type);
        selectedWeekList = gson.fromJson(json1, type);*/
        if (json2.equalsIgnoreCase("null")) {
            weekListCompleted.put("rrr", "iiii");
        } else {
            weekListCompleted = gson.fromJson(json2, type1);
        }


        // checking below if the array list is empty or not
      /*  Log.d("mylist", daysCheckedList.toString());
        Log.d("mylist1", selectedWeekList.toString());*/
        Log.d("mylist2", weekListCompleted.toString());
    }

}



