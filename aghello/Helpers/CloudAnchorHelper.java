package com.example.aghello.Helpers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.example.aghello.R;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

//handles cloud anchor resolving and hosting
@RequiresApi(api = Build.VERSION_CODES.N)
public class CloudAnchorHelper
{
    private boolean isPlaced;
    private final ArFragment arFragment;
    private Anchor cloudAnchor;
    private final Activity activity;
    private AppAnchorState anchorState;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    public CloudAnchorHelper(ArFragment arFragment, Activity activity)
    {
        this.arFragment = arFragment;
        isPlaced = false;
        anchorState = AppAnchorState.NONE;
        this.activity = activity;
        prefs = activity.getSharedPreferences("AnchorId", activity.MODE_PRIVATE);
        editor = prefs.edit();
        showToast("Connect to opponent by placing an anchor");
    }

    public void startCloudAnchorProcess()
    {
        //adds a cloud anchor and starts hosting it
        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) ->
        {
            if(!isPlaced)
            {
                anchorState = AppAnchorState.HOSTING;
                cloudAnchor = arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                showToast("Hosting");
            }
        }));

        //if hosting is done it saved the cloud anchor
        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime ->
        {
            if(anchorState !=  AppAnchorState.HOSTING)
            {
                return;
            }
            Anchor.CloudAnchorState cloudAnchorState = cloudAnchor.getCloudAnchorState();

            if(cloudAnchorState.isError())
            {
                showToast(cloudAnchorState.toString());
            }
            else if(cloudAnchorState == Anchor.CloudAnchorState.SUCCESS)
            {
                anchorState  = AppAnchorState.HOSTED;
                String anchorId = cloudAnchor.getCloudAnchorId();
                editor.putString("anchorId", anchorId);
                editor.apply();
                showToast("Anchor hosted successfully, Anchor id is:"
                        + anchorId);
            }
        });


        //searches for a cloud anchor
        /*Button resolve = activity.findViewById(R.id.resolve);
        resolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String anchorId = prefs.getString("anchorId", "null");

                if(anchorId.equals("null"))
                {
                    showToast("No anchor id found");
                }

                Anchor resolvedAnchor = arFragment.getArSceneView().getSession().resolveCloudAnchor(anchorId);
                isPlaced = true;
                arFragment.setOnTapArPlaneListener(null);
                activity.findViewById(R.id.resolve).setVisibility(View.GONE);
                createCloudAnchor(resolvedAnchor);
            }
        });*/
    }


    //creates a cloud anchor
    private void createCloudAnchor(Anchor anchor)
    {
        cloudAnchor = anchor;
        ModelRenderable
                .builder()
                .setSource(activity, GameObjectRenderHelper.ANCHOR_OBJECT.ID)
                .build()
                .thenAccept(modelRenderable -> addCloudAnchorToScene(anchor, modelRenderable));
    }

    //adds and renders the cloud anchor
    private void addCloudAnchorToScene(Anchor anchor, ModelRenderable renderable)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode cloudNode = new TransformableNode(arFragment.getTransformationSystem());
        cloudNode.getScaleController().setMaxScale(GameObjectRenderHelper.ANCHOR_OBJECT.maxSize);
        cloudNode.getScaleController().setMinScale(GameObjectRenderHelper.ANCHOR_OBJECT.minSize);
        cloudNode.setParent(anchorNode);
        cloudNode.setRenderable(renderable);
        cloudNode.getTranslationController().setEnabled(false);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        cloudNode.select();
        isPlaced = true;
        arFragment.setOnTapArPlaneListener(null);
    }

    public Anchor getCloudAnchor()
    {
        return cloudAnchor;
    }

    private void showToast(String s)
    {
        Toast toast = Toast.makeText(activity, s, Toast.LENGTH_SHORT);
        toast.show();
    }
}

