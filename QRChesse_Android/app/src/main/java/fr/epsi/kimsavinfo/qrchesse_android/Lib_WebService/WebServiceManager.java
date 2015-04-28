package fr.epsi.kimsavinfo.qrchesse_android.Lib_WebService;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.epsi.kimsavinfo.qrchesse_android.MainActivity;


/**
 * Created by kimsavinfo on 17/04/15.
 */
public class WebServiceManager
{
    // http://kimsavinfo.fr/qrcheese/index.php?login=toto&password=keepcalm
    private String uri;
    private String login;
    private String password;

    public WebServiceManager()
    {
        uri =  "http://kimsavinfo.fr/qrcheese/index.php?";
        resetUser();
    }

    public void resetUser()
    {
        login= "";
        password= "";
    }

    private String construireURL()
    {
        return uri + "login=" + login + "&password=" + password;
    }

    public boolean checkUser(String _login, String _password)
    {
        boolean isUserIdentified = false;

        login = _login;
        password = _password;
        String uri = construireURL();
        String readJSON = getJSON(uri);

        if(readJSON.equals("[{\"reponse\":\"ok\"}]"))
        {
            isUserIdentified = true;
        }
        resetUser();

        return isUserIdentified;
    }

    public String getJSON(String _uri)
    {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(_uri);

        try
        {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if(statusCode == 200)
            {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line = reader.readLine()) != null)
                {
                    builder.append(line);
                }
            }
            else
            {
                Log.e(MainActivity.class.toString(),"Failed to get JSON object");
            }
        }
        catch(ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return builder.toString();
    }

}
