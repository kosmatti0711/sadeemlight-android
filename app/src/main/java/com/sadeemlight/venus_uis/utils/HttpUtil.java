package com.sadeemlight.venus_uis.utils;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

public class HttpUtil
{
    public static final int TIME_OUT = 30000;
    public static AsyncHttpClient asynClient = new AsyncHttpClient();
    public static SyncHttpClient syncClient = new SyncHttpClient();

    static
    {
        asynClient.setTimeout(TIME_OUT);
        //syncClient.setTimeout(TIME_OUT);
        //syncClient.setTimeout(TIME_OUT);
        asynClient.setConnectTimeout(TIME_OUT);
        asynClient.setResponseTimeout(TIME_OUT);
        asynClient.setMaxRetriesAndTimeout(3, 1000);
    }

    public static AsyncHttpClient getAsynClient() {
        return asynClient;
    }
    public static SyncHttpClient getSycnClient() {
        return syncClient;
    }

    public static void get(String paramString, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {
        asynClient.get(paramString, paramAsyncHttpResponseHandler);
    }

    public static void get(String paramString, BinaryHttpResponseHandler paramBinaryHttpResponseHandler) {
        asynClient.get(paramString, paramBinaryHttpResponseHandler);
    }

    public static void get(String paramString, JsonHttpResponseHandler paramJsonHttpResponseHandler) {
        asynClient.get(paramString, paramJsonHttpResponseHandler);
    }

    public static void get(String paramString, RequestParams paramRequestParams, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {
        asynClient.get(paramString, paramRequestParams, paramAsyncHttpResponseHandler);
    }

    public static void get(String paramString, RequestParams paramRequestParams, JsonHttpResponseHandler paramJsonHttpResponseHandler) {
        asynClient.get(paramString, paramRequestParams, paramJsonHttpResponseHandler);
    }

    public static void post(String paramString, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {
        asynClient.post(paramString, paramAsyncHttpResponseHandler);
    }

    public static void post(String paramString, BinaryHttpResponseHandler paramBinaryHttpResponseHandler) {
        asynClient.post(paramString, paramBinaryHttpResponseHandler);
    }

    public static void post(String paramString, JsonHttpResponseHandler paramJsonHttpResponseHandler) {
        asynClient.post(paramString, paramJsonHttpResponseHandler);
    }

    public static void post(String paramString, RequestParams paramRequestParams, AsyncHttpResponseHandler paramAsyncHttpResponseHandler)
    {
        asynClient.post(paramString, paramRequestParams, paramAsyncHttpResponseHandler);
    }

    public static void post(String paramString, RequestParams paramRequestParams, JsonHttpResponseHandler paramJsonHttpResponseHandler) {
        asynClient.post(paramString, paramRequestParams, paramJsonHttpResponseHandler);
    }

    public static void syncPost(String url, RequestParams paramRequestParams, ResponseHandlerInterface response)
    {
        syncClient.post(url, paramRequestParams, response);
    }
}

/* Location:           D:\Workspace\ReverseTool\classes_dex2jar.jar
 * Qualified Name:     com.redorange.motutv1.utils.HttpUtil
 * JD-Core Version:    0.6.0
 */