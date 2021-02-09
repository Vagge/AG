package com.example.aghello.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aghello.Network.GameConnectorNetwork;

import java.io.IOException;

public class GameConnectorViewModel extends ViewModel
{
    private String gameSessionName;
    private GameConnectorNetwork connectHandler;
    private MutableLiveData<String> userId;
    public GameConnectorViewModel()
    {

    }

    public MutableLiveData<String> getUserId()
    {
        if(userId == null)
        {
            userId = new MutableLiveData<>();
            connectHandler = new GameConnectorNetwork(userId);
        }
        return userId;
    }

    public String getGameSessionName()
    {
        return gameSessionName;
    }

    //if successful, return userId given by server
    public void joinGame(String gameSessionName) throws IOException
    {
        this.gameSessionName = gameSessionName;
        connectHandler.setUp();
        connectHandler.joinGame(gameSessionName);
    }

    //if successful, return userId given by server
    public void hostGame(String gameSessionName) throws IOException
    {
        this.gameSessionName = gameSessionName;
        connectHandler.setUp();
        connectHandler.hostGame(gameSessionName);
    }
}
