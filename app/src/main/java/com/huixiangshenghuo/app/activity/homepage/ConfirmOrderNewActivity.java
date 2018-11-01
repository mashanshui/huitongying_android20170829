package com.huixiangshenghuo.app.activity.homepage;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.goodsorder.GoodsOrderAddResponseObject;
import com.doumee.model.response.goodsorder.GoodsOrderAddResponseParam;
import com.doumee.model.response.goodsorder.WeixinOrderAddResponseObject;
import com.doumee.model.response.goodsorder.WeixinOrderResponseParam;
import com.doumee.model.response.memberaddr.AddrListResponseObject;
import com.doumee.model.response.memberaddr.AddrListResponseParam;
import com.doumee.model.response.userinfo.AlipayResponseParam;
import com.doumee.model.response.userinfo.MemberInfoResponseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.doumee.common.weixin.entity.request.PayOrderRequestEntity;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.ThirdPartyClient;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.adapter.CustomBaseAdapter;
import com.huixiangshenghuo.app.comm.alipay.AliPayTool;
import com.huixiangshenghuo.app.comm.http.HttpTool;
import com.huixiangshenghuo.app.comm.model.ShopcartListParam;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.comm.utils.StringUtils;
import com.huixiangshenghuo.app.comm.wxpay.WXPayTool;
import com.huixiangshenghuo.app.ui.BaseActivity;
import com.huixiangshenghuo.app.ui.login.RegActivity;
import com.huixiangshenghuo.app.ui.mine.SelectAcceptLocationActivity;
import com.huixiangshenghuo.app.view.MyListView;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.request.base.RequestBaseObject;
import com.doumee.model.request.goodsorder.GoodsDetailsRequestParam;
import com.doumee.model.request.goodsorder.GoodsOrderAddRequestObject;
import com.doumee.model.request.goodsorder.GoodsOrderAddRequestParam;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderAddRequestParam;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestObject;
import com.doumee.model.request.goodsorder.WeixinOrderQueryRequestParam;
import com.doumee.model.request.userInfo.MemberInfoParamObject;
import com.doumee.model.request.userInfo.MemberInfoRequestObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/6/1.
 * 确认订单 新
 */

public class ConfirmOrderNewActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout selectAddress;//选择地址
    TextView nameView;//名字
    TextView telView;//请选择送货地址
    TextView addressView;//送货地址
    RadioGroup payType;//支付方式
    RadioButton tybPay, jfPay, aliPay;
    MyListView myListView;//商品列表
    EditText notesView;//备注
    TextView payTotalView;//待支付
    Button submitButton;//提交订单
    TextView paid;//实付
    TextView tv_zongjia;//总价

    private static final int WECHAT_PAY = 0;//微信支付
    private static final int ALI_PAY = 1;//支付宝支付
    private static final int UNION__PAY = 2;//惠宝
    private static final int JF_PAY = 3;//积分
    private int pay = ALI_PAY;
    private int payMethod = UNION__PAY;
    private int isJFGoods = 0; //0普通商品 1积分商品


    private String addId;//地址ID
    private String shopId;//商品id
    private String orderId;
    private int payWeChat = 0;

    public static final int PAY_SUCCESS = 1;//支付成功


    private ArrayList<ShopcartListParam> goods;

    private double totalPrice;
    //   private double returnPrice;
    private double money;//惠宝数量
    private double integral;//积分数量
//   private String idCardCheckStatus;//是否实名认证 实名认证审核状态 0未申请 1申请中 2审核通过 3审核未通过

    private AlertDialog alertDialog;
    private AlertDialog payAlertTip;
    private String payPassword = "";//惠宝密码
    private double payNum = 0;//使用惠宝数量
    private String str;//惠宝输入框

    private double kdfee;//快递费
    private double freeFee;//免邮金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order_new);
        initview();
        initData();
        loadDataIndex();
