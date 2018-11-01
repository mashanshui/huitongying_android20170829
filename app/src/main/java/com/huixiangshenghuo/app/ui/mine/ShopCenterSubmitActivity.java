package com.huixiangshenghuo.app.ui.mine;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.doumee.common.weixin.entity.request.PayOrderRequestEntity;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ThirdPartyClient;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.alipay.AliPayTool;
import com.huixiangshenghuo.app.comm.baidu.BaiduPoiSearchActivity;
import com.huixiangshenghuo.app.comm.ftp.FtpUploadBean;
import com.huixiangshenghuo.app.comm.ftp.FtpUploadUtils;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.comm.utils.StringUtils;
import com.huixiangshenghuo.app.comm.wxpay.WXPayTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.activityshopcircle.ShopInfoActivity;
import com.huixiangshenghuo.app.ui.login.RegActivity;
import com.huixiangshenghuo.app.view.MyCityPicker;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestParam;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestParam;
import com.doumee.model.request.shop.ShopAddRequestObject;
import com.doumee.model.request.shop.ShopAddRequestParam;
import com.doumee.model.request.shop.ShopEditRequestObject;
import com.doumee.model.request.shop.ShopEditRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.request.userInfo.ProvinceRequestObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.goodsorder.WeixinOrderAddResponseObject;
import com.doumee.model.response.goodsorder.WeixinOrderResponseParam;
import com.doumee.model.response.shop.ShopAddResponseObject;
import com.doumee.model.response.shop.ShopAddResponseParam;
import com.doumee.model.response.shop.ShopParam;
import com.doumee.model.response.userinfo.AreaResponseParam;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShopCenterSubmitActivity extends BaseActivity implements View.OnClickListener {


    private ImageView shopImage,shopCodeImage,shopXukezhengImage;
    private TextView uploadView;
    private EditText shopNameView,addressCodeView,tuijianView,telView;
    private TextView areaView, addressView,typeView,startView,endView,shopCodeView,shopCodeImageView,xukeZhenView;
    private RadioGroup radioGroup;
    private CheckBox agreementCheckBox;
    private Button submitButton;
    private TextView shopMoneyView,shopInfoView;
    private TextView tv_sys_protocol;
    private LinearLayout sysProLinearLayout,shopInfoLoyout,shopViewLayout;
    private TextView payTypeLabel;
    private EditText tv_peisongfei;
    private EditText tv_baoyou;
    private LinearLayout ll_sendfee;
    private LinearLayout ll_freeSendfee;


    private static final int SHOP_IMAGE = 0;
    private static final int SHOP_CODE_IMAGE = 1;
    private static final int SHOP_XUKE_IMAGE = 2;
    private static final int SHOP_TYPE = 3;
    private static final int SHOP_ADDRESS = 4;
    private static final int SHOP_AREA = 5;
    private static final int CAMERA = 6;//拍照
    private static final int SHOP_INFO = 7;

    private String shopImagePath,shopCodeImagePath,shopXukePath,shopType;
    private int shopImageType;

    private static final int START_TIME = 0;
    private static final int END_TIME = 1;
    private TimePickerDialog timePickerDialog;
    private int timeType = START_TIME;

    private static final int PAY_GOLD = 0;//金币
    private static final int PAY_ALI = 1;//支付宝
    //=============================================
    private static final int WECHAT_PAY = 2;//微信支付
    private int payWeChat = 0;
    private String orderId;
    //=============================================
    private int payType = PAY_GOLD;
    private String longitude,latitude;
    private String areaId;
    private double payMoney;
    private String payPassword;
    private AlertDialog alertDialog,picAlertDialog;
    private String imagePath;

    public static final int FLAG_UPDATE = 1;
    public static final int FLAG_ADD = 0;
    private int flag;//用来标示 是会员还是商家 0 是会员 1 是商家

    public static void startShopCenterSubmitActivity(Context context,int flag){
        Intent intent = new Intent(context,ShopCenterSubmitActivity.class);
        intent.putExtra("flag",flag);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_center_submit);
        flag = getIntent().getIntExtra("flag",FLAG_ADD);
        initView();
        loadDataIndex();
        initDialog();
        initPicDialog();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("商户中心");
        shopImage = (ImageView)findViewById(R.id.shop_image);
        uploadView = (TextView)findViewById(R.id.upload);
        shopNameView = (EditText)findViewById(R.id.name);
        tuijianView = (EditText)findViewById(R.id.tuijian);
        addressView = (TextView) findViewById(R.id.address);
        telView = (EditText)findViewById(R.id.tel);
        typeView = (TextView)findViewById(R.id.shop_type);
        startView = (TextView)findViewById(R.id.start_time);
        endView = (TextView)findViewById(R.id.end_time);
        shopCodeView = (EditText)findViewById(R.id.shop_code);
        shopCodeImageView = (TextView)findViewById(R.id.shop_code_image);
        xukeZhenView = (TextView)findViewById(R.id.shop_xukezheng);
        radioGroup = (RadioGroup)findViewById(R.id.pay_type);
        agreementCheckBox = (CheckBox)findViewById(R.id.agreement);
        submitButton = (Button)findViewById(R.id.submit);
        areaView = (TextView) findViewById(R.id.area);
        addressCodeView = (EditText)findViewById(R.id.address_code);
        shopMoneyView = (TextView)findViewById(R.id.shop_money);
        tv_sys_protocol = (TextView) findViewById(R.id.tv_shop_sys_protocol);
        shopCodeImage = (ImageView)findViewById(R.id.shop_code_show_image) ;
        shopXukezhengImage = (ImageView)findViewById(R.id.shop_xukezheng_show_image) ;
        sysProLinearLayout = (LinearLayout)findViewById(R.id.ll_shop_sys_protocol) ;
        payTypeLabel = (TextView)findViewById(R.id.pay_type_label) ;
        shopInfoLoyout = (LinearLayout)findViewById(R.id.shop_info_label) ;
        shopViewLayout = (LinearLayout)findViewById(R.id.shop_info_view) ;
        shopInfoView = (TextView)findViewById(R.id.shop_info) ;
        tv_peisongfei = (EditText) findViewById(R.id.tv_peisongfei);
        tv_baoyou = (EditText) findViewById(R.id.tv_baoyou);
        ll_sendfee = (LinearLayout) findViewById(R.id.ll_shop_center_submit_sendfee);
        ll_freeSendfee = (LinearLayout) findViewById(R.id.ll_shop_center_submit_freeSendfee);
        tv_baoyou.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tv_peisongfei.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        uploadView.setOnClickListener(this);
        shopCodeImageView.setOnClickListener(this);
        xukeZhenView.setOnClickListener(this);
        typeView.setOnClickListener(this);
        startView.setOnClickListener(this);
        endView.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        addressView.setOnClickListener(this);
        areaView.setOnClickListener(this);
        shopInfoView.setOnClickListener(this);
        tv_sys_protocol.setOnClickListener(this);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time =  hourOfDay + ":"+minute;
                if (timeType == START_TIME){
                    startView.setText(time);

                }else{
                    endView.setText(time);
                }
            }
        },hour,minute,true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.pay_gold:
                        payType = PAY_GOLD;
                        break;
                    case R.id.pay_ali:
                        payType = PAY_ALI;
                        break;
                    case R.id.wechat_pay_shop_center:
                        payType = WECHAT_PAY;
                        break;
                }
            }
        });

        if (flag == FLAG_UPDATE){
            radioGroup.setVisibility(View.GONE);
            shopMoneyView.setVisibility(View.GONE);
            submitButton.setText("提交");
            sysProLinearLayout.setVisibility(View.GONE);
            payTypeLabel.setVisibility(View.GONE);
            shopInfoLoyout.setVisibility(View.GONE);
            tuijianView.setEnabled(false);
            shopViewLayout.setVisibility(View.VISIBLE);
            ll_sendfee.setVisibility(View.VISIBLE);
            ll_freeSendfee.setVisibility(View.VISIBLE);

        }
        setViewVal();
    }


    private void setViewVal(){
      UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
      ShopParam shopParam = userInfo.getShopParam();
      if (null != shopParam){
          shopNameView.setText(shopParam.getName());
          shopType = shopParam.getCategoryId();
          typeView.setText(shopParam.getCategoryName());
          addressView.setText(shopParam.getAddr());
          longitude = shopParam.getJingdu();
          latitude = shopParam.getWeidu();
          tuijianView.setText(shopParam.getAgentName());
          areaId = shopParam.getAreaId();
          areaView.setText(shopParam.getProvinceName()+"/"+shopParam.getCityName()+"/"+shopParam.getAreaName());
          shopImagePath = shopParam.getListImg();
          ImageLoader.getInstance().displayImage(shopParam.getListImgFull(),shopImage);
          telView.setText(shopParam.getMobile());
          startView.setText(shopParam.getStartTime());
          endView.setText(shopParam.getEndTime());
          shopCodeView.setText(shopParam.getOperateCode());
          shopCodeImagePath = shopParam.getOperateUrl();
          ImageLoader.getInstance().displayImage(shopParam.getOperateUrlFull(),shopCodeImage);
          shopXukePath = shopParam.getOtherUrl();
          ImageLoader.getInstance().displayImage(shopParam.getOtherUrlFull(),shopXukezhengImage);
          addressCodeView.setText(shopParam.getMenpaihao());
          shopInfoView.setText(shopParam.getInfo());
          tv_peisongfei.setText(shopParam.getSendFee() + "");
          tv_baoyou.setText(shopParam.getFreeSendFee() + "");
          shopInfoView.setText(shopParam.getInfo());

      }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.upload:
                shopImageType = SHOP_IMAGE;
                picAlertDialog.show();
                break;

            case R.id.area:
//                Intent areaIntent = new Intent(this, AreaActivity.class);
//                startActivityForResult(areaIntent,SHOP_AREA);
//                Intent areaIntent = new Intent(this, AreaRegisterActivity.class);
//                startActivityForResult(areaIntent, SHOP_AREA);//页面选择区域


                requestArea();//弹窗选择区域
                break;

            case R.id.address:
                Intent intent = new Intent(this, BaiduPoiSearchActivity.class);
                startActivityForResult(intent,SHOP_ADDRESS);
                break;

            case R.id.shop_code_image:
                shopImageType = SHOP_CODE_IMAGE;
                picAlertDialog.show();
                break;

            case R.id.shop_xukezheng:
                shopImageType = SHOP_XUKE_IMAGE;
                picAlertDialog.show();
                break;

            case R.id.shop_type:
//                ShopTypeActivity.startShopTypeActivity(this,SHOP_TYPE);
                ShopTypeActivity2.startShopTypeActivity(this, SHOP_TYPE);
                break;

            case R.id.start_time:
                timeType = START_TIME;
                timePickerDialog.show();
                break;

            case R.id.end_time:
                timeType = END_TIME;
                timePickerDialog.show();
                break;

            case R.id.shop_info:
                String d = shopInfoView.getText().toString().trim();
                startActivityForResult(new Intent(this,InputShopInfoActivity.class).putExtra("content",d),SHOP_INFO);
                break;

            case R.id.submit:
                if (flag == FLAG_ADD) {//提交
                    if (payMoney <= 0) {//免费升级
                        submit();
                    } else {
                        //需要保证金
                        if (payType == PAY_GOLD) {//选择惠宝支付
                    String shopName = shopNameView.getText().toString().trim();
                    String address = addressView.getText().toString().trim();
                    String tel = telView.getText().toString().trim();
                    String startTime = startView.getText().toString();
                    String endTime = endView.getText().toString();
                    String shopCode = shopCodeView.getText().toString().trim();
                    boolean isCheck = agreementCheckBox.isChecked();

                    if (TextUtils.isEmpty(shopImagePath)){
                        ToastView.show("请上传商铺图片");
                        return;
                    }
                    if (TextUtils.isEmpty(shopName)){
                        ToastView.show("请输入商户名称");
                        return;
                    }
                    if (TextUtils.isEmpty(areaId)){
                        ToastView.show("请选择所属区域");
                        return;
                    }
                    if (TextUtils.isEmpty(address)){
                        ToastView.show("请选择商户地址");
                        return;
                    }
                    if (TextUtils.isEmpty(shopType)){
                        ToastView.show("请选择商户类型");
                        return;
                    }
                    if (TextUtils.isEmpty(tel)){
                        ToastView.show("请输入服务电话");
                        return;
                    }
                    if (TextUtils.isEmpty(startTime)){
                        ToastView.show("请选择开始营业时间");
                        return;
                    }
                    if (TextUtils.isEmpty(endTime)){
                        ToastView.show("请选择结束营业时间");
                        return;
                    }
                    if (TextUtils.isEmpty(shopCode)){
                        ToastView.show("请输入营业执照编号");
                        return;
                    }
                    if (TextUtils.isEmpty(shopCodeImagePath)){
                        ToastView.show("请上传营业执照");
                        return;
                    }
                    if (TextUtils.isEmpty(shopXukePath)){
                        ToastView.show("请上传许可证");
                        return;
                    }
                    if (!isCheck){
                        ToastView.show("请同意惠享生活商户协议");
                        return;
                    }
                    alertDialog.show();//金币支付小窗口 需要输入支付密码
                }else{
                    submit();
                }
                    }
                }else {
                    update();//编辑信息
                }
                break;
            case R.id.tv_shop_sys_protocol://用户协议
                ShopInfoActivity.startShopInfoActivity(ShopCenterSubmitActivity.this, "", "2");
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SHOP_TYPE){
                HashMap<String,String> map = (HashMap) data.getSerializableExtra("data");
               Set<String> keyList =  map.keySet();
               for (String key : keyList){
                   shopType = key;
                   String name = map.get(shopType);
                   typeView.setText(name);
               }
                return;
            }else if (requestCode == SHOP_ADDRESS){
                String address = data.getStringExtra("data");
                 longitude =  data.getStringExtra("longitude");
                 latitude = data.getStringExtra("latitude");
                 addressView.setText(address);
                return;
            }else if (requestCode == SHOP_AREA){
//                HashMap<String,String> map = (HashMap) data.getSerializableExtra("data");
//                Set<String> keyList =  map.keySet();
//                for (String key : keyList){
//                    areaId = key;
//                    String name = map.get(areaId);
//                    areaView.setText(name);
//                }
                //===============================
                areaId = data.getStringExtra("areaId");
                areaView.setText(StringUtils.avoidNull(data.getStringExtra("areaName")));
                //===============================
                return;
            }else if (requestCode == CAMERA){
                File file = new File(imagePath);
                if(file.exists()){
                    ftpUploadImage(imagePath);
                }else{
                    ToastView.show("照片不存在..");
                }
                return;
            }else if(requestCode == SHOP_INFO){
                String d = data.getStringExtra("data");
                shopInfoView.setText(d);
            }
            if (data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    sendPicByUri(selectedImage);
                }
            }
        }
    }



    private void initPicDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.activity_send_news_dialog,null);
        Button button1 = (Button)view.findViewById(R.id.item_1);
        Button button2 = (Button)view.findViewById(R.id.item_2);
        Button cancelButton = (Button)view.findViewById(R.id.cancel);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAlertDialog.dismiss();
                fromCamera();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAlertDialog.dismiss();
                selectPicFromLocal();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAlertDialog.dismiss();
            }
        });
        builder.setView(view);
        builder.setCancelable(true);
        picAlertDialog = builder.create();
    }

    private void fromCamera(){//CAMERA
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File rootPath = new File(CustomConfig.IMAGE_PATH);
            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }
            File file = new File(rootPath,System.currentTimeMillis()+".jpg");
            if (!file.exists()){
                try{
                    file.createNewFile();
                }catch (Exception e){
                }
            }
            imagePath = file.getAbsolutePath();
            Uri uri= Uri.fromFile(file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent, CAMERA);
        }else{
            ToastView.show("没有内存卡不能拍照");
        }
    }

    /**
     * s从本地现在图片
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, shopImageType);
    }



    private void sendPicByUri(Uri selectedImage){
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;
            if (picturePath == null || picturePath.equals("null")) {
                ToastView.show("选择的图片不存在");
                return;
            }
            ftpUploadImage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                ToastView.show("选择的图片不存在");
                return;
            }
            ftpUploadImage(file.getAbsolutePath());
        }
    }

    private void ftpUploadImage(final String picturePath){
        showProgressDialog("正在上传图片..");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<FtpUploadBean> fileList = new ArrayList<>();
                final FtpUploadBean ftpUploadBean = new FtpUploadBean();
                ftpUploadBean.file = new File(picturePath);
                fileList.add(ftpUploadBean);
                FtpUploadUtils ftpUploadUtils = new FtpUploadUtils();
                ftpUploadUtils.setListener(new FtpUploadUtils.OnUploadListener() {
                    @Override
                    public void onCompleted() {
                        dismissProgressDialog();
                        if (shopImageType == SHOP_IMAGE)
                        shopImagePath = ftpUploadBean.serverPath;
                        else if (shopImageType == SHOP_CODE_IMAGE){
                            shopCodeImagePath = ftpUploadBean.serverPath;
                        }else if (shopImageType == SHOP_XUKE_IMAGE){
                            shopXukePath = ftpUploadBean.serverPath;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showImage();
                            }
                        });
                    }
                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Log.e("图片上传失败" ,e.getMessage());
                    }
                    @Override
                    public void onNext(String s, String type) {
                    }
                });
                ftpUploadUtils.upLoadListFtpUploadBean(fileList,CustomConfig.SHOP_IMAGE);
            }
        }).start();
    }

    private void showImage(){
        switch (shopImageType){
            case SHOP_IMAGE:
                String path = "http://" + CustomConfig.FTP_URL +CustomConfig.HTTP_PORT+
                        CustomConfig.SHOP_IMAGE+shopImagePath;
                ImageLoader.getInstance().displayImage(path,shopImage);
                break;
            case SHOP_CODE_IMAGE:
                shopCodeImageView.setText("重新上传");
                String codeImage = "http://" + CustomConfig.FTP_URL +CustomConfig.HTTP_PORT+
                        CustomConfig.SHOP_IMAGE+shopCodeImagePath;
                ImageLoader.getInstance().displayImage(codeImage,shopCodeImage);

                break;
            case SHOP_XUKE_IMAGE:
                xukeZhenView.setText("重新上传");
                String xukezhjengImage = "http://" + CustomConfig.FTP_URL +CustomConfig.HTTP_PORT+
                        CustomConfig.SHOP_IMAGE+shopXukePath;
                ImageLoader.getInstance().displayImage(xukezhjengImage,shopXukezhengImage);
                break;
        }
    }

    private void initDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.activity_zhuan_info_dialog,null);
        ImageButton closeButton = (ImageButton)view.findViewById(R.id.close);
        TextView button = (TextView) view.findViewById(R.id.pay);
        TextView cancelButton = (TextView)view.findViewById(R.id.cancel) ;
        final EditText editText = (EditText)view.findViewById(R.id.pay_password);
        Button helpButton = (Button)view.findViewById(R.id.help);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                alertDialog.dismiss();
            }
        });
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegActivity.startRegActivity(ShopCenterSubmitActivity.this,RegActivity.EDIT_PAY_PASSWORD);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                alertDialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payPassword = editText.getText().toString().trim();
                if (TextUtils.isEmpty(payPassword)){
                    ToastView.show("请输入支付密码");
                    return;
                }
                editText.setText("");
                alertDialog.dismiss();
                submit();
            }
        });
        builder.setCancelable(false);
        builder.setView(view);
        alertDialog = builder.create();
    }


    private void submit(){
        String shopName = shopNameView.getText().toString().trim();
        String tuijian = tuijianView.getText().toString().trim();
        String address = addressView.getText().toString().trim();
        String tel = telView.getText().toString().trim();
        String startTime = startView.getText().toString();
        String endTime = endView.getText().toString();
        String shopCode = shopCodeView.getText().toString().trim();
        String shopInfo = shopInfoView.getText().toString().trim();
        boolean isCheck = agreementCheckBox.isChecked();
        String addressCode = addressCodeView.getText().toString().trim();
        if (TextUtils.isEmpty(shopImagePath)){
            ToastView.show("请上传商铺图片");
            return;
        }
        if (TextUtils.isEmpty(shopName)){
            ToastView.show("请输入商户名称");
            return;
        }
        if (TextUtils.isEmpty(areaId)){
            ToastView.show("请选择所属区域");
            return;
        }
        if (TextUtils.isEmpty(address)){
            ToastView.show("请选择商户地址");
            return;
        }
        if (TextUtils.isEmpty(shopType)){
            ToastView.show("请选择商户类型");
            return;
        }
        if (TextUtils.isEmpty(tel)){
            ToastView.show("请输入服务电话");
            return;
        }
        if (TextUtils.isEmpty(startTime)){
            ToastView.show("请选择开始营业时间");
            return;
        }
        if (TextUtils.isEmpty(endTime)){
            ToastView.show("请选择结束营业时间");
            return;
        }
        if (TextUtils.isEmpty(shopCode)){
            ToastView.show("请输入营业执照编号");
            return;
        }
        if (TextUtils.isEmpty(shopCodeImagePath)){
            ToastView.show("请上传营业执照");
            return;
        }
        if (TextUtils.isEmpty(shopXukePath)){
            ToastView.show("请上传许可证");
            return;
        }
        if (!isCheck){
            ToastView.show("请同意惠享生活商户协议");
            return;
        }

        ShopAddRequestParam shopAddRequestParam = new ShopAddRequestParam();
        shopAddRequestParam.setName(shopName);
        shopAddRequestParam.setCategoryId(shopType);
        shopAddRequestParam.setAddr(address);
        shopAddRequestParam.setJingdu(longitude);
        shopAddRequestParam.setWeidu(latitude);
        shopAddRequestParam.setMobile(tel);
        shopAddRequestParam.setStartTime(startTime);
        shopAddRequestParam.setEndTime(endTime);
        shopAddRequestParam.setBusinessCode(shopCode);
        shopAddRequestParam.setOprateCode(shopCode);
        shopAddRequestParam.setAreaId(areaId);
        shopAddRequestParam.setOperateUrl(shopCodeImagePath);
        shopAddRequestParam.setOtherUrl(shopXukePath);
        shopAddRequestParam.setListImg(shopImagePath);
        shopAddRequestParam.setInfo(shopInfo);
//        shopAddRequestParam.setAgentName("");
        shopAddRequestParam.setMenpaihao(addressCode);

//        if (payType == PAY_GOLD) {//金币支付
//            shopAddRequestParam.setIntegralnum(payMoney);
//            shopAddRequestParam.setPaypwd(payPassword);
//        }
//        if (payType == WECHAT_PAY) {
//            shopAddRequestParam.setPayMethod("0");//支付方式 0微信 1支付宝
//        } else {
//            shopAddRequestParam.setPayMethod("1");//支付方式 0微信 1支付宝
//        }
        if (payMoney <= 0) {
            shopAddRequestParam.setPayMethod("2");//支付方式 0微信 1支付宝 2免支付
        } else {
            if (payType == PAY_GOLD) {//金币支付
            shopAddRequestParam.setIntegralnum(payMoney);
            shopAddRequestParam.setPaypwd(payPassword);
        }
        if (payType == WECHAT_PAY) {
            shopAddRequestParam.setPayMethod("0");//支付方式 0微信 1支付宝
        } else {
            shopAddRequestParam.setPayMethod("1");//支付方式 0微信 1支付宝
        }
        }


//        shopAddRequestParam.setPayMethod("1");//支付方式 1支付宝
        ShopAddRequestObject shopAddRequestObject = new ShopAddRequestObject();
        shopAddRequestObject.setParam(shopAddRequestParam);

        showProgressDialog("正在操作");
        httpTool.post(shopAddRequestObject, URLConfig.APPLY_SHOP, new HttpTool.HttpCallBack<ShopAddResponseObject>() {
            @Override
            public void onSuccess(ShopAddResponseObject o) {
                dismissProgressDialog();
                ShopAddResponseParam info = o.getRecord();
                String isPayDone = info.getIsPayDone();
                if (TextUtils.equals("0",isPayDone)) {//未支付完成
                    if (payType == WECHAT_PAY) {

                        if (ThirdPartyClient.isWeixinAvilible(ShopCenterSubmitActivity.this)) {
                            payOrder(o.getRecord().getOrderId(), "");
                        } else {
                            ToastView.show("请安装微信客户端，或者使用其它支付方式");
                        }

//                        payOrder(o.getRecord().getOrderId(), "");
                    } else {
                        aliPay(o.getParam().getParamStr());
                    }

//                    aliPay(o.getParam().getParamStr());
                } else if (TextUtils.equals("1", isPayDone)) {//0未支付，1已支付完成（如果使用全部积分支付） 2 完成 免支付
                    loadUser();
                } else if (TextUtils.equals("2", isPayDone)) {//0未支付，1已支付完成（如果使用全部积分支付） 2 完成 免支付
                    loadUser();
                }
            }
            @Override
            public void onError(ShopAddResponseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void update(){
        String shopName = shopNameView.getText().toString().trim();
        String address = addressView.getText().toString().trim();
        String tel = telView.getText().toString().trim();
        String startTime = startView.getText().toString().trim();
        String endTime = endView.getText().toString().trim();
        String shopInfo = shopInfoView.getText().toString().trim();
        double sendFee = Double.parseDouble(tv_peisongfei.getText().toString().trim());
        double freeSendFee = Double.parseDouble(tv_baoyou.getText().toString().trim());


        String addressCode = addressCodeView.getText().toString().trim();
        if (TextUtils.isEmpty(shopImagePath)){
            ToastView.show("请上传商铺图片");
            return;
        }
        if (TextUtils.isEmpty(shopName)){
            ToastView.show("请输入商户名称");
            return;
        }
        if (TextUtils.isEmpty(areaId)){
            ToastView.show("请选择所属区域");
            return;
        }
        if (TextUtils.isEmpty(address)){
            ToastView.show("请选择商户地址");
            return;
        }
        if (TextUtils.isEmpty(shopType)){
            ToastView.show("请选择商户类型");
            return;
        }
        if (TextUtils.isEmpty(tel)){
            ToastView.show("请输入服务电话");
            return;
        }
        if (TextUtils.isEmpty(startTime)){
            ToastView.show("请选择开始营业时间");
            return;
        }
        if (TextUtils.isEmpty(endTime)){
            ToastView.show("请选择结束营业时间");
            return;
        }

        showProgressDialog("正在操作");
        ShopEditRequestParam shopAddRequestParam = new ShopEditRequestParam();
        shopAddRequestParam.setName(shopName);
        shopAddRequestParam.setCategoryId(shopType);
        shopAddRequestParam.setAddr(address);
        shopAddRequestParam.setJingdu(longitude);
        shopAddRequestParam.setWeidu(latitude);
        shopAddRequestParam.setMobile(tel);
        shopAddRequestParam.setStartTime(startTime);
        shopAddRequestParam.setEndTime(endTime);
        shopAddRequestParam.setInfo(shopInfo);
        shopAddRequestParam.setAreaId(areaId);
        shopAddRequestParam.setListImg(shopImagePath);
        shopAddRequestParam.setMenpaihao(addressCode);
        shopAddRequestParam.setSendFee(sendFee);
        shopAddRequestParam.setFreeSendFee(freeSendFee);

        ShopEditRequestObject shopEditRequestObject = new ShopEditRequestObject();
        shopEditRequestObject.setParam(shopAddRequestParam);

        httpTool.post(shopEditRequestObject, URLConfig.SHOP_UPDATE, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                dismissProgressDialog();
                loadUser();
            }
            @Override
            public void onError(ResponseBaseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }


    private void aliPay(String params){
        AliPayTool aliPayTool = new AliPayTool(this);
        aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
            @Override
            public void onPaySuccess() {
                loadUser();
            }
            @Override
            public void onPayError(String resultInfo) {

            }
        });
        aliPayTool.pay(params);
    }

    //==========================================================================================
    @Override
    protected void onResume() {
        super.onResume();
        if (payType == WECHAT_PAY && payWeChat == 1) {
            loadPayResult();
        }
//        loadDataIndex();
    }


    //支付订单
    private void payOrder(final String orderId, String aliParams) {

        switch (payType) {
            case WECHAT_PAY:
                this.orderId = orderId;
                showProgressDialog(getString(R.string.loading));
                WeixinOrderAddRequestParam weixinOrderAddRequestParam = new WeixinOrderAddRequestParam();
                weixinOrderAddRequestParam.setOrderId(orderId);
                weixinOrderAddRequestParam.setTradeType("APP");
                weixinOrderAddRequestParam.setType("3");//订单类型：订单类型：0商品订单 1充值 2升级 3成为商户
                WeixinOrderAddRequestObject weixinOrderAddRequestObject = new WeixinOrderAddRequestObject();
                weixinOrderAddRequestObject.setParam(weixinOrderAddRequestParam);
                httpTool.post(weixinOrderAddRequestObject, URLConfig.ORDER_WECHAT_PAY, new HttpTool.HttpCallBack<WeixinOrderAddResponseObject>() {
                    @Override
                    public void onSuccess(WeixinOrderAddResponseObject o) {
                        dismissProgressDialog();
                        WeixinOrderResponseParam weiXin = o.getData();
                        PayOrderRequestEntity payOrderRequestEntity = weiXin.getParam();
                        payWeChat = 1;
                        WXPayTool.WXPay wxPay = new WXPayTool.WXPay();
                        wxPay.setNonceStr(payOrderRequestEntity.getNoncestr());
                        wxPay.setPrepayId(payOrderRequestEntity.getPrepayid());
                        wxPay.setSign(payOrderRequestEntity.getSign());
                        wxPay.setAppId(payOrderRequestEntity.getAppid());
                        wxPay.setPartnerId(payOrderRequestEntity.getPartnerid());
                        wxPay.setTimeStamp(payOrderRequestEntity.getTimestamp());
                        wxPay.setPackageStr(payOrderRequestEntity.getPackageStr());
                        WXPayTool wxPayTool = new WXPayTool(ShopCenterSubmitActivity.this, weiXin.getParam().getAppid());
                        wxPayTool.payRequest(wxPay);
                    }

                    @Override
                    public void onError(WeixinOrderAddResponseObject o) {
                        dismissProgressDialog();
                        ToastView.show(o.getErrorMsg());
                    }
                });
                break;
            case PAY_ALI:
                AliPayTool aliPayTool = new AliPayTool(this);
                aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
                    @Override
                    public void onPaySuccess() {
                        loadUser();
                    }

                    @Override
                    public void onPayError(String resultInfo) {

                    }
                });
                aliPayTool.pay(aliParams);
                break;


        }
    }

    private void loadPayResult() {
        showProgressDialog(getString(R.string.loading));
        WeixinOrderQueryRequestParam weixinOrderQueryRequestParam = new WeixinOrderQueryRequestParam();
        weixinOrderQueryRequestParam.setOrderId(orderId);
        weixinOrderQueryRequestParam.setType("3");//订单类型：订单类型：0商品订单 1充值 2升级 3成为商户

        WeixinOrderQueryRequestObject weixinOrderQueryRequestObject = new WeixinOrderQueryRequestObject();
        weixinOrderQueryRequestObject.setParam(weixinOrderQueryRequestParam);

        httpTool.post(weixinOrderQueryRequestObject, URLConfig.ORDER_PAY_RESULT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                dismissProgressDialog();
                loadUser();
            }

            @Override
            public void onError(ResponseBaseObject o) {
                dismissProgressDialog();
//                payAlertTip.show();
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    //=============================================
    private void loadUser(){
        showProgressDialog("正在加载..");
        UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
        MemberInfoParamObject memberInfoParamObject = new MemberInfoParamObject();
        memberInfoParamObject.setMemberId(userInfoResponseParam.getMemberId());
        MemberInfoRequestObject memberInfoRequestObject = new MemberInfoRequestObject();
        memberInfoRequestObject.setParam(memberInfoParamObject);
        httpTool.post(memberInfoRequestObject, URLConfig.USER_INFO, new HttpTool.HttpCallBack<MemberInfoResponseObject>() {
            @Override
            public void onSuccess(MemberInfoResponseObject o) {
                dismissProgressDialog();
                ToastView.show("商户已提交");
                UserInfoResponseParam userInfo = o.getMember();
                SaveObjectTool.saveObject(userInfo);
                finish();
            }
            @Override
            public void onError(MemberInfoResponseObject o) {
                dismissProgressDialog();
            }
        });
    }


    //加载数据字典
    public void loadDataIndex(){
        AppDicInfoParam appDicInfoParam = new AppDicInfoParam();
        AppDicInfoRequestObject appDicInfoRequestObject = new AppDicInfoRequestObject();
        appDicInfoRequestObject.setParam(appDicInfoParam);
        httpTool.post(appDicInfoRequestObject, URLConfig.DATA_INDEX, new HttpTool.HttpCallBack<AppConfigureResponseObject>() {
            @Override
            public void onSuccess(AppConfigureResponseObject o) {
                List<AppConfigureResponseParam> dataList = o.getDataList();
                for (AppConfigureResponseParam app : dataList){
                    if (app.getName().equals(CustomConfig.DATA_INDEX_SHOP_MONEY)){
                        payMoney = Double.parseDouble(app.getContent());

                        if (Double.parseDouble(app.getContent()) <= 0) {
                            radioGroup.setVisibility(View.GONE);
                            payTypeLabel.setVisibility(View.GONE);
                            shopMoneyView.setText("");
                            shopMoneyView.setVisibility(View.GONE);
                        } else {
                            shopMoneyView.setVisibility(View.VISIBLE);
                            shopMoneyView.setText("为了确保您成功通过审核，需缴纳" + payMoney + "元保证金");
                        }
                    }
                }
            }
            @Override
            public void onError(AppConfigureResponseObject o) {

            }
        });
    }
    //==============================================================
    /**
     * 所有省
     */
    protected List<ProvinceParam> mProvinceDatas;

    /**
     * key - 省 value - 市
     */
    protected Map<String, List<CityParam>> mCitisDatasMap = new HashMap<>();

    /**
     * key - 市 values - 区
     */
    protected Map<String, List<AreaResponseParam>> mDistrictDatasMap = new HashMap<>();

    private void requestArea() {
        showProgressDialog(null);
        httpTool.post(new ProvinceRequestObject(), URLConfig.CITY_LIST, new HttpTool.HttpCallBack<ProvinceObject>() {
            @Override
            public void onSuccess(ProvinceObject resp) {
                dismissProgressDialog();
                if (resp.getLstProvince() != null && resp.getLstProvince().size() > 0) {
                    mProvinceDatas = resp.getLstProvince();
                    mCitisDatasMap = new HashMap<>();
                    mDistrictDatasMap = new HashMap<>();
                    for (ProvinceParam param : resp.getLstProvince()) {
                        if (param.getLstCity() != null && param.getLstCity().size() > 0) {
                            mCitisDatasMap.put(param.getProvinceId(), param.getLstCity());
                            for (CityParam cityParam : param.getLstCity()) {
                                mDistrictDatasMap.put(cityParam.getCityId(), cityParam.getLstArea());
                            }
                        }
                    }
                    chooseArea(areaView);
                }
            }

            @Override
            public void onError(ProvinceObject provinceObject) {
                dismissProgressDialog();
                ToastView.show(provinceObject.getErrorMsg());
            }


        });
    }

    //Texview的点击事件
    public void chooseArea(View view) {
        //判断输入法的隐藏状态
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                  InputMethodManager.HIDE_NOT_ALWAYS);
            selectAddress();//调用CityPicker选取区域

        }
    }

    private void selectAddress() {
        MyCityPicker cityPicker = new MyCityPicker.Builder(ShopCenterSubmitActivity.this)
              .textSize(14)
              .title("地址选择")
              .titleBackgroundColor("#FFFFFF")
              .confirTextColor("#696969")
              .cancelTextColor("#696969")
              .textColor(Color.parseColor("#000000"))
              .provinceCyclic(false)
              .cityCyclic(false)
              .districtCyclic(false)
              .visibleItemsCount(7)
              .itemPadding(10)
              .onlyShowProvinceAndCity(false)
              .build();
        cityPicker.setmProvinceDatas(mProvinceDatas);
        cityPicker.setmCitisDatasMap(mCitisDatasMap);
        cityPicker.setmDistrictDatasMap(mDistrictDatasMap);
        cityPicker.show();
        //监听方法，获取选择结果
        cityPicker.setOnCityItemClickListener(new MyCityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(int... citySelected) {
                areaId = mProvinceDatas.get(citySelected[0]).getLstCity().get(citySelected[1]).getLstArea().get(citySelected[2]).getAreaId();
                areaView.setText(mProvinceDatas.get(citySelected[0]).getName() + "/" + mProvinceDatas.get(citySelected[0]).getLstCity().get(citySelected[1]).getCityName() + "/" + mProvinceDatas.get(citySelected[0]).getLstCity().get(citySelected[1]).getLstArea().get(citySelected[2]).getAreaName());

            }
        });
    }


    //==============================================================
}
