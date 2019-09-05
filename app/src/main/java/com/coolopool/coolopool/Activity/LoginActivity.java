package com.coolopool.coolopool.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coolopool.coolopool.Backend.Authentication;
import com.coolopool.coolopool.R;
import com.coolopool.coolopool.Storage.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.roger.catloadinglibrary.CatLoadingView;

public class LoginActivity extends AppCompatActivity {

    Button loginbtn;
    // ImageButton mBackButton;  uncomment if back button is required.
    TextView signUpbtn;
    static String UserName;
    private static String Password;

    private EditText etusername;
    private EditText etpassword;

    Authentication authentication;

    CatLoadingView loadingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingView = new CatLoadingView();
        loadingView.setText("       Loading...");
        loadingView.setText("Few more sec...");
        loadingView.setCanceledOnTouchOutside(false);


        authentication = new Authentication(this, LoginActivity.this);


        etusername = findViewById(R.id.login_name);
        etpassword = findViewById(R.id.login_pass);


        loginbtn = findViewById(R.id.login_btn);
        signUpbtn = findViewById(R.id.SignUp_btn);

        //  uncomment if back button is required.
       /* mBackButton = findViewById(R.id.backButton);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                startActivity(intent);
            }
        });*/


        ConnectivityManager connMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingView();
                Log.d("Log_test","Login button is clicked");
                // If there is a network connection, setup Login
                if (networkInfo != null && networkInfo.isConnected()) {
                    setUpLogin();
                } else {
                    hideLoadingView();
                    Toast.makeText(LoginActivity.this,"No Internet Connection.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Log_test","Sign UP button is clicked");
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authentication.isUserLoggedIn()){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }

    }

    private void showLoadingView(){
        loadingView.show(getSupportFragmentManager(), "");
    }

    private void hideLoadingView(){
        loadingView.dismiss();
    }

    private void setUpLogin(){
        UserName = etusername.getText().toString().trim();
        Password = etpassword.getText().toString().trim();

        if(UserName.isEmpty()){
            loadingView.setText("Invalid Username");
            etusername.setError("Please enter a valid Username!");
            etusername.requestFocus();
            dismiss();
            return;
        }

        if(Password.isEmpty() || Password.length() < 4){
            etpassword.setError("Invalid password");
            etpassword.requestFocus();
            dismiss();
            return;
        }

        authentication.loginUser(UserName+"@coolopool.com", Password, loadingView);


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

    public static String getUsername() {
        return UserName;
    }
}
