package fr.epsi.kimsavinfo.qrcheese;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by kimsavinfo on 16/04/15.
 */
public class Lib_Camera
{

    public static Camera getCameraInstance()
    {
        int cameraCount = 0;
        Camera camera = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();

        for (int camIdx = 0; camIdx < cameraCount; camIdx++)
        {
            Camera.getCameraInfo(camIdx, cameraInfo);
            // if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                try
                {
                    camera = Camera.open(camIdx);
                }
                catch (RuntimeException e)
                {
                    Log.e("Camera failed to open", e.getLocalizedMessage());
                }
            }
        }

        return camera; // returns null if camera is unavailable
    }
}
