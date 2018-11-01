package com.huixiangshenghuo.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/4/13.
 */

public class ImageCompressUtil {
   private static File file2;

   public static void setImage(ImageView imageView, String path, Context context) {
      Glide.with(context).load(path).centerCrop().error(R.mipmap.business_default).into(imageView);
   }


   //压缩图片工具方法
   public static synchronized String getCompressImg(String srcPath, Context context) {
      int orientation = readPictureDegree(srcPath);
      BitmapFactory.Options newOpts = new BitmapFactory.Options();
      //开始读入图片，此时把options.inJustDecodeBounds 设回true了
      newOpts.inJustDecodeBounds = true;
      Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

      newOpts.inJustDecodeBounds = false;
      int w = newOpts.outWidth;
      int h = newOpts.outHeight;
      //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
      float hh = 800f;//这里设置高度为800f
      float ww = 480f;//这里设置宽度为480f
      //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
      int be = 1;//be=1表示不缩放
      if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
         be = (int) (newOpts.outWidth / ww);
      } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
         be = (int) (newOpts.outHeight / hh);
      }
      if (be <= 0)
         be = 1;
      newOpts.inSampleSize = be;//设置缩放比例
      //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
      bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
      if (Math.abs(orientation) > 0) {
         bitmap = rotaingImageView(orientation, bitmap);
      }
      //注意，这个地方改了。只要压缩的文件全部存放到临时文件夹中，重新启动APP全部删除。
//      File file = new File(Environment.getExternalStorageDirectory() + CustomConfig.FILE_PATH + "/shop/","cut.jpg");
////      if (!file.exists()) {
////         try {
////            file.createNewFile();
////         } catch (IOException e) {
////            e.printStackTrace();
////         }
////      }
//      try {
//         FileOutputStream out = new FileOutputStream(file);
//         if (bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)) {
//            out.flush();
//            out.close();
//         }
//      } catch (FileNotFoundException e) {
//         e.printStackTrace();
//      } catch (IOException e) {
//         e.printStackTrace();
//      }
      String path = CustomConfig.SHOP_IMAGE_PATH_XC;
      File f = new File(path);
      if (!f.exists()) {  //如果文件夹不存在，创建文件夹
         f.mkdirs();
      }
//      file2 = f;
      File file = new File(path + "cut.png");
      try {
         file2 = file;
      } catch (Exception e) {
         e.printStackTrace();
      }
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      try {
         //f.createNewFile();
//         FileOutputStream out = new FileOutputStream(file);
//         bitmap.compress(Bitmap.CompressFormat.PNG,75, out);
//         out.flush();
//         out.close();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         int options = 80;//个人喜欢从80开始,
         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
         while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
         }
         try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return file.getAbsolutePath();
   }


   public static synchronized Drawable byteToDrawable(String icon) {

      byte[] img = Base64.decode(icon.getBytes(), Base64.DEFAULT);
      Bitmap bitmap;
      if (img != null) {


         bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
         @SuppressWarnings("deprecation")
         Drawable drawable = new BitmapDrawable(bitmap);

         return drawable;
      }
      return null;

   }

   public static synchronized String drawableToByte(Drawable drawable) {
      if (drawable != null) {
         Bitmap bitmap = Bitmap
               .createBitmap(
                     drawable.getIntrinsicWidth(),
                     drawable.getIntrinsicHeight(),
                     drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                           : Bitmap.Config.RGB_565);
         Canvas canvas = new Canvas(bitmap);
         drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
               drawable.getIntrinsicHeight());
         drawable.draw(canvas);
         int size = bitmap.getWidth() * bitmap.getHeight() * 4;

         // 创建一个字节数组输出流,流的大小为size
         ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
         // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
         bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
         // 将字节数组输出流转化为字节数组byte[]
         byte[] imagedata = baos.toByteArray();

         String icon = Base64.encodeToString(imagedata, Base64.DEFAULT);
         return icon;
      }
      return null;
   }


   public static int readPictureDegree(String path) {
      int degree = 0;
      try {
         ExifInterface exifInterface = new ExifInterface(path);
         int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
         switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
               degree = 90;
               break;
            case ExifInterface.ORIENTATION_ROTATE_180:
               degree = 180;
               break;
            case ExifInterface.ORIENTATION_ROTATE_270:
               degree = 270;
               break;
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return degree;
   }

   public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
      //旋转图片 动作
      Matrix matrix = new Matrix();
      matrix.postRotate(angle);
      System.out.println("angle2=" + angle);
      // 创建新的图片
      Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
            bitmap.getWidth(), bitmap.getHeight(), matrix, true);
      return resizedBitmap;
   }

   public static void setBackgroundImage(final ImageView imageView, int drawable) {
      imageView.setBackgroundResource(drawable);
   }

   public static void Delete() {
      try {
         if (file2.exists()) {  //如果文件夹不存在，创建文件夹
            file2.delete();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
