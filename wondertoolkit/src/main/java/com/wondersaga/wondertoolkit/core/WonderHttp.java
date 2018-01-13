package com.wondersaga.wondertoolkit.core;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ProphecyX on 8/30/2017 AD.
 */

public class WonderHttp {

    // ---------------------------------------------------------------------------
    // ---------------------------------------------------------------------------

    public static String GET = "GET";
    public static String POST = "POST";
    public static String PUT = "PUT";
    public static String DELETE = "DELETE";

    public static String ERROR_CODE_UNDEFINED = "undefined";
    public static String ERROR_CODE_NO_INTERNET = "no_internet";
    public static String ERROR_CODE_UNKNOWN_HOST = "unknown_host";
    public static String ERROR_CODE_SOCKET_TIMEOUT = "socket_time_out";

    public static String baseUrl = "";

    public enum ContentType {
        JSON,
        X_WWW_FORM_URLENCODED,
    }

    public static class WonderHttpRequest {
        public String apiName;
        public String method;
        public String customBaseUrl;
        public JSONObject header = new JSONObject();
        public JSONObject data = new JSONObject();
        public ContentType contentType = ContentType.JSON;
    }

    public static class WonderHttpResponse {
        public String apiName;
        public String method;
        public JSONObject data;
        public WonderDef.WonderError error;
    }

    // ---------------------------------------------------------------------------
    // ---------------------------------------------------------------------------
    // Final vars
    private static final String TAG = WonderHttp.class.getSimpleName();

    // ---------------------------------------------------------------------------
    // ---------------------------------------------------------------------------
    // Success or failure response are returned through this
    public interface CompleteListener {

        void onCompleted(WonderHttpResponse response);
    }

    // ---------------------------------------------------------------------------
    // ---------------------------------------------------------------------------
    // Client object set parameter using this listener, this include exception handler
    public interface DataSetter {

        JSONObject onAddParams(JSONObject params) throws JSONException;
    }

