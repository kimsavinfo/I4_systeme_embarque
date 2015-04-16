package fr.epsi.kimsavinfo.qrcheese;

import android.app.Activity;
import android.os.Bundle;
import android.hardware.Camera;
import android.widget.FrameLayout;

import fr.epsi.kimsavinfo.qrcheese.Lib_Camera.CameraManager;

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

}
