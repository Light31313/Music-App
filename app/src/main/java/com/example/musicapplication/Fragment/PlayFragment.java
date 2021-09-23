package com.example.musicapplication.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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
import com.example.musicapplication.R;
import com.example.musicapplication.entity.Music;
import com.example.musicapplication.service.CreateNotification;
import com.example.musicapplication.service.OnClearFromRecentService;
import com.example.musicapplication.utils.MediaPlayerUtils;
import com.example.musicapplication.viewmodel.MainFragmentViewModel;
import com.example.musicapplication.viewmodel.MusicViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PlayFragment extends Fragment implements View.OnClickListener, MediaPlayerUtils.onListener, SeekBar.OnSeekBarChangeListener, Playable {
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

    private int mode;

    private MusicViewModel viewModel;
    private MainFragmentViewModel mainFragmentViewModel;

    private NotificationManager notificationManager;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action) {
                case CreateNotification.ACTION_PREVIOUS:
                    onMusicPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (mediaPlayerUtils.isPlaying()) {
                        onMusicPause();
                    } else {
                        onMusicPlay();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    onMusicNext();
                    break;
            }
        }
    };

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            getActivity().startService(new Intent(getActivity().getBaseContext(), OnClearFromRecentService.class));
        }
        viewModel.setReady(true);
    }

    private void initComponent() {
        viewModel = new ViewModelProvider(requireParentFragment()).get(MusicViewModel.class);
        mainFragmentViewModel = new ViewModelProvider(requireActivity()).get(MainFragmentViewModel.class);
        mediaPlayerUtils = new MediaPlayerUtils(this);
        mode = Mode.NORMAL;
        updateSeekBarRunnable = new UpdateSeekBarRunnable();
        handler = new Handler();
        mainFragmentViewModel.setVisible(false);
        rotateAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate_disk);
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
            mediaPlayerUtils.reset();
            music = receivedMusic;
            mediaPlayerUtils.setDataSource(music.getSource());
        });
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getActivity().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
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
                onMusicPause();
            } else {
                onMusicPlay();
            }
        } else if (id == R.id.img_back) {
            getParentFragmentManager().popBackStack();
            mainFragmentViewModel.setVisible(true);
            viewModel.setClickable(false);
            mediaPlayerUtils.release();
        } else if (id == R.id.btn_previous_song) {
            onMusicPrevious();
        } else if (id == R.id.btn_next_song) {
            onMusicNext();
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

    @Override
    public void onError() {
        Toast.makeText(getActivity(), "Cannot open media", Toast.LENGTH_SHORT).show();
    }

    public void setupSong(Music music) {
        if (music != null) {
            txtPlaySongName.setText(music.getSongName());
            txtPlaySingerName.setText(music.getSinger());
            Glide.with(this).load(music.getImageMusic()).into(imgPlayDisk);
        }
    }

    @Override
    public void changeState(MediaPlayerUtils.State state) {
        switch (state) {
            case ERROR:
            case INITIALIZED:
                imgPlayDisk.clearAnimation();
                sbMusicProgress.setProgress(0);
                sbMusicProgress.setSecondaryProgress(0);
                txtCurrentTime.setText(R.string.time);
                txtTotalTime.setText(R.string.time);
                setupSong(music);
                enableButton(false);
                break;
            case PREPARED:
                enableButton(true);
                break;
            case STARTED:
                imgPlayDisk.startAnimation(rotateAnim);
                btnPlayMusic.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                handler.post(updateSeekBarRunnable);
                break;

            case PAUSED:
                imgPlayDisk.clearAnimation();
                btnPlayMusic.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                handler.removeCallbacks(updateSeekBarRunnable);
                break;

            case COMPLETED:
                handler.removeCallbacks(updateSeekBarRunnable);
                if (mode == Mode.NORMAL) {
                    viewModel.setNext(true);
                } else if (mode == Mode.LOOP) {
                    mediaPlayerUtils.start();
                } else if (mode == Mode.RANDOM) {
                    viewModel.setRandom(true);
                }
                break;

            case RELEASE:
                handler.removeCallbacks(updateSeekBarRunnable);
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

    @Override
    public void onMusicPrevious() {
        viewModel.setPrevious(true);
        CreateNotification.updateNotification(music, R.drawable.ic_baseline_pause_circle_outline_24);
    }

    @Override
    public void onMusicPlay() {
        mediaPlayerUtils.start();
        CreateNotification.updateNotification(music, R.drawable.ic_baseline_pause_circle_outline_24);
    }

    @Override
    public void onMusicPause() {
        mediaPlayerUtils.pause();
        CreateNotification.updateNotification(music, R.drawable.ic_baseline_play_circle_outline_24);
    }

    @Override
    public void onMusicNext() {
        viewModel.setNext(true);
        CreateNotification.updateNotification(music, R.drawable.ic_baseline_pause_circle_outline_24);
    }

    private class UpdateSeekBarRunnable implements Runnable {

        @Override
        public void run() {
            int currentPosition = mediaPlayerUtils.getCurrentPosition(); // seconds
            if (currentPosition <= mediaPlayerUtils.getDuration()) {
                sbMusicProgress.setProgress(currentPosition);
                txtCurrentTime.setText(convertSecondsToTimeFormat(currentPosition));
            }
            handler.postDelayed(this, 1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayerUtils.release();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
