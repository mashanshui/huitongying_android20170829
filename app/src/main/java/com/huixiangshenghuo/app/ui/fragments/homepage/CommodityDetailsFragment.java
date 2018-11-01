package com.huixiangshenghuo.app.ui.fragments.homepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.comm.http.HttpTool;

/**
 * Created by Administrator on 2017/5/31.
 * 商品详情
 */

public class CommodityDetailsFragment extends Fragment {
   public static final String ARG_PARAM1 = "info";
   private String info;
   private HttpTool httpTool;
   private WebView webView;

   View view;

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      httpTool = HttpTool.newInstance(getActivity());
      if (getArguments() != null) {
         info = getArguments().getString(ARG_PARAM1);
      }
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

      view = inflater.inflate(R.layout.fragment_commodity_details, container, false);
      initview();
      return view;
   }

   private void initview() {
      webView = (WebView) view.findViewById(R.id.webview);


      if (info.equals("")) {

      } else {

         webView.loadDataWithBaseURL(null, info, "text/html", "utf-8", null);//内容
      }


//      //外链接
//         webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//               super.onPageStarted(view, url, favicon);
//
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//               super.onPageFinished(view, url);
//
//            }
//         });
//         WebSettings settings = webView.getSettings();
//         settings.setUseWideViewPort(true);//设定支持viewport
//         settings.setLoadWithOverviewMode(true);   //自适应屏幕
//         settings.setBuiltInZoomControls(true);
//         settings.setDisplayZoomControls(false);
//         settings.setSupportZoom(false);//设定支持缩放
//         webView.loadUrl(info);



   }
}
