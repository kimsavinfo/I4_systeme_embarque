package fr.epsi.kimsavinfo.qrchesse_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import Lib_Email.EmailManager;
import Lib_Email.GmailAccountManager;
import fr.epsi.kimsavinfo.qrchesse_android.Lib_Camera.CameraManager;

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

        camera = CameraManager.getCameraInstance(true);
        cameraPreview = new CameraPreview(this, camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);

        // Get local gmail account
        // <!> TODO : change  "myPassword"
        // -> can't get gmail local password whithout publicing the app
        emailManager = new EmailManager(
                GmailAccountManager.getAdress(getApplicationContext()),
                "myPassword");
    }

    /** ======================================================================
     * Send an email with the Go PRO photo
     ====================================================================== */
    public void sendEmail(View view)
    {
        Message message = emailManager.createMessage();
        new SendMailTask().execute(message);
    }

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

}
