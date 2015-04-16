package fr.epsi.kimsavinfo.qrcheese;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;

/**
 * Created by kimsavinfo on 16/04/15.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private MultiFormatReader mMultiFormatReader;

    public CameraPreview(Context context, Camera camera)
    {
        super(context);
        mCamera = camera;
        mMultiFormatReader = new MultiFormatReader();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        // The Surface has been created, now tell the camera where to draw the preview.
        try
        {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("Error setting camera preview: ", e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null)
        {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try
        {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try
        {
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }
        catch (Exception e)
        {
            Log.d("Error starting camera preview: ", e.getMessage());
        }
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            Camera.Parameters params= camera.getParameters();
            int width = params.getPreviewSize().width;
            int height = params.getPreviewSize().height;

            LuminanceSource source = new PlanarYUVLuminanceSource(data,
                    width, height,
                    0, 0,
                    width, height,
                    false);

            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result;

            try
            {
                result = mMultiFormatReader.decode(bitmap, null);
                if (result != null)
                {
                    Log.d("QRC",result.getText());
                }
            }
            catch (NotFoundException e)
            {
                e.printStackTrace();
            }
        }
    };
}
