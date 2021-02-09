package com.example.aghello.Helpers;

import com.example.aghello.R;

//all the rendered objects and their sizes goes here
public enum GameObjectRenderHelper
{
    TANK_OBJECT(R.raw.tank, 0.1f, 0.2f),
    MISSILE_OBJECT(R.raw.missile, 0.06f, 0.08f),
    GEAR_OBJECT(R.raw.gear, 0.03f, 0.05f),
    ANCHOR_OBJECT(R.raw.anchor, 0.01f, 0.05f),
    AMMO_BOX_OBJECT(R.raw.model, 0.3f, 0.5f);

    public final int ID;
    public final float minSize, maxSize;
    GameObjectRenderHelper(int id, float minSize, float maxSize)
    {
        this.ID = id;
        this.maxSize = maxSize;
        this.minSize = minSize;
    }
}
