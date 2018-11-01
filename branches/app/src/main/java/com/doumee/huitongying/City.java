package com.huixiangshenghuo.app;

public class City {
   private String order;
   private String time;
   // 图标
   private int resIcon;
   private String income;
   private String xl;

   private void City() {

   }

   public City(String order, String time, String income, int resIcon, String xl) {
      super();
      this.order = order;
      this.time = time;
      this.income = income;
      this.resIcon = resIcon;
      this.xl = xl;
   }

   public void setResIcon(int resIcon) {
      this.resIcon = resIcon;
   }

   public void setXl(String xl) {
      this.xl = xl;
   }

   public int getResIcon() {
      return resIcon;
   }

   public String getXl() {
      return xl;
   }

   public void setOrder(String order) {
      this.order = order;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public void setIncome(String income) {
      this.income = income;
   }

   public String getOrder() {
      return order;
   }

   public String getTime() {
      return time;
   }

   public String getIncome() {
      return income;
   }

   @Override
   public String toString() {
      return "City{" +
            "order='" + order + '\'' +
            ", time='" + time + '\'' +
            ", resIcon=" + resIcon +
            ", income='" + income + '\'' +
            ", xl='" + xl + '\'' +
            '}';
   }
}
