package fr.epsi.kimsavinfo.qrcheese;

import android.app.Activity;
import android.app.ProgressDialog;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.hardware.Camera;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import fr.epsi.kimsavinfo.qrcheese.Lib_Camera.CameraManager;
import fr.epsi.kimsavinfo.qrcheese.Lib_Email.EmailManager;
import fr.epsi.kimsavinfo.qrcheese.Lib_Email.GmailAccountManager;

public class MainActivity extends Activity
{
    private Camera camera;
    private CameraPreview cameraPreview;
    private EmailManager emailManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get local gmail account
        // <!> TODO : change  "myPassword"
        // -> can't get gmail local password whithout publicing the app
        emailManager = new EmailManager(
                GmailAccountManager.getAdress(getApplicationContext()),
                "myPassword");

        camera = CameraManager.getCameraInstance(true);
        cameraPreview = new CameraPreview(this, camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);

    }

    public void sendEmail(View view)
    {
        Message message = emailManager.createMessage();
        new SendMailTask().execute(message);
    }

    /** ======================================================================
     * Send an email with the Go PRO photo
     ====================================================================== */

    private class SendMailTask extends AsyncTask<Message, Void, Void>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
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

    /** ======================================================================
     * Send signal to Arduino
     * 0 : there's an intruder
     * 1 : the person is allowed to go on
     ====================================================================== */

    public void sendSignal(View view)
    {

    }

    /*
    public void sendSignal(View view)
    {
        Log.d("sendSignal", "en cours");

        ToggleButton buttonLED = (ToggleButton) findViewById(R.id.toggleButtonLED);
        byte[] buffer = new byte[1];
        if(buttonLED.isChecked())
        {
            buffer[0] = (byte) 0; // button says on, light is off
        }
        else
        {
            buffer[0] = (byte) 1; // button says off, light is on
        }

        if (mOutputStream != null)
        {
            try
            {
                mOutputStream.write(buffer);
                Log.d("sendSignal", "OK");
            }
            catch (IOException e)
            {
                Log.e("sendSignal", "write failed", e);
            }
        }
    }
    */
}
