package com.example.szczocik.yafa_yetanotherfitnessapp.Activities;

import android.content.Intent;
import android.provider.ContactsContract;
import android.service.textservice.SpellCheckerService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.DatabaseHandler;
import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.LocationHandler;
import com.example.szczocik.yafa_yetanotherfitnessapp.MainActivity;
import com.example.szczocik.yafa_yetanotherfitnessapp.R;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Marcin Szczot (40180425)
 * Login activity
 */
public class Login extends AppCompatActivity {

    private LoginButton loginButton;
    // Creating Facebook CallbackManager Value
    public static CallbackManager callbackManager;
    private DatabaseHandler db;
    LocationHandler lh;

    TextView test;

    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //creates new database handler and new instance of locationhandler
        db = new DatabaseHandler(this);
        lh = LocationHandler.getInstance(db);

        test = (TextView) findViewById(R.id.test);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //Facebook login button
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPublishPermissions("publish_actions");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {}
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
