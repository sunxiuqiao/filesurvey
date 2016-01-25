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

public class gxselect extends AppCompatActivity {

    ListView gxListview;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    SimpleCursorAdapter adapter;
    CreateSurveyDB gxattdb =new CreateSurveyDB(this,dbpath + "/" + attributedb, null,2);
    CreateSurveyDB gxspatdb =new CreateSurveyDB(this,dbpath + "/" + spatialdb, null,2);
    String sqla="select ID as _id,FTName,LinkID,DLXZX,DLXFS,ZJTIME,BZ from GXData order by ID desc";
    Cursor gxCursor;
    int linkid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gxselect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gxListview= (ListView) findViewById(R.id.gxlistview);
        gxCursor=aexecQuery(sqla,null);
        adapter=new SimpleCursorAdapter(gxselect.this,R.layout.selectgx,gxCursor,new String[]{"LinkID","DLXZX","DLXFS"},
                new int[]{R.id.gxlinkid,R.id.gxdlxzx,R.id.gxdlxfs}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        gxListview.setAdapter(adapter);
        gxCursor.close();
        gxListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(gxselect.this);
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
                    }
                });
                builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA);
                        Date currentdate = new Date(System.currentTimeMillis());
                        final String zhujitime = format.format(currentdate);
                        String typename=getid.getString(1);
                        String gxdlxzx=getid.getString(3);
                        String gxdlxfs=getid.getString(4);
                        String gxbz2=getid.getString(6);
                        View gxView=getLayoutInflater().inflate(R.layout.guanxian,null);
                        final EditText gxname= (EditText) gxView.findViewById(R.id.ftName);
                        final EditText gxLinkID= (EditText) gxView.findViewById(R.id.linkID);
                        final EditText dlxzx= (EditText) gxView.findViewById(R.id.dlxzx);
                        final EditText dlxfs= (EditText) gxView.findViewById(R.id.dlxfs);
                        final EditText gxbz= (EditText) gxView.findViewById(R.id.bz);
                        gxname.setText(typename);
                        gxLinkID.setText(String.valueOf(linkid));
                        dlxzx.setText(gxdlxzx);
                        dlxfs.setText(gxdlxfs);
                        gxbz.setText(gxbz2);
                        AlertDialog.Builder modify=new AlertDialog.Builder(gxselect.this);
                        modify.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String dlxzx2=dlxzx.getText().toString();
                                String dlxfs3=dlxfs.getText().toString();
                                String dlxbz=gxbz.getText().toString();
                                SQLiteDatabase database=gxattdb.getReadableDatabase();
                                database.execSQL("update GXData set DLXZX=?,DLXFS=?,ZJTIME=?,BZ=? where LinkID=?",
                                        new Object[]{dlxzx2,dlxfs3,zhujitime,dlxbz,linkid});
                                database.close();
                                gxCursor=aexecQuery(sqla,null);
                                adapter.changeCursor(gxCursor);
                                gxCursor.close();
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
        SQLiteDatabase adb= gxattdb.getReadableDatabase();
        return adb.rawQuery(sql,params);
    }

    public Cursor sexecQuery(String sql,String[] params){
        SQLiteDatabase sdb=gxspatdb.getReadableDatabase();
        return sdb.rawQuery(sql,params);
    }

    public long aexecInsert(ContentValues values){
        SQLiteDatabase adb= gxattdb.getReadableDatabase();
        long linkid=adb.insert("GXData", null, values);
        adb.close();
        return linkid;
    }

    public long bexecInsert(ContentValues values){
        SQLiteDatabase sdb=gxspatdb.getReadableDatabase();
        long linkid=sdb.insert("GXData", null, values);
        sdb.close();
        return linkid;
    }

    public void aexecDelete(int LinkID){
        SQLiteDatabase adb= gxattdb.getReadableDatabase();
        adb.delete("GXData","LinkID=?",new String[]{String.valueOf(LinkID)});
        adb.close();
    }

    public void sexecDelete(int LinkID){
        SQLiteDatabase sdb=gxspatdb.getReadableDatabase();
        sdb.delete("GXData","LinkAID=?",new String[]{String.valueOf(LinkID)});
        sdb.close();
    }

    public void aexecUpdate(ContentValues values,String id){
        SQLiteDatabase adb= gxattdb.getReadableDatabase();
        adb.update("GXData", values, "LinkID=?", new String[]{id});
        adb.close();
    }

    public void sexecUpdate(ContentValues values,String id){
        SQLiteDatabase sdb=gxspatdb.getReadableDatabase();
        sdb.update("GXData", values, "LinkAID=?", new String[]{id});
        sdb.close();
    }

}
