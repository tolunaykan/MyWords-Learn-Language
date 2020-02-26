package com.tolunaykandirmaz.mywords;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Switch hideNativeWord;
    private Switch hideImage;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        hideNativeWord = findViewById(R.id.switch1);
        hideImage = findViewById(R.id.switch2);
        progressBar = findViewById(R.id.progressBar3);


        SharedPreferences preferences = getSharedPreferences("settings",MODE_PRIVATE);

        if(preferences.getBoolean("hideNativeWord",true)){
            hideNativeWord.setChecked(true);
        }else{
            hideNativeWord.setChecked(false);
        }

        if(preferences.getBoolean("hideImage",true)){
            hideImage.setChecked(true);
        }else{
            hideImage.setChecked(false);
        }

    }

    @Override
    public void onBackPressed() {
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences preferences = getSharedPreferences("settings",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideNativeWord",hideNativeWord.isChecked());
        editor.putBoolean("hideImage",hideImage.isChecked());
        editor.commit();
        progressBar.setVisibility(View.GONE);
        Intent mainActivity = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}
