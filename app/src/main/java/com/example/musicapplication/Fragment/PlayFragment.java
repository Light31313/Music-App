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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentPlayBinding;
import com.example.musicapplication.entity.Music;
import com.example.musicapplication.utils.MediaPlayerUtils;
import com.example.musicapplication.viewmodel.MainFragmentViewModel;
import com.example.musicapplication.viewmodel.MusicViewModel;


public class PlayFragment extends Fragment implements View.OnClickListener, MediaPlayerUtils.onListener, SeekBar.OnSeekBarChangeListener, Playable {
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

    private ConstraintLayout llPlay;
    private FragmentPlayBinding binding;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        imgPlayDisk = binding.imgPlayDisk;
        sbMusicProgress = binding.sbMusicProgress;
        btnPreviousSong = binding.btnPreviousSong;
        btnNextSong = binding.btnNextSong;
        btnLoopSong = binding.btnLoopSong;
        btnRandomSong = binding.btnRandomSong;
        btnPlayMusic = binding.btnPlayMusic;
        imgBack = binding.imgBack;
        llPlay = binding.llPlay;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent();
        iniEvent();

        viewModel.setReady(true);
    }

    private void initComponent() {
        viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        mainFragmentViewModel = new ViewModelProvider(requireActivity()).get(MainFragmentViewModel.class);
        mediaPlayerUtils = new MediaPlayerUtils(this);
        mode = Mode.NORMAL;
        updateSeekBarRunnable = new UpdateSeekBarRunnable();
        handler = new Handler();
        mainFragmentViewModel.setVisible(false);
        rotateAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate_disk);
        llPlay.setClickable(true);
        llPlay.setFocusable(true);
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
            binding.setMusic(music);
        });
        viewModel.getShowCollapse().observe(getViewLifecycleOwner(), showCollapse -> {
            if (showCollapse) {
                llPlay.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_move_down));
                getParentFragmentManager().beginTransaction().hide(this).commit();
                mainFragmentViewModel.setVisible(true);
            } else {
                getParentFragmentManager().beginTransaction().show(this).commit();
                llPlay.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_move_up));
                mainFragmentViewModel.setVisible(false);
            }
        });
        viewModel.getPlay().observe(getViewLifecycleOwner(), isPlay -> {
            if (isPlay)
                onMusicPlay();
            else
                onMusicPause();
        });
    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_play_music) {
            viewModel.setPlay(!mediaPlayerUtils.isPlaying());
        } else if (id == R.id.img_back) {
            viewModel.setShowCollapse(true);
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


    @Override
    public void changeState(MediaPlayerUtils.State state) {
        switch (state) {
            case ERROR:
            case INITIALIZED:
                imgPlayDisk.clearAnimation();
                sbMusicProgress.setProgress(0);
                sbMusicProgress.setSecondaryProgress(0);
                binding.setTotalTime(0);
                binding.setCurrentTime(0);
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
        binding.setTotalTime(duration);
    }

    @Override
    public void onMusicPrevious() {
        viewModel.setPrevious(true);

    }

    @Override
    public void onMusicPlay() {
        mediaPlayerUtils.start();

    }

    @Override
    public void onMusicPause() {
        mediaPlayerUtils.pause();
    }

    @Override
    public void onMusicNext() {
        viewModel.setNext(true);
    }

    private class UpdateSeekBarRunnable implements Runnable {

        @Override
        public void run() {
            int currentPosition = mediaPlayerUtils.getCurrentPosition(); // seconds
            if (currentPosition <= mediaPlayerUtils.getDuration()) {
                binding.setCurrentTime(currentPosition);
            }
            handler.postDelayed(this, 1000);
        }
    }

    @Override
    public void onDestroy() {
        mediaPlayerUtils.release();
        super.onDestroy();
    }
}
