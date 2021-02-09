package com.example.aghello.Network;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GameRoundNetwork
{
    private String userId;
    private final int port;
    private InetAddress server;
    private DatagramSocket socket;
    private MutableLiveData<Boolean> gameStarted;
    private final String DEBUG_TAG = "debugtag";

    public GameRoundNetwork(MutableLiveData<Boolean> gameStarted)
    {
        port = Integer.parseInt(ClientServerStrings.SERVER_PORT.toString());
        this.gameStarted = gameStarted;
    }

    //call this before trying to connect to game
    public void setUp() throws UnknownHostException, SocketException
    {
        this.server = InetAddress.getByName(ClientServerStrings.SERVER_NAME.toString());
        socket = new DatagramSocket(Integer.parseInt(ClientServerStrings.CLIENT_PORT.toString()));
    }

    //tries to host a game, return true if successful
    public void placeTank()
    {
        GameRoundNetwork.PlaceTank h = new GameRoundNetwork.PlaceTank();
        h.start();
    }

    private class PlaceTank extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                sendToServer(ClientServerStrings.TANK_PLACED.toString());
                String serverResponse = receiveFromServer();
                if(serverResponse.equals(ClientServerStrings.START_ROUND.toString()))
                {
                    gameStarted.postValue(true);
                }
                else
                {

                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        private void sendToServer(String msg) throws IOException {
            DatagramPacket packet;
            packet = new DatagramPacket(msg.getBytes(), msg.length(), server, port);
            socket.send(packet);
        }

        private String receiveFromServer() throws IOException {
            byte[] buffer = new byte[200];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server, port);
            socket.receive(packet);
            return packetToString(packet);
        }

        private String packetToString(DatagramPacket datagramPacket)
        {
            return new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        }
    }
}
