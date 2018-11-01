package com.huixiangshenghuo.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.view.Dialog;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.appVersion.AppVersionRequestObject;
import com.doumee.model.response.appversion.AppVersionObject;
import com.doumee.model.response.appversion.AppVersionResponseObject;


/**
 * Created by Administrator on 2016/8/18.
 */
public class MyUpdateDialog {


   //   private static HttpTool httpTool;
   public static void checkVersion(final Context context, final int type) {
      HttpTool httpTool;
      httpTool = HttpTool.newInstance((Activity) context);
      AppVersionRequestObject object = new AppVersionRequestObject();

      httpTool.post(object, URLConfig.APPVERSION, new HttpTool
            .HttpCallBack<AppVersionResponseObject>() {
         @Override
         public void onSuccess(final AppVersionResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  final AppVersionObject data = o.getData();
                  if (data.getIsNeedUpdate().equals("0")) {
                     if (type == 1) {
                        //      PharmacyApplication.getInstance().showToast("目前是最新版本");
                        Toast.makeText(context, "目前是最新版本", Toast.LENGTH_LONG).show();
                     }

                  } else {//提示升级
//                     Dialog dialog = new Dialog(context);
//                     dialog.setTitle("检测到新版本");
//                     dialog.setMessage(o.getData().getInfo());
//                     dialog.setConfirmClick(new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                           if (!isWifiConnected(context)) {
//                              showIsWifiDialog(context, o.getData().getUpdateUrl());
//                           } else {
//                              Intent intent = new Intent(context.getApplicationContext
//                                    (), ApkDownloadService.class);
//                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                              intent.putExtra(Constants.APK_DOWNLOAD_URL, o.getData()
//                                    .getUpdateUrl());
//                              context.startService(intent);
//                           }
//                           dialog.dismiss();
//                        }
//                     });
//                     dialog.show();


                     //判断是否是强制升级  升级类型：0推荐升级1强制升级
                     if (data.getType().equals("0")) {
                        Dialog dialog = new Dialog(context);
                        dialog.setTitle("检测到新版本");
                        dialog.setMessage(o.getData().getInfo());
                        dialog.setConfirmClick(new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                              if (!isWifiConnected(context)) {
                                 showIsWifiDialog(context, o.getData().getUpdateUrl());
                              } else {
                                 Intent intent = new Intent(context.getApplicationContext
                                       (), ApkDownloadService.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                 intent.putExtra(Constants.APK_DOWNLOAD_URL, o.getData()
                                       .getUpdateUrl());
                                 context.startService(intent);
                              }
                              dialog.dismiss();
                           }
                        });
                        dialog.show();
                     } else {

                        Dialog dialog = new Dialog(context);
                        dialog.setTitle("检测到新版本");
                        dialog.setMessage(o.getData().getInfo());
                        dialog.setSingleText("确定");
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        dialog.setSingleClick(new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                              dialog.dismiss();
                              Intent intent = new Intent(context.getApplicationContext(), ApkDownloadService
                                    .class);
                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                              intent.putExtra(Constants.APK_DOWNLOAD_URL, o.getData()
                                    .getUpdateUrl());
                              context.startService(intent);

                           }
                        });
                        dialog.show();
//                                if (!MainActivity.this.isFinishing()) {
//                                    dialog.show();
//                                }


                     }

                  }

               }
            }

         }

         @Override
         public void onError(AppVersionResponseObject o) {

            ToastView.show(o.getErrorMsg());
         }
      });

   }

   private static void showIsWifiDialog(final Context context, final String url) {
      Dialog dialog = new Dialog(context);
      dialog.setTitle("温馨提示");
      dialog.setMessage("当前网络为非WIFI，是否要进行版本升级？");
      dialog.setCancelClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
         }
      });
      dialog.setConfirmText("升级");
      dialog.setConfirmClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            Intent intent = new Intent(context.getApplicationContext(), ApkDownloadService
                  .class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.APK_DOWNLOAD_URL, url);
            context.startService(intent);
         }
      });
      dialog.show();

   }

   //是否连接WIFI
   public static boolean isWifiConnected(Context context) {
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      if (wifiNetworkInfo.isConnected()) {
         return true;
      }
      return false;
   }

}
