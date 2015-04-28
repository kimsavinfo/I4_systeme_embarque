package fr.epsi.kimsavinfo.qrchesse_android.Lib_Camera;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by kimsavinfo on 16/04/15.
 */
public class CameraManager
{
    public static Camera getCameraInstance(boolean _backCameraWanted)
    {
        Camera camera = null;

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraIndex = _backCameraWanted ? getBackCameraIndex(cameraInfo) : getFrontCameraIndex(cameraInfo);

        try
        {
            camera = Camera.open(cameraIndex);
        }
        catch (RuntimeException e)
        {
            Log.e("getCameraInstance - Camera failed to open", e.getLocalizedMessage());
        }

        return camera;
    }

    private static int getBackCameraIndex(Camera.CameraInfo cameraInfo)
    {
        int iBackCamera = 0;
        int cameraCount = Camera.getNumberOfCameras();

        for (int iCamera = 0; iCamera < cameraCount; iCamera++)
        {
            Camera.getCameraInfo(iCamera, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                iBackCamera = iCamera;
            }
        }

        return iBackCamera;
    }

    private static int getFrontCameraIndex(Camera.CameraInfo cameraInfo)
    {
        int iFrontCamera = 0;
        int cameraCount = Camera.getNumberOfCameras();

        for (int iCamera = 0; iCamera < cameraCount; iCamera++)
        {
            Camera.getCameraInfo(iCamera, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
            {
                iFrontCamera = iCamera;
            }
        }

        return iFrontCamera;
    }
}