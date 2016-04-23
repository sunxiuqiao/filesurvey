package com.example.yanhejin.myapplication.ChildActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.yanhejin.myapplication.R;

public class newuser extends AppCompatActivity {

    EditText username;
    EditText password;
    RadioButton admin;
    RadioButton ordiary;
    Button createuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        username= (EditText) findViewById(R.id.username);
        password= (EditText) findViewById(R.id.password);
        admin= (RadioButton) findViewById(R.id.chooseadmin);
        ordiary= (RadioButton) findViewById(R.id.chooseordial);
        createuser= (Button) findViewById(R.id.createuser);
    }

}
