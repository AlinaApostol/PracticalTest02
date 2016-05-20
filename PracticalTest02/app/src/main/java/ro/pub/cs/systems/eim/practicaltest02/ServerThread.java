package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

/**
 * Created by student on 5/20/16.
 */
public class ServerThread extends Thread{
    private int port = 0;
    private ServerSocket serverSocket = null;

    private String data = null;

    public ServerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e("", "An exception has occurred: " + ioException.getMessage());
        }
        this.data = "";
    }

    public void setPort(int port) {

        this.port = port;
    }

    public int getPort() {

        return port;
    }

    public void setServerSocker(ServerSocket serverSocket) {

        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {

        return serverSocket;
    }

    public synchronized void setData(String data) {
        this.data = data;
    }


    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i("", "[SERVER] Waiting for a connection...");
                Socket socket = serverSocket.accept();
                Log.i("", "[SERVER] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (ClientProtocolException clientProtocolException) {
            Log.e("", "An exception has occurred: " + clientProtocolException.getMessage());

        } catch (IOException ioException) {
            Log.e("", "An exception has occurred: " + ioException.getMessage());

        }
    }

    public void stopThread() {
        if (serverSocket != null) {
            interrupt();
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e("", "An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
