package com.taylorhoss.matclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.R.attr.port;

/**
 * Created by Hoss on 10/26/2017.
 * Base code courtesy of:
 * http://androidsrc.net/android-client-server-using-sockets-client-implementation/
 */

public class Client extends AsyncTask<String, String, String> {

    Activity activity;
    Button button;
    TextView textResponse;
    IOException ioException;
    UnknownHostException unknownHostException;

    Client(Activity activity, TextView textView, Button button) {
        super();
        this.activity = activity;
        this.textResponse = textView;
        this.button = button;
        this.ioException = null;
        this.unknownHostException = null;
    }

    @Override
    protected String doInBackground(String... params) {

        Socket socket = null;
        StringBuilder sb = new StringBuilder();

        try {
            socket = new Socket(params[0], Integer.parseInt(params[1]));

            // send request through socket
            OutputStream out = socket.getOutputStream();
            out.write(params[2].getBytes());

            // Create byte stream to dump read bytes into
            InputStream in = socket.getInputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;

            // Read from input stream. Note: inputStream.read() will block
            // if no data return
            while ((bytesRead = in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, bytesRead));
            }

        } catch (UnknownHostException e) {
            this.unknownHostException = e;
            return "Error: unknownHostException";
        } catch (IOException e) {
            this.ioException = e;
            return "Error: ioException";
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    this.ioException = e;
                    return "Error: ioException";
                }
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (this.ioException != null) {
            new AlertDialog.Builder(this.activity)
                    .setTitle("An error occurred")
                    .setMessage(this.ioException.toString())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else if (this.unknownHostException != null) {
            new AlertDialog.Builder(this.activity)
                    .setTitle("An error occurred")
                    .setMessage(this.unknownHostException.toString())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            this.textResponse.setText(result);
        }
        this.button.setEnabled(true);
        super.onPostExecute(result);
    }

}
