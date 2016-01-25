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

public class sxselect extends AppCompatActivity {

    ListView sxListview;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    SimpleCursorAdapter adapter;
    CreateSurveyDB sxattdb =new CreateSurveyDB(this,dbpath + "/" + attributedb, null,2);
    CreateSurveyDB sxspatdb =new CreateSurveyDB(this,dbpath + "/" + spatialdb, null,2);
    String sqla="select ID as _id,FTName,LinkID,YSMC,FSSS,ZJTIME,BZ from SXData order by ID desc";
    Cursor sxCursor;
    int linkid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sxselect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sxListview= (ListView) findViewById(R.id.sxListview);
        sxCursor=aexecQuery(sqla,null);
        adapter=new SimpleCursorAdapter(sxselect.this,R.layout.selectsx,sxCursor,new String[]{"LinkID","YSMC","FSSS"},
                new int[]{R.id.sxlinkid,R.id.sxname,R.id.fsssselect}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        sxListview.setAdapter(adapter);
        sxListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(sxselect.this);
                builder.setTitle("选择操作");
                builder.setMessage("follow your heart");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor getid = (Cursor) adapter.getItem(position);
                        int linkid = getid.getInt(2);
                        aexecDelete(linkid);
                        sexecDelete(linkid);
                        getid.close();
                        sxCursor = aexecQuery(sqla, null);
                        adapter.changeCursor(sxCursor);
                    }
                });
                builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA);
                        Date currentdate = new Date(System.currentTimeMillis());
                        final String zhujitime = format.format(currentdate);
                        final Cursor cursor = (Cursor) adapter.getItem(position);
                        String type=cursor.getString(1);
                        int id=cursor.getInt(2);
                        String name=cursor.getString(3);
                        String fsss=cursor.getString(4);
                        String bz=cursor.getString(6);
                        View shuixiView=getLayoutInflater().inflate(R.layout.shuixi, null);
                        AlertDialog.Builder modify=new AlertDialog.Builder(sxselect.this);
                        modify.setView(shuixiView);
                        final EditText ftnametext = (EditText) shuixiView.findViewById(R.id.ftName);
                        final EditText LinkID = (EditText) shuixiView.findViewById(R.id.linkID);
                        final EditText featurename = (EditText) shuixiView.findViewById(R.id.featurename);
                        final EditText fssstext = (EditText) shuixiView.findViewById(R.id.fsss);
                        final EditText sxbztext = (EditText) shuixiView.findViewById(R.id.bz);
                        ftnametext.setText(type);
                        LinkID.setText(String.valueOf(id));
                        featurename.setText(name);
                        fssstext.setText(fsss);
                        sxbztext.setText(bz);
                        modify.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int ID=Integer.valueOf(LinkID.getText().toString());
                                String sxname=featurename.getText().toString();
                                String sxfsss=fssstext.getText().toString();
                                String sxbz=sxbztext.getText().toString();
                                SQLiteDatabase database=sxattdb.getReadableDatabase();
                                database.execSQL("update SXData set YSMC=?,FSSS=?,ZJTIME=?,BZ=? where LinkID=?",
                                        new Object[]{sxname,sxfsss,zhujitime,sxbz,ID});
                                database.close();
                                cursor.close();
                                sxCursor=aexecQuery(sqla,null);
                                adapter.changeCursor(sxCursor);
                            }
                        });
                        modify.setNegativeButton("取消",null);
                    }
                });
                builder.create().show();
            }
        });
    }

    public Cursor aexecQuery(String sql,String[] params){
        SQLiteDatabase adb= sxattdb.getReadableDatabase();
        return adb.rawQuery(sql,params);
    }

    public Cursor sexecQuery(String sql,String[] params){
        SQLiteDatabase sdb=sxspatdb.getReadableDatabase();
        return sdb.rawQuery(sql,params);
    }

    public long aexecInsert(ContentValues values){
        SQLiteDatabase adb= sxattdb.getReadableDatabase();
        long linkid=adb.insert("SXData", null, values);
        adb.close();
        return linkid;
    }

    public long bexecInsert(ContentValues values){
        SQLiteDatabase sdb=sxspatdb.getReadableDatabase();
        long linkid=sdb.insert("SXData", null, values);
        sdb.close();
        return linkid;
    }

    public void aexecDelete(int LinkID){
        SQLiteDatabase adb= sxattdb.getReadableDatabase();
        adb.delete("SXData","LinkID=?",new String[]{String.valueOf(LinkID)});
        adb.close();
    }

    public void sexecDelete(int LinkID){
        SQLiteDatabase sdb=sxspatdb.getReadableDatabase();
        sdb.delete("SXData","LinkAID=?",new String[]{String.valueOf(LinkID)});
        sdb.close();
    }

    public void aexecUpdate(ContentValues values,String id){
        SQLiteDatabase adb= sxattdb.getReadableDatabase();
        adb.update("SXData", values, "LinkID=?", new String[]{id});
        adb.close();
    }

    public void sexecUpdate(ContentValues values,String id){
        SQLiteDatabase sdb=sxspatdb.getReadableDatabase();
        sdb.update("SXData", values, "LinkAID=?", new String[]{id});
        sdb.close();
    }
}
