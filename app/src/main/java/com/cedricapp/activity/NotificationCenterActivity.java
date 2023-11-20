package com.cedricapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedricapp.adapters.NotificationCenterAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.interfaces.NotificationItemClickListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.NotificationModel;
import com.cedricapp.R;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class NotificationCenterActivity extends AppCompatActivity implements NotificationItemClickListener {

    RecyclerView recyclerView;
    TextView clearTV, readAllTV, notificationTitleTV, noNotificationTitle,noNotificationDetailTV;
    MaterialToolbar toolbar;
    ImageButton backImgBtn;

    DBHelper dbHelper;

    NotificationCenterAdapter notificationCenterActivity;

    LinearLayout notificationLL, actionLL;

    String TAG = "NOTIFICATION_TAG";

    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);
        init();
    }

    void init() {
        //resources = Localization.setLanguage(getApplicationContext(),getResources());
        resources = getResources();
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.white));

        recyclerView = findViewById(R.id.notificationCenterRV);

        notificationLL = findViewById(R.id.notificationLL);
        actionLL = findViewById(R.id.actionLL);


        toolbar = findViewById(R.id.materialToolbar);
        backImgBtn = toolbar.findViewById(R.id.backBtn);
        notificationTitleTV = toolbar.findViewById(R.id.notificationTitleTV);

        noNotificationTitle = findViewById(R.id.noNotificationTitle);
        noNotificationDetailTV = findViewById(R.id.noNotificationDetailTV);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        dbHelper = new DBHelper(getApplicationContext());

        setNotificationList();
        initClearAllNotifications();
        initReadAllNotification();
        setLanguageToWidgets();

        backImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void setLanguageToWidgets(){
        notificationTitleTV.setText(resources.getString(R.string.notifications));
        readAllTV.setText(resources.getString(R.string.read_all));
        clearTV.setText(resources.getString(R.string.clear_all));
        noNotificationTitle.setText(resources.getString(R.string.no_notification_yet));
        noNotificationDetailTV.setText(resources.getString(R.string.check_this_section_for_updates_news_and_general_notifications));
    }

    @SuppressLint("NotifyDataSetChanged")
    void setNotificationList(){
        ArrayList<NotificationModel> notifications = dbHelper.getNotifications(SessionUtil.getUserID(getApplicationContext()));
        if(Common.isLoggingEnabled){
            Log.d(TAG,"Notification data retrieved from DB: "+notifications.toString());
        }
        notificationCenterActivity = new NotificationCenterAdapter(getApplicationContext(), notifications, this);
        recyclerView.setAdapter(notificationCenterActivity);
        notificationCenterActivity.notifyDataSetChanged();
        if(notifications.size()>0){
            notificationLL.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            actionLL.setVisibility(View.VISIBLE);
        }else{
            notificationLL.setVisibility(View.VISIBLE);
            actionLL.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }



    void initClearAllNotifications() {
        clearTV = findViewById(R.id.clearAllTV);
        clearTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dbHelper.deleteAllNotifications(SessionUtil.getUserID(getApplicationContext()));
                setNotificationList();
            }
        });
    }

    void initReadAllNotification() {
        readAllTV = findViewById(R.id.readAllTV);
        readAllTV.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                dbHelper.readAllNotifications(SessionUtil.getUserID(getApplicationContext()));
                setNotificationList();
            }
        });
    }

    @Override
    public void onNotificationSelection(int notificationID) {
        dbHelper.readNotificationByID(SessionUtil.getUserID(getApplicationContext()),notificationID);
        setNotificationList();
    }
}