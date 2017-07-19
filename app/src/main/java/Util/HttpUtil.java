package Util;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by youker on 2017/7/8 0008.
 */

public class HttpUtil {
    public static String sendGetRequest(String getUrl){
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    //.url("http://api.yeelink.net/v1.0/device/356403/sensor/408048/datapoint/")//yeelink温度传感器
                    //oneNet接口
                    .url(getUrl)
                    //添加api-key
                    .addHeader("api-key", "OymFBYqQHPxKCO=8m0QC8KHyDp0=")
                    .build();
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            return responseData;
        }
        catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static String sendPostRequest(String postUrl, String jsonString){
        try{
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
                    //oneNet接口
                    .url(postUrl)
                    .post(requestBody)
                    //添加api-key
                    .addHeader("api-key", "OymFBYqQHPxKCO=8m0QC8KHyDp0=")
                    .build();
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            return responseData;
        }
        catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static String sendGetRequest_yee(String getUrl){
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(getUrl)
                    //添加U-Apikey
                    .addHeader("U-ApiKey", "c1bc15b6d0adec562864af9829102870")
                    .build();
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            return responseData;
        }
        catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static String sendPostRequest_yee(String postUrl, String value){
        try{
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, "{\"value\":"+ value +"}");
            Request request = new Request.Builder()
                    .url(postUrl)
                    .addHeader("U-ApiKey", "c1bc15b6d0adec562864af9829102870")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            return responseData;
        }
        catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }
}
