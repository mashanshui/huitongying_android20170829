package com.huixiangshenghuo.app.comm.model;

import com.doumee.model.response.base.ResponseBaseObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
public class ShopcartListObject extends ResponseBaseObject implements Serializable {
   private static final long serialVersionUID = -7548901365357463190L;
   private List<ShopcartListParam> recordList;

   public ShopcartListObject() {
   }

   public List<ShopcartListParam> getRecordList() {
      return this.recordList;
   }

   public void setRecordList(List<ShopcartListParam> recordList) {
      this.recordList = recordList;
   }
}

