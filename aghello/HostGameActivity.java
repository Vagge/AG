package com.example.aghello;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aghello.ViewModels.GameConnectorViewModel;
import com.example.aghello.ViewModels.ViewModel;

import java.io.IOException;

public class HostGameActivity extends AppCompatActivity
{
    private TextView gameNameView;
    private GameConnectorViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);
        gameNameView = findViewById(R.id.host_game_text);
        vm = new ViewModelProvider(this).get(GameConnectorViewModel.class);
        vm.getUserId().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String userId)
            {
                if(userId!=null)
                {
                    showToast(userId);
                }
                else
                {
                    showToast("Game not found");
                }
            }
        });
    }

    public void hostGame(View view)
    {
        if(!gameNameView.getText().toString().equals(""))
        {
            if(!gameNameView.getText().toString().equals(""))
            {
                try {
                    vm.hostGame(gameNameView.getText().toString());

                } catch (IOException e)
                {
                    showToast(e.getLocalizedMessage());
                }
            }
        }
    }

    private void showToast(String s)
    {
        Toast t = Toast.makeText(this, s, Toast.LENGTH_LONG);
        t.show();
    }
}