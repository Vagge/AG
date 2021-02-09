package com.example.aghello.Network;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


//connects and makes a game session to server
public class GameConnectorNetwork
{
    private MutableLiveData<String> userId;
    private final int port;
    private InetAddress server;
    private DatagramSocket socket;
    private final String DEBUG_TAG = "debugtag";

    public GameConnectorNetwork(MutableLiveData<String> userId)
    {
        port = Integer.parseInt(ClientServerStrings.SERVER_PORT.toString());
        this.userId = userId;
    }

    //call this before trying to connect to game
    public void setUp() throws UnknownHostException, SocketException
    {
        this.server = InetAddress.getByName(ClientServerStrings.SERVER_NAME.toString());
        socket = new DatagramSocket(Integer.parseInt(ClientServerStrings.CLIENT_PORT.toString()));
    }


    //tries to join game
    public void joinGame(String gameName)
    {
        JoinGame j = new JoinGame(gameName);
        j.start();
    }

    //tries to host a game, return true if successful
    public void hostGame(String gameName)
    {
        HostGame h = new HostGame(gameName);
        h.start();
    }

    private class HostGame extends Thread
    {
        String gameName;
        HostGame(String gameName)
        {
            this.gameName = gameName;
        }
        @Override
        public void run()
        {
            if(gameName!=null)
            {
                try {
                    sendToServer(ClientServerStrings.HOST_GAME_REQUEST.toString() + gameName);
                    String serverResponse = receiveFromServer();
                    if(serverResponse==null)
                    {
                        userId.postValue(null);
                    }
                    else if(serverResponse.equals(ClientServerStrings.HOST_GAME_FAIL.toString()))
                    {
                        userId.postValue(null);
                    }
                    else
                    {
                        userId.postValue(serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    socket.close();
                }
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

    private class JoinGame extends Thread
    {
        String gameName;
        JoinGame(String gameName)
        {
            this.gameName = gameName;
        }
        @Override
        public void run()
        {
            if(gameName!=null)
            {
                try {
                    sendToServer(ClientServerStrings.JOIN_GAME_REQUEST.toString() + gameName);
                    String serverResponse = receiveFromServer();
                    if(serverResponse==null)
                    {
                        userId.postValue(null);
                    }
                    else if(serverResponse.equals(ClientServerStrings.JOIN_GAME_FAIL.toString()))
                    {
                        userId.postValue(null);
                    }
                    else
                    {
                        userId.postValue(serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    socket.close();
                }
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
