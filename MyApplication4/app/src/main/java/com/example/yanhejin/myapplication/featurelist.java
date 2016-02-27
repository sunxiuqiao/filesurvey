package com.example.yanhejin.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yanhejin.myapplication.FeatureView.dlselect;
import com.example.yanhejin.myapplication.FeatureView.dlzjselect;
import com.example.yanhejin.myapplication.FeatureView.dmselect;
import com.example.yanhejin.myapplication.FeatureView.fwselect;
import com.example.yanhejin.myapplication.FeatureView.gxselect;
import com.example.yanhejin.myapplication.FeatureView.jjxselect;
import com.example.yanhejin.myapplication.FeatureView.sxselect;
import com.example.yanhejin.myapplication.FeatureView.wzselect;
import com.example.yanhejin.myapplication.FeatureView.zbselect;

public class featurelist extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featurelist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView= (ListView) findViewById(R.id.featureselect);
        String[] features={"居民地","道路","水系","植被","管线","地貌土质","境界线","地理注记","文字注记"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(featurelist.this,R.layout.features,features);
        featureadapter adapter1=new featureadapter(featurelist.this,features);
        listView.setAdapter(adapter1);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView= (TextView) view.findViewById(R.id.featuresname);
                switch (position) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(featurelist.this, fwselect.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent dlintent = new Intent();
                        dlintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dlintent.setClass(featurelist.this, dlselect.class);
                        startActivity(dlintent);
                        break;
                    case 2:
                        Intent sxintent = new Intent();
                        sxintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sxintent.setClass(featurelist.this, sxselect.class);
                        startActivity(sxintent);
                        break;
                    case 3:
                        Intent zbintent = new Intent();
                        zbintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        zbintent.setClass(featurelist.this, zbselect.class);
                        startActivity(zbintent);
                        break;
                    case 4:
                        Intent gxintent = new Intent();
                        gxintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        gxintent.setClass(featurelist.this, gxselect.class);
                        startActivity(gxintent);
                        break;
                    case 5:
                        Intent tzdmintent = new Intent();
                        tzdmintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        tzdmintent.setClass(featurelist.this, dmselect.class);
                        startActivity(tzdmintent);
                        break;
                    case 6:
                        Intent jjxintent=new Intent();
                        jjxintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        jjxintent.setClass(featurelist.this, jjxselect.class);
                        startActivity(jjxintent);
                        break;
                    case 7:
                        Intent dlzjintent = new Intent();
                        dlzjintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dlzjintent.setClass(featurelist.this, dlzjselect.class);
                        startActivity(dlzjintent);
                        break;
                    case 8:
                        Intent wzzjintent = new Intent();
                        wzzjintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        wzzjintent.setClass(featurelist.this, wzselect.class);
                        startActivity(wzzjintent);
                        break;


                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

}
