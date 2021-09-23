package com.example.musicapplication.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import java.io.IOException;

public class MediaPlayerUtils implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private MediaPlayer mediaPlayer;
    private State state;
    private final onListener listener;

    public MediaPlayerUtils(onListener listener) {
        this.listener = listener;
        mediaPlayer = new MediaPlayer();
        registerListener();
        setState(State.IDLE);
    }

    public void setDataSource(Context context, int resId) {
        mediaPlayer = MediaPlayer.create(context, resId);
        setState(State.INITIALIZED);
        registerListener();
    }

    public void setDataSource(Context context, Uri uri) {
        try {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(context, uri);
            setState(State.INITIALIZED);
            prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDataSource(String url) {
        try {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(url);
            setState(State.INITIALIZED);
            prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            setState(State.ERROR);
            listener.onError();
        }
    }

    public boolean isPlaying() {
        if (state != State.ERROR)
            return mediaPlayer.isPlaying();
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setState(State.ERROR);
        listener.onError();
        return false;
    }

    private void registerListener() {
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    private void prepareAsync() {
        if (state == State.INITIALIZED || state == State.STOPPED) {
            mediaPlayer.prepareAsync();
            setState(State.PREPARING);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        listener.updateBuffer(percent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setState(State.PREPARED);
        listener.onPrepared(getDuration());
        start();
    }

    public void start() {
        if (state == State.PREPARED
                || state == State.STOPPED
                || state == State.PAUSED
                || state == State.COMPLETED) {
            mediaPlayer.start();
            setState(State.STARTED);
        }
    }

    public void pause() {
        if (state == State.STARTED) {
            mediaPlayer.pause();
            setState(State.PAUSED);
        }
    }

    public void stop() {
        if (state == State.STARTED || state == State.PAUSED) {
            mediaPlayer.stop();
            setState(State.STOPPED);
        }
    }

    public void reset() {
        mediaPlayer.reset();
    }


    public int getCurrentPosition() {
        if (state != State.RELEASE) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getDuration() {
        if (state != State.IDLE && state != State.INITIALIZED && state != State.ERROR && state != State.PREPARING && state!= State.RELEASE) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public void seekTo(int position) {
        if ((position <= getDuration()) && (state == State.STARTED
                || state == State.PAUSED
                || state == State.STOPPED
                || state == State.PREPARED
                || state == State.COMPLETED)) {
            mediaPlayer.seekTo(position);
        }
    }

    public void rewind(int seconds) {
        seekTo(Math.max(getCurrentPosition() - seconds, 0));
    }

    public void fastForward(int seconds) {
        seekTo(Math.min(getCurrentPosition() + seconds, getDuration()));
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (state == State.ERROR) {
            return;
        }
        setState(State.COMPLETED);
    }

    public void setLoop(boolean isLoop) {
        if (state != State.RELEASE) {
            mediaPlayer.setLooping(isLoop);
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        setState(State.RELEASE);
    }

    public interface onListener {
        void onError();

        void changeState(State state);

        void updateBuffer(int percent);

        void onPrepared(int duration);
    }

    private void setState(State state) {
        this.state = state;
        listener.changeState(state);
    }

    public enum State {
        IDLE,
        INITIALIZED,
        PREPARING,
        PREPARED,
        STARTED,
        PAUSED,
        STOPPED,
        COMPLETED,
        RELEASE,
        ERROR
    }
}
