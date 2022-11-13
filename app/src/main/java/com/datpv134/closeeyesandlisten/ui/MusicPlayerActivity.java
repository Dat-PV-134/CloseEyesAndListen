package com.datpv134.closeeyesandlisten.ui;

import static com.datpv134.closeeyesandlisten.service.MyApplication.mediaPlayer;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isRunning;
import static com.datpv134.closeeyesandlisten.service.MyApplication.playNumber;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.databinding.ActivityMusicPlayerBinding;
import com.datpv134.closeeyesandlisten.model.Song;
import com.datpv134.closeeyesandlisten.service.MyApplication;
import com.datpv134.closeeyesandlisten.service.MyService;

import java.util.Objects;

public class MusicPlayerActivity extends AppCompatActivity {
    ActivityMusicPlayerBinding binding;
    private final Handler handler = new Handler();
    private Song song;
    int myAction;
    private Intent intent1;
    private Bundle bundleF = new Bundle();

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(messageReceiver, new IntentFilter("my_message"));
        isRunning = true;
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            myAction = intent.getIntExtra("my_action", 0);
            if (myAction == 1) {
                playOrPause();
            } else if (myAction == 2) {
                playOrPause();
            } else if (myAction == 4) {
                stopService(intent1);
                bundleF.putSerializable("song1", song);
                bundleF.putBoolean("isPlaying", true);
                bundleF.putBoolean("isNewSong", false);
                intent1.putExtras(bundleF);
                startService(intent1);
            } else if (myAction == 0) {
                stopService(intent1);
            }
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        isRunning = false;
        super.onPause();
        onStop();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_music_player);

        isRunning = true;

        song = (Song) getIntent().getSerializableExtra("Song");
        Song s = (Song) ((MyApplication) this.getApplication()).getCurrentSong();

        if (song == null) {
            song = s;
        }

        Glide.with(getBaseContext())
                .load(song.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgSongPlayer);

        intent1 = new Intent(getBaseContext(), MyService.class);

        binding.sbPlayer.setMax(100);
        setButtonPlayOrPause();

        bundleF.putSerializable("song1", song);
        bundleF.putBoolean("isPlaying", false);
        bundleF.putBoolean("isNewSong", false);
        intent1.putExtras(bundleF);
        startService(intent1);

        if (!Objects.equals(s.getId(), song.getId()) || Objects.equals(s.getId(), "-1")) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            mediaPlayer = new MediaPlayer();

            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", true);
            bundleF.putBoolean("isNewSong", true);
            intent1.putExtras(bundleF);
            startService(intent1);

            prepareMediaPlayer();

            ((MyApplication) this.getApplication()).setCurrentSong(song);
            playNumber = 0;

        } else {
            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", true);
            bundleF.putBoolean("isNewSong", false);
            intent1.putExtras(bundleF);
            startService(intent1);
            setButtonPlayOrPause();
            continueOldSong();
            binding.sbPlayer.setSecondaryProgress(100);
        }

//
//        Bundle bundleF = new Bundle();
//        bundleF.putSerializable("song1", song);
//        bundleF.putBoolean("isPlaying", true);
//        intent1.putExtras(bundleF);
//
//        startService(intent1);



//        mediaPlayer = new MediaPlayer();

        binding.imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playOrPause();
            }
        });

//        prepareMediaPlayer();

        binding.sbPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SeekBar seekBar = (SeekBar) view;
                int playPosition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                mediaPlayer.seekTo(playPosition);
                binding.tvCurrentTime.setText(miliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });



        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                binding.sbPlayer.setSecondaryProgress(i);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.sbPlayer.setProgress(0);
                binding.imagePlay.setImageResource(R.drawable.icon_play);
                binding.tvCurrentTime.setText(R.string.zero);
                mediaPlayer.reset();
                playNumber++;
                prepareMediaPlayer();
//              binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
            }
        });
    }

    private void prepareMediaPlayer() {
        try {
            mediaPlayer.setDataSource(song.getSrc());
            mediaPlayer.prepare();
            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", true);
            bundleF.putBoolean("isNewSong", false);
            intent1.putExtras(bundleF);
            startService(intent1);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    if (playNumber <= 1) {
                        mediaPlayer.start();
                        binding.imagePlay.setImageResource(R.drawable.icon_pause);
                    } else {
                        bundleF.putSerializable("song1", song);
                        bundleF.putBoolean("isPlaying", false);
                        bundleF.putBoolean("isNewSong", false);
                        intent1.putExtras(bundleF);
                        startService(intent1);
                    }
                    updateSeekBar();
                }
            });
            binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void continueOldSong() {
        binding.tvCurrentTime.setText(miliSecondsToTimer(mediaPlayer.getCurrentPosition()));
        binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
        updateSeekBar();
    }

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            binding.tvCurrentTime.setText(miliSecondsToTimer(currentDuration));
        }
    };

    private void updateSeekBar() {
        if (mediaPlayer.isPlaying()) {
            binding.sbPlayer.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    private void playOrPause() {
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(updater);
            mediaPlayer.pause();
            binding.imagePlay.setImageResource(R.drawable.icon_play);
            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", false);
            bundleF.putBoolean("isNewSong", false);
            intent1.putExtras(bundleF);
            startService(intent1);
        } else {
            mediaPlayer.start();
            binding.imagePlay.setImageResource(R.drawable.icon_pause);
            updateSeekBar();
            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", true);
            bundleF.putBoolean("isNewSong", false);
            intent1.putExtras(bundleF);
            startService(intent1);
        }
    }

    private void setButtonPlayOrPause() {
        if (mediaPlayer.isPlaying()) {
            binding.imagePlay.setImageResource(R.drawable.icon_pause);
        } else {
            binding.imagePlay.setImageResource(R.drawable.icon_play);
        }
    }

    private String miliSecondsToTimer(long miliSeconds) {
        String timerString = "";
        String secondString;

        int hours = (int) (miliSeconds / (1000 * 60 * 60));
        int minutes = (int) (miliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((miliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";
        }

        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;
        }
        timerString = timerString + minutes + ":" + secondString;

        return timerString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }
}