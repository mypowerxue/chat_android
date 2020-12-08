package com.xxx.myapplication.ui.activity.login;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xxx.myapplication.R;
import com.xxx.myapplication.base.BaseActivity;
import com.xxx.myapplication.base.LoadingDialog;
import com.xxx.myapplication.model.http.Api;
import com.xxx.myapplication.model.http.ApiCallback;
import com.xxx.myapplication.model.http.bean.LoginBean;
import com.xxx.myapplication.model.sp.SharedConst;
import com.xxx.myapplication.model.sp.SharedPreferencesUtil;
import com.xxx.myapplication.ui.activity.chat.MainActivity;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_account_edit)
    EditText mAccountEdit;
    @BindView(R.id.login_password_edit)
    EditText mPasswordEdit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.login_forget, R.id.login_register, R.id.login_btn})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.login_forget:
                startActivity(new Intent(LoginActivity.this, ForgetPswActivity.class));
                break;
            case R.id.login_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.login_btn:
                login();
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        String account = mAccountEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();

        final LoadingDialog loadingDialog = LoadingDialog.getInstance(this);
        Api.getInstance().login(account, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiCallback<LoginBean>(this) {

                    @Override
                    public void onSuccess(LoginBean bean) {
                        SharedPreferencesUtil.getInstance().saveString(SharedConst.VALUE_TOKEN, bean.getToken());
                        SharedPreferencesUtil.getInstance().saveInt(SharedConst.VALUE_ID, bean.getUserId());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        super.onError(errorCode, errorMessage);
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
