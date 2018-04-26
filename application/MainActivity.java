package com.example.shy16.expandablelistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import netNode.NetUI;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start of network code
        String ip;
        String str;

        NetUI netUI = new NetUI(ip);
        netUI.start();

        // When want a record
        netUI.SendString("record 1");

        //split the string here

        Boolean flag = false;
        do {
            str = netUI.pop();
            if (str[0] == "record") {
                weight[str[1]] = str[2];
                text[str[1]] = str[3];
                flag = true;
            }
            else if (str[0] = "add") {
                //add new item
            }
        } while (flag == false);

        //End of network code

        listView = (ExpandableListView)findViewById(R.id.lvExp);
        initData();
        listAdapter = new ExpandableListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(listAdapter);

    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("First Item");
        listDataHeader.add("Second item");

        List<String> item1 = new ArrayList<>();
        item1.add("This is Expandable ListView");

        List<String> item2 = new ArrayList<>();
        item2.add("Item 2 expanded ListView");
        item2.add("Subpoint 2");
        item2.add("subpoint 3");

        listHash.put(listDataHeader.get(0),item1);
        listHash.put(listDataHeader.get(1),item2);

    }
}
