package com.huixiangshenghuo.app.ui.mine;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.utils.StringUtils;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.view.MyCityPicker;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.base.RequestBaseObject;
import com.doumee.model.request.memberaddr.AddrAddRequestObject;
import com.doumee.model.request.memberaddr.AddrAddRequestParam;
import com.doumee.model.request.memberaddr.AddrEditRequestObject;
import com.doumee.model.request.memberaddr.AddrEditRequestParam;
import com.doumee.model.request.userInfo.ProvinceRequestObject;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.memberaddr.AddrListResponseObject;
import com.doumee.model.response.memberaddr.AddrListResponseParam;
import com.doumee.model.response.userinfo.AreaResponseParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/8.
 */

public class AddAcceptLocationActivity extends BaseActivity {


   EditText nameView;

   EditText telView;

   TextView areaView;

   TextView addressView;

   EditText moreAddressView;

   EditText codeView;

   //   private HttpTool httpTool;
   private String areaId;
   private String address = "";
   //private BaiduLocationTool baiduLocationTool;
   private String addId;//地址ID

   public static void startAddAcceptLocationActivity(Context context, String addId) {
      Intent intent = new Intent(context, AddAcceptLocationActivity.class);
      intent.putExtra("addId", addId);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addId = getIntent().getStringExtra("addId");
//      httpTool = HttpTool.newInstance(this);
      setContentView(R.layout.activity_add_acceptlocation);
      if (!TextUtils.isEmpty(addId)) {
         loadUserAddress();
      }
      initView();
   }


