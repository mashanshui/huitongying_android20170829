package com.huixiangshenghuo.app.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.CustomAlertDialog;
import com.doumee.model.request.base.RequestBaseObject;
import com.doumee.model.request.memberaddr.AddrDelRequestObject;
import com.doumee.model.request.memberaddr.AddrDelRequestParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.memberaddr.AddrListResponseObject;
import com.doumee.model.response.memberaddr.AddrListResponseParam;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.comm.http.HttpTool;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/8.
 * 收货地址
 */

public class SelectAcceptLocationActivity extends BaseActivity {


   ListView listView;

   LinearLayout addButton;

   Button yesButton;

   //   private HttpTool httpTool;
   private ArrayList<AddrListResponseParam> addressList;
   private CustomBaseAdapter<AddrListResponseParam> adapter;
   private AddrListResponseParam addrListResponseParam;
   private static final int EDIT_STATUS = 1;//编辑地址状态
   private static final int SELECT_STATUS = 0;//选择地址状态
   private int status = SELECT_STATUS;
   public static final int SELECT_ADDRESS_YES = 1;
   public static final int SELECT_ADDRESS_NO = 0;
   private int selectAdd = SELECT_ADDRESS_YES;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      selectAdd = getIntent().getIntExtra("address", SELECT_ADDRESS_YES);
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_select_acceptlocation);
      initView();

//      httpTool = HttpTool.newInstance(this);
      addressList = new ArrayList<>();
      adapter = new CustomBaseAdapter<AddrListResponseParam>(addressList, R.layout.item_location_list) {
         @Override
         public void bindView(ViewHolder holder, final AddrListResponseParam address) {
            TextView nameView = holder.getView(R.id.name);
            TextView addressView = holder.getView(R.id.address);
            TextView telView = holder.getView(R.id.tel);
            RadioButton radioButton = holder.getView(R.id.select_button);
            ImageButton deleteButton = holder.getView(R.id.delete);
            ImageButton editButton = holder.getView(R.id.edit);
//            if (status == SELECT_STATUS) {
//               radioButton.setVisibility(View.VISIBLE);
//               deleteButton.setVisibility(View.GONE);
//               editButton.setVisibility(View.GONE);
//            } else {
//               radioButton.setVisibility(View.GONE);
//               deleteButton.setVisibility(View.VISIBLE);
//               editButton.setVisibility(View.VISIBLE);
//            }
            if (status == SELECT_STATUS) {
               radioButton.setVisibility(View.GONE);
               deleteButton.setVisibility(View.GONE);
               editButton.setVisibility(View.GONE);
            } else {
               radioButton.setVisibility(View.GONE);
               deleteButton.setVisibility(View.VISIBLE);
               editButton.setVisibility(View.VISIBLE);
            }

            nameView.setText(address.getName());
            addressView.setText(address.getAddr() + "   " + address.getInfo());
            telView.setText(address.getPhone());
            final String id = address.getAddrId();
            radioButton.setTag(id);
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if (isChecked) {
                     //         addrListResponseParam = address;
                     initAddress(id);
                  }
               }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  delAddress(id);
               }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  editAddress(id);
               }
            });
            String de = address.getIsDefault();
            if (TextUtils.equals("1", de)) {
               addrListResponseParam = address;
               radioButton.setChecked(true);
            }
         }
      };
      listView.setAdapter(adapter);

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (selectAdd == SELECT_ADDRESS_NO) {//个人资料跳转过来


            } else {//买单
               addrListResponseParam = addressList.get(position);
               Intent intent = new Intent();
               intent.putExtra("data", addrListResponseParam);
               setResult(RESULT_OK, intent);
               finish();
            }

         }
      });
   }

   private void initAddress(String tag) {
      int count = listView.getChildCount();
      for (int i = 0; i < count; i++) {
         View view = listView.getChildAt(i);
         RadioButton radioButton = (RadioButton) view.findViewById(R.id.select_button);
         if (!TextUtils.equals(tag, radioButton.getTag().toString())) {
            radioButton.setChecked(false);
         }
      }
   }


   public void initView() {
      initTitleBar_1();
      titleView.setText("选择收货地址");
      actionButton.setText("管理");
      actionButton.setVisibility(View.VISIBLE);
      //   View view = View.inflate(mContext, R.layout.activity_select_acceptlocation, null);

      listView = (ListView) findViewById(R.id.list_view);
      addButton = (LinearLayout) findViewById(R.id.add_address);
      yesButton = (Button) findViewById(R.id.yes_button);


      actionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            String name = actionButton.getText().toString();
            if (TextUtils.equals("管理", name)) {
               status = EDIT_STATUS;
               actionButton.setText("完成");
            } else {
               status = SELECT_STATUS;
               actionButton.setText("管理");
            }
            adapter.notifyDataSetChanged();
         }
      });
      //新增收货地址
      addButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            AddAcceptLocationActivity.startAddAcceptLocationActivity(SelectAcceptLocationActivity.this, "");
         }
      });
      yesButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (null == addrListResponseParam) {
               Toast.makeText(getApplicationContext(), "请选择收货地址", Toast.LENGTH_LONG).show();
               return;
            }
            Intent intent = new Intent();
            intent.putExtra("data", addrListResponseParam);
            setResult(RESULT_OK, intent);
            finish();
         }
      });
//      if (selectAdd == SELECT_ADDRESS_NO) {
//         yesButton.setVisibility(View.GONE);
//      }
      if (selectAdd == SELECT_ADDRESS_NO) {
         yesButton.setVisibility(View.GONE);
      } else {
         yesButton.setVisibility(View.GONE);
      }
   }

   @Override
   protected void onStart() {
      super.onStart();
      loadUserAddress();
   }

   //查询收货地址
   private void loadUserAddress() {
      RequestBaseObject requestBaseObject = new RequestBaseObject();
      httpTool.post(requestBaseObject, URLConfig.USER_ADDRESS_LIST, new HttpTool.HttpCallBack<AddrListResponseObject>() {
         @Override
         public void onSuccess(AddrListResponseObject o) {
            addressList.clear();
            addressList.addAll(o.getRecordList());
            adapter.notifyDataSetChanged();
         }

         @Override
         public void onError(AddrListResponseObject o) {
         }
      });
   }

   private void delAddress(final String addId) {
      CustomAlertDialog.showAlertDialog(this, "确定删除收货地址？", new CustomAlertDialog.OnLeftButtonOnClickListener() {
         @Override
         public void onClick() {
            AddrDelRequestParam addrDelRequestParam = new AddrDelRequestParam();
            addrDelRequestParam.setAddrId(addId);
            AddrDelRequestObject addrDelRequestObject = new AddrDelRequestObject();
            addrDelRequestObject.setParam(addrDelRequestParam);
            httpTool.post(addrDelRequestObject, URLConfig.USER_DEL_ADDRESS, new HttpTool.HttpCallBack<ResponseBaseObject>() {
               @Override
               public void onSuccess(ResponseBaseObject o) {
                  loadUserAddress();
               }

               @Override
               public void onError(ResponseBaseObject o) {
                  Toast.makeText(getApplicationContext(), o.getErrorMsg(), Toast.LENGTH_LONG).show();
               }
            });
         }
      }, new CustomAlertDialog.OnRightButtonOnClickListener() {
         @Override
         public void onClick() {

         }
      });
   }

   private void editAddress(String addId) {
      AddAcceptLocationActivity.startAddAcceptLocationActivity(SelectAcceptLocationActivity.this, addId);
   }


}
