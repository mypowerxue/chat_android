package com.xxx.myapplication.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private ImmersionBar mImmersionBar;
    private boolean isShowData;  //用于绑定Activity与网络请求的生命周期是否可以加载数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        //可以加载数据
        isShowData = true;

        //添加到AppManager中
        AppManager.getInstance().addActivity(this);

        //初始化ButterKnife
        ButterKnife.bind(this);

        //沉浸式状态栏
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();

        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //      修改状态栏字体颜色
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }

        //初始化数据
        initData();
    }

    //是否可以展示数据
    public boolean isShowData() {
        return isShowData;
    }

    @Override
    public void finish() {
        super.finish();
        AppManager.getInstance().finishActivity(this);
    }

    @Override
    protected void onDestroy() {
        isShowData = false;
        if (mImmersionBar != null) mImmersionBar.destroy();
        super.onDestroy();
    }

    //获取到Layout的ID
    protected abstract int getLayoutId();

    //初始化数据
    protected abstract void initData();

}