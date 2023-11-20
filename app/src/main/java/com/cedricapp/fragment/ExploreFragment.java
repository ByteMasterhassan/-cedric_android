package com.cedricapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.adapters.ExploreBodySpecificAdapter;
import com.cedricapp.adapters.ExploreWorkoutAdapter;
import com.cedricapp.adapters.InstructorAdapter;
import com.cedricapp.model.ExploreBodySpecificDataModel;
import com.cedricapp.common.ExploreWorkoutData;
import com.cedricapp.model.ExploreWorkoutDataModel;
import com.cedricapp.model.InstructorDataModel;
import com.cedricapp.R;

import java.util.ArrayList;
import java.util.Objects;


@SuppressWarnings("ALL")
public class ExploreFragment extends Fragment {
    private static RecyclerView mRecommendedForYouRecyclerView;
    private static RecyclerView mInstructorRecyclerView;
    private static RecyclerView mPopularMealRecyclerView;
    private static RecyclerView mExploreFeatureOnDemandRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.LayoutManager layoutManager1;
    private static ArrayList<ExploreWorkoutDataModel> data;
    private static ArrayList<InstructorDataModel> instuctorDataList;
    private static ArrayList<ExploreBodySpecificDataModel> data1;
    private static RecyclerView.Adapter exploreAdapter;
    private static RecyclerView.Adapter instructorAdapter;
    private static RecyclerView.Adapter exploreBodySpecificAdapter;
    private ImageButton filterButton;
    private View view1;
    private ArrayList<ExploreBodySpecificDataModel> data2;


    public ExploreFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
//        getActivity().getWindow().setStatusBarColor(R.color.yellow);

        //set status bar color
//        if (Build.VERSION.SDK_INT >= 21) {
//            Window window = getActivity().getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(this.getResources().getColor(R.color.yellow));
//        }

        init();


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment();
            }
        });
    }

    private void popluateFeaturesOnDemandData() {

        //   Populate data for body features on demand

        data1 = new ArrayList<ExploreBodySpecificDataModel>();
        data1.add(new ExploreBodySpecificDataModel(R.drawable.ic_feature_on_demad,0,"Legs Workout","3 Mins"));
        data1.add(new ExploreBodySpecificDataModel(R.drawable.ic_feature_on_demad,0,"Fresh Breath","5 Mins"));
        data1.add(new ExploreBodySpecificDataModel(R.drawable.ic_feature_on_demad,0,"Legs Workout","2 Mins"));

        exploreBodySpecificAdapter = new ExploreBodySpecificAdapter(data1);
        mExploreFeatureOnDemandRecyclerView.setAdapter(exploreBodySpecificAdapter);

    }

    private void populateSpecifcData() {

        //   Populate for body specific cardview
        data2 = new ArrayList<ExploreBodySpecificDataModel>();
        data2.add(new ExploreBodySpecificDataModel(R.drawable.ic_popular_meals,0,"Fried Chicken","Lunch"));
        data2.add(new ExploreBodySpecificDataModel(R.drawable.ic_popular_meals,0,"Fried Chicken","Dinner"));
        data2.add(new ExploreBodySpecificDataModel(R.drawable.ic_popular_meals,0,"Fried Chicken","Lunch"));

        exploreBodySpecificAdapter = new ExploreBodySpecificAdapter(data2);
        mPopularMealRecyclerView.setAdapter(exploreBodySpecificAdapter);

    }

    private void popluateRecommendedData() {
        //   Populate data of first horizontal cardview
        data = new ArrayList<ExploreWorkoutDataModel>();
        /*for (int i = 0; i < ExploreWorkoutData.mainWorkoutArray.length; i++) {
            data.add(new ExploreWorkoutDataModel(
                    ExploreWorkoutData.mainWorkoutArray[i],
                    ExploreWorkoutData.numberOfWorkoutArray[i],
                    ExploreWorkoutData.imageMainWorkoutArray[i],
                    ExploreWorkoutData.imageWorkout1Array[i],
                    ExploreWorkoutData.imageWorkout2Array[i],
                    ExploreWorkoutData.imageWorkout3Array[i],
                    ExploreWorkoutData.workout1NameArray[i],
                    ExploreWorkoutData.workout2NameArray[i],
                    ExploreWorkoutData.workout3NameArray[i],
                    ExploreWorkoutData.id_[i]

            ));
        }*/

        exploreAdapter = new ExploreWorkoutAdapter(data);
        mRecommendedForYouRecyclerView.setAdapter(exploreAdapter);
    }

    private void loadFragment() {
        Fragment fragment = new FiltersFragment();
        //replacing the fragment

        @SuppressLint("UseRequireInsteadOfGet") FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        fragmentTransaction.replace(R.id.navigation_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void init() {
        mRecommendedForYouRecyclerView = view1.findViewById(R.id.recyclerviewExplore);
        mPopularMealRecyclerView = view1.findViewById(R.id.recyclerviewExploreBodySpecific);
        mExploreFeatureOnDemandRecyclerView = view1.findViewById(R.id.recyclerviewOnDemand);
        mInstructorRecyclerView = view1.findViewById(R.id.instructorRecyclerview);
        filterButton = view1.findViewById(R.id.filterButton);

        //   recyclerview for first horizontal cardView
        mInstructorRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mInstructorRecyclerView.setLayoutManager(layoutManager);
        populateInstructorData();

        //   recyclerview for first horizontal cardView
        mRecommendedForYouRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecommendedForYouRecyclerView.setLayoutManager(layoutManager);

        popluateRecommendedData();

        //   recyclerview for body specific cardView
        mPopularMealRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mPopularMealRecyclerView.setLayoutManager(layoutManager);

        populateSpecifcData();

        //   recyclerview for body features on demand
        mExploreFeatureOnDemandRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mExploreFeatureOnDemandRecyclerView.setLayoutManager(layoutManager);

        popluateFeaturesOnDemandData();
    }

    private void populateInstructorData() {

        instuctorDataList = new ArrayList<InstructorDataModel>();
        instuctorDataList.add(new InstructorDataModel("Dawyane Jonhnson", "30 Videos", R.drawable.jumping,
                R.drawable.camera));

        instuctorDataList.add(new InstructorDataModel("Mitchel Slater", "20 Videos", R.drawable.jumping,
                R.drawable.camera));

        instuctorDataList.add(new InstructorDataModel("Mark Hayysmen", "10 Videos", R.drawable.jumping,
                R.drawable.camera));


        instructorAdapter = new InstructorAdapter(getContext(), instuctorDataList);
        mInstructorRecyclerView.setAdapter(instructorAdapter);

    }

}