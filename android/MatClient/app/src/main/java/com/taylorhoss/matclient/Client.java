package com.taylorhoss.matclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
    private static final String TAG = "MatClient";

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
            Log.i(TAG, "creating socket...");
            socket = new Socket(params[0], Integer.parseInt(params[1]));

            // send request through socket
            Log.i(TAG, "sending request through socket...");
            Log.i(TAG, "string sent: " + params[2]);
            Log.i(TAG, "bytes sent: " + params[2].getBytes());
            OutputStream out = socket.getOutputStream();
            out.write(params[2].getBytes());
            out.flush();

            /*
            // Create byte stream to dump read bytes into
            InputStream in = socket.getInputStream();

            int byteRead = 0;

            // Read from input stream. Note: inputStream.read() will block
            // if no data return
            Log.i(TAG, "Reading in response from socket...");
            while (byteRead != -1) {
                byteRead = in.read();
                if (byteRead == 126){
                    byteRead = -1;
                }else {
                    sb.append((char) byteRead);
                }
            }
            */
            File file = new File(Environment.getExternalStorageDirectory(), "database.txt");
            //will need to increase size of byte array if information exceeds 1024 bytes
            byte[] bytes = new byte[1024];
            InputStream in = socket.getInputStream();
            FileOutputStream fOut = new FileOutputStream(file);
            BufferedOutputStream bOut = new BufferedOutputStream(fOut);

            int bytesRead = in.read(bytes, 0, bytes.length);
            bOut.write(bytes, 0, bytesRead);
            bOut.close();

        } catch (UnknownHostException e) {
            this.unknownHostException = e;
            return "Error: unknownHostException";
        } catch (IOException e) {
            this.ioException = e;
            Log.i(TAG, "IOException...");
            return "Error: ioException";
        } finally {
            if (socket != null) {
                try {
                    Log.i(TAG, "closing socket");
                    socket.close();
                } catch (IOException e) {
                    this.ioException = e;
                    Log.i(TAG, "IOException when closing socket...");
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
            Log.i(TAG, "Setting response text...");
            this.textResponse.setText(result);
        }
        this.button.setEnabled(true);
        super.onPostExecute(result);
    }

}
