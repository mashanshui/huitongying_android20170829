package com.huixiangshenghuo.app.ui.mine;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.huixiangshenghuo.app.comm.ftp.FtpUploadBean;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ImageCompressUtil;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.product.ProductEditRequestObject;
import com.doumee.model.request.product.ProductEditRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.ftp.FtpUploadUtils;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/7.
 */

public class EditShangpinManageActivity extends BaseActivity implements View.OnClickListener {
   public static String IMGURL = "imgurl";
   public static String NAME = "name";
   public static String PRICE = "price";
   public static String STOCKNUM = "stockNum";
   public static String PROID = "proId";
   public static String CATEGORYID = "categoryId";
   public static String CATEGROYNAME = "categroyName";
   /**
    * 销售中 0  已下架 1
    */
   public static String STATE = "state";
   private String imgurl;
   private String name;
   private String price;
   private String stockNum;
   private String proId;
   private String categroyName;
   private String state;

   private ImageView iv_icon;
   private EditText et_name;
   private EditText et_price;
   private EditText et_inventory;
   private TextView tv_classification;
   private BitmapUtils bitmapUtils;
   private RelativeLayout rl_shp_classification;

   private String uploadPath;
   private AlertDialog picAlertDialog;
   private static final int REQUEST_CODE_LOCAL = 3;
   private static final int CAMERA = 4;
   private String imagePath;

   private static final int CLASSIFY_REQUEST_CODE = 1;
   private String categoryId;

   //==================================================
   private RelativeLayout rl_shp_pics;
   //==================================================

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_shangpin);
      initView();
      initPicDialog();
      initBitmapParames();
      initData();
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

      rl_shp_pics = (RelativeLayout) findViewById(R.id.rl_shp_pics);
      rl_shp_pics.setVisibility(View.VISIBLE);
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


      iv_icon.setOnClickListener(this);
      //   tv_classification.setOnClickListener(this);
      rl_shp_classification.setOnClickListener(this);
      rl_shp_pics.setOnClickListener(this);
   }

   private void initData() {
      Intent intent = getIntent();
      imgurl = intent.getStringExtra(IMGURL);
      name = intent.getStringExtra(NAME);
      price = intent.getStringExtra(PRICE);
      stockNum = intent.getStringExtra(STOCKNUM);
      proId = intent.getStringExtra(PROID);
      categoryId = intent.getStringExtra(CATEGORYID);
      categroyName = intent.getStringExtra(CATEGROYNAME);
      state = intent.getStringExtra(STATE);
      bitmapUtils.display(iv_icon, imgurl);
      et_name.setText(name);
      et_price.setText(price);
      et_inventory.setText(stockNum);
      tv_classification.setText(categroyName);
      if (imgurl != null && !imgurl.equals("")) {
         uploadPath = imgurl.substring(imgurl.indexOf("product") + 8);
      }
//      Log.i("uploadPath", uploadPath);
   }

   /**
    * 图片加载
    */
   public void initBitmapParames() {
      bitmapUtils = new BitmapUtils(EditShangpinManageActivity.this);
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.add);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.add);
   }

   private void condition() {
      if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
         Toast.makeText(EditShangpinManageActivity.this, "请填写商品名称", Toast.LENGTH_LONG).show();
         return;
      }
      if (TextUtils.isEmpty(et_price.getText().toString().trim())) {
         Toast.makeText(EditShangpinManageActivity.this, "请输入价格", Toast.LENGTH_LONG).show();
         return;
      }
      if (TextUtils.isEmpty(et_inventory.getText().toString().trim())) {
         Toast.makeText(EditShangpinManageActivity.this, "请输入库存", Toast.LENGTH_LONG).show();
         return;
      }
      if (et_name.getText().toString().trim().equals("选择分类")) {
         Toast.makeText(EditShangpinManageActivity.this, "请选择分类", Toast.LENGTH_LONG).show();
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
//            Intent intent1 = new Intent(EditShangpinManageActivity.this, SeeShangpinCategory.class);
//            startActivityForResult(intent1, CLASSIFY_REQUEST_CODE);
//            break;
         case R.id.rl_shp_classification:
            Intent intent1 = new Intent(EditShangpinManageActivity.this, SeeShangpinCategory.class);
            startActivityForResult(intent1, CLASSIFY_REQUEST_CODE);
            break;
         case R.id.rl_shp_pics:
            AddShangpinPics.startShopPicsActivity(EditShangpinManageActivity.this, proId);
            break;
      }
   }

   private void quest() {

      ProductEditRequestObject productEditRequestObject = new ProductEditRequestObject();
      ProductEditRequestParam productEditRequestParam = new ProductEditRequestParam();
      productEditRequestParam.setProductId(proId);
      productEditRequestParam.setImgurl(uploadPath);
      productEditRequestParam.setStockNum(Integer.parseInt(et_inventory.getText().toString().trim()));
      productEditRequestParam.setName(et_name.getText().toString().trim());
      //   productEditRequestParam.setCategoryId(categoryId);
      productEditRequestParam.setProCateId(categoryId);
      productEditRequestParam.setPrice(Double.parseDouble(et_price.getText().toString().trim()));
      productEditRequestObject.setParam(productEditRequestParam);
      httpTool.post(productEditRequestObject, URLConfig.SHOP_EDIT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
               if (state.equals("0")) {
                  ShopSelesStatic.setShopSeles_static("1");

               } else {
                  ShopSelesStatic.setShopSeles_static("3");
               }
               EditShangpinManageActivity.this.finish();
            }
            @Override
            public void onError(ResponseBaseObject o) {

            }
      });
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
               ftpUploadImage(ImageCompressUtil.getCompressImg(imagePath, EditShangpinManageActivity.this));
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
         //=====================================
         ftpUploadImage(ImageCompressUtil.getCompressImg(picturePath, EditShangpinManageActivity.this));
         //=====================================
      } else {
         File file = new File(selectedImage.getPath());
         if (!file.exists()) {
            ToastView.show("选择的图片不存在");
            return;
         }
//         ftpUploadImage(file.getAbsolutePath());
         //=====================================
         ftpUploadImage(ImageCompressUtil.getCompressImg(file.getAbsolutePath(), EditShangpinManageActivity.this));
         //=====================================
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
                  //==============================
                  ImageCompressUtil.Delete();
                  //==============================
                  dismissProgressDialog();
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
