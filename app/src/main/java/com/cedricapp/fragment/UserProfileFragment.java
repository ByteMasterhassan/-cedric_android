
package com.cedricapp.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.cedricapp.R;
import com.cedricapp.activity.HomeActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.cedricapp.common.SharedData;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


@SuppressWarnings("ALL")
public class UserProfileFragment extends Fragment {
    private MaterialButton mEditProfileButton;
    private ImageButton backArrow;
    ImageView mUserImage;
    private FrameLayout mConstraintLayout;
    private MaterialTextView mUsername ,mGender, mHeight, mWeight, mAge,mHeightUnit,mWeightUnit;
    private String age, height, weight, gender,userName,userImage;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    ShimmerFrameLayout shimmerForProfile;
    MaterialCardView profileCardView;


    String uid;
    private View view1;

    public UserProfileFragment() {
        // Required empty public constructor
    }
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
        return inflater.inflate(R.layout.fragment_user_profile, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1=view;
        uid = SharedData.id;





        //get id's
       init();


        SetUnityType();
        StartShimmer();

        //getting image from firebase storage
        try {
            File localFile =  File.createTempFile("profile_images", "jpg");

            storageRef.getReference().child("profile_images/"+uid)
                    .getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    mUserImage.setImageBitmap(bitmap);

                  StoptShimmer();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  StoptShimmer();
                }
            });
        } catch (IOException e) {
            StoptShimmer();
            e.printStackTrace();
        }



        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new DashboardFragment();
                //replacing the fragment
                if (fragment != null) {
                    FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.navigation_container, fragment);
                    ft.addToBackStack("DashboardFragment");
                    ft.commit();


                }

            }
        });


        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mConstraintLayout.setVisibility(View.GONE);
                Fragment fragment = new UpdateProfileFragment();
                @SuppressLint("UseRequireInsteadOfGet") FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                fragmentTransaction.replace(R.id.navigation_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    private void init() {
        mHeightUnit=view1.findViewById(R.id.textViewHeightUnit);
        mWeightUnit=view1.findViewById(R.id.textViewWeightUnit);
        mEditProfileButton = view1.findViewById(R.id.editProfileBtn);
        backArrow = view1.findViewById(R.id.backArrow);
        mConstraintLayout = view1.findViewById(R.id.fragmentContainerProfile);
        mUsername=view1.findViewById(R.id.userName);
        mUsername.setText(SharedData.username);
        mGender = view1.findViewById(R.id.userGender);
        mGender.setText(SharedData.gender);
        mHeight = view1.findViewById(R.id.userHeight);
        mHeight.setText(SharedData.height);
        mAge = view1.findViewById(R.id.userAge);
        mAge.setText(SharedData.age);
        mWeight = view1.findViewById(R.id.userWeight);
        mWeight.setText(SharedData.weight);
        mUserImage=view1.findViewById(R.id.profileUserImage);
        shimmerForProfile=view1.findViewById(R.id.shimmerForProfile);
    }

    private void SetUnityType() {
      //  Toast.makeText(requireContext(),SharedData.unitType , Toast.LENGTH_SHORT).show();
        if(SharedData.unitType.toString().trim().equalsIgnoreCase("Metric") || SharedData.unitType.toString().trim().equalsIgnoreCase("Metrisk")){
            mWeightUnit.setText("(LB)");
            mHeightUnit.setText("(CM)");
        }
    }

    private void StartShimmer(){
        shimmerForProfile.startShimmerAnimation();
        shimmerForProfile.setVisibility(View.VISIBLE);
        mUserImage.setVisibility(View.INVISIBLE);
    }

    private void StoptShimmer(){
        shimmerForProfile.stopShimmerAnimation();
        shimmerForProfile.setVisibility(View.GONE);
        mUserImage.setVisibility(View.VISIBLE);
    }
}