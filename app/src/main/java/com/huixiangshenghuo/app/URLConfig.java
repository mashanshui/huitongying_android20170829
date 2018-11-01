package com.huixiangshenghuo.app;

/**
 * Created by lenovo on 2016/12/20.
 */
public class URLConfig {

    public static final String LOGIN = CustomConfig.BASE_URL + "1001";//登录
    public static final String USER_INFO = CustomConfig.BASE_URL + "1002";//用户信息
    public static final String UPDATE_USER_INFO = CustomConfig.BASE_URL + "1003";//修改个人资料
    public static final String REG = CustomConfig.BASE_URL + "1004";//注册
    public static final String AD_LIST = CustomConfig.BASE_URL + "1005";//广告列表
    public static final String ORDER_LIST = CustomConfig.BASE_URL + "1006";//账单列表
    public static final String MINE_TUI_JIAN = CustomConfig.BASE_URL + "1007";//直推会员列表
    public static final String RECOMMENDED_BUSINESS = CustomConfig.BASE_URL + "1008";//推荐的商家列表
    public static final String GOODS_MENU = CustomConfig.BASE_URL + "1009";//商家 商品一级菜单列表
    public static final String GOODS_MENU_LIST = CustomConfig.BASE_URL + "1010";//根据（商家、商品）一级分类编码查询二级分类
    public static final String SHOP_LIST = CustomConfig.BASE_URL + "1011";//商家列表
    public static final String SHOP_INFO = CustomConfig.BASE_URL + "1012";//商家详情
    public static final String USER_ADD_ADDRESS = CustomConfig.BASE_URL + "1013";//新增收货地址
    public static final String USER_EDIT_ADDRESS = CustomConfig.BASE_URL + "1014";//修改收货地址
    public static final String USER_DEL_ADDRESS = CustomConfig.BASE_URL + "1015";//删除收货地址
    public static final String USER_ADDRESS_LIST = CustomConfig.BASE_URL + "1016";//收货地址列表
    public static final String UPDATE_PAY_PASSWORD = CustomConfig.BASE_URL + "1017";//修改支付密码
    public static final String UPDATE_PASSWORD = CustomConfig.BASE_URL + "1018";//修改密码
    public static final String EDIT_PASSWORD = CustomConfig.BASE_URL + "1019";//忘记密码
    public static final String EDIT_PAY_PASSWORD = CustomConfig.BASE_URL + "1020";//忘记支付密码
    public static final String SYSTEM_INFORMATION = CustomConfig.BASE_URL + "1021";//系统消息列表
    public static final String READBACK = CustomConfig.BASE_URL + "1022";//意见反馈
    public static final String DATA_INDEX = CustomConfig.BASE_URL + "1023";//数据字典
    public static final String APPVERSION = CustomConfig.BASE_URL + "1024";//检查更新接口
    public static final String SEND_CODE_USER = CustomConfig.BASE_URL + "1025";//根据用户获取验证码
    public static final String SEND_CODE = CustomConfig.BASE_URL + "1026";//根据手机号获取验证码
    public static final String USER_BY_LOGIN_NAME = CustomConfig.BASE_URL + "1027";//根据登录账户查询用户信息
    public static final String APPLY_SHOP = CustomConfig.BASE_URL + "1028";//申请成为商家
    public static final String ORDER_SHOP_PAY = CustomConfig.BASE_URL + "1029";//到店消费
    public static final String USER_UP_VIP = CustomConfig.BASE_URL + "1030";//升级VIP
    public static final String MY_ORDER_LIST = CustomConfig.BASE_URL + "1031";//我的订单
    public static final String ORDER_INFO = CustomConfig.BASE_URL + "1032";//账单详情
    public static final String ORDER_UPDATE = CustomConfig.BASE_URL + "1033";//更新订单
    public static final String ORDER_CREATE = CustomConfig.BASE_URL + "1034";//线上订单生成
    public static final String ORDER_WECHAT_PAY = CustomConfig.BASE_URL + "1035";//微信支付生成订单
    public static final String ORDER_PAY_RESULT = CustomConfig.BASE_URL + "1036";//订单支付结果
    public static final String USER_LIST_BY_LOGIN_NAME = CustomConfig.BASE_URL + "1037";//查询人员列表
    public static final String CITY_LIST = CustomConfig.BASE_URL + "1038";//城市列表
    public static final String USER_TIXIAN = CustomConfig.BASE_URL + "1039";//积分提现
    public static final String ORDER_COMMENT = CustomConfig.BASE_URL + "1040";//订单评论
    public static final String SHOP_COMMENT = CustomConfig.BASE_URL + "1041";//商家评论
    public static final String PRODUCT_REVIEWS = CustomConfig.BASE_URL + "1042";//商品评论记录列表
    public static final String SYS_MESSAGE = CustomConfig.BASE_URL + "1043";//通知列表
    public static final String CHECK_USER = CustomConfig.BASE_URL + "1044";//实名认证
    public static final String USER_PAY_MONEY = CustomConfig.BASE_URL + "1045";//会员充值
    public static final String SHOP_UPDATE = CustomConfig.BASE_URL + "1046";//修改商家
    public static final String SHOP_QUERY = CustomConfig.BASE_URL + "1047";//商品查询列表
    public static final String PRODUCT_DETAILS = CustomConfig.BASE_URL + "1048";//商品详情
    public static final String SHOP_INCOME_DETAILS = CustomConfig.BASE_URL + "1049";//店铺收入明细接口
    public static final String NOVICE_GUIDE = CustomConfig.BASE_URL + "1050";//新手指南列表
    public static final String CART_LIST = CustomConfig.BASE_URL + "1051";//购物车列表
    public static final String CART_GOOD_EDIT = CustomConfig.BASE_URL + "1053";//购物车商品删除、设置数量
    public static final String CLEAR_CART = CustomConfig.BASE_URL + "1052";//清除购物车
    public static final String SHOP_CART_ADD = CustomConfig.BASE_URL + "1053";//加入购物车
    public static final String REC_PEOPLE_NUM = CustomConfig.BASE_URL + "1054";//查询用户推荐人数数量





















