package fr.epsi.kimsavinfo.qrcheese;

import android.hardware.Camera;

/**
 * Created by kimsavinfo on 16/04/15.
 */
public class Lib_Camera
{

    public static Camera getCameraInstance()
    {
        Camera c = null;
        try
        {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e)
        {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
