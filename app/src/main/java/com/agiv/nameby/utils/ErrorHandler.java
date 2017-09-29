package com.agiv.nameby.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.agiv.nameby.R;

/**
 * Created by Noa Agiv on 9/29/2017.
 */

public class ErrorHandler {
    public static void showErrorAlert(String error, Context context){
        new AlertDialog.Builder(context)
                .setTitle(R.string.error_dialog_title)
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .show();
    }
    public static void showErrorAlert(int error, Context context){
        new AlertDialog.Builder(context)
                .setTitle(R.string.error_dialog_title)
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .show();
    }
}
