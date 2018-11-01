package com.huixiangshenghuo.app.ui.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.activity.homepage.InvitingFriendsActivity;
import com.huixiangshenghuo.app.activity.homepage.RecommendedManagementActivity;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.mine.AboutActivity;
import com.huixiangshenghuo.app.ui.mine.HelpTwoActivity;
import com.huixiangshenghuo.app.ui.mine.MineGoldActivity;
import com.huixiangshenghuo.app.ui.mine.MineInfoActivity;
import com.huixiangshenghuo.app.ui.mine.MineIntegralActivity;
import com.huixiangshenghuo.app.ui.mine.MineLevelUpActivity;
import com.huixiangshenghuo.app.ui.mine.MineOrderListActivity;
import com.huixiangshenghuo.app.ui.mine.MyCollectActivity;
import com.huixiangshenghuo.app.ui.mine.PublicWelfareFundActivity;
import com.huixiangshenghuo.app.ui.mine.ShopCenterCenterActivity;
import com.huixiangshenghuo.app.ui.mine.ShopCenterPaidSubmitActivity;
import com.huixiangshenghuo.app.ui.mine.ShopCenterSubmitActivity;
import com.huixiangshenghuo.app.view.Dialog;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.doumee.model.response.shop.ShopParam;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import static com.huixiangshenghuo.app.R.id.face;


/**
 * 首页我的
 */
public class HomeMineFragment extends Fragment implements View.OnClickListener,RefreshLayout.OnRefreshListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton backButton;
    private TextView titleView;

    private TextView tv_shop;//商家标签

    private RefreshLayout refreshLayout;
    private ImageView faceView;
    private RelativeLayout minInfoView;
    private TextView nameView ,tuijianView,levelView,orderView;
    private TextView tv_phone;//手机号
    private GridView gridView;
    private TextView tab0View, tab1View, tab2View, tab3View, tab4View, tab5View, tv_sc;
    private static final int GOLD = 1;
    private static final int JIFEN = 2;
    private static final int JIESUAN = 3;
    private static final int BANK_CARD = 4;

    private int tab = GOLD;

    private String mParam1;
    private String mParam2;

    private ArrayList<LabelItem> dataList;
    private CustomBaseAdapter<LabelItem> adapter;
    private String isShop,payStatus = "0",shopStatus = "0";
    protected HttpTool httpTool;
    private SharedPreferences sharedPreferences;
    /**
     * 客服电话
     */
    private String Phone;
    //会员类型
    private String vip_type;
    private View view_mine;

    public HomeMineFragment() {

    }


    public static HomeMineFragment newInstance(String param1, String param2) {
        HomeMineFragment fragment = new HomeMineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        sharedPreferences = CustomApplication.getAppUserSharedPreferencesCity();
        dataList = new ArrayList<>();
        adapter = new CustomBaseAdapter<LabelItem>(dataList,R.layout.fragment_home_mine_item) {
            @Override
            public void bindView(ViewHolder holder, LabelItem obj) {
                ImageView labelImage = holder.getView(R.id.label_icon);
                TextView labelView = holder.getView(R.id.label);
                TextView valView = holder.getView(R.id.val);
                labelView.setText(obj.label);
                switch (obj.index){
                    case GOLD:
                        double v = Double.valueOf(obj.val.toString());
                        valView.setText(CustomConfig.RMB+ NumberFormatTool.numberFormatTo4(v));
                        labelImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.my_money_icon));
                        break;
                    case JIFEN:
                        double v2 = Double.valueOf(obj.val.toString());
                        valView.setText(CustomConfig.RMB+ NumberFormatTool.numberFormatTo4(v2));
                        labelImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.my_integral_icon));
                        break;
                    case JIESUAN:
                        valView.setText(obj.val.toString());
                        labelImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.my_tj));
                        break;
                    case BANK_CARD:
                        valView.setText(obj.val.toString());
                        labelImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.gys_icon));
                        break;

                }

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        httpTool = HttpTool.newInstance(getActivity());
        loadDataIndex();
        view_mine = inflater.inflate(R.layout.fragment_home_mine, container, false);
        return view_mine;
