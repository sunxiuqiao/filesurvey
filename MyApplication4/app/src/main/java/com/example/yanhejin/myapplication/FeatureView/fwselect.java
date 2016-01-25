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
import android.widget.Toast;

import com.example.yanhejin.myapplication.Database.CreateSpatialDB;
import com.example.yanhejin.myapplication.Database.CreateSurveyDB;
import com.example.yanhejin.myapplication.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class fwselect extends AppCompatActivity {

    ListView fwlist;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    SimpleCursorAdapter adapter;
    Cursor jmdCursor;
    String sqla="select ID as _id,FTName,LinkID,FWCS,FWCZ ,FYGZ,ZJTIME,BZ from JMDData order by LinkID desc";
    String sqls="select JMDID as _id,LinkAID,x,y from JMDData order";
    CreateSurveyDB dlattridb = new CreateSurveyDB(fwselect.this,dbpath + "/" + attributedb, null,2);
    CreateSpatialDB dlspatialdb =new  CreateSpatialDB(fwselect.this,dbpath + "/" + spatialdb, null,2);
    int linkid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fwselect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fwlist= (ListView) findViewById(R.id.fwlist);
        getdata();
        fwlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Cursor cursorWrapper= (Cursor) adapter.getItem(position);
                if (!cursorWrapper.isNull(linkid)){
                    linkid= cursorWrapper.getInt(1);
                    Toast.makeText(fwselect.this, String.valueOf(linkid), Toast.LENGTH_LONG).show();
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(fwselect.this);
                builder.setTitle("选择操作");
                builder.setMessage("follow your heart");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aexecDelete(linkid);
                        SQLiteDatabase sdb = dlspatialdb.getReadableDatabase();
                        sdb.execSQL("delete from JMDData where LinkAID=" + linkid);
                        sdb.close();
                        jmdCursor = aexecQuery(sqla, null);
                        adapter.changeCursor(jmdCursor);
                    }
                });
                builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA);
                        Date currentdate = new Date(System.currentTimeMillis());
                        final String zhujitime = format.format(currentdate);
                        AlertDialog.Builder moidfy = new AlertDialog.Builder(fwselect.this);
                        View jmdview = getLayoutInflater().inflate(R.layout.jumindi, null);
                        final Cursor cursor = (Cursor) adapter.getItem(position);
                        String name = cursor.getString(1);
                        int ID = cursor.getInt(2);
                        final int fwcs = cursor.getInt(3);
                        String fwcz = cursor.getString(4);
                        double fygz = cursor.getDouble(5);
                        String datetime = cursor.getString(6);
                        String bz = cursor.getString(7);
                        final EditText linkData = (EditText) jmdview.findViewById(R.id.linkID);
                        final EditText fnameText = (EditText) jmdview.findViewById(R.id.ftName);
                        final EditText fwcstext = (EditText) jmdview.findViewById(R.id.fwcs);
                        final EditText fwcztext = (EditText) jmdview.findViewById(R.id.fwcz);
                        final EditText fygztext = (EditText) jmdview.findViewById(R.id.fygz);
                        final EditText bztext = (EditText) jmdview.findViewById(R.id.bz);
                        linkData.setText(String.valueOf(ID));
                        fwcstext.setText(String.valueOf(fwcs));
                        fwcztext.setText(fwcz);
                        fnameText.setText(name);
                        fygztext.setText(String.valueOf(fygz));
                        bztext.setText(bz);
                        moidfy.setView(jmdview);
                        moidfy.setTitle("修改属性");
                        moidfy.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int id = Integer.valueOf(linkData.getText().toString());
                                String typename = fnameText.getText().toString();
                                int dlfwcs = Integer.valueOf(fwcstext.getText().toString());
                                String jmdfwcz = fwcztext.getText().toString();
                                double jmdfygz = Double.valueOf(fygztext.getText().toString());
                                String jmdbz = bztext.getText().toString();
                                SQLiteDatabase database = dlattridb.getReadableDatabase();
                                database.execSQL("update DLData set FWCS=?,FWCZ=?,FYGZ=?,ZJTIME=?,BZ=? where LinkID=?", new Object[]{dlfwcs,jmdfwcz,jmdfygz,jmdbz,id});
                                database.close();
                                cursor.close();
                                jmdCursor =aexecQuery(sqla,null);
                                adapter.changeCursor(jmdCursor);
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
    public void getdata(){
        jmdCursor =aexecQuery(sqla,null);
        adapter=new SimpleCursorAdapter(fwselect.this,R.layout.selectfw, jmdCursor,new String[]{"LinkID","FTName","FWCS","FWCZ"},
                new int[]{R.id.linkid,R.id.fwname,R.id.fwcselect,R.id.fwczselect}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        fwlist.setAdapter(adapter);
    }


    @Override
    protected void onResume() {

        super.onResume();
        fwlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        jmdCursor =aexecQuery(sqla,null);
        adapter.changeCursor(jmdCursor);

    }

    public Cursor aexecQuery(String sql,String[] params){
        SQLiteDatabase adb= dlattridb.getReadableDatabase();
        return adb.rawQuery(sql,params);
    }

    public Cursor sexecQuery(String sql,String[] params){
        SQLiteDatabase sdb= dlspatialdb.getReadableDatabase();
        return sdb.rawQuery(sql,params);
    }

    public long aexecInsert(ContentValues values){
        SQLiteDatabase adb= dlattridb.getReadableDatabase();
        long linkid=adb.insert("JMDData", null, values);
        adb.close();
        return linkid;
    }

    public long bexecInsert(ContentValues values){
        SQLiteDatabase sdb= dlspatialdb.getReadableDatabase();
        long linkid=sdb.insert("JMDData", null, values);
        sdb.close();
        return linkid;
    }

    public void aexecDelete(int LinkID){
        SQLiteDatabase adb= dlattridb.getReadableDatabase();
        adb.delete("JMDData","LinkID=?",new String[]{String.valueOf(LinkID)});
        adb.close();
    }

    public void sexecDelete(int LinkID){
        SQLiteDatabase sdb= dlspatialdb.getReadableDatabase();
        sdb.delete("JMDData","LinkAID=?",new String[]{String.valueOf(LinkID)});
        sdb.close();
    }

    public void aexecUpdate(ContentValues values,String id){
        SQLiteDatabase adb= dlattridb.getReadableDatabase();
        adb.update("JMDData", values, "LinkID=?", new String[]{id});
        adb.close();
    }

    public void sexecUpdate(ContentValues values,String id){
        SQLiteDatabase sdb= dlspatialdb.getReadableDatabase();
        sdb.update("JMDData",values,"LinkAID=?",new String[]{id});
        sdb.close();
    }



}




