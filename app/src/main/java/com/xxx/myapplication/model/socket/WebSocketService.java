package com.xxx.myapplication.model.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.xxx.myapplication.ConfigClass;
import com.xxx.myapplication.base.App;
import com.xxx.myapplication.base.AppManager;
import com.xxx.myapplication.model.db.GreenDaoUtil;
import com.xxx.myapplication.model.db.greendao.WebSocketMessageBeanDao;
import com.xxx.myapplication.model.db.entry.WebSocketMessageBean;
import com.xxx.myapplication.model.sp.SharedPreferencesUtil;
import com.xxx.myapplication.ui.activity.chat.ChatActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketService extends Service {

    //WebSocket对象
    private WebSocketClient mSocketClient;

    //这里内部类
    private MyBinder binder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        initService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void sendMessage(WebSocketMessageBean bean) {
        if (mSocketClient != null && mSocketClient.isConnecting()) {
            mSocketClient.send(new Gson().toJson(bean));
        } else {
            initService();
        }
    }

    //初始化链接
    private void initService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int userId = SharedPreferencesUtil.getInstance().getInt("userId");
                    //TODO 这里URL 别忘了切换到自己的IP
                    mSocketClient = new WebSocketClient(new URI(ConfigClass.SOCKET_URL + userId), new Draft_6455()) {
                        @Override
                        public void onOpen(ServerHandshake handshake) {
                            Log.e("webSocket_onOpen", "打开通道");

                            Intent intent = new Intent("com.xxx.myapplication.model.socket.WebSocketService");
                            intent.putExtra("type", 1);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        }

                        @Override
                        public void onMessage(String message) {
                            Log.e("webSocket_onMessage", message);

                            Intent intent = new Intent(ConfigClass.ACTION_RECEIVE);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 2);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        }

                        @Override
                        public void onClose(int code, String reason, boolean remote) {
                            Log.e("webSocket_onClose", "链接关闭");

                            Intent intent = new Intent("com.xxx.myapplication.model.socket.WebSocketService");
                            intent.putExtra("type", 3);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        }

                        @Override
                        public void onError(Exception ex) {
                            Log.e("webSocket_onError", "链接错误");

                            Intent intent = new Intent("com.xxx.myapplication.model.socket.WebSocketService");
                            intent.putExtra("type", 4);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        }
                    };
                    mSocketClient.connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public class MyBinder extends Binder {

        public void sendMessage(int friendId, String message, int messageType) {
            int userId = SharedPreferencesUtil.getInstance().getInt("userId");
            WebSocketMessageBean webSocketMessageBean = new WebSocketMessageBean();
            webSocketMessageBean.setMessage(message);
            webSocketMessageBean.setReceiveId(friendId);
            webSocketMessageBean.setSendId(userId);
            webSocketMessageBean.setSendType(1);
            webSocketMessageBean.setMessageType(messageType);

            WebSocketMessageBeanDao dao = GreenDaoUtil.getInstance(WebSocketService.this).getWebSocketMessageBeanDao();
            dao.insert(webSocketMessageBean);
            WebSocketService.this.sendMessage(webSocketMessageBean);

            ChatActivity activity = (ChatActivity) AppManager.getInstance().getActivity(ChatActivity.class.getSimpleName());
            if (activity != null) {
                activity.loadData();
            }
        }
    }

}
