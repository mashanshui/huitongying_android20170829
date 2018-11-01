package com.huixiangshenghuo.app.ui.mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.swipemenulistview.SwipeMenu;
import com.huixiangshenghuo.app.swipemenulistview.SwipeMenuCreator;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.Dialog;
import com.doumee.model.request.shopCate.CateAddRequestObject;
import com.doumee.model.request.shopCate.CateAddRequestParam;
import com.doumee.model.request.shopCate.CateDelRequestObject;
import com.doumee.model.request.shopCate.CateDelRequestParam;
import com.doumee.model.request.shopCate.CateEditRequestObject;
import com.doumee.model.request.shopCate.CateEditRequestParam;
import com.doumee.model.request.shopCate.CateListRequestObject;
import com.doumee.model.request.shopCate.CateListRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.shopCate.CateListResponseObject;
import com.doumee.model.response.shopCate.CateListResponseParam;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.swipemenulistview.SwipeMenuItem;
import com.huixiangshenghuo.app.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/6.
 */

public class ShangpinCategoryActivity extends BaseActivity {
   private SwipeMenuListView listView;
   private Button bt_xfl;

   /**
    * 适配器
    */
   private ShangpinCategoryAdapter adapter;
   //数据源
//   private List<City> list;
   //数据源
   private ArrayList<CateListResponseParam> arrlist = new ArrayList<CateListResponseParam>();
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_shangpin_category);

      initView();
   }

   private void initView() {
      initTitleBar_1();
      titleView.setText("商品类别");
      listView = (SwipeMenuListView) findViewById(R.id.collector_listview);
      bt_xfl = (Button) findViewById(R.id.bt_shangpin_category_xfl);
  /*    list = new ArrayList<City>();
      City city1 = new City("商品分类1", "共10件商品", "排序码1", R.mipmap.icon, "￥38");
      City city2 = new City("商品分类2", "共10件商品", "排序码2", R.mipmap.icon, "￥38");
      City city3 = new City("商品分类3", "共10件商品", "排序码3", R.mipmap.icon, "￥38");

      list.add(city1);
      list.add(city2);
      list.add(city3);*/
      bt_xfl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            newAdd();
         }
      });

      //构建适配器
      adapter = new ShangpinCategoryAdapter(arrlist, ShangpinCategoryActivity.this);
      listView.setAdapter(adapter);
      request();

      initData();


   }


   //初始化ListView数据，在OnCreate方法中调用
   private void initData() {

     /* final List<SystemDevice> data = new SystemDeviceDao(context).queryAllSystemDevice();
      final CollectorListAdapter adapter = new CollectorListAdapter(context, data);

      listView.setAdapter(adapter);
      listView.setEmptyView(activity.findViewById(R.id.collector_listview_empty));*/


      // step 1. create a MenuCreator
      SwipeMenuCreator creator = new SwipeMenuCreator() {
         @Override
         public void create(SwipeMenu menu) {
            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(ShangpinCategoryActivity.this.getApplicationContext());
            // set item background
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
            // set item width
            openItem.setWidth(dp2px(90));
            // set item title
            openItem.setTitle("编辑");
            // set item title fontsize
            openItem.setTitleSize(18);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(ShangpinCategoryActivity.this.getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
//            // set item width
//            deleteItem.setWidth(dp2px(90));
//            // set a icon
//            deleteItem.setIcon(R.mipmap.gg);
//            // add to menu
//            menu.addMenuItem(deleteItem);
            // set item width
            deleteItem.setWidth(dp2px(90));
            // set item title
            deleteItem.setTitle("删除");
            // set item title fontsize
            deleteItem.setTitleSize(18);
            // set item title font color
            deleteItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(deleteItem);
         }
      };

      // set creator
      listView.setMenuCreator(creator);

      // step 2. listener item click event
      listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            switch (index) {
               case 0:
                  // open
                  //         open(device);
                  //            Toast.makeText(ShangpinCategoryActivity.this, "编辑", Toast.LENGTH_LONG).show();
                  editor(arrlist.get(position).getName(), arrlist.get(position).getSortNum(), position);
                  break;
               case 1:
                  // delete
                 /* delete(device);
                  data.remove(position);
                  adapter.notifyDataSetChanged();*/
                  delete(position);
                  break;
            }
            return false;
         }
//         @Override
//         public void onMenuItemClick(int position, SwipeMenu menu, int index)
//         {
//   //         SystemDevice device = data.get(position);
//
//            switch (index)
//            {
//               case 0:
//                  // open
//         //         open(device);
//                  break;
//               case 1:
//                  // delete
//                 /* delete(device);
//                  data.remove(position);
//                  adapter.notifyDataSetChanged();*/
//                  break;
//            }
//         }
      });

      // set SwipeListener
      listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
         @Override
         public void onSwipeStart(int position) {
            // swipe start
         }

         @Override
         public void onSwipeEnd(int position) {
            // swipe end
         }
      });

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position,
                                 long id) {

           /* SystemDevice device = data.get(position);
            open(device);*/
         }
      });
   }

   private int dp2px(int dp) {
      return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            getResources().getDisplayMetrics());
   }

   /**
    * 打开
    * @param item
    */
