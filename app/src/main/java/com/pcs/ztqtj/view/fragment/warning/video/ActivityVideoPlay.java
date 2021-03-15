package com.pcs.ztqtj.view.fragment.warning.video;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.pcs.ztqtj.R;

import java.io.File;

/**
 * Created by Administrator on 2017/8/10 0010.
 * chen_jx
 */

public class ActivityVideoPlay extends Activity{

    private Intent intent;
    private String path;

    private final String TAG = "main";
    private EditText et_path;
    private SurfaceView sv;

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private int currentPosition = 0;
    private boolean isPlaying,isContine=false;
    private CheckBox start_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_video);
        intent =getIntent();
        path=intent.getStringExtra("path");
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        sv = (SurfaceView) findViewById(R.id.sv);

        start_btn= (CheckBox) findViewById(R.id.start_btn);

        start_btn.setOnClickListener(click);

        // 为SurfaceHolder添加回调
        sv.getHolder().addCallback(callback);

        // 4.0版本之下需要设置的属性
        // 设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到界面
        // sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // 为进度条添加进度更改事件
        seekBar.setOnSeekBarChangeListener(change);
        initBtnRotate();
    }


    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        // SurfaceHolder被修改的时候回调
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "SurfaceHolder 被销毁");
            // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                currentPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "SurfaceHolder 被创建");
            if (currentPosition > 0) {
                // 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
                play(currentPosition);
                currentPosition = 0;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Log.i(TAG, "SurfaceHolder 大小被改变");
        }

    };

    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                // 设置当前播放的位置
                mediaPlayer.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };

    private View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.start_btn:
                    if (!isContine){
                        play(0);
                        isContine=true;
                    }else {
                        if (mediaPlayer.isPlaying()){
                            mediaPlayer.pause();
                        }else {
                            mediaPlayer.start();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /*
     * 停止播放
     */
    protected void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    /**
     * 开始播放
     *
     * @param msec 播放初始位置
     */
    protected void play(final int msec) {
        // 获取视频文件地址
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(this, "视频文件路径错误", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri=Uri.parse(path);
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置播放的视频源
            // mediaPlayer.setDataSource(file.getAbsolutePath());

            mediaPlayer.setDataSource(ActivityVideoPlay.this,uri);
            // 设置显示视频的SurfaceHolder
            mediaPlayer.setDisplay(sv.getHolder());
            Log.i(TAG, "开始装载");
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.i(TAG, "装载完成");
                    mediaPlayer.start();
                    // 按照初始位置播放
                    mediaPlayer.seekTo(msec);
                    // 设置进度条的最大进度为视频流的最大播放时长
                    seekBar.setMax(mediaPlayer.getDuration());
                    // 开始线程，更新进度条的刻度
                    new Thread() {

                        @Override
                        public void run() {
                            try {
                                isPlaying = true;
                                while (isPlaying) {
                                    int current = mediaPlayer
                                            .getCurrentPosition();
                                    seekBar.setProgress(current);
                                    sleep(500);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 在播放完毕被回调
                    mediaPlayer.reset();
                    isContine=false;
                    start_btn.setChecked(false);
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 发生错误重新播放
                    play(0);
                    isPlaying = false;
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 重新开始播放
     */
    protected void replay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
            Toast.makeText(this, "重新播放", Toast.LENGTH_SHORT).show();
            return;
        }
        isPlaying = false;
        play(0);


    }

    /**
     * 暂停或继续
     */
    protected void pause() {
       /* if (btn_pause.getText().toString().trim().equals("继续")) {
            btn_pause.setText("暂停");
            mediaPlayer.start();
            Toast.makeText(this, "继续播放", 0).show();
            return;
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btn_pause.setText("继续");
            Toast.makeText(this, "暂停播放", 0).show();
        }*/

    }


    /**
     * 初始化按钮
     */
    private void initBtnRotate() {
        Button btn_del, btn_up, btn_finish;

        btn_del = (Button) findViewById(R.id.btn_video_del);
        btn_del.setOnClickListener(mOnClick);

        btn_up = (Button) findViewById(R.id.btn_video_up);
        btn_up.setOnClickListener(mOnClick);

        btn_finish = (Button) findViewById(R.id.btn_video_finish);
        btn_finish.setOnClickListener(mOnClick);

    }
    private View.OnClickListener mOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.btn_video_del:
                    // 删除
                    //把返回数据存入Intent
                    intent.putExtra("type", "0");
                    //设置返回数据
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    // showProgressDialog();
                    //mResizer.rotateSD(mPath, -90, mImageListener);
                    break;
                case R.id.btn_video_up:
                    //重新上传
                    //把返回数据存入Intent
                    intent.putExtra("type", "1");
                    //设置返回数据
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    // showProgressDialog();
                    // mResizer.rotateSD(mPath, 90, mImageListener);
                    break;
                case R.id.btn_video_finish:
                    //把返回数据存入Intent
                    intent.putExtra("type", "2");
                    //设置返回数据
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };


}
