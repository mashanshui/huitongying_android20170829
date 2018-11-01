package com.huixiangshenghuo.app.comm.app;

import java.text.DecimalFormat;

/**
 * Created by lenovo on 2016/12/15.
 */
public class NumberFormatTool {

    private static DecimalFormat decimalFormat = new DecimalFormat("######0.00");

    private static DecimalFormat decimalFormatTo4 = new DecimalFormat("######0.0000");

    public static String numberFormat(double d){
        return  decimalFormat.format(d);
    }

    public static String numberFormatTo4(double d){
        return  decimalFormatTo4.format(d);
    }

    public static String floatFormat(float f){
        return  decimalFormat.format(f);
    }
}
