package com.xxx.myapplication.ui.activity.friend;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xxx.myapplication.R;
import com.xxx.myapplication.base.BaseActivity;
import com.xxx.myapplication.base.LoadingDialog;
import com.xxx.myapplication.model.http.Api;
import com.xxx.myapplication.model.http.ApiCallback;
import com.xxx.myapplication.model.http.bean.FriendBean;
import com.xxx.myapplication.model.http.bean.ResultBean;
import com.xxx.myapplication.model.sp.SharedConst;
import com.xxx.myapplication.model.sp.SharedPreferencesUtil;
import com.xxx.myapplication.ui.adapter.AddFriendAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddFriendActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.add_edit)
    EditText mAccountEdit;
    @BindView(R.id.add_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.add_not_data)
    TextView mNotData;

    private List<FriendBean> mList = new ArrayList<>();
    private AddFriendAdapter mAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add;
    }

    @Override
    protected void initData() {
        mAdapter = new AddFriendAdapter(mList);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(this);
    }

    @OnClick({R.id.add_return, R.id.add_btn})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.add_return:
                finish();
                break;
            case R.id.add_btn:
                searchFriendList();
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        sendAddFriend(mList.get(position).getFriendId());
    }


    /**
     * 查询用户
     */
    private void searchFriendList() {
        String account = mAccountEdit.getText().toString();

        final LoadingDialog loadingDialog = LoadingDialog.getInstance(this);
        Api.getInstance().searchFriendList(SharedPreferencesUtil.getInstance().getInt(SharedConst.VALUE_ID), account)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiCallback<List<FriendBean>>(this) {

                    @Override
                    public void onSuccess(List<FriendBean> list) {
                        if (list != null && list.size() != 0) {
                            mRecycler.setVisibility(View.VISIBLE);
                            mNotData.setVisibility(View.GONE);
                            mList.clear();
                            mList.addAll(list);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mRecycler.setVisibility(View.GONE);
                            mNotData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        super.onError(errorCode, errorMessage);
                        Toast.makeText(AddFriendActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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

    /**
     * 发送请求
     */
    private void sendAddFriend(int friendId) {
        final LoadingDialog loadingDialog = LoadingDialog.getInstance(this);
        Api.getInstance().sendAddFriend(SharedPreferencesUtil.getInstance().getInt(SharedConst.VALUE_ID), friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiCallback<ResultBean>(this) {

                    @Override
                    public void onSuccess(ResultBean bean) {
                        Toast.makeText(AddFriendActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        super.onError(errorCode, errorMessage);
                        Toast.makeText(AddFriendActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
