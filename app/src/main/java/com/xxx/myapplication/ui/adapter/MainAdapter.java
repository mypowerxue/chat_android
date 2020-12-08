package com.xxx.myapplication.ui.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xxx.myapplication.R;
import com.xxx.myapplication.model.db.entry.WebSocketMessageBean;
import com.xxx.myapplication.model.db.greendao.WebSocketMessageBeanDao;
import com.xxx.myapplication.model.db.GreenDaoUtil;
import com.xxx.myapplication.model.http.bean.FriendBean;

import java.util.List;

public class MainAdapter extends BaseQuickAdapter<FriendBean, BaseViewHolder> {

    public MainAdapter(@Nullable List<FriendBean> data) {
        super(R.layout.friend_item, data);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    protected void convert(BaseViewHolder helper, FriendBean item) {
        helper.setText(R.id.item_friend_name, item.getNickName());

        WebSocketMessageBeanDao dao = GreenDaoUtil.getInstance(mContext).getWebSocketMessageBeanDao();
        List<WebSocketMessageBean> list = dao.loadAll();
        int num = 0;
        for (int i = 0; i < list.size(); i++) {
            WebSocketMessageBean bean = list.get(i);
            if (
                //如果这条消息是我发给你的 或者是你发给我的 我就保存
                    (bean.getSendId() == item.getUserId() && bean.getReceiveId() == item.getFriendId()) ||
                            (bean.getSendId() == item.getFriendId() && bean.getReceiveId() == item.getUserId())
                    ) {
                Integer status = bean.getStatus();
                if (status != null && status == 2) {    //未读
                    num++;
                }
            } else {
                list.remove(i);
                i--;
            }
        }
        if (list.size() == 0) return;

        WebSocketMessageBean bean = list.get(list.size() - 1);
        String message = bean.getMessage();
        if (bean.getMessageType() == 2) {
            message = "[图片]";
        }
        if (bean.getMessageType() == 3) {
            message = "[语音]";
        }

        String createTime = bean.getCreateTime();

        helper.setText(R.id.item_friend_message, message)
                .setText(R.id.item_friend_time, createTime);

        TextView textView = helper.getView(R.id.item_friend_count);
        if (num != 0) {
            textView.setText(num + "");
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}
