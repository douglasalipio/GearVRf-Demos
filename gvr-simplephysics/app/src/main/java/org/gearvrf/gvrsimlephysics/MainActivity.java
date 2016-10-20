package org.gearvrf.gvrsimlephysics;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.gearvrf.GVRActivity;

public class MainActivity extends GVRActivity {

    private MainScript main = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = new MainScript();
        setMain(main, "gvr.xml");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {

        }

        return super.onTouchEvent(event);
    }

}
