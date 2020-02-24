package com.example.audiovideo;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    //UI Components
    private VideoView mVideoView;
    private Button btnPlayVideo;
    private MediaController mMediaController;
    private Button btnPlayMusic, btnPauseMusic;
    private SeekBar mVolumeSeekBar, mMoveSeekBar;

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private Timer timer;

    //media
    @Override
    public void onCompletion(MediaPlayer mp) {
        timer.cancel();
        Toast.makeText(this,"Music is end",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoView = findViewById(R.id.videoView);
        btnPlayVideo = findViewById(R.id.btnVideo);
        btnPauseMusic = findViewById(R.id.btnPauseMusic);
        btnPlayMusic = findViewById(R.id.btnPlayMusic);
        mVolumeSeekBar = findViewById(R.id.seekBarVolume);
        mMoveSeekBar = findViewById(R.id.seekBarMove);

        mMediaController = new MediaController(MainActivity.this);
        mMediaPlayer = MediaPlayer.create(this,R.raw.music1);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        btnPlayVideo.setOnClickListener(MainActivity.this);
        btnPlayMusic.setOnClickListener(MainActivity.this);
        btnPauseMusic.setOnClickListener(MainActivity.this);
        mMoveSeekBar.setOnSeekBarChangeListener(MainActivity.this); //make the listener work!!!
        mMoveSeekBar.setMax(mMediaPlayer.getDuration());
        mMediaPlayer.setOnCompletionListener(MainActivity.this);




        //change the volume of music
        int maximumVolumeOfUserDevice = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final int currentVolumeOfUserDevice = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mVolumeSeekBar.setMax(maximumVolumeOfUserDevice);
        mVolumeSeekBar.setProgress(currentVolumeOfUserDevice);
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    //Toast.makeText(MainActivity.this,Integer.toString(progress)+"",Toast.LENGTH_SHORT).show();
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    public void onClick(View buttonView) {
        switch (buttonView.getId()){
            case R.id.btnVideo:
                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
                mVideoView.setVideoURI(videoUri);
                mVideoView.setMediaController(mMediaController);
                mMediaController.setAnchorView(mVideoView);
                mVideoView.start();
                break;
            case R.id.btnPlayMusic:
                mMediaPlayer.start();
                timer = new Timer();
                //timer is a new thread
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        mMoveSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                    }
                }, 0, 1000); //0 means now
                break;
            case R.id.btnPauseMusic:
                if (mMoveSeekBar.getProgress() != 0){
                    mMediaPlayer.pause();
                    timer.cancel();
                    break;
                }

        }


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        //move the music forth and back
        if (fromUser){
//            Toast.makeText(this,Integer.toString(progress),Toast.LENGTH_SHORT).show();
            mMediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mMediaPlayer.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaPlayer.start();
    }
}