//      initData();
        payAlertTip();
        initListener();
        loadUser();
        loadUserAddress();

    }

    private void initview() {
        initTitleBar_1();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            goods = (ArrayList<ShopcartListParam>) bundle.getSerializable("list");
        }
        isContainJFGoods();
        titleView.setText("确认订单");
        selectAddress = (RelativeLayout) findViewById(R.id.select_address_new);
        nameView = (TextView) findViewById(R.id.name);
        telView = (TextView) findViewById(R.id.tel);
        addressView = (TextView) findViewById(R.id.address_new);
        payType = (RadioGroup) findViewById(R.id.pay_type);
        tybPay = (RadioButton) findViewById(R.id.tyb_pay);
        jfPay = (RadioButton) findViewById(R.id.jf_pay);
        aliPay = (RadioButton) findViewById(R.id.ali_pay);
        myListView = (MyListView) findViewById(R.id.list_view);
        notesView = (EditText) findViewById(R.id.notes);
        payTotalView = (TextView) findViewById(R.id.total_pay_new);
        tv_zongjia = (TextView) findViewById(R.id.tv_zongjia_new);
        submitButton = (Button) findViewById(R.id.btn_order_submit_new);
        paid = (TextView) findViewById(R.id.paid);
        notesView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//限制20个字

        selectAddress.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        if (goods != null && isJFGoods == 1) {
            tybPay.setVisibility(View.GONE);
            aliPay.setVisibility(View.GONE);
        } else if (goods != null && isJFGoods == 0) {
            jfPay.setVisibility(View.GONE);
        }
    }

    private void initData() {
        if (goods == null) {
            goods = new ArrayList<>();
        }
        calculate();
        myListView.setAdapter(new CustomBaseAdapter<ShopcartListParam>(goods, R.layout.item_order_good) {
            @Override
            public void bindView(ViewHolder holder, ShopcartListParam obj) {
                TextView nameTxt = holder.getView(R.id.goods_name);
                TextView priceTxt = holder.getView(R.id.price);
                TextView numTxt = holder.getView(R.id.number);
                nameTxt.setText(StringUtils.avoidNull(obj.getProName()));

                //保留两位小数
                BigDecimal b = new BigDecimal(obj.getPrice());
                double price = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                priceTxt.setText("￥" + price);
//            priceTxt.setText("￥" + obj.getPrice());
                numTxt.setText("x" + obj.getNum());
            }
        });
//      if (totalPrice < freeFee) {
//         paid.setText(kdfee + "元");
//         totalPrice += kdfee;
//      } else {
//         paid.setText("免费配送");
//
//      }
//      tv_zongjia.setText(totalPrice + "元");
//      payTotalView.setText(totalPrice + "元");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pay == WECHAT_PAY && payWeChat == 1) {
            loadPayResult();
        }
    }

    private void initListener() {
        payType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.wechat_pay:
                        pay = WECHAT_PAY;
                        payMethod = WECHAT_PAY;
                        break;
                    case R.id.ali_pay:
                        pay = ALI_PAY;
                        payMethod = ALI_PAY;
                        break;
                    case R.id.tyb_pay://惠宝
//                  pay = UNION__PAY;
                        payMethod = UNION__PAY;
                        break;
                    case R.id.jf_pay:
                        payMethod = JF_PAY;
                        pay = JF_PAY;
                        break;
                    case R.id.blank_pay:
                        pay = UNION__PAY;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_address_new:
                Intent intent = new Intent(ConfirmOrderNewActivity.this, SelectAcceptLocationActivity.class);
                startActivityForResult(intent, 1, null);
                break;
            case R.id.btn_order_submit_new:
                if (TextUtils.isEmpty(addId)) {
                    ToastView.show("请选择收货地址");
                    return;
                }
                if (payMethod == UNION__PAY) {
                    if (money < totalPrice) {
                        ToastView.show("惠宝数量不足，请选择其他方式支付");
                    } else {
                        showPayDialog();
                    }

                } else if (payMethod == JF_PAY) {
                    if (integral < totalPrice) {
                        ToastView.show("积分数量不足");
                    }else {
                        showPayDialog();
                    }
                } else {
                    submitData();
                }
                break;
            default:
                break;
        }
    }

    //   private void showPayDialog() {
