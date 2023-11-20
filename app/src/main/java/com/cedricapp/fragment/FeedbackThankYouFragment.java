package com.cedricapp.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cedricapp.R;
import com.cedricapp.activity.HomeActivity;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackThankYouFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("ALL")
public class FeedbackThankYouFragment extends Fragment {


    MaterialButton backToDashboardBtn;
    Resources resources;
    TextView txt_thankyou,txt_wewillcontact;


    public FeedbackThankYouFragment() {
        // Required empty public constructor
    }


    public static FeedbackThankYouFragment newInstance(String param1, String param2) {
        FeedbackThankYouFragment fragment = new FeedbackThankYouFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback_thank_you, container, false);
        //resources = Localization.setLanguage(getContext(),getResources());
        resources = getResources();
        init(view);
        return view;

    }

    void init(View view) {
        backToDashboardBtn = view.findViewById(R.id.redirectBtn);
        txt_thankyou= view.findViewById(R.id.txt_thankyou);
        txt_wewillcontact=view.findViewById(R.id.txt_wewillcontactyou);

        setlanguageToWidget();

        backToDashboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToDashboard();
            }
        });
    }

    private void setlanguageToWidget() {
        backToDashboardBtn.setText(resources.getString(R.string.take_me_to_dashboard));
        txt_thankyou.setText(resources.getString(R.string.thank_you));
        txt_wewillcontact.setText(resources.getString(R.string.we_will_contact_you_back_within_n48_hours));
    }

    void redirectToDashboard() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        /*if (getFragmentManager() != null *//*&& getFragmentManager().isStateSaved()*//*) {
            if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            }
        }*/
        startActivity(intent);

    }
}