package com.example.musicapplication.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.musicapplication.R;
import com.example.musicapplication.adapter.IMusicAdapter;
import com.example.musicapplication.adapter.MusicAdapter;
import com.example.musicapplication.api.API;
import com.example.musicapplication.databinding.DialogDeleteMusicBinding;
import com.example.musicapplication.databinding.DialogInsertMusicBinding;
import com.example.musicapplication.databinding.FragmentMusicBinding;
import com.example.musicapplication.entity.Music;

import com.example.musicapplication.viewmodel.MusicViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MusicFragment extends Fragment implements IMusicAdapter {
    private List<Music> musicList;
    private RecyclerView rvListSong;
    private MusicViewModel viewModel;
    private int currentPosition;
    private boolean isCreated;
    private MusicAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

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
        adapter = new MusicAdapter(getContext(), this, musicList);
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
        } else {
            viewModel.setMusic(musicList.get(currentPosition));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete:
                initDeleteDialog();
                return true;

            case R.id.action_insert:
                initInsertDialog();
                return true;
            case R.id.action_update:
                updateMusic();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateMusic() {
    }

    private void insertMusic(Music music) {
        API.getMusicRetrofit().insertMusic(music)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(requireContext(), "Insert Successfully", Toast.LENGTH_SHORT).show();
                        initMusicList();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initInsertDialog() {
        Dialog dialog = new Dialog(requireContext());
        DialogInsertMusicBinding dialogBinding = DialogInsertMusicBinding.inflate(dialog.getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnInsertSong.setOnClickListener(view -> {
                    String singer = dialogBinding.edtSinger.getText().toString();
                    String imageUrl = dialogBinding.edtImageUrl.getText().toString();
                    String source = dialogBinding.edtSource.getText().toString();
                    String songName = dialogBinding.edtSongName.getText().toString();
                    Music music = new Music(imageUrl, songName, singer, source);
                    insertMusic(music);
                    dialog.dismiss();
                }
        );
        dialog.show();
    }


    private void initDeleteDialog() {

        Dialog dialog = new Dialog(requireContext());
        DialogDeleteMusicBinding dialogBinding = DialogDeleteMusicBinding.inflate(dialog.getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        List<String> songNames = new ArrayList<>();
        for (Music music : musicList) {
            songNames.add(music.getSongName());
        }
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, songNames);
        dialogBinding.spMusics.setAdapter(arrayAdapter);

        dialogBinding.btnDeleteSong.setOnClickListener(view -> {
                    int id = musicList.get(dialogBinding.spMusics.getSelectedItemPosition()).getId();
                    deleteMusic(id);
                    dialog.dismiss();
                }
        );
        dialog.show();
    }

    private void deleteMusic(int id) {

        API.getMusicRetrofit().deleteMusic(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(requireContext(), "Delete Successfully", Toast.LENGTH_SHORT).show();
                initMusicList();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initMusicList() {
        API.getMusicRetrofit().getAllMusics().enqueue(new Callback<ArrayList<Music>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ArrayList<Music>> call, Response<ArrayList<Music>> response) {
                musicList.clear();
                if (response.body() != null)
                    musicList.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<Music>> call, Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}