package com.huixiangshenghuo.app.ui.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.ftp.FtpUploadBean;
import com.huixiangshenghuo.app.comm.ftp.FtpUploadUtils;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.request.userInfo.UpdateMemberRequestObject;
import com.doumee.model.request.userInfo.UpdateMemberRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.huixiangshenghuo.app.R.id.phone;

public class MyInfoActivity extends BaseActivity implements View.OnClickListener{



    private String facePath;
    private String uploadPath;

    private LinearLayout faceButton,sexButton;
    private RelativeLayout rl_name;//昵称
    private ImageView faceView;
    private TextView sexView,loginNameView,phoneView;
    private TextView tv_qq;
    private AlertDialog alertDialog,picAlertDialog;
    private static final int BOY = 0;
    private static final int GIRL = 1;
    private static final int NO = 2;
    private int sex = BOY;

    private static final int REQUEST_CODE_LOCAL = 3;
    private static final int CAMERA = 4;
    private String imagePath;


    private RelativeLayout rel_myinfo_sh;
    private static final int CLASSIFY_REQUEST_CODE = 6;

    private static final int NC = 10;//昵称


    public static void startMyInfoActivity(Context context){
        Intent intent = new Intent(context,MyInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        initTitleBar_1();
        initView();
        initSexDialog();
        initPicDialog();
    }

    private void initView(){
        titleView.setText("个人资料");
        actionButton.setText("提交");
        actionButton.setOnClickListener(this);
        actionButton.setVisibility(View.VISIBLE);
        faceButton = (LinearLayout)findViewById(R.id.face_button);
        rl_name = (RelativeLayout) findViewById(R.id.rl_login_name);
        sexButton = (LinearLayout)findViewById(R.id.sex_button);
        faceView = (ImageView)findViewById(R.id.face);
        sexView = (TextView)findViewById(R.id.sex) ;
        loginNameView = (TextView)findViewById(R.id.login_name);
        phoneView = (TextView) findViewById(phone);
        tv_qq = (TextView) findViewById(R.id.tv_qq);
        rel_myinfo_sh = (RelativeLayout) findViewById(R.id.rel_myinfo_sh);

        rl_name.setOnClickListener(this);
        faceButton.setOnClickListener(this);
        sexButton.setOnClickListener(this);
        rel_myinfo_sh.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
       UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
        String face = userInfo.getImgUrl();
//        String loginName = userInfo.getLoginName();
        String name = userInfo.getName();
        String phone = userInfo.getPhone();
        String sex = userInfo.getSex();
        if (!TextUtils.isEmpty(face)){
            ImageLoader.getInstance().displayImage(face,faceView);
        }
//        loginNameView.setText(loginName);
        loginNameView.setText(name);
        phone = phone.substring(0, 3) + "****" + phone.substring(7, 11);
        phoneView.setText(phone);
        //       phoneView.setText(phone);
        if (TextUtils.equals("0",sex)){
            sex = "男";
        }else if (TextUtils.equals("1",sex)){
            sex = "女";
        }else{
            sex = "保密";
        }
        sexView.setText(sex);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.action:
                submit();
                break;

            case R.id.rl_login_name:
                Intent intent1 = new Intent();
                intent1.putExtra("name", loginNameView.getText().toString().trim());
                intent1.setClass(MyInfoActivity.this, ActivityNickName.class);
                startActivityForResult(intent1, NC);
                break;

            case R.id.face_button:
                picAlertDialog.show();
                break;

            case R.id.sex_button:
                alertDialog.show();
                break;
            case R.id.rel_myinfo_sh:
                Intent intent = new Intent();
                intent.putExtra("address", SelectAcceptLocationActivity.SELECT_ADDRESS_NO);
                intent.setClass(MyInfoActivity.this, SelectAcceptLocationActivity.class);
                startActivityForResult(intent, CLASSIFY_REQUEST_CODE);

                break;
        }
    }

    private void submit(){
        showProgressDialog("正在提交..");
        UpdateMemberRequestParam updateMemberRequestParam = new UpdateMemberRequestParam();
        updateMemberRequestParam.setImgurl(uploadPath);
        updateMemberRequestParam.setSex(sex+"");
        UpdateMemberRequestObject updateMemberRequestObject = new UpdateMemberRequestObject();
        updateMemberRequestObject.setParam(updateMemberRequestParam);
        httpTool.post(updateMemberRequestObject, URLConfig.UPDATE_USER_INFO, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                dismissProgressDialog();
                updateUser();
            }
            @Override
            public void onError(ResponseBaseObject o) {
                dismissProgressDialog();
              ToastView.show(o.getErrorMsg());
            }
        });
    }

    private void updateUser(){
        showProgressDialog("正在加载..");
         UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
         MemberInfoParamObject memberInfoParamObject = new MemberInfoParamObject();
         memberInfoParamObject.setMemberId(userInfo.getMemberId());
         MemberInfoRequestObject memberInfoRequestObject = new MemberInfoRequestObject();
         memberInfoRequestObject.setParam(memberInfoParamObject);
         httpTool.post(memberInfoRequestObject, URLConfig.USER_INFO, new HttpTool.HttpCallBack<MemberInfoResponseObject>() {
             @Override
             public void onSuccess(MemberInfoResponseObject o) {
                 dismissProgressDialog();
                 ToastView.show("更新成功");
                 UserInfoResponseParam userInfoResponseParam = o.getMember();
                 SaveObjectTool.saveObject(userInfoResponseParam);
                 finish();
             }
             @Override
             public void onError(MemberInfoResponseObject o) {
                  dismissProgressDialog();
             }
         });
    }

    private void initSexDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.activity_my_info_sex,null);
        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.sex_group) ;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.boy:
                        sex = BOY;
                        sexView.setText("男");
                        break;
                    case R.id.girl:
                        sex = GIRL;
                        sexView.setText("女");
                        break;
                    case R.id.no:
                        sex = NO;
                        sexView.setText("保密");
                        break;
                }
                alertDialog.dismiss();

            }
        });
        builder.setView(view);
        builder.setCancelable(true);
        alertDialog = builder.create();
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
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            }
            else if (requestCode == CAMERA){
                File file = new File(imagePath);
                if(file.exists()){
                    ftpUploadImage(imagePath);
                }else{
                    ToastView.show("照片不存在..");
                }
            } else if (requestCode == NC) {
                loginNameView.setText(data.getStringExtra("name"));
            }

        }
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
        showProgressDialog("正在上传图片");
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
                        uploadPath = ftpUploadBean.serverPath;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String path = "http://" + CustomConfig.FTP_URL +CustomConfig.HTTP_PORT+
                                        CustomConfig.MEMBER_IMAGE+uploadPath;
                                ImageLoader.getInstance().displayImage(path,faceView);
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
                ftpUploadUtils.upLoadListFtpUploadBean(fileList,CustomConfig.MEMBER_IMAGE);
            }
        }).start();
    }


}
