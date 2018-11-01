package com.huixiangshenghuo.app.comm.http;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.comm.store.SaveObjectTool;
import com.huixiangshenghuo.app.view.ToastView;
import com.doumee.model.request.base.RequestBaseObject;
import com.doumee.model.response.base.ResponseBaseObject;
import com.doumee.model.response.userinfo.UserInfoResponseParam;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 网络请求
 */
public class HttpTool {

    private WeakReference<Activity> weakReference;
    private HttpUtils httpUtils;
    private HttpRequest.HttpMethod requestMethod;

    public static HttpTool newInstance(Activity context){
        HttpTool httpTool = new HttpTool(context);
        return  httpTool;
    }

    private HttpTool(Activity context){
        weakReference = new WeakReference<Activity>(context);
        httpUtils = new HttpUtils();
        requestMethod = HttpRequest.HttpMethod.POST;
    }


    public void post(RequestBaseObject p, String url, final HttpCallBack mCallBack){
        RequestParams params = buildRequestParams(p);
        httpUtils.send(requestMethod, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                final String str = parseData(responseInfo.result);
                Type typeClazz = mCallBack.getClass().getGenericInterfaces()[0];
                Type type = ((ParameterizedType) typeClazz).getActualTypeArguments()[0];
                Object e = JSON.parseObject(str, type);
                Activity activity = weakReference.get();
                if(null != e && null != activity){
                    final ResponseBaseObject re = (ResponseBaseObject)e;
                    final String error = re.getErrorCode();
                    final String msg = re.getErrorMsg();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(TextUtils.equals(error,"00000")){
                                mCallBack.onSuccess(re);
                                Log.e("HTTP_NET","请求的返回:----"+str);
                            }else{
                                Log.e("请求失败",msg);
                                 mCallBack.onError(re);
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(HttpException e, String s) {
                Activity activity = weakReference.get();
                if(null != activity){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastView.show("网络错误");
                        }
                    });
                }
            }
        });
    }

    private RequestParams buildRequestParams(RequestBaseObject p){
        String userId = "";
        UserInfoResponseParam user = SaveObjectTool.openUserInfoResponseParam();
        if (null != user){
            userId = user.getMemberId();
        }
        RequestBaseObject requestParams = p;
        requestParams.setUserId(userId);
        requestParams.setAppDeviceNumber(SystemUtil.getIMEI());
        requestParams.setPlatform(CustomConfig.PLATFORM);
        requestParams.setVersion(CustomConfig.API_VERSION);
        RequestParams params = new RequestParams();
        params.addHeader("Content-Type", "application/json");
        String jsonParams = JSON.toJSONString(requestParams);
        Log.e("请求参数",jsonParams);

        byte[] bytes = CompressUtil.compressByGzip(jsonParams.getBytes());//Gzip压缩处理
        try {
            bytes = EncryptUtil.encryptDES(bytes, CustomConfig.NET_KEY);//加密操作
            params.setBodyEntity(new StringEntity(new String(bytes, "utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }
    private String parseData(String str) {
        String sendString = "";
        try {
            byte[] response = str.getBytes("UTF-8");
            byte[] newstrsi = EncryptUtil.decryptDES(response, CustomConfig.NET_KEY);//解密
            byte[] newstrsan = CompressUtil.uncompressByGzip(newstrsi, "UTF-8");//解压缩
            sendString = new String(newstrsan, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendString;
    }

    public interface HttpCallBack<T> {
        void onSuccess(T t);
        void onError(T t);
    }


}
