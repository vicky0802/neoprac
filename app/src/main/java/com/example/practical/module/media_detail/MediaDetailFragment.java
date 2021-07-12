package com.example.practical.module.media_detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.practical.R;
import com.example.practical.api.modal.Content;
import com.example.practical.databinding.MediaDetailFragmentBinding;

import org.jetbrains.annotations.NotNull;

public class MediaDetailFragment extends Fragment {

    private MediaDetailViewModel mViewModel;
    MediaDetailFragmentBinding binding;

    public static MediaDetailFragment newInstance() {
        return new MediaDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.media_detail_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MediaDetailViewModel.class);
        if (getArguments() != null) {
            if (getArguments().containsKey("mediaData")) {
                Content media = getArguments().getParcelable("mediaData");
                binding.txtTitle.setText(media.getMediaTitleCustom());
                binding.txtDate.setText(media.getMediaDate().getDateString());
                binding.txtLink.setText(media.getMediaUrl());
                binding.txtLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = media.getMediaUrl();
                        if (!url.startsWith("http") && !url.startsWith("https")) {
                            url = "http" + url;
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        getContext().startActivity(intent);
                    }
                });
            }
        }
    }
}