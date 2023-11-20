package com.cedricapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cedricapp.R;
import com.google.android.material.textview.MaterialTextView;


@SuppressWarnings("ALL")
public class ShoppingListFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private MaterialTextView mNutritionName;

    private ImageView nutritionImage;
    String nutritionName;
    private ImageButton backArrow;
    private CheckBox mCheckbox1, mCheckbox2, mCheckbox3, mCheckbox4, mCheckbox5, mCheckbox6,
            mCheckbox7;
    private String mNutritionImageShopping;
    private View view1;


    public ShoppingListFragment() {
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
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1=view;

        //initialze id and get data
        //init();

      /*  mCheckbox1.setOnCheckedChangeListener(this);
        mCheckbox2.setOnCheckedChangeListener(this);
        mCheckbox3.setOnCheckedChangeListener(this);
        mCheckbox4.setOnCheckedChangeListener(this);
        mCheckbox5.setOnCheckedChangeListener(this);
        mCheckbox6.setOnCheckedChangeListener(this);
        mCheckbox7.setOnCheckedChangeListener(this);*/

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();

                }

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    /*private void init() {
        mNutritionName = view1.findViewById(R.id.textViewNutritionName);
        nutritionImage = view1.findViewById(R.id.nutritionImage);
        nutritionName = getArguments().getString("Nutrition");


        backArrow = view1.findViewById(R.id.backArrow);
        mCheckbox1 = view1.findViewById(R.id.checkboxLoremIpsum);
        mCheckbox2 = view1.findViewById(R.id.checkbox02);
        mCheckbox3 = view1.findViewById(R.id.checkbox03);
        mCheckbox4 = view1.findViewById(R.id.checkbox04);
        mCheckbox5 = view1.findViewById(R.id.checkbox05);
        mCheckbox6 = view1.findViewById(R.id.checkbox06);
        mCheckbox7 = view1.findViewById(R.id.checkbox07);

        try {
            mNutritionImageShopping = getArguments().getString("nutritionImage");
            // Glide.with(getContext()).asBitmap().load(mNutritionImageShopping).into(nutritionImage);
            //Picasso.get().load(mNutritionImageShopping).into(nutritionImage);

        } catch (Exception e) {

            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            System.out.println("not working...");

        }
    }*/


    /*@SuppressLint({"NonConstantResourceId", "ResourceType"})
    @Override*/
    /*public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        boolean checked = ((CheckBox) buttonView).isChecked();
        String str = "";
        // Check which checkbox was clicked
        switch (buttonView.getId()) {
            case R.id.checkboxLoremIpsum:
                if (isChecked) {
                    mCheckbox1.setPaintFlags(mCheckbox1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    mCheckbox1.setPaintFlags(mCheckbox1.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                }
                break;
            case R.id.checkbox02:
                if (isChecked)
                    mCheckbox2.setPaintFlags(mCheckbox2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    mCheckbox2.setPaintFlags(mCheckbox2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                break;
            case R.id.checkbox03:
                if (isChecked)
                    mCheckbox3.setPaintFlags(mCheckbox3.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    mCheckbox3.setPaintFlags(mCheckbox3.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                break;
            case R.id.checkbox04:
                if (isChecked)
                    mCheckbox4.setPaintFlags(mCheckbox4.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    mCheckbox4.setPaintFlags(mCheckbox4.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                break;
            case R.id.checkbox05:
                if (isChecked)
                    mCheckbox5.setPaintFlags(mCheckbox5.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    mCheckbox5.setPaintFlags(mCheckbox5.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                break;
            case R.id.checkbox06:
                if (isChecked)
                    mCheckbox6.setPaintFlags(mCheckbox6.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    mCheckbox6.setPaintFlags(mCheckbox6.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                break;
            case R.id.checkbox07:
                if (isChecked)
                    mCheckbox7.setPaintFlags(mCheckbox7.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    mCheckbox7.setPaintFlags(mCheckbox7.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                break;


        }

    }*/
}
