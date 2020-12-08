package com.xxx.myapplication.ui.activity.friend;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xxx.myapplication.R;
import com.xxx.myapplication.base.BaseActivity;
import com.xxx.myapplication.base.LoadingDialog;
import com.xxx.myapplication.model.db.GreenDaoUtil;
import com.xxx.myapplication.model.db.entry.WebSocketMessageBean;
import com.xxx.myapplication.model.db.greendao.WebSocketMessageBeanDao;
import com.xxx.myapplication.model.http.Api;
import com.xxx.myapplication.model.http.ApiCallback;
import com.xxx.myapplication.model.http.bean.FriendBean;
import com.xxx.myapplication.model.http.bean.ResultBean;
import com.xxx.myapplication.model.sp.SharedConst;
import com.xxx.myapplication.model.sp.SharedPreferencesUtil;
import com.xxx.myapplication.ui.adapter.AddListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddListActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.add_friend_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.add_friend_not_data)
    TextView mNotData;

    private List<FriendBean> mList = new ArrayList<>();
    private AddListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_friend;
    }

    @Override
    protected void initData() {
        mAdapter = new AddListAdapter(mList);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(this);
    }

    @OnClick({R.id.add_friend_return, R.id.add_friend_add})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.add_friend_return:
                finish();
                break;
            case R.id.add_friend_add:
                startActivity(new Intent(AddListActivity.this, AddFriendActivity.class));
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.item_add_true) {
            confirmAddFriend(mList.get(position).getUserId(), 1);
        } else {
            confirmAddFriend(mList.get(position).getUserId(), 2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFriendList();
    }


    /**
     * 查询用户
     */
    public void getFriendList() {
        Api.getInstance().getFriendList(SharedPreferencesUtil.getInstance().getInt(SharedConst.VALUE_ID), 0)
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
                        Toast.makeText(AddListActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 确认添加好友
     */
    private void confirmAddFriend(final int friendId, int type) {
        final LoadingDialog loadingDialog = LoadingDialog.getInstance(this);
        final int userId = SharedPreferencesUtil.getInstance().getInt(SharedConst.VALUE_ID);
        Api.getInstance().confirmAddFriend(type, userId, friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiCallback<ResultBean>(this) {

                    @Override
                    public void onSuccess(ResultBean bean) {
                        WebSocketMessageBeanDao dao = GreenDaoUtil.getInstance(AddListActivity.this).getWebSocketMessageBeanDao();
                        WebSocketMessageBean webSocketMessageBean = new WebSocketMessageBean();
                        webSocketMessageBean.setCreateTime(new SimpleDateFormat("mm-dd hh:MM:ss").format(System.currentTimeMillis()));
                        webSocketMessageBean.setSendId(userId);
                        webSocketMessageBean.setReceiveId(friendId);
                        webSocketMessageBean.setStatus(2);
                        webSocketMessageBean.setMessageType(1);
                        webSocketMessageBean.setSendType(1);
                        webSocketMessageBean.setMessage("我们已经是好友了");
                        dao.insert(webSocketMessageBean);
                        Toast.makeText(AddListActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        super.onError(errorCode, errorMessage);
                        Toast.makeText(AddListActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
