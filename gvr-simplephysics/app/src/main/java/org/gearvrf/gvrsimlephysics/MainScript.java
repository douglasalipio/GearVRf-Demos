package org.gearvrf.gvrsimlephysics;

import android.util.Log;
import android.view.MotionEvent;

import org.gearvrf.FutureWrapper;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.gvrsimlephysics.obj.Ball;
import org.gearvrf.gvrsimlephysics.obj.CylinderGroup;
import org.gearvrf.physics.GVRRigidBody;
import org.gearvrf.physics.GVRWorld;

import java.io.IOException;

public class MainScript extends GVRMain {

    private GVRContext gvrContext = null;
    private GVRScene scene;
    private GVRCameraRig mainCameraRig;


    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        this.gvrContext = gvrContext;
        scene = this.gvrContext.getNextMainScene();

        mainCameraRig = scene.getMainCameraRig();
        mainCameraRig.getTransform().setPosition(0.0f, 6.0f, 20f);
        addCylinderGroup();
        addGaze();
        addGroundMesh(scene, 0.0f, 0f, 0.0f, 0.0f);
        scene.getRoot().attachComponent(new GVRWorld(gvrContext));
    }

//    public void touchEvent() {
//        mSphereRigidBody.applyCentralForce(-20.0f, 500.0f, 0.0f);
//        mSphereRigidBody.applyTorque(5.0f, 0.5f, 0.0f);
//    }


    private void addCylinderGroup() throws IOException {
        //Added cylinderGroup in scene.
        CylinderGroup cylinderGroup = new CylinderGroup(gvrContext);
        cylinderGroup.getTransform().setPosition(-3, 0, 0);
        scene.addSceneObject(cylinderGroup);
    }

    private Ball createBall(float x, float y, float z) throws IOException {
        Ball ball = new Ball(gvrContext, new GVRAndroidResource(gvrContext, "ball.fbx"),
                new GVRAndroidResource(gvrContext, R.drawable.orange));
        ball.getTransform().setPosition(x, y, z);
        scene.addSceneObject(ball);
        return ball;

    }

    private void addGaze() {

        GVRSceneObject gaze = new GVRSceneObject(gvrContext,
                new FutureWrapper<GVRMesh>(gvrContext.createQuad(0.1f, 0.1f)),
                gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.gaze)));

        gaze.getTransform().setPosition(0.0f, 0.0f, -1f);
        gaze.getRenderData().setDepthTest(false);
        gaze.getRenderData().setRenderingOrder(100000);
        scene.getMainCameraRig().addChildObject(gaze);

    }

    private void addGroundMesh(GVRScene scene, float x, float y, float z, float mass) {

        GVRMesh mesh = gvrContext.createQuad(30.0f, 30.0f);
        GVRTexture texture =
                gvrContext.loadTexture(new GVRAndroidResource(gvrContext, R.drawable.default_color));
        GVRSceneObject meshObject = new GVRSceneObject(gvrContext, mesh, texture);
        meshObject.getTransform().setPosition(x, y, z);
        meshObject.getTransform().setRotationByAxis(-90.0f, 1.0f, 0.0f, 0.0f);

        // Collider
        GVRMeshCollider meshCollider = new GVRMeshCollider(gvrContext, mesh);
        meshObject.attachCollider(meshCollider);

        // Physics body
        GVRRigidBody body = new GVRRigidBody(gvrContext);
        body.setRestitution(0.5f);
        body.setFriction(1.0f);
        meshObject.attachComponent(body);
        scene.addSceneObject(meshObject);
    }

    @Override
    public void onStep() {
    }

    public void onTouchEvent(MotionEvent event) {
        float lastX = 0;
        float lastY = 0;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                float currentY = event.getY();
                float dx = currentX - lastX;
                float dy = currentY - lastY;
                float distance = dx * dx + dy * dy;
                Log.d("douglas", "distance - " + distance);

                break;
        }
    }


    public void onSwipe(MotionEvent e, VRTouchPadGestureDetector.SwipeDirection swipeDirection, float velocityX, float velocityY) {

        if (swipeDirection == VRTouchPadGestureDetector.SwipeDirection.Up) {
            Ball ball = null;
            try {
                ball = createBall(mainCameraRig.getTransform().getPositionX(),
                        mainCameraRig.getTransform().getPositionY(), mainCameraRig.getTransform().getPositionZ());
//                ball.getTransform().setRotation(mainCameraRig.getTransform().getRotationX(),
//                        mainCameraRig.getTransform().getRotationY(),
//                        mainCameraRig.getTransform().getRotationZ(),
//                        mainCameraRig.getTransform().getRotationW());
                        ball.setPhysic();
                ball.getRigidBody().applyCentralForce(mainCameraRig.getHeadTransformObject().getTransform().getRotationY() * -2000,
                        mainCameraRig.getHeadTransformObject().getTransform().getRotationX() * 2000,
                        -1000);
                // ball.getRigidBody().applyTorque(5.0f, 0.5f, 0.0f);
            } catch (IOException e1) {
                Log.e("Error", "");
            }
        }
    }
}
