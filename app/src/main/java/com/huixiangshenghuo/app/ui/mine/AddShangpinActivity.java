package com.huixiangshenghuo.app.ui.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.ftp.FtpUploadBean;
import com.huixiangshenghuo.app.comm.ftp.FtpUploadUtils;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.Dialog;
import com.huixiangshenghuo.app.view.ImageCompressUtil;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.product.ProductAddRequestObject;
import com.doumee.model.request.product.ProductAddRequestParam;
import com.doumee.model.response.product.ProductAddResponseObject;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 * 添加商品 暂时不知道传到哪个文件
 */

public class AddShangpinActivity extends BaseActivity implements View.OnClickListener {
   private ImageView iv_icon;
   private EditText et_name;
   private EditText et_price;
   private EditText et_inventory;
   private TextView tv_classification;
   private RelativeLayout rl_shp_classification;


   private String uploadPath;
   private AlertDialog picAlertDialog;
   private static final int REQUEST_CODE_LOCAL = 3;
   private static final int CAMERA = 4;
   private String imagePath;

   private static final int CLASSIFY_REQUEST_CODE = 1;
   private String categoryId;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_shangpin);
      initView();
      initPicDialog();
   }

   private void initView() {
      initTitleBar_1();
      titleView.setText("添加商品");
      actionButton.setVisibility(View.VISIBLE);
      actionButton.setText("完成");
      actionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            condition();
         }


      });
      iv_icon = (ImageView) findViewById(R.id.iv_shp_icon);
      et_name = (EditText) findViewById(R.id.et_shp_name);
      et_price = (EditText) findViewById(R.id.tv_shp_price);
      et_inventory = (EditText) findViewById(R.id.et_shp_inventory);
      tv_classification = (TextView) findViewById(R.id.tv_shp_classification);
      rl_shp_classification = (RelativeLayout) findViewById(R.id.rl_shp_classification);
      et_price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
      iv_icon.setOnClickListener(this);
      //   tv_classification.setOnClickListener(this);
      rl_shp_classification.setOnClickListener(this);
      et_price.addTextChangedListener(new TextWatcher() {
         public void afterTextChanged(Editable edt) {
            String temp = edt.toString();
            int posDot = temp.indexOf(".");
            if (posDot <= 0) return;
            if (temp.length() - posDot - 1 > 2) {
               edt.delete(posDot + 3, posDot + 4);
            }
         }

         public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
         }

         public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
         }
      });


   }

   private void condition() {
      if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
         Toast.makeText(AddShangpinActivity.this, "请填写商品名称", Toast.LENGTH_LONG).show();
         return;
      }
      if (TextUtils.isEmpty(et_price.getText().toString().trim())) {
         Toast.makeText(AddShangpinActivity.this, "请输入价格", Toast.LENGTH_LONG).show();
         return;
      }
      if (TextUtils.isEmpty(et_inventory.getText().toString().trim())) {
         Toast.makeText(AddShangpinActivity.this, "请输入库存", Toast.LENGTH_LONG).show();
         return;
      }
      if (et_name.getText().toString().trim().equals("选择分类")) {
         Toast.makeText(AddShangpinActivity.this, "请选择分类", Toast.LENGTH_LONG).show();
         return;
      }
      quest();

   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.iv_shp_icon:
            picAlertDialog.show();
            break;
