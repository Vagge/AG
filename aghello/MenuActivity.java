package com.example.aghello;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);
    }

    public void hostGame(View view)
    {
        Intent hostGameIntent = new Intent(this, HostGameActivity.class);
        startActivity(hostGameIntent);
    }

    public void joinGame(View view)
    {
        Intent joinGameIntent = new Intent(this, JoinGameActivity.class);
        startActivity(joinGameIntent);
    }

    public void settings(View view)
    {
        Intent settingsIntent = new Intent(this, HostGameActivity.class);
        startActivity(settingsIntent);
    }
}