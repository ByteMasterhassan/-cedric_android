package com.cedricapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cedricapp.common.SharedData;
import com.cedricapp.R;
import com.cedricapp.activity.HomeActivity;
import com.google.android.material.button.MaterialButton;

public class WebViewPrivacyFragment extends Fragment {


    private String url;
    private MaterialButton mAcceptButton;
    public final static String SHARED_PREF_NAME = "log_user_info";

    public WebViewPrivacyFragment() {
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
        View view = inflater.inflate(R.layout.fragment_web_view_privacy, container, false);

        assert getArguments() != null;
        url = getArguments().getString("Url");
        SharedData.id = getArguments().getString("id");
        SharedData.email = getArguments().getString("email");
        Log.d("email", SharedData.email);
        Log.d("email", SharedData.id);
        WebView webView = (WebView) view.findViewById(R.id.webview);
        mAcceptButton = view.findViewById(R.id.acceptButton);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCredentials();
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
            }

        });

    }


    private void saveCredentials() {
        SharedPreferences sharedPref = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", SharedData.email);
        editor.putString("id", SharedData.id);
        editor.apply();
    }

}