package fr.epsi.kimsavinfo.qrchesse_android;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import fr.epsi.kimsavinfo.qrchesse_android.Lib_Binary.BinaryManager;
import fr.epsi.kimsavinfo.qrchesse_android.Lib_Email.EmailManager;
import fr.epsi.kimsavinfo.qrchesse_android.Lib_Email.GmailAccountManager;
import fr.epsi.kimsavinfo.qrchesse_android.Lib_WebService.WebServiceManager;

/**
 * Created by kimsavinfo on 16/04/15.
 */
public class CameraPreviewCallback
{
    private static MultiFormatReader multiFormatReader = new MultiFormatReader();
    private static WebServiceManager webServiceManager = new WebServiceManager();
    private static UsbBroadcastReceiver usbReceiver;
    private static EmailManager emailManager;
    private static String messageLoginDenied = "QRCode non identifié";

    public static Camera.PreviewCallback recogniseQRCode(Context _context, UsbBroadcastReceiver _usbReceiver)
    {
        usbReceiver = _usbReceiver;
        // Email
        // Get local gmail account
        // <!> TODO : change  "myPassword"
        // -> can't get gmail local password whithout publicing the app
        emailManager = new EmailManager(
                GmailAccountManager.getAdress(_context),
                "myPassword");

        return new Camera.PreviewCallback()
        {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera)
            {
                Camera.Parameters params = camera.getParameters();
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
                    result = multiFormatReader.decode(bitmap, null);
                    if (result != null)
                    {
                        new QRCodeManagerTask().execute(result);
                    }
                }
                catch (Resources.NotFoundException e)
                {
                    // Log.d("Camera.PreviewCallback - NotFoundException", e.toString());
                }
                catch (Exception e)
                {
                    // Log.e("Camera.PreviewCallback - Exception", e.toString());
                }
            }
        };
    }

    /** ======================================================================
     * Send QR code
     ====================================================================== */

    // Ex :
    // 01110100011011110111010001101111////0110101101100101011001010111000001100011011000010110110001101101
    // -> toto
    // -> keepcalm
    // Adresse : http://kimsavinfo.fr/qrcheese/index.php?login=toto&password=keepcalm

    private static class QRCodeManagerTask extends AsyncTask<Result, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Result... _params)
        {
            String[] arguments = (_params[0].getText()).split("////");
            String login = "";
            String password = "";
            boolean isUserIdentified = false;

            try
            {
                login = BinaryManager.binaryToASCII(arguments[0]);
                password = BinaryManager.binaryToASCII(arguments[1]);

                isUserIdentified = webServiceManager.checkUser(login, password);
            }
            catch (Exception e)
            {
                Log.e("QRCodeManagerTask - manageQRCodeResult", e.toString());
            }

            return isUserIdentified;
        }

        /**
         * ======================================================================
         * Send signal to Arduino
         * 0 : there's an intruder
         * 1 : the person is allowed to go on
         * ======================================================================
         */
        @Override
        protected void onPostExecute(Boolean _isUserIdentified)
        {
            byte[] buffer = new byte[1];

            if(_isUserIdentified)
            {
                Log.v("QRCodeManagerTask  - user identifie", _isUserIdentified.toString());
                buffer[0] = (byte) 1;
            }
            else
            {
                Log.v("QRCodeManagerTask  - user rejete", _isUserIdentified.toString());
                buffer[0] = (byte) 0;

                Message message = emailManager.createMessage(messageLoginDenied);
                new SendMailTask().execute(message);
            }

            usbReceiver.sendSignal(buffer);
        }
    }

    /**
     * ======================================================================
     * Send an email with the Go PRO photo
     * ======================================================================
     */
    private static class SendMailTask extends AsyncTask<Message, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
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
}
