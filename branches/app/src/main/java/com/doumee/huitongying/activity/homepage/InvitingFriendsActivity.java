package com.huixiangshenghuo.app.activity.homepage;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.bitmap.CuttingBitmap;
import com.huixiangshenghuo.app.comm.bitmap.QRCodeTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.NETWorkUtils;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.huixiangshenghuo.app.R.id.action_image;

/**
 * Created by Administrator on 2017/5/27.
 * 邀请好友
 */

public class InvitingFriendsActivity extends BaseActivity implements View.OnClickListener {
   private Bitmap defaultBitmap;
   private Bitmap defaultBitmap2;
   private ImageView codeView;
   private TextView tv_shareid;
   private String filePath;
   private String app_share = "";//分享内容
   private String share_link = "";//分享链接
   private TextView tv_share_link;
   private Button bt_copy_link;
   private String recommentContent;//推荐分享链接


   public static void InvitingFriendsActivity(Context context) {
      Intent intent = new Intent(context, InvitingFriendsActivity.class);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_inviting_friends);
//      ShareSDK.initSDK(this);
      //    ShareSDK.initSDK(this,"androidv1101");
      defaultBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.header_img_bg);
      defaultBitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
//      filePath = CustomConfig.USER_SHARE_IMAGE;
      filepath();
      saveMyBitmap();
      initView();
      loadDataIndex();
      loadUser();
   }

   private void filepath() {
      String path = CustomConfig.IMAGE_PATH;
      File f = new File(path);
      if (!f.exists()) {  //如果文件夹不存在，创建文件夹
         f.mkdirs();
      }
      filePath = CustomConfig.USER_SHARE_IMAGE;
   }

   private void initView() {
      initTitleBar_1();
      titleView.setText("我的二维码");
      actionImageButton.setImageResource(R.mipmap.share_icon);
      actionImageButton.setVisibility(View.VISIBLE);
      codeView = (ImageView) findViewById(R.id.code_image);
      tv_share_link = (TextView) findViewById(R.id.tv_share_link_inviting_friends);
      bt_copy_link = (Button) findViewById(R.id.bt_copy_link_inviting_friends);
      tv_shareid = (TextView) findViewById(R.id.tv_shareid);
      bt_copy_link.setOnClickListener(this);
      actionImageButton.setOnClickListener(this);

   }

   private void share() {

   }

   @Override
   protected void onStart() {
      super.onStart();
   }

   private void createCode() {
      UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
//      File file = new File(filePath);
//      if (file.exists()) {
//         codeView.setImageBitmap(BitmapFactory.decodeFile(filePath));
//         return;
//      }
      String face = userInfo.getImgUrl();
      if (!TextUtils.isEmpty(face)) {
         ImageLoader.getInstance().loadImage(face, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
               super.onLoadingComplete(imageUri, view, loadedImage);
               defaultBitmap = CuttingBitmap.toRoundBitmap(loadedImage);
               createImage();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
               super.onLoadingFailed(imageUri, view, failReason);
               createImage();
            }
         });
      } else {
         createImage();
      }
   }

   private void createImage() {
      new Thread(new Runnable() {
         @Override
         public void run() {
//            boolean success = QRCodeTool.createQRImage(share_link, 800, 800, defaultBitmap, filePath);
            boolean success = QRCodeTool.createQRImage(recommentContent, 800, 800, defaultBitmap, filePath);
            if (success) {
               runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                     codeView.setImageBitmap(BitmapFactory.decodeFile(filePath));
                  }
               });
            }
         }
      }).start();
   }

   private void showShare() {
//      ShareSDK.initSDK(this);
      OnekeyShare oks = new OnekeyShare();
      //关闭sso授权
      oks.disableSSOWhenAuthorize();


      // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
      oks.setTitle(getString(R.string.app_name));
      // titleUrl是标题的网络链接，QQ和QQ空间等使用
      oks.setTitleUrl(share_link);
      // text是分享文本，所有平台都需要这个字段
      oks.setText(app_share);
      // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
      //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
      try {
         oks.setImagePath(CustomConfig.SHARE_IMAGE_PATH + "business.png");//确保SDcard下面存在此张图片
      } catch (Exception e) {
         e.printStackTrace();
      }

      // url仅在微信（包括好友和朋友圈）中使用
      oks.setUrl(share_link);
      // comment是我对这条分享的评论，仅在人人网和QQ空间使用
      oks.setComment(app_share);
      // site是分享此内容的网站名称，仅在QQ空间使用
      oks.setSite(getString(R.string.app_name));
      // siteUrl是分享此内容的网站地址，仅在QQ空间使用
      oks.setSiteUrl(share_link);


// 启动分享GUI
      oks.show(this);
   }

   //加载数据字典
   public void loadDataIndex() {
      showProgressDialog("正在加载");
      AppDicInfoParam appDicInfoParam = new AppDicInfoParam();
      AppDicInfoRequestObject appDicInfoRequestObject = new AppDicInfoRequestObject();
      appDicInfoRequestObject.setParam(appDicInfoParam);
      httpTool.post(appDicInfoRequestObject, URLConfig.DATA_INDEX, new HttpTool.HttpCallBack<AppConfigureResponseObject>() {
         @Override
         public void onSuccess(AppConfigureResponseObject o) {
            dismissProgressDialog();
            List<AppConfigureResponseParam> dataList = o.getDataList();
            for (AppConfigureResponseParam app : dataList) {
               if (app.getName().equals(CustomConfig.RECOMMEND_CONTENT)) {
                  app_share = app.getContent();


               }
               if (app.getName().equals(CustomConfig.SHARE_LINK)) {
                  share_link = app.getContent();
                  tv_share_link.setText(share_link);
               }
            }
//            createCode();
         }

         @Override
         public void onError(AppConfigureResponseObject o) {
            dismissProgressDialog();
            ToastView.show(o.getErrorMsg());
         }
      });
   }

   /**
    * 保存方法
    */
   public void saveMyBitmap() {
      try {
         Bitmap mBitmap = defaultBitmap2;
         String path = CustomConfig.SHARE_IMAGE_PATH;
         File f = new File(path);
         if (!f.exists()) {  //如果文件夹不存在，创建文件夹
            f.mkdirs();
         }
         File file = new File(path + "business.png");
         file.createNewFile();
         //f.createNewFile();
         FileOutputStream fOut = new FileOutputStream(file);
         mBitmap.compress(Bitmap.CompressFormat.PNG, 60, fOut);
         fOut.flush();
         fOut.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case action_image:
            if (share_link.equals("") && app_share.equals("")) {
               if (NETWorkUtils.isNetworkConnected(InvitingFriendsActivity.this) == false) {
                  ToastView.show("网络不佳，请查看网络");
               } else {
                  ToastView.show("网络不佳，请查看网络");
               }
            } else {
               share();
               showShare();
            }


            break;
         case R.id.bt_copy_link_inviting_friends:

            ClipboardManager cmb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(tv_share_link.getText().toString());
            break;
      }
   }

   private void loadUser() {

      UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
      if (null == userInfoResponseParam) {
         userInfoResponseParam = new UserInfoResponseParam();
         userInfoResponseParam.setMemberId("");
      }
      MemberInfoParamObject memberInfoParamObject = new MemberInfoParamObject();
      memberInfoParamObject.setMemberId(userInfoResponseParam.getMemberId());
      MemberInfoRequestObject memberInfoRequestObject = new MemberInfoRequestObject();
      memberInfoRequestObject.setParam(memberInfoParamObject);

      httpTool.post(memberInfoRequestObject, URLConfig.USER_INFO, new HttpTool.HttpCallBack<MemberInfoResponseObject>() {
         @Override
         public void onSuccess(MemberInfoResponseObject o) {
//            recommentContent = o.getMember().getRecommendContent();
//            tv_share_link.setText(recommentContent);
            recommentContent = o.getMember().getShareLink();
            tv_shareid.setText(o.getMember().getLoginName());
            createCode();
         }

         @Override
         public void onError(MemberInfoResponseObject o) {

            ToastView.show(o.getErrorMsg());
         }
      });
   }
}
