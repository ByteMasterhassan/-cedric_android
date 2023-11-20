package com.cedricapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import com.cedricapp.R;

public class AutoDismissalDialogUtil {
    public static void showDialog(Context context, String dialogText){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.simple_dialog_without_button);

        TextView textView = dialog.findViewById(R.id.dialogTextView);
        textView.setText(dialogText);
        dialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },3000);

    }
}
