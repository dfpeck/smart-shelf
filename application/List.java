package com.example.shy16.smartshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by shy16 on 11/29/2017.
 */

public class List extends AppCompatActivity {

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

        Button a = (Button) findViewById(R.id.button);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (List.this, Settings.class));
            }
        });

        b_read = (Button) findViewById(R.id.ShowText);

        tv_text = (TextView) findViewById(R.id.tv_text);

        b_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = "";
                try {
                    InputStream ls = getAssets().open("file.txt");
                    int size = ls.available();
                    byte[] buffer = new byte[size];
                    ls.read(buffer);
                    ls.close();
                    text = new String(buffer);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                tv_text.setText(text);
            }
        });

        ListView ll=(ListView) findViewById(R.id.listView);
        String[] array = { "Item1, Item2, Item3"};
        ArrayList<String> lst = new ArrayList<String>(Arrays.asList(array));
        lst.add("Item4");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, lst);

        ll.setAdapter(adapter);
        ll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                TextView txt = (TextView) arg1;
                System.out.println(txt.getText().toString());
            }
        });

    }

    Button b_read;

    TextView tv_text;
}