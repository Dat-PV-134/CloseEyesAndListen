package com.datpv134.closeeyesandlisten.ui;

import static com.datpv134.closeeyesandlisten.service.MyApplication.mediaPlayer;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isRunning;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.getCurrentPos;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isShuffe;
import static com.datpv134.closeeyesandlisten.service.MyApplication.repeatCode;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class MusicPlayerActivity extends AppCompatActivity {
    ActivityMusicPlayerBinding binding;
    private final Handler handler = new Handler();
    private Song song;
    int myAction;
    private Intent intent1;
    private Bundle bundleF = new Bundle();
    private int isFirstTime = 0;


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
            myAction = intent.getIntExtra("my_action", -1);
            if (myAction == 1) {
                playOrPause();
            } else if (myAction == 2) {
                playOrPause();
            } else if (myAction == 3) {
                nextSong();
            } else if (myAction == 4) {
                previousSong();
            } else if (myAction == 5) {
                stopService(intent1);
                bundleF.putSerializable("song1", song);
                bundleF.putBoolean("isPlaying", false);
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

    private void startNewSong() {
        mediaPlayer.stop();
        mediaPlayer.reset();

        mediaPlayer = new MediaPlayer();

        bundleF.putSerializable("song1", song);
        bundleF.putBoolean("isPlaying", false);
        bundleF.putBoolean("isNewSong", true);
        intent1.putExtras(bundleF);
        startService(intent1);

        prepareMediaPlayer();

        ((MyApplication) this.getApplication()).setCurrentSong(song);
    }


    private void startOldSong() {
        binding.sbPlayer.setMax(100);
        setButtonPlayOrPause();
        binding.sbPlayer.setSecondaryProgress(100);
        continueOldSong();
    }

    private void setNewSong() {
        ((MyApplication) this.getApplication()).setCurrentSong(song);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_music_player);

        Song s = ((MyApplication) this.getApplication()).getCurrentSong();

        boolean checkComeBack = getIntent().getBooleanExtra("comeback", false);

        if (!checkComeBack) {
            songList = (ArrayList<Song>) getIntent().getSerializableExtra("SongList");
            song = (Song) getIntent().getSerializableExtra("Song");
            getCurrentPos = Integer.parseInt(song.getId()) - 1;
            Log.e("yep", "yep");
        } else {
            song = s;
        }

        Glide.with(getBaseContext())
                .load(song.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgSongPlayer);

        intent1 = new Intent(getBaseContext(), MyService.class);

//        bundleF.putSerializable("song1", song);
//        bundleF.putBoolean("isPlaying", false);
//        bundleF.putBoolean("isNewSong", false);
//        intent1.putExtras(bundleF);
//        startService(intent1);

        binding.sbPlayer.setMax(100);

        if (!mediaPlayer.isPlaying()) {
            binding.sbPlayer.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
        }

        Log.e("current", s.getId());
        Log.e("Song", song.getId());

        if (!Objects.equals(song.getId(), s.getId()) || s.getId() == "-1") {
            startNewSong();
            Log.e("new song", "new song");
        } else {
            Log.e("old song", "old song");
            startOldSong();
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

        isRunning = true;

        setOnCompleteASong();

        onClickOtherButton();
    }

    private void setOnCompleteASong() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.sbPlayer.setProgress(0);
                binding.imagePlay.setImageResource(R.drawable.icon_play);
                binding.tvCurrentTime.setText(R.string.zero);
                mediaPlayer.reset();

                if (isFirstTime == 0) {
                    prepareMediaPlayer();
                    isFirstTime++;
                    return;
                }

                if (repeatCode == 2) {
                    repeatSong();
                    return;
                }

                if (getCurrentPos == (songList.size() - 1)) {
                    if (repeatCode == 0) {
                        nextSongNotStart();
                        return;
                    } else if (repeatCode == 1) {
                        nextSong();
                        return;
                    }
                } else {
                    nextSong();
                    return;
                }

//              binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
            }
        });
    }

    private void nextSongNotStart() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = new MediaPlayer();


        song = songList.get(0);
        getCurrentPos = 0;

        Glide.with(getBaseContext())
                .load(song.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgSongPlayer);

        try {
            mediaPlayer.setDataSource(song.getSrc());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
                    setNewSong();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.sbPlayer.setSecondaryProgress(100);

        bundleF.putSerializable("song1", song);
        bundleF.putBoolean("isPlaying", false);
        bundleF.putBoolean("isNewSong", false);
        intent1.putExtras(bundleF);
        startService(intent1);
    }


    private void repeatSong() {
        try {
            mediaPlayer.setDataSource(song.getSrc());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    binding.imagePlay.setImageResource(R.drawable.icon_pause);

                    bundleF.putSerializable("song1", song);
                    bundleF.putBoolean("isPlaying", true);
                    bundleF.putBoolean("isNewSong", false);
                    intent1.putExtras(bundleF);
                    startService(intent1);

                    updateSeekBar();
                }
            });
            binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
            ((MyApplication) this.getApplication()).setCurrentSong(song);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Loi mang", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickOtherButton() {
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousSong();
            }
        });

        binding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong();
            }
        });

        binding.imgShuffing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShuffe) {
                    Collections.sort(songList, new Comparator<Song>() {
                        @Override
                        public int compare(Song song, Song t1) {
                            return Integer.parseInt(song.getId()) - Integer.parseInt(t1.getId());
                        }
                    });
                    isShuffe = false;
                    binding.imgShuffing.setImageResource(R.drawable.icon_shuffing);
                } else {
                    Collections.shuffle(songList);
                    isShuffe = true;
                    binding.imgShuffing.setImageResource(R.drawable.ic_shuffed);
                }
            }
        });

        binding.imgRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatCode == 0) {
                    repeatCode = 1;
                    binding.imgRepeat.setImageResource(R.drawable.ic_repeat_list);
                } else if (repeatCode == 1) {
                    repeatCode = 2;
                    binding.imgRepeat.setImageResource(R.drawable.ic_repeat_one);
                } else if (repeatCode == 2) {
                    repeatCode = 0;
                    binding.imgRepeat.setImageResource(R.drawable.icon_repeat);
                }
            }
        });
    }

