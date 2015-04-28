package fr.epsi.kimsavinfo.qrchesse_android;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;


import fr.epsi.kimsavinfo.qrchesse_android.Lib_Camera.CameraManager;

public class MainActivity extends Activity
{
    private Camera camera;
    private CameraPreview cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = CameraManager.getCameraInstance(true);
        cameraPreview = new CameraPreview(this, camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);
    }

    /** ======================================================================
     * Send an email with the Go PRO photo
     ====================================================================== */
    public void sendEmail(View view)
    {

    }


    /** ======================================================================
     * Send signal to Arduino
     * 0 : there's an intruder
     * 1 : the person is allowed to go on
     ====================================================================== */

    public void sendSignal(View view)
    {

    }

}
