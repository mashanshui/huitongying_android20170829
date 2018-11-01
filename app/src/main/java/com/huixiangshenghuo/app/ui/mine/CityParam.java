package com.huixiangshenghuo.app.ui.mine;


import com.doumee.model.response.userinfo.AreaResponseParam;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public class CityParam implements Serializable {
   private static final long serialVersionUID = -1724190430508751055L;
   private String cityName;
   private String cityId;
   private String provinceName;
   private List<AreaResponseParam> lstArea;
   private boolean expand;

   public CityParam() {
   }

   public boolean isExpand() {
      return expand;
   }

   public String getProvinceName() {
      return provinceName;
   }

   public void setProvinceName(String provinceName) {
      this.provinceName = provinceName;
   }

   public void setExpand(boolean expand) {
      this.expand = expand;
   }

   public static long getSerialversionuid() {
      return -1724190430508751055L;
   }

   public List<AreaResponseParam> getLstArea() {
      return this.lstArea;
   }

   public void setLstArea(List<AreaResponseParam> lstArea) {
      this.lstArea = lstArea;
   }

   public String getCityName() {
      return this.cityName;
   }

   public void setCityName(String cityName) {
      this.cityName = cityName;
   }

   public String getCityId() {
      return this.cityId;
   }

   public void setCityId(String cityId) {
      this.cityId = cityId;
   }
}