//         case R.id.tv_shp_classification:
//            Intent intent1 = new Intent(AddShangpinActivity.this, SeeShangpinCategory.class);
//            startActivityForResult(intent1, CLASSIFY_REQUEST_CODE);
//            break;
         case R.id.rl_shp_classification:
            Intent intent1 = new Intent(AddShangpinActivity.this, SeeShangpinCategory.class);
            startActivityForResult(intent1, CLASSIFY_REQUEST_CODE);
            break;
      }
   }

   private void quest() {
      ProductAddRequestObject productAddRequestObject = new ProductAddRequestObject();
      ProductAddRequestParam productAddRequestParam = new ProductAddRequestParam();
      productAddRequestParam.setName(et_name.getText().toString().trim());
      productAddRequestParam.setPrice(Double.parseDouble(et_price.getText().toString().trim()));
      productAddRequestParam.setImgurl(uploadPath);
      productAddRequestParam.setStockNum(Integer.parseInt(et_inventory.getText().toString().trim()));
      //   productAddRequestParam.setCategoryId(categoryId);
      productAddRequestParam.setProCateId(categoryId);
      productAddRequestObject.setParam(productAddRequestParam);
      httpTool.post(productAddRequestObject, URLConfig.SHOP_PRODUCT_ADD, new HttpTool.HttpCallBack<ProductAddResponseObject>() {
         @Override
         public void onSuccess(ProductAddResponseObject o) {

//            Toast.makeText(AddShangpinActivity.this, "添加成功", Toast.LENGTH_LONG).show();
            //           finish();
            xc(o.getRecord().getProId());
         }

         @Override
         public void onError(ProductAddResponseObject o) {

         }
      });

   }

   private void xc(final String proId) {
      final Dialog dialog1 = new Dialog(AddShangpinActivity.this);
      dialog1.setTitle("温馨提示");
      dialog1.setMessage("是否添加商品相册");
      dialog1.setConfirmText("确定");
      dialog1.setCancelText("取消");
      dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {

            AddShangpinPics.startShopPicsActivity(AddShangpinActivity.this, proId);
            finish();
         }
      });
      dialog1.setCancelClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            finish();
         }
      });
      dialog1.setCanceledOnTouchOutside(false);
      dialog1.setCancelable(false);
      dialog1.show();
   }

   private void initPicDialog() {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      View view = View.inflate(this, R.layout.activity_send_news_dialog, null);
      Button button1 = (Button) view.findViewById(R.id.item_1);
      Button button2 = (Button) view.findViewById(R.id.item_2);
      Button cancelButton = (Button) view.findViewById(R.id.cancel);
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

   private void fromCamera() {//CAMERA
      if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
         Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         File rootPath = new File(CustomConfig.IMAGE_PATH);
         if (!rootPath.exists()) {
            rootPath.mkdirs();
         }
         File file = new File(rootPath, System.currentTimeMillis() + ".jpg");
         if (!file.exists()) {
            try {
               file.createNewFile();
            } catch (Exception e) {
            }
         }
         imagePath = file.getAbsolutePath();
         Uri uri = Uri.fromFile(file);
         cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
         startActivityForResult(cameraIntent, CAMERA);
      } else {
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
         } else if (requestCode == CAMERA) {
            File file = new File(imagePath);
            if (file.exists()) {
//               ftpUploadImage(imagePath);
               //=====================================
               ftpUploadImage(ImageCompressUtil.getCompressImg(imagePath, AddShangpinActivity.this));
               //=====================================
            } else {
               ToastView.show("照片不存在..");
            }
         } else if (requestCode == CLASSIFY_REQUEST_CODE) {

            try {

               tv_classification.setText(data.getStringExtra("categoryname"));
               categoryId = data.getStringExtra("categoryId");
            } catch (Exception e) {
               // TODO: handle exception
            }
         }

      }
   }


   private void sendPicByUri(Uri selectedImage) {
      String[] filePathColumn = {MediaStore.Images.Media.DATA};
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
//         ftpUploadImage(picturePath);
         //======================================================================================
         ftpUploadImage(ImageCompressUtil.getCompressImg(picturePath, AddShangpinActivity.this));
         //======================================================================================
      } else {
         File file = new File(selectedImage.getPath());
         if (!file.exists()) {
            ToastView.show("选择的图片不存在");
            return;
         }
//         ftpUploadImage(file.getAbsolutePath());
         //=============================================================
         ftpUploadImage(ImageCompressUtil.getCompressImg(file.getAbsolutePath(), AddShangpinActivity.this));
         //=============================================================
      }
   }

   private void ftpUploadImage(final String picturePath) {
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
                  /*dismissProgressDialog();*/
                  uploadPath = ftpUploadBean.serverPath;
                  runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                        String path = "http://" + CustomConfig.FTP_URL + CustomConfig.HTTP_PORT +
                              CustomConfig.SHOP_PRODUCT + uploadPath;
                        ImageLoader.getInstance().displayImage(path, iv_icon);
                     }
                  });
                  dismissProgressDialog();
                  //==============================
                  ImageCompressUtil.Delete();
                  //==============================
               }

               @Override
               public void onError(Throwable e) {
                  dismissProgressDialog();
                  Log.e("图片上传失败", e.getMessage());
               }

               @Override
               public void onNext(String s, String type) {
               }
            });
            ftpUploadUtils.upLoadListFtpUploadBean(fileList, CustomConfig.SHOP_PRODUCT);
         }
      }).start();
   }
}
