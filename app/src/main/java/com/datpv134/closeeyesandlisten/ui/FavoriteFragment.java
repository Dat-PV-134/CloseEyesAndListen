package com.datpv134.closeeyesandlisten.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.databinding.FragmentFavoriteBinding;

public class FavoriteFragment extends Fragment {
    FragmentFavoriteBinding binding;

    public static FavoriteFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FavoriteFragment fragment = new FavoriteFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);
        return binding.getRoot();
    }
}
