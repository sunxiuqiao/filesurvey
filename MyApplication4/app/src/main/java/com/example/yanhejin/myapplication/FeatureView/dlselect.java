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
import com.example.yanhejin.myapplication.MainActivity;
import com.example.yanhejin.myapplication.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class dlselect extends AppCompatActivity {

    ListView dllistview;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    MainActivity main;
    SimpleCursorAdapter adapter;
    CreateSurveyDB dlattdb =new CreateSurveyDB(this,dbpath + "/" + attributedb, null,2);
    CreateSurveyDB dlspatdb =new CreateSurveyDB(this,dbpath + "/" + spatialdb, null,2);
    String sqla="select ID as _id,FTName,LinkID,DLMC,DLXH,DJDM,ZJTIME,BZ from DLData order by ID desc";
    Cursor dlCursor;
    int linkid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlselect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dllistview= (ListView) findViewById(R.id.dlListView);
        dlCursor=aexecQuery(sqla,null);
        adapter=new SimpleCursorAdapter(this,R.layout.selectdl,dlCursor,new String[]{"LinkID","DLMC","DLXH","DJDM"},
                new int[]{R.id.linkid,R.id.dlname,R.id.dlxhselect,R.id.djdmselect}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        dllistview.setAdapter(adapter);
        dllistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(dlselect.this);
                builder.setTitle("选择操作");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor cursor = (Cursor) adapter.getItem(position);
                        linkid = cursor.getInt(2);
                        aexecDelete(linkid);
                        sexecDelete(linkid);
                        dlCursor=aexecQuery(sqla,null);
                        adapter.changeCursor(dlCursor);
                    }
                });
                builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA);
                        Date currentdate = new Date(System.currentTimeMillis());
                        final String zhujitime = format.format(currentdate);
                        Cursor cursor = (Cursor) adapter.getItem(position);
                        final String featurename = cursor.getString(1);
                        final int linkid = cursor.getInt(2);
                        final String dlmc = cursor.getString(3);
                        final String dlxh = cursor.getString(4);
                        final String djdm = cursor.getString(5);
                        //final String dltime=cursor.getString(6);
                        final String bz = cursor.getString(7);
                        final AlertDialog.Builder modify = new AlertDialog.Builder(dlselect.this);
                        final View view1 = getLayoutInflater().inflate(R.layout.daolu, null);
                        modify.setView(view1);
                        EditText fdlName = (EditText) view1.findViewById(R.id.ftName);
                        final EditText dlLinkId = (EditText) view1.findViewById(R.id.linkID);
                        final EditText dlmctext = (EditText) view1.findViewById(R.id.dlmc);
                        final EditText dlxhtext = (EditText) view1.findViewById(R.id.dlxh);
                        final EditText djdmtext = (EditText) view1.findViewById(R.id.djdm);
                        final EditText dlbztext = (EditText) view1.findViewById(R.id.bz);
                        fdlName.setText(featurename);
                        dlLinkId.setText(String.valueOf(linkid));
                        dlmctext.setText(dlmc);
                        dlxhtext.setText(dlxh);
                        djdmtext.setText(djdm);
                        dlbztext.setText(bz);
                        modify.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int ID = Integer.valueOf(dlLinkId.getText().toString());
                                String MC = dlmctext.getText().toString();
                                String XH = dlxhtext.getText().toString();
                                String DJDM = djdmtext.getText().toString();
                                String BZ = dlbztext.getText().toString();
                                SQLiteDatabase database = dlattdb.getReadableDatabase();
                                database.execSQL("update DLData set DLMC=?,DLXH=?,DJDM=?,ZJTIME=?,BZ=? where LinkID=?",
                                        new Object[]{MC, XH, DJDM, zhujitime, BZ,ID});
                                database.close();
                                dlCursor=aexecQuery(sqla,null);
                                adapter.changeCursor(dlCursor);
                            }
                        });
                        modify.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
        SQLiteDatabase adb= dlattdb.getReadableDatabase();
        return adb.rawQuery(sql,params);
    }

    public Cursor sexecQuery(String sql,String[] params){
        SQLiteDatabase sdb=dlspatdb.getReadableDatabase();
        return sdb.rawQuery(sql,params);
    }

    public long aexecInsert(ContentValues values){
        SQLiteDatabase adb= dlattdb.getReadableDatabase();
        long linkid=adb.insert("DLData", null, values);
        adb.close();
        return linkid;
    }

    public long bexecInsert(ContentValues values){
        SQLiteDatabase sdb=dlspatdb.getReadableDatabase();
        long linkid=sdb.insert("DLData", null, values);
        sdb.close();
        return linkid;
    }

    public void aexecDelete(int LinkID){
        SQLiteDatabase adb= dlattdb.getReadableDatabase();
        adb.delete("DLData","LinkID=?",new String[]{String.valueOf(LinkID)});
        adb.close();
    }

    public void sexecDelete(int LinkID){
        SQLiteDatabase sdb=dlspatdb.getReadableDatabase();
        sdb.delete("DLData","LinkAID=?",new String[]{String.valueOf(LinkID)});
        sdb.close();
    }

    public void aexecUpdate(ContentValues values,String id){
        SQLiteDatabase adb= dlattdb.getReadableDatabase();
        adb.update("DLData", values, "LinkID=?", new String[]{id});
        adb.close();
    }

    public void sexecUpdate(ContentValues values,String id){
        SQLiteDatabase sdb=dlspatdb.getReadableDatabase();
        sdb.update("DLData",values,"LinkAID=?",new String[]{id});
        sdb.close();
    }


}