/*   private void open(SystemDevice item)
   {

   }*/

   /**
    * 删除
    *
    * @paramitem
    */
 /*  private void delete(SystemDevice item)
   {

   }*/
   private void delete(final int i) {

      final Dialog dialog1 = new Dialog(ShangpinCategoryActivity.this);
      dialog1.setTitle("温馨提示");
      dialog1.setMessage("删除该商品");
      dialog1.setConfirmText("确定");
      dialog1.setConfirmClick(new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {

            //         Toast.makeText(ShangpinCategoryActivity.this, "删除该商品", Toast.LENGTH_LONG).show();
            deleterequest(i);

            dialog1.dismiss();
         }
      });
      dialog1.show();

   }

   //编辑  arrlist.get(i).setName("11111"); adapter.notifyDataSetChanged();
   AlertDialog alertDialog;

   public void editor(final String name, final int sortNum, final int i) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      View view = View.inflate(ShangpinCategoryActivity.this, R.layout.activity_shangpin_category_editor, null);
      final EditText et_fl = (EditText) view.findViewById(R.id.et_shangpin_actegory_editor_fl);
      final EditText et_px = (EditText) view.findViewById(R.id.et_shangpin_actegory_editor_px);
      Button bt_qx = (Button) view.findViewById(R.id.bt_qx);
      Button bt_qr = (Button) view.findViewById(R.id.bt_qr);
      et_fl.setText(name);
      et_px.setText(sortNum + "");


      bt_qr.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (TextUtils.isEmpty(et_fl.getText().toString().trim())) {
               Toast.makeText(ShangpinCategoryActivity.this, "请设置分类名称", Toast.LENGTH_LONG).show();
               return;
            }
            if (TextUtils.isEmpty(et_px.getText().toString().trim())) {
               Toast.makeText(ShangpinCategoryActivity.this, "请设置排序码", Toast.LENGTH_LONG).show();
               return;
            }
            //编辑


            editorequest(et_fl.getText().toString().trim(), et_px.getText().toString().trim(), i);

            alertDialog.dismiss();

         }
      });
      bt_qx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            alertDialog.dismiss();
         }
      });

      builder.setView(view);
      builder.setCancelable(false);
      alertDialog = builder.create();
      alertDialog.show();
   }

   AlertDialog alertDialog2;

   private void newAdd() {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      View view = View.inflate(ShangpinCategoryActivity.this, R.layout.activity_shangpin_category_editor, null);
      final EditText et_fl = (EditText) view.findViewById(R.id.et_shangpin_actegory_editor_fl);
      final EditText et_px = (EditText) view.findViewById(R.id.et_shangpin_actegory_editor_px);
      TextView tv_tite = (TextView) view.findViewById(R.id.tv_shangpin_category_tite);
      Button bt_qx = (Button) view.findViewById(R.id.bt_qx);
      Button bt_qr = (Button) view.findViewById(R.id.bt_qr);
      tv_tite.setText("新填分类");
      bt_qr.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (TextUtils.isEmpty(et_fl.getText().toString().trim())) {
               Toast.makeText(ShangpinCategoryActivity.this, "请设置分类名称", Toast.LENGTH_LONG).show();
               return;
            }
            if (TextUtils.isEmpty(et_px.getText().toString().trim())) {
               Toast.makeText(ShangpinCategoryActivity.this, "请设置排序码", Toast.LENGTH_LONG).show();
               return;
            }
            //新填
            newAddrequest(et_fl.getText().toString().trim(), et_px.getText().toString().trim());


            alertDialog2.dismiss();

         }
      });
      bt_qx.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            alertDialog2.dismiss();
         }
      });

      builder.setView(view);
      builder.setCancelable(false);
      alertDialog2 = builder.create();
      alertDialog2.show();
   }

   //查看列表
   private void request() {

      CateListRequestObject memberInfoParamObject = new CateListRequestObject();
      UserInfoResponseParam userInfo = SaveObjectTool.openUserInfoResponseParam();
      CateListRequestParam cateListRequestParam = new CateListRequestParam();
      cateListRequestParam.setShopId(userInfo.getShopId());
      memberInfoParamObject.setParam(cateListRequestParam);
      httpTool.post(memberInfoParamObject, URLConfig.SHOP_CATE, new HttpTool.HttpCallBack<CateListResponseObject>() {
         @Override
         public void onSuccess(CateListResponseObject o) {
            if (o != null && o.getRecordList() != null) {
               /**
                * 分页
                */
               if (!arrlist.isEmpty()) {
                  //清空
                  arrlist.clear();
               }

               arrlist.addAll(o.getRecordList());

               adapter.notifyDataSetChanged();
            }

         }

         @Override
         public void onError(CateListResponseObject o) {

         }
      });
   }

   //新填分类
   private void newAddrequest(String name, String sortNum) {
      CateAddRequestObject cateAddRequestObject = new CateAddRequestObject();
      CateAddRequestParam cateAddRequestParam = new CateAddRequestParam();
      cateAddRequestParam.setName(name);
      cateAddRequestParam.setSortNum(Integer.parseInt(sortNum));
      cateAddRequestObject.setParam(cateAddRequestParam);
      httpTool.post(cateAddRequestObject, URLConfig.SHOP_FL_NEWADD, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject o) {
            request();
            adapter.notifyDataSetChanged();
         }

         @Override
         public void onError(ResponseBaseObject o) {

         }
      });
   }

   //编辑分类
   private void editorequest(final String et_name, final String et_sottNum, final int i) {
      CateEditRequestObject cateEditRequestObject = new CateEditRequestObject();
      CateEditRequestParam cateEditRequestParam = new CateEditRequestParam();
      cateEditRequestParam.setName(et_name);
      cateEditRequestParam.setSortNum(Integer.parseInt(et_sottNum));
      cateEditRequestParam.setCateId(arrlist.get(i).getCateId());
      cateEditRequestObject.setParam(cateEditRequestParam);
      httpTool.post(cateEditRequestObject, URLConfig.SHOP_FL_EDITO, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject o) {
            arrlist.get(i).setName(et_name);
            arrlist.get(i).setSortNum(Integer.parseInt(et_sottNum));
            adapter.notifyDataSetChanged();

         }

         @Override
         public void onError(ResponseBaseObject o) {

         }
      });
   }

   //删除分类
   private void deleterequest(final int i) {
      CateDelRequestObject cateDelRequestObject = new CateDelRequestObject();
      CateDelRequestParam cateDelRequestParam = new CateDelRequestParam();

      cateDelRequestParam.setCateId(arrlist.get(i).getCateId());
      cateDelRequestObject.setParam(cateDelRequestParam);
      httpTool.post(cateDelRequestObject, URLConfig.SHOP_FL_DEL, new HttpTool.HttpCallBack<ResponseBaseObject>() {
         @Override
         public void onSuccess(ResponseBaseObject o) {
            adapter.remove(arrlist.get(i));
            adapter.notifyDataSetChanged();
         }

         @Override
         public void onError(ResponseBaseObject o) {

         }
      });

   }

}
