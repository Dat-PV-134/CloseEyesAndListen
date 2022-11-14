package com.datpv134.closeeyesandlisten.ui;

import static com.datpv134.closeeyesandlisten.service.MyApplication.CHANNEL_ID;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isPushNotifi;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isRunning;
import static com.datpv134.closeeyesandlisten.service.MyApplication.mediaPlayer;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.databinding.ActivityMainBinding;
import com.datpv134.closeeyesandlisten.service.MyService;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity implements Serializable {
    ActivityMainBinding binding;
    private final Handler handler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isRunning || mediaPlayer.isPlaying() || isPushNotifi) {
            Intent intent = new Intent(getBaseContext(), MusicPlayerActivity.class);
            intent.putExtra("comeback", true);
            startActivity(intent);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        onClickNavigationButton();

        getFragment(HomeFragmet.newInstance());
    }

    void onClickNavigationButton() {
        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragment(HomeFragmet.newInstance());
                binding.layoutSongPlayingSmall.setVisibility(View.VISIBLE);
                changeButton(1);
            }
        });

        binding.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragment(FavoriteFragment.newInstance());
                binding.layoutSongPlayingSmall.setVisibility(View.GONE);
                changeButton(2);
            }
        });

        binding.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragment(ProfileFragment.newInstance());
                binding.layoutSongPlayingSmall.setVisibility(View.GONE);
                changeButton(3);
            }
        });
    }

    void changeButton(int id) {
        if (id == 1) {
            binding.btnHome.setImageResource(R.drawable.icon_home_onclick);
        } else {
            binding.btnHome.setImageResource(R.drawable.icon_home);
        }

        if (id == 2) {
            binding.btnFav.setImageResource(R.drawable.icon_fav_onclick);
        } else {
            binding.btnFav.setImageResource(R.drawable.icon_fav);
        }

        if (id == 3) {
            binding.btnProfile.setImageResource(R.drawable.icon_me_onclick);
        } else {
            binding.btnProfile.setImageResource(R.drawable.icon_me);
        }
    }

    void getFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentId, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(getBaseContext(), MyService.class);
        stopService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.deleteNotificationChannel(CHANNEL_ID);
            }
        }
        super.onDestroy();
    }
}