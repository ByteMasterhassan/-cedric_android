package com.cedricapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.adapters.FiltersWorkoutListAdapter;
import com.cedricapp.model.FiltersWorkoutModel;
import com.cedricapp.R;
import com.cedricapp.activity.HomeActivity;

import java.util.ArrayList;


@SuppressWarnings("ALL")
public class FiltersFragment extends Fragment {

    private View view1;
    private RecyclerView mFilterWorkoutRecyclerView,mFilterYogaRecyclerview,mFilterMealRecyclerview;
    private LinearLayoutManager layoutManager;
    private static ArrayList<FiltersWorkoutModel> workoutCheckList;
    private static RecyclerView.Adapter workoutCheckedListAdapter;
    private ImageButton backArrow;

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.hideBottomNav();
    }
    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filters, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1=view;
        init();
    }

    private void init() {
        mFilterWorkoutRecyclerView = view1.findViewById(R.id.workoutListRecyclerview);
        mFilterYogaRecyclerview = view1.findViewById(R.id.yogaRecyclerView);
        mFilterMealRecyclerview = view1.findViewById(R.id.mealRecyclerView);
        backArrow = view1.findViewById(R.id.backArrow);

        //   recyclerview for first vertical workoutList checkbox
        mFilterWorkoutRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mFilterWorkoutRecyclerView.setLayoutManager(layoutManager);
        populateWorkoutList();

        //   recyclerview for first vertical yoga checkbox
        mFilterYogaRecyclerview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mFilterYogaRecyclerview.setLayoutManager(layoutManager);
        populateYogaCheckedList();

        //   recyclerview for first vertical meal checkbox
        mFilterYogaRecyclerview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mFilterMealRecyclerview.setLayoutManager(layoutManager);
        populateMealsCheckedList();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popback();

            }
        });

    }

    private void popback() {
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        }
    }

    private void populateMealsCheckedList() {
        workoutCheckList = new ArrayList<FiltersWorkoutModel>();
        workoutCheckList.add(new FiltersWorkoutModel("Breakfast",false));
        workoutCheckList.add(new FiltersWorkoutModel("Brunch",false));
        workoutCheckList.add(new FiltersWorkoutModel("supper",false));
        workoutCheckList.add(new FiltersWorkoutModel("Dinner",false));
        workoutCheckList.add(new FiltersWorkoutModel("Lunch",false));

        workoutCheckedListAdapter = new FiltersWorkoutListAdapter(workoutCheckList,getContext());
        mFilterMealRecyclerview.setAdapter(workoutCheckedListAdapter);
    }

    private void populateYogaCheckedList() {
        workoutCheckList = new ArrayList<FiltersWorkoutModel>();
        workoutCheckList.add(new FiltersWorkoutModel("Benefits Of Asanas",false));
        workoutCheckList.add(new FiltersWorkoutModel("Sukhasana Or Easy Pose",false));
        workoutCheckList.add(new FiltersWorkoutModel("Naukasana Or Boat Pose",false));
        workoutCheckList.add(new FiltersWorkoutModel("Dhanurasana Or Bow Pose",false));

        workoutCheckedListAdapter = new FiltersWorkoutListAdapter(workoutCheckList,getContext());
        mFilterYogaRecyclerview.setAdapter(workoutCheckedListAdapter);
    }

    private void populateWorkoutList() {
        workoutCheckList = new ArrayList<FiltersWorkoutModel>();
        workoutCheckList.add(new FiltersWorkoutModel("Biceps Workout",false));
        workoutCheckList.add(new FiltersWorkoutModel("Biceps Workout",false));
        workoutCheckList.add(new FiltersWorkoutModel("Biceps Workout",false));
        workoutCheckList.add(new FiltersWorkoutModel("Biceps Workout",false));

        workoutCheckedListAdapter = new FiltersWorkoutListAdapter(workoutCheckList,getContext());
        mFilterWorkoutRecyclerView.setAdapter(workoutCheckedListAdapter);


    }
}