package com.example.musicapplication.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.musicapplication.MainActivity;
import com.example.musicapplication.R;
import com.example.musicapplication.callback.IBottomNavigation;
import com.example.musicapplication.entity.Music;
import com.example.musicapplication.utils.MediaPlayerUtils;
import com.example.musicapplication.viewmodel.MainFragmentViewModel;
import com.example.musicapplication.viewmodel.MusicViewModel;

import java.text.SimpleDateFormat;
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

    private int mode;

    private MusicViewModel viewModel;
    private MainFragmentViewModel mainFragmentViewModel;

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
        viewModel = new ViewModelProvider(requireParentFragment()).get(MusicViewModel.class);
        mainFragmentViewModel = new ViewModelProvider(requireActivity()).get(MainFragmentViewModel.class);
        mediaPlayerUtils = new MediaPlayerUtils(this);
        mode = Mode.NORMAL;
        updateSeekBarRunnable = new UpdateSeekBarRunnable();
        handler = new Handler();
        bundle = getArguments();
        if (bundle != null)
            music = (Music) bundle.getSerializable("song_info");
        rotateAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate_disk);
        mainFragmentViewModel.setVisible(false);
    }

    private void iniEvent() {
        btnPlayMusic.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        sbMusicProgress.setOnSeekBarChangeListener(this);
        btnLoopSong.setOnClickListener(this);
        btnRandomSong.setOnClickListener(this);
        btnNextSong.setOnClickListener(this);
        btnPreviousSong.setOnClickListener(this);

        // Receive music object from MusicFragment
        viewModel.getMusic().observe(getViewLifecycleOwner(), receivedMusic -> {
            mediaPlayerUtils.release();
            mediaPlayerUtils = new MediaPlayerUtils(this);
            music = receivedMusic;
            mediaPlayerUtils.setDataSource(music.getSource());
        });
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
            } else {
                btnPlayMusic.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                mediaPlayerUtils.start();
            }
        } else if (id == R.id.img_back) {
            getParentFragmentManager().popBackStack();
            mainFragmentViewModel.setVisible(true);
            viewModel.setClickable(false);
        } else if (id == R.id.btn_previous_song) {
            viewModel.setPrevious(true);
        } else if (id == R.id.btn_next_song) {
            viewModel.setNext(true);
        } else if (id == R.id.btn_loop_song) {
            if (mode != Mode.LOOP) {
                mode = Mode.LOOP;
                btnLoopSong.setBackgroundResource(R.color.blue_gray);
                btnRandomSong.setBackgroundResource(android.R.color.transparent);
            } else {
                mode = Mode.NORMAL;
                btnLoopSong.setBackgroundResource(android.R.color.transparent);
            }
        } else if (id == R.id.btn_random_song) {
            if (mode != Mode.RANDOM) {
                mode = Mode.RANDOM;
                btnRandomSong.setBackgroundResource(R.color.blue_gray);
                btnLoopSong.setBackgroundResource(android.R.color.transparent);
            } else {
                mode = Mode.NORMAL;
                btnRandomSong.setBackgroundResource(android.R.color.transparent);
            }
        }

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

    public void setupSong(Music music) {
        if (music != null) {
            txtPlaySongName.setText(music.getSongName());
            txtPlaySingerName.setText(music.getSinger());
            Glide.with(this).load(music.getImageMusic()).into(imgPlayDisk);
        }
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

                setupSong(music);

                enableButton(false);

                break;

            case STARTED:
                imgPlayDisk.startAnimation(rotateAnim);
                handler.post(updateSeekBarRunnable);
                enableButton(true);
                break;

            case PAUSED:
                imgPlayDisk.clearAnimation();
                handler.removeCallbacks(updateSeekBarRunnable);
                enableButton(true);
                break;

            case COMPLETED:
                handler.removeCallbacks(updateSeekBarRunnable);
                if (mode == Mode.NORMAL) {
                    viewModel.setNext(true);
                } else if (mode == Mode.LOOP)
                    mediaPlayerUtils.start();
                else if (mode == Mode.RANDOM) {
                    viewModel.setRandom(true);
                }
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
