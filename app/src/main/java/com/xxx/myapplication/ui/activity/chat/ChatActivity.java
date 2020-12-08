package com.xxx.myapplication.ui.activity.chat;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.xxx.myapplication.R;
import com.xxx.myapplication.base.AppManager;
import com.xxx.myapplication.base.BaseActivity;
import com.xxx.myapplication.model.db.GreenDaoUtil;
import com.xxx.myapplication.model.db.entry.WebSocketMessageBean;
import com.xxx.myapplication.model.db.greendao.WebSocketMessageBeanDao;
import com.xxx.myapplication.model.http.Api;
import com.xxx.myapplication.model.http.ApiCallback;
import com.xxx.myapplication.model.http.bean.FileInfoBean;
import com.xxx.myapplication.model.http.bean.MP4Bean;
import com.xxx.myapplication.model.sp.SharedConst;
import com.xxx.myapplication.model.sp.SharedPreferencesUtil;
import com.xxx.myapplication.model.util.CameraUtil;
import com.xxx.myapplication.model.util.KeyBoardUtil;
import com.xxx.myapplication.model.util.PermissionUtil;
import com.xxx.myapplication.model.util.RecordingUtil;
import com.xxx.myapplication.ui.adapter.ChatAdapter;
import com.xxx.myapplication.ui.window.BigImageWindow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import butterknife.OnTouch;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;

public class ChatActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemChildLongClickListener {

    @BindView(R.id.chat_edit)
    EditText mContentEdit;
    @BindView(R.id.chat_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.chat_name)
    TextView mName;

    private List<WebSocketMessageBean> mList = new ArrayList<>();
    private ChatAdapter mAdapter;

    public int userId;
    private int position;
    private String tag;

    private RecordingUtil recordingUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();

        //判断是否是通知点过来的
        long id = intent.getLongExtra("id", -1);
        if (id != -1) {  //如果是 就关闭通知
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancel((int) id);
        }

        userId = intent.getIntExtra("userId", 0);
        String userName = intent.getStringExtra("userName");
        mName.setText(userName);

        mAdapter = new ChatAdapter(mList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);  //键盘弹出
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.setOnItemChildLongClickListener(this);

        loadData();

        //初始化音频播放
        recordingUtil = new RecordingUtil();
    }

    @OnClick({R.id.chat_add_image, R.id.chat_send, R.id.chat_return})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.chat_add_image:
                tag = "相机";
                CameraUtil.openPhoto(this); //发送图片
                break;
            case R.id.chat_send:
                sendTextMessage();  //发送消息
                break;
            case R.id.chat_return:
                finish();
                break;
        }
    }

    @OnTouch({R.id.chat_add_mp4})
    public boolean OnTouch(MotionEvent event, View view) {
        switch (view.getId()) {
            case R.id.chat_add_mp4:
                tag = "";
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!PermissionUtil.checkPermission(this, PermissionUtil.RECORD_PERMISSION)) {
                        recordingUtil.startRecording(this);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    MP4Bean mp4Bean = recordingUtil.stopRecording();
                    if (mp4Bean != null) {
                        if (mp4Bean.getTime() > 1500) {
                            sendMp4Message(new File(mp4Bean.getFileUrl()), mp4Bean.getTime());
                        } else {
                            Toast.makeText(this, "说话时间太短", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.item_chat_image_1:
            case R.id.item_chat_image_2:
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap bitmap = view.getDrawingCache();
                BigImageWindow.getInstance(this, bitmap);
                break;
            case R.id.item_chat_mp4_1:
            case R.id.item_chat_mp4_2:
                String message = mList.get(position).getMessage();
                MP4Bean mp4Bean = new Gson().fromJson(message, MP4Bean.class);
                if (recordingUtil.isPlay()) {
                    recordingUtil.playMp4(mp4Bean.getFileUrl());
                } else {
                    recordingUtil.stopMp4();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        recordingUtil.stopMp4();
    }

    @Override
    public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
        this.position = position;
        view.setOnCreateContextMenuListener(this);//为Button单独实现ContextMenu
        ChatActivity.this.registerForContextMenu(view);//长按页面空白地方会弹出Me
        return false;
    }


    //同步本地数据库
    public void loadData() {
        WebSocketMessageBeanDao dao = GreenDaoUtil.getInstance(this).getWebSocketMessageBeanDao();
        List<WebSocketMessageBean> webSocketMessageBeans = dao.loadAll();
        for (int i = 0; i < webSocketMessageBeans.size(); i++) {
            if (webSocketMessageBeans.get(i).getSendId() != userId && webSocketMessageBeans.get(i).getReceiveId() != userId) {
                webSocketMessageBeans.remove(i);
                i--;
            }
        }
        mList.clear();
        mList.addAll(webSocketMessageBeans);
        mAdapter.notifyDataSetChanged();
        mRecycler.scrollToPosition(mList.size() - 1);
    }


    //发送消息
    public void sendMessage(String content, int messageType) {
        MainActivity activity = (MainActivity) AppManager.getInstance().getActivity(MainActivity.class.getSimpleName());
        activity.sendMessage(userId, content, messageType);
    }


    /**
     * 发送文本消息
     */
    public void sendTextMessage() {
        String content = mContentEdit.getText().toString();
        mContentEdit.setText("");
        sendMessage(content, 1);
    }


    /**
     * 发送图片消息
     */
    private void sendImageMessage(File file) {
        MultipartBody.Part part = Api.getFileRequestBody(file.getPath());
        Api.getInstance().sendImageChat(SharedPreferencesUtil.getInstance().getInt(SharedConst.VALUE_ID), part)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiCallback<FileInfoBean>(this) {

                    @Override
                    public void onSuccess(FileInfoBean bean) {
                        sendMessage(bean.getFileUrl(), 2);
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        super.onError(errorCode, errorMessage);
                        Toast.makeText(ChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 发送语音消息
     */
    private void sendMp4Message(File file, final long time) {
        MultipartBody.Part part = Api.getFileRequestBody(file.getPath());
        Api.getInstance().sendMp4Chat(SharedPreferencesUtil.getInstance().getInt(SharedConst.VALUE_ID), part)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiCallback<FileInfoBean>(this) {

                    @Override
                    public void onSuccess(FileInfoBean bean) {
                        MP4Bean mp4Bean = new MP4Bean();
                        mp4Bean.setFileUrl(bean.getFileUrl());
                        mp4Bean.setTime(time);
                        sendMessage(new Gson().toJson(mp4Bean), 3);
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        super.onError(errorCode, errorMessage);
                        Toast.makeText(ChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ("相机".equals(tag)) {
            CameraUtil.openPhoto(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CameraUtil.onActivityResult(this, requestCode, resultCode, data, new CameraUtil.CallBack() {
            @Override
            public void callback(Bitmap bitmap, File file) {
                sendImageMessage(file);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {//这里是ContextMenu参数
        menu.add(1, 1, 1, "删除");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        WebSocketMessageBean messageBean = mList.get(position);
        switch (item.getItemId()) {
            case 1:
                WebSocketMessageBeanDao dao = GreenDaoUtil.getInstance(this).getWebSocketMessageBeanDao();
                dao.delete(messageBean);
                mList.remove(position);
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }


}
