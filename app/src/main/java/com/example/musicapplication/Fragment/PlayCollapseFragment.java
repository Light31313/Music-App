package com.example.musicapplication.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentPlayCollapseBinding;
import com.example.musicapplication.entity.Music;
import com.example.musicapplication.viewmodel.MusicViewModel;

public class PlayCollapseFragment extends Fragment {
    private ImageButton btnCollapsePlay, btnCollapseNext;
    private FragmentPlayCollapseBinding binding;
    private LinearLayout llPlayCollapse;
    private MusicViewModel viewModel;
    private Music music;
    private boolean isPlaying;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayCollapseBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        btnCollapsePlay = binding.btnCollapsePlay;
        btnCollapseNext = binding.btnCollapseNext;
        llPlayCollapse = binding.llPlayCollapse;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getParentFragmentManager().beginTransaction().hide(this).commit();
        initComponent();
        initEvent();
    }

    private void initComponent() {
        viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        llPlayCollapse.setClickable(true);
        isPlaying = true;
    }

    private void initEvent() {
        llPlayCollapse.setOnClickListener(view -> viewModel.setShowCollapse(false));
        btnCollapsePlay.setOnClickListener(view -> viewModel.setPlay(!isPlaying));
        btnCollapseNext.setOnClickListener(view -> viewModel.setNext(true));

        viewModel.getMusic().observe(getViewLifecycleOwner(), receivedMusic ->{
            music = receivedMusic;
            binding.setMusic(music);
        });
        viewModel.getShowCollapse().observe(getViewLifecycleOwner(), showCollapse ->{
            if(showCollapse) {
                getParentFragmentManager().beginTransaction().show(this).commit();
                llPlayCollapse.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_navigation_move_up));
            }
            else {
                llPlayCollapse.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_navigation_move_down));
                getParentFragmentManager().beginTransaction().hide(this).commit();
            }
        });
        viewModel.getPlay().observe(getViewLifecycleOwner(), isPlay ->{
            if(isPlay) {
                btnCollapsePlay.setImageResource(R.drawable.ic_baseline_pause_24);
                isPlaying = true;
            }
            else {
                btnCollapsePlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                isPlaying = false;
            }
        });
    }
}
