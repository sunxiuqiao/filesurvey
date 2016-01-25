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

public class dmselect extends AppCompatActivity {

    ListView dmlistview;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    SimpleCursorAdapter adapter;
    CreateSurveyDB dmattdb =new CreateSurveyDB(this,dbpath + "/" + attributedb, null,2);
    CreateSurveyDB dmspatdb =new CreateSurveyDB(this,dbpath + "/" + spatialdb, null,2);
    String sqla="select ID as _id,FTName,LinkID,DMMC,ZJTIME,BZ from DMData order by ID desc";
    Cursor dmCursor;
    int linkid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmselect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dmlistview= (ListView) findViewById(R.id.dmlistview);
        dmCursor=aexecQuery(sqla,null);
        adapter=new SimpleCursorAdapter(dmselect.this,R.layout.selectdm,dmCursor,new String[]{"LinkID","DMMC"},
                new int[]{R.id.dmlinkid,R.id.dmmc}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        dmlistview.setAdapter(adapter);
        dmCursor.close();
        dmlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(dmselect.this);
                builder.setTitle("选择操作");
                final Cursor getid= (Cursor) adapter.getItem(position);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linkid=getid.getInt(2);
                        aexecDelete(linkid);
                        sexecDelete(linkid);
                        getid.close();
                    }
                });
                builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA);
                        Date currentdate = new Date(System.currentTimeMillis());
                        final String zhujitime = format.format(currentdate);
                        String typename=getid.getString(1);
                        String dmname=getid.getString(3);
                        String dmbz2=getid.getString(5);
                        AlertDialog.Builder modify=new AlertDialog.Builder(dmselect.this);
                        View dmView=getLayoutInflater().inflate(R.layout.dimao,null);
                        EditText dmName= (EditText) dmView.findViewById(R.id.ftName);
                        final EditText dmLinkID= (EditText) dmView.findViewById(R.id.linkID);
                        final EditText dmMC= (EditText) dmView.findViewById(R.id.dmmc);
                        final EditText dmbz= (EditText) dmView.findViewById(R.id.bz);
                        dmName.setText(typename);
                        dmLinkID.setText(String.valueOf(linkid));
                        dmMC.setText(dmname);
                        dmbz.setText(dmbz2);
                        modify.setView(dmView);
                        modify.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String mc=dmMC.getText().toString();
                                String bz=dmbz.getText().toString();
                                SQLiteDatabase database=dmattdb.getReadableDatabase();
                                database.execSQL("updata DMData set DMMC=?,ZJTIME=?,BZ=? where LinkID=?",new Object[]{mc,zhujitime,bz,linkid});
                                database.close();
                                getid.close();
                                dmCursor=aexecQuery(sqla,null);
                                adapter.changeCursor(dmCursor);
                                dmCursor.close();
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
        SQLiteDatabase adb= dmattdb.getReadableDatabase();
        return adb.rawQuery(sql,params);
    }

    public Cursor sexecQuery(String sql,String[] params){
        SQLiteDatabase sdb=dmspatdb.getReadableDatabase();
        return sdb.rawQuery(sql,params);
    }

    public long aexecInsert(ContentValues values){
        SQLiteDatabase adb= dmattdb.getReadableDatabase();
        long linkid=adb.insert("DMData", null, values);
        adb.close();
        return linkid;
    }

    public long bexecInsert(ContentValues values){
        SQLiteDatabase sdb=dmspatdb.getReadableDatabase();
        long linkid=sdb.insert("DMData", null, values);
        sdb.close();
        return linkid;
    }

    public void aexecDelete(int LinkID){
        SQLiteDatabase adb= dmattdb.getReadableDatabase();
        adb.delete("DMData","LinkID=?",new String[]{String.valueOf(LinkID)});
        adb.close();
    }

    public void sexecDelete(int LinkID){
        SQLiteDatabase sdb=dmspatdb.getReadableDatabase();
        sdb.delete("DMData","LinkAID=?",new String[]{String.valueOf(LinkID)});
        sdb.close();
    }

    public void aexecUpdate(ContentValues values,String id){
        SQLiteDatabase adb= dmattdb.getReadableDatabase();
        adb.update("DMData", values, "LinkID=?", new String[]{id});
        adb.close();
    }

    public void sexecUpdate(ContentValues values,String id){
        SQLiteDatabase sdb=dmspatdb.getReadableDatabase();
        sdb.update("DMData", values, "LinkAID=?", new String[]{id});
        sdb.close();
    }

}
