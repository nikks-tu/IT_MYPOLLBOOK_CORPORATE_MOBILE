package com.corporate.new_chanages_corporate.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.corporate.contus_Corporate.activity.CustomRequest;
import com.corporate.contus_Corporate.activity.VolleyResponseListener;
import com.corporate.contus_Corporate.app.Constants;
import com.corporate.contusfly_corporate.MApplication;
import com.corporate.contusfly_corporate.model.AnnouncementReadObject;
import com.corporate.contusfly_corporate.utils.Utils;
import com.corporate.new_chanages_corporate.AnnouncementLikes_actvty;
import com.corporate.new_chanages_corporate.Announcement_comments_Act;
import com.corporate.new_chanages_corporate.models.AnnouncementModel;
import com.corporate.polls_corporate.polls.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AnnouncementRecyclerAdapter extends RecyclerView.Adapter<AnnouncementRecyclerAdapter.ViewHolder> {

    private final ArrayList<AnnouncementModel> mValues;
    Context context;
    String userId;
    boolean like;
    public EventListener eventListener;
    public AnnouncementRecyclerAdapter(ArrayList<AnnouncementModel> items, EventListener eventListener) {
        mValues = items;
        this.eventListener = eventListener;
    }

    @Override
    public AnnouncementRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_item, parent, false);
        context=parent.getContext();
        return new AnnouncementRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AnnouncementRecyclerAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.txtName.setText(mValues.get(position).getTitle());

        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Light.ttf");
        Typeface faceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Regular.otf");

        holder.txtName.setTypeface(faceRegular);
        holder.txtTime.setTypeface(face);
        holder.txtCategory.setTypeface(face);
        holder.txtPostedOn.setTypeface(face);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.txtStatus.setText(Html.fromHtml(mValues.get(position).getAnnouncement(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.txtStatus.setText(Html.fromHtml(mValues.get(position).getAnnouncement()));
        }
       // holder.txtStatus.setText(mValues.get(position).getAnnouncement());
        if(mValues.get(position).getUser_participate_announcements()!=null)
        {

            if(mValues.get(position).getUser_participate_announcements().size()>0)
            {
                if(mValues.get(position).getUser_participate_announcements().get(0).getIsRead()==1)
                {
                    holder.txtParticcipation.setTextColor(context.getResources().getColor(R.color.grey_color));
                    holder.txtParticcipation.setEnabled(false);
                }
                else {
                    holder.txtParticcipation.setTextColor(context.getResources().getColor(R.color.blue_color));
                    holder.txtParticcipation.setEnabled(true);
                }
            }

        }
        else {
            holder.txtParticcipation.setTextColor(context.getResources().getColor(R.color.blue_color));
            holder.txtParticcipation.setEnabled(true);
        }
        holder.txtLike2.setText(mValues.get(position).getAnnouncementLikesCounts());
        holder.txtCommentsCounts.setText(mValues.get(position).getAnnouncementCommentsCounts());
        if(mValues.get(position).getCreated_at()!=null)
        {
            String strCurrentDate = mValues.get(position).getCreated_at();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date newDate = null;
            try {
                newDate = format.parse(strCurrentDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            format = new SimpleDateFormat("dd-MMM-yy HH:mm");
            String date = format.format(newDate);
            holder.txtTime.setText(date);
        }
        else
        {
        }
        Utils.loadImageWithGlideRounderCorner(context, mValues.get(position).getAdminProfilePic(), holder.imgProfile,
                R.drawable.img_ic_user);
        userId = MApplication.getString(context, Constants.USER_ID);
        //Loading into the gilde
       if(mValues.get(position).getImage().equals(""))
       {
           holder.singleOption.setVisibility(View.GONE);
       }
       else {
           holder.singleOption.setVisibility(View.VISIBLE);
           Utils.loadImageWithGlideRounderCorner(context, mValues.get(position).getImage(), holder.singleOption,
                   R.drawable.placeholder_image);
            }
        holder.txtCommentsCounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Announcement_comments_Act.class);
                i.putExtra("AnnouncemenrID",mValues.get(position).getId());
                context.startActivity(i);
            }
        });


        holder.unLikeDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(mValues.get(position).getUserAnnouncementLikes().equalsIgnoreCase("0"))
                {
                    like=false;
                }
                else
                {
                    like=true;
                }


                if(like)
                {
                    like=false;
                    mValues.get(position).setUserAnnouncementLikes("0");
                }
                else
                {
                    like=true;
                    mValues.get(position).setUserAnnouncementLikes("1");
                }

                update_like(position,like ,mValues.get(position).getId(),holder.txtLike2);
            }
        });

       /* holder.unLikeDislike.s(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                update_like(isChecked,mValues.get(position).getId(),holder.txtLike2);
            }
        });*/

        holder.txtLike2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AnnouncementLikes_actvty.class);
                i.putExtra("AnnouncemenrID",mValues.get(position).getId());
                context.startActivity(i);
            }
        });
        if(mValues.get(position).getUserAnnouncementLikes().toString().equalsIgnoreCase("1"))
        {
            holder.unLikeDislike.setChecked(true);
            like=true;
        }
        else
        {
            holder.unLikeDislike.setChecked(false);
            like=false;
        }

        holder.txtCategory.setText(mValues.get(position).getCategory_name());

        holder.txtParticcipation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAnnouncement(position,mValues.get(position).getId());
            }
        });
    }

    public void updateAnnouncement(final int position , String id)
    {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action","announcement_read");
            obj.put("user_id",userId);
            obj.put("announcement_id",id);

            Log.v("...", obj.toString());
        }
        catch (Exception ae)
        {

        }


        CustomRequest.makeJsonObjectRequest(context, Constants.PARTICIPATE_ANNOUNCEMENT,obj, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
//            dialog.dismiss();
                Log.i("onErrormessage", "message= " +message);
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Log.i("PCCmessage", "message " + result.getString("msg"));
                    int seccesss=response.optInt("success");
                    int count=response.optInt("count");
                    if(seccesss==1)
                    {

                       // eventListener.onEvent(true);
                        Toast.makeText(context,response.getString("msg"),Toast.LENGTH_SHORT).show();


                        ArrayList<AnnouncementReadObject> list = new ArrayList<>();
                        AnnouncementReadObject readObject = new AnnouncementReadObject();
                        readObject.setId(0);
                        readObject.setAnnouncementId(0);
                        readObject.setIsRead(1);
                        readObject.setUserId(0);
                        readObject.setStatus(0);
                        readObject.setIsDelete(0);
                        list.add(readObject);

                        mValues.get(position).setUser_participate_announcements(list);
                        notifyDataSetChanged();


                    }
                    else
                    {

                       // eventListener.onEvent(false);
                        Toast.makeText(context,response.getString("msg"),Toast.LENGTH_SHORT).show();
                        mValues.get(position).getUser_participate_announcements().get(0).setIsRead(0);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void update_like(final int position , final boolean like, String id, final TextView likecount)
    {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action","announcement_likes");
            obj.put("user_id",userId);
            obj.put("announcement_id",id);
            if(like)
            {
                obj.put("likes","1") ;
            }
            else
            {
                obj.put("likes","0");
            }

            Log.v("...", obj.toString());
        }
        catch (Exception ae)
        {

        }


        CustomRequest.makeJsonObjectRequest(context, Constants.UPDATE_LIKE,obj, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
//            dialog.dismiss();
                Log.i("onErrormessage", "message= " +message);
            }

            @Override
            public void onResponse(JSONObject response) {
                //Log.i("PCCmessage", "message " + result.getString("msg"));
                int success = response.optInt("success");
                int count=response.optInt("count");
                likecount.setText(""+count);
                mValues.get(position).setAnnouncementLikesCounts(""+count);



                if(success==1)
                {
                   // Toast.makeText(context,response.getString("msg"),Toast.LENGTH_SHORT).show();
                }
                else
                {
                   // Toast.makeText(context,response.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface EventListener {
        void onEvent(Boolean data);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        TextView txtTime,txtName,txtCategory,txtStatus,txtLike2,txtCommentsCounts,txtParticcipation, txtPostedOn;
        ImageView imgTime;
        AnnouncementModel mItem;
        ImageView singleOption,imgProfile;
        CheckBox unLikeDislike;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtTime =  view.findViewById(R.id.txtTime);
            txtName =  view.findViewById(R.id.txtName);
            txtCategory =  view.findViewById(R.id.txtCategory);
            txtStatus =  view.findViewById(R.id.txtStatus);
            txtLike2 =  view.findViewById(R.id.txtLike2);
            txtCommentsCounts =  view.findViewById(R.id.txtCommentsCounts);
            imgProfile = view.findViewById(R.id.imgProfile);
            imgProfile.setVisibility(View.GONE);
            singleOption = view.findViewById(R.id.singleOption);
            unLikeDislike  = view.findViewById(R.id.unLikeDislike);
            txtParticcipation = view.findViewById(R.id.txtParticcipation);
            txtPostedOn = view.findViewById(R.id.txtPostedOn);
            imgTime = view.findViewById(R.id.imgTime);
            imgTime.setVisibility(View.GONE);

        }
    }
}

