package com.corporate.new_chanages_corporate.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.corporate.contus_Corporate.activity.CustomRequest;
import com.corporate.contus_Corporate.activity.VolleyResponseListener;
import com.corporate.contus_Corporate.app.Constants;
import com.corporate.contusfly_corporate.MApplication;
import com.corporate.contusfly_corporate.model.AnnouncementReadObject;
import com.corporate.contusfly_corporate.smoothprogressbar.SmoothProgressBar;
import com.google.android.gms.ads.AdView;
import com.corporate.new_chanages_corporate.adapters.AnnouncementRecyclerAdapter;
import com.corporate.new_chanages_corporate.models.AnnouncementModel;
import com.corporate.polls_corporate.polls.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Announcements extends Fragment implements AnnouncementRecyclerAdapter.EventListener{
    //View
    private View parentView;
    //Iamge view chat
    private ImageView imgChat;

    private SmoothProgressBar googleNow;

    //Google ad
    private AdView mAdView;
    private Activity contectFragment;
    RecyclerView announcement_list;
    private SwipeRefreshLayout pull_to_refresh;
    LinearLayout ll_no_announcement;
    String userId;
    ArrayList<AnnouncementModel> annonceList;
    ArrayList<AnnouncementReadObject>   list;

    int NextPageNumber,PageNumber,RowsPerPage,TotalCount;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    AnnouncementRecyclerAdapter linearListAdapter;
    int pageno=1;
    private boolean loading = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.announcement_layout, container, false);
        mAdView =  parentView.findViewById(R.id.adView);
        googleNow =  parentView.findViewById(R.id.google_now);
        pull_to_refresh = parentView.findViewById(R.id.pull_to_refresh);
        ll_no_announcement = parentView.findViewById(R.id.ll_no_announcement);
        announcement_list   = parentView.findViewById(R.id.announcement_list);
        userId = MApplication.getString(getActivity(), Constants.USER_ID);

        LinearLayout userpoll =  getActivity().findViewById(R.id.publicpoll);
        userpoll.setVisibility(View.VISIBLE);

        ImageView pollsearch_iv=getActivity().findViewById(R.id.imgSearch);
        ImageView imgEdit=getActivity().findViewById(R.id.imgEdit);

        pollsearch_iv.setVisibility(View.INVISIBLE);
        imgEdit.setVisibility(View.INVISIBLE);

        //getAnnouncement_list(false);

        pull_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageno=1;
                loading = true;
                getAnnouncement_list(false);
            }
        });



        announcement_list.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            //progressBar.setVisibility(View.VISIBLE);

                            //mAuthTask = new UserLoginTask("", "");
                            // mAuthTask.execute((Void) null);
                            pageno= pageno+1;

                            getAnnouncement_list(true);
                        }
                    }
                }
            }
        });

        //Returning the view
        return parentView;
    }


   public void getAnnouncement_list(final boolean more)
   {
       EditText search_et= getActivity().findViewById(R.id.search_et);
       JSONObject obj = new JSONObject();
       try {
           obj.put("action","getAnnouncements");
           obj.put("user_id",userId);
           obj.put("page",pageno);
           obj.put("search_key",search_et.getText().toString());
           Log.v("...", obj.toString());
       }
       catch (Exception ae)
       {

       }
       googleNow.setVisibility(View.VISIBLE);
       googleNow.progressiveStart();

       CustomRequest.makeJsonObjectRequest(getActivity(), Constants.GET_ANNOUNCEMENT,obj, new VolleyResponseListener() {
           @Override
           public void onError(String message) {
//            dialog.dismiss();
               Log.i("onErrormessage", "message= " +message);
           }

           @Override
           public void onResponse(JSONObject response) {
               JSONObject result = response.optJSONObject("results");
               googleNow.progressiveStop();
               googleNow.setVisibility(View.GONE);
               //stopping swipe refresh
               pull_to_refresh.setRefreshing(false);
               try {
                   //Log.i("PCCmessage", "message " + result.getString("msg"));
                   int success=response.optInt("success");

                   if(success==1)
                   {
                       announcement_list.setVisibility(View.VISIBLE);
                       ll_no_announcement.setVisibility(View.GONE);
                       TotalCount =result.optInt("total");
                       int per_page =result.optInt("per_page");
                       int current_page =result.optInt("current_page");
                       int last_page =result.optInt("last_page");
                       int from =result.optInt("from");
                       int to =result.optInt("to");

                       JSONArray data =result.optJSONArray("data");

                       if(!more) {
                           annonceList = new ArrayList<>();
                       }
                       for(int i=0;i<data.length();i++)
                       {
                          JSONObject obj =data.getJSONObject(i);
                          String adminCount= obj.optString("adminCount");
                           String announcement= obj.optString("announcement");
                           String announcementCommentsCounts= obj.optString("announcementCommentsCounts");
                           String announcementLikesCounts= obj.optString("announcementLikesCounts");
                           String announcementParticipateCount= obj.optString("announcementParticipateCount");
                           String created_at= obj.optString("created_at");
                           String id= obj.optString("id");
                           String image= obj.optString("image");
                           String is_delete= obj.optString("is_delete");
                           String status= obj.optString("status");
                           String title= obj.optString("title");
                           String updated_at= obj.optString("updated_at");
                           String userAnnouncementLikes= obj.optString("userAnnouncementLikes");
                           String userCount= obj.optString("userCount");
                           JSONArray jsonArray = new JSONArray();
                           list = new ArrayList<>();
                           if(obj.opt("user_participate_announcements")!=null)
                           {
                               jsonArray = obj.optJSONArray("user_participate_announcements");
                               for (int a=0; a<jsonArray.length(); a++)
                               {
                                   AnnouncementReadObject readObject = new AnnouncementReadObject();
                                   JSONObject jsonObject = jsonArray.optJSONObject(a);
                                   readObject.setId(jsonObject.optInt("id"));
                                   readObject.setAnnouncementId(jsonObject.optInt("announcement_id"));
                                   readObject.setIsRead(jsonObject.getInt("is_read"));
                                   readObject.setUserId(jsonObject.getInt("user_id"));
                                   readObject.setStatus(jsonObject.getInt("status"));
                                   readObject.setIsDelete(jsonObject.getInt("is_delete"));
                                   list.add(readObject);
                               }
                           }
                           JSONObject announcementcategory =obj.optJSONObject("announcementcategory");
                           String category_name="";
                           if(announcementcategory!=null)
                           {
                               JSONObject category =announcementcategory.optJSONObject("category");
                                category_name =category.optString("category_name");
                           }
                           JSONObject user_infos =obj.optJSONObject("user_infos");
                           String adminProfilePic="";
                           if(user_infos!=null)
                           {
                               adminProfilePic =user_infos.optString("image");
                           }

                           AnnouncementModel obi=new AnnouncementModel();
                           obi.setAdminCount(adminCount);
                           obi.setAnnouncement(announcement);
                           obi.setAnnouncementCommentsCounts(announcementCommentsCounts);
                           obi.setAnnouncementLikesCounts(announcementLikesCounts);
                           obi.setAnnouncementParticipateCount(announcementLikesCounts);
                           obi.setAnnouncementParticipateCount(announcementParticipateCount);
                           obi.setCreated_at(created_at);
                           obi.setId(id);
                           obi.setImage(image);
                           obi.setIs_delete(is_delete);
                           obi.setStatus(status);
                           obi.setTitle(title);
                           obi.setUpdated_at(updated_at);
                           obi.setUserCount(userCount);
                           obi.setCategory_name(category_name);
                           obi.setUserAnnouncementLikes(userAnnouncementLikes);
                           obi.setAdminProfilePic(adminProfilePic);
                           if(list.size()>0)
                           {
                               obi.setUser_participate_announcements(list);
                           }
                           annonceList.add(obi);

                       }

                       if(!more) {
                           mLayoutManager = new LinearLayoutManager(getContext());
                           announcement_list.setLayoutManager(mLayoutManager);
                           linearListAdapter=new AnnouncementRecyclerAdapter(annonceList, Announcements.this);
                           announcement_list.setAdapter(linearListAdapter);
                       }
                       else
                       {
                           linearListAdapter.notifyDataSetChanged();
                       }

                       if (more) {
                           if (announcement_list.getAdapter() != null && announcement_list.getAdapter().getItemCount() < TotalCount) {
                               loading = true;
                           } else {
                               loading = false;
                           }
                       }
                   }
                   else
                   {
                       if(pageno==1)
                       {
                           announcement_list.setVisibility(View.GONE);
                           ll_no_announcement.setVisibility(View.VISIBLE);
                       }
                       Toast.makeText(getContext(),result.getString("msg"),Toast.LENGTH_SHORT).show();
                   }


               } catch (JSONException e) {
                   e.printStackTrace();
               }


           }
       });
   }

    @Override
    public void onResume() {
        super.onResume();
            //Toast.makeText(getContext(), "Resume", Toast.LENGTH_SHORT).show();
        pageno = 1;
        getAnnouncement_list(false);
        MApplication.googleAd(mAdView);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof Activity) {
            contectFragment = (Activity) context;
        }

    }

    @Override
    public void onEvent(Boolean data) {
        annonceList = new ArrayList<>();
        pageno = 1;
        getAnnouncement_list(false);
    }
}

