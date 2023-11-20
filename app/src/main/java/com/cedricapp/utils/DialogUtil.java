package com.cedricapp.utils;

import static com.cedricapp.common.Common.IOS_CHANGE_SUBSCRIPTION_URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.cedricapp.common.Common;
import com.cedricapp.fragment.NewSelectedSubscriptionFragment;
import com.cedricapp.R;
import com.cedricapp.fragment.WebViewFragment;
import com.google.android.material.textview.MaterialTextView;

public class DialogUtil {

    public static void showSettingsAlert(Context context) {
        try {
            Resources resources = Localization.setLanguage(context,context.getResources());

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);


            alertDialog.setTitle(resources.getString(R.string.gps_not_enable));

            alertDialog.setMessage(resources.getString(R.string.do_u_want_to_turn_on_gps));


            alertDialog.setPositiveButton(resources.getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });


            alertDialog.setNegativeButton(resources.getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });


            alertDialog.show();
        }catch (Exception ex){
            if(Common.isLoggingEnabled){
                ex.printStackTrace();
            }
        }
    }

    public static void showSubscriptionEndDialogBox(Context context, Resources resources) {
        MaterialTextView btn_Cancel, btn_Continue;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog_box_for_subscription);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));

        MaterialTextView dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_title.setText(resources.getString(R.string.your_subscription_is_ended));

        MaterialTextView dialog_description = dialog.findViewById(R.id.dialog_description);
        dialog_description.setText(resources.getString(R.string.do_you_want_to_resubscribe));


        btn_Cancel = dialog.findViewById(R.id.btn_left);
        btn_Cancel.setText(resources.getString(R.string.btn_no));
        btn_Continue = dialog.findViewById(R.id.btn_right);
        btn_Continue.setText(resources.getString(R.string.btn_yes));


        btn_Cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btn_Cancel.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_background_dialog_left_click));
                LogoutUtil.redirectToLogin(context);

                dialog.dismiss();
            }
            // dialog.dismiss();
        });

        btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_Continue.setBackgroundColor(R.drawable.btn_background_dialog_right_click);
                btn_Continue.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_background_dialog_right_click));
                /*  if (isAdded()) {*/
                if (context != null) {
                    if (SessionUtil.getSignedUpPlatform(context).matches("ios")) {
                        Fragment fragment = new WebViewFragment();
                        FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("Url", IOS_CHANGE_SUBSCRIPTION_URL); //key and value
                        bundle.putString("title", resources.getString(R.string.subscription));
                        fragment.setArguments(bundle);
                        ft.replace(R.id.navigation_container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    } else {
                        Fragment fragment = new NewSelectedSubscriptionFragment();
                        FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.navigation_container, fragment);
                        //changes
//
                        ft.addToBackStack("SubscriptionFragment");
                        ft.commit();
                    }
                }
                //}
                dialog.dismiss();
            }


        });
        dialog.show();
    }

    public static void showUserBlockDialog(Context context, Resources resources) {
        MaterialTextView btn_Cancel, btn_Continue, btn_Ok;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_blocked_dialog_box);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MaterialTextView alertMTV = dialog.findViewById(R.id.alertMTV);
        alertMTV.setText(resources.getString(R.string.alert_dialog_text));

        MaterialTextView dialog_title = dialog.findViewById(R.id.dialog_title);
        dialog_title.setText(resources.getString(R.string.you_are_not_authorized_to_use_the_application));


        // requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_Ok = dialog.findViewById(R.id.btn_Ok);
        btn_Ok.setText(resources.getString(R.string.ok_btn));
        //btn_Continue = dialog.findViewById(R.id.btn_right);
        TextView textView = dialog.findViewById(R.id.dialog_blocked_description);
        textView.setText(resources.getString(R.string.admin_email));
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        btn_Ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //btn_Cancel.setBackgroundColor(R.drawable.btn_background_dialog_left_click);

                btn_Ok.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_background_dialog_blocked));

                LogoutUtil.redirectToLogin(context);

                dialog.dismiss();
            }
            // dialog.dismiss();
        });

        dialog.show();

    }
}
