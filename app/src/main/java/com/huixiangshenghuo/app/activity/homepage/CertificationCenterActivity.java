package com.huixiangshenghuo.app.activity.homepage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.mine.MineRealActivity;
import com.huixiangshenghuo.app.ui.mine.ShopCenterCenterActivity;
import com.huixiangshenghuo.app.ui.mine.ShopCenterPaidSubmitActivity;
import com.huixiangshenghuo.app.ui.mine.ShopCenterSubmitActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;

/**
 * Created by Administrator on 2017/5/27.
 * 首页 二级页面认证中心
 */

public class CertificationCenterActivity extends BaseActivity implements View.OnClickListener {
   public static String VIPTYPE = "viptype";
   private TextView tv_shiming;
   private TextView tv_shanghu;
   private String viptype;

   public static void startCertificationCenterActivity(Context context, String viptype) {
      Intent intent = new Intent(context, CertificationCenterActivity.class);
      intent.putExtra(VIPTYPE, viptype);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_certification_center);
      viptype = getIntent().getStringExtra(VIPTYPE);
      initview();
   }

   private void initview() {
      initTitleBar_1();
      titleView.setText("认证中心");
      tv_shiming = (TextView) findViewById(R.id.tv_shiming_certification_center);
      tv_shanghu = (TextView) findViewById(R.id.tv_shanghu_certification_center);


      if (viptype.equals("0")) {//会员类型 0普通会员 1VIP会员
         tv_shanghu.setVisibility(View.GONE);
      } else {
         tv_shanghu.setVisibility(View.VISIBLE);
      }

      tv_shiming.setOnClickListener(this);
      tv_shanghu.setOnClickListener(this);
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.tv_shiming_certification_center:
            MineRealActivity.startMineRealActivity(this);
            break;
         case R.id.tv_shanghu_certification_center:
            loadUser();
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

            try {
//               if (TextUtils.equals("0", o.getMember().getIsShop())) {
//                  ShopCenterSubmitActivity.startShopCenterSubmitActivity(CertificationCenterActivity.this, ShopCenterSubmitActivity.FLAG_ADD);
//               } else {
//                  if (TextUtils.equals("0", o.getMember().getShopStatus())) {
//                     //       ShopCenterActivity.startShopCenterActivity(getActivity());
//                     startActivity(new Intent(CertificationCenterActivity.this, ShopCenterCenterActivity.class));
//                  } else {
//                     ToastView.show("商家已经禁用，请联系客服.");
//                  }
//               }
               if (TextUtils.equals("0", o.getMember().getIsShop())) {//是否是商家 0不是 1是 2申请中未付款 3申请中已付款 4 已申请（免费）
                  //                ShopCenterSubmitActivity.startShopCenterSubmitActivity(getActivity(),ShopCenterSubmitActivity.FLAG_ADD);
//                     ShopCenterSubmitNewActivity.startShopCenterSubmitNewActivity(getActivity(),ShopCenterSubmitActivity.FLAG_ADD);
                  ShopCenterSubmitActivity.startShopCenterSubmitActivity(CertificationCenterActivity.this, ShopCenterSubmitActivity.FLAG_ADD);
               } else if (TextUtils.equals("1", o.getMember().getIsShop())) {
                  if (TextUtils.equals("0", o.getMember().getShopStatus())) {
                     //       ShopCenterActivity.startShopCenterActivity(getActivity());
                     startActivity(new Intent(CertificationCenterActivity.this, ShopCenterCenterActivity.class));
                  } else {
                     ToastView.show("商家已经禁用，请联系客服.");
                  }
               } else if (TextUtils.equals("2", o.getMember().getIsShop())) {
                  ShopCenterSubmitActivity.startShopCenterSubmitActivity(CertificationCenterActivity.this, ShopCenterSubmitActivity.FLAG_ADD);
               } else if (TextUtils.equals("3", o.getMember().getIsShop())) {
                  ShopCenterPaidSubmitActivity.startShopCenterPaidSubmitActivity(CertificationCenterActivity.this, ShopCenterSubmitActivity.FLAG_ADD);
               } else if (TextUtils.equals("4", o.getMember().getIsShop())) {
                  ShopCenterPaidSubmitActivity.startShopCenterPaidSubmitActivity(CertificationCenterActivity.this, ShopCenterSubmitActivity.FLAG_ADD);
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }

         @Override
         public void onError(MemberInfoResponseObject o) {

            ToastView.show(o.getErrorMsg());
         }
      });
   }
}
