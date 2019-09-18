package com.coolopool.coolopool.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.coolopool.coolopool.Backend.Authentication;
import com.coolopool.coolopool.Backend.Model.User;
import com.coolopool.coolopool.R;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roger.catloadinglibrary.CatLoadingView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp2Activity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_LOAD_IMAGE = 1;
    Button mCreateButton;
    TextView mLoginButton;
    CircleImageView mUserProfilePic;
    private String username, password, name, phoneNo, email;

    EditText etname, etphoneNo, etemail;
    NetworkInfo networkInfo;

    Authentication authentication;

    String googleName;
    String googleImageUrl;
    boolean isGoogleSignIn = false;

    CatLoadingView loadingView;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        loadingView = new CatLoadingView();
        loadingView.setText("Few more sec...");
        loadingView.setCanceledOnTouchOutside(false);

        authentication = new Authentication(this, SignUp2Activity.this);

        etname =  findViewById(R.id.Name);
        etphoneNo =  findViewById(R.id.phoneNo);
        etemail =  findViewById(R.id.email);

        mUserProfilePic = findViewById(R.id.userProfilePic);
        mCreateButton = findViewById(R.id.createAccountButton);

        mUserProfilePic.setOnClickListener(this);
        mCreateButton.setOnClickListener(this);

        getIntentData();

        ConnectivityManager connMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.userProfilePic:
                Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryintent, RESULT_LOAD_IMAGE);
                break;
            case R.id.createAccountButton:
                showLoadingView();
                if (networkInfo != null && networkInfo.isConnected()) {
                    createAccount();
                } else {
                    loadingView.dismiss();
                    Toast.makeText(SignUp2Activity.this,"No Internet Connection.",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showLoadingView(){
        loadingView.show(getSupportFragmentManager(), "");
    }

    private  void dismiss(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                loadingView.dismiss();
            }
        }, 2000);
    }

    private void createAccount() {

        name = etname.getText().toString().trim();
        phoneNo = etphoneNo.getText().toString().trim();
        email = etemail.getText().toString().trim();

        if(name.isEmpty()){
            loadingView.setText("Invalid name");
            etname.setError("Please enter a valid Name.");
            etname.requestFocus();
            dismiss();
            return;
        }

        if(phoneNo.isEmpty() || phoneNo.length() < 10){
            loadingView.setText("Invalid phone no.");
            etphoneNo.setError("Please enter a valid phone number.");
            etphoneNo.requestFocus();
            dismiss();
            return;
        }

        if(email.isEmpty()){
            loadingView.setText("Invalid email");
            etemail.setError("Please enter a valid email.");
            etemail.requestFocus();
            dismiss();
            return;
        }

        if(uri == null){
            uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.drawable.userfacepic);
        }
        User user;
        if(isGoogleSignIn){
            user = new User(username, password, name, phoneNo, email);
        }else{
            user = new User(username+"@coolopool.com", password, name, phoneNo, email);
        }


        storeImage(getContactBitmapFromURI(uri));

        authentication.signUp(user, uri, loadingView);

    }

    private void storeImage(Bitmap bitmap){
        String root = Environment.getRootDirectory().toString();
        File myDir = new File(root + "/profile_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        String fname = "image.jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  Bitmap getContactBitmapFromURI(Uri uri) {
        try {

            InputStream input = getContentResolver().openInputStream(uri);
            if (input == null) {
                return null;
            }
            return BitmapFactory.decodeStream(input);
        }
        catch (FileNotFoundException e)
        {

        }
        return null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE &&resultCode == RESULT_OK && data != null ){
            uri = data.getData();
            mUserProfilePic.setScaleType(ImageButton.ScaleType.CENTER_CROP);
            mUserProfilePic.setImageURI(uri);
        }
    }

    private void getGoogleIntentData(Intent intent) {

        googleName = intent.getStringExtra("GoogleDisplayName");
        googleImageUrl = intent.getStringExtra("GooglePicUrl");

        etname.setText(googleName);


        //Todo: getting gmail profile picture from url to uri
        //uri = Uri.parse(Uri.decode(googleImageUrl));
        //Picasso.get().load(uri).into(mUserProfilePic);
    }

    private void getIntentData() {

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        password = intent.getStringExtra("Password");
        isGoogleSignIn = intent.getBooleanExtra("IsGoogleSignIn", false);
        if(isGoogleSignIn){
            getGoogleIntentData(intent);
        }
    }

}
