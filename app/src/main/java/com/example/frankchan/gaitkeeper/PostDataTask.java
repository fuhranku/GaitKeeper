package com.example.frankchan.gaitkeeper;

import android.os.AsyncTask;
import android.util.Log;

import com.android.internal.http.multipart.MultipartEntity;

import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import org.apache.http.entity.FileEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by frankchan on 4/29/17.
 */

public class PostDataTask extends AsyncTask<String, Void, Boolean> {
    private Exception exception;

    protected Boolean doInBackground(String... rawData) {
        // try until the file is sent

        try {
            postData(rawData[0], rawData[1], rawData[2]);
            return true;
        } catch (Exception e) {
            this.exception = e;
            System.out.println(e);
            return false;
        }
    }

    protected void onPostExecute(Boolean feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }

    public void postData(String rawData, String id, String endpoint) throws Exception {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httpPost = new HttpPost(endpoint + id);
        httpPost.addHeader("content-type", "text/plain");
        httpPost.setEntity(new StringEntity(rawData));


        StatusLine statusLine = httpclient.execute(httpPost).getStatusLine();
        System.out.println(statusLine.toString());
        int code = statusLine.getStatusCode();

        if (code == 200) {
            System.out.println("good");
        } else {
            throw new Exception("bad");
        }
    }
}
