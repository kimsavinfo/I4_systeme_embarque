package fr.epsi.kimsavinfo.qrchesse_android;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.FrameLayout;

import fr.epsi.kimsavinfo.qrchesse_android.Lib_Camera.CameraManager;

public class MainActivity extends Activity
{
    // Camera
    private Camera camera;
    private CameraPreview cameraPreview;
    // USB
    private UsbBroadcastReceiver usbReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // USB
        usbReceiver = new UsbBroadcastReceiver((UsbManager)getSystemService(Context.USB_SERVICE), this);

        registerReceiver(usbReceiver, usbReceiver.getFilter());
        if (getLastNonConfigurationInstance() != null)
        {
            usbReceiver.setAccessory((UsbAccessory) getLastNonConfigurationInstance());
        }

        // Camera
        camera = CameraManager.getCameraInstance(true);
        cameraPreview = new CameraPreview(this, camera, usbReceiver);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);
    }

    @Override
    public Object onRetainNonConfigurationInstance()
    {
        UsbAccessory usbAccessory = usbReceiver.getAccessory();

        if(usbAccessory != null)
        {
            return usbAccessory;
        }
        else
        {
            return super.onRetainNonConfigurationInstance();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(!usbReceiver.areInputsStreamNotNull())
        {
            if(usbReceiver.resume())
            {
                synchronized (usbReceiver)
                {
                    usbReceiver.synchronizeAccessory();
                }
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        usbReceiver.closeAccessory();
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(usbReceiver);
        super.onDestroy();
    }

}
