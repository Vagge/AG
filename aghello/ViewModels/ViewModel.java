package com.example.aghello.ViewModels;

import androidx.lifecycle.MutableLiveData;

import com.example.aghello.Network.GameConnectorNetwork;
import com.example.aghello.Network.GameRoundNetwork;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel
{

    private boolean isTankPlaced;
    private MutableLiveData<Boolean> startGame;
    private  GameRoundNetwork gameRoundHandler;

    public ViewModel()
    {
        isTankPlaced = false;
    }

    public MutableLiveData<Boolean> getStartGame()
    {
        if(startGame == null)
        {
            startGame = new MutableLiveData<>();
            gameRoundHandler = new GameRoundNetwork(startGame);
        }
        return startGame;
    }


    public boolean isTankPlaced()
    {
        return isTankPlaced;
    }

    public void placeTank() throws SocketException, UnknownHostException {
        isTankPlaced = true;
        gameRoundHandler.setUp();
        gameRoundHandler.placeTank();
    }

    public void fireProjectile()
    {
        //model fire projectile
    }

    public  void removeProjectile()
    {

    }

    public List<Integer> getPowerups()
    {

        List list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(1);
        list.add(2);
        list.add(3);
        return list;
    }

    public void consumePowerup()
    {

    }
}
