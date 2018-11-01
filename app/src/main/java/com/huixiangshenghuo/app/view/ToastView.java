package com.huixiangshenghuo.app.view;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.R;

/**
 * Created by lenovo on 2016/12/9.
 */
public class ToastView {

    public static void show(String msg){
        Toast toast = new Toast(CustomApplication.getCustomApplication());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        View view = View.inflate(CustomApplication.getCustomApplication(), R.layout.toast_view,null);
        TextView textView = (TextView)view.findViewById(R.id.message) ;
        textView.setText(msg);
        toast.setView(view);
        toast.show();
    }
}
