package com.huixiangshenghuo.app.ui.mine;


import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public class ProvinceParam implements Serializable {
   private static final long serialVersionUID = -1724190430508751055L;
   private String name;
   private String provinceId;
   private List<CityParam> lstCity;

   public ProvinceParam() {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getProvinceId() {
      return this.provinceId;
   }

   public void setProvinceId(String provinceId) {
      this.provinceId = provinceId;
   }

   public String toString() {
      return "ProvinceResponseParam [" + (this.lstCity != null ? "lstCity=" + this.lstCity + ", " : "") + (this.name != null ? "name=" + this.name + ", " : "") + (this.provinceId != null ? "provinceId=" + this.provinceId : "") + "]";
   }

   public static long getSerialversionuid() {
      return -1724190430508751055L;
   }

   public List<CityParam> getLstCity() {
      return this.lstCity;
   }

   public void setLstCity(List<CityParam> lstCity) {
      this.lstCity = lstCity;
   }
}

