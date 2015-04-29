package fr.epsi.kimsavinfo.qrchesse_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import fr.epsi.kimsavinfo.qrchesse_android.Lib_Camera.CameraManager;
import fr.epsi.kimsavinfo.qrchesse_android.Lib_Email.EmailManager;
import fr.epsi.kimsavinfo.qrchesse_android.Lib_Email.GmailAccountManager;

public class MainActivity extends Activity
{
    // Camera
    private Camera camera;
    private CameraPreview cameraPreview;
    // Email
    private EmailManager emailManager;
    String messageLoginDenied = "QRCode non identifié";
    // USB
    private ToggleButton buttonLED;
    private UsbBroadcastReceiver usbReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Camera
        camera = CameraManager.getCameraInstance(true);
        cameraPreview = new CameraPreview(this, camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);

        // Email
        // Get local gmail account
        // <!> TODO : change  "myPassword"
        // -> can't get gmail local password whithout publicing the app
        emailManager = new EmailManager(
                GmailAccountManager.getAdress(getApplicationContext()),
                "myPassword");

        // USB
        buttonLED = (ToggleButton) findViewById(R.id.toggleButtonLED);
        usbReceiver = new UsbBroadcastReceiver((UsbManager)getSystemService(Context.USB_SERVICE), this);

        registerReceiver(usbReceiver, usbReceiver.getFilter());
        if (getLastNonConfigurationInstance() != null)
        {
            usbReceiver.setAccessory((UsbAccessory) getLastNonConfigurationInstance());
        }
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

    /**
     * ======================================================================
     * Send an email with the Go PRO photo
     * ======================================================================
     */

    public void sendEmail(View view)
    {
        Message message = emailManager.createMessage(messageLoginDenied);
        new SendMailTask().execute(message);
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages)
        {
            try
            {
                Transport.send(messages[0]);
            }
            catch (MessagingException e)
            {
                Log.e("sendMail - doInBackground ", e.toString());
            }
            return null;
        }
    }

    /**
     * ======================================================================
     * Send signal to Arduino
     * 0 : there's an intruder
     * 1 : the person is allowed to go on
     * ======================================================================
     */

    public void sendSignal(View view)
    {
        byte[] buffer = new byte[1];
        buffer[0] = (byte) 0;

        if(buttonLED.isChecked())
        {
            buffer[0] = (byte) 1;
        }

        usbReceiver.sendSignal(buffer);
    }
}
