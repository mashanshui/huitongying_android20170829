package com.huixiangshenghuo.app.ui.fragments.homepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.homepage.ReviewDetailsAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.RefreshLayout;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.comment.PrdCommentListRequestObject;
import com.doumee.model.request.comment.PrdCommentListRequestParam;
import com.doumee.model.response.comment.PrdCommentListResponseObject;
import com.doumee.model.response.comment.PrdCommentListResponseParam;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/31.
 * 评论详情
 */

public class ReviewDetailsFragment extends Fragment implements RefreshLayout.ILoadListener, RefreshLayout.OnRefreshListener {
   public static final String ARG_PARAM1 = "productsId";
   private String productsId;
   private HttpTool httpTool;

   View view;
   RefreshLayout refreshLayout;
   private MyListView lv_review_details;
   private ReviewDetailsAdapter re_adapter;//适配器
   private ArrayList<PrdCommentListResponseParam> arrlist = new ArrayList<PrdCommentListResponseParam>();//数据源
   private int page = 1;//设置页面
   private String firstQueryTime;//获取当前时间

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      httpTool = HttpTool.newInstance(getActivity());
      if (getArguments() != null) {
         productsId = getArguments().getString(ARG_PARAM1);
      }
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

      view = inflater.inflate(R.layout.fragment_review_details, container, false);
      initview();
      initData();
      recommendrequest();
      return view;
   }

   private void initview() {
      refreshLayout = (RefreshLayout) view.findViewById(R.id.rl_sx_review_details);
      lv_review_details = (MyListView) view.findViewById(R.id.lv_review_details);
   }


   private void initData() {
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setOnLoadListener(this);

      re_adapter = new ReviewDetailsAdapter(arrlist, getActivity());
      lv_review_details.setAdapter(re_adapter);

   }

   /**
    * 猜你喜欢 商品排序
    */
   private void recommendrequest() {
      // TODO Auto-generated method stub
      PrdCommentListRequestObject object = new PrdCommentListRequestObject();
      object.setParam(new PrdCommentListRequestParam());
      object.getParam().setPrdId(productsId);
      object.getParam().setScoreLevel("0");//0全部1好评2中评3差评
      object.setPagination(new PaginationBaseObject());
      object.getPagination().setPage(page);//第一页
      object.getPagination().setRows(CustomConfig.PAGE_SIZE);//每一页10行

      if (page == 1) {
         object.getPagination().setFirstQueryTime("");
      } else if (page != 1) {
         object.getPagination().setFirstQueryTime(firstQueryTime);
      }

      httpTool.post(object, URLConfig.PRODUCT_REVIEWS, new HttpTool.HttpCallBack<PrdCommentListResponseObject>() {
         @Override
         public void onSuccess(PrdCommentListResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getRecordList() != null) {
                     /**
                      * 分页
                      */
                     if (page == 1 && !arrlist.isEmpty()) {
                        //清空
                        arrlist.clear();
                     }

                     firstQueryTime = o.getFirstQueryTime();
                     arrlist.addAll(o.getRecordList());

                     re_adapter.notifyDataSetChanged();
//                     if (o.getRecordList().isEmpty()) {
//                        lv_review_details.setBackgroundResource(R.mipmap.gwc_default);
//                     } else {
//                        lv_review_details.setBackgroundResource(0);
//                     }
                  }

               }
            }


         }

         @Override
         public void onError(PrdCommentListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });


   }


   @Override
   public void onRefresh() {
      refreshLayout.setRefreshing(true);
      page = 1;
      recommendrequest();
   }

   @Override
   public void onLoad() {
      refreshLayout.setRefreshing(true);
      page++;
      recommendrequest();
   }
}
