package com.example.musicapplication.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapplication.R;
import com.example.musicapplication.adapter.IMusicAdapter;
import com.example.musicapplication.adapter.MusicAdapter;
import com.example.musicapplication.databinding.FragmentMusicBinding;
import com.example.musicapplication.entity.Music;

import com.example.musicapplication.service.CreateNotification;
import com.example.musicapplication.viewmodel.MusicViewModel;

import java.util.List;
import java.util.Random;


public class MusicFragment extends Fragment implements IMusicAdapter {
    private List<Music> musicList;
    private RecyclerView rvListSong;
    private MusicViewModel viewModel;
    private int currentPosition;
    private boolean isCreated;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMusicBinding binding = FragmentMusicBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        rvListSong = binding.rvListSong;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent();
        initEvent();
    }

    private void initComponent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            musicList = (List<Music>) bundle.getSerializable("musicList");
        }
        MusicAdapter adapter = new MusicAdapter(getContext(), this, musicList);
        rvListSong.setAdapter(adapter);
        rvListSong.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        isCreated = false;
    }


    private void initEvent() {
        viewModel.getNext().observe(getViewLifecycleOwner(), isNext -> {
            currentPosition++;
            if (currentPosition >= musicList.size() - 1)
                currentPosition = 0;
            viewModel.setMusic(musicList.get(currentPosition));
        });
        viewModel.getPrevious().observe(getViewLifecycleOwner(), isPrevious -> {
            currentPosition--;
            if (currentPosition < 0)
                currentPosition = musicList.size() - 1;
            viewModel.setMusic(musicList.get(currentPosition));
        });
        viewModel.getRandom().observe(getViewLifecycleOwner(), isRandom -> {
            Random random = new Random();
            int randomSong = random.nextInt(musicList.size());
            while (randomSong == currentPosition) {
                randomSong = random.nextInt(musicList.size());
            }
            currentPosition = randomSong;
            viewModel.setMusic(musicList.get(currentPosition));
        });
        viewModel.getReady().observe(getViewLifecycleOwner(), isReady -> {
            viewModel.setMusic(musicList.get(currentPosition));
            CreateNotification.createNotification(getActivity(), musicList.get(currentPosition), R.drawable.ic_baseline_pause_circle_outline_24);
        });
    }

    @Override
    public void chooseMusic(int position) {
        currentPosition = position;
        if (!isCreated) {
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(R.anim.anim_move_up, R.anim.anim_move_down, R.anim.anim_move_up, R.anim.anim_move_down)
                    .add(R.id.fv_play_music, PlayFragment.class, null, "normal")
                    .addToBackStack(null)
                    .commit();

            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(R.anim.anim_move_up, R.anim.anim_move_down, R.anim.anim_move_up, R.anim.anim_move_down)
                    .add(R.id.fv_play_collapse_music, PlayCollapseFragment.class, null, "collapse")
                    .addToBackStack(null)
                    .commit();

            isCreated = true;
        }
        else{
            viewModel.setMusic(musicList.get(currentPosition));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}