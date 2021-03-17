package com.jj.socialloginapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookActivity extends AppCompatActivity {

    private TextView info;
    private TextView name;
    private TextView email;
    private ImageView profilePhoto;
    private LoginButton loginButton;

    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        // Passing MainActivity in Facebook SDK.
        FacebookSdk.sdkInitialize(FacebookActivity.this);

        info = findViewById(R.id.info);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        profilePhoto = findViewById(R.id.profilePhoto);
        loginButton = findViewById(R.id.login);


        callbackManager = CallbackManager.Factory.create();

        // Giving permission to Login Button.
        loginButton.setReadPermissions("email");

        // Checking the Access Token.
        if(AccessToken.getCurrentAccessToken()!=null){

            GraphLoginRequest(AccessToken.getCurrentAccessToken());

            // If already login in then show the Toast.
            Toast.makeText(FacebookActivity.this,"Already logged in",Toast.LENGTH_SHORT).show();

        }else {

            // If not login in then show the Toast.
            Toast.makeText(FacebookActivity.this,"User not logged in",Toast.LENGTH_SHORT).show();
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                String imageUrl = "https://graph.facebook.com/"+loginResult.getAccessToken().getUserId()+ "/picture?type=square";
                Picasso.get().load(imageUrl).into(profilePhoto);

                // Calling method to access User Data After successfully login.
                GraphLoginRequest(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(FacebookActivity.this,"Login Canceled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FacebookActivity.this, " Login Error!!!", Toast.LENGTH_SHORT).show();
            }
        });

        // Detect user is login or not. If logout then clear the TextView and delete all the user info from TextView.
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken2 == null) {

                    // Clear the TextView after logout.
                    info.setText("");
                    name.setText("");
                    email.setText("");
                    Picasso.get().load("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.drupal.org%2Fproject%2Fimce_copylink&psig=AOvVaw07zILGWugnAeMEvFyrX1yg&ust=1615114960479000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCNiuyp3Cm-8CFQAAAAAdAAAAABAI").into(profilePhoto);


                }
            }
        };

    }


    // Method to access Facebook User Data.
    protected void GraphLoginRequest(AccessToken accessToken){
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                        try {

                            // Adding all user info one by one into TextView.
                            info.setText("ID: " + jsonObject.getString("id"));

                            name.setText("\nName : " + jsonObject.getString("name"));

                            email.setText("\nEmail : " + jsonObject.getString("email"));


                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString(
                "fields",
                "id,name,link,email,gender,last_name,first_name,locale,timezone,updated_time,verified"
        );
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}