package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.adapters.ProgramsAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.ProgramsDataModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class ProgramsFragment extends Fragment {

    public static View.OnClickListener myOnClickListener;
    private static RecyclerView mProgramsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    //private static ArrayList<ProgramsDataModel> programsList = new ArrayList<>();
    //private static RecyclerView.Adapter programsAdapter;
    private ProgramsAdapter programsAdapter;
    private ConstraintLayout mConstraintLayout;
    private ImageButton backArrow;
    private DatabaseReference mDatabaseReference;
    List<ProgramsDataModel.Datum> programDataList;
    //private CircularProgressIndicator circularProgress;

    LottieAnimationView loading_lav;
    BlurView blurView;
    DBHelper dbHelper;
    private WeekDaysHelper weekDaysHelper;
    private String toDate;

    public ProgramsDataModel programData;
    private View view1;
    boolean isLoading = false;
    private int currentDateDifference;
    SwipeRefreshLayout swipeRefreshLayout;
    private String message;

    MaterialTextView programTV, best_programTV;

    Resources resources;

    String TAG = "PROGRAM_FRAGMENT_TAG";

    public ProgramsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weekDaysHelper = new WeekDaysHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_programs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        //Initialize widgets
        init();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isAdded()) {
                    if (getContext() != null) {
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            stopLoading();
                            getAllPrograms(SessionUtil.getAccessToken(getContext()));
                        } else {
                            if (swipeRefreshLayout != null) {
                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                            showToast(resources.getString(R.string.turn_on_internet));
                            //StoptShimmer();
                        }
                    } else {
                        if (swipeRefreshLayout != null) {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                        // showToast("No Internet, Please turn ON Internet");
                    }
                } else {
                    if (swipeRefreshLayout != null) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }

            }
        });


        //dissable user interaction
        /*requireActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/

        //dbHelper = new DBHelper(getContext());
    }


    private void init() {
        dbHelper = new DBHelper(getContext());
        //programDataList=new ArrayList<>();
        /*circularProgress = view1.findViewById(R.id.circular_progress);*/
        swipeRefreshLayout = view1.findViewById(R.id.pullToRefreshProgram);
        mProgramsRecyclerView = view1.findViewById(R.id.recyclerViewPrograms);
        mConstraintLayout = view1.findViewById(R.id.program_fragment);
        loading_lav = view1.findViewById(R.id.loading_lav);
        blurView = view1.findViewById(R.id.blurView);
        programTV = view1.findViewById(R.id.programTV);
        best_programTV = view1.findViewById(R.id.best_programTV);

        programTV.setText(resources.getString(R.string.programs));
        best_programTV.setText(resources.getString(R.string.best_programs));


        getDataFromDbOrAPI();


        // you can set max and current progress values individually
        /*circularProgress.setMaxProgress(100);
        circularProgress.setCurrentProgress(90);*/
        // or all at once
        /*circularProgress.setProgress(50, 10000);*/

        // you can get progress values using following getters
        /*circularProgress.getProgress() // returns 5000
        circularProgress.getMaxProgress() // returns 10000*/


    }

    private void getDataFromDbOrAPI() {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                currentDateDifference = WeekDaysHelper.getCountOfDays(weekDaysHelper.getCurrentDateLikeServer(), WeekDaysHelper.getDateTimeNow_yyyyMMdd());
                if (currentDateDifference == 1) {
                    programDataList = dbHelper.getAllProgram();
                    if (programDataList != null && programDataList.size() > 0) {
                        stopLoading();
                        setProgramAdapter(programDataList);
                    } else {
                        blurrBackground();
                        StartLoading();
                        getAllPrograms(SessionUtil.getAccessToken(getContext()));
                    }
                } else {
                    blurrBackground();
                    StartLoading();
                    dbHelper.deleteAllProgramsData();
                    getAllPrograms(SessionUtil.getAccessToken(getContext()));
                }
            } else {
                programDataList = dbHelper.getAllProgram();
                if (programDataList != null && programDataList.size() > 0) {
                    stopLoading();
                    setProgramAdapter(programDataList);
                } else {
                    showToast(resources.getString(R.string.no_internet_connection_no_data_available));
                }
            }

        }
    }


    private void getAllPrograms(String token) {

        // on below line we are calling a method to get all the programs from API.
        Call<ProgramsDataModel> call = ApiClient.getService().getAllPrograms("Bearer " + token);

        // on below line we are calling method to enqueue and calling
        // all the data from array list.
        call.enqueue(new Callback<ProgramsDataModel>() {
            @Override
            public void onResponse(Call<ProgramsDataModel> call, Response<ProgramsDataModel> response) {
                try {
                    // inside on response method we are checking
                    // if the response is success or not.
                    if (response.isSuccessful()) {
                        // below line is to add our data from api to our array list.
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(TAG, "Response Status " + message.toString());
                        }
                        //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        programData = response.body();
                        if (Common.isLoggingEnabled) {
                            if (programData != null) {
                                Log.d(TAG, programData.toString());
                            } else {
                                Log.e(TAG, "Program data is null");
                            }
                        }
                        if (programData != null) {
                            if (programData.getStatus()) {
                                if (programData.getData() != null) {
                                    if (programData.getData().size() > 0) {
                                        addprogramDatatoDB(programData);
                                        if (isAdded() && getContext() != null) {
                                            //get data from db or server
                                            getProgramsDataFromDBorServer(programData.getData());

                                        } else {
                                            if (Common.isLoggingEnabled) {
                                                Log.e(TAG, "Fragement is not added to activity");
                                            }
                                        }
                                    } else {
                                        showToast(resources.getString(R.string.workout_not_available));
                                        if (Common.isLoggingEnabled) {
                                            Log.e(TAG, "Program Data list size is 0");
                                        }
                                    }
                                } else {
                                    showToast(resources.getString(R.string.something_went_wrong));
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "Program Data is null");
                                    }
                                }
                            } else {
                                showToast(resources.getString(R.string.something_went_wrong));
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Program Status is false");
                                }
                            }

                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Program List is null");
                            }
                            showToast(resources.getString(R.string.something_went_wrong));
                        }
                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (message != null)
                            Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Response Status " + message.toString());
                            Log.e(TAG, "Response is not successfully retrieved from server");
                        }
                        //showToast(resources.getString(R.string.program_not_successfully_retrieved));
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("ProgramsFragment_swipeTORefresh",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                    }
                    showToast(resources.getString(R.string.something_went_wrong));
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
                if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                stopLoading();
            }

            @Override
            public void onFailure(Call<ProgramsDataModel> call, Throwable t) {
                // in the method of on failure we are displaying a
                // toast message for fail to get data.
                FirebaseCrashlytics.getInstance().recordException(t);
                stopLoading();
                showToast(resources.getString(R.string.fail_to_retrieve_program));
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("ProgramsFragment_getAllProgram",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
            }
        });

    }

    private void addprogramDatatoDB(ProgramsDataModel programData) {
        for (int i = 0; i < programData.getData().size(); i++) {
            if (!dbHelper.checkProgramId(String.valueOf(programData.getData().get(i).getId()))) {
                dbHelper.addProgram(programData.getData().get(i));
            } else {
                dbHelper.updateProgram(programData.getData().get(i));
            }
        }
    }

    private void getProgramsDataFromDBorServer(List<ProgramsDataModel.Datum> programData) {
        // for(int i=0 ;i<programData.getData().size();i++){
        List<ProgramsDataModel.Datum> programData2 = dbHelper.getAllProgram();
        if (programData2 != null && programData2.size() > 0) {
            stopLoading();
            setProgramAdapter(programData2);
        } else {
            stopLoading();
            setProgramAdapter(programData);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = true;
    }

    private void setProgramAdapter(List<ProgramsDataModel.Datum> programData) {
        programsAdapter = new ProgramsAdapter(getContext(), programData);
        //   recyclerview for first horizontal cardView
        mProgramsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mProgramsRecyclerView.setLayoutManager(layoutManager);
        mProgramsRecyclerView.setAdapter(programsAdapter);
        programsAdapter.notifyDataSetChanged();
    }

    private void StartLoading() {

        //dissable user interaction
        requireActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        isLoading = true;
        loading_lav.setVisibility(View.VISIBLE);
        loading_lav.playAnimation();
    }

    private void stopLoading() {
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction
        Activity activity = getActivity();
        try {
            if (isAdded() && activity != null) {
                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } catch (ActivityNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("ProgramFragment_stopLoading",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }

        isLoading = false;
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();

    }

    private void blurrBackground() {
        blurView.setVisibility(View.VISIBLE);
        float radius = 1f;

        //======================add disable button when load
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });

        View decorView = requireActivity().getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(requireContext()))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(false);
    }

    @Override
    public void onPause() {
        if (isLoading) {
            stopLoading();
        }
        super.onPause();

    }


    void showToast(String message) {
        try {
            if (isAdded()) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "getContext is null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Toast: Fragement is not added to activity");
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("ProgramsFragment_showToasr",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
    }


    /*private void getProgramsDataFromAPI(ArrayList<ProgramsDataModel> programsList) {
        // below line we are running a loop to add data to our adapter class.
        for (int i = 0; i < programsList.size(); i++) {

            // below line is to set adapter to our recycler view.
            programsAdapter = new ProgramsAdapter(getContext(), programsList);
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mProgramsRecyclerView.setLayoutManager(layoutManager);
            mProgramsRecyclerView.setAdapter(programsAdapter);
        }
        //StopLoading();

    }

    private void programsDataFromLocalDb(List<ProgramsDataModel> programData) {
        // below line we are running a loop to add data to our adapter class.
        for (int i = 0; i < programData.size(); i++) {

            // below line is to set adapter to our recycler view.
            programsAdapter = new ProgramsAdapter(getContext(), programData);
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mProgramsRecyclerView.setLayoutManager(layoutManager);
            mProgramsRecyclerView.setAdapter(programsAdapter);

        }
        //StopLoading();
    }*/
    private void checkNetworkConnection() {
        /*if (isOnline()) {
            getAllPrograms();
        } else {
            *//*programData = dbHelper.getAllProgram();

            if (programData.size() != 0) {
                programsDataFromLocalDb(programData);
            } else {

                blurrBackground();
                StartLoading();
                Toast.makeText(getContext(), "No internet Connection and ,No data Available", Toast.LENGTH_LONG).show();
            }*//*
        }*/
    }
}