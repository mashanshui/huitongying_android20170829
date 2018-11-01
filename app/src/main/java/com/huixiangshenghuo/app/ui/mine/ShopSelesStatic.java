package com.huixiangshenghuo.app.ui.mine;

/**
 * Created by Administrator on 2017/2/9.
 * 商品静态用来更新 商品列表
 */

public class ShopSelesStatic {
   /**
    * ShopSeles_static = 0 不更新  ShopSeles_static = 1 更新
    * ShopSeles_static = 2 不更新 ShopSeles_static = 3 更新
    */
   private static String ShopSeles_static;

   public static void setShopSeles_static(String shopSeles_static) {
      ShopSeles_static = shopSeles_static;
   }

   public static String getShopSeles_static() {
      return ShopSeles_static;
   }

}
