package com.example.yanhejin.myapplication.FeatureView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.yanhejin.myapplication.Database.CreateSurveyDB;
import com.example.yanhejin.myapplication.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class wzselect extends AppCompatActivity {


    ListView wzlistview;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    SimpleCursorAdapter adapter;
    CreateSurveyDB wzattdb =new CreateSurveyDB(this,dbpath + "/" + attributedb, null,2);
    CreateSurveyDB wzspatdb =new CreateSurveyDB(this,dbpath + "/" + spatialdb, null,2);
    String sqla="select ID as _id,YSDM,YSName,YSType,ZJTIME,BZ from WZBZData order by ID desc";
    String sqls="select ID as _id,LinkAID,x,y from WZBZData order by ID desc";
    Cursor wzCursor;
    int wzid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wzselect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wzlistview= (ListView) findViewById(R.id.wzlistview);
        wzCursor=aexecQuery(sqla,null);
        adapter=new SimpleCursorAdapter(wzselect.this,R.layout.selectwz,wzCursor,new String[]{"YSDM","YSName","YSType"},
                new int[]{R.id.wzlinkid,R.id.wzname,R.id.wztype}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        wzlistview.setAdapter(adapter);
        wzCursor.close();
        wzlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(wzselect.this);
                builder.setTitle("选择操作");
                final Cursor getid= (Cursor) adapter.getItem(position);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wzid=getid.getInt(0);
                        aexecDelete(wzid);
                        getid.close();
                        wzCursor=aexecQuery(sqla, null);
                        adapter.changeCursor(wzCursor);
                        wzCursor.close();
                    }
                });
                builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA);
                        Date currentdate = new Date(System.currentTimeMillis());
                        final String zhujitime = format.format(currentdate);
                        int dm=getid.getInt(1);
                        String name=getid.getString(2);
                        String type=getid.getString(3);
                        final String bz=getid.getString(5);
                        View textview=getLayoutInflater().inflate(R.layout.biaozhu, null);
                        final EditText ysdmtext= (EditText) textview.findViewById(R.id.featureDM);
                        final EditText text= (EditText) textview.findViewById(R.id.zjmc);
                        final EditText yslxtext= (EditText) textview.findViewById(R.id.ftName);
                        final EditText bztext= (EditText) textview.findViewById(R.id.bz);
                        ysdmtext.setText(String.valueOf(dm));
                        text.setText(name);
                        yslxtext.setText(type);
                        bztext.setText(bz);
                        AlertDialog.Builder modify=new AlertDialog.Builder(wzselect.this);
                        modify.setView(textview);
                        modify.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String wzdm=ysdmtext.getText().toString();
                                String wzname=text.getText().toString();
                                String wztype=yslxtext.getText().toString();
                                String wzbz=bztext.getText().toString();
                                SQLiteDatabase database=wzattdb.getReadableDatabase();
                                database.execSQL("update WZBZData set YSDM=?,YSName=?,YSType=?,ZJTIME=?,BZ=? where ID=?",
                                        new Object[]{wzdm,wzname,wztype,zhujitime,wzbz,wzid});
                                database.close();
                                wzCursor=aexecQuery(sqla,null);
                                adapter.changeCursor(wzCursor);
                                wzCursor.close();
                            }
                        });
                        modify.create().show();
                    }
                });
                builder.create().show();
            }
        });

    }

    public Cursor aexecQuery(String sql,String[] params){
        SQLiteDatabase adb= wzattdb.getReadableDatabase();
        return adb.rawQuery(sql,params);
    }

    public Cursor sexecQuery(String sql,String[] params){
        SQLiteDatabase sdb=wzspatdb.getReadableDatabase();
        return sdb.rawQuery(sql,params);
    }

    public long aexecInsert(ContentValues values){
        SQLiteDatabase adb= wzattdb.getReadableDatabase();
        long linkid=adb.insert("WZBZData", null, values);
        adb.close();
        return linkid;
    }

    public long bexecInsert(ContentValues values){
        SQLiteDatabase sdb=wzspatdb.getReadableDatabase();
        long linkid=sdb.insert("WZBZData", null, values);
        sdb.close();
        return linkid;
    }

    public void aexecDelete(int LinkID){
        SQLiteDatabase adb= wzattdb.getReadableDatabase();
        adb.delete("WZBZData","LinkID=?",new String[]{String.valueOf(LinkID)});
        adb.close();
    }

    public void sexecDelete(int LinkID){
        SQLiteDatabase sdb=wzspatdb.getReadableDatabase();
        sdb.delete("WZBZData","LinkAID=?",new String[]{String.valueOf(LinkID)});
        sdb.close();
    }

    public void aexecUpdate(ContentValues values,String id){
        SQLiteDatabase adb= wzattdb.getReadableDatabase();
        adb.update("WZBZData", values, "LinkID=?", new String[]{id});
        adb.close();
    }

    public void sexecUpdate(ContentValues values,String id){
        SQLiteDatabase sdb=wzspatdb.getReadableDatabase();
        sdb.update("WZBZData",values,"LinkAID=?",new String[]{id});
        sdb.close();
    }
}
