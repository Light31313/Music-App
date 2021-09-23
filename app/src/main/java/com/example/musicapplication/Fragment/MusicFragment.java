package com.example.musicapplication.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicapplication.R;
import com.example.musicapplication.adapter.IMusicAdapter;
import com.example.musicapplication.adapter.MusicAdapter;
import com.example.musicapplication.entity.Music;
import com.example.musicapplication.service.ForegroundService;

import com.example.musicapplication.viewmodel.MusicViewModel;

import java.util.List;
import java.util.Random;


public class MusicFragment extends Fragment implements IMusicAdapter {
    private List<Music> musicList;
    private MusicAdapter adapter;
    private RecyclerView rvListSong;
    private MusicViewModel viewModel;
    private int currentPosition;
    private FragmentContainerView fvPlayMusic;
    public static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION = "CHANNEL_DESCRIPTION";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        rvListSong = view.findViewById(R.id.rv_list_song);
        fvPlayMusic = view.findViewById(R.id.fv_play_music);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent();
        initEvent();
        createNotificationChannel();
    }

    private void initComponent() {
        Bundle bundle = getArguments();
        if (bundle != null)
            musicList = (List<Music>) bundle.getSerializable("musicList");
        adapter = new MusicAdapter(this, musicList);
        rvListSong.setAdapter(adapter);
        rvListSong.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel = new ViewModelProvider(this).get(MusicViewModel.class);
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
        viewModel.getClickable().observe(getViewLifecycleOwner(), isClickable ->{
                fvPlayMusic.setClickable(false);
                fvPlayMusic.setFocusable(false);
        });
        viewModel.getReady().observe(getViewLifecycleOwner(), isReady ->{
            viewModel.setMusic(musicList.get(currentPosition));
        });
    }

    @Override
    public void chooseMusic(int position) {
        currentPosition = position;
        Bundle bundle = new Bundle();
        bundle.putSerializable("music", musicList.get(currentPosition));
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.anim_move_up, R.anim.anim_move_down, R.anim.anim_move_up, R.anim.anim_move_down)
                .add(R.id.fv_play_music, PlayFragment.class, bundle)
                .addToBackStack(null)
                .commit();
        fvPlayMusic.setClickable(true);
        fvPlayMusic.setFocusable(true);
        //startService();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);

            getContext().getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private void startService() {
        Intent intent = new Intent(getActivity(), ForegroundService.class);
        intent.setAction("Action Start Service");
        // put extra...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().startForegroundService(intent);
        } else {
            getContext().startService(intent);
        }
    }

    private void stopService(){
        Intent intent = new Intent(getContext(), ForegroundService.class);
        getContext().stopService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService();
    }
}