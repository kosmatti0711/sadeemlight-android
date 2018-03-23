package com.sadeemlight.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.config.ConstValue;
import com.sadeemlight.venus_uis.utils.GlobalProfile;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Rajesh Dabhi on 12/8/2016.
 */
public class ServiceHandler {

    static String response = null;
    private OkHttpClient client;
    private RequestBody body;
    private FormBody.Builder bodyBuilder;
    private Request request;
    private Response okResponse;


    public ServiceHandler() {
    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     */
    public String makeServiceCall(String url, int method, List<NameValuePair> params) {
        try {

            HttpParams httpParameters = new BasicHttpParams();
// Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used.
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 5000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == ConstValue.POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                }
                httpResponse = httpClient.execute(httpPost);

            } else if (method == ConstValue.GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String parammsg = "";
        if(params != null) parammsg = params.toString();
        String toastmsg = "url=" + url+ "\nparam=" + parammsg + "\n" + response;
        GlobalProfile.appendResport(toastmsg);

        return response;

    }

    public String makeServiceCallWithToke(String url, int method, List<NameValuePair> params, String access_token) {
        try {

            HttpParams httpParameters = new BasicHttpParams();
// Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used.
            int timeoutConnection = 10000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 10000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == ConstValue.POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                }
                httpPost.setHeader("authorization", "Bearer " + access_token);
                httpResponse = httpClient.execute(httpPost);

            }
            //Get method
            else if (method == ConstValue.GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpGet.setHeader("authorization", "Bearer " + access_token);
                httpResponse = httpClient.execute(httpGet);

            }

            else if (method == ConstValue.DELETE) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpDelete httpDelete = new HttpDelete(url);

                httpDelete.setHeader("authorization", "Bearer " + access_token);
                httpResponse = httpClient.execute(httpDelete);
            }

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String parammsg = "";
        if(params != null) parammsg = params.toString();
        String toastmsg = "url=" + url+ "\nparam=" + parammsg + "\naccesstoken=" + access_token + "\n" + response;
        GlobalProfile.appendResport(toastmsg);

        return response;

    }

    public String makeServiceCallWithTokeOk(String url, int method, List<ModelParamsPair> params, String accessToken) throws IOException {
        response = null;
        try{
            client = new OkHttpClient();
            if (method == ConstValue.POST ){
                if (params != null){
                    bodyBuilder = new FormBody.Builder();
                    for (ModelParamsPair param : params) {
                        bodyBuilder.add(param.getKey(),param.getValue());
                    }
                    body = bodyBuilder.build();
                }
                request = new Request.Builder().url(url)
                        .addHeader("authorization","Bearer " + accessToken)
                        .method("POST",body)
                        .build();
                okResponse = client.newCall(request).execute();

            }else if (method == ConstValue.GET){
                if (params != null){
                    url += "?" + params.get(0).getKey()+"="+params.get(0).getValue();
                }
                request = new Request.Builder().url(url)
                        .addHeader("authorization","Bearer " + accessToken)
                        .method("GET",null).build();
                okResponse = client.newCall(request).execute();
            }else if (method == ConstValue.DELETE){
                if (params != null){
                    url += "?" + params.get(0).getKey()+"="+params.get(0).getValue();
                }
                request = new Request.Builder().url(url)
                        .addHeader("authorization","Bearer " + accessToken)
                        .method("DELETE",null).build();
                okResponse = client.newCall(request).execute();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        String parammsg = "";
        String toastmsg = "url=" + url+ "\nparam=" + parammsg + "\naccesstoken=" + accessToken + "\n" + response;

        if (okResponse != null){
            response = okResponse.body().string();
        }

        return response;

    }
}
