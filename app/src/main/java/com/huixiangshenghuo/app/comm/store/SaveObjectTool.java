package com.huixiangshenghuo.app.comm.store;


import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.huixiangshenghuo.app.CustomApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 保存实例化对象
 * Created by lenovo on 2016/11/15.
 */
public class SaveObjectTool {

    private static final String USER_INFO = "user.dat";

    public static void saveObject(UserInfoResponseParam o){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        CustomApplication application = CustomApplication.getCustomApplication();
        try {
            fos = application.openFileOutput(USER_INFO, application.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
        } catch (Exception e) {
            e.printStackTrace();
            //这里是保存文件产生异常
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    //fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    //oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    public static UserInfoResponseParam openUserInfoResponseParam(){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        CustomApplication application = CustomApplication.getCustomApplication();
        try {
            fis = application.openFileInput(USER_INFO);
            ois = new ObjectInputStream(fis);
            return (UserInfoResponseParam) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            //这里是读取文件产生异常
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    //fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    //ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        //读取产生异常，返回null
        return null;
    }

}
