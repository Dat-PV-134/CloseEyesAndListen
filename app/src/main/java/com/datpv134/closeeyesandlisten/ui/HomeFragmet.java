package com.datpv134.closeeyesandlisten.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.adapter.APIClientPM;
import com.datpv134.closeeyesandlisten.adapter.IOnClickSong;
import com.datpv134.closeeyesandlisten.adapter.SongAdapter;
import com.datpv134.closeeyesandlisten.databinding.FragmentHomeBinding;
import com.datpv134.closeeyesandlisten.model.Song;
import com.datpv134.closeeyesandlisten.service.MyApplication;
import com.datpv134.closeeyesandlisten.service.MyService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragmet extends Fragment {
    FragmentHomeBinding binding;
    List<Song> songList;
    SongAdapter songAdapter;

    public static HomeFragmet newInstance() {

        Bundle args = new Bundle();

        HomeFragmet fragment = new HomeFragmet();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        songList = new ArrayList<>();

        Call<List<Song>> call = APIClientPM.getInstance().getSongs();

        call.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songList = response.body();

                RecyclerView.LayoutManager managerChill = new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false);
                RecyclerView.LayoutManager managerLove = new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false);
                RecyclerView.LayoutManager managerRemix = new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false);

                songAdapter = new SongAdapter(songList, getContext(), new IOnClickSong() {
                    @SuppressLint("UseRequireInsteadOfGet")
                    @Override
                    public void onClickSong(Song song) {


//                        getActivity().bindService(intent1, serviceConnection, Context.BIND_AUTO_CREATE);

//                        Intent intent = new Intent(getContext(), MainActivity.class);
//                        intent.putExtra("checkPlaying", true);
//
//                        intent.putExtra("song", song);
//
//                        startActivity(intent);

                        Intent intent2 = new Intent(getContext(), MusicPlayerActivity.class);
                        intent2.putExtra("Song", song);
                        startActivity(intent2);
                    }
                });

                binding.rvChill.setLayoutManager(managerChill);
                binding.rvLove.setLayoutManager(managerLove);
                binding.rvRemix.setLayoutManager(managerRemix);

                binding.rvChill.setAdapter(songAdapter);
                binding.rvLove.setAdapter(songAdapter);
                binding.rvRemix.setAdapter(songAdapter);
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }
}