   public void initView() {
      initTitleBar_1();
      titleView.setText("新增收货地址");
      actionButton.setText("保存");

      nameView = (EditText) findViewById(R.id.name);
      telView = (EditText) findViewById(R.id.tel);
      areaView = (TextView) findViewById(R.id.area);
      addressView = (TextView) findViewById(R.id.address);
      moreAddressView = (EditText) findViewById(R.id.more_address);
      codeView = (EditText) findViewById(R.id.emal);

      actionButton.setVisibility(View.VISIBLE);
      actionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            saveAddress();
         }
      });

      areaView.setOnClickListener(new View.OnClickListener() {
         @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
         @Override
         public void onClick(View v) {
//            Intent intent = new Intent(AddAcceptLocationActivity.this, AreaActivity.class);
//            startActivityForResult(intent, 1, null);

//            Intent intent = new Intent(AddAcceptLocationActivity.this, AreaRegisterActivity.class);
//            startActivityForResult(intent, 1, null);//页面选择区域

            requestArea();//弹窗选择区域
         }
      });
      addressView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            baiDuLocation();//定位地址

         }
      });
   }

   @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
   private void baiDuLocation() {

//      Intent intent = new Intent(this, BaiduLocationActivity.class);
//      startActivityForResult(intent, 2, null);

      Intent intent = new Intent(this, AutoChooseAddressActivity.class);
      startActivityForResult(intent, 2, null);

   }

   private void saveAddress() {
      String name = nameView.getText().toString().trim();
      String tel = telView.getText().toString().trim();
      String moreAddress = moreAddressView.getText().toString();
      String code = codeView.getText().toString().trim();
      if (TextUtils.isEmpty(name)) {
         Toast.makeText(getApplicationContext(), "请填写收货人姓名", Toast.LENGTH_LONG).show();
         return;
      }
      if (TextUtils.isEmpty(tel)) {
         Toast.makeText(getApplicationContext(), "请填写收货人手机号", Toast.LENGTH_LONG).show();
         return;
      }
      if (TextUtils.isEmpty(areaId)) {
         Toast.makeText(getApplicationContext(), "请选择收货区域", Toast.LENGTH_LONG).show();
         return;
      }
      if (TextUtils.isEmpty(moreAddress)) {
         Toast.makeText(getApplicationContext(), "请填写详细地址", Toast.LENGTH_LONG).show();
         return;
      }
      if (TextUtils.isEmpty(code)) {
         Toast.makeText(getApplicationContext(), "请填写邮政编码", Toast.LENGTH_LONG).show();
         return;
      }

      if (TextUtils.isEmpty(addId)) {
         AddrAddRequestParam addrAddRequestParam = new AddrAddRequestParam();
         addrAddRequestParam.setName(name);
         addrAddRequestParam.setPhone(tel);
         addrAddRequestParam.setAreaId(areaId);
         String info = address + moreAddress;
//         addrAddRequestParam.setInfo(info);
         addrAddRequestParam.setInfo(moreAddress);
         addrAddRequestParam.setAddr(addressView.getText().toString().trim());
         addrAddRequestParam.setCode(code);
         addrAddRequestParam.setIsDefault("");
         AddrAddRequestObject addrAddRequestObject = new AddrAddRequestObject();
         addrAddRequestObject.setParam(addrAddRequestParam);

         httpTool.post(addrAddRequestObject, URLConfig.USER_ADD_ADDRESS, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
               //            Toast.makeText(getApplicationContext(), o.getErrorMsg(), Toast.LENGTH_LONG).show();
               finish();
            }

            @Override
            public void onError(ResponseBaseObject o) {
               Toast.makeText(getApplicationContext(), o.getErrorMsg(), Toast.LENGTH_LONG).show();
            }
         });
      } else {

         AddrEditRequestParam addrEditRequestParam = new AddrEditRequestParam();
         addrEditRequestParam.setAddrId(addId);
         addrEditRequestParam.setAreaId(areaId);
         addrEditRequestParam.setName(name);
         addrEditRequestParam.setPhone(tel);
         String info = address + moreAddress;
//         addrEditRequestParam.setInfo(info);
         addrEditRequestParam.setInfo(moreAddress);
         addrEditRequestParam.setAddr(addressView.getText().toString().trim());
         addrEditRequestParam.setCode(code);
         addrEditRequestParam.setIsDefault("");
         AddrEditRequestObject addrEditRequestObject = new AddrEditRequestObject();
         addrEditRequestObject.setParam(addrEditRequestParam);

         httpTool.post(addrEditRequestObject, URLConfig.USER_EDIT_ADDRESS, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
               //         Toast.makeText(getApplicationContext(), o.getErrorMsg(), Toast.LENGTH_LONG).show();
               finish();
            }

            @Override
            public void onError(ResponseBaseObject o) {
               Toast.makeText(getApplicationContext(), o.getErrorMsg(), Toast.LENGTH_LONG).show();
            }
         });

      }

   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode == RESULT_OK) {
         if (requestCode == 1) {
//            HashMap<String, String> areaMap = (HashMap) data.getSerializableExtra("data");
//            Set<String> keys = areaMap.keySet();
//            for (String key : keys) {
//               areaId = key;
//               areaView.setText(areaMap.get(key));
//            }
            areaId = data.getStringExtra("areaId");
            areaView.setText(StringUtils.avoidNull(data.getStringExtra("areaName")));
         } else if (requestCode == 2) {
            String name = data.getStringExtra("data");
            address = name;
            addressView.setText(address);
         }
      }
   }

   @Override
   protected void onStart() {
      super.onStart();
      /*if (!TextUtils.isEmpty(addId)) {
         loadUserAddress();
      }*/
   }

   private void loadUserAddress() {
      RequestBaseObject requestBaseObject = new RequestBaseObject();
      httpTool.post(requestBaseObject, URLConfig.USER_ADDRESS_LIST, new HttpTool.HttpCallBack<AddrListResponseObject>() {
         @Override
         public void onSuccess(AddrListResponseObject o) {
            for (AddrListResponseParam address : o.getRecordList()) {
               String id = address.getAddrId();
               if (TextUtils.equals(id, addId)) {
                  nameView.setText(address.getName());
                  telView.setText(address.getPhone());
                  areaView.setText(address.getProvinceName() + "/" + address.getCityName() + "/" + address.getAreaName());
                  areaId = address.getAreaId();
                  moreAddressView.setText(address.getInfo());
                  codeView.setText(address.getCode());
                  addressView.setText(address.getAddr());
               }
            }

         }

         @Override
         public void onError(AddrListResponseObject o) {

         }
      });
   }

   @Override
   protected void onStop() {
      super.onStop();
   }

   //==============================================================
   /**
    * 所有省
    */
   protected List<ProvinceParam> mProvinceDatas;

   /**
    * key - 省 value - 市
    */
   protected Map<String, List<CityParam>> mCitisDatasMap = new HashMap<>();

   /**
    * key - 市 values - 区
    */
   protected Map<String, List<AreaResponseParam>> mDistrictDatasMap = new HashMap<>();

   private void requestArea() {
      showProgressDialog(null);
      httpTool.post(new ProvinceRequestObject(), URLConfig.CITY_LIST, new HttpTool.HttpCallBack<ProvinceObject>() {
         @Override
         public void onSuccess(ProvinceObject resp) {
            dismissProgressDialog();
            if (resp.getLstProvince() != null && resp.getLstProvince().size() > 0) {
               mProvinceDatas = resp.getLstProvince();
               mCitisDatasMap = new HashMap<>();
               mDistrictDatasMap = new HashMap<>();
               for (ProvinceParam param : resp.getLstProvince()) {
                  if (param.getLstCity() != null && param.getLstCity().size() > 0) {
                     mCitisDatasMap.put(param.getProvinceId(), param.getLstCity());
                     for (CityParam cityParam : param.getLstCity()) {
                        mDistrictDatasMap.put(cityParam.getCityId(), cityParam.getLstArea());
                     }
                  }
               }
               chooseArea(areaView);
            }
         }

         @Override
         public void onError(ProvinceObject provinceObject) {
            dismissProgressDialog();
            ToastView.show(provinceObject.getErrorMsg());
         }


      });
   }

   //Texview的点击事件
   public void chooseArea(View view) {
      //判断输入法的隐藏状态
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      if (imm.isActive()) {
         imm.hideSoftInputFromWindow(view.getWindowToken(),
               InputMethodManager.HIDE_NOT_ALWAYS);
         selectAddress();//调用CityPicker选取区域

      }
   }

   private void selectAddress() {
      MyCityPicker cityPicker = new MyCityPicker.Builder(AddAcceptLocationActivity.this)
            .textSize(14)
            .title("地址选择")
            .titleBackgroundColor("#FFFFFF")
            .confirTextColor("#696969")
            .cancelTextColor("#696969")
            .textColor(Color.parseColor("#000000"))
            .provinceCyclic(false)
            .cityCyclic(false)
            .districtCyclic(false)
            .visibleItemsCount(7)
            .itemPadding(10)
            .onlyShowProvinceAndCity(false)
            .build();
      cityPicker.setmProvinceDatas(mProvinceDatas);
      cityPicker.setmCitisDatasMap(mCitisDatasMap);
      cityPicker.setmDistrictDatasMap(mDistrictDatasMap);
      cityPicker.show();
      //监听方法，获取选择结果
      cityPicker.setOnCityItemClickListener(new MyCityPicker.OnCityItemClickListener() {
         @Override
         public void onSelected(int... citySelected) {
            areaId = mProvinceDatas.get(citySelected[0]).getLstCity().get(citySelected[1]).getLstArea().get(citySelected[2]).getAreaId();
            areaView.setText(mProvinceDatas.get(citySelected[0]).getName() + "/" + mProvinceDatas.get(citySelected[0]).getLstCity().get(citySelected[1]).getCityName() + "/" + mProvinceDatas.get(citySelected[0]).getLstCity().get(citySelected[1]).getLstArea().get(citySelected[2]).getAreaName());

         }
      });
   }


   //==============================================================

}
