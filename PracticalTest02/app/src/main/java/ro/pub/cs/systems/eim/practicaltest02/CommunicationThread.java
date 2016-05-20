package ro.pub.cs.systems.eim.practicaltest02;

import android.provider.DocumentsContract;
import android.provider.SyncStateContract;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by student on 5/20/16.
 */
public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader != null && printWriter != null) {
                    Log.i("", "[COMMUNICATION THREAD] Waiting for parameters from client !");
                    String url = bufferedReader.readLine();

                    if (url != null && !url.contains("bad")) {
                        Log.i("", "[COMMUNICATION THREAD] Getting the information from the webservice...");
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("https://" + url);
                        List params = new ArrayList();
                        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        httpPost.setEntity(urlEncodedFormEntity);
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                        if (pageSourceCode != null) {
                            serverThread.setData(pageSourceCode);
                        }

                        String result = pageSourceCode;
                        printWriter.println(result);
                        printWriter.flush();

                    } else {
                        String result = "Rejected";
                        printWriter.println(result);
                        printWriter.flush();
                        Log.e("", "[COMMUNICATION THREAD] Error receiving parameters from client!");
                    }
                } else {
                    Log.e("", "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
                }
                socket.close();
            } catch (IOException ioException) {
                Log.e("", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            }
        } else {
            Log.e("", "[COMMUNICATION THREAD] Socket is null!");
        }
    }

}
