package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by student on 5/20/16.
 */
public class ClientThread extends Thread {

    private String address;
    private int port;
    private String url;
    private TextView weatherForecastTextView;

    private Socket socket;

    public ClientThread(
            String address,
            int port,
            String url,
            TextView weatherForecastTextView) {
        this.address = address;
        this.port = port;
        this.url = url;
        this.weatherForecastTextView = weatherForecastTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e("", "[CLIENT THREAD] Could not create socket!");
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader != null && printWriter != null) {
                printWriter.println(url);
                printWriter.flush();
                String weatherInformation;
                while ((weatherInformation = bufferedReader.readLine()) != null) {
                    final String finalizedWeatherInformation = weatherInformation;
                    weatherForecastTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            weatherForecastTextView.append(finalizedWeatherInformation + "\n");
                        }
                    });
                }
            } else {
                Log.e("", "[CLIENT THREAD] BufferedReader / PrintWriter are null!");
            }
            socket.close();
        } catch (IOException ioException) {
            Log.e("", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());

        }
    }

}