//    private int getPlayRepeat(int repeatCode) {
//        int playRepeat = 0;
//
//        switch (repeatCode) {
//            case 0: break;
//            case 1: break;
//            case 2: break;
//            default: break;
//        }
//
//        return ;
//    }


    private void nextSong() {
        bundleF.putSerializable("song1", song);
        bundleF.putBoolean("isPlaying", false);
        bundleF.putBoolean("isNewSong", false);
        intent1.putExtras(bundleF);
        startService(intent1);

        if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
            mediaPlayer.stop();
        }

        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = new MediaPlayer();

        if (getCurrentPos < (songList.size() - 1)) {
            song = songList.get(getCurrentPos + 1);
            getCurrentPos += 1;
            Glide.with(getBaseContext())
                    .load(song.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgSongPlayer);

            prepareMediaPlayer();
        } else {
            song = songList.get(0);
            getCurrentPos = 0;
            Glide.with(getBaseContext())
                    .load(song.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgSongPlayer);

            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", true);
            bundleF.putBoolean("isNewSong", false);
            intent1.putExtras(bundleF);
            startService(intent1);

            prepareMediaPlayer();
        }

        setNewSong();

        binding.sbPlayer.setSecondaryProgress(100);

        setOnCompleteASong();
    }

    private void previousSong() {
        bundleF.putSerializable("song1", song);
        bundleF.putBoolean("isPlaying", false);
        bundleF.putBoolean("isNewSong", false);
        intent1.putExtras(bundleF);
        startService(intent1);

        if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
            mediaPlayer.stop();
        }

        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = new MediaPlayer();

        if (getCurrentPos > 0) {
            song = songList.get(getCurrentPos - 1);
            getCurrentPos -= 1;
            Glide.with(getBaseContext())
                    .load(song.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgSongPlayer);

            prepareMediaPlayer();
        } else {
            song = songList.get(songList.size() - 1);
            getCurrentPos = songList.size() - 1;
            Glide.with(getBaseContext())
                    .load(song.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgSongPlayer);

            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", true);
            bundleF.putBoolean("isNewSong", false);
            intent1.putExtras(bundleF);
            startService(intent1);

            prepareMediaPlayer();
        }

        setNewSong();

        binding.sbPlayer.setSecondaryProgress(100);

        setOnCompleteASong();
    }

    private void prepareMediaPlayer() {
        try {
            mediaPlayer.setDataSource(song.getSrc());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    binding.imagePlay.setImageResource(R.drawable.icon_pause);

                    bundleF.putSerializable("song1", song);
                    bundleF.putBoolean("isPlaying", true);
                    bundleF.putBoolean("isNewSong", false);
                    intent1.putExtras(bundleF);
                    startService(intent1);

                    updateSeekBar();
                }
            });
            binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Loi mang", Toast.LENGTH_SHORT).show();
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