package fr.epsi.kimsavinfo.qrcheese;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by kimsavinfo on 16/04/15.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Camera.PreviewCallback recogniseQRCode;

    public CameraPreview(Context context, Camera camera)
    {
        super(context);
        this.camera = camera;
        recogniseQRCode = CameraPreviewCallback.recogniseQRCode();

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch (IOException e)
        {
            Log.e("surfaceCreated - Error setting camera preview: ", e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        if (surfaceHolder.getSurface() != null)
        {
            try
            {
                camera.stopPreview();
            }
            catch (Exception e)
            {
                Log.e("surfaceChanged - Tried to stop a non-existent preview", e.getMessage());
            }

            try
            {
                // Can a QRCode be recognised ?
                camera.setPreviewCallback(recogniseQRCode);

                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            }
            catch (Exception e)
            {
                Log.d("Error starting camera preview: ", e.getMessage());
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // Nothing special for the moment
    }
}
