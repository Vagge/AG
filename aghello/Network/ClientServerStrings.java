package com.example.aghello.Network;

public enum ClientServerStrings
{
    HOST_GAME_REQUEST("host "),
    JOIN_GAME_REQUEST("join "),
    HOST_GAME_FAIL("fail"),
    JOIN_GAME_FAIL("fail"),
    TANK_PLACED("tank placed"),
    START_ROUND("start round"),
    TANK_POSITION("tank position"),
    SERVER_NAME("92.35.97.211"),
    SERVER_PORT("7878"),
    CLIENT_PORT("7879");

    private String msg;
    ClientServerStrings(String msg)
    {
        this.msg = msg;
    }

    @Override
    public String toString()
    {
        return msg;
    }
}
