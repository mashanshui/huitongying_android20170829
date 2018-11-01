package com.huixiangshenghuo.app.comm.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class StringUtils {
   public static boolean isEmpty(String str) {
      if (str == null
            || str.length() == 0
            || str.equalsIgnoreCase("null")
            || str.isEmpty()
            || str.trim().equals("")) {
         return true;
      } else {
         return false;
      }
   }

   public static String getEditString(EditText view) {
      return view.getText().toString().trim();
   }

   public static String showPhone(String phone) {
      if (phone.length() < 9) {
         return phone;
      }
      return phone.substring(0, 3) + "****" + phone.substring(7);
   }

   public static boolean isCaptcha(String verifyCode) {
      return verifyCode != null && verifyCode.matches("^\\d{4}$");
   }

   public static String avoidNull(String str) {
      return str == null ? "" : str;
   }

   public static boolean noEmptyList(List list) {
      if (list == null || list.isEmpty() || list.size() == 0) return false;
      return true;
   }

   public static boolean isNull(Object... obj) {
      for (Object o : obj) {
         if (o == null) return true;
      }
      return false;
   }

   public static boolean isNull2(Object... obj) {
      for (Object o : obj) {
         if (o == null || o.equals("")) return true;
      }
      return false;
   }

   public static String arrayToString(String[] array) {
      if (array != null) {
         StringBuffer sb = new StringBuffer();
         for (String s : array) {
            sb.append(s);
         }
         return sb.toString();
      }
      return "";
   }


   public static void setWordColor(String string, int start, int end, TextView view, int colorStateList) {

      //设置局部字体颜色变化
      SpannableStringBuilder builder = new SpannableStringBuilder(string);
      //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
      ForegroundColorSpan redSpan = new ForegroundColorSpan(colorStateList);
      builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      view.setText(builder);
   }

   public static SpannableStringBuilder getWordColor(String string, int start, int end, int colorStateList) {

      //设置局部字体颜色变化
      SpannableStringBuilder builder = new SpannableStringBuilder(string);
      //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
      ForegroundColorSpan redSpan = new ForegroundColorSpan(colorStateList);
      builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      return builder;
   }

   public static String subStringAvoidNull(String str, int start, int end) {
      try {
         return str.substring(start, end);
      } catch (Exception e) {
         return str;
      }
   }

}
