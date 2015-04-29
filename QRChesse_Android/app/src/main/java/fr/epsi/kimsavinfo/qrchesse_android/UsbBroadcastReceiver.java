package fr.epsi.kimsavinfo.qrchesse_android;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kimsavinfo on 29/04/15.
 */
public class UsbBroadcastReceiver extends BroadcastReceiver
{
    private static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";
    private PendingIntent permissionIntent;
    private boolean permissionRequestPending;
    private UsbAccessory usbAccessory;
    private ParcelFileDescriptor fileDescriptor;
    private UsbManager usbManager;
    private FileInputStream inputStream;
    private FileOutputStream outputStream;
    private IntentFilter filter;

    public UsbBroadcastReceiver(UsbManager _usbManager, Context _context)
    {
        super();

        usbManager = _usbManager;
        permissionIntent = PendingIntent.getBroadcast(_context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (ACTION_USB_PERMISSION.equals(action))
        {
            synchronized (this)
            {
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                {
                    openAccessory(accessory);
                } else {
                    Log.d("BroadcastReceiver", "permission denied for accessory " + accessory);
                }
                permissionRequestPending = false;
            }
        } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action))
        {
            UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

            if (accessory != null && accessory.equals(usbAccessory))
            {
                closeAccessory();
            }
        }
    }

    public void openAccessory(UsbAccessory accessory)
    {
        fileDescriptor = usbManager.openAccessory(accessory);

        if (fileDescriptor != null)
        {
            usbAccessory = accessory;
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
            Log.d("openAccessory", "accessory opened");
        }
        else
        {
            Log.d("openAccessory", "accessory open fail");
        }
    }

    public void closeAccessory()
    {
        try
        {
            if (fileDescriptor != null)
            {
                fileDescriptor.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            fileDescriptor = null;
            usbAccessory = null;
        }
    }

    public void sendSignal(byte[] _buffer)
    {
        if (outputStream != null)
        {
            try
            {
                outputStream.write(_buffer);
            }
            catch (IOException e)
            {
                Log.e("sendSignal", "write failed", e);
            }
        }
    }

    public boolean areInputsStreamNotNull()
    {
        boolean areNotNull = false;

        if (inputStream != null && outputStream != null)
        {
            areNotNull = true;
        }

        return areNotNull;
    }

    public boolean resume()
    {
        boolean shouldSynchronized = false;
        UsbAccessory[] accessories = usbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);

        if (accessory != null)
        {
            if (usbManager.hasPermission(accessory))
            {
                openAccessory(accessory);
            }
            else
            {
                shouldSynchronized = true;
            }
        }
        else
        {
            Log.d("onResume", "usbAccessory is null");
        }

        return shouldSynchronized;
    }

    public void synchronizeAccessory()
    {
        UsbAccessory[] accessories = usbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);

        if (!permissionRequestPending)
        {
            usbManager.requestPermission(accessory, permissionIntent);
            permissionRequestPending = true;
        }
    }

    public IntentFilter getFilter()
    {
        return filter;
    }

    public void setAccessory(UsbAccessory _accessory)
    {
        usbAccessory = _accessory;
        openAccessory(usbAccessory);
    }

    public UsbAccessory getAccessory()
    {
        return usbAccessory;
    }
}