    // ---------------------------------------------------------------------------
    // ---------------------------------------------------------------------------
    private static String composeRequestBodyString(
            final WonderHttpRequest wonderHttpRequest,
            JSONObject data) throws JSONException {

        String bodyString = "";

        if (wonderHttpRequest.contentType == ContentType.JSON)
            bodyString = data.toString();
        else if (wonderHttpRequest.contentType == ContentType.X_WWW_FORM_URLENCODED) {
            // Create string builder
            StringBuilder sb = new StringBuilder();
            // Compose params to string builder
            int i = 0;
            Iterator<String> it = data.keys();
            for(Iterator<String> iter = it; iter.hasNext(); ++i) {
                String prefix = "";
                if (i>0)
                    prefix = "&";
                String key = iter.next();
                String value = (String)data.get(key);

                // Convert to URL encode
                try {
                    value = URLEncoder.encode(value,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    WonderLog.logError(e);
                }

                sb.append(prefix + key + "=" + value);
            }
            // Set to body string
            bodyString = sb.toString();

            WonderLog.logDebug(TAG, "Body in " + ContentType.X_WWW_FORM_URLENCODED + ": " + bodyString);
        }

        return bodyString;
    }


    // ---------------------------------------------------------------------------
    // ---------------------------------------------------------------------------
    // Make and handle request using this
    public static void requestAPI(final WonderHttpRequest wonderHttpRequest,
                                  final CompleteListener completeListener) {

        // ---------------------------------------------------------------------------
        // Handle custom URL. If no custom URL, default URL is used.

        final String urlString;

        // Use custom URL
        if (wonderHttpRequest.customBaseUrl != null &&
                !wonderHttpRequest.customBaseUrl.isEmpty()) {
            urlString = wonderHttpRequest.customBaseUrl + wonderHttpRequest.apiName;
        }
        // Use default URL
        else {
            urlString = baseUrl + wonderHttpRequest.apiName;
        }

        WonderLog.logDebug(TAG, wonderHttpRequest.method + ": " + urlString);

        if (wonderHttpRequest.apiName == null)
            throw new RuntimeException("ApiName is null");

        // ---------------------------------------------------------------------------
        // Make request in sub thread
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try {

                    // ---------------------------------------------------------------------------
                    OkHttpClient client = new OkHttpClient();

                    // ---------------------------------------------------------------------------
                    // Build request
                    Request.Builder requestBuilder = new Request.Builder();

                    // ---------------------------------------------------------------------------
                    // GET
                    //     Compose URL string from data
                    if (wonderHttpRequest.method.equals(GET)) {

                        requestBuilder.url(composeUrlParams(urlString, wonderHttpRequest.data));
                    }
                    else {

                        // ---------------------------------------------------------------------------
                        // Body string is composed from configurations
                        String requestBodyString = composeRequestBodyString(wonderHttpRequest, wonderHttpRequest.data);

                        // ---------------------------------------------------------------------------
                        // Compose media type from content type
                        final String jsonContentType = "application/json";
                        final String formUrlEncodeContentType = "application/x-www-form-urlencoded";

                        String contentType;

                        if (wonderHttpRequest.contentType == ContentType.X_WWW_FORM_URLENCODED)
                            contentType = formUrlEncodeContentType;
                        else
                            contentType = jsonContentType;

                        final MediaType mediaType = MediaType.parse(contentType + "; charset=utf-8");

                        // Compose request body
                        RequestBody body = RequestBody.create(mediaType, requestBodyString);

                        if (wonderHttpRequest.method.equals(POST))
                            requestBuilder.url(urlString).post(body);
                        else if (wonderHttpRequest.method.equals(PUT))
                            requestBuilder.url(urlString).put(body);
                        else if (wonderHttpRequest.method.equals(DELETE))
                            requestBuilder.url(urlString).delete(body);
                        else
                            WonderLog.logError(TAG, "Request with unsupported method");
                    }

                    // ---------------------------------------------------------------------------
                    // ---------------------------------------------------------------------------
                    // Add header
                    Iterator<String> keys = wonderHttpRequest.header.keys();
                    for (int i = 0; keys.hasNext(); ++i) {
                        String key = keys.next();
                        requestBuilder.addHeader(key, (String) wonderHttpRequest.header.get(key));
                    }

                    WonderLog.logDebug(TAG, "Header: " + wonderHttpRequest.header.toString());

                    // Unused header (Notes)
                    //requestBuilder.addHeader("X-Requested-With", "XMLHttpRequest");
                    //requestBuilder.addHeader("Accept", "*/*");
                    //requestBuilder.addHeader("Accept-Encoding", "gzip, deflate");
                    //requestBuilder.addHeader("Accept-Language", ":en-US,en;q=0.8,th;q=0.6,ko;q=0.4");
                    //requestBuilder.addHeader("Content-Type", "application/json");
                    //requestBuilder.addHeader("Host", "chicchatapi20160620060034.azurewebsites.net");
                    //requestBuilder.addHeader("Referer", "http://chicchatapi20160620060034.azurewebsites.net/");
                    //requestBuilder.addHeader("Connection", "keep-alive");
                    //requestBuilder.addHeader("Content-Length", "64");
                    //requestBuilder.addHeader("Origin", "null");

                    // ---------------------------------------------------------------------------
                    // ---------------------------------------------------------------------------
                    // Build
                    Request request = requestBuilder.build();

                    // ---------------------------------------------------------------------------
                    // ---------------------------------------------------------------------------
                    // Check request validation
                    if (request == null) {

                        WonderLog.logError(TAG, "Null request! Is it wrong method?");

                        if (completeListener != null) {
                            WonderDef.WonderError error = new WonderDef.WonderError();
                            error.code = "null_request";
                            error.message = "Null request";
                            onFailureOnMainThread(wonderHttpRequest, error, completeListener);
                        }

                        return;
                    }

                    // ---------------------------------------------------------------------------
                    // ---------------------------------------------------------------------------
                    // ---------------------------------------------------------------------------
                    // ---------------------------------------------------------------------------
                    // Response
                    Response response;

                    WonderLog.logDebug(TAG, "Requesting: " + wonderHttpRequest.apiName + " please wait...");

                    // ---------------------------------------------------------------------------
                    // Make request
                    response = client.newCall(request).execute();

                    // Fixme: Consider with enqueue request
                    /*
                    okhttp3.Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                        }
                    });
                    */

                    // ---------------------------------------------------------------------------
                    // Got it! The server is reachable, get response string
                    String responseString = response.body().string();

                    WonderLog.logDebug(TAG, "Response(" + response.code() + "): ApiName: "
                            + wonderHttpRequest.apiName + " Data: " + responseString);

                    // ---------------------------------------------------------------------------
                    // ---------------------------------------------------------------------------
                    // Handle error
                    if (completeListener != null) {

                        try {

                            // Parse response data
                            JSONObject responseData = new JSONObject(responseString);

                            // Check error from error object
                            if (responseData.has("error")) {

                                WonderDef.WonderError error = new WonderDef.WonderError();

                                JSONObject errorObject = responseData.getJSONObject("error");
                                error.code = errorObject.getString("code");
                                error.message = errorObject.getString("message");

                                onFailureOnMainThread(wonderHttpRequest, error, completeListener);
                            }
                            else {

                                if (!responseData.has("data")) {

                                    responseData = new JSONObject();
                                    responseData.put("data", new JSONObject());
                                }

                                onSuccessOnMainThread(wonderHttpRequest, responseData.getJSONObject("data"), completeListener);
                            }
                        }
                        // ---------------------------------------------------------------------------
                        // ---------------------------------------------------------------------------
                        // Handle error here as well, in case exception is risen
                        catch (Exception e) {

                            // Exception is risen, log error here
                            WonderLog.logError(TAG, e);

                            // False negative case, error occur but response code
                            int responseCode = response.code();
                            if (responseCode == 200) {

                                WonderLog.logWarning(TAG, "False negative! Error occurred but response code is 200.");

                                // Return empty JSON object
                                JSONObject resData = new JSONObject();
                                onSuccessOnMainThread(wonderHttpRequest, resData, completeListener);
                            }
                            else {
                                onFailureOnMainThread(wonderHttpRequest, new WonderDef.WonderError(), completeListener);
                            }
                        }

                    }
                }
                catch (Exception e)
                {
                    WonderLog.logError(TAG, e);

                    String code = ERROR_CODE_UNDEFINED;
                    String message = "Undefined error!";

                    if (e.getClass() == SocketTimeoutException.class) {

                        code = ERROR_CODE_SOCKET_TIMEOUT;
                        message = "Socket time out";
                    }
                    else if (e.getClass() == ConnectException.class) {

                        code = ERROR_CODE_NO_INTERNET;
                        message = "No Internet connection";
                    }
                    else if (e.getClass() == UnknownHostException.class) {

                        code = ERROR_CODE_UNKNOWN_HOST;
                        message = "Unknown host";
                    }

                    if (completeListener != null) {

                        WonderDef.WonderError error = new WonderDef.WonderError();

                        error.code = code;
                        error.message = message;

                        onFailureOnMainThread(wonderHttpRequest, error, completeListener);
                    }
                }
            }
        });

        thread.start();
    }

    protected static void onSuccessOnMainThread(
            final WonderHttpRequest wonderHttpRequest,
            final JSONObject responseObject,
            final CompleteListener completeListener) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                // Compose response object
                WonderHttpResponse response = new WonderHttpResponse();
                response.apiName = wonderHttpRequest.apiName;
                response.method = wonderHttpRequest.method;
                response.data = responseObject;
                response.error = null;

                WonderLog.logDebug(TAG, response.apiName + " requests success: " + response.data.toString());

                completeListener.onCompleted(response);
            }
        });
    }

    protected static void onFailureOnMainThread(
            final WonderHttpRequest wonderHttpRequest,
            final WonderDef.WonderError error,
            final CompleteListener completeListener) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                // Compose response object
                WonderHttpResponse response = new WonderHttpResponse();
                response.apiName = wonderHttpRequest.apiName;
                response.method = wonderHttpRequest.method;
                response.data = null;
                response.error = error;

                WonderLog.logDebug(TAG, response.apiName + " requests error: code: " +
                        response.error.code + " message: " + response.error.message);

                completeListener.onCompleted(response);
            }
        });
    }

    // ---------------------------------------------------------------------------
    // ---------------------------------------------------------------------------
    // For REST: GET method, compose params and attach it with URL
    protected static String composeUrlParams(String baseUrl, JSONObject data) {

        if (data == null)
            return baseUrl;

        try {
            // Compose url with params
            StringBuilder sb = new StringBuilder();
            Iterator keys = data.keys();
            for (int i = 0; keys.hasNext(); ++i) {
                String key = (String) keys.next();
                if (i == 0)
                    sb.append("?");
                else
                    sb.append("&");
                sb.append(key);
                sb.append("=" + data.getString(key));
            }

            return baseUrl + sb.toString();
        }
        catch (Exception e) {

            WonderLog.logError(TAG, e);
            return baseUrl;
        }
    }
}
