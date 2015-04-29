package fr.epsi.kimsavinfo.qrchesse_android;

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

    public CameraPreview(Context _context, Camera _camera, UsbBroadcastReceiver _usbReceiver)
    {
        super(_context);
        this.camera = _camera;
        recogniseQRCode = CameraPreviewCallback.recogniseQRCode(_context, _usbReceiver);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder _holder)
    {
        try
        {
            camera.setPreviewDisplay(_holder);
            camera.startPreview();
        }
        catch (IOException e)
        {
            Log.e("surfaceCreated - Error setting camera preview: ", e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int _format, int _width, int _height)
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
    public void surfaceDestroyed(SurfaceHolder _holder)
    {
        // Nothing special for the moment
    }
}
