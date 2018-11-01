package com.huixiangshenghuo.app.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;

/**
 * Created by Administrator on 2017/2/8.
 * 自定义确认框
 */

public class CustomAlertDialog {
   public static void showAlertDialog(Context context, String msg, final OnLeftButtonOnClickListener leftButtonOnClickListener,
                                      final OnRightButtonOnClickListener listener) {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      View view = View.inflate(context, R.layout.custom_alert_dialog_view, null);
      TextView titleView = (TextView) view.findViewById(R.id.message);
      Button leftButton = (Button) view.findViewById(R.id.left_button);
      Button rightButton = (Button) view.findViewById(R.id.right_button);
      titleView.setText(msg);
      builder.setView(view);
      builder.setCancelable(false);
      final AlertDialog alert = builder.create();
      leftButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            alert.dismiss();
            leftButtonOnClickListener.onClick();
         }
      });
      rightButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            alert.dismiss();
            listener.onClick();
         }
      });

      alert.show();
   }

   public interface OnLeftButtonOnClickListener {
      void onClick();
   }

   public interface OnRightButtonOnClickListener {
      void onClick();
   }
}
