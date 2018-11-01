package com.huixiangshenghuo.app.activity.homepage;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.request.userInfo.MemberRecNumParamObject;
import com.doumee.model.request.userInfo.MemberRecNumRequestObject;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.MemberRecNumResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;



/**
 * Created by Administrator on 2017/5/31.
 * 推荐管理
 */

public class RecommendedManagementActivity extends BaseActivity implements View.OnClickListener {
   private RelativeLayout rl_recommended;//我的推荐人
   private TextView tv_name;//推荐人名字
   private RelativeLayout rl_regular_members;//普通会员
   private TextView tv_regular_members;//推荐普通会员人数
   private RelativeLayout rl_vipmembers;//VIP会员
   private TextView tv_vipmembers;//推荐VIP会员人数
   private RelativeLayout rl_merchants;//商户
   private TextView tv_merchants;//推荐商户人数

   private String recPeopleName = "";//推荐人姓名
   private String recPeoplePhone;//推荐人电话
   //  private int directRecNum;//直推人数

   private int recVipNum = 0;//推荐VIP人数
   private int recCommNum = 0;//推荐普通用户人数
   private int recShopNum = 0;//推荐商家数量

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_recommended_management);
      initview();
      RecNum();
      loadUser();
   }

   private void initview() {
      initTitleBar_1();
      titleView.setText("推荐管理");
      rl_recommended = (RelativeLayout) findViewById(R.id.rl_recommended);
      tv_name = (TextView) findViewById(R.id.tv_name_recommended_management);
      rl_regular_members = (RelativeLayout) findViewById(R.id.rl_regular_members);
      tv_regular_members = (TextView) findViewById(R.id.tv_regular_members);
      rl_vipmembers = (RelativeLayout) findViewById(R.id.rl_vipmembers);
      tv_vipmembers = (TextView) findViewById(R.id.tv_vipmembers);
      rl_merchants = (RelativeLayout) findViewById(R.id.rl_merchants);
      tv_merchants = (TextView) findViewById(R.id.tv_merchants);
      rl_recommended.setOnClickListener(this);
      rl_regular_members.setOnClickListener(this);
      rl_vipmembers.setOnClickListener(this);
      rl_merchants.setOnClickListener(this);

   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.rl_recommended:
            if (recPeopleName.equals("")) {
               ToastView.show("暂无推荐人");
            } else {
               RecommendPersonActivity.startRecommendPersonActivity(RecommendedManagementActivity.this, recPeopleName, recPeoplePhone);
            }

            break;
         case R.id.rl_regular_members:
            if (recCommNum <= 0) {
               ToastView.show("暂无推荐普通会员");
            } else {
               RegularMembersActivity.startRegularMembersActivity(RecommendedManagementActivity.this, RegularMembersActivity.ZERO);
            }

            break;
         case R.id.rl_vipmembers:
            if (recVipNum <= 0) {
               ToastView.show("暂无推荐VIP会员");
            } else {
               RegularMembersActivity.startRegularMembersActivity(RecommendedManagementActivity.this, RegularMembersActivity.ONE);
            }

            break;
         case R.id.rl_merchants:
            if (recShopNum <= 0) {
               ToastView.show("暂无推荐商户");
            } else {
               RegularMembersActivity.startRegularMembersActivity(RecommendedManagementActivity.this, RegularMembersActivity.TWO);
            }

            break;

      }
   }

   /**
    * 查询推荐人 姓名 直推的人数
    */
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
            recPeopleName = o.getMember().getRecPeopleName();
            recPeoplePhone = o.getMember().getRecPeoplePhone();
//            directRecNum = o.getMember().getDirectRecNum();
            if (recPeopleName.equals("")) {
               tv_name.setText("暂无推荐人");
            } else {
               tv_name.setText(recPeopleName);
            }

         }

         @Override
         public void onError(MemberInfoResponseObject o) {

            ToastView.show(o.getErrorMsg());
         }
      });
   }

   /**
    * 直推的人数
    */
   private void RecNum() {

      UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
      if (null == userInfoResponseParam) {
         userInfoResponseParam = new UserInfoResponseParam();
         userInfoResponseParam.setMemberId("");
      }
      MemberRecNumParamObject memberRecNumParamObject = new MemberRecNumParamObject();
      memberRecNumParamObject.setMemberId(userInfoResponseParam.getMemberId());
      MemberRecNumRequestObject memberRecNumRequestObject = new MemberRecNumRequestObject();
      memberRecNumRequestObject.setParam(memberRecNumParamObject);

      httpTool.post(memberRecNumRequestObject, URLConfig.REC_PEOPLE_NUM, new HttpTool.HttpCallBack<MemberRecNumResponseObject>() {
         @Override
         public void onSuccess(MemberRecNumResponseObject o) {
            recVipNum = o.getMember().getRecVipNum();
            recCommNum = o.getMember().getRecCommNum();
            recShopNum = o.getMember().getRecShopNum();
            tv_vipmembers.setText(recVipNum + "");
            tv_regular_members.setText(recCommNum + "");
            tv_merchants.setText(recShopNum + "");
         }

         @Override
         public void onError(MemberRecNumResponseObject o) {

            ToastView.show(o.getErrorMsg());
         }
      });
   }
}
