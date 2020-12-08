package com.xxx.myapplication.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xxx.myapplication.R;
import com.xxx.myapplication.model.http.bean.FriendBean;
import com.xxx.myapplication.model.sp.SharedPreferencesUtil;

import java.util.List;

public class AddListAdapter extends BaseQuickAdapter<FriendBean, BaseViewHolder> {

    public AddListAdapter(@Nullable List<FriendBean> data) {
        super(R.layout.friend_add, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendBean item) {
        helper.setText(R.id.item_add_name, item.getNickName())
                .addOnClickListener(R.id.item_add_true)
                .addOnClickListener(R.id.item_add_false);

        View btn = helper.getView(R.id.item_add_btn);
        TextView btn2 = helper.getView(R.id.item_add_btn_2);

        int userId = SharedPreferencesUtil.getInstance().getInt("userId");
        if (item.getUserId() == userId) {   //发起者视角
            Integer status = item.getStatus();
            if (status == -1) {
                btn.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.GONE);
            } else if (status == 0) {
                btn.setVisibility(View.GONE);
                btn2.setVisibility(View.VISIBLE);
                btn2.setText("等待对方确认中");
            } else if (status == 5) {
                btn.setVisibility(View.GONE);
                btn2.setVisibility(View.VISIBLE);
                btn2.setText("对方已拒绝");
            } else {
                btn.setVisibility(View.GONE);
                btn2.setVisibility(View.VISIBLE);
                btn2.setText("添加成功");
            }
        } else {  //被发起者视角
            Integer status = item.getStatus();
            if (status == 0) {
                btn.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.GONE);
            } else if (status == 5) {
                btn.setVisibility(View.GONE);
                btn2.setVisibility(View.VISIBLE);
                btn2.setText("您已拒绝");
            } else {
                btn.setVisibility(View.GONE);
                btn2.setVisibility(View.VISIBLE);
                btn2.setText("添加成功");
            }
        }
    }
}
