package com.example.shy16.smartshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shy16 on 11/29/2017.
 */

public class List extends AppCompatActivity{

    private static final String TAG = "List";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        Log.d(TAG, "onCreate: Starting.");

        Button main = (Button) findViewById(R.id.back);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked main.");

                Intent intent = new Intent(List.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    Button b_read;

    TextView tv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            b_read = (Button) findViewById(R.id.ShowText);

            tv_text = (TextView) findViewById (R.id.tv_text);

            b_read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text="";
                    try{
                        InputStream ls = getAssets().open("file.txt");
                        int size=ls.available();
                        byte[] buffer = new byte[size];
                        ls.read(buffer);
                        ls.close();
                        text=new String(buffer);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    tv_text.setText(text);
                }
            });
    }
