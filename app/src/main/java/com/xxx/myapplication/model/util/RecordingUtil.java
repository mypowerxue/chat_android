package com.xxx.myapplication.model.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import com.xxx.myapplication.ConfigClass;
import com.xxx.myapplication.model.http.bean.MP4Bean;

import java.io.File;
import java.io.IOException;

public class RecordingUtil {

    private String mFilePath;
    private MediaRecorder mRecorder;
    private MediaPlayer mMediaPlayer;

    private long mStartingTimeMillis;

    // 开始录音
    public void startRecording(final Context context) {
        mFilePath = context.getFilesDir().getPath() + "/recorder/";
        File file = new File(mFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        mFilePath += System.currentTimeMillis() + ".amr";
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR); //录音文件保存的格式，这里保存为 mp4
        mRecorder.setOutputFile(mFilePath); // 设置录音文件的保存路径
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            Log.d("RecordingUtil", e.getMessage());
        }
    }

    // 停止录音
    public MP4Bean stopRecording() {
        if (mStartingTimeMillis == 0) {
            return null;
        }
        long mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.stop();
        mRecorder.release();


        if (mElapsedMillis <= 1500) {
            File file = new File(mFilePath);
            file.delete();
            mFilePath = null;
        }

        mRecorder = null;
        MP4Bean mp4Bean = new MP4Bean();
        mp4Bean.setFileUrl(mFilePath);
        mp4Bean.setTime(mElapsedMillis);
        return mp4Bean;
    }

    //开始播放录音
    public void playMp4(String url) {
        stopMp4();
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(ConfigClass.BASE_URL + url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    //停止播放录音
    public void stopMp4() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    //是否正在播放
    public boolean isPlay() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }
}