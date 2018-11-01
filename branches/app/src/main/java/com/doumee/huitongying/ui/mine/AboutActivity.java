package com.huixiangshenghuo.app.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.huixiangshenghuo.app.ui.BaseActivity;
import com.doumee.model.request.appDicInfo.AppDicInfoParam;
import com.doumee.model.request.appDicInfo.AppDicInfoRequestObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseObject;
import com.doumee.model.response.appDicInfo.AppConfigureResponseParam;
import com.huixiangshenghuo.app.CustomConfig;
import com.huixiangshenghuo.app.R;
import com.huixiangshenghuo.app.URLConfig;
import com.huixiangshenghuo.app.comm.http.HttpTool;

import java.util.List;

public class AboutActivity extends BaseActivity {


    private TextView versionView,contentView;

    public static void startAboutActivity(Context context){
        Intent intent = new Intent(context,AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        loadDataIndex();

    }
    private void initView(){
        initTitleBar_1();
        titleView.setText("关于我们");
        versionView = (TextView)findViewById(R.id.version);
        contentView = (TextView)findViewById(R.id.content);
        String version = getString(R.string.app_name) + "v" + CustomConfig.API_VERSION;
        versionView.setText(version);

        //    contentView.setText("\u3000\u3000"+"2012年5月21日大连万达与全球第二大院线集团AMC签署并购协议，万达以26亿美元并购AMC。北京时间2012年9月5日凌");
    }

    //加载数据字典
    public void loadDataIndex() {
        AppDicInfoParam appDicInfoParam = new AppDicInfoParam();
        AppDicInfoRequestObject appDicInfoRequestObject = new AppDicInfoRequestObject();
        appDicInfoRequestObject.setParam(appDicInfoParam);
        httpTool.post(appDicInfoRequestObject, URLConfig.DATA_INDEX, new HttpTool.HttpCallBack<AppConfigureResponseObject>() {
            @Override
            public void onSuccess(AppConfigureResponseObject o) {
                List<AppConfigureResponseParam> dataList = o.getDataList();
                for (AppConfigureResponseParam app : dataList) {
                    if (app.getName().equals(CustomConfig.ABOUT_US)) {
                        contentView.setText("\u3000\u3000" + app.getContent());
                        break;
                    }
                }
            }

            @Override
            public void onError(AppConfigureResponseObject o) {

            }
        });
    }
}
