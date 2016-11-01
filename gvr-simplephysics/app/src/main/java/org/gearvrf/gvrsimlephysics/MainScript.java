package org.gearvrf.gvrsimlephysics;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;

import org.gearvrf.FutureWrapper;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.physics.GVRCollisionType;
import org.gearvrf.physics.GVRRigidBody;
import org.gearvrf.physics.GVRWorld;
import org.gearvrf.physics.ICollisionEvents;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;

import java.io.IOException;
import java.util.concurrent.Future;

import static org.gearvrf.gvrsimlephysics.R.drawable.cy;

public class MainScript extends GVRMain {

    private GVRContext gvrContext = null;
    private GVRScene scene;
    private GVRCameraRig mainCameraRig;
    private static final float CUBE_MASS = 0.3f;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    public static GVRCollisionType GROUND_ID;
    public static GVRCollisionType CYLINDER_ID;
    public static GVRCollisionType BALL_ID;
    public static GVRCollisionType INVISIBLE_GROUND_ID;

    static {
        GROUND_ID = new GVRCollisionType((short) 1);
        CYLINDER_ID = new GVRCollisionType((short) 2);
        BALL_ID = new GVRCollisionType((short) 3);
        INVISIBLE_GROUND_ID = new GVRCollisionType((short) 4);

        BALL_ID.colideWith(CYLINDER_ID);
        BALL_ID.colideWith(GROUND_ID);

        GROUND_ID.colideWith(BALL_ID);
        GROUND_ID.colideWith(CYLINDER_ID);

        CYLINDER_ID.colideWith(BALL_ID);
        CYLINDER_ID.colideWith(INVISIBLE_GROUND_ID);
        CYLINDER_ID.colideWith(GROUND_ID);
        CYLINDER_ID.colideWith(CYLINDER_ID);

    }

    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        this.gvrContext = gvrContext;
        scene = this.gvrContext.getNextMainScene();
        scene.addSceneObject(createLight(getGVRContext()));

        mainCameraRig = scene.getMainCameraRig();
        mainCameraRig.getLeftCamera().setBackgroundColor(245f, 244f, 214f, 255f);
        mainCameraRig.getRightCamera().setBackgroundColor(245f, 244f, 214f, 255f);
        mainCameraRig.getTransform().setPosition(0.0f, 6.0f, 20f);

