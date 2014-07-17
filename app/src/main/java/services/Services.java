package services;

import com.sheikhu.qrscanner.R;

import org.json.JSONObject;

import lib.RestClient;

/**
 * Created by Sheikhu on 17/07/2014.
 */
public class Services {

    public static String url;

    public static JSONObject auth(String identifier, String password)
    {
        RestClient client = new RestClient(url);
        String response ;
        try {
            client.AddParam("identifier", identifier);
            client.AddParam("password", password);
            client.Execute(RestClient.RequestMethod.POST);
            response = client.getResponse();
            return new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


}
