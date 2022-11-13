package com.datpv134.closeeyesandlisten.ui;

import static com.datpv134.closeeyesandlisten.service.MyApplication.mediaPlayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.databinding.ActivityMainBinding;
import com.datpv134.closeeyesandlisten.model.Song;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity implements Serializable {
    ActivityMainBinding binding;
    private final Handler handler = new Handler();
    Song song;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mediaPlayer.isPlaying()) {
            startActivity(new Intent(getBaseContext(), MusicPlayerActivity.class));
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
}