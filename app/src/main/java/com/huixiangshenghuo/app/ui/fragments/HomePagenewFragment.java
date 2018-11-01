package com.huixiangshenghuo.app.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.activity.homemall.GoodsListActivity;
import com.huixiangshenghuo.app.activity.homepage.BeginnerGuideActivity;
import com.huixiangshenghuo.app.activity.homepage.CertificationCenterActivity;
import com.huixiangshenghuo.app.activity.homepage.InvitingFriendsActivity;
import com.huixiangshenghuo.app.activity.homepage.ProductsDetailsNewActivity;
import com.huixiangshenghuo.app.activity.homepage.RecommendedManagementActivity;
import com.huixiangshenghuo.app.activity.integralmall.IntegralGoodsListActivity;
import com.huixiangshenghuo.app.adapter.homepage.HomePageNoticeAdapter;
import com.huixiangshenghuo.app.adapter.homepage.RecommendAdapter;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.ui.home.NocationActivity;
import com.huixiangshenghuo.app.ui.home.NoticesInfoActivity;
import com.huixiangshenghuo.app.ui.home.ZhuanInfoActivity;
import com.huixiangshenghuo.app.view.BannerViewImageHolder;
import com.huixiangshenghuo.app.view.MyGridView;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.RefreshScrollviewLayout;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.PaginationBaseObject;
import com.doumee.model.request.ad.AdRequestObject;
import com.doumee.model.request.ad.AdRequestParam;
import com.doumee.model.request.notices.NoticesListRequestObject;
import com.doumee.model.request.notices.NoticesListRequestParam;
import com.doumee.model.request.product.ProductListRequestObject;
import com.doumee.model.request.product.ProductListRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;
import com.doumee.model.request.userInfo.MemberListByNamesRequestObject;
import com.doumee.model.request.userInfo.MemberListByNamesRequestParam;
import com.doumee.model.response.ad.AdListResponseObject;
import com.doumee.model.response.ad.AdListResponseParam;
import com.doumee.model.response.notices.NoticesListResponseObject;
import com.doumee.model.response.notices.NoticesListResponseParam;
import com.doumee.model.response.product.ProductListResponseObject;
import com.doumee.model.response.product.ProductListResponseParam;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.MemberListByNamesResponseObject;
import com.doumee.model.response.userinfo.MemberListByNamesResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.lidroid.xutils.BitmapUtils;
import com.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/26.
 * 首页
 */

