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

public class ForgetPswActivity extends BaseActivity {

    @BindView(R.id.forget_account_edit)
    EditText mAccountEdit;
    @BindView(R.id.forget_password_edit)
    EditText mPasswordEdit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget_psw;
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.forget_btn})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.forget_btn:
                forgetPsw();
                break;
        }
    }

    /**
     * 忘记密码
     */
    private void forgetPsw() {
        String account = mAccountEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();

        final LoadingDialog loadingDialog = LoadingDialog.getInstance(this);
        Api.getInstance().forgetPsw(account, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiCallback<ResultBean>(this) {

                    @Override
                    public void onSuccess(ResultBean bean) {
                        Toast.makeText(ForgetPswActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        super.onError(errorCode, errorMessage);
                        Toast.makeText(ForgetPswActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
