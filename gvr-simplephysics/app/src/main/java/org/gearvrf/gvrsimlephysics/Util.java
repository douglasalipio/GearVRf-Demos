package org.gearvrf.gvrsimlephysics;

/**
 * Created by d.alipio@samsung.com on 10/31/16.
 */

public class Util {

    public static final int MAX_DURATION = 2;
    private static float MAX_FORCE = 2.000f;

    public static float forceToBall(float velocity) {
        float force = Math.abs(velocity) * MAX_DURATION / 1000f;
        return force;
    }
}