        addGroundMesh();
        addCylinderGroup();
        addGaze();
        setTimer();
        addInvisibleGround();
        scene.getRoot().attachComponent(new GVRWorld(gvrContext));


    }


    private void setTimer() {
        GVRMesh mesh = gvrContext.createQuad(5.0f, 5.0f);
        GVRTexture texture = gvrContext.loadTexture(new GVRAndroidResource(gvrContext, R.drawable.empty));
        GVRSceneObject quadTimer = new GVRSceneObject(gvrContext, mesh, texture);
        quadTimer.getTransform().setPosition(2.5f, 10f, 3.3f);
        GVRTextViewSceneObject timeObject = new GVRTextViewSceneObject(gvrContext);
        timeObject.setText("03:00");
        timeObject.setTextColor(Color.BLACK);
        timeObject.setGravity(Gravity.CENTER);
        timeObject.setTextSize(20f);
        timeObject.setRefreshFrequency(GVRTextViewSceneObject.IntervalFrequency.LOW);
        timeObject.getTransform().setPosition(2.5f, 8f, 3.5f);
        scene.addSceneObject(quadTimer);
        scene.addSceneObject(timeObject);
    }

    private void addInvisibleGround() {

        GVRMesh mesh = gvrContext.createQuad(300.0f, 300.0f);
        Future<GVRTexture> texture = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.empty));
        GVRMaterial material = new GVRMaterial(gvrContext);
        GVRSceneObject groundObject = new GVRSceneObject(gvrContext, mesh);
        groundObject.getRenderData().setMaterial(material);
        groundObject.getRenderData().getMaterial().setTexture("diffuseTexture", texture);
        groundObject.getTransform().setPosition(0.0f, -1f, 0.0f);
        groundObject.getTransform().setRotationByAxis(-90.0f, 1.0f, 0.0f, 0.0f);
        groundObject.getEventReceiver().addListener(new ICollisionEvents() {
            @Override
            public void onEnter(GVRSceneObject gvrSceneObject, GVRSceneObject gvrSceneObject1, float[] floats, float v) {

                if (gvrSceneObject.getName().equals("cylinder")) {

                    gvrSceneObject.setEnable(false);
                    GVRRigidBody rigidBody = (GVRRigidBody) gvrSceneObject.getComponent(GVRRigidBody.getComponentType());
                    rigidBody.getCollisionType().colideNotWith(INVISIBLE_GROUND_ID);
                    gvrSceneObject.setEnable(true);

                } else {

                }
            }

            @Override
            public void onExit(GVRSceneObject gvrSceneObject, GVRSceneObject gvrSceneObject1, float[] floats, float v) {

            }
        });

        //set phong Shader
        groundObject.getRenderData().setShaderTemplate(GVRPhongShader.class);


        // Collider
        GVRMeshCollider meshCollider = new GVRMeshCollider(gvrContext, mesh);
        groundObject.attachCollider(meshCollider);

        // Physics body
        GVRRigidBody body = new GVRRigidBody(gvrContext);
        body.setRestitution(0.5f);
        body.setFriction(1.0f);
        body.setCollisionType(INVISIBLE_GROUND_ID);
        groundObject.attachComponent(body);
        scene.addSceneObject(groundObject);
    }

    private void addCylinderGroup() throws IOException {

        scene.addSceneObject(createCylinder(7f, .5f, 1.0f, CUBE_MASS, R.drawable.black));
        scene.addSceneObject(createCylinder(5f, .5f, 1.0f, CUBE_MASS, R.drawable.brown));
        scene.addSceneObject(createCylinder(3f, .5f, 1.0f, CUBE_MASS, R.drawable.green));
        scene.addSceneObject(createCylinder(7f, 1.8f, 1.0f, CUBE_MASS, R.drawable.grey));
        scene.addSceneObject(createCylinder(4f, .5f, 2.5f, CUBE_MASS, R.drawable.orange));
        scene.addSceneObject(createCylinder(-3f, .5f, 2.5f, CUBE_MASS, R.drawable.pink));
        scene.addSceneObject(createCylinder(0.5f, .5f, 2f, CUBE_MASS, R.drawable.red));
        scene.addSceneObject(createCylinder(2.5f, .5f, 3.5f, CUBE_MASS, R.drawable.yellow));
        scene.addSceneObject(createCylinder(2.5f, 1.8f, 3.3f, CUBE_MASS, R.drawable.light_blue));
        scene.addSceneObject(createCylinder(3.0f, .5f, 5.5f, CUBE_MASS, R.drawable.light_green));
        scene.addSceneObject(createCylinder(-5f, 5f, -1.5f, CUBE_MASS, R.drawable.dark_blue));
        scene.addSceneObject(createCylinder(5.5f, 15f, 7f, CUBE_MASS, cy));
    }

    private Ball createBall(float x, float y, float z) throws IOException {
        Ball ball = new Ball(gvrContext, new GVRAndroidResource(gvrContext, "ball.fbx"),
                new GVRAndroidResource(gvrContext, R.drawable.orange));
        ball.getTransform().setPosition(x, y, z);
        scene.addSceneObject(ball);
        return ball;

    }

    private GVRSceneObject createCylinder(float x, float y, float z, float mass, int drawable) throws IOException {
        GVRSceneObject cubeObject = new GVRSceneObject(gvrContext, new GVRAndroidResource(gvrContext, "cylinder.fbx"),
                new GVRAndroidResource(gvrContext, drawable));
        cubeObject.getTransform().setPosition(x, y, z);
        cubeObject.getTransform().setRotationByAxis(90.0f, 1.0f, 0.0f, 0.0f);
        cubeObject.setName("cylinder");


        // Collider
        GVRMeshCollider meshCollider = new GVRMeshCollider(gvrContext, cubeObject.getRenderData().getMesh());
        cubeObject.attachCollider(meshCollider);

        // Physics body
        GVRRigidBody body = new GVRRigidBody(gvrContext);
        body.setMass(mass);
        body.setCollisionType(CYLINDER_ID);
        body.setRestitution(0.5f);
        body.setFriction(5.0f);
        cubeObject.attachComponent(body);

        return cubeObject;
    }

    private GVRSceneObject createLight(GVRContext context) {
        GVRSceneObject lightNode = new GVRSceneObject(context);
        GVRDirectLight light = new GVRDirectLight(context);
        light.setCastShadow(true);
        light.setAmbientIntensity(0.3f * 1, 0.3f * 1, 0, 1);
        light.setDiffuseIntensity(1, .9f, .8f, 1);
        light.setSpecularIntensity(1, .9f, .8f, 1);
        lightNode.getTransform().setPosition(0, 9f, 1);
        lightNode.getTransform().setRotationByAxis(-90, 1, 0, 0);
        lightNode.attachLight(light);
        return lightNode;
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

    private void addGroundMesh() {

        GVRMesh mesh = gvrContext.createQuad(30.0f, 30.0f);
        Future<GVRTexture> texture = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.orange));
        GVRMaterial material = new GVRMaterial(gvrContext);
        GVRSceneObject groundObject = new GVRSceneObject(gvrContext, mesh);
        groundObject.getRenderData().setMaterial(material);
        groundObject.getRenderData().getMaterial().setTexture("diffuseTexture", texture);
        groundObject.getTransform().setPosition(0.0f, 0f, 0.0f);
        groundObject.getTransform().setRotationByAxis(-90.0f, 1.0f, 0.0f, 0.0f);

        //set phong Shader
        groundObject.getRenderData().setShaderTemplate(GVRPhongShader.class);


        // Collider
        GVRMeshCollider meshCollider = new GVRMeshCollider(gvrContext, mesh);
        groundObject.attachCollider(meshCollider);

        // Physics body
        GVRRigidBody body = new GVRRigidBody(gvrContext);
        body.setRestitution(0.5f);
        body.setFriction(1.0f);
        body.setCollisionType(GROUND_ID);
        groundObject.attachComponent(body);
        scene.addSceneObject(groundObject);
    }

    @Override
    public void onStep() {

    }

    public void onSwipe(MotionEvent event, VRTouchPadGestureDetector.SwipeDirection swipeDirection, float velocityX, float velocityY) {

        if (swipeDirection == VRTouchPadGestureDetector.SwipeDirection.Forward) {

            Ball ball = null;
            int maxDuration = 2;
            float test = Math.abs(velocityX) * maxDuration / 1000f;
            Log.d("douglas", "velocity y = " + test);

            try {
                ball = createBall(mainCameraRig.getTransform().getPositionX(),
                        mainCameraRig.getTransform().getPositionY(), mainCameraRig.getTransform().getPositionZ());
                ball.setPhysic();
                ball.getRigidBody().applyCentralForce(mainCameraRig.getHeadTransformObject().getTransform().getRotationY() * -2000,
                        mainCameraRig.getHeadTransformObject().getTransform().getRotationX() * 2000,
                        -1000);
            } catch (IOException e1) {
                e1.getMessage();
            }
        }
    }
}
