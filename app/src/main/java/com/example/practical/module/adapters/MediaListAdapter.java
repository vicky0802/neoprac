package com.example.practical.module.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practical.R;
import com.example.practical.api.modal.Content;
import com.example.practical.utils.GenRecyclerAdapter;

import java.util.ArrayList;


public class MediaListAdapter extends GenRecyclerAdapter<MediaListAdapter.DataObjectHolder, Content> {


    public MediaListAdapter(ArrayList<Content> contactInfoModels) {
        super(contactInfoModels);
    }

    @Override
    protected DataObjectHolder creatingViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_media, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    protected void bindingViewHolder(DataObjectHolder holder, int position) {
        Content contactInfoModel = getItem(position);
        if (contactInfoModel != null) {
            holder.txtTitle.setText(contactInfoModel.getMediaTitleCustom());
        }
    }


    class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private AppCompatTextView txtTitle;

        DataObjectHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_media_title);
            txtTitle.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getMyClickListener() != null)
                getMyClickListener().onItemClick(getLayoutPosition(), v);
        }
    }
}
