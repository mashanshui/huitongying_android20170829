package com.huixiangshenghuo.app.ui.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.huixiangshenghuo.app.comm.ftp.FtpUploadBean;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.userInfo.MemberIdCardAddRequestObject;
import com.doumee.model.request.userInfo.MemberIdCardAddRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.app.IDCardValidate;
import com.huixiangshenghuo.app.comm.ftp.FtpUploadUtils;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MineRealActivity extends BaseActivity implements View.OnClickListener {


    private EditText nameView,cardView;
    private ImageButton mineImageButton,cardImageButton;
    private Button submitButton;
    private String minePath,cardPath;
    private AlertDialog alertDialog;

    private static final int MINE_IMAGE = 1;
    private static final int CARD_IMAGE = 2;
    private static final int CAMERA = 3;//拍照

    private String imagePath;

    private int imageType;

    public static void startMineRealActivity(Context context){
        Intent intent = new Intent(context,MineRealActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_real);
        initView();
        initDialog();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("实名认证");
        nameView = (EditText)findViewById(R.id.name);
        cardView = (EditText)findViewById(R.id.card);
        mineImageButton = (ImageButton)findViewById(R.id.mine_image);
        cardImageButton = (ImageButton)findViewById(R.id.mine_card);
        submitButton = (Button)findViewById(R.id.submit);
        mineImageButton.setOnClickListener(this);
        cardImageButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
        String name = userInfoResponseParam.getName();
        String card = userInfoResponseParam.getIdCardNo();
        String cardImage1 = userInfoResponseParam.getIdCardImg();
        String cardImage2 = userInfoResponseParam.getIdCardImg2();
        String idCardCheckStatus = userInfoResponseParam.getIdCardCheckStatus();
        if (TextUtils.equals("0",idCardCheckStatus)){
            idCardCheckStatus = "（未认证）";
            submitButton.setVisibility(View.VISIBLE);
        }else if (TextUtils.equals("1",idCardCheckStatus)){
            idCardCheckStatus = "（已提交审核）";
            submitButton.setVisibility(View.GONE);
            mineImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mineImageButton.setEnabled(false);
            cardImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cardImageButton.setEnabled(false);
            nameView.setEnabled(false);
            cardView.setEnabled(false);

        }else if (TextUtils.equals("2",idCardCheckStatus)){
            idCardCheckStatus = "（已认证）";
            submitButton.setVisibility(View.GONE);
            mineImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mineImageButton.setEnabled(false);
            cardImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cardImageButton.setEnabled(false);
            nameView.setEnabled(false);
            cardView.setEnabled(false);

        }else if (TextUtils.equals("3",idCardCheckStatus)){
            idCardCheckStatus = "（认证失败）";
            submitButton.setVisibility(View.VISIBLE);
            mineImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cardImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        nameView.setText(name);
        cardView.setText(card);
        if (!TextUtils.isEmpty(cardImage1)){
            int index = cardImage1.indexOf("member")+7;
            minePath = cardImage1.substring(index);
            ImageLoader.getInstance().displayImage(cardImage1,mineImageButton);
        }
        if (!TextUtils.isEmpty(cardImage2)){
            int index = cardImage2.indexOf("member")+7;
            cardPath = cardImage2.substring(index);
            ImageLoader.getInstance().displayImage(cardImage2,cardImageButton);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_image:
                imageType = MINE_IMAGE;
                alertDialog.show();
                break;
            case R.id.mine_card:
                imageType = CARD_IMAGE;
                alertDialog.show();
                break;
            case  R.id.submit:
                submit();
                break;
        }
    }

    private void submit(){
        String name = nameView.getText().toString().trim();
        String card = cardView.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            ToastView.show("请输入您的真实姓名");
            return;
        }
        if (TextUtils.isEmpty(card)){
            ToastView.show("请输入您的身份证号");
            return;
        }
         String resultCard = IDCardValidate.validate_effective(card);
         if (!TextUtils.equals(card,resultCard)){
             ToastView.show(resultCard);
             return;
         }

        if (TextUtils.isEmpty(minePath)){
            ToastView.show("请上传手持身份证自拍照");
            return;
        }

        if (TextUtils.isEmpty(cardPath)){
            ToastView.show("请上传身份证正面照片");
            return;
        }
        showProgressDialog("正在提交审核..");
        MemberIdCardAddRequestParam memberIdCardAddRequestParam = new MemberIdCardAddRequestParam();
        memberIdCardAddRequestParam.setIdcardNo(card);
        memberIdCardAddRequestParam.setName(name);
        memberIdCardAddRequestParam.setImgurl1(minePath);
        memberIdCardAddRequestParam.setImgurl2(cardPath);
        MemberIdCardAddRequestObject memberIdCardAddRequestObject = new MemberIdCardAddRequestObject();
        memberIdCardAddRequestObject.setParam(memberIdCardAddRequestParam);
        httpTool.post(memberIdCardAddRequestObject, URLConfig.CHECK_USER, new HttpTool.HttpCallBack<ResponseBaseObject>() {
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
                ToastView.show("提交审核成功");
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

    private void initDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.activity_send_news_dialog,null);
        Button button1 = (Button)view.findViewById(R.id.item_1);
        Button button2 = (Button)view.findViewById(R.id.item_2);
        Button cancelButton = (Button)view.findViewById(R.id.cancel);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                fromCamera();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                selectPicFromLocal();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        builder.setView(view);
        builder.setCancelable(true);
        alertDialog = builder.create();
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
        startActivityForResult(intent, imageType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA){
                File file = new File(imagePath);
                if(file.exists()){
                    ftpUploadImage(imagePath);
                }else{
                    ToastView.show("图片不存在");
                }
            }else {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    sendPicByUri(selectedImage);
                }
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
                        if (imageType == MINE_IMAGE){
                            minePath = ftpUploadBean.serverPath;
                        }else{
                            cardPath = ftpUploadBean.serverPath;
                        }
                      showUpload();
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

    private void showUpload(){
       runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (imageType == MINE_IMAGE){
                    String path = "http://" + CustomConfig.FTP_URL +CustomConfig.HTTP_PORT+
                            CustomConfig.MEMBER_IMAGE+minePath;
                    ImageLoader.getInstance().displayImage(path,mineImageButton);
                }else{
                    String path = "http://" + CustomConfig.FTP_URL +CustomConfig.HTTP_PORT+
                            CustomConfig.MEMBER_IMAGE+cardPath;
                    ImageLoader.getInstance().displayImage(path,cardImageButton);
                }
            }
        });
    }
}
