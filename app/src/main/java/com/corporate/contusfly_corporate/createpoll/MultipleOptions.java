package com.corporate.contusfly_corporate.createpoll;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.corporate.contus_Corporate.activity.CustomDialogAdapter;
import com.corporate.contus_Corporate.app.Constants;
import com.corporate.contus_Corporate.chatlib.listeners.OnTaskCompleted;
import com.corporate.contus_Corporate.responsemodel.CategoryPollResponseModel;
import com.corporate.contus_Corporate.responsemodel.CreatePollResponseModel;
import com.corporate.contus_Corporate.restclient.CategoryRestClient;
import com.corporate.contus_Corporate.restclient.CreatePollRestClient;
import com.corporate.contusfly_corporate.MApplication;
import com.corporate.contusfly_corporate.chooseContactsDb.DatabaseHelper;
import com.corporate.contusfly_corporate.smoothprogressbar.SmoothProgressBar;
import com.corporate.contusfly_corporate.utils.ImageUploadS3;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.corporate.polls_corporate.polls.R;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by user on 7/15/2015.
 */
public class MultipleOptions extends Activity implements OnTaskCompleted {

    private static Activity mMultipleOptions;
    private EditText txtCategory;
    private SmoothProgressBar googleNow;
    private static List<CategoryPollResponseModel.Results> dataResults;
    private Uri mImageCaptureUri;
    private File filepath;
    private Uri fileUri;
    private ImageView imageChoose;
    private ImageView imageAdditional;
    private String userId;
    private ArrayList<String> contacts;
    private File question1 = null;
    private File question2 = null;
    private EditText editQuestion;
    private EditText editAnswer1;
    private EditText editAnswer2;
    private EditText editAnswer3;
    private EditText editAnswer4;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String category;
    private String question;
    private Dialog listDialog;
    private Uri imageFileUri;
    private String choosenPhoto;
    private ArrayList<String> listGroupid;
    private ArrayList<String> mArrayList;
    private ArrayList<String> mCategory;
    private ArrayList<String> mGroupName;
    private ArrayList<String> mContactName;
    private TextView txtTitle;
    private TextView txtPublic;
    private TextView txtGroup;
    private TextView txtContacts;
    private ArrayList<String> listContactId;
    private String mGroupId;
    private String mContactsId;
    private String categoryId;
    private String mContact;
    private Uri uri;
    private Uri uri1;
    private String contactName;
    ImageUploadS3 imgTask;
    String image_choose_url,image_addition_url;
    boolean isimagechoose=true;
    private File choose_file_path,additinal_file_path;
    String imageType;
    private Button create;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private final int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fresco initialization
        Fresco.initialize(this);
        setContentView(R.layout.activity_options_poll);
        init();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
        CategoryPollRequest(googleNow, mMultipleOptions, txtCategory);


        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataResults.size()>1)
                {
                    //MApplication.materialdesignDialogStart(mYesOrNo);
                    MApplication.customDialogList(mMultipleOptions, txtCategory, dataResults);
                }
            }
        });

    }

    private void init() {
        /**Initializing the activity**/
        mMultipleOptions = this;
        txtCategory = (EditText) findViewById(R.id.txtCategory);
        editQuestion = (EditText) findViewById(R.id.editQuestion);
        userId = MApplication.getString(mMultipleOptions, Constants.USER_ID);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        googleNow = (SmoothProgressBar) findViewById(R.id.google_now);
        imageChoose = (ImageView) findViewById(R.id.choose);
        imageAdditional = (ImageView) findViewById(R.id.ChooseAdditional);
        editAnswer1 = (EditText) findViewById(R.id.editAnswer1);
        editAnswer2 = (EditText) findViewById(R.id.editAnswer2);
        editAnswer3 = (EditText) findViewById(R.id.editAnswer3);
        editAnswer4 = (EditText) findViewById(R.id.editAnswer4);
        txtTitle = (TextView) findViewById(R.id.title);
        txtPublic = (TextView) findViewById(R.id.txtPublic);
        txtGroup = (TextView) findViewById(R.id.txtGroup);
        txtContacts = (TextView) findViewById(R.id.txtContacts);
        create       =(Button)findViewById(R.id.create);
        mCategory = new ArrayList<String>();
        listGroupid = new ArrayList<>();
        mArrayList = new ArrayList<>();
        mContactName = new ArrayList<>();
        mGroupName = new ArrayList<>();

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Light.ttf");
        txtTitle.setTypeface(face);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.loadAd(adRequest);

        //Uploading an image in S3 bucket
        imgTask = new ImageUploadS3(getApplicationContext());
        //call back method
        imgTask.uplodingCallback(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCategory = MApplication.loadStringArray(mMultipleOptions, mCategory, com.corporate.contus_Corporate.app.Constants.ACTIVITY_CATEGORY_INFO, com.corporate.contus_Corporate.app.Constants.ACTIVITY_CATEGORY_INFO_SIZE);
        Log.e("mCategory", mCategory + "");
        mArrayList.clear();
        mContactName.clear();
        listGroupid.clear();
        mGroupName.clear();
        DatabaseHelper db = new DatabaseHelper(this);
        if (mCategory.contains("Public")) {
            txtPublic.setVisibility(View.VISIBLE);
        } else {
            txtPublic.setVisibility(View.GONE);
        }
        if (!db.getAllGroupDetails(1).isEmpty()) {
            for (int i = 0; db.getAllGroupDetails(1).size() > i; i++) {
                txtGroup.setVisibility(View.VISIBLE);
                if (!listGroupid.contains(db.getAllGroupDetails(1).get(i).getId())) {
                    mGroupName.add(db.getAllGroupDetails(1).get(i).getName());
                    listGroupid.add(db.getAllGroupDetails(1).get(i).getId());
                }
                contactName = mGroupName.toString().replaceAll("[\\s\\[\\]]", "");
            }
            contactName = mGroupName.toString().replaceAll("[\\s\\[\\]]", "");
            txtGroup.setText(contactName);
            mGroupId = listGroupid.toString().replaceAll("[\\s\\[\\]]", "");
        } else {
            txtGroup.setVisibility(View.GONE);
        }

        if (!db.getAllContactsDetails(1).isEmpty()) {
            for (int i = 0; db.getAllContactsDetails(1).size() > i; i++) {
                txtContacts.setVisibility(View.VISIBLE);
                if (!mArrayList.contains(db.getAllContactsDetails(1).get(i).getId())) {
                    mContactName.add(db.getAllContactsDetails(1).get(i).getName());
                    mArrayList.add(db.getAllContactsDetails(1).get(i).getId());
                }
            }
            contactName = mContactName.toString().replaceAll("[\\s\\[\\]]", "");
            txtContacts.setText(contactName);
            mContactsId = mArrayList.toString().replaceAll("[\\s\\[\\]]", "");
        } else {
            txtContacts.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    public void onClick(final View clickedView) {
        switch (clickedView.getId()) {
            case R.id.imagBackArrow: {
                finish();
            }
            break;
            case R.id.relativeLayout: {
                /*Intent a = new Intent(MultipleOptions.this, CreatePollOptions.class);
                startActivity(a);*/
            }
            break;
            case R.id.done: {
                this.finish();
            }
            break;
            case R.id.create: {



                category = txtCategory.getText().toString().trim();
                question = editQuestion.getText().toString().trim();
                answer1 = editAnswer1.getText().toString().trim();
                answer2 = editAnswer2.getText().toString().trim();
                answer3 = editAnswer3.getText().toString().trim();
                answer4 = editAnswer4.getText().toString().trim();
                if (TextUtils.isEmpty(category)) {
                    Toast.makeText(mMultipleOptions, getResources().getString(R.string.select_your_category),
                            Toast.LENGTH_SHORT).show();
                    txtCategory.requestFocus();
                } else if (TextUtils.isEmpty(question)) {
                    Toast.makeText(mMultipleOptions, getResources().getString(R.string.enter_enter_question),
                            Toast.LENGTH_SHORT).show();
                    editQuestion.requestFocus();
                } else if (question.length() < 2) {
                    Toast.makeText(mMultipleOptions, getResources().getString(R.string.enter_valid_question),
                            Toast.LENGTH_SHORT).show();
                    editQuestion.requestFocus();
                } else if (TextUtils.isEmpty(answer1)) {
                    Toast.makeText(mMultipleOptions, getResources().getString(R.string.option1),
                            Toast.LENGTH_SHORT).show();
                    editAnswer1.requestFocus();
                } else if (answer1.length() < 2) {
                    Toast.makeText(mMultipleOptions, getResources().getString(R.string.valid_option1),
                            Toast.LENGTH_SHORT).show();
                    editAnswer1.requestFocus();
                } else if (TextUtils.isEmpty(answer2)) {
                    Toast.makeText(mMultipleOptions, getResources().getString(R.string.option2),
                            Toast.LENGTH_SHORT).show();
                    editAnswer2.requestFocus();
                } else if (answer2.length() < 2) {
                    Toast.makeText(mMultipleOptions, getResources().getString(R.string.valid_option2),
                            Toast.LENGTH_SHORT).show();
                    editAnswer2.requestFocus();
                } else {
                    //Creates a builder for an alert dialog that uses the default alert dialog theme.
                    AlertDialog.Builder builder;
                    //Dialog text visible By NIKITA
                    if (Build.VERSION.SDK_INT >= 21)
                        builder = new AlertDialog.Builder(MultipleOptions.this, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
                    else
                        builder = new AlertDialog.Builder(MultipleOptions.this);


                    //set message
                    builder.setMessage("Please validate your poll for appropriate grammar, spellings and content before submitting it. Otherwise it will be rejected.");
                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //dialog
                                    dialog.dismiss();
                                }
                            });

                    builder.setPositiveButton("Create",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    create.setEnabled(false);
                                    googleNow.setVisibility(View.VISIBLE);
                                    googleNow.progressiveStart();
                                    imgTask.executeUpload(choose_file_path, "image", "","POLLS/");
                                    //deleteThePoll(mClickPosition);//This method is used to delete the poll

                                    dialog.dismiss();
                                }
                            });
                    //create
                    builder.create().show();
                }
            }
            break;
          /*  case R.id.txtCategory: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    YesOrNo.CategoryPollRequest(googleNow, mMultipleOptions, txtCategory);
                }
            }
            break;*/

            case R.id.choose: {
                MApplication.chooseFlag = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(!hasPermissions(MultipleOptions.this, PERMISSIONS)){
                        ActivityCompat.requestPermissions(MultipleOptions.this, PERMISSIONS, PERMISSION_ALL);
                        // Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        choosePic();
                    }
                }
                else
                {
                    choosePic();
                }
            }
            break;
            case R.id.ChooseAdditional: {
                MApplication.chooseFlag = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(!hasPermissions(MultipleOptions.this, PERMISSIONS)){
                        ActivityCompat.requestPermissions(MultipleOptions.this, PERMISSIONS, PERMISSION_ALL);
                        //  Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        choosePic();
                    }
                }
                else
                {
                    choosePic();
                }
            }
            break;
            default:
                break;
        }
    }

    public void createPollSubmit(){

        question1 = new File(MApplication.getString(mMultipleOptions, "imageMultipleOptionFilePathQuestion1"));
        question2 = new File(MApplication.getString(mMultipleOptions, "imageMultipleOptionFilePathQuestion2"));
        if (!MApplication.getString(mMultipleOptions, "imageMultipleOptionFilePathQuestion1").isEmpty() || !MApplication.getString(mMultipleOptions, "imageMultipleOptionFilePathQuestion2").isEmpty()) {
            if (!MApplication.getString(mMultipleOptions, "imageMultipleOptionFilePathQuestion2").isEmpty() && MApplication.getString(mMultipleOptions, "imageMultipleOptionFilePathQuestion1").isEmpty()) {
                createMultipleOptionsQuestion2();

                // imageType="2";
                // uploadMethode();
            } else if (!MApplication.getString(mMultipleOptions, "imageMultipleOptionFilePathQuestion1").isEmpty() && MApplication.getString(mMultipleOptions, "imageMultipleOptionFilePathQuestion2").isEmpty()) {
                createMultipleOptionsQuestion1();
                // imageType="1";
                // uploadMethode();

            } else {

                //imageType="all";
                // uploadMethode();

                createMultipleOptions();

            }
        } else {
            createMultipleOptionsEmpty();
        }
    }

    private void uploadMethode() {

        googleNow.setVisibility(View.VISIBLE);
        googleNow.progressiveStart();

        if(imageType.equalsIgnoreCase("2"))
        {
            imgTask.executeUpload(additinal_file_path, "image", "","POLLS/");
        }
        else if(imageType.equalsIgnoreCase("1"))
        {
            imgTask.executeUpload(choose_file_path, "image", "","POLLS/");
        }
        else if(imageType.equalsIgnoreCase("all"))
        {
            imgTask.executeUpload(choose_file_path, "image", "","POLLS/");

        }
    }

    /**
     * Sending the request and getting the response using the method
     */
    private void createMultipleOptions() {
        if (MApplication.isNetConnected(mMultipleOptions)) {
            categoryId = MApplication.getString(mMultipleOptions, Constants.CATEGORY_ID);
            mContact = mCategory.toString().replaceAll("[\\s\\[\\]]", "");
            MApplication.materialdesignDialogStart(mMultipleOptions);

            String CurrentString = image_addition_url;
            String[] separated = CurrentString.split("com/");
            CurrentString=separated[1];


            String chooseurl = image_choose_url;
            String[] separated1 = chooseurl.split("com/");
            chooseurl=separated1[1];



            /**Request and response in retrofit**/
            CreatePollRestClient.getInstance().postCreateMultiplePoll(new String("save_userpolls"), new String(userId), new String("users"), new String("2"), new String(chooseurl),  new String(CurrentString), new String(question), new String(answer1), new String(answer2), new String(answer3), new String(answer4), new String(categoryId), mContact, mGroupId, mContactsId
                    , new Callback<CreatePollResponseModel>() {

                        @Override
                        public void success(CreatePollResponseModel createResponseModel, Response response) {
                            if (createResponseModel.getSuccess().equals("1")) {
                                finish();
                            }
                            Toast.makeText(mMultipleOptions, createResponseModel.getMsg(),
                                    Toast.LENGTH_SHORT).show();
                            MApplication.materialdesignDialogStop();
                        }


                        @Override
                        public void failure(RetrofitError retrofitError) {
                            MApplication.materialdesignDialogStop();
                            /**Retrofit error description**/
                            String errorDescription = retrofitError.getMessage();

                            switch (retrofitError.getKind()) {
                                case HTTP:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.http_error);
                                    break;
                                case NETWORK:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_connecting_error);
                                    break;
                                case CONVERSION:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_passing_data);
                                    break;
                                case UNEXPECTED:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_unexpected);
                                    break;
                                default:
                                    break;
                            }
                            Toast.makeText(mMultipleOptions, errorDescription,
                                    Toast.LENGTH_SHORT).show();


                        }

                    });
        } else {
            Toast.makeText(mMultipleOptions, mMultipleOptions.getResources().getString(R.string.check_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sending the request and getting the response using the method
     */
    private void createMultipleOptionsEmpty() {
        if (MApplication.isNetConnected(mMultipleOptions)) {
            categoryId = MApplication.getString(mMultipleOptions, Constants.CATEGORY_ID);

            mContact = mCategory.toString().replaceAll("[\\s\\[\\]]", "");
            MApplication.materialdesignDialogStart(mMultipleOptions);
            /**Request and response in retrofit**/
            CreatePollRestClient.getInstance().postCreateMultiplePollEmpty(new String("save_userpolls"), new String(userId), new String("users"), new String("2"),new String(""),  new String(""), new String(question), new String(answer1), new String(answer2), new String(answer3), new String(answer4), new String(categoryId), mContact, mGroupId, mContactsId
                    , new Callback<CreatePollResponseModel>() {

                        @Override
                        public void success(CreatePollResponseModel createResponseModel, Response response) {
                            if (createResponseModel.getSuccess().equals("1")) {
                                finish();
                            }
                            Toast.makeText(mMultipleOptions, createResponseModel.getMsg(),
                                    Toast.LENGTH_SHORT).show();
                            MApplication.materialdesignDialogStop();
                        }


                        @Override
                        public void failure(RetrofitError retrofitError) {
                            MApplication.materialdesignDialogStop();
                            /**Retrofit error description**/
                            String errorDescription = retrofitError.getMessage();

                            switch (retrofitError.getKind()) {
                                case HTTP:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.http_error);
                                    break;
                                case NETWORK:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_connecting_error);
                                    break;
                                case CONVERSION:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_passing_data);
                                    break;
                                case UNEXPECTED:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_unexpected);
                                    break;
                                default:
                                    break;
                            }
                            Toast.makeText(mMultipleOptions, errorDescription,
                                    Toast.LENGTH_SHORT).show();


                        }

                    });
        } else {
            Toast.makeText(mMultipleOptions, mMultipleOptions.getResources().getString(R.string.check_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sending the request and getting the response using the method
     */
    private void createMultipleOptionsQuestion2() {
        if (MApplication.isNetConnected(mMultipleOptions)) {
            categoryId = MApplication.getString(mMultipleOptions, Constants.CATEGORY_ID);
            mContact = mCategory.toString().replaceAll("[\\s\\[\\]]", "");
            MApplication.materialdesignDialogStart(mMultipleOptions);

            String CurrentString = image_addition_url;
            String[] separated = CurrentString.split("com/");
            CurrentString=separated[1];




            /**Request and response in retrofit**/
            CreatePollRestClient.getInstance().postCreateMultiplePollQuestion2(new String("save_userpolls"), new String(userId), new String("users"), new String("2"), new String(""),  new String(CurrentString), new String(question), new String(answer1), new String(answer2), new String(answer3), new String(answer4), new String(categoryId), mContact, mGroupId, mContactsId
                    , new Callback<CreatePollResponseModel>() {

                        @Override
                        public void success(CreatePollResponseModel createResponseModel, Response response) {
                            if (createResponseModel.getSuccess().equals("1")) {
                                finish();
                            }
                            Toast.makeText(mMultipleOptions, createResponseModel.getMsg(),
                                    Toast.LENGTH_SHORT).show();
                            MApplication.materialdesignDialogStop();
                        }


                        @Override
                        public void failure(RetrofitError retrofitError) {
                            MApplication.materialdesignDialogStop();
                            /**Retrofit error description**/
                            String errorDescription = retrofitError.getMessage();

                            switch (retrofitError.getKind()) {
                                case HTTP:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.http_error);
                                    break;
                                case NETWORK:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_connecting_error);
                                    break;
                                case CONVERSION:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_passing_data);
                                    break;
                                case UNEXPECTED:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_unexpected);
                                    break;
                                default:
                                    break;
                            }
                            Toast.makeText(mMultipleOptions, errorDescription,
                                    Toast.LENGTH_SHORT).show();


                        }

                    });
        } else {
            Toast.makeText(mMultipleOptions, mMultipleOptions.getResources().getString(R.string.check_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sending the request and getting the response using the method
     */
    private void createMultipleOptionsQuestion1() {
        if (MApplication.isNetConnected(mMultipleOptions)) {
            categoryId = MApplication.getString(mMultipleOptions, Constants.CATEGORY_ID);

            mContact = mCategory.toString().replaceAll("[\\s\\[\\]]", "");
            MApplication.materialdesignDialogStart(mMultipleOptions);




            String chooseurl = image_choose_url;
            String[] separated1 = chooseurl.split("com/");
            chooseurl=separated1[1];

            /**Request and response in retrofit**/
            CreatePollRestClient.getInstance().postCreateMultiplePollQuestion1(new String("save_userpolls"), new String(userId), new String("users"), new String("2"), new String(chooseurl),  new String(""), new String(question), new String(answer1), new String(answer2), new String(answer3), new String(answer4), new String(categoryId), mContact, mGroupId, mContactsId
                    , new Callback<CreatePollResponseModel>() {

                        @Override
                        public void success(CreatePollResponseModel createResponseModel, Response response) {
                            if (createResponseModel.getSuccess().equals("1")) {
                                finish();
                            }
                            Toast.makeText(mMultipleOptions, createResponseModel.getMsg(),
                                    Toast.LENGTH_SHORT).show();
                            /**Progressive bar stop**/
                            googleNow.progressiveStop();
                            /**Visiblity gone**/
                            googleNow.setVisibility(View.GONE);
                        }


                        @Override
                        public void failure(RetrofitError retrofitError) {
                            MApplication.materialdesignDialogStop();
                            /**Retrofit error description**/
                            String errorDescription = retrofitError.getMessage();

                            switch (retrofitError.getKind()) {
                                case HTTP:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.http_error);
                                    break;
                                case NETWORK:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_connecting_error);
                                    break;
                                case CONVERSION:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_passing_data);
                                    break;
                                case UNEXPECTED:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_unexpected);
                                    break;
                                default:
                                    break;
                            }
                            Toast.makeText(mMultipleOptions, errorDescription,
                                    Toast.LENGTH_SHORT).show();


                        }

                    });
        } else {
            Toast.makeText(mMultipleOptions, mMultipleOptions.getResources().getString(R.string.check_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 15:
                if (resultCode == RESULT_OK) {
                    mImageCaptureUri = data.getData();
                    filepath = new File(getRealPathFromURI(mMultipleOptions, mImageCaptureUri));

                    //image Compression
                    MApplication.Imagecompression(filepath);

                    if (MApplication.chooseFlag) {
                        uri = MApplication.decodeFileConvertUri(mMultipleOptions, filepath);
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionQuestion1", uri.toString());
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionFilePathQuestion1", filepath.toString());
                        if (!MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion2").isEmpty()) {
                            imageAdditional.setImageURI(Uri.parse(MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion2")));
                        }
                        imageChoose.setImageURI(uri);
                        question1 = filepath;
                        choose_file_path=filepath;
                        // imgTask.executeUpload(filepath, "image", "","POLLS/");
                    } else {
                        uri1 = MApplication.decodeFileConvertUri(mMultipleOptions, filepath);
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionQuestion2", uri1.toString());
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionFilePathQuestion2", filepath.toString());
                        if (!MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion1").isEmpty()) {
                            imageChoose.setImageURI(Uri.parse(MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion1")));
                        }
                        imageAdditional.setImageURI(uri1);
                        question2 = filepath;
                        //imgTask.executeUpload(filepath, "image", "","POLLS/");
                        additinal_file_path=filepath;
                    }

                }
                break;
            case 10:
                if (resultCode == RESULT_OK) {
                    try {
                        mImageCaptureUri = data.getData();
                        filepath = new File(MApplication.getPath(mMultipleOptions, mImageCaptureUri));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //image Compression
                    MApplication.Imagecompression(filepath);

                    /*Intent newIntent1 = new AviaryIntent.Builder(this)
                            .setData(mImageCaptureUri) // input image src
                            .withOutput(Uri.parse("file://" + filepath)) // output file
                            .withOutputFormat(Bitmap.CompressFormat.JPEG) // output format
                            .withOutputSize(MegaPixels.Mp5) // output size
                            .withOutputQuality(90) // output quality
                            .build();
                    // start the activity
                    startActivityForResult(newIntent1, 15);*/

                    if (MApplication.chooseFlag) {
                        uri = MApplication.decodeFileConvertUri(mMultipleOptions, filepath);
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionQuestion1", uri.toString());
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionFilePathQuestion1", filepath.toString());
                        if (!MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion2").isEmpty()) {
                            imageAdditional.setImageURI(Uri.parse(MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion2")));
                        }
                        imageChoose.setImageURI(uri);
                        question1 = filepath;
                        choose_file_path=filepath;
                        // imgTask.executeUpload(filepath, "image", "","POLLS/");
                    } else {
                        uri1 = MApplication.decodeFileConvertUri(mMultipleOptions, filepath);
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionQuestion2", uri1.toString());
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionFilePathQuestion2", filepath.toString());
                        if (!MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion1").isEmpty()) {
                            imageChoose.setImageURI(Uri.parse(MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion1")));
                        }
                        imageAdditional.setImageURI(uri1);
                        question2 = filepath;
                        //imgTask.executeUpload(filepath, "image", "","POLLS/");
                        additinal_file_path=filepath;
                    }
                }
                break;


            case 11:
                if (resultCode == Activity.RESULT_OK) {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;


                    filepath = new File(getRealPathFromURI(mMultipleOptions, imageFileUri));
                    /*Intent newIntent1 = new AviaryIntent.Builder(this)
                            .setData(imageFileUri) // input image src
                            .withOutput(Uri.parse(Constants.pictureFile + filepath)) // output file
                            .withOutputFormat(Bitmap.CompressFormat.JPEG) // output format
                            .withOutputSize(MegaPixels.Mp5) // output size
                            .withOutputQuality(90) // output quality
                            .build();
                    // start the activity
                    startActivityForResult(newIntent1, 15);*/

                    //image Compression
                    MApplication.Imagecompression(filepath);

                    if (MApplication.chooseFlag) {
                        uri = MApplication.decodeFileConvertUri(mMultipleOptions, filepath);
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionQuestion1", uri.toString());
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionFilePathQuestion1", filepath.toString());
                        if (!MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion2").isEmpty()) {
                            imageAdditional.setImageURI(Uri.parse(MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion2")));
                        }
                        imageChoose.setImageURI(uri);
                        question1 = filepath;
                        choose_file_path=filepath;
                        // imgTask.executeUpload(filepath, "image", "","POLLS/");
                    } else {
                        uri1 = MApplication.decodeFileConvertUri(mMultipleOptions, filepath);
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionQuestion2", uri1.toString());
                        MApplication.setString(mMultipleOptions, "imageMultipleOptionFilePathQuestion2", filepath.toString());
                        if (!MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion1").isEmpty()) {
                            imageChoose.setImageURI(Uri.parse(MApplication.getString(mMultipleOptions, "imageMultipleOptionQuestion1")));
                        }
                        imageAdditional.setImageURI(uri1);
                        question2 = filepath;
                        //imgTask.executeUpload(filepath, "image", "","POLLS/");
                        additinal_file_path=filepath;
                    }
                }
                break;
        }


    }

    private void choosePic() {
        listDialog = new Dialog(mMultipleOptions);
        listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        listDialog.setContentView(R.layout.custom_dialog_adapter);
        ListView list = (ListView) listDialog.findViewById(R.id.component_list);
        String[] cameraOptions = new String[]{"Take Photo", "Choose from gallery", "Cancel"};
        CustomDialogAdapter adapter = new CustomDialogAdapter(this, cameraOptions);
        list.setAdapter(adapter);
        listDialog.show();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        takePictureIntent();
                        break;
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,
                                "Complete ACTION using"), 10);
                        listDialog.cancel();
                        break;
                    case 2:
                        listDialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public static String getRealPathFromURI(Activity activity, Uri uri) {
        Log.e("uri", uri + "");
        String result;
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {

            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    /**
     * Take picture intent.
     *
     * @param
     */
    public void takePictureIntent() {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File folder = new File(Environment.getExternalStorageDirectory() + "/"
                + getResources().getString(R.string.app_name));

        if (!folder.exists()) {
            folder.mkdir();
        }
        final Calendar c = Calendar.getInstance();
        String new_Date = c.get(Calendar.DAY_OF_MONTH) + "-"
                + ((c.get(Calendar.MONTH)) + 1) + "-" + c.get(Calendar.YEAR)
                + " " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE)
                + "-" + c.get(Calendar.SECOND);
        choosenPhoto = String.format(
                Environment.getExternalStorageDirectory() + "/"
                        + getResources().getString(R.string.app_name)
                        + "/%s.png", "profilepic(" + new_Date + ")");
        Log.e("choosenPhoto", choosenPhoto + "");
        // "vehicle_Info(" + new_Date + ")"
        File photo = new File(choosenPhoto);
        imageFileUri = Uri.fromFile(photo);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent,
                11);
        listDialog.cancel();

    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", imageFileUri);
    }

  /*  @Override
    protected void onDestroy() {
        super.onDestroy();
        MApplication.materialdesignDialogStop();
    }*/

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageFileUri = savedInstanceState.getParcelable("file_uri");
    }




    //on completion of imnage upload
    @Override
    public void onTaskCompleted(URL url, String type, String encodedImg, int fileSize) {




        if (isimagechoose) {
            isimagechoose = false;
            image_choose_url = url.toString();
            Log.v("image_choose_url", url.toString());
            imgTask.executeUpload(additinal_file_path, "image", "", "POLLS/");
        } else {
            image_addition_url = url.toString();
            Log.v("image_addition_url", url.toString());

            googleNow.setVisibility(View.GONE);
            googleNow.progressiveStop();
            createPollSubmit();

           /* category = txtCategory.getText().toString().trim();
            question = editQuestion.getText().toString().trim();
            answer1 = editAnswer1.getText().toString().trim();
            answer2 = editAnswer2.getText().toString().trim();
            answer3 = editAnswer3.getText().toString().trim();
            answer4 = editAnswer4.getText().toString().trim();
            if (TextUtils.isEmpty(category)) {
                Toast.makeText(mMultipleOptions, getResources().getString(R.string.select_your_category),
                        Toast.LENGTH_SHORT).show();
                txtCategory.requestFocus();
            } else if (TextUtils.isEmpty(question)) {
                Toast.makeText(mMultipleOptions, getResources().getString(R.string.enter_enter_question),
                        Toast.LENGTH_SHORT).show();
                editQuestion.requestFocus();
            } else if (question.length() < 2) {
                Toast.makeText(mMultipleOptions, getResources().getString(R.string.enter_valid_question),
                        Toast.LENGTH_SHORT).show();
                editQuestion.requestFocus();
            } else if (TextUtils.isEmpty(answer1)) {
                Toast.makeText(mMultipleOptions, getResources().getString(R.string.option1),
                        Toast.LENGTH_SHORT).show();
                editAnswer1.requestFocus();
            } else if (answer1.length() < 2) {
                Toast.makeText(mMultipleOptions, getResources().getString(R.string.valid_option1),
                        Toast.LENGTH_SHORT).show();
                editAnswer1.requestFocus();
            } else if (TextUtils.isEmpty(answer2)) {
                Toast.makeText(mMultipleOptions, getResources().getString(R.string.option2),
                        Toast.LENGTH_SHORT).show();
                editAnswer2.requestFocus();
            } else if (answer2.length() < 2) {
                Toast.makeText(mMultipleOptions, getResources().getString(R.string.valid_option2),
                        Toast.LENGTH_SHORT).show();
                editAnswer2.requestFocus();
            } else {
                //Creates a builder for an alert dialog that uses the default alert dialog theme.
                AlertDialog.Builder builder = new AlertDialog.Builder(MultipleOptions.this);
                //set message
                builder.setMessage("Please validate your poll for appropriate grammar, spellings and content before submitting it. Otherwise it will be rejected.");
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog
                                dialog.dismiss();
                            }
                        });

                builder.setPositiveButton("Create",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //deleteThePoll(mClickPosition);//This method is used to delete the poll
                                createPollSubmit();
                                dialog.dismiss();
                            }
                        });
                //create
                builder.create().show();
            }

        }*/

        }
    }





       /* googleNow.setVisibility(View.GONE);
        googleNow.progressiveStop();

        if(isimagechoose)
        {
            isimagechoose=false;
            image_choose_url=url.toString();
            Log.v("image_choose_url",url.toString());

            if(imageType.equalsIgnoreCase("all"))
            {
                imgTask.executeUpload(additinal_file_path, "image", "","POLLS/");
               // createMultipleOptions();
            }
        }
        if(!isimagechoose)
        {
            image_addition_url=url.toString();
            Log.v("image_addition_url",url.toString());
        }
        if(imageType.equalsIgnoreCase("2"))
        {
            createMultipleOptionsQuestion2();
        }
        else if(imageType.equalsIgnoreCase("1"))
        {
            createMultipleOptionsQuestion1();
        }
        else if(imageType.equalsIgnoreCase("all")&&image_addition_url!=null&&image_addition_url.length()>0&&image_choose_url!=null&&image_choose_url.length()>0)
        {
            createMultipleOptions();
        }


    }*/

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                else
                    return true;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("", "Permission callback called-------");
        switch (requestCode) {
            case PERMISSION_ALL: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("", "Storage services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        // Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                        choosePic();

                    } else {
                        Log.d("", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK("Please provide required permissions!", "Information",
                                    new DialogInterface.OnClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.M)
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    hasPermissions(MultipleOptions.this, PERMISSIONS);
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Please enable permissions from settings", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
            }
        }

    }
    private void showDialogOK(String message,String title, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= 21)
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        else
            builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Information");
        builder.setMessage("Please provide required permissions!");
        builder.setPositiveButton("OK", okListener);
        builder.setNegativeButton("Cancel", okListener);
        builder.create().show();

    }
    public static void CategoryPollRequest(final SmoothProgressBar googleNow, final Activity activity, final TextView txtCategory) {
        if (MApplication.isNetConnected(activity)) {
            String userId = MApplication.getString(activity, Constants.USER_ID);
            /* Requesting the response from the given base url**/
            CategoryRestClient.getInstance().postCategoryData("categoryapi_list", userId
                    , new Callback<CategoryPollResponseModel>() {
                        @Override
                        public void success(CategoryPollResponseModel welcomeResponseModel, Response response) {

                            googleNow.setVisibility(View.GONE);
                            googleNow.progressiveStart();

                            dataResults = welcomeResponseModel.getResults();
                            if (dataResults.size() != 0) {
                                MApplication.setBoolean(activity, Constants.YES_OR_NO, true);

                                if(dataResults.size()==1)
                                {
                                    MApplication.setString(activity, com.corporate.contus_Corporate.app.Constants.CATEGORY_ID, dataResults.get(0).getCategoryId());
                                    //setting the category name
                                    txtCategory.setText(dataResults.get(0).getCategoryName());
                                    txtCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    txtCategory.setEnabled(false);
                                }
                                else {
                                    txtCategory.setEnabled(true);
                                    txtCategory.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.arrow_down, 0);
                                    // MApplication.customDialogList(activity, txtCategory, dataResults);
                                }
                            }
                            googleNow.setVisibility(View.GONE);
                            googleNow.progressiveStop();
                            MApplication.materialdesignDialogStop();

                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            String errorDescription = retrofitError.getMessage();
                            switch (retrofitError.getKind()) {
                                case HTTP:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.http_error);
                                    break;
                                case NETWORK:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_connecting_error);
                                    break;
                                case CONVERSION:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_passing_data);
                                    break;
                                case UNEXPECTED:
                                    errorDescription = mMultipleOptions.getResources().getString(R.string.error_unexpected);
                                    break;
                                default:
                                    break;
                            }
                            Toast.makeText(mMultipleOptions, errorDescription,
                                    Toast.LENGTH_SHORT).show();
                            MApplication.materialdesignDialogStop();

                        }

                    });
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.check_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }

    }




}