//      AlertDialog.Builder builder = new AlertDialog.Builder(this);
//      View view = View.inflate(this, R.layout.activity_order_confirm_new_dialog, null);
//      final EditText payPwdView = (EditText) view.findViewById(R.id.pay_password_new);
//      final EditText pay_num = (EditText) view.findViewById(R.id.pay_num_new);
//      Button editPdwButton = (Button) view.findViewById(R.id.edit_password_new);
//      Button noButton = (Button) view.findViewById(R.id.bt_no_new);
//      Button yesButton = (Button) view.findViewById(R.id.bt_yes_new);
//      pay_num.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//      pay_num.setHint("当前可使用惠宝" + money);
//      pay_num.addTextChangedListener(new TextWatcher() {   // 这是主要方法，下面为一些处理
//
//         @Override
//         public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//            str = pay_num.getText().toString().trim();
////          payNum = Double.valueOf(str).doubleValue();
//            if (str.indexOf('.') == 0) {
//               ToastView.show("不能为.");
//               pay_num.setText("");
//            }
//            if(str.equals("")){
//
//              return;
//            }
//
//
//            payNum = Double.valueOf(str).doubleValue();
//            if (payNum > money) {
//               payNum = money;
//               pay_num.setText(money + "");
//            }
//         }
//
//         @Override
//         public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//                                       int arg3) {
//            // TODO Auto-generated method stub
//
//         }
//
//         @Override
//         public void afterTextChanged(Editable arg0) {
//            // TODO Auto-generated method stub
//
//         }
//      });
//      noButton.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            alertDialog.dismiss();
//         }
//      });
//      yesButton.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            payPassword = payPwdView.getText().toString().trim();
//
//            if (!TextUtils.isEmpty(payPassword) && !TextUtils.isEmpty(str)) {
//               if (payNum < totalPrice) {
//                  ToastView.show("惠宝数量不足，请选择其他方式支付");
//               } else {
//                  submitData();
//               }
//               alertDialog.dismiss();
//            } else {
//               ToastView.show("请输入惠宝数量或者支付密码");
//            }
//         }
//      });
//      editPdwButton.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            Intent intent_modifypaymentpassword = new Intent(ConfirmOrderNewActivity.this, RegActivity.class);
//            startActivity(intent_modifypaymentpassword);
//         }
//      });
//      builder.setView(view);
//      builder.setCancelable(false);
//      alertDialog = builder.create();
//      alertDialog.show();
//   }
    private void showPayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.activity_order_confirm_dialog, null);
        final EditText payPwdView = (EditText) view.findViewById(R.id.pay_password);
        Button editPdwButton = (Button) view.findViewById(R.id.edit_password);
        Button noButton = (Button) view.findViewById(R.id.no);
        Button yesButton = (Button) view.findViewById(R.id.yes);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payPassword = payPwdView.getText().toString().trim();
                if (!TextUtils.isEmpty(payPassword)) {
                    submitData();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(ConfirmOrderNewActivity.this, "请输入积分支付密码", Toast.LENGTH_LONG).show();
                }
            }
        });
        editPdwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_modifypaymentpassword = new Intent(ConfirmOrderNewActivity.this, RegActivity.class);
                startActivity(intent_modifypaymentpassword);
            }
        });
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    //加载用户地址
    private void loadUserAddress() {
        RequestBaseObject requestBaseObject = new RequestBaseObject();
        httpTool.post(requestBaseObject, URLConfig.USER_ADDRESS_LIST, new HttpTool.HttpCallBack<AddrListResponseObject>() {
            @Override
            public void onSuccess(AddrListResponseObject o) {

                //====================================================
                if (o.getRecordList().size() > 0) {//拿第一条
                    String de = o.getRecordList().get(0).getIsDefault();
                    nameView.setText(o.getRecordList().get(0).getName());
                    telView.setText(o.getRecordList().get(0).getPhone());
                    addressView.setText(o.getRecordList().get(0).getAddr() + "   " + o.getRecordList().get(0).getInfo());
                    addId = o.getRecordList().get(0).getAddrId();
                }
                //====================================================
            }

            @Override
            public void onError(AddrListResponseObject o) {

            }
        });
    }

    private void isContainJFGoods() {
        for (ShopcartListParam chooseGood : goods) {
            if (chooseGood.getType() != null && TextUtils.equals(chooseGood.getType(), CustomConfig.INTEGRAL_GOODS)) {
                isJFGoods = 1;
                return;
            }
        }
        isJFGoods = 0;
    }

    /**
     * 提交订单   线上订单生成
     */
    private void submitData() {
        showProgressDialog(getString(R.string.loading));

        GoodsOrderAddRequestParam goodsOrderAddRequestParam = new GoodsOrderAddRequestParam();
        goodsOrderAddRequestParam.setAddrId(addId);
        if (payMethod == UNION__PAY) {
            goodsOrderAddRequestParam.setIntegralNum(totalPrice);
            goodsOrderAddRequestParam.setPayPwd(payPassword);
        }
        if (payMethod == JF_PAY) {
            goodsOrderAddRequestParam.setIntegral(totalPrice);
            goodsOrderAddRequestParam.setPayPwd(payPassword);
        }
        goodsOrderAddRequestParam.setInfo(notesView.getText().toString().trim());//备注
        goodsOrderAddRequestParam.setPayMethod(pay + "");//支付方式 0微信转账 1支付宝转账
//      List<GoodsDetailsRequestParam> proList = new ArrayList<>();
//      for (GoodsInfo goodsInfo : goodsDataList) {
//         if (goodsInfo.type == 1) {
//            GoodsDetailsResponeParam goods = goodsInfo.goods;
//            GoodsDetailsRequestParam goodsDetailsRequestParam = new GoodsDetailsRequestParam();
//            goodsDetailsRequestParam.setProId(goods.getProId());
//            goodsDetailsRequestParam.setNum(goods.getNum());
//            if (!TextUtils.isEmpty(goods.getSkuId())) {
//               goodsDetailsRequestParam.setSkuId(goods.getSkuId());
//            }
//            proList.add(goodsDetailsRequestParam);
//         }
//      }

        List<GoodsDetailsRequestParam> proList = new ArrayList<>();
        for (ShopcartListParam item : goods) {
            GoodsDetailsRequestParam pro = new GoodsDetailsRequestParam();
            pro.setNum(item.getNum());
            pro.setProId(item.getProId());
            proList.add(pro);
        }

        goodsOrderAddRequestParam.setProList(proList);
        GoodsOrderAddRequestObject goodsOrderAddRequestObject = new GoodsOrderAddRequestObject();
        goodsOrderAddRequestObject.setParam(goodsOrderAddRequestParam);
        httpTool.post(goodsOrderAddRequestObject, URLConfig.ORDER_CREATE, new HttpTool.HttpCallBack<GoodsOrderAddResponseObject>() {
            @Override
            public void onSuccess(GoodsOrderAddResponseObject o) {
                dismissProgressDialog();
//            if (idCardCheckStatus.equals("2")) {//实名认证审核状态 0未申请 1申请中 2审核通过 3审核未通过
                GoodsOrderAddResponseParam goodsOrderAddResponseParam = o.getRecord();
                String orderId = goodsOrderAddResponseParam.getOrderId();
                String isPayDone = goodsOrderAddResponseParam.getIsPayDone();
                AlipayResponseParam alipayResponseParam = o.getParam();

                if (TextUtils.equals(isPayDone, "1")) {//支付完成
                    paySuccess();
                } else {
                    if (pay == WECHAT_PAY) {
                        if (ThirdPartyClient.isWeixinAvilible(ConfirmOrderNewActivity.this)) {
                            payOrder(orderId, "");
                        } else {
                            ToastView.show("请安装微信客户端，或者使用其它支付方式");
                        }
                        //                    payOrder(orderId, "");
                    } else {
                        payOrder(orderId, alipayResponseParam.getParamStr());
                    }

//               payOrder(orderId, alipayResponseParam.getParamStr());
                }
                //           } else {
                //             ToastView.show("对不起，只有实名认证用户才能进行该操作，请前往个人中心进行实名认证");
                //           }
            }

            @Override
            public void onError(GoodsOrderAddResponseObject o) {
                dismissProgressDialog();
                Toast.makeText(ConfirmOrderNewActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 支付成功后，返回支付状态
     */
    private void paySuccess() {
        Toast.makeText(ConfirmOrderNewActivity.this, "支付成功", Toast.LENGTH_LONG).show();

        Intent intent = new Intent();
        intent.putExtra("data", PAY_SUCCESS);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 订单支付结果
     */
    private void loadPayResult() {
        showProgressDialog(getString(R.string.loading));
        WeixinOrderQueryRequestParam weixinOrderQueryRequestParam = new WeixinOrderQueryRequestParam();
        weixinOrderQueryRequestParam.setOrderId(orderId);
        weixinOrderQueryRequestParam.setType(WeixinOrderQueryRequestParam.TYPE_PRODUCT);

        WeixinOrderQueryRequestObject weixinOrderQueryRequestObject = new WeixinOrderQueryRequestObject();
        weixinOrderQueryRequestObject.setParam(weixinOrderQueryRequestParam);

        httpTool.post(weixinOrderQueryRequestObject, URLConfig.ORDER_PAY_RESULT, new HttpTool.HttpCallBack<ResponseBaseObject>() {
            @Override
            public void onSuccess(ResponseBaseObject o) {
                dismissProgressDialog();
                paySuccess();
            }

            @Override
            public void onError(ResponseBaseObject o) {
                dismissProgressDialog();
                payAlertTip.show();
            }
        });
    }

    //支付订单
    private void payOrder(final String orderId, String aliParams) {

        switch (pay) {
            case WECHAT_PAY:
                this.orderId = orderId;
                showProgressDialog(getString(R.string.loading));
                WeixinOrderAddRequestParam weixinOrderAddRequestParam = new WeixinOrderAddRequestParam();
                weixinOrderAddRequestParam.setOrderId(orderId);
                weixinOrderAddRequestParam.setTradeType("APP");
                weixinOrderAddRequestParam.setType("0");//订单类型：订单类型：0商品订单 1充值 2升级 3成为商户
                WeixinOrderAddRequestObject weixinOrderAddRequestObject = new WeixinOrderAddRequestObject();
                weixinOrderAddRequestObject.setParam(weixinOrderAddRequestParam);
                httpTool.post(weixinOrderAddRequestObject, URLConfig.ORDER_WECHAT_PAY, new HttpTool.HttpCallBack<WeixinOrderAddResponseObject>() {
                    @Override
                    public void onSuccess(WeixinOrderAddResponseObject o) {
                        dismissProgressDialog();
                        WeixinOrderResponseParam weiXin = o.getData();
                        PayOrderRequestEntity payOrderRequestEntity = weiXin.getParam();
                        payWeChat = 1;
                        WXPayTool.WXPay wxPay = new WXPayTool.WXPay();
                        wxPay.setNonceStr(payOrderRequestEntity.getNoncestr());
                        wxPay.setPrepayId(payOrderRequestEntity.getPrepayid());
                        wxPay.setSign(payOrderRequestEntity.getSign());
                        wxPay.setAppId(payOrderRequestEntity.getAppid());
                        wxPay.setPartnerId(payOrderRequestEntity.getPartnerid());
                        wxPay.setTimeStamp(payOrderRequestEntity.getTimestamp());
                        wxPay.setPackageStr(payOrderRequestEntity.getPackageStr());
                        WXPayTool wxPayTool = new WXPayTool(ConfirmOrderNewActivity.this, weiXin.getParam().getAppid());
                        wxPayTool.payRequest(wxPay);
                    }

                    @Override
                    public void onError(WeixinOrderAddResponseObject o) {
                        dismissProgressDialog();
                        Toast.makeText(ConfirmOrderNewActivity.this, o.getErrorMsg(), Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case ALI_PAY:
                AliPayTool aliPayTool = new AliPayTool(ConfirmOrderNewActivity.this);
                aliPayTool.setOnAliPayResultListener(new AliPayTool.OnAliPayResultListener() {
                    @Override
                    public void onPaySuccess() {
                        paySuccess();
                    }

                    @Override
                    public void onPayError(String resultInfo) {
                        payAlertTip.show();
                    }
                });
                aliPayTool.pay(aliParams);
                break;

        }
    }

    private void payAlertTip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(), R.layout.app_user_login_tip, null);
        TextView textView = (TextView) view.findViewById(R.id.message);
        Button button = (Button) view.findViewById(R.id.cancel_button);
        textView.setText("该订单未支付，可以到订单列表中继续支付");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payAlertTip.dismiss();
                finish();
            }
        });
        builder.setView(view);
        builder.setCancelable(false);
        payAlertTip = builder.create();
    }

    private void calculate() {
        for (ShopcartListParam param : goods) {
            //积分商品计算总积分
            if (isJFGoods == 1) {
                totalPrice += param.getIntegral() * param.getNum();
            } else {
                totalPrice += param.getPrice() * param.getNum();
            }
//         returnPrice+=param.getPrice()*param.getReturnBate()*param.getNum();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                AddrListResponseParam address = (AddrListResponseParam) data.getSerializableExtra("data");
                if (null != address) {
                    nameView.setText(address.getName());
                    telView.setText(address.getPhone());
                    addressView.setText(address.getAddr() + "   " + address.getInfo());
                    addId = address.getAddrId();
                }
            }
        }
        if (data == null) {
            return;
        }

    }

    /**
     * 查询惠宝
     */
    private void loadUser() {

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
                money = o.getMember().getMoney();
                integral = o.getMember().getIntegral();
//            idCardCheckStatus = o.getMember().getIdCardCheckStatus();
            }

            @Override
            public void onError(MemberInfoResponseObject o) {
                ToastView.show(o.getErrorMsg());
            }
        });
    }

    //加载数据字典  快递费  免邮额度
    private void loadDataIndex() {
//      showProgressDialog("正在加载");
        AppDicInfoParam appDicInfoParam = new AppDicInfoParam();
        AppDicInfoRequestObject appDicInfoRequestObject = new AppDicInfoRequestObject();
        appDicInfoRequestObject.setParam(appDicInfoParam);
        httpTool.post(appDicInfoRequestObject, URLConfig.DATA_INDEX, new HttpTool.HttpCallBack<AppConfigureResponseObject>() {
            @Override
            public void onSuccess(AppConfigureResponseObject o) {
//            dismissProgressDialog();
                List<AppConfigureResponseParam> dataList = o.getDataList();
                for (AppConfigureResponseParam app : dataList) {
                    if (app.getName().equals(CustomConfig.GOODSORDER_SENDFEE)) {
                        if (!app.getContent().equals("") && app.getContent() != null) {
//                     kdfee = app.getContent();
                            try {
                                kdfee = Double.valueOf(app.getContent()).doubleValue();
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                kdfee = 0.0;
                            }
                        }
                    }
                    if (app.getName().equals(CustomConfig.GOODSORDER_FREE_SENDFEE)) {
                        if (!app.getContent().equals("") && app.getContent() != null) {
//                     kdfee = app.getContent();
                            try {
                                freeFee = Double.valueOf(app.getContent()).doubleValue();
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                freeFee = 0.0;
                            }
                        }
                    }

                }


                if (totalPrice < freeFee) {
                    paid.setText(kdfee + "元");
                    totalPrice += kdfee;
                } else {
                    paid.setText("免费配送");

                }
                if (isJFGoods == 1) {
                    tv_zongjia.setText(totalPrice + "积分");
                    payTotalView.setText(totalPrice + "积分");
                } else {
                    tv_zongjia.setText(totalPrice + "元");
                    payTotalView.setText(totalPrice + "元");
                }

            }

            @Override
            public void onError(AppConfigureResponseObject o) {
                //           dismissProgressDialog();
                ToastView.show(o.getErrorMsg());
            }
        });
    }
}
