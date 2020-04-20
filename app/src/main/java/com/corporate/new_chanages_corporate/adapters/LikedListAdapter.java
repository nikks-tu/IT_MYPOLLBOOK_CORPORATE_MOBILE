package com.corporate.new_chanages_corporate.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.corporate.contusfly_corporate.utils.Utils;
import com.corporate.new_chanages_corporate.models.LikeListModel;
import com.corporate.polls_corporate.polls.R;

import java.util.ArrayList;

public class LikedListAdapter extends RecyclerView.Adapter<LikedListAdapter.ViewHolder> {

    private final ArrayList<LikeListModel> mValues;
    Context context;
    String userId;

    public LikedListAdapter(ArrayList<LikeListModel> items) {
        mValues = items;

    }

    @Override
    public LikedListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liked_list_item, parent, false);
        context=parent.getContext();
        return new LikedListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LikedListAdapter.ViewHolder holder, final int position) {

        holder.title.setText(mValues.get(position).getName());
        Utils.loadImageWithGlideRounderCorner(context, mValues.get(position).getImage(), holder.image,
                R.drawable.img_ic_user);

    }




    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        TextView title;
        ImageView image;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.image);

        }


    }
}


