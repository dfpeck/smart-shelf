package com.taylorhoss.matserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Server server;
    TextView infoip, msg;
    boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        started = false;
    }

    public void onButtonTap(View v){
        if(started == true){
            Toast myToast = Toast.makeText(getApplicationContext(), "Server already started.", Toast.LENGTH_LONG);
            myToast.show();
        }else{
            server = new Server(this);
            started = true;
            infoip.setText(server.getIpAddress() + ":" + server.getPort());

            Toast myToast = Toast.makeText(getApplicationContext(), "Server started.", Toast.LENGTH_LONG);
            myToast.show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }
}
