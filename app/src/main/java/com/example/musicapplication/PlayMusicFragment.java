package com.example.musicapplication;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplication.utils.MediaPlayerUtils;


public class PlayMusicFragment extends AppCompatActivity implements View.OnClickListener, MediaPlayerUtils.onListener, SeekBar.OnSeekBarChangeListener {

    private static final int SUBTRACT_TIME = 5;
    private static final int ADD_TIME = 5;

    private SeekBar sb_song;
    private TextView tv_max_time;
    private TextView tv_current_position;
    private Button btn_rewind;
    private Button btn_pause;
    private Button btn_start;
    private Button btn_fast_forward;

    private MediaPlayerUtils mediaPlayerUtils;

    private Handler handler;
    private UpdateSeekBarRunnable updateSeekBarRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        sb_song = findViewById(R.id.sb_song);
        tv_max_time = findViewById(R.id.tv_max_time);
        tv_current_position = findViewById(R.id.tv_current_position);

        btn_rewind = findViewById(R.id.btn_rewind);
        btn_pause = findViewById(R.id.btn_pause);
        btn_start = findViewById(R.id.btn_start);
        btn_fast_forward = findViewById(R.id.btn_fast_forward);

        btn_start.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_rewind.setOnClickListener(this);
        btn_fast_forward.setOnClickListener(this);

        sb_song.setClickable(false);
        sb_song.setOnSeekBarChangeListener(this);

        handler = new Handler();
        updateSeekBarRunnable = new UpdateSeekBarRunnable();

        mediaPlayerUtils = new MediaPlayerUtils(this);
        String url = "https://data2.chiasenhac.com/stream2/1719/5/1718334-c470e7fb/128/Minh%20La%20Gi%20Cua%20Nhau%20-%20Lou%20Hoang.mp3";
        mediaPlayerUtils.setDataSource(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rewind:
                mediaPlayerUtils.rewind(SUBTRACT_TIME);
                break;
            case R.id.btn_start:
                mediaPlayerUtils.start();
                break;
            case R.id.btn_pause:
                mediaPlayerUtils.pause();
                break;
            case R.id.btn_fast_forward:
                mediaPlayerUtils.fastForward(ADD_TIME);
                break;
        }
    }

    private String convertSecondsToString(int seconds) {
        long m = seconds / 60;
        long s = seconds - 60 * m;
        return m + ":" + s;
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Cannot open media", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeState(MediaPlayerUtils.State state) {
        // update UI
        switch (state) {
            case INITIALIZED:
            case ERROR:
                sb_song.setProgress(0);
                sb_song.setSecondaryProgress(0);

                tv_current_position.setText(convertSecondsToString(0));
                tv_max_time.setText(convertSecondsToString(0));

                btn_start.setEnabled(false);
                btn_pause.setEnabled(false);
                btn_rewind.setEnabled(false);
                btn_fast_forward.setEnabled(false);
                break;

            case STARTED:
                handler.post(updateSeekBarRunnable);

                btn_start.setEnabled(false);
                btn_pause.setEnabled(true);
                btn_rewind.setEnabled(true);
                btn_fast_forward.setEnabled(true);
                break;

            case PAUSED:
                handler.removeCallbacks(updateSeekBarRunnable);

                btn_start.setEnabled(true);
                btn_pause.setEnabled(false);
                break;

            case COMPLETED:
                handler.removeCallbacks(updateSeekBarRunnable);

                btn_start.setEnabled(true);
                btn_pause.setEnabled(false);
                btn_rewind.setEnabled(true);
                btn_fast_forward.setEnabled(false);
                break;

            default:
                break;
        }
    }

    @Override
    public void updateBuffer(int percent) {
        Log.i("PlayMusicFragment", "Buffer: " + percent);
        int max = sb_song.getMax();
        sb_song.setSecondaryProgress(percent * max / 100);
    }

    @Override
    public void onPrepared(int duration) {
        sb_song.setMax(duration);
        tv_max_time.setText(convertSecondsToString(duration));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mediaPlayerUtils.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class UpdateSeekBarRunnable implements Runnable {

        @Override
        public void run() {
            int currentPosition = mediaPlayerUtils.getCurrentPosition(); // seconds
            sb_song.setProgress(currentPosition);
            tv_current_position.setText(convertSecondsToString(currentPosition));

            handler.postDelayed(this, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayerUtils.release();
    }
}

