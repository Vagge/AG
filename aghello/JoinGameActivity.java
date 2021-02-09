package com.example.aghello;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aghello.ViewModels.GameConnectorViewModel;

import java.io.IOException;

public class JoinGameActivity extends AppCompatActivity {

    private TextView gameNameView;
    private GameConnectorViewModel vm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        gameNameView = findViewById(R.id.join_game_txt);
        vm = new ViewModelProvider(this).get(GameConnectorViewModel.class);
        vm.getUserId().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String userId)
            {
                if(userId!=null)
                {
                    showToast(userId);
                    startGameIntent();
                }
                else
                {
                    showToast("Game not found");
                }
            }
        });
        startGameIntent();
    }

    private void startGameIntent()
    {
        Intent startGameIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(startGameIntent);
    }

    public void joinGame(View view)
    {
        if(!gameNameView.getText().toString().equals(""))
        {
            try
            {
                vm.joinGame(gameNameView.getText().toString());
            }
            catch (IOException e)
            {
                showToast(e.getLocalizedMessage());
            }
        }
    }

    private void showToast(String s)
    {
        Toast t = Toast.makeText(this, s, Toast.LENGTH_LONG);
        t.show();
    }
}