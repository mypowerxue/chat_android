package com.xxx.myapplication.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.xxx.myapplication.R;
import com.xxx.myapplication.model.db.entry.WebSocketMessageBean;
import com.xxx.myapplication.model.http.bean.MP4Bean;
import com.xxx.myapplication.model.sp.SharedPreferencesUtil;
import com.xxx.myapplication.model.util.GlideUtil;
import com.xxx.myapplication.ui.window.BigImageWindow;

import java.util.List;

public class ChatAdapter extends BaseQuickAdapter<WebSocketMessageBean, BaseViewHolder> {

    private final int userId;

    public ChatAdapter(@Nullable List<WebSocketMessageBean> data) {
        super(R.layout.chat_item, data);
        userId = SharedPreferencesUtil.getInstance().getInt("userId");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(BaseViewHolder helper, WebSocketMessageBean item) {
        helper.addOnClickListener(R.id.item_chat_image_1)
                .addOnClickListener(R.id.item_chat_image_2)
                .addOnClickListener(R.id.item_chat_mp4_1)
                .addOnClickListener(R.id.item_chat_mp4_2);

        helper.addOnLongClickListener(R.id.item_chat_image_1)
                .addOnLongClickListener(R.id.item_chat_image_2)
                .addOnLongClickListener(R.id.item_chat_content_1)
                .addOnLongClickListener(R.id.item_chat_content_2)
                .addOnLongClickListener(R.id.item_chat_mp4_1)
                .addOnLongClickListener(R.id.item_chat_mp4_2);

        if (item.getReceiveId() == userId) {
            helper.getView(R.id.item_chat_1).setVisibility(View.VISIBLE);
            helper.getView(R.id.item_chat_2).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.item_chat_1).setVisibility(View.GONE);
            helper.getView(R.id.item_chat_2).setVisibility(View.VISIBLE);
        }
        TextView textView1 = helper.getView(R.id.item_chat_content_1);
        TextView textView2 = helper.getView(R.id.item_chat_content_2);
        ImageView imageView1 = helper.getView(R.id.item_chat_image_1);
        ImageView imageView2 = helper.getView(R.id.item_chat_image_2);
        TextView mp4Tv1 = helper.getView(R.id.item_chat_mp4_1);
        TextView mp4Tv2 = helper.getView(R.id.item_chat_mp4_2);

        textView1.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        imageView1.setVisibility(View.GONE);
        imageView2.setVisibility(View.GONE);
        mp4Tv1.setVisibility(View.GONE);
        mp4Tv2.setVisibility(View.GONE);

        switch (item.getMessageType()) {
            case 1: //文字消息
                if (item.getReceiveId() == userId) {
                    textView1.setVisibility(View.VISIBLE);
                    helper.setText(R.id.item_chat_content_1, item.getMessage());
                } else {
                    textView2.setVisibility(View.VISIBLE);
                    helper.setText(R.id.item_chat_content_2, item.getMessage());
                }
                break;
            case 2: //图片消息
                if (item.getReceiveId() == userId) {
                    imageView1.setVisibility(View.VISIBLE);
                    GlideUtil.loadBase(mContext, item.getMessage(), imageView1);
                } else {
                    imageView2.setVisibility(View.VISIBLE);
                    GlideUtil.loadBase(mContext, item.getMessage(), imageView2);
                }
                break;
            case 3: //语音消息
                String message = item.getMessage();
                MP4Bean mp4Bean = new Gson().fromJson(message, MP4Bean.class);
                if (item.getReceiveId() == userId) {
                    mp4Tv1.setText((mp4Bean.getTime() / 1000) + "");
                    mp4Tv1.setVisibility(View.VISIBLE);
                } else {
                    mp4Tv2.setText((mp4Bean.getTime() / 1000) + "");
                    mp4Tv2.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