//        return inflater.inflate(R.layout.fragment_home_mine, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        refreshLayout = (RefreshLayout)view.findViewById(R.id.refresh);
        backButton = (ImageButton)view.findViewById(R.id.back);
        titleView = (TextView)view.findViewById(R.id.title);
        backButton.setVisibility(View.GONE);
        titleView.setText("我的");

        minInfoView = (RelativeLayout)view.findViewById(R.id.info);
        faceView = (ImageView) view.findViewById(face);
        nameView = (TextView)view.findViewById(R.id.name);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tuijianView = (TextView)view.findViewById(R.id.tuijian);
        tv_shop = (TextView) view.findViewById(R.id.tv_shop_yuanjiao);
        levelView = (TextView)view.findViewById(R.id.level);
        orderView = (TextView)view.findViewById(R.id.order_list);
        gridView = (GridView)view.findViewById(R.id.grid_view);
        tab0View = (TextView)view.findViewById(R.id.tab0);
        tab1View = (TextView)view.findViewById(R.id.tab1);
        tab2View = (TextView)view.findViewById(R.id.tab2);
        tab3View = (TextView)view.findViewById(R.id.tab3);
        tab4View = (TextView)view.findViewById(R.id.tab4);
        tab5View = (TextView)view.findViewById(R.id.tab5);
        tv_sc = (TextView) view.findViewById(R.id.tv_sc);
        initView();
        loadUser();
    }

    private void initView(){
        gridView.setAdapter(adapter);
        minInfoView.setOnClickListener(this);
        orderView.setOnClickListener(this);
        tab1View.setOnClickListener(this);
        tab2View.setOnClickListener(this);
        tab3View.setOnClickListener(this);
        tab4View.setOnClickListener(this);
        tab5View.setOnClickListener(this);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(this);
        tab0View.setOnClickListener(this);
        tv_sc.setOnClickListener(this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        tab = GOLD;
                        break;
                    case 1:
                        tab =JIFEN;
                        break;
                    case 2:
                        tab = JIESUAN;
                        break;
                    case 3:
                        tab = BANK_CARD;
                        break;
                }
                tabClick();
            }
        });
    }

    @Override
    public void onRefresh() {
        loadUser();
    }

    private void loadUser(){
        refreshLayout.setRefreshing(true);
        UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
        if (null == userInfoResponseParam){
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

                refreshLayout.setRefreshing(false);
//                vip_type = o.getMember().getType();

                UserInfoResponseParam userInfo = o.getMember();
                String city = sharedPreferences.getString(CustomConfig.CITY_ID,"");
                String cityName = sharedPreferences.getString(CustomConfig.CITY_NAME,"");
                userInfo.setCityId(city);
                userInfo.setCityName(cityName);
//                ToastView.show(cityName);
                SaveObjectTool.saveObject(userInfo);
                onResume();
            }
            @Override
            public void onError(MemberInfoResponseObject o) {

                refreshLayout.setRefreshing(false);
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
        isShop = userInfo.getIsShop();
        shopStatus = userInfo.getShopStatus();
        vip_type = userInfo.getType();
        ShopParam shopParam = userInfo.getShopParam();
        if (null != shopParam){
            payStatus = shopParam.getPayStauts();
        }
        if (isShop.equals("1")) {//是否是商家 0不是 1是
            tv_shop.setVisibility(View.VISIBLE);
        } else {
            tv_shop.setVisibility(View.GONE);
        }
        if (vip_type.equals("0")) {//会员类型 0普通会员 1VIP会员
            tab2View.setVisibility(View.GONE);
        } else {
            tab2View.setVisibility(View.VISIBLE);
        }

        String face = userInfo.getImgUrl();
        String recPeopleName = userInfo.getRecPeopleName();
        String name = userInfo.getName();
        String phone = userInfo.getPhone();
        if (!TextUtils.isEmpty(face)){
            ImageLoader.getInstance().displayImage(face,faceView);
        }
        if (TextUtils.isEmpty(name)){
//            name = "未实名认证";
            nameView.setText(userInfo.getLoginName());
        } else {
            nameView.setText(name);
        }

        if (TextUtils.isEmpty(recPeopleName)){
            recPeopleName = "暂无";
        }
        if (TextUtils.isEmpty(phone)) {
            tv_phone.setText("");
        } else {
            phone = phone.substring(0, 3) + "****" + phone.substring(7, 11);
            tv_phone.setText("(" + phone + ")");
        }
        tuijianView.setText("推荐人："+recPeopleName);
        String levelStr = "普通会员";
        String type = userInfo.getType();
        if (TextUtils.equals("1",type)){
            levelStr = "VIP会员";
        }
        levelView.setText(levelStr);

        dataList.clear();
        LabelItem labelItem = new LabelItem();
        labelItem.label = "惠宝";
        labelItem.val = userInfo.getMoney();
        labelItem.id = "1";
        labelItem.index = GOLD;
        dataList.add(labelItem);

        LabelItem labelItem2 = new LabelItem();
        labelItem2.label = "积分";
        labelItem2.val = userInfo.getIntegral();
        labelItem2.id = "2";
        labelItem2.index = JIFEN;
        dataList.add(labelItem2);

        LabelItem labelItem3 = new LabelItem();
        labelItem3.label = "我的推荐";
        labelItem3.val = userInfo.getDirectRecNum() + "个";
        labelItem3.id = "3";
        labelItem3.index = JIESUAN;
        dataList.add(labelItem3);
        //银行卡
/*        String bankCode = "添加卡号";
        String bank = userInfo.getBankCode();
        if (!TextUtils.isEmpty(bank)){
            bankCode = "尾号"+bank.substring(bank.length() - 4);
        }
        LabelItem labelItem4 = new LabelItem();
        labelItem4.label = "银行卡";
        labelItem4.val =  bankCode;
        labelItem4.id = "4";
        labelItem4.index = BANK_CARD;
        dataList.add(labelItem4);*/
        //公益积金
        LabelItem labelItem4 = new LabelItem();
        labelItem4.label = "公益积金";
        labelItem4.val = userInfo.getPublicFee();
        labelItem4.id = "4";
        labelItem4.index = BANK_CARD;
        dataList.add(labelItem4);

        adapter.notifyDataSetChanged();

    }

    private void tabClick(){
        switch (tab){
            case GOLD:
                MineGoldActivity.startMineGoldActivity(getActivity());
                break;
            case JIFEN:
                MineIntegralActivity.startMineIntegralActivity(getActivity());
                break;
            case JIESUAN:
//                MineTuiJianActivity.startMineTuiJianActivity(getActivity());
                startActivity(new Intent(getActivity(), RecommendedManagementActivity.class));
                break;
            case BANK_CARD:
                //银行卡
//                MineBankCardActivity.startMineBankCardActivity(getActivity());

                PublicWelfareFundActivity.startPublicWelfareFundActivity(getActivity());

                break;
        }
    }

    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.info://个人资料
                  MineInfoActivity.startMineInfoActivity(getActivity());
                  break;
              case R.id.order_list://我的订单
                  MineOrderListActivity.startMineOrderListActivity(getActivity());
                  break;
              case R.id.tab0:
                  if (vip_type.equals("0")) {//会员类型 0普通会员 1VIP会员
                      ToastView.show("成为VIP会员才能邀请好友");
                  } else {
//                      MineCodeActivity.startMineCodeActivity(getActivity());
                      startActivity(new Intent(getActivity(), InvitingFriendsActivity.class));
                  }

                  break;
              case R.id.tab1://会员升级
                  UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
                  String type = userInfo.getType();
                  if (TextUtils.equals("1",type)){
                      ToastView.show("您已经是VIP会员");
                  }else{
                      MineLevelUpActivity.startMineLevelUpActivity(getActivity());
                  }
                  break;
              case R.id.tab2://商户中心
                  if (TextUtils.equals("0", isShop)) {//是否是商家 0不是 1是 2申请中未付款 3申请中已付款 4 已申请（免费）
                      //                ShopCenterSubmitActivity.startShopCenterSubmitActivity(getActivity(),ShopCenterSubmitActivity.FLAG_ADD);
//                     ShopCenterSubmitNewActivity.startShopCenterSubmitNewActivity(getActivity(),ShopCenterSubmitActivity.FLAG_ADD);
                     ShopCenterSubmitActivity.startShopCenterSubmitActivity(getActivity(),ShopCenterSubmitActivity.FLAG_ADD);
                  } else if (TextUtils.equals("1", isShop)) {
                     if (TextUtils.equals("0",shopStatus)){
                         //       ShopCenterActivity.startShopCenterActivity(getActivity());
                         startActivity(new Intent(getActivity(), ShopCenterCenterActivity.class));
                     }else{
                         ToastView.show("商家已经禁用，请联系客服.");
                     }
                  } else if (TextUtils.equals("2", isShop)) {
                      ShopCenterSubmitActivity.startShopCenterSubmitActivity(getActivity(), ShopCenterSubmitActivity.FLAG_ADD);
                  } else if (TextUtils.equals("3", isShop)) {
                      ShopCenterPaidSubmitActivity.startShopCenterPaidSubmitActivity(getActivity(), ShopCenterSubmitActivity.FLAG_ADD);
                  } else if (TextUtils.equals("4", isShop)) {
                      ShopCenterPaidSubmitActivity.startShopCenterPaidSubmitActivity(getActivity(), ShopCenterSubmitActivity.FLAG_ADD);
                  }
                 break;
              case R.id.tv_sc://收藏
                  MyCollectActivity.CollectActivity(getActivity());
                  break;
              case R.id.tab3://关于我们
                  AboutActivity.startAboutActivity(getActivity());
                  break;
              case R.id.tab4://联系客服
                  dh();
                  break;
              case R.id.tab5://帮助反馈
                 // HelpActivity.startHelpActivity(getActivity());
                  HelpTwoActivity.startHelpTwoActivity(getActivity());
                  break;

          }
    }

    private class LabelItem {
        String id;
        String label;
        Object val;
        private int index;
    }

    //加载数据字典
    public void loadDataIndex() {
        AppDicInfoParam appDicInfoParam = new AppDicInfoParam();
        AppDicInfoRequestObject appDicInfoRequestObject = new AppDicInfoRequestObject();
        appDicInfoRequestObject.setParam(appDicInfoParam);
        httpTool.post(appDicInfoRequestObject, URLConfig.DATA_INDEX, new HttpTool.HttpCallBack<AppConfigureResponseObject>() {
            @Override
            public void onSuccess(AppConfigureResponseObject o) {
                List<AppConfigureResponseParam> dataList = o.getDataList();
                for (AppConfigureResponseParam app : dataList) {
                    if (app.getName().equals(CustomConfig.SERVICE_PHONE)) {
                        Phone = app.getContent();
                        break;
                    }
                }
            }

            @Override
            public void onError(AppConfigureResponseObject o) {

            }
        });
    }

    private void dh() {
        final Dialog dialog1 = new Dialog(getActivity());
        dialog1.setTitle("温馨提示");
        dialog1.setMessage(Phone);
        dialog1.setConfirmText("呼叫");
        dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialog1.dismiss();
            }
        });
        dialog1.show();
    }
}
