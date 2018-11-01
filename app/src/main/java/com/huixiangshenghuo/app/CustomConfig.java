package com.huixiangshenghuo.app;

import android.os.Environment;

import java.io.File;

/**
 * Created by lenovo on 2016/12/9.
 */
public class CustomConfig {

    public static final String APP_INFO = "app_info";
    public static final String APP_LOGIN_STATUS = "status";
    public static final String CITY_CONTENT = "city_content";
    public static final String CITY_ID = "city_id";
    public static final String CITY_NAME = "city_name";
    public static final String START_COUNT = "start_count";
    public static final String MESSAGE_COUNT = "msg_count";
    public static final String APP_IS_RUNNING = "running";
    public static final int[] GUIDE_IMAGE = {R.mipmap.q01, R.mipmap.q02, R.mipmap.q03};//引导页图片
    public static final int LOAD_TIME = 3000;//启动页加载时间
    public static final int LOGIN = 1;//已经登录
    public static final int LOGOUT = 0;//退出登录
    public static String FILE_PATH = "/doumee";
    //FTP 配置 和 图片路径配置
    public static final String HTTP_PORT = ":8080";
//    public static final String FTP_URL = "39.107.246.201";
    public static final String FTP_URL = "interface.booco.cn";
    public static final int FTP_PORT = 2122;
    public static final String LOGIN_NAME = "huixiang";
    public static final String LOGIN_PWD = "123456";

    //   public static final String HTTP_PORT = ":2122";
/*  public static final String HTTP_PORT = ":80";
   public static final String FTP_URL = "211.149.182.190";
    public static final int FTP_PORT = 2122;
   public static final String LOGIN_NAME = "test";
   public static final String LOGIN_PWD = "test@hthy2017";*/

    public static final String API_VERSION = "1.0";
    public static final String PLATFORM = "0";
    public static final String NET_KEY = "ABD#-*EY";

       public static final String BASE_URL = "http://interface.booco.cn/huitongying_interface/do?c=";//测试版本
//    public static final String BASE_URL = "http://192.168.42.248:8087/huitongying_interface/do?c=";//测试版本
//   public static final String BASE_URL = "http://192.168.1.102:8080/huitongying_interface/do?c=";//测试版本
    //   public static final String BASE_URL = "http://211.149.182.190:80/huitongying_interface/do?c=";//正式版本

    public static final int PAGE_SIZE = 10;
    //    public static final String RMB = "¥";
    public static final String RMB = "￥";
    public static final String INTEGRAL = "积分:";
    //积分商品
    public static final String INTEGRAL_GOODS = "2";
    //普通商品
    public static final String NORMAL_GOODS = "0";


    public static final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/doumee_huitongying" + File.separator + "image" + File.separator;
    public static final String SHARE_IMAGE_PATH = Environment.getExternalStorageDirectory() + "/doumee_huitongying" + File.separator + "share" + File.separator;
    public static final String SHOP_IMAGE_PATH_XC = Environment.getExternalStorageDirectory() + "/doumee_huitongying" + File.separator + "shopxc" + File.separator;
    private static final String FTP_ROOT_PATH = "/huitongying/";
    public static final String MEMBER_IMAGE = FTP_ROOT_PATH + "member/";//会员头像路径
    public static final String CATEGORY_IMAGE = FTP_ROOT_PATH + "category/";//分类图标
    public static final String SHOP_IMAGE = FTP_ROOT_PATH + "shop/";//商家图片
    public static final String AD_IMAGE = FTP_ROOT_PATH + "ad/";//广告图片]

    public static final String SHOP_PRODUCT = FTP_ROOT_PATH + "product/";//商品图片
    public static final String USER_CODE_IMAGE = Environment.getExternalStorageDirectory() + "/doumee_huitongying" + File.separator + "image" + File.separator + "user_code" + ".jpg";
    public static final String USER_SHARE_IMAGE = Environment.getExternalStorageDirectory() + "/doumee_huitongying" + File.separator + "image" + File.separator + "user_share" + ".jpg";


    public static final String DATA_INDEX_MINE_INTEGRAL = "MEM_TRANS_RATE";//会员每日积分转换率
    public static final String DATA_INDEX_VIP_INTEGRAL = "VIP_TRANS_RATE";//VIP每日积分转化率
    public static final String DATA_INDEX_UP_VIP_MONEY = "MEMBER_UPGRADE_FEE";//升级VIP费用
    public static final String DATA_INDEX_SHOP_MONEY = "SHOP_ADD_FEE";//商家保证金
    public static final String SERVICE_PHONE = "SERVICE_PHONE";//客服电话
    public static final String ABOUT_US = "ABOUT_US";//关于
    public static final String SYS_PROTOCOL = "SYS_PROTOCOL";//用户协议
    public static final String RECOMMEND_CONTENT = "RECOMMEND_CONTENT";//分享内容
    public static final String SHARE_LINK = "SHARE_LINK";//分享链接
    public static final String DATA_INDEX_TIXIAN = "MEMBER_WITHDRAW_RATE";//会员提现手续费率
    public static final String DATA_INDEX_SHOP_TIXIAN = "SHOP_WITHDRAW_RATE";//商户提现手续费
    public static final String SHOP_WITHDRAW_PUBLIC_RATE = "SHOP_WITHDRAW_PUBLIC_RATE";//商家提现公益资金比例
    public static final String MEMBER_WITHDRAW_PUBLIC_RATE = "MEMBER_WITHDRAW_PUBLIC_RATE";//会员提现公益资金比例
    public static final String SHOP_PROTOCOL = "SHOP_PROTOCOL";//商家协议
    public static final String GOODSORDER_FREE_SENDFEE = "GOODSORDER_FREE_SENDFEE";//免邮额度
    public static final String GOODSORDER_SENDFEE = "GOODSORDER_SENDFEE";//快递费

}
