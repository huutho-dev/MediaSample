package com.htn.samplefragment.pager;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Util;
import com.htn.samplefragment.MediaEntity;
import com.htn.samplefragment.R;
import com.htn.samplefragment.Utils;

/**
 * Created by ThoNh on 2/27/2018.
 */

public class AudioFragment extends Fragment implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, SeekBar.OnSeekBarChangeListener {
    public static final String TAG = AudioFragment.class.getSimpleName();
    public static final int TIME_INTERVAL = 3000;
    public static final String EXTRA_MEDIA = "EXTRA_MEDIA";

    public static AudioFragment newInstance(MediaEntity entity) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_MEDIA, entity);
        AudioFragment fragment = new AudioFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ProgressBar mProgressBar;
    private SeekBar mSeekBar;
    private TextView mCurrentTime, mRemainTime;
    private ImageView mThumbnail, mPlayPause, mNext, mPrevious;


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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_audio, container, false);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mCurrentTime = view.findViewById(R.id.txt_current_time);
        mRemainTime = view.findViewById(R.id.txt_remain_time);
        mThumbnail = view.findViewById(R.id.img_thumbnail);
        mPlayPause = view.findViewById(R.id.img_play_pause);
        mNext = view.findViewById(R.id.img_next);
        mPrevious = view.findViewById(R.id.img_previous);

        mSeekBar = view.findViewById(R.id.seek_bar_audio);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        mSeekBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        mPlayPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");

        Glide.with(getContext()).load(mMediaEntity.mUrlThumbnail).apply(RequestOptions.circleCropTransform()).into(mThumbnail);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isPrepared) {
            try {
                mProgressBar.setVisibility(View.VISIBLE);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(mMediaEntity.mUrlContent);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setOnInfoListener(this);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*Fragment Active in Here if meniVisible=true*/
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);


        if (menuVisible && mMediaPlayer != null && isPrepared && !mMediaPlayer.isPlaying()) {
            start();
        }

        if (!menuVisible && mMediaPlayer != null && isPrepared && mMediaPlayer.isPlaying()) {
            pause();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null && isPrepared && mMediaPlayer.isPlaying()) {
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
        mPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        mHandler.postDelayed(mRunnable, 0);
        if (mProgressBar.getVisibility() != View.GONE)
            mProgressBar.setVisibility(View.GONE);
    }

    private void pause() {
        mMediaPlayer.pause();
        mPlayPause.setImageResource(android.R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
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


    /********************************** SEEK BAR CHANGE LISTENER **********************************/

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
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
}
