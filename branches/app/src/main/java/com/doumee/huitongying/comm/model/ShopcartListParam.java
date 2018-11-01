package com.huixiangshenghuo.app.comm.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
public class ShopcartListParam implements Serializable {
   private static final long serialVersionUID = 4165361833781569082L;
   private String proId;
   private String shopcartId;
   private String proName;

   private double price;
   private int stock;
   private String proImg;
   private int num;

   private boolean choose;

   private double kdfee;

   private double freeFee;

   public void setChoose(boolean choose) {
      this.choose = choose;
   }

   public boolean isChoose() {
      return choose;
   }

   public ShopcartListParam() {
   }

   public String getProId() {
      return this.proId;
   }

   public void setProId(String proId) {
      this.proId = proId;
   }

   public String getShopcartId() {
      return this.shopcartId;
   }

   public void setShopcartId(String shopcartId) {
      this.shopcartId = shopcartId;
   }

   public String getProName() {
      return this.proName;
   }

   public void setProName(String proName) {
      this.proName = proName;
   }


   public double getPrice() {
      return this.price;
   }

   public void setPrice(double price) {
      this.price = price;
   }

   public String getProImg() {
      return this.proImg;
   }

   public void setProImg(String proImg) {
      this.proImg = proImg;
   }

   public int getStock() {
      return this.stock;
   }

   public void setStock(int stock) {
      this.stock = stock;
   }

   public int getNum() {
      return this.num;
   }

   public void setNum(int num) {
      this.num = num;
   }

   public void setKdfee(double kdfee) {
      this.kdfee = kdfee;
   }

   public void setFreeFee(double freeFee) {
      this.freeFee = freeFee;
   }

   public double getKdfee() {
      return kdfee;
   }

   public double getFreeFee() {
      return freeFee;
   }
}

