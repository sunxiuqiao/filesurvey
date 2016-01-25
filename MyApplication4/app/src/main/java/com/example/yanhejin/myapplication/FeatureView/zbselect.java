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

public class zbselect extends AppCompatActivity {

    ListView zbxListview;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    SimpleCursorAdapter adapter;
    CreateSurveyDB zbattdb =new CreateSurveyDB(this,dbpath + "/" + attributedb, null,2);
    CreateSurveyDB zbspatdb =new CreateSurveyDB(this,dbpath + "/" + spatialdb, null,2);
    String sqla="select ID as _id,FTName,LinkID,YSMC,YSZL,SSLC,ZJTIME,BZ from ZBData order by ID desc";
    Cursor zbCursor;
    int linkid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zbselect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        zbxListview= (ListView) findViewById(R.id.zblistview);
        zbCursor=aexecQuery(sqla,null);
        adapter=new SimpleCursorAdapter(zbselect.this,R.layout.selectzb,zbCursor,new String[]{"LinkID","YSMC","YSZL","SSLC"},
                new int[]{R.id.zblinkid,R.id.zbname,R.id.zbYSZL,R.id.zbSSLC}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        zbxListview.setAdapter(adapter);
        zbxListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(zbselect.this);
                builder.setTitle("选择操作");
                builder.setMessage("follow your heart");
                final Cursor getid = (Cursor) adapter.getItem(position);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linkid = getid.getInt(2);
                        aexecDelete(linkid);
                        sexecDelete(linkid);
                        getid.close();
                        zbCursor = aexecQuery(sqla, null);
                        adapter.changeCursor(zbCursor);
                    }
                });
                builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA);
                        Date currentdate = new Date(System.currentTimeMillis());
                        final String zhujitime = format.format(currentdate);
                        View zbView=getLayoutInflater().inflate(R.layout.zhibei,null);
                        EditText zbFName= (EditText) zbView.findViewById(R.id.linkID);
                        final EditText zbLinkID= (EditText) zbView.findViewById(R.id.ftName);
                        final EditText zbmctext= (EditText) zbView.findViewById(R.id.zbmc);
                        final EditText zbzltext= (EditText) zbView.findViewById(R.id.zbzl);
                        final EditText sslctext= (EditText) zbView.findViewById(R.id.sslc);
                        final EditText bztext= (EditText) zbView.findViewById(R.id.bz);
                        String typename=getid.getString(1);
                        String name=getid.getString(3);
                        String zbzl=getid.getString(4);
                        String zbsslc=getid.getString(5);
                        final String zbbz=getid.getString(7);
                        zbFName.setText(typename);
                        zbLinkID.setText(String.valueOf(linkid));
                        zbmctext.setText(name);
                        zbzltext.setText(zbzl);
                        sslctext.setText(zbsslc);
                        bztext.setText(zbbz);
                        AlertDialog.Builder modify=new AlertDialog.Builder(zbselect.this);
                        modify.setView(zbView);
                        modify.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String zbname=zbmctext.getText().toString();
                                String zbzl2=zbzltext.getText().toString();
                                String zbsslc2=sslctext.getText().toString();
                                String zbbz2=bztext.getText().toString();
                                SQLiteDatabase database=zbattdb.getReadableDatabase();
                                database.execSQL("updata ZBData set YSMC=?,YSZL=?,SSLC=?,ZJTIME=?BZ=? where LinkID=?",
                                        new Object[]{zbname,zbzl2,zbsslc2,zhujitime,zbbz2,linkid});
                                database.close();
                                getid.close();
                                zbCursor=aexecQuery(sqla,null);
                                adapter.changeCursor(zbCursor);
                                zbCursor.close();
                            }
                        });
                        modify.setNegativeButton("取消",null);
                        modify.create().show();
                    }
                });
                builder.create().show();
            }
        });

    }

    public Cursor aexecQuery(String sql,String[] params){
        SQLiteDatabase adb= zbattdb.getReadableDatabase();
        return adb.rawQuery(sql,params);
    }

    public Cursor sexecQuery(String sql,String[] params){
        SQLiteDatabase sdb=zbspatdb.getReadableDatabase();
        return sdb.rawQuery(sql,params);
    }

    public long aexecInsert(ContentValues values){
        SQLiteDatabase adb= zbattdb.getReadableDatabase();
        long linkid=adb.insert("ZBData", null, values);
        adb.close();
        return linkid;
    }

    public long bexecInsert(ContentValues values){
        SQLiteDatabase sdb=zbspatdb.getReadableDatabase();
        long linkid=sdb.insert("ZBData", null, values);
        sdb.close();
        return linkid;
    }

    public void aexecDelete(int LinkID){
        SQLiteDatabase adb= zbattdb.getReadableDatabase();
        adb.delete("ZBData","LinkID=?",new String[]{String.valueOf(LinkID)});
        adb.close();
    }

    public void sexecDelete(int LinkID){
        SQLiteDatabase sdb=zbspatdb.getReadableDatabase();
        sdb.delete("ZBData","LinkAID=?",new String[]{String.valueOf(LinkID)});
        sdb.close();
    }

    public void aexecUpdate(ContentValues values,String id){
        SQLiteDatabase adb= zbattdb.getReadableDatabase();
        adb.update("ZBData", values, "LinkID=?", new String[]{id});
        adb.close();
    }

    public void sexecUpdate(ContentValues values,String id){
        SQLiteDatabase sdb=zbspatdb.getReadableDatabase();
        sdb.update("ZBData", values, "LinkAID=?", new String[]{id});
        sdb.close();
    }


}
