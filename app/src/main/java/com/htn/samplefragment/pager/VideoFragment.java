package com.htn.samplefragment.pager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.htn.samplefragment.MainActivity;
import com.htn.samplefragment.MediaEntity;
import com.htn.samplefragment.R;
import com.htn.samplefragment.Utils;

/**
 * Created by ThoNh on 2/27/2018.
 */

public class VideoFragment extends Fragment implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener, SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener {

    public static final String TAG = VideoFragment.class.getSimpleName();
    public static final int TIME_INTERVAL = 5000;
    public static final String EXTRA_MEDIA = "EXTRA_MEDIA";

    public static VideoFragment newInstance(MediaEntity entity) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_MEDIA, entity);
        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private SeekBar mSeekBar;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;
    private TextView mCurrentTime, mRemainTime;
    private ImageView mPlayPause, mNext, mPrevious;

    private int mCurrentPosition;
    private boolean isPrepared = false;
    private MediaEntity mMediaEntity;
    private MediaPlayer mMediaPlayer;


    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentTime != null && mRemainTime != null && mSeekBar != null && mMediaPlayer.isPlaying()) {
                mCurrentTime.setText(Utils.convertMillisToSecond(mMediaPlayer.getCurrentPosition()));
                mRemainTime.setText(Utils.convertMillisToSecond(mMediaPlayer.getDuration() - mMediaPlayer.getCurrentPosition()));
                mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            }

            mHandler.postDelayed(this, 500);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setUserVisibleHint(false);
        super.onCreate(savedInstanceState);
        mMediaEntity = getArguments().getParcelable(EXTRA_MEDIA);
        Log.e(TAG, "onCreate");


        LocalBroadcastManager.getInstance(
                getContext()).registerReceiver(mOrientationChangeReceiver,
                new IntentFilter("custom-event-name"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_video, container, false);

        mProgressBar = view.findViewById(R.id.progress_bar);
        mCurrentTime = view.findViewById(R.id.txt_current_time);
        mRemainTime = view.findViewById(R.id.txt_remain_time);

        mSurfaceView = view.findViewById(R.id.surface_view);
        mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mPlayPause = view.findViewById(R.id.img_play_pause);
        mNext = view.findViewById(R.id.img_next);
        mPrevious = view.findViewById(R.id.img_previous);

        mSeekBar = view.findViewById(R.id.seek_bar_audio);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSeekBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }

        mPlayPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!isPrepared) {
            try {
                Log.e(TAG, "isPrepared:" + isPrepared);
                mProgressBar.setVisibility(View.VISIBLE);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource("https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setOnInfoListener(this);
                mMediaPlayer.setOnVideoSizeChangedListener(this);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null && isPrepared && mMediaPlayer.isPlaying()) {
            Log.e(TAG, "onPause: pause");
            pause();
        }
    }


    /*Fragment Active in Here if meniVisible=true*/
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        Log.e(TAG, "setMenuVisibility -> " + menuVisible);

        if (menuVisible && mMediaPlayer != null && isPrepared && !mMediaPlayer.isPlaying()) {
            start();
        }

        if (!menuVisible && mMediaPlayer != null && isPrepared && mMediaPlayer.isPlaying()) {
            pause();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_play_pause:
                if (mMediaPlayer.isPlaying()) {
                    pause();
                } else {
                    start();
                }
                break;
            case R.id.img_next:
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + TIME_INTERVAL);
                break;
            case R.id.img_previous:
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - TIME_INTERVAL);
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.e(TAG, "onPrepared");
        isPrepared = true;

        if (mMediaPlayer != null) {
            mSeekBar.setMax(mMediaPlayer.getDuration());
        }

        if (getUserVisibleHint()) {
            start();
        }
    }


    private void start() {
        mMediaPlayer.start();

        Log.e(TAG,"start -> " + mMediaPlayer.getCurrentPosition());

        mPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        mHandler.post(mRunnable);
        if (mProgressBar.getVisibility() != View.GONE)
            mProgressBar.setVisibility(View.GONE);
    }

    private void pause() {
        mMediaPlayer.pause();
        mPlayPause.setImageResource(android.R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(null);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    /******************************** MEDIA PLAYER ERROR LISTENER *********************************/
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        switch (i) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.e(TAG, "MEDIA_ERROR_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.e(TAG, "MEDIA_ERROR_SERVER_DIED");
                break;
            case MediaPlayer.MEDIA_ERROR_IO:
                Log.e(TAG, "MEDIA_ERROR_IO");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Log.e(TAG, "MEDIA_ERROR_MALFORMED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Log.e(TAG, "MEDIA_ERROR_UNSUPPORTED");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Log.e(TAG, "MEDIA_ERROR_TIMED_OUT");
                break;
        }
        return true;
    }


    /******************************** MEDIA PLAYER INFO LISTENER **********************************/
    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        switch (i) {
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
                Log.e(TAG, "MEDIA_INFO_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                Log.e(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.e(TAG, "MEDIA_INFO_BUFFERING_START");
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                Log.e(TAG, "MEDIA_INFO_BUFFERING_END");
                mProgressBar.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                Log.e(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                Log.e(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                Log.e(TAG, "MEDIA_INFO_METADATA_UPDATE");
                break;
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                Log.e(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE");
                break;
            case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                Log.e(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT");
                break;
        }
        return true;
    }


    /********************************** VIDEO SIZE CHANGE LISTENER ********************************/

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        Log.e(TAG, "onVideoSizeChanged:" + width + ":" + height);
        int orientation = getActivity().getResources().getConfiguration().orientation;
        setSizeSurfaceView(orientation);
    }


    private void setSizeSurfaceView(int orientation) {
        if (getActivity() != null) {

            int widthScreen = Resources.getSystem().getDisplayMetrics().widthPixels;
            int heightScreen = Resources.getSystem().getDisplayMetrics().heightPixels;
            if (widthScreen > heightScreen) {
                int temp = widthScreen;
                widthScreen = heightScreen;
                heightScreen = temp;
            }

            int videoWidth = mMediaPlayer.getVideoWidth();                       // get width video
            int videoHeight = mMediaPlayer.getVideoHeight();                     // get height video
            float videoProportion = (float) videoWidth / videoHeight;           // ratio

            final ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();          // Get the SurfaceView layout param
            switch (orientation) {
                case 0:
                case 180:
                case Configuration.ORIENTATION_PORTRAIT:
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                    if (videoProportion > 1) {
                        lp.width = widthScreen;
                        lp.height = (int) ((float) widthScreen / videoProportion);
                        Log.e(TAG, "Screen Vertical, Video Horizontal");

                    } else {
                        lp.height = heightScreen;
                        lp.width = (int) ((float) widthScreen * videoProportion);
                        Log.e(TAG, "Screen Vertical, Video Horizontal");

                    }

                    Log.e(TAG, lp.width + "-" + lp.height);
                    mSurfaceView.getHolder().setFixedSize(lp.width, lp.height);
                    mSurfaceView.setLayoutParams(lp);
                    break;

                case 90:
                case 270:
                case Configuration.ORIENTATION_LANDSCAPE:
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

                    if (videoProportion > 1) {                      // Screen Horizontal, Video Horizontal
                        lp.width = heightScreen;
                        lp.height = (int) (float) (heightScreen / videoProportion);
                        Log.e(TAG, "Screen Horizontal, Video Horizontal");

                    } else {                                        // Screen Horizontal, Video Vertical
                        lp.height = widthScreen;
                        lp.width = (int) (float) (widthScreen * videoProportion);
                        Log.e(TAG, "Screen Horizontal, Video Vertical");
                    }

                    // LANDSCAPE --> hide actionbar
                    if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                    }

                    // LANDSCAPE --> hide statusBar
                    View decorView = getActivity().getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);

                    Log.e(TAG, lp.width + "-" + lp.height);
                    mSurfaceView.getHolder().setFixedSize(lp.width, lp.height);
                    mSurfaceView.setLayoutParams(lp);
                    break;
            }
        }

    }


    /********************************** SEEK BAR CHANGE LISTENER **********************************/

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Log.e(TAG,"onProgressChanged:" + i);
        if (b) {
            mMediaPlayer.seekTo(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    /*********************************** CREATE SURFACE LISTENER **********************************/
    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        Log.e(TAG, "surfaceCreated");

        mSurfaceHolder = surfaceHolder;
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(surfaceHolder);
            mMediaPlayer.setSurface(surfaceHolder.getSurface());
            Log.e(TAG, "mMediaPlayer.setDisplay(surfaceHolder)");
        }

    }

    @Override
    public void surfaceChanged(final SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e(TAG, "surfaceChanged");
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(surfaceHolder);
            mMediaPlayer.setSurface(surfaceHolder.getSurface());
            Log.e(TAG, "mMediaPlayer.setDisplay(surfaceHolder)");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e(TAG, "surfaceDestroyed");
        mMediaPlayer.setDisplay(null);
        mMediaPlayer.setSurface(null);
    }

    /******************************* ORIENTATION CHANGE LISTENER **********************************/

    private BroadcastReceiver mOrientationChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int orientation = intent.getIntExtra(MainActivity.BUNDLE_EXTRA_ORIENTATION, 0);
            Log.d("receiver", "Got message: " + orientation);
            setSizeSurfaceView(orientation);
        }
    };

}
