package com.example.aghello.Helpers;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.example.aghello.R;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;

import io.github.controlwear.virtual.joystick.android.JoystickView;


//two joysticks that control a nodes position and directions
public class Joystick
{
    private JoystickView joystickPosition;
    private JoystickView joystickDirection;
    private TransformableNode node;
    private final Activity activity;
    private Vector3 directionVector;
    public Joystick(Activity activity, TransformableNode node)
    {
        this.activity = activity;
        joystickPosition = (JoystickView) activity.findViewById(R.id.joystickViewPosition);
        joystickDirection = (JoystickView) activity.findViewById(R.id.joystickViewDirection);
        joystickPosition.setVisibility(View.VISIBLE);
        joystickDirection.setVisibility(View.VISIBLE);
        this.node = node;
        directionVector = new Vector3(0, 90, 0);
        setJoystickDirectionListener();
        setJoystickPositionListener();
    }

    //uses direction of the tank to calculate z and x using unit circle formulas
    //substracts 30 degrees from direction because the model and real direction are not similar
    public void setJoystickPositionListener()
    {
        joystickPosition.setOnMoveListener((angle, strength) ->
        {
            Vector3 v = node.getLocalPosition();
            if(angle > 0 && angle < 180)
            {
                v.z = v.z - strength * 0.0001f * (float)Math.sin(Math.toRadians(directionVector.y-30));
                v.x = v.x + strength * 0.0001f * (float)Math.cos(Math.toRadians(directionVector.y-30));
            }
            else
            {
                v.z = v.z + strength * 0.0001f * (float)Math.sin(Math.toRadians(directionVector.y-30));
                v.x = v.x - strength * 0.0001f * (float)Math.cos(Math.toRadians(directionVector.y-30));
            }
            node.setLocalPosition(v);
        });
    }

    //changes direction that the tank is facing, uses stored direction Vector to keep
    //track of direction
    public void setJoystickDirectionListener()
    {
        joystickDirection.setOnMoveListener((angle, strength) ->
        {
            directionVector.y = directionVector.y - strength * 0.01f * (float)Math.cos(Math.toRadians(angle));
            if(directionVector.y > 360)
            {
                directionVector.y = 0;
            }
            if(directionVector.y < 0 )
            {
                directionVector.y = 360;
            }
            node.setLocalRotation(new Quaternion(directionVector));
        });
    }

    public float getDirection()
    {
        return directionVector.y;
    }
}
