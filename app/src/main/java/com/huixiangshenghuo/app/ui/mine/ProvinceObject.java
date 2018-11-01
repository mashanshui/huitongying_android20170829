package com.huixiangshenghuo.app.ui.mine;

import com.doumee.model.response.base.ResponseBaseObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public class ProvinceObject extends ResponseBaseObject implements Serializable {
   private static final long serialVersionUID = 4446123729612031804L;
   private List<ProvinceParam> lstProvince;

   public ProvinceObject() {
   }

   public String toString() {
      return "ProvinceResponseObject [" + (this.lstProvince != null ? "lstProvince=" + this.lstProvince : "") + "]";
   }

   public List<ProvinceParam> getLstProvince() {
      return this.lstProvince;
   }

   public void setLstProvince(List<ProvinceParam> lstProvince) {
      this.lstProvince = lstProvince;
   }
}
