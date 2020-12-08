package com.xxx.myapplication.ui.activity.chat;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.xxx.myapplication.ConfigClass;
import com.xxx.myapplication.R;
import com.xxx.myapplication.base.App;
import com.xxx.myapplication.base.AppManager;
import com.xxx.myapplication.base.BaseActivity;
import com.xxx.myapplication.model.db.GreenDaoUtil;
import com.xxx.myapplication.model.db.entry.WebSocketMessageBean;
import com.xxx.myapplication.model.db.greendao.WebSocketMessageBeanDao;
import com.xxx.myapplication.model.http.Api;
import com.xxx.myapplication.model.http.ApiCallback;
import com.xxx.myapplication.model.http.bean.FriendBean;
import com.xxx.myapplication.model.http.bean.UserInfoBean;
import com.xxx.myapplication.model.socket.WebSocketService;
import com.xxx.myapplication.model.sp.SharedConst;
import com.xxx.myapplication.model.sp.SharedPreferencesUtil;
import com.xxx.myapplication.model.util.NotificationUtil;
import com.xxx.myapplication.ui.activity.friend.AddListActivity;
import com.xxx.myapplication.ui.adapter.MainAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.main_name)
    TextView mName;
    @BindView(R.id.main_notice_number)
    TextView mNumber;
    @BindView(R.id.main_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.main_not_data)
    TextView mNotData;

    private List<FriendBean> mList = new ArrayList<>();
    private MainAdapter mAdapter;

    private MainReceiver mReceiver;
    private WebSocketService.MyBinder mBinder;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        //检查是否开启通知栏
        NotificationUtil.isOpen(this);

        //启动通知
        mReceiver = new MainReceiver();
        IntentFilter filter = new IntentFilter(ConfigClass.ACTION_RECEIVE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, filter);

        //启动服务
        bindService(new Intent(this, WebSocketService.class), conn, Service.BIND_AUTO_CREATE);

        //加载首页列表
        mList = new ArrayList<>();
        mAdapter = new MainAdapter(mList);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @OnClick(R.id.main_notice_btn)
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.main_notice_btn:
                startActivity(new Intent(MainActivity.this, AddListActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        //首先加载好友列表并返回
        super.onResume();
        loadInfo();
        loadList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        cleanList(position);
        adapter.notifyItemChanged(position);
    }


    //-----------------------------网络数据层-------------------------//


    //查找未读通知
    private void initTotal() {
        int totalNum = 0;
        WebSocketMessageBeanDao dao = GreenDaoUtil.getInstance(MainActivity.this).getWebSocketMessageBeanDao();
        List<WebSocketMessageBean> loadAll = dao.loadAll();
        for (int i = 0; i < loadAll.size(); i++) {
            if (loadAll.get(i).getSendType() == 3 && loadAll.get(i).getStatus() == 2) {
                totalNum++;
            }
        }

        if (totalNum != 0) {
            mNumber.setVisibility(View.VISIBLE);
            mNumber.setText(totalNum + "");
        } else {
            mNumber.setVisibility(View.GONE);
        }
    }

    //清空通知小红点
    private void cleanTotal() {
        WebSocketMessageBeanDao dao = GreenDaoUtil.getInstance(MainActivity.this).getWebSocketMessageBeanDao();
        List<WebSocketMessageBean> loadAll = dao.loadAll();
        for (int i = 0; i < loadAll.size(); i++) {
            if (loadAll.get(i).getSendType() == 3 && loadAll.get(i).getStatus() == 2) {
                WebSocketMessageBean bean = loadAll.get(i);
                bean.setStatus(1);
                dao.update(bean);
            }
        }

        mNumber.setVisibility(View.GONE);
    }

    //获取用户信息
    private void loadInfo() {
        Api.getInstance().getUserInfo(SharedPreferencesUtil.getInstance().getInt(SharedConst.VALUE_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiCallback<UserInfoBean>(this) {

                    @Override
                    public void onSuccess(UserInfoBean bean) {
                        mName.setText(bean.getNickName());
                    }
                });
    }

    //获取全部条目
    private void loadList() {
        Api.getInstance().getFriendList(SharedPreferencesUtil.getInstance().getInt(SharedConst.VALUE_ID), 1)
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
                });
    }

    //清空指定条目未读信息
    private void cleanList(int position) {
        WebSocketMessageBeanDao dao = GreenDaoUtil.getInstance(MainActivity.this).getWebSocketMessageBeanDao();
        List<WebSocketMessageBean> webSocketMessageBeans = dao.loadAll();
        for (int i = 0; i < webSocketMessageBeans.size(); i++) {
            WebSocketMessageBean bean = webSocketMessageBeans.get(i);
            bean.setStatus(1);
            webSocketMessageBeans.set(i, bean);
            dao.update(bean);
        }


        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        FriendBean dataBean = mList.get(position);
        intent.putExtra("userId", dataBean.getFriendId());
        intent.putExtra("userName", dataBean.getNickName());
        startActivity(intent);
    }

    //----------------------服务通信层-------------------------------//


    /**
     * Service接口和Activity通信
     */
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //获取Service的OnBind方法所返回的MyBinder对象
            mBinder = (WebSocketService.MyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    /**
     * MainActivity接受消息发送给Service
     */
    public void sendMessage(int friendId, String content, int messageType) {
        mBinder.sendMessage(friendId, content, messageType);
    }

    /**
     * 广播接收Service消息
     */
    class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 1);
            if (type == 2) {
                String value = intent.getStringExtra("message");
                WebSocketMessageBean messageBean = new Gson().fromJson(value, WebSocketMessageBean.class);
                if (messageBean.getSendType() == 1) {   //聊天内容
                    chatBroadcast(messageBean);
                } else if (messageBean.getSendType() == 3) {    //通知内容
                    addFriendBroadcast(messageBean);
                }
            }
        }
    }


    //添加好友消息通知
    private void addFriendBroadcast(WebSocketMessageBean bean) {
        //如果是2就添加到数据库中 说明我们已经成为好友了
        if (bean.getMessageType() == 2) {
            WebSocketMessageBeanDao dao = GreenDaoUtil.getInstance(MainActivity.this).getWebSocketMessageBeanDao();
            //消息标记为未读
            bean.setMessageType(1);
            bean.setStatus(2);
            dao.insert(bean);
        }

        //通知到状态栏
        notification(bean, 2);
        //触发音效
        playRingtone();

        //如果添加列表页面 就刷新添加列表
        AddListActivity activity = (AddListActivity) AppManager.getInstance().getActivity(AddListActivity.class.getSimpleName());
        if (activity != null) {
            //清空首页消息提示图标
            cleanTotal();
            //刷新好友添加列表
            activity.getFriendList();
            //重置首页通知图标
            initTotal();
            //重置首页列表
            loadList();
        } else {        //如果不在 就刷新当前
            //重置首页通知图标
            initTotal();
            //重置首页列表
            loadList();
        }
    }

    //聊天消息刷新提示
    private void chatBroadcast(WebSocketMessageBean bean) {
        //消息标记为未读
        bean.setStatus(2);

        //第一步 插入数据库
        WebSocketMessageBeanDao dao = GreenDaoUtil.getInstance(MainActivity.this).getWebSocketMessageBeanDao();
        dao.insert(bean);

        int friendId = bean.getSendId();
        ChatActivity activity = (ChatActivity) AppManager.getInstance().getActivity(ChatActivity.class.getSimpleName());
        //如果在聊天页面 就去刷新聊天
        if (activity != null) {
            //判断当前消息是否是这个人
            if (activity.userId == friendId) {
                //如果是 就直接刷新
                activity.loadData();
            } else {
                //如果不是 就需要给出通知
                playRingtone();
                notification(bean, 1);
            }
        } else {
            //如果在首页 就刷新首页条目
            for (int i = 0; i < mList.size(); i++) {
                FriendBean dataBean = mList.get(i);
                if (dataBean.getFriendId() == friendId) {
                    mList.set(i, dataBean);
                    mAdapter.notifyItemChanged(i);
                }
            }
            //并给出通知音效
            playRingtone();
            //如果当前在桌面 就给出通知
            if (!AppManager.getInstance().isForeground(MainActivity.class.getSimpleName())) {
                notification(bean, 1);
            }
        }
    }


    /**
     * 通知音效
     */
    private void playRingtone() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(App.getContext(), uri);
        rt.play();
    }

    /**
     * 通知状态栏
     */
    private void notification(WebSocketMessageBean messageBean, int id) {
        String name = "系统通知";
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getUserId() == messageBean.getSendId()) {
                name = mList.get(i).getNickName();
            }
        }
        NotificationUtil.showNotification(MainActivity.this, messageBean, name, id);
    }

}
