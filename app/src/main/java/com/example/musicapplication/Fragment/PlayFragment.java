package com.example.musicapplication.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.musicapplication.R;
import com.example.musicapplication.entity.Music;
import com.example.musicapplication.utils.MediaPlayerUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Locale;

public class PlayFragment extends Fragment implements View.OnClickListener, MediaPlayerUtils.onListener, SeekBar.OnSeekBarChangeListener {
    private TextView txtCurrentTime, txtTotalTime;
    private TextView txtPlaySongName, txtPlaySingerName;
    private ImageView imgPlayDisk, imgBack;
    private SeekBar sbMusicProgress;
    private ImageButton btnPreviousSong, btnNextSong, btnLoopSong, btnRandomSong, btnPlayMusic;

    private UpdateSeekBarRunnable updateSeekBarRunnable;
    private MediaPlayerUtils mediaPlayerUtils;
    private Handler handler;
    private Music music;

    private Animation rotateAnim;

    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        txtCurrentTime = view.findViewById(R.id.txt_current_time);
        txtTotalTime = view.findViewById(R.id.txt_total_time);
        imgPlayDisk = view.findViewById(R.id.img_play_disk);
        sbMusicProgress = view.findViewById(R.id.sb_music_progress);
        btnPreviousSong = view.findViewById(R.id.btn_previous_song);
        btnNextSong = view.findViewById(R.id.btn_next_song);
        btnLoopSong = view.findViewById(R.id.btn_loop_song);
        btnRandomSong = view.findViewById(R.id.btn_random_song);
        btnPlayMusic = view.findViewById(R.id.btn_play_music);
        imgBack = view.findViewById(R.id.img_back);
        txtPlaySingerName = view.findViewById(R.id.txt_play_singer_name);
        txtPlaySongName = view.findViewById(R.id.txt_play_song_name);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent();
        iniEvent();
        mediaPlayerUtils.setDataSource(music.getSource());

    }

    private void initComponent() {
        mediaPlayerUtils = new MediaPlayerUtils(this);

        updateSeekBarRunnable = new UpdateSeekBarRunnable();
        handler = new Handler();
        bundle = getArguments();
        if (bundle != null)
            music = (Music) bundle.getSerializable("song_info");
        rotateAnim = AnimationUtils.loadAnimation(getContext() ,R.anim.anim_rotate_disk);

    }

    private void iniEvent() {
        btnPlayMusic.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        sbMusicProgress.setOnSeekBarChangeListener(this);
    }

    private String convertSecondsToTimeFormat(int milliseconds) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.US);
        return timeFormat.format(milliseconds);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_play_music) {
            if (mediaPlayerUtils.isPlaying()) {
                btnPlayMusic.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                mediaPlayerUtils.pause();
                imgPlayDisk.clearAnimation();
            } else {
                btnPlayMusic.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                mediaPlayerUtils.start();
                imgPlayDisk.startAnimation(rotateAnim);
            }
        } else if (id == R.id.img_back) {
            getParentFragmentManager().popBackStack();
        }

            /*else if (id == R.id.btn_previous_song) {

        } else if (id == R.id.btn_next_song) {

        } else if (id == R.id.btn_loop_song) {

        } else if (id == R.id.btn_random_song) {

        }*/

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) {
            mediaPlayerUtils.seekTo(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onError() {
        Toast.makeText(getActivity(), "Cannot open media", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeState(MediaPlayerUtils.State state) {
        switch (state) {
            case INITIALIZED:
            case ERROR:
                sbMusicProgress.setProgress(0);
                sbMusicProgress.setSecondaryProgress(0);

                if (music != null) {
                    txtPlaySongName.setText(music.getSongName());
                    txtPlaySingerName.setText(music.getSinger());
                    Glide.with(this).load(music.getImageMusic()).into(imgPlayDisk);
                }
                imgPlayDisk.startAnimation(rotateAnim);
                enableButton(false);

                break;

            case STARTED:
                handler.post(updateSeekBarRunnable);
                enableButton(true);
                break;

            case PAUSED:
                handler.removeCallbacks(updateSeekBarRunnable);

                enableButton(true);
                break;

            case COMPLETED:
                handler.removeCallbacks(updateSeekBarRunnable);
                mediaPlayerUtils.start();
                break;
            default:
                break;

        }
    }

    void enableButton(boolean isEnabled) {
        btnPlayMusic.setEnabled(isEnabled);
        btnPreviousSong.setEnabled(isEnabled);
        btnNextSong.setEnabled(isEnabled);
        btnRandomSong.setEnabled(isEnabled);
        btnLoopSong.setEnabled(isEnabled);
    }

    @Override
    public void updateBuffer(int percent) {
        int max = sbMusicProgress.getMax();
        sbMusicProgress.setSecondaryProgress(percent * max / 100);
    }

    @Override
    public void onPrepared(int duration) {
        sbMusicProgress.setMax(duration);
        txtTotalTime.setText(convertSecondsToTimeFormat(duration));
    }

    private class UpdateSeekBarRunnable implements Runnable {

        @Override
        public void run() {
            int currentPosition = mediaPlayerUtils.getCurrentPosition(); // seconds
            sbMusicProgress.setProgress(currentPosition);
            txtCurrentTime.setText(convertSecondsToTimeFormat(currentPosition));

            handler.postDelayed(this, 1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayerUtils.release();
    }
}
