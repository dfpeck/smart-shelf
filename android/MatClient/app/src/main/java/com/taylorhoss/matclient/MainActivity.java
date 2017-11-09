package com.taylorhoss.matclient;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView request, response;
    EditText address, port;
    Button buttonConnect, buttonClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address = (EditText) findViewById(R.id.addressEditText);
        port = (EditText) findViewById(R.id.portEditText);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonClear = (Button) findViewById(R.id.clearButton);
        response = (TextView) findViewById(R.id.responseTextView);
        request = (TextView) findViewById(R.id.requestTextView);

        Log.i("MainActivity", Environment.getExternalStorageDirectory().getAbsolutePath());

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonConnect.setEnabled(false);
                Client myClient = new Client(MainActivity.this, response, buttonConnect);

                request.setText("Database");

                myClient.execute(
                        address.getText().toString(),
                        port.getText().toString(),
                        "Database~");
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response.setText("");
            }
        });
    }
}
