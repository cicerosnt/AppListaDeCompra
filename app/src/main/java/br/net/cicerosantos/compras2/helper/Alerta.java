package br.net.cicerosantos.compras2.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Alerta {

    public static ProgressDialog progressDialog;

    public static void getProgesso(String msg, Activity activity){
        progressDialog = ProgressDialog.show(activity, "", msg);
    }

    public static void getToast(String msg, Activity activity){
        Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void getHideKeyBoard(Context context, View editText){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }
}
