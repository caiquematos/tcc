package Tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by caique on 01/08/15.
 */
public class Methods {

    public static JSONObject POST(Context context, JSONObject rawJson, String UrlEnd) throws JSONException {
        URL url;
        HttpURLConnection connection;

        //some android s fail while trying to post: reason for this
        for(int i = 0; i <= Constants.MAX_RETRIES; i++) {
            try{
                Log.e("Response Stream: ", "trying " + i);
                Log.e("Json on Post: ", rawJson.toString());
                url = new URL(Constants.MAIN_URL + UrlEnd);
                String param="json=" + URLEncoder.encode(rawJson.toString(), "UTF-8");
                Log.e("Json on Post: ", param.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setFixedLengthStreamingMode(param.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(param);
                out.close();
                String response = "";
                Scanner inStream = new Scanner(connection.getInputStream());
                while (inStream.hasNextLine()){
                    response += (inStream.nextLine());
                }
                Log.e("Response Stream: ", response);
                return new JSONObject(response);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
