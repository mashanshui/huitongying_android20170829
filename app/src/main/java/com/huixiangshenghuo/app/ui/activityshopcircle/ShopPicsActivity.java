package com.huixiangshenghuo.app.ui.activityshopcircle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huixiangshenghuo.app.comm.ftp.FtpUploadBean;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.shopImg.ShopImgAddDetailsRequestParam;
import com.doumee.model.request.shopImg.ShopImgAddRequestObject;
import com.doumee.model.request.shopImg.ShopImgAddRequestParam;
import com.doumee.model.request.shopImg.ShopImgDelRequestObject;
import com.doumee.model.request.shopImg.ShopImgDelRequestParam;
import com.doumee.model.request.shopImg.ShopImgListRequestObject;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.shopImg.ShopImgListResponseObject;
import com.doumee.model.response.shopImg.ShopImgListResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.ftp.FtpUploadUtils;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.view.ImageCompressUtil;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ShopPicsActivity extends BaseActivity implements View.OnClickListener,RefreshLayout.ILoadListener,RefreshLayout.OnRefreshListener{


    private GridView gridView;
    private RelativeLayout actionbar;
    private Button uploadButton;
    private TextView allButton,delButton;
    private RefreshLayout refreshLayout;

    private ArrayList<ShopImgListResponseParam> dataList;
    private CustomBaseAdapter<ShopImgListResponseParam> adapter;
    private LinkedList<ShopImgListResponseParam> selectPicList;
    private ArrayList<String> picList;
    private boolean isAction = false;
    private static final int REQUEST_CODE_LOCAL = 3;
    private static final int CAMERA = 4;
    private String imagePath;
    private AlertDialog picAlertDialog;
    private HttpTool httpTool;
    private int page;
    private String queryTime,shopId;
    private Bitmap defaultBitmap;

    private TranslateAnimation translateAnimation;

    public static void startShopPicsActivity(Context context){
        Intent intent = new Intent(context,ShopPicsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_pics);
        dataList = new ArrayList<>();
        selectPicList = new LinkedList<>();
        picList = new ArrayList<>();
        defaultBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.business_default);
        httpTool = HttpTool.newInstance(this);
        shopId = SaveObjectTool.openUserInfoResponseParam().getShopId();
        initView();
        initAdapter();
        initAnim();
        initPicDialog();
        refreshLayout.setRefreshing(true);
        onRefresh();
    }

    private void initView(){
        initTitleBar_1();
        titleView.setText("商家相册");
        actionButton.setText("选择");
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setOnClickListener(this);
        refreshLayout = (RefreshLayout)findViewById(R.id.refresh);
        gridView = (GridView)findViewById(R.id.grid_view);
        actionbar = (RelativeLayout)findViewById(R.id.delete_bar);
        uploadButton = (Button)findViewById(R.id.upload);
        allButton = (TextView)findViewById(R.id.all);
        delButton = (TextView)findViewById(R.id.delete);
        allButton.setOnClickListener(this);
        delButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        refreshLayout.setOnLoadListener(this);
        refreshLayout.setOnRefreshListener(this);
    }

    private void initAnim(){
        translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,1f,
                Animation.RELATIVE_TO_SELF,0f);
        translateAnimation.setDuration(500);
    }

    private void initAdapter(){
        adapter = new CustomBaseAdapter<ShopImgListResponseParam>(dataList,R.layout.activity_shop_pics_item) {
            @Override
            public void bindView(ViewHolder holder, final ShopImgListResponseParam obj) {
               final ImageView imageView = holder.getView(R.id.shop_pic);
                ImageButton imageButton = holder.getView(R.id.select);
                ImageLoader.getInstance().displayImage(obj.getImgurl(),imageView);
                if (selectPicList.contains(obj)){
                    imageButton.setVisibility(View.VISIBLE);
                }else{
                    imageButton.setVisibility(View.GONE);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isAction){
                            if (selectPicList.contains(obj)){
                                selectPicList.remove(obj);
                            }else{
                                selectPicList.add(obj);
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            picList.clear();
                            for (ShopImgListResponseParam shopImgListResponseParam : dataList){
                                picList.add(shopImgListResponseParam.getImgurl());
                            }
                            PhotoActivity.startPhotoActivity(ShopPicsActivity.this,obj.getImgurl(),picList);
                        }
                    }
                });
            }
        };
        gridView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action:
                if (!isAction){
                    isAction = true;
                    actionButton.setText("取消");
                    uploadButton.setVisibility(View.GONE);
                    actionbar.setVisibility(View.VISIBLE);
                    actionbar.startAnimation(translateAnimation);
                }else{
                    actionButton.setText("选择");
                    isAction = false;
                    uploadButton.setVisibility(View.VISIBLE);
                    uploadButton.startAnimation(translateAnimation);
                    actionbar.setVisibility(View.GONE);
                    selectPicList.clear();
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.all:
                selectPicList.clear();
                for (ShopImgListResponseParam shopPic : dataList){
                    selectPicList.add(shopPic);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.delete:
                 deletePic();
                break;
            case R.id.upload:
                picAlertDialog.show();
                break;
        }
    }

    private void deletePic(){
        if (!selectPicList.isEmpty()){
            LinkedList<String> delList = new LinkedList<>();
            for (ShopImgListResponseParam shopImgListResponseParam : selectPicList){
                delList.add(shopImgListResponseParam.getImgId());
            }
            ShopImgDelRequestParam shopImgDelRequestParam = new ShopImgDelRequestParam();
            shopImgDelRequestParam.setImgIds(delList);
            ShopImgDelRequestObject shopImgDelRequestObject = new ShopImgDelRequestObject();
            shopImgDelRequestObject.setParam(shopImgDelRequestParam);
            showProgressDialog("正在删除相册图片");
            httpTool.post(shopImgDelRequestObject, URLConfig.SHOP_DEL_PIC, new HttpTool.HttpCallBack<ResponseBaseObject>() {
                @Override
                public void onSuccess(ResponseBaseObject o) {
                    dismissProgressDialog();
                    selectPicList.clear();
                    refreshLayout.setRefreshing(true);
                    onRefresh();
                }
                @Override
                public void onError(ResponseBaseObject o) {
                    dismissProgressDialog();
                    ToastView.show(o.getErrorMsg());
                }
            });
        }else{
            ToastView.show("请选择要删除的照片");
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
//                    ftpUploadImage(imagePath);
                    //===================================================================================
                    ftpUploadImage(ImageCompressUtil.getCompressImg(imagePath, ShopPicsActivity.this));
                    //====================================================================================
                }else{
                    ToastView.show("照片不存在..");
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
//            ftpUploadImage(picturePath);
            //===================================================================================
            ftpUploadImage(ImageCompressUtil.getCompressImg(picturePath, ShopPicsActivity.this));
            //====================================================================================
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                ToastView.show("选择的图片不存在");
                return;
            }
//            ftpUploadImage(file.getAbsolutePath());
            //===================================================================================
            ftpUploadImage(ImageCompressUtil.getCompressImg(file.getAbsolutePath(), ShopPicsActivity.this));
            //====================================================================================
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
                        String uploadPath = ftpUploadBean.serverPath;
                        uploadPic(uploadPath);
                        //===========================
                        ImageCompressUtil.Delete();
                        //===========================
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

    private void uploadPic(String path){
        showProgressDialog("正在上传图片");
        ShopImgAddDetailsRequestParam shopImgAddDetailsRequestParam = new ShopImgAddDetailsRequestParam();
        shopImgAddDetailsRequestParam.setImgurl(path);
        List<ShopImgAddDetailsRequestParam> imgList = new ArrayList<>();
        imgList.add(shopImgAddDetailsRequestParam);

        ShopImgAddRequestParam shopImgAddRequestParam1 = new ShopImgAddRequestParam();
        shopImgAddRequestParam1.setImgList(imgList);

        ShopImgAddRequestObject shopImgAddRequestObject = new ShopImgAddRequestObject();
        shopImgAddRequestObject.setParam(shopImgAddRequestParam1);

        httpTool.post(shopImgAddRequestObject, URLConfig.SHOP_ADD_PIC, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                dismissProgressDialog();
                refreshLayout.setRefreshing(true);
                onRefresh();
            }
            @Override
            public void onError(ResponseBaseObject o) {
                dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        queryTime = "";
        dataList.clear();
        loadPics();
    }

    @Override
    public void onLoad() {
        page++;
        loadPics();
    }

    private void loadPics(){
        PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
        paginationBaseObject.setFirstQueryTime(queryTime);
        paginationBaseObject.setPage(page);
        paginationBaseObject.setRows(CustomConfig.PAGE_SIZE);
        ShopImgListRequestObject shopImgListRequestObject = new ShopImgListRequestObject();
        shopImgListRequestObject.setPagination(paginationBaseObject);
        httpTool.post(shopImgListRequestObject, URLConfig.SHOP_PICS, new HttpTool.HttpCallBack<ShopImgListResponseObject>() {
            @Override
            public void onSuccess(ShopImgListResponseObject o) {
                refreshLayout.setRefreshing(false);
                refreshLayout.setLoading(false);
                queryTime = o.getFirstQueryTime();
                dataList.addAll(o.getRecordList());
                adapter.notifyDataSetChanged();
                if (dataList.isEmpty()){
                    gridView.setBackgroundResource(R.mipmap.gwc_default);
                }else{
                    gridView.setBackgroundResource(0);
                }
            }
            @Override
            public void onError(ShopImgListResponseObject o) {
                refreshLayout.setRefreshing(false);
                refreshLayout.setLoading(false);
                ToastView.show(o.getErrorMsg());
            }
        });
    }
}
