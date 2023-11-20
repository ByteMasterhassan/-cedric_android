package com.cedricapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.cedricapp.interfaces.DialogInterface;
import com.cedricapp.R;
import com.google.android.material.textview.MaterialTextView;

public class CustomDialogUtil {
    public static void showDialog(Context context, String title, String description, DialogInterface dialogInterface){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_box);

        MaterialTextView titleTV = dialog.findViewById(R.id.dialog_title);
        MaterialTextView descriptionTV = dialog.findViewById(R.id.dialog_description);
        MaterialTextView noBtn = dialog.findViewById(R.id.noBtn);
        MaterialTextView yesBtn = dialog.findViewById(R.id.yesBtn);

        titleTV.setText(title);
        descriptionTV.setText(description);

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogInterface.onClickedNo();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
               dialogInterface.onClickedYes();
            }
        });
        dialog.show();
    }
}
