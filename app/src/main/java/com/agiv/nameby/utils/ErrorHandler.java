package com.agiv.nameby.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.LauncherApps;

import com.agiv.nameby.R;

/**
 * Created by Noa Agiv on 9/29/2017.
 */

public class ErrorHandler {
    public static abstract class DismissAction {
        public void run(){}
    }

    public static void showErrorAlert(String error, Context context, final DismissAction dismissAction){
        new AlertDialog.Builder(context)
                .setTitle(R.string.error_dialog_title)
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (dismissAction!=null)
                            dismissAction.run();


                    }
                })
                .show();
    }
    public static void showErrorAlert(int error, Context context, final DismissAction dismissAction){
        new AlertDialog.Builder(context)
                .setTitle(R.string.error_dialog_title)
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (dismissAction!=null)
                            dismissAction.run();

                    }
                })
                .show();
    }

    public static void showErrorAlert(int error, Context context){
        showErrorAlert(error, context, null);
    }

    public static void showErrorAlert(String  error, Context context){
        showErrorAlert(error, context, null);
    }
}
