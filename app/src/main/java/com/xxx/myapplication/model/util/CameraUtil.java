package com.xxx.myapplication.model.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 相机工具类
 */
public class CameraUtil{

    //相册请求码
    private static final int REQUEST_CODE_ALBUM = 0;
    //相机请求码
    private static final int REQUEST_CODE_CAMERA = 1;
    //剪裁请求码
    private static final int REQUEST_CODE_CROP = 2;

    private static File tempFile;   //临时文件
    private static Uri imageCropUri;    //临时Uri

    /**
     * 从相册获取图片
     */
    public static void openPhoto(Activity activity) {
        if (PermissionUtil.checkPermission(activity)) return;

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, REQUEST_CODE_ALBUM);
    }

    /**
     * 从相机获取图片
     */
    public static void openCamera(Activity activity) {
        if (PermissionUtil.checkPermission(activity)) return;
        //用于保存调用相机拍照后所生成的文件
        //跳转到调用系统相机
        //用于保存调用相机拍照后所生成的文件

        tempFile = new File(Environment.getExternalStorageDirectory().getPath(), UUID.randomUUID() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            //先判断是否有权限
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, activity.getApplication().getPackageName() + ".fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 回调方法
     */
    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent, CallBack callBack) {
        switch (requestCode) {
            // 调用相机后返回
            case REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    //用相机返回的照片去调用剪裁也需要对Uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(activity,activity.getApplication().getPackageName() + ".fileprovider", tempFile);
                        cropPhoto(activity, contentUri);//裁剪图片
                    } else {
                        cropPhoto(activity, Uri.fromFile(tempFile));//裁剪图片
                    }
                }
                break;
            //调用相册后返回
            case REQUEST_CODE_ALBUM:
//                if (resultCode == Activity.RESULT_OK) {
//                    Uri uri = intent.getData();
//                    cropPhoto(activity, uri);//裁剪图片
//                }
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = intent.getData();
                    try {
                        Bitmap image = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));
                        if (callBack != null) callBack.callback(image, saveImage(image));
                    } catch (Exception ignored) {

                    }
                }
                break;
            //调用剪裁后返回
            case REQUEST_CODE_CROP:
                //在这里获得了剪裁后的Bitmap对象，可以用于上传
                try {
                    Bitmap image = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageCropUri));
                    if (callBack != null) callBack.callback(image, saveImage(image));
                } catch (Exception ignored) {

                }
                break;
        }
    }



    /**
     * 裁剪图片
     */
    private static void cropPhoto(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);

        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        imageCropUri = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + UUID.randomUUID() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, REQUEST_CODE_CROP);
    }


    /**
     * 保存图片到本地
     *
     * @param bmp
     * @return File
     */
    private static File saveImage(Bitmap bmp) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 回调接口
     */
    public interface CallBack {
        void callback(Bitmap bitmap, File file);
    }

}