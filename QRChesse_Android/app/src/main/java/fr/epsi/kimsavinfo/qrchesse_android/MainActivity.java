package fr.epsi.kimsavinfo.qrchesse_android;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
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

import fr.epsi.kimsavinfo.qrchesse_android.Lib_Email.EmailManager;
import fr.epsi.kimsavinfo.qrchesse_android.Lib_Email.GmailAccountManager;
import fr.epsi.kimsavinfo.qrchesse_android.Lib_Camera.CameraManager;

public class MainActivity extends Activity {
    // Camera
    private Camera camera;
    private CameraPreview cameraPreview;
    // Email
    private EmailManager emailManager;
    String messageLoginDenied = "QRCode non identifié";
    // USB
    private ToggleButton buttonLED;
    private static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;
    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    FileInputStream mInputStream;
    FileOutputStream mOutputStream;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action))
            {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                    {
                        openAccessory(accessory);
                    }
                    else
                    {
                        Log.d("BroadcastReceiver", "permission denied for accessory "
                                + accessory);
                    }
                    mPermissionRequestPending = false;
                }
            }
            else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action))
            {
                UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                if (accessory != null && accessory.equals(mAccessory))
                {
                    closeAccessory();
                }
            }
        }
    };


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
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);
        if (getLastNonConfigurationInstance() != null) {
            mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
            openAccessory(mAccessory);
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance()
    {
        if (mAccessory != null)
        {
            return mAccessory;
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

        if (mInputStream != null && mOutputStream != null)
        {
            return;
        }

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();

        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null)
        {
            if (mUsbManager.hasPermission(accessory))
            {
                openAccessory(accessory);
            }
            else
            {
                synchronized (mUsbReceiver)
                {
                    if (!mPermissionRequestPending)
                    {
                        mUsbManager.requestPermission(accessory,mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        }
        else
        {
            Log.d("onResume", "mAccessory is null");
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        closeAccessory();
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(mUsbReceiver);
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

        if(buttonLED.isChecked())
            buffer[0]=(byte)0; // button says on, light is off
        else
            buffer[0]=(byte)1; // button says off, light is on

        if (mOutputStream != null)
        {
            try
            {
                mOutputStream.write(buffer);
            }
            catch (IOException e)
            {
                Log.e("sendSignal", "write failed", e);
            }
        }
    }

    private void openAccessory(UsbAccessory accessory)
    {
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null)
        {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);
            Log.d("openAccessory", "accessory opened");
        }
        else
        {
            Log.d("openAccessory", "accessory open fail");
        }
    }


    private void closeAccessory()
    {
        try
        {
            if (mFileDescriptor != null)
            {
                mFileDescriptor.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            mFileDescriptor = null;
            mAccessory = null;
        }
    }

}
