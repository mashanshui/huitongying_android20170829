package com.huixiangshenghuo.app.ui.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.doumee.model.request.base.RequestBaseObject;
import com.doumee.model.request.userInfo.MemberListByNamesRequestObject;
import com.doumee.model.request.userInfo.MemberListByNamesRequestParam;
import com.doumee.model.response.sign.SignInResponseObject;
import com.doumee.model.response.userinfo.MemberListByNamesResponseObject;
import com.doumee.model.response.userinfo.MemberListByNamesResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.CustomApplication;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.app.NumberFormatTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.HomeActivity;
import com.huixiangshenghuo.app.ui.home.NocationActivity;
import com.huixiangshenghuo.app.ui.home.OrderListActivity;
import com.huixiangshenghuo.app.ui.home.PayMoneyActivity;
import com.huixiangshenghuo.app.ui.home.ShouKuanActivity;
import com.huixiangshenghuo.app.ui.home.TiXianActivity;
import com.huixiangshenghuo.app.ui.home.ZhuanInfoActivity;
import com.huixiangshenghuo.app.ui.home.ZhuanZhangActivity;
import com.huixiangshenghuo.app.ui.mine.MineBankCardActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.zxing.activity.CaptureActivity;

import java.util.LinkedList;
import java.util.List;


/**
 * 首页
 */
public class HomePageFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button qianDaoButton,messageButton;
    private ImageButton saoButton,closeButton;
    private TextView tab1View,tab2View,tab3View,tab4View,tab5View;
    private TextView qianDaoView,totalView;
    private AlertDialog alertDialog;
    private ImageView msgCountView;

    private String mParam1;
    private String mParam2;
    private HttpTool httpTool;
    private MessageCountReceiver messageCountReceiver;



    public HomePageFragment() {

    }

    public static HomePageFragment newInstance(String param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
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
        httpTool = HttpTool.newInstance(getActivity());
        messageCountReceiver = new MessageCountReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HomeActivity.REFRESH_USER);
        getActivity().registerReceiver(messageCountReceiver,intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        qianDaoButton = (Button)view.findViewById(R.id.qiandao);
        messageButton = (Button)view.findViewById(R.id.message);
        saoButton = (ImageButton)view.findViewById(R.id.saoma);
        tab1View = (TextView)view.findViewById(R.id.tab1);
        tab2View = (TextView)view.findViewById(R.id.tab2);
        tab3View = (TextView)view.findViewById(R.id.tab3);
        tab4View = (TextView)view.findViewById(R.id.tab4);
        tab5View = (TextView)view.findViewById(R.id.tab5);
        msgCountView = (ImageView)view.findViewById(R.id.msg_count);
        qianDaoButton.setOnClickListener(this);
        messageButton.setOnClickListener(this);
        saoButton.setOnClickListener(this);
        tab1View.setOnClickListener(this);
        tab2View.setOnClickListener(this);
        tab3View.setOnClickListener(this);
        tab4View.setOnClickListener(this);
        tab5View.setOnClickListener(this);

        initAlert();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = CustomApplication.getAppUserSharedPreferences();
        int msgCount = sharedPreferences.getInt(CustomConfig.MESSAGE_COUNT,0);
        int isRun = sharedPreferences.getInt(CustomConfig.APP_IS_RUNNING,1);
        if (isRun == 0){
            sharedPreferences.edit().remove(CustomConfig.MESSAGE_COUNT)
                    .remove(CustomConfig.APP_IS_RUNNING).commit();
            NocationActivity.startNocationActivity(getActivity());
        }else{
            if (msgCount > 0){
                msgCountView.setVisibility(View.VISIBLE);
            }else{
                msgCountView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qiandao:
                qianDaoSubmit();
                break;
            case R.id.message:
                NotificationManager manager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancelAll();
                SharedPreferences sharedPreferences = CustomApplication.getAppUserSharedPreferences();
                sharedPreferences.edit().remove(CustomConfig.MESSAGE_COUNT).commit();
                NocationActivity.startNocationActivity(getActivity());
                break;
            case R.id.saoma:
                Intent intent = new Intent(this.getActivity(), CaptureActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.tab1:
                ZhuanZhangActivity.startZhuanZhangActivity(getActivity());
                break;
            case R.id.tab2:
                ShouKuanActivity.startShouKuanActivity(getActivity());
                break;
            case R.id.tab3:
                OrderListActivity.startOrderListActivity(getActivity());
                break;
            case R.id.tab4:
                PayMoneyActivity.startPayMoneyActivity(getActivity());
                break;
            case R.id.tab5:
                UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
                String bank = userInfo.getBankCode();
                if (TextUtils.isEmpty(bank)){
                    MineBankCardActivity.startMineBankCardActivity(getActivity());
                }else{
                    TiXianActivity.startTiXianActivity(getActivity());
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 1){
                String result = data.getStringExtra("data");
                if (result.startsWith("http")){
                    Uri uri = Uri.parse(result);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }else{
                    submit(result);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(messageCountReceiver);
    }

    private void initAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(),R.layout.home_qiandao_dialog,null);
        closeButton = (ImageButton)view.findViewById(R.id.close);
        qianDaoView = (TextView)view.findViewById(R.id.qiandao);
        totalView =(TextView)view.findViewById(R.id.total_qiandao);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();
    }

    private void submit(String username){
        showProgressDialog("正在加载..");
        MemberListByNamesRequestParam memberListByNamesRequestParam = new MemberListByNamesRequestParam();
        List<String> list = new LinkedList<>();
        list.add(username);
        memberListByNamesRequestParam.setLoginNames(list);
        MemberListByNamesRequestObject memberListByNamesRequestObject = new MemberListByNamesRequestObject();
        memberListByNamesRequestObject.setParam(memberListByNamesRequestParam);

        httpTool.post(memberListByNamesRequestObject, URLConfig.USER_LIST_BY_LOGIN_NAME, new HttpTool.HttpCallBack<MemberListByNamesResponseObject>() {
            @Override
            public void onSuccess(MemberListByNamesResponseObject o) {
                dismissProgressDialog();
                List<MemberListByNamesResponseParam>  list = o.getRecordList();
                if (!list.isEmpty()){
                    ZhuanInfoActivity.startZhuanInfoActivity(getActivity(),list.get(0));
                }else{
                    ToastView.show("账户不存在");
                }
            }
            @Override
            public void onError(MemberListByNamesResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void qianDaoSubmit(){
        showProgressDialog("正在签到..");
        RequestBaseObject requestBaseObject = new RequestBaseObject();
        httpTool.post(requestBaseObject, URLConfig.QIAN_DAO, new HttpTool.HttpCallBack<SignInResponseObject>() {
            @Override
            public void onSuccess(SignInResponseObject o) {
                dismissProgressDialog();
                HomeActivity.sendRefreshUserBroadcast(getActivity());
                qianDaoView.setText(NumberFormatTool.numberFormat(o.getToDayIntegral()) );
                totalView.setText(NumberFormatTool.numberFormat(o.getAllSignIntegral()));
                alertDialog.show();
            }
            @Override
            public void onError(SignInResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private ProgressDialog progressDialog;
    protected void showProgressDialog(String msg){
        progressDialog =  ProgressDialog.show(this.getActivity(),"",msg);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    protected void dismissProgressDialog(){
        if(null != progressDialog){
            progressDialog.dismiss();
        }
    }

    private class MessageCountReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
              onResume();
        }
    }
}
