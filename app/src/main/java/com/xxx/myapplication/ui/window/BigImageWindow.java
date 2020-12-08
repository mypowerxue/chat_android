package com.xxx.myapplication.ui.window;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xxx.myapplication.R;

public class BigImageWindow extends AlertDialog {

    public static BigImageWindow getInstance(Context context, Bitmap bitmap) {
        return new BigImageWindow(context, bitmap);
    }

    private BigImageWindow(Context context, Bitmap bitmap) {
        super(context);

        //渲染布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.window_big_image, null);
        ImageView viewById = view.findViewById(R.id.big_image_img);
        viewById.setImageBitmap(bitmap);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        super.show();   //展示
        setContentView(view);

        //设置大小
        final Window window = getWindow();
        if (window != null) {
            final WindowManager.LayoutParams lp = window.getAttributes();

            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            window.setAttributes(layoutParams);

            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    window.setAttributes(lp);
                }
            });
        }
    }
}