public class HomePagenewFragment extends Fragment implements View.OnClickListener, RefreshScrollviewLayout.OnRefreshListener,
      RefreshScrollviewLayout.OnLoadListener {
   private static final String ARG_PARAM1 = "param1";
   private static final String ARG_PARAM2 = "param2";
   private HttpTool httpTool;
   BitmapUtils bitmapUtils;

   //广告
   ConvenientBanner adsLyt;
   private List<AdListResponseParam> ads;
   //分享
   private Button bt_share;
   //新手指南
   private LinearLayout ll_beginner_guide;
   //实名认证
   private LinearLayout ll_mine_real;
   //推荐管理
   private LinearLayout ll_recommended_management;
   //扫一扫
   private LinearLayout ll_saoma;
   //公告通知
   private MyListView listview;
   private HomePageNoticeAdapter adapter_sysnotice;//适配器
   //数据源
   private ArrayList<NoticesListResponseParam> arrlist_sysnotice = new ArrayList<NoticesListResponseParam>();
   private LinearLayout ll_announcement_inform;

   //会员类型
   private String vip_type;

   //猜你喜欢
//   private HeaderGridView HeaderGridView;
   private MyGridView gridView;
   private RecommendAdapter re_adapter;//适配器
   private ArrayList<ProductListResponseParam> arrlist = new ArrayList<ProductListResponseParam>();//数据源
   private int page = 1;//设置页面
   private String firstQueryTime;//获取当前时间

   //刷新
   RefreshScrollviewLayout refreshLayout;
   View view;

   public static HomePagenewFragment newInstance(String param1, String param2) {
      HomePagenewFragment fragment = new HomePagenewFragment();
      Bundle args = new Bundle();
      args.putString(ARG_PARAM1, param1);
      args.putString(ARG_PARAM2, param2);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      httpTool = HttpTool.newInstance(getActivity());

   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      view = inflater.inflate(R.layout.fragment_home_page_new, container, false);
      initview();
      initData();
      initBitmapParames();
      loadUser();
      loadData();
      requestAds();
      refresh();
      recommendrequest();

      return view;
   }

   private void initview() {

      bt_share = (Button) view.findViewById(R.id.bt_share_home_pager_new);


      gridView = (MyGridView) view.findViewById(R.id.gv_recommend_home_page_new);
      refreshLayout = (RefreshScrollviewLayout) view.findViewById(R.id.rl_sx_home_pager_new);
      bt_share.setOnClickListener(this);
      adsLyt = (ConvenientBanner) view.findViewById(R.id.fh_ads_lyt);
      ll_beginner_guide = (LinearLayout) view.findViewById(R.id.ll_beginner_guide_home_pager_new);
      ll_mine_real = (LinearLayout) view.findViewById(R.id.ll_mine_real_home_pager_new);
      ll_recommended_management = (LinearLayout) view.findViewById(R.id.ll_recommended_management_home_page_new);
      ll_saoma = (LinearLayout) view.findViewById(R.id.ll_saoma_home_page_new);
      ll_announcement_inform = (LinearLayout) view.findViewById(R.id.ll_announcement_inform);
      listview = (MyListView) view.findViewById(R.id.lv_notice_home_pager_new);
//      listview.requestFocus();
      gridView.requestFocus();
      //头部
//      View headView = View.inflate(getActivity(), R.layout.fragment_home_page_top_new, null);
//      adsLyt = (ConvenientBanner) headView.findViewById(R.id.fh_ads_lyt);
//      ll_beginner_guide = (LinearLayout) headView.findViewById(R.id.ll_beginner_guide_home_pager_new);
//      ll_mine_real = (LinearLayout) headView.findViewById(R.id.ll_mine_real_home_pager_new);
//      ll_recommended_management = (LinearLayout) headView.findViewById(R.id.ll_recommended_management_home_page_new);
//      ll_saoma = (LinearLayout) headView.findViewById(R.id.ll_saoma_home_page_new);
//      ll_announcement_inform = (LinearLayout) headView.findViewById(R.id.ll_announcement_inform);
//      listview = (MyListView) headView.findViewById(R.id.lv_notice_home_pager_new);


      ll_beginner_guide.setOnClickListener(this);
      ll_mine_real.setOnClickListener(this);
      ll_recommended_management.setOnClickListener(this);
      ll_saoma.setOnClickListener(this);
      ll_announcement_inform.setOnClickListener(this);
//      HeaderGridView.addHeaderView(headView);


   }

   /**
    * 图片加载
    */
   public void initBitmapParames() {
      bitmapUtils = new BitmapUtils(getActivity());
      // 设置加载失败图片
      bitmapUtils.configDefaultLoadFailedImage(R.mipmap.business_default);
      // 设置没有加载成功图片
      bitmapUtils.configDefaultLoadingImage(R.mipmap.business_default);
      re_adapter = new RecommendAdapter(arrlist, getActivity(), bitmapUtils);
      gridView.setAdapter(re_adapter);
   }

   private void initData() {


      adapter_sysnotice = new HomePageNoticeAdapter(arrlist_sysnotice, getActivity());
      listview.setAdapter(adapter_sysnotice);
      listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NoticesListResponseParam info = arrlist_sysnotice.get(position);
            NoticesInfoActivity.startNoticesInfoActivity(getActivity(), info.getTitle(), info.getContent(), info.getCreateDate());
         }
      });

      gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            ProductsDetailsActivity.startProductsDetailsActivity(getActivity(), arrlist.get(position).getProId());
            //HeaderGridView  position 要向前移动2位
            try {
//               ProductsDetailsActivity.startProductsDetailsActivity(getActivity(), arrlist.get(position - 2).getProId());
               ProductsDetailsNewActivity.startProductsDetailsNewActivity(getActivity(), arrlist.get(position).getProId());
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });

   }

   private void refresh() {
      // TODO Auto-generated method stub
      refreshLayout.setLoading(false);
      refreshLayout.setRefreshing(false);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setOnLoadListener(this);

   }

   /**
    * 广告
    */
   private void requestAds() {
      AdRequestParam adRequestParam = new AdRequestParam();
      adRequestParam.setPosition("0");//广告位置 0首页广告 1商城 2商圈页广告
      AdRequestObject adRequestObject = new AdRequestObject();
      adRequestObject.setParam(adRequestParam);
      httpTool.post(adRequestObject, URLConfig.AD_LIST, new HttpTool.HttpCallBack<AdListResponseObject>() {
         @Override
         public void onSuccess(AdListResponseObject o) {
//            refreshLayout.setLoading(false);
//            refreshLayout.setRefreshing(false);
            ads = o.getAdLst();
            if (ads != null) {

               adsLyt.setPages(new CBViewHolderCreator<BannerViewImageHolder>() {
                  @Override
                  public BannerViewImageHolder createHolder() {
                     return new BannerViewImageHolder();
                  }
               }, ads).setPageIndicator(new int[]{R.drawable.bg_dot_gray, R.drawable.bg_dot_blue});
            }

            if (o.getAdLst().size() <= 0) {
               adsLyt.setVisibility(View.GONE);
            } else {
               adsLyt.setVisibility(View.VISIBLE);
            }

         }

         @Override
         public void onError(AdListResponseObject o) {
//            refreshLayout.setLoading(false);
//            refreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });
   }

   /**
    * 系统通知
    */
   private void loadData() {


      NoticesListRequestParam noticesListRequestParam = new NoticesListRequestParam();
      PaginationBaseObject paginationBaseObject = new PaginationBaseObject();
      paginationBaseObject.setPage(1);
      paginationBaseObject.setRows(4);
      paginationBaseObject.setFirstQueryTime("");
      NoticesListRequestObject noticesListRequestObject = new NoticesListRequestObject();
      noticesListRequestObject.setParam(noticesListRequestParam);
      noticesListRequestObject.setPagination(paginationBaseObject);
      httpTool.post(noticesListRequestObject, URLConfig.SYS_MESSAGE, new HttpTool.HttpCallBack<NoticesListResponseObject>() {
         @Override
         public void onSuccess(NoticesListResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
//                  refreshLayout.setLoading(false);
//                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getRecordList() != null) {
//                     if (page == 1 && !arrlist.isEmpty()) {
//                        arrlist_sysnotice.clear();
//                     }
                     arrlist_sysnotice.clear();

                     arrlist_sysnotice.addAll(o.getRecordList());
                     adapter_sysnotice.notifyDataSetChanged();
                  }

               }
            }
         }

         @Override
         public void onError(NoticesListResponseObject o) {
//            refreshLayout.setRefreshing(false);
//            refreshLayout.setLoading(false);
            ToastView.show(o.getErrorMsg());
         }
      });


   }


   /**
    * 猜你喜欢 商品排序
    */
   private void recommendrequest() {
      // TODO Auto-generated method stub
      ProductListRequestObject object = new ProductListRequestObject();
      object.setParam(new ProductListRequestParam());
      object.getParam().setType("0");
      object.getParam().setStatus("0");//商品类型：0 销售中 1已下架
      object.getParam().setSortType("2");//排序类型：0综合排序、1销量升序 2销量降序、3价格升序 4价格降序 5人气升序 6人气倒序 7时间倒序
      object.setPagination(new PaginationBaseObject());
      object.getPagination().setPage(page);//第一页
      object.getPagination().setRows(CustomConfig.PAGE_SIZE);//每一页10行

      if (page == 1) {
         object.getPagination().setFirstQueryTime("");
      } else if (page != 1) {
         object.getPagination().setFirstQueryTime(firstQueryTime);
      }

      httpTool.post(object, URLConfig.SHOP_QUERY, new HttpTool.HttpCallBack<ProductListResponseObject>() {
         @Override
         public void onSuccess(ProductListResponseObject o) {
            if (o.getErrorCode().equals("00000")) {
               if (o.getErrorMsg().equals("success")) {
                  refreshLayout.setLoading(false);
                  refreshLayout.setRefreshing(false);
                  if (o != null && o.getProList() != null) {
                     /**
                      * 分页
                      */
                     if (page == 1 && !arrlist.isEmpty()) {
                        //清空
                        arrlist.clear();
                     }

                     firstQueryTime = o.getFirstQueryTime();
                     arrlist.addAll(o.getProList());

                     re_adapter.notifyDataSetChanged();
                  }

               }
            }


         }

         @Override
         public void onError(ProductListResponseObject o) {
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), o.getErrorMsg(), Toast.LENGTH_SHORT).show();
         }
      });


   }




   @Override
   public void onResume() {
      super.onResume();
      adsLyt.startTurning(5000);

   }

   @Override
   public void onPause() {
      super.onPause();
      adsLyt.stopTurning();

   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.bt_share_home_pager_new:
            if (vip_type.equals("0")) {//会员类型 0普通会员 1VIP会员
               ToastView.show("成为VIP会员才能邀请好友");
            } else {
               startActivity(new Intent(getActivity(), InvitingFriendsActivity.class));
            }

            break;
         case R.id.ll_beginner_guide_home_pager_new:
            startActivity(new Intent(getActivity(), BeginnerGuideActivity.class));
            break;
         case R.id.ll_mine_real_home_pager_new:
//            startActivity(new Intent(getActivity(), CertificationCenterActivity.class));
            CertificationCenterActivity.startCertificationCenterActivity(getActivity(), vip_type);
            break;
         case R.id.ll_recommended_management_home_page_new:
//            startActivity(new Intent(getActivity(), RecommendedManagementActivity.class));
            IntegralGoodsListActivity.startGoodsListActivity(getActivity(), "", "积分商城");
            break;
         case R.id.ll_saoma_home_page_new:
            Intent intent = new Intent(this.getActivity(), CaptureActivity.class);
            startActivityForResult(intent, 1);
            break;
         case R.id.ll_announcement_inform:
//            startActivity(new Intent(getActivity(), AnnouncementInformActivity.class));
            NocationActivity.startNocationActivity(getActivity());
            break;
         default:
            break;
      }
   }

   //下拉
   @Override
   public void onRefresh() {
      refreshLayout.setRefreshing(true);


      refreshLayout.postDelayed(new Runnable() {

         @Override
         public void run() {
            // 更新数据
            // 更新完后调用该方法结束刷新

            requestAds();
            page = 1;

            loadData();

            loadUser();
            recommendrequest();
         }
      }, 2000);

   }

   @Override
   public void onLoad() {


      refreshLayout.setLoading(true);


      refreshLayout.postDelayed(new Runnable() {

         @Override
         public void run() {
            // 更新数据
            // 更新完后调用该方法结束刷新
            page++;

            recommendrequest();
         }
      }, 2000);

   }




   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode == Activity.RESULT_OK) {
         if (requestCode == 1) {
            String result = data.getStringExtra("data");
            if (result.startsWith("http")) {
               Uri uri = Uri.parse(result);
               Intent intent = new Intent(Intent.ACTION_VIEW, uri);
               startActivity(intent);
            } else {
               submit(result);
            }
         }
      }
   }

   private void submit(String username) {
      showProgressDialog("正在加载..");
      MemberListByNamesRequestParam memberListByNamesRequestParam = new MemberListByNamesRequestParam();
      List<String> list = new LinkedList<>();
      list.add(username);
      memberListByNamesRequestParam.setLoginNames(list);
      MemberListByNamesRequestObject memberListByNamesRequestObject = new MemberListByNamesRequestObject();
      memberListByNamesRequestObject.setParam(memberListByNamesRequestParam);

      httpTool.post(memberListByNamesRequestObject, URLConfig.USER_LIST_BY_LOGIN_NAME, new HttpTool.HttpCallBack<MemberListByNamesResponseObject>() {
         @Override
         public void onSuccess(MemberListByNamesResponseObject o) {
            dismissProgressDialog();
            List<MemberListByNamesResponseParam> list = o.getRecordList();
            if (!list.isEmpty()) {
               ZhuanInfoActivity.startZhuanInfoActivity(getActivity(), list.get(0));
            } else {
               ToastView.show("账户不存在");
            }
         }

         @Override
         public void onError(MemberListByNamesResponseObject o) {
            dismissProgressDialog();
            ToastView.show(o.getErrorMsg());
         }
      });
   }

   private ProgressDialog progressDialog;

   protected void showProgressDialog(String msg) {
      progressDialog = ProgressDialog.show(this.getActivity(), "", msg);
      progressDialog.setCancelable(true);
      progressDialog.show();
   }

   protected void dismissProgressDialog() {
      if (null != progressDialog) {
         progressDialog.dismiss();
      }
   }

   private void loadUser() {
      refreshLayout.setRefreshing(true);
      UserInfoResponseParam userInfoResponseParam = SaveObjectTool.openUserInfoResponseParam();
      if (null == userInfoResponseParam) {
         userInfoResponseParam = new UserInfoResponseParam();
         userInfoResponseParam.setMemberId("");
      }
      MemberInfoParamObject memberInfoParamObject = new MemberInfoParamObject();
      memberInfoParamObject.setMemberId(userInfoResponseParam.getMemberId());
      MemberInfoRequestObject memberInfoRequestObject = new MemberInfoRequestObject();
      memberInfoRequestObject.setParam(memberInfoParamObject);

      httpTool.post(memberInfoRequestObject, URLConfig.USER_INFO, new HttpTool.HttpCallBack<MemberInfoResponseObject>() {
         @Override
         public void onSuccess(MemberInfoResponseObject o) {
//            refreshLayout.setLoading(false);
//            refreshLayout.setRefreshing(false);
            vip_type = o.getMember().getType();

         }

         @Override
         public void onError(MemberInfoResponseObject o) {
//            refreshLayout.setLoading(false);
//            refreshLayout.setRefreshing(false);
            ToastView.show(o.getErrorMsg());
         }
      });
   }
}
