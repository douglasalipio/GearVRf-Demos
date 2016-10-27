package org.gearvrf.gvrsimlephysics;

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
import org.gearvrf.physics.GVRRigidBody;
import org.gearvrf.physics.GVRWorld;

import java.io.IOException;
import java.util.concurrent.Future;

public class MainScript extends GVRMain {

    private GVRContext gvrContext = null;
    private GVRScene scene;
    private GVRCameraRig mainCameraRig;
    private static final float CUBE_MASS = 0.5f;


    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        this.gvrContext = gvrContext;
        scene = this.gvrContext.getNextMainScene();
        scene.addSceneObject(createLight(getGVRContext()));

        mainCameraRig = scene.getMainCameraRig();
        mainCameraRig.getLeftCamera().setBackgroundColor(245f,244f,214f,255f);
        mainCameraRig.getRightCamera().setBackgroundColor(245f,244f,214f,255f);
        mainCameraRig.getTransform().setPosition(0.0f, 6.0f, 20f);

        addGroundMesh(scene, 0.0f, 0f, 0.0f, 0.0f);
        addCylinderGroup();
        addGaze();

        scene.getRoot().attachComponent(new GVRWorld(gvrContext));

    }

    private void addCylinderGroup() throws IOException {
        scene.addSceneObject(((setColorInObject(createCylinder(7f, .5f, 1.0f, CUBE_MASS), R.drawable.black, 0, 0, 0))));
        scene.addSceneObject((setColorInObject(createCylinder(5f, .5f, 1.0f, CUBE_MASS), R.drawable.brown, .5f, .3f, .1f)));
        scene.addSceneObject((setColorInObject(createCylinder(3f, .5f, 1.0f, CUBE_MASS), R.drawable.green, .1f, .5f, .3f)));
        scene.addSceneObject((setColorInObject(createCylinder(7f, 1.8f, 1.0f, CUBE_MASS), R.drawable.grey, .6f, .6f, .6f)));
        scene.addSceneObject((setColorInObject(createCylinder(4f, .5f, 2.5f, CUBE_MASS), R.drawable.orange, 1f, .5f, 0f)));
        scene.addSceneObject((setColorInObject(createCylinder(-3f, .5f, 2.5f, CUBE_MASS), R.drawable.pink, 1f, 0f, .6f)));
        scene.addSceneObject((setColorInObject(createCylinder(0.5f, .5f, 2f, CUBE_MASS), R.drawable.red, 1f, 0f, 0f)));
        scene.addSceneObject((setColorInObject(createCylinder(2.5f, .5f, 3.5f, CUBE_MASS), R.drawable.yellow, 1f, .8f, 0f)));
        scene.addSceneObject((setColorInObject(createCylinder(2.5f, 1.8f, 3.3f, CUBE_MASS), R.drawable.light_blue, .4f, .2f, .9f)));
        scene.addSceneObject((setColorInObject(createCylinder(3.0f, .5f, 5.5f, CUBE_MASS), R.drawable.light_green, .5f, 1f, .7f)));
        scene.addSceneObject((setColorInObject(createCylinder(-5f, 5f, -1.5f, CUBE_MASS), R.drawable.dark_blue, 0f, 0f, .6f)));
        scene.addSceneObject((setColorInObject(createCylinder(5.5f, 15f, 7f, CUBE_MASS), R.drawable.cy, .2f, .8f, .7f)));
    }

    private Ball createBall(float x, float y, float z) throws IOException {
        Ball ball = new Ball(gvrContext, new GVRAndroidResource(gvrContext, "ball.fbx"),
                new GVRAndroidResource(gvrContext, R.drawable.orange));
        ball.getTransform().setPosition(x, y, z);
        scene.addSceneObject(ball);
        return ball;

    }

    private GVRSceneObject createCylinder(float x, float y, float z, float mass) throws IOException {
        GVRSceneObject cubeObject = new GVRSceneObject(gvrContext, new GVRAndroidResource(gvrContext, "cyl.fbx"),
                new GVRAndroidResource(gvrContext, R.drawable.orange));
        cubeObject.getTransform().setPosition(x, y, z);
        cubeObject.getTransform().setRotationByAxis(90.0f, 1.0f, 0.0f, 0.0f);


        // Collider
        GVRMeshCollider meshCollider = new GVRMeshCollider(gvrContext, cubeObject.getRenderData().getMesh());
        cubeObject.attachCollider(meshCollider);

        // Physics body
        GVRRigidBody body = new GVRRigidBody(gvrContext);
        body.setMass(mass);
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
        lightNode.getTransform().setPosition(0, 9f, 0);
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

    private void addGroundMesh(GVRScene scene, float x, float y, float z, float mass) {

        GVRMesh mesh = gvrContext.createQuad(30.0f, 30.0f);
        Future<GVRTexture> texture = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, R.drawable.orange));
        GVRMaterial material = new GVRMaterial(gvrContext);
        GVRSceneObject groundObject = new GVRSceneObject(gvrContext, mesh);
        groundObject.getRenderData().setMaterial(material);
        groundObject.getRenderData().getMaterial().setTexture("diffuseTexture", texture);
        groundObject.getTransform().setPosition(x, y, z);
        groundObject.getTransform().setRotationByAxis(-90.0f, 1.0f, 0.0f, 0.0f);

        //set Light
        groundObject.getRenderData().setShaderTemplate(GVRPhongShader.class);


        // Collider
        GVRMeshCollider meshCollider = new GVRMeshCollider(gvrContext, mesh);
        groundObject.attachCollider(meshCollider);

        // Physics body
        GVRRigidBody body = new GVRRigidBody(gvrContext);
        body.setRestitution(0.5f);
        body.setFriction(1.0f);
        groundObject.attachComponent(body);
        scene.addSceneObject(groundObject);
    }

    @Override
    public void onStep() {

    }


    public void onSwipe(MotionEvent e, VRTouchPadGestureDetector.SwipeDirection swipeDirection, float velocityX, float velocityY) {

        if (swipeDirection == VRTouchPadGestureDetector.SwipeDirection.Up) {
            Ball ball = null;
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

    private GVRSceneObject setColorInObject(GVRSceneObject object, int drawableColor, float r, float g, float b) {
        GVRMaterial material = new GVRMaterial(gvrContext);
        Future<GVRTexture> texture = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, drawableColor));
//        material.setVec4("diffuse_color", r, g, b, 1.0f);
//        material.setVec4("ambient_color", r, g, b, 1.0f);
//        material.setVec4("specular_color", r, g, b, 1.0f);
//        material.setVec4("emissive_color", 0.0f, 0.0f, 0.0f, 0.0f);
//        material.setFloat("specular_exponent", 10.0f);
//        material.setTexture("diffuseTexture", texture);
        material.setMainTexture(texture);

        object.getRenderData().setMaterial(material);
//        object.getRenderData().setShaderTemplate(GVRPhongShader.class);
        return object;
    }
}
