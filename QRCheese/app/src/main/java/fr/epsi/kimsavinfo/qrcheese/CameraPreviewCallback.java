package fr.epsi.kimsavinfo.qrcheese;

import android.hardware.Camera;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import fr.epsi.kimsavinfo.qrcheese.Lib_Binary.BinaryManager;
import fr.epsi.kimsavinfo.qrcheese.Lib_WebService.WebServiceManager;

/**
 * Created by kimsavinfo on 16/04/15.
 */
public class CameraPreviewCallback
{
    private static MultiFormatReader multiFormatReader = new MultiFormatReader();
    private static WebServiceManager webServiceManager = new WebServiceManager();

    public static Camera.PreviewCallback recogniseQRCode()
    {
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
                        manageQRCodeResult(result);
                    }
                } catch (NotFoundException e)
                {
                    Log.e("Camera.PreviewCallback - recogniseQRCode", e.toString());
                }
            }
        };
    }

    // Ex :
    // 01110101011100110110010101110010////01110101011100110110010101110010
    // -> toto
    // -> keepcalm
    // Adresse : http://kimsavinfo.fr/qrcheese/index.php?login=toto&password=keepcalm
    private static void manageQRCodeResult(Result _result)
    {
        String[] arguments = (_result.getText()).split("////");
        String login = "";
        String password = "";
        boolean arduinoSignal = false;

        try
        {
            BinaryManager.binaryToASCII(arguments[0]);
            BinaryManager.binaryToASCII(arguments[1]);

            arduinoSignal = webServiceManager.checkUser(login, password);
        }
        catch (Exception e)
        {
            Log.e("Camera.PreviewCallback - manageQRCodeResult", e.toString());


        }
        finally
        {
            // TODO : Arduino = envoyer arduinoSignal
        }
    }
}
