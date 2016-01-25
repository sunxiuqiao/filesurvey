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

public class jjxselect extends AppCompatActivity {
    ListView jjxlistview;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    SimpleCursorAdapter adapter;
    CreateSurveyDB jjxattdb =new CreateSurveyDB(this,dbpath + "/" + attributedb, null,2);
    CreateSurveyDB jjxspatdb =new CreateSurveyDB(this,dbpath + "/" + spatialdb, null,2);
    String sqla="select ID as _id,FTName,LinkID,GJ,NBJJX,ZJTIME,BZ from JJXData order by ID desc";
    Cursor jjxCursor;
    int linkid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jjxselect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        jjxlistview= (ListView) findViewById(R.id.jjxlistview);
        jjxCursor=aexecQuery(sqla,null);
        adapter=new SimpleCursorAdapter(jjxselect.this,R.layout.selectjjx,jjxCursor,new String[]{"LinkID","GJ","NBJJX"},
                new int[]{R.id.jjxlinkid,R.id.jjxgj,R.id.jjxNBJJX}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        jjxlistview.setAdapter(adapter);
        jjxCursor.close();
        jjxlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(jjxselect.this);
                builder.setTitle("选择操作");
                final Cursor getid= (Cursor) adapter.getItem(position);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linkid=getid.getInt(2);
                        aexecDelete(linkid);
                        aexecDelete(linkid);
                        getid.close();
                        jjxCursor=aexecQuery(sqla,null);
                        adapter.changeCursor(jjxCursor);
                        jjxCursor.close();
                    }
                });
                builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String typename=getid.getString(1);
                        String gj=getid.getString(3);
                        String nbjx=getid.getString(4);
                        String bz=getid.getString(6);
                        AlertDialog.Builder moidfy=new AlertDialog.Builder(jjxselect.this);
                        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA);
                        Date currentdate = new Date(System.currentTimeMillis());
                        final String zhujitime = format.format(currentdate);
                        View jjxView=getLayoutInflater().inflate(R.layout.jingjie,null);
                        final EditText jjxlinkID= (EditText) jjxView.findViewById(R.id.linkID);
                        final EditText jjxname= (EditText) jjxView.findViewById(R.id.ftName);
                        final EditText jjxgj= (EditText) jjxView.findViewById(R.id.gj);
                        final EditText jjxnbjx= (EditText) jjxView.findViewById(R.id.nbjjx);
                        final EditText jjxbz= (EditText) jjxView.findViewById(R.id.bz);
                        jjxname.setText(typename);
                        jjxlinkID.setText(String.valueOf(linkid));
                        jjxgj.setText(gj);
                        jjxnbjx.setText(nbjx);
                        jjxbz.setText(bz);
                        moidfy.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String gjname=jjxgj.getText().toString();
                                String jjxnb=jjxnbjx.getText().toString();
                                String bzjjx=jjxbz.getText().toString();
                                SQLiteDatabase database=jjxattdb.getReadableDatabase();
                                database.execSQL("updata JJXData set GJ=?,NBJJX=?,ZJTIME=?,BZ=? where LinkID=?",
                                        new Object[]{gjname,jjxnb,zhujitime,bzjjx,linkid});
                                database.close();
                                jjxCursor=aexecQuery(sqla,null);
                                adapter.changeCursor(jjxCursor);
                                jjxCursor.close();
                            }
                        });
                        moidfy.setNegativeButton("取消",null);
                        moidfy.create().show();
                    }
                });
                builder.create().show();
            }
        });

    }

    public Cursor aexecQuery(String sql,String[] params){
        SQLiteDatabase adb= jjxattdb.getReadableDatabase();
        return adb.rawQuery(sql,params);
    }

    public Cursor sexecQuery(String sql,String[] params){
        SQLiteDatabase sdb=jjxspatdb.getReadableDatabase();
        return sdb.rawQuery(sql,params);
    }

    public long aexecInsert(ContentValues values){
        SQLiteDatabase adb= jjxattdb.getReadableDatabase();
        long linkid=adb.insert("JJXData", null, values);
        adb.close();
        return linkid;
    }

    public long bexecInsert(ContentValues values){
        SQLiteDatabase sdb=jjxspatdb.getReadableDatabase();
        long linkid=sdb.insert("JJXData", null, values);
        sdb.close();
        return linkid;
    }

    public void aexecDelete(int LinkID){
        SQLiteDatabase adb= jjxattdb.getReadableDatabase();
        adb.delete("JJXData","LinkID=?",new String[]{String.valueOf(LinkID)});
        adb.close();
    }

    public void sexecDelete(int LinkID){
        SQLiteDatabase sdb=jjxspatdb.getReadableDatabase();
        sdb.delete("JJXData","LinkAID=?",new String[]{String.valueOf(LinkID)});
        sdb.close();
    }

    public void aexecUpdate(ContentValues values,String id){
        SQLiteDatabase adb= jjxattdb.getReadableDatabase();
        adb.update("JJXData", values, "LinkID=?", new String[]{id});
        adb.close();
    }

    public void sexecUpdate(ContentValues values,String id){
        SQLiteDatabase sdb=jjxspatdb.getReadableDatabase();
        sdb.update("JJXData",values,"LinkAID=?",new String[]{id});
        sdb.close();
    }



}
