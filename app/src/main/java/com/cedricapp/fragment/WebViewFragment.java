package com.cedricapp.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.cedricapp.activity.HomeActivity;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.R;
import com.google.android.material.textview.MaterialTextView;


public class WebViewFragment extends Fragment {


    String url;
    String title;
    Resources resources;

    MaterialTextView screenTitle;

    ImageButton backArrow;

    Boolean backPress = true;

    @Override
    public void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = false;
        HomeActivity.hideBottomNav();
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }

    public WebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (backPress) {
                    onBackPress();
                } else {
                    //Toast.makeText(getContext(), "wait little while", Toast.LENGTH_SHORT).show();
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    private void onBackPress() {

        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
            if (getFragmentManager().getBackStackEntryCount() != 0) {
                if (isAdded()) {
                    getFragmentManager().popBackStack();
                }
            }
        } else {
            Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        //resources = Localization.setLanguage(getContext(),getResources());
        resources = getResources();
        assert getArguments() != null;
        url = getArguments().getString("Url");
        title = getArguments().getString("title");
        WebView webView = (WebView) view.findViewById(R.id.webview);
        screenTitle = view.findViewById(R.id.screenTitle);
        backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();

            }
        });
        screenTitle.setText(title);
        if (ConnectionDetector.isConnectedWithInternet(getActivity())) {
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(url);
        } else {
            Toast.makeText(getContext(), resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
        return view;



    }
}