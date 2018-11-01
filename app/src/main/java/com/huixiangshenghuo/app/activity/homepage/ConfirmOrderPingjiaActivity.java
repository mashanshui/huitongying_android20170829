package com.huixiangshenghuo.app.activity.homepage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.doumee.model.request.comment.ComInfoAddRequestParam;
import com.doumee.model.request.comment.CommentAddRequestObject;
import com.doumee.model.request.comment.CommentAddRequestParam;
import com.doumee.model.request.goodsorder.GoodsOrderInfoRequestObject;
import com.doumee.model.request.goodsorder.GoodsOrderInfoRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.goodsorder.GoodsDetailsResponeParam;
import com.doumee.model.response.goodsorder.GoodsOrderInfoResponseObject;
import com.doumee.model.response.goodsorder.GoodsOrderInfoResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.ToastView;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/15.
 * 商品评价
 */

public class ConfirmOrderPingjiaActivity extends BaseActivity {


   LinearLayout listView;


   RatingBar shopScoreView;

   EditText shopContentView;

   private HttpTool httpTool;
   private String orderNo;
   private ArrayList<ComInfoAddInfo> comInfoList;
   // private CustomBaseAdapter<ComInfoAddInfo> adapter;

   public static void startConfirmOrderPingjiaActivity(Context context, String orderNo) {
      Intent intent = new Intent(context, ConfirmOrderPingjiaActivity.class);
      intent.putExtra("order", orderNo);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_pingjia);
      orderNo = getIntent().getStringExtra("order");
      httpTool = HttpTool.newInstance(this);
      comInfoList = new ArrayList<>();
      initView();
      loadData();
   }

   public void initView() {
      initTitleBar_1();
      titleView.setText("评价");
      actionButton.setVisibility(View.VISIBLE);
      actionButton.setText("提交");
      actionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            submitData();
         }
      });

      initdate();
   }

   private void initdate() {

      listView = (LinearLayout) findViewById(R.id.goodsList);
      shopScoreView = (RatingBar) findViewById(R.id.shop_score);
      shopContentView = (EditText) findViewById(R.id.shop_content);

   }

   private void initGoodsAdapter() {
      for (final ComInfoAddInfo comInfoAddInfo : comInfoList) {
         View view = View.inflate(this, R.layout.activity_pingjia_item, null);
         TextView goodsNameView = (TextView) view.findViewById(R.id.goods_name);
         RatingBar goodsScoreView = (RatingBar) view.findViewById(R.id.goods_score);
         EditText goodsContentView = (EditText) view.findViewById(R.id.goods_content);
         goodsNameView.setText(comInfoAddInfo.name);
         goodsScoreView.setRating(comInfoAddInfo.iscore);
         goodsScoreView.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
               comInfoAddInfo.iscore = rating;
            }
         });
         goodsContentView.setText(comInfoAddInfo.icontent);
         goodsContentView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
               comInfoAddInfo.icontent = s.toString().trim();
            }
         });

         listView.addView(view);
      }
   }


   private void loadData() {
      showProgressDialog("正在加载..");
      GoodsOrderInfoRequestParam goodsOrderInfoRequestParam = new GoodsOrderInfoRequestParam();
      goodsOrderInfoRequestParam.setOrderId(Long.parseLong(orderNo));
      GoodsOrderInfoRequestObject goodsOrderInfoRequestObject = new GoodsOrderInfoRequestObject();
      goodsOrderInfoRequestObject.setParam(goodsOrderInfoRequestParam);

      httpTool.post(goodsOrderInfoRequestObject, URLConfig.ORDER_INFO, new HttpTool.HttpCallBack<GoodsOrderInfoResponseObject>() {
         @Override
         public void onSuccess(GoodsOrderInfoResponseObject o) {
            dismissProgressDialog();
            GoodsOrderInfoResponseParam goodsOrderInfoResponseParam = o.getRecord();
            List<GoodsDetailsResponeParam> goodsList = goodsOrderInfoResponseParam.getGoodsList();
            for (GoodsDetailsResponeParam goods : goodsList) {
               ComInfoAddInfo comInfoAddInfo = new ComInfoAddInfo();
               comInfoAddInfo.itemId = goods.getProId();
               comInfoAddInfo.icontent = "";
               comInfoAddInfo.iscore = 0f;
               comInfoAddInfo.name = goods.getProName();
               comInfoList.add(comInfoAddInfo);
            }
            initGoodsAdapter();
         }

         @Override
         public void onError(GoodsOrderInfoResponseObject o) {
            dismissProgressDialog();
            ToastView.show(o.getErrorMsg());
         }
      });
   }


   private void submitData() {

      String content = shopContentView.getText().toString().trim();
      Float score = shopScoreView.getRating();

//      if (score.intValue() == 0){
//         ToastView.show("请选择您的评分");
//         return;
//      }

//      if (TextUtils.isEmpty(content)){
//         ToastView.show("请输入您的评价");
//         return;
//      }

      CommentAddRequestParam commentAddRequestParam = new CommentAddRequestParam();
      commentAddRequestParam.setOrderId(Long.parseLong(orderNo));
//      commentAddRequestParam.setContent(content);
//      commentAddRequestParam.setScore(score.intValue());
      commentAddRequestParam.setContent("0");
      commentAddRequestParam.setScore(1);

      List<ComInfoAddRequestParam> itemList = new LinkedList<>();
      for (ComInfoAddInfo infoAddInfo : comInfoList) {
         if (null != infoAddInfo.iscore && infoAddInfo.iscore.intValue() > 0) {
            ComInfoAddRequestParam comInfoAddRequestParam = new ComInfoAddRequestParam();
            comInfoAddRequestParam.setItemId(infoAddInfo.itemId);
            comInfoAddRequestParam.setIscore(infoAddInfo.iscore.intValue());
            comInfoAddRequestParam.setIcontent(infoAddInfo.icontent);
            itemList.add(comInfoAddRequestParam);
         }
      }
      commentAddRequestParam.setItemList(itemList);
      CommentAddRequestObject commentAddRequestObject = new CommentAddRequestObject();
      commentAddRequestObject.setParam(commentAddRequestParam);

      httpTool.post(commentAddRequestObject, URLConfig.ORDER_COMMENT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject o) {
            ToastView.show("评论成功");
            Intent intent = new Intent();
            intent.putExtra("order", orderNo);
            setResult(RESULT_OK, intent);
            finish();
         }

         @Override
         public void onError(ResponseBaseObject o) {
            ToastView.show(o.getErrorMsg());
         }
      });
   }

   private class ComInfoAddInfo {
      private String itemId;
      private String icontent;
      private Float iscore;
      private String name;
   }

}