    public static final String USER_ZHUANZHANG = CustomConfig.BASE_URL + "1046";//会员转账
    public static final String QIAN_DAO = CustomConfig.BASE_URL + "1050";//会员签到
    public static final String SHOP_FL_NEWADD = CustomConfig.BASE_URL + "1055";//店铺商品分类新增
    public static final String SHOP_FL_EDITO = CustomConfig.BASE_URL + "1056";//店铺商品分类编辑
    public static final String SHOP_FL_DEL = CustomConfig.BASE_URL + "1057";//店铺商品分类删除
    public static final String SHOP_CATE = CustomConfig.BASE_URL + "1058";//店铺商品分类列表
    public static final String SHOP_PRODUCT_ADD = CustomConfig.BASE_URL + "1059";//商家添加商品
    public static final String SHOP_EDIT = CustomConfig.BASE_URL + "1060";//商家编辑商品
    public static final String SHOP_DEL = CustomConfig.BASE_URL + "1061";//商家删除商品
    public static final String SHOP_ADD_PIC = CustomConfig.BASE_URL + "1062";//批量删除商家图片
    public static final String SHOP_DEL_PIC = CustomConfig.BASE_URL + "1063";//批量删除商家图片
    public static final String SHOP_PICS = CustomConfig.BASE_URL + "1064";//查询商家图片
    public static final String SHOP_AD_EDIT = CustomConfig.BASE_URL + "1065";//商家更新公告
    public static final String SHANGP_ADD_PIC = CustomConfig.BASE_URL + "1067";//添加商家图片
    public static final String SHANGP_DEL_PIC = CustomConfig.BASE_URL + "1068";//批量删除商家图片
    public static final String SHANGP_PICS = CustomConfig.BASE_URL + "1069";//查询商家图片
    public static final String SHOP_COLLECT = CustomConfig.BASE_URL + "1019";//收藏列表
    public static final String DIS_COLLECT = CustomConfig.BASE_URL + "1018";//取消收藏
    public static final String COLLECT = CustomConfig.BASE_URL + "1017";//收藏商店 商品


//    public static final String GOODS_MENU = CustomConfig.BASE_URL + "1008";//商品一级菜单列表
//    public static final String GOODS_MENU_LIST = CustomConfig.BASE_URL + "1009";//商品二级菜单列表
//    public static final String SHOP_LIST = CustomConfig.BASE_URL + "1011";//商家列表
//    public static final String SHOP_INFO = CustomConfig.BASE_URL + "1012";//商家详情
//    public static final String USER_ADD_ADDRESS = CustomConfig.BASE_URL + "1013";//新增收货地址
//    public static final String USER_EDIT_ADDRESS = CustomConfig.BASE_URL + "1014";//修改收货地址
//    public static final String USER_DEL_ADDRESS = CustomConfig.BASE_URL + "1015";//删除收货地址
//    public static final String USER_ADDRESS_LIST = CustomConfig.BASE_URL + "1016";//收货地址列表
//    public static final String COLLECT = CustomConfig.BASE_URL + "1017";//收藏商店 商品
//    public static final String DIS_COLLECT = CustomConfig.BASE_URL + "1018";//取消收藏
//    public static final String SHOP_COLLECT = CustomConfig.BASE_URL + "1019";//收藏列表
//    public static final String UPDATE_PAY_PASSWORD = CustomConfig.BASE_URL + "1020";//修改支付密码
//    public static final String UPDATE_PASSWORD = CustomConfig.BASE_URL + "1021";//修改密码
//    public static final String EDIT_PASSWORD = CustomConfig.BASE_URL + "1022";//忘记密码
//    public static final String EDIT_PAY_PASSWORD = CustomConfig.BASE_URL + "1023";//忘记支付密码
//    public static final String READBACK = CustomConfig.BASE_URL + "1025";//意见反馈
//    public static final String DATA_INDEX = CustomConfig.BASE_URL + "1026";//数据字典
//    public static final String APPVERSION = CustomConfig.BASE_URL + "1027";//检查更新接口
//    public static final String SEND_CODE_USER = CustomConfig.BASE_URL + "1028";//根据用户获取验证码
//    public static final String SEND_CODE = CustomConfig.BASE_URL + "1029";//根据手机号获取验证码
//    public static final String USER_BY_LOGIN_NAME = CustomConfig.BASE_URL + "1030";//根据登录账户查询用户信息
//    public static final String APPLY_SHOP = CustomConfig.BASE_URL + "1031";//申请成为商家
//    public static final String ORDER_SHOP_PAY = CustomConfig.BASE_URL + "1032";//到店消费
//    public static final String USER_UP_VIP = CustomConfig.BASE_URL + "1033";//升级VIP
//    public static final String MY_ORDER_LIST = CustomConfig.BASE_URL  +"1034";//我的订单
//    public static final String ORDER_INFO = CustomConfig.BASE_URL + "1035";//账单详情
//    public static final String ORDER_UPDATE = CustomConfig.BASE_URL + "1036";//更新订单
//    public static final String ORDER_CREATE = CustomConfig.BASE_URL + "1037";//线上订单生成
//    public static final String ORDER_WECHAT_PAY = CustomConfig.BASE_URL + "1038";//微信支付生成订单
//    public static final String ORDER_PAY_RESULT = CustomConfig.BASE_URL + "1039";//订单支付结果
//    public static final String USER_LIST_BY_LOGIN_NAME = CustomConfig.BASE_URL + "1040";//查询人员列表
//    public static final String CITY_LIST = CustomConfig.BASE_URL + "1041";//城市列表
//    public static final String USER_TIXIAN = CustomConfig.BASE_URL + "1042";//积分提现
//    public static final String ORDER_COMMENT = CustomConfig.BASE_URL + "1043";//订单评论
//    public static final String SHOP_COMMENT = CustomConfig.BASE_URL  + "1044";//商家评论
//    public static final String USER_ZHUANZHANG = CustomConfig.BASE_URL + "1046";//会员转账
//    public static final String CHECK_USER = CustomConfig.BASE_URL + "1047";//实名认证
//    public static final String SYS_MESSAGE = CustomConfig.BASE_URL + "1048";//通知
//    public static final String USER_PAY_MONEY = CustomConfig.BASE_URL  + "1049";//会员充值
//    public static final String QIAN_DAO = CustomConfig.BASE_URL + "1050";//会员签到
//    public static final String MINE_TUI_JIAN = CustomConfig.BASE_URL + "1051";//我的推荐
//    public static final String SHOP_UPDATE = CustomConfig.BASE_URL + "1052";//修改商家
//    public static final String SHOP_QUERY = CustomConfig.BASE_URL + "1053";//商品查询列表
//    public static final String SHOP_FL_NEWADD = CustomConfig.BASE_URL + "1055";//店铺商品分类新增
//    public static final String SHOP_FL_EDITO = CustomConfig.BASE_URL + "1056";//店铺商品分类编辑
//    public static final String SHOP_FL_DEL = CustomConfig.BASE_URL + "1057";//店铺商品分类删除
//    public static final String SHOP_CATE = CustomConfig.BASE_URL + "1058";//店铺商品分类列表
//    public static final String SHOP_PRODUCT_ADD = CustomConfig.BASE_URL + "1059";//商家添加商品
//    public static final String SHOP_EDIT = CustomConfig.BASE_URL + "1060";//商家编辑商品
//    public static final String SHOP_DEL = CustomConfig.BASE_URL + "1061";//商家删除商品
//    public static final String SHOP_ADD_PIC = CustomConfig.BASE_URL + "1062";//批量删除商家图片
//    public static final String SHOP_DEL_PIC = CustomConfig.BASE_URL + "1063";//批量删除商家图片
//    public static final String SHOP_PICS = CustomConfig.BASE_URL + "1064";//查询商家图片
//    public static final String SHOP_AD_EDIT = CustomConfig.BASE_URL + "1065";//商家更新公告
//    public static final String SHANGP_ADD_PIC = CustomConfig.BASE_URL + "1067";//添加商家图片
//    public static final String SHANGP_DEL_PIC = CustomConfig.BASE_URL + "1068";//批量删除商家图片
//    public static final String SHANGP_PICS = CustomConfig.BASE_URL + "1069";//查询商家图片
//
//    public static final String CART_LIST = CustomConfig.BASE_URL + "1014";//购物车列表
//    public static final String CART_GOOD_EDIT = CustomConfig.BASE_URL + "1015";//购物车数量加减
//    public static final String CLEAR_CART = CustomConfig.BASE_URL + "1016";//批量删除或清除购物车
//    public static final String SHOP_CART_ADD = CustomConfig.BASE_URL + "1017";//加入购物车
}
