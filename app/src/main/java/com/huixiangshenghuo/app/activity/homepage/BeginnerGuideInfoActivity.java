package com.huixiangshenghuo.app.activity.homepage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ui.BaseActivity;

/**
 * Created by Administrator on 2017/8/29.
 * <p>
 * 新手指南详情
 */

public class BeginnerGuideInfoActivity extends BaseActivity {


   WebView webView;

   private String type;
   private String content;
   private String link;
   private String title;

   public static void startBeginnerGuideInfoActivity(Context context, String type, String content, String link, String title) {
      Intent intent = new Intent(context, BeginnerGuideInfoActivity.class);
      intent.putExtra("type", type);
      intent.putExtra("content", content);
      intent.putExtra("link", link);
      intent.putExtra("title", title);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_ad_info);
      initTitleBar_1();
      initView();
      type = getIntent().getStringExtra("type");
      content = getIntent().getStringExtra("content");
      link = getIntent().getStringExtra("link");
      title = getIntent().getStringExtra("title");
   }


   public void initView() {
      titleView.setText(title);
      webView = (WebView) findViewById(R.id.webview);
   }

   @Override
   protected void onStart() {
      super.onStart();
      if (TextUtils.equals(type, "0")) {//广告内容
         webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
      } else if (TextUtils.equals(type, "2") || TextUtils.equals(type, "1")) {//外链接
         webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
               super.onPageStarted(view, url, favicon);
               showProgressDialog("正在加载..");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
               super.onPageFinished(view, url);
               dismissProgressDialog();
            }
         });
         WebSettings settings = webView.getSettings();
         settings.setUseWideViewPort(true);//设定支持viewport
         settings.setLoadWithOverviewMode(true);   //自适应屏幕
         settings.setBuiltInZoomControls(true);
         settings.setDisplayZoomControls(false);
         settings.setSupportZoom(false);//设定支持缩放
         webView.loadUrl(link);
      }
   }
}
