package com.example.aghello;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.aghello.Helpers.CloudAnchorHelper;
import com.example.aghello.Helpers.GameObjectRenderHelper;
import com.example.aghello.Helpers.Joystick;
import com.example.aghello.ViewModels.ViewModel;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.N)
public class  MainActivity extends AppCompatActivity {

    private CustomArFragment arFragment;
    private Random random;
    private ViewModel vm;
    private Joystick joystickHelper;
    private TransformableNode tankNode;
    private Anchor tankAnchor;
    private final int PROJECTILE_DURATION = 6;
    private String TAG = "debugtag";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        random = new Random();
        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        vm = new ViewModelProvider(this).get(ViewModel.class);

        vm.getStartGame().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                {
                    showToast("Game started");
                }
            }
        });
        startGame();
    }

    private void showToast(String s)
    {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void addTankToScene(Anchor anchor, ModelRenderable modelRenderable)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        tankAnchor = anchor;
        tankNode = new TransformableNode(arFragment.getTransformationSystem());
        tankNode.getScaleController().setMaxScale(GameObjectRenderHelper.TANK_OBJECT.maxSize);
        tankNode.getScaleController().setMinScale(GameObjectRenderHelper.TANK_OBJECT.minSize);
        tankNode.setParent(anchorNode);
        tankNode.setRenderable(modelRenderable);
        tankNode.getTranslationController().setEnabled(false);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        tankNode.select();
        tankNode.setLocalRotation(new Quaternion(new Vector3(0, 90, 0)));
        try {
            vm.placeTank();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        joystickHelper = new Joystick(this, tankNode);
        spawnPowerups();
    }

    private void addProjectileToScene(Anchor anchor, ModelRenderable modelRenderable)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode projectile = new TransformableNode(arFragment.getTransformationSystem());
        projectile.getScaleController().setMaxScale(0.05f);
        projectile.getScaleController().setMinScale(0.03f);
        projectile.setParent(anchorNode);
        projectile.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        projectile.getTranslationController().setEnabled(false);
        projectile.select();
        projectile.setLocalRotation(new Quaternion(new Vector3(0, joystickHelper.getDirection()+56, 0)));
        Vector3 position = tankNode.getLocalPosition();
        position.y = position.y + 0.1f;
        projectile.setLocalPosition(position);
        vm.fireProjectile();

        ProjectileThread mrt = new ProjectileThread(projectile, joystickHelper.getDirection());

        //starts a thread to move the projectile
        //stops after a period of time and detaches the projectile
        Thread t = new Thread(mrt);
        t.start();

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = PROJECTILE_DURATION; // Time in seconds

            public void run() {
                System.out.println(i--);
                if (i < 0) {
                    timer.cancel();
                    mrt.setRun(false);
                    anchor.detach();
                    vm.removeProjectile();
                }
            }
        }, 0, 1000);
    }

    private void addPowerUpToScene(Anchor anchor, ModelRenderable modelRenderable, GameObjectRenderHelper powerUpType)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode powerUpNode = new TransformableNode(arFragment.getTransformationSystem());
        powerUpNode.getScaleController().setMaxScale(powerUpType.maxSize);
        powerUpNode.getScaleController().setMinScale(powerUpType.minSize);
        powerUpNode.setParent(anchorNode);
        powerUpNode.setRenderable(modelRenderable);
        powerUpNode.getTranslationController().setEnabled(false);
        powerUpNode.setLocalPosition(new Vector3(2*random.nextFloat()-1f, 2*random.nextFloat()-1f, 0));
        Log.d(TAG, powerUpNode.getLocalPosition().toString());
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        powerUpNode.select();
    }

    private void generatePowerUpAnchor(GameObjectRenderHelper powerUpType)
    {
        ModelRenderable.builder()
                .setSource(this, powerUpType.ID)
                .build()
                .thenAccept(modelRenderable -> addPowerUpToScene(arFragment.getArSceneView()
                        .getSession().createAnchor(tankAnchor.getPose()), modelRenderable, powerUpType))
                .exceptionally(throwable ->
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .show();
                    return null;
                });
    }


    //linked to fire button,
    public void fireProjectile(View view)
    {
        if(vm.isTankPlaced())
        {
            ModelRenderable.builder()
                    .setSource(this, R.raw.bullet)
                    .build()
                    .thenAccept(modelRenderable -> addProjectileToScene(arFragment.getArSceneView()
                            .getSession().createAnchor(tankAnchor.getPose()), modelRenderable))
                    .exceptionally(throwable ->
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(throwable.getMessage())
                                .show();
                        return null;
                    });
        }
    }

    public void spawnPowerups()
    {
        List<Integer> powerups = vm.getPowerups();
        for(int powerup : powerups)
        {
            //change from 1 2 3 to the enums
            switch(powerup)
            {
                case 1:
                    generatePowerUpAnchor(GameObjectRenderHelper.MISSILE_OBJECT);
                    break;
                case 2:
                    generatePowerUpAnchor(GameObjectRenderHelper.GEAR_OBJECT);
                    break;
                case 3:
                    generatePowerUpAnchor(GameObjectRenderHelper.AMMO_BOX_OBJECT);
                    break;
            }
        }
    }

    //if user has connected to the opponent, place tank and activate controls
    public void startGame()
    {
        showToast("Add the map to the scene by clicking on a point");
        findViewById(R.id.fire_bt).setVisibility(View.VISIBLE);
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if(!vm.isTankPlaced())
            {
                Anchor anchor = hitResult.createAnchor();
                ModelRenderable.builder()
                        .setSource(this, GameObjectRenderHelper.TANK_OBJECT.ID)
                        .build()
                        .thenAccept(modelRenderable -> addTankToScene(anchor, modelRenderable))
                        .exceptionally(throwable ->
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(throwable.getMessage())
                                    .show();
                            return null;
                        });
            }
        });
    }


    class ProjectileThread implements Runnable
    {
        private final TransformableNode transformableNode;
        private final Vector3 position;
        private boolean run;
        private float direction;
        public ProjectileThread(TransformableNode transformableNode, float startDirection)
        {
            this.transformableNode = transformableNode;
            run = true;
            direction = startDirection;
            position = transformableNode.getLocalPosition();
        }

        public void setRun(boolean set)
        {
            run = set;
        }
        @Override
        public void run()
        {
            while(run)
            {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                position.z = position.z -  0.01f * (float)Math.sin(Math.toRadians(direction-30));
                position.x = position.x +  0.01f * (float)Math.cos(Math.toRadians(direction-30));
                transformableNode.setLocalPosition(position);
            }
        }
    }
}