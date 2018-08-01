package com.xiaozi.android.exoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.xiaozi.framework.libs.BaseActivity;
import com.xiaozi.framework.libs.utils.Logger;

/**
 * Created by user on 2018-01-29.
 */

public class PlayerActivity extends BaseActivity {
    private SimpleExoPlayerView mPlayerView = null;

    private SimpleExoPlayer mPlayer = null;
    private SurfaceView mSurfaceView = null;

    private SurfaceHolder mSurfaceHolder = null;

    private final static float PLAYBACK_SPEED = 1.5f;

    private String mVideoFilePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initialize();
        initView();
        initVideoPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initialize() {
        super.initialize();
        mVideoFilePath = getIntent().getStringExtra("video_path");
        Logger.d(LOG_TAG, "initialize mVideoFilePath : " + mVideoFilePath);
    }

    @Override
    protected void initView() {
        super.initView();
        mPlayerView = findViewById(R.id.player_player_view);
        mSurfaceView = findViewById(R.id.player_surface_view);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Logger.i(LOG_TAG, "surfaceCreated");
                playVideo(mVideoFilePath);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Logger.i(LOG_TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Logger.i(LOG_TAG, "surfaceDestroyed");
                if (mPlayer != null) mPlayer.release();
            }
        });
    }

    private void initVideoPlayer() {
        Logger.i(LOG_TAG, "initVideoPlayer");
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mPlayer = ExoPlayerFactory.newSimpleInstance(mActivity, trackSelector);

        mPlayer.setVideoSurfaceView(mSurfaceView);
        mPlayer.addVideoListener(new SimpleExoPlayer.VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                Logger.i(LOG_TAG, "initVideoPlayer onVideoSizeChanged");
                int displayWidth = mActivity.getResources().getDisplayMetrics().widthPixels;
                int displayHeight = mActivity.getResources().getDisplayMetrics().heightPixels;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
                layoutParams.width = (int) (width * (displayHeight / (float) height));
                mSurfaceView.setLayoutParams(layoutParams);
                Logger.d(LOG_TAG, "initVideoPlayer onVideoSizeChanged displayWidth : " + displayWidth);
                Logger.d(LOG_TAG, "initVideoPlayer onVideoSizeChanged displayHeight : " + displayHeight);
                Logger.d(LOG_TAG, "initVideoPlayer onVideoSizeChanged width : " + width);
                Logger.d(LOG_TAG, "initVideoPlayer onVideoSizeChanged height : " + height);
                Logger.d(LOG_TAG, "initVideoPlayer onVideoSizeChanged unappliedRotationDegrees : " + unappliedRotationDegrees);
                Logger.d(LOG_TAG, "initVideoPlayer onVideoSizeChanged pixelWidthHeightRatio : " + pixelWidthHeightRatio);
            }

            @Override
            public void onRenderedFirstFrame() {
                Logger.i(LOG_TAG, "initVideoPlayer onRenderedFirstFrame");
                mPlayer.setPlayWhenReady(true);
            }
        });
        mPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Logger.i(LOG_TAG, "initVideoPlayer onTimelineChanged");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Logger.i(LOG_TAG, "initVideoPlayer onTracksChanged");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Logger.i(LOG_TAG, "initVideoPlayer onLoadingChanged");
                Logger.d(LOG_TAG, "initVideoPlayer onLoadingChanged isLoading : " + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Logger.i(LOG_TAG, "initVideoPlayer onPlayerStateChanged");
                Logger.d(LOG_TAG, "initVideoPlayer onPlayerStateChanged playWhenReady : " + playWhenReady);
                Logger.d(LOG_TAG, "initVideoPlayer onPlayerStateChanged playbackState : " + playbackState);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Logger.i(LOG_TAG, "initVideoPlayer onRepeatModeChanged");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Logger.i(LOG_TAG, "initVideoPlayer onShuffleModeEnabledChanged");
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Logger.i(LOG_TAG, "initVideoPlayer onPlayerError");
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Logger.i(LOG_TAG, "initVideoPlayer onPositionDiscontinuity");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Logger.i(LOG_TAG, "initVideoPlayer onPlaybackParametersChanged");
            }

            @Override
            public void onSeekProcessed() {
                Logger.i(LOG_TAG, "initVideoPlayer onSeekProcessed");
            }
        });

        PlaybackParameters parameters = new PlaybackParameters(PLAYBACK_SPEED, PLAYBACK_SPEED);
        mPlayer.setPlaybackParameters(parameters);
//        mPlayerView.setPlayer(mPlayer);
    }

    private void playVideo(String videoFilePath) {
        Logger.i(LOG_TAG, "playVideo");
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mActivity,
                Util.getUserAgent(mActivity, BuildConfig.APPLICATION_ID),
                bandwidthMeter);
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoFilePath));
        mPlayer.prepare(videoSource);
//        mPlayer.setPlayWhenReady(true);
    }
}
