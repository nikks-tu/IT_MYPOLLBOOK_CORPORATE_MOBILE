package com.corporate.contus_Corporate.likes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.corporate.contus_Corporate.responsemodel.LikesResponseModel;
import com.corporate.contusfly_corporate.utils.Utils;
import com.corporate.polls_corporate.polls.R;

import java.util.List;

/**
 * Created by user on 7/8/2015.
 */
public class PollLikesAdapter extends ArrayAdapter<LikesResponseModel.Results.Data> {
    //Layout id
    private final int pollLikesAdapterLayoutId;
    //Activity
    Context pollLikesDisplay;
    //Response from the model class
    private LikesResponseModel.Results.Data likesResponseResult;
    //View holder
    private ViewHolder holder;
    //like view
    private View mlikeView;
    //user profile image
    private String likesUserProfile;

    /**
     * initializes a new instance of the ListView class.
     * @param activity-activity
     * @param dataResults-dataResults
     * @param layoutId-layoutId
     */
    public PollLikesAdapter(PollLikes activity, List<LikesResponseModel.Results.Data> dataResults, int layoutId) {
        super(activity, 0, dataResults);
        this.pollLikesAdapterLayoutId = layoutId;
        this.pollLikesDisplay = activity;
    }

    @Override
    public View getView(final int position, View mPollLikesView, ViewGroup parent) {
        //getView() method to be able to inflate our custom layout & create a View class out of it,
        // we need an instance of LayoutInflater class
        LayoutInflater mInflater = (LayoutInflater)
                pollLikesDisplay.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        mlikeView=mPollLikesView;
        // The old view to reuse, if possible.If convertView is NOT null, we can simple re-use the convertView as the new View.
        // It will happen when a new row appear and a old row in the other end roll out.
        if (mlikeView == null) {
             /* create a new view of my layout and inflate it in the row */
            mlikeView = mInflater.inflate(pollLikesAdapterLayoutId, parent, false);
            //Views holder class
            holder = new ViewHolder();
            //Profile image
            holder.campaignImage = (ImageView) mlikeView.findViewById(R.id.imgProfile);
            //User name
            holder.userName = (TextView) mlikeView.findViewById(R.id.txtGroupName);
            mlikeView.setTag(holder);
        } else {
            holder = (ViewHolder) mlikeView.getTag();
        }
        //Get the reposne from the particular position
        likesResponseResult = getItem(position);
        //Gettting the user profile image
        likesUserProfile = likesResponseResult.getUserInfo().getUserProfileImg();
        //Set the profiel image from the resposne
        Utils.loadImageWithGlideProfileRounderCorner(pollLikesDisplay,likesUserProfile,holder.campaignImage,R.drawable.icon_profile);
        //Setting the user name in text view
        holder.userName.setText(likesResponseResult.getUserInfo().getUserName());
        //Returning the views
        return mlikeView;
    }

    /**
     * A ViewHolder object stores each of the component views inside the tag field of the Layout,
     * so you can immediately access them without the need to look them up repeatedly.
     */
    private class ViewHolder {
        //User name
        private TextView userName;
        //Campaign image
        private ImageView campaignImage;
    }
}