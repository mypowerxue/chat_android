package com.xxx.myapplication.ui.activity.login;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xxx.myapplication.R;
import com.xxx.myapplication.base.BaseActivity;
import com.xxx.myapplication.base.LoadingDialog;
import com.xxx.myapplication.model.http.Api;
import com.xxx.myapplication.model.http.ApiCallback;
import com.xxx.myapplication.model.http.bean.ResultBean;

import butterknife.BindView;
import butterknife.OnClick;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.register_name_edit)
    EditText mNameEdit;
    @BindView(R.id.register_account_edit)
    EditText mAccountEdit;
    @BindView(R.id.register_password_edit)
    EditText mPasswordEdit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.register_btn})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.register_btn:
                register();
                break;
        }
    }



    /**
     * 注册
     */
    private void register() {
        String account = mAccountEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();
        String name = mNameEdit.getText().toString();

        final LoadingDialog loadingDialog = LoadingDialog.getInstance(this);
        Api.getInstance().register(account, password, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiCallback<ResultBean>(this) {

                    @Override
                    public void onSuccess(ResultBean bean) {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        super.onError(errorCode, errorMessage);
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStart(Disposable d) {
                        super.onStart(d);
                        loadingDialog.show();
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        loadingDialog.dismiss();
                    }
                });
    }

}
