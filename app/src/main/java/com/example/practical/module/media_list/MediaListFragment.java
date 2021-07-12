package com.example.practical.module.media_list;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.practical.R;
import com.example.practical.api.modal.Content;
import com.example.practical.databinding.MediaListFragmentBinding;
import com.example.practical.module.adapters.MediaListAdapter;
import com.example.practical.utils.GenRecyclerAdapter;
import com.example.practical.utils.SwipeToDeleteCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MediaListFragment extends Fragment {

    private MediaListViewModel mViewModel;
    MediaListFragmentBinding binding;
    MediaListAdapter mAdapter;

    public static MediaListFragment newInstance() {
        return new MediaListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.media_list_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MediaListViewModel.class);
        mAdapter = new MediaListAdapter(new ArrayList<>());
        callAPI();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpSwipeRefresh() {
        binding.swpLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callAPI();
            }
        });
    }

    private void callAPI() {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("loading");
        pd.show();
        mViewModel.getMediaList(pd);
        mViewModel.mediaList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                enableSwipeToDeleteAndUndo();
                setUpSwipeRefresh();
                setupRecyclerView(Objects.requireNonNull(mViewModel.mediaList.get()).getContent());
                if ((binding.swpLayout.isRefreshing()))
                    binding.swpLayout.setRefreshing(false);
                pd.hide();
            }
        });
    }

    private void setupRecyclerView(ArrayList<Content> content) {
        binding.rvMediaList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((position, v) -> {
            Bundle data = new Bundle();
            data.putParcelable("mediaData", mAdapter.getItem(position));
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_media_list_to_media_detail, data);
        });
        mAdapter.addAll(content);
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Content item = mAdapter.getItem(position);

                mAdapter.deleteItem(position);


            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(binding.rvMediaList);
    }
}