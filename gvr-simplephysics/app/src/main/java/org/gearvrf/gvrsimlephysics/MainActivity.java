package org.gearvrf.gvrsimlephysics;

import android.os.Bundle;
import android.view.MotionEvent;

import org.gearvrf.GVRActivity;

public class MainActivity extends GVRActivity implements VRTouchPadGestureDetector.OnTouchPadGestureListener {

    private MainScript main = null;
    private VRTouchPadGestureDetector touchPadGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = new MainScript();
        touchPadGestureDetector = new VRTouchPadGestureDetector(this);
        setMain(main, "gvr.xml");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchPadGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTap(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onSwipe(MotionEvent e, VRTouchPadGestureDetector.SwipeDirection swipeDirection, float velocityX, float velocityY) {
        main.onSwipe( e,  swipeDirection,  velocityX, velocityY);
        return false;
    }
}
