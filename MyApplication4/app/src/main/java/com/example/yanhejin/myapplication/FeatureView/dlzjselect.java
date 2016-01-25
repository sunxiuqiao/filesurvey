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

public class dlzjselect extends AppCompatActivity {


    ListView dlzjlistview;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    SimpleCursorAdapter adapter;
    CreateSurveyDB dlzjattdb =new CreateSurveyDB(this,dbpath + "/" + attributedb, null,2);
    CreateSurveyDB dlzjspatdb =new CreateSurveyDB(this,dbpath + "/" + spatialdb, null,2);
    String sqla="select ID as _id,LinkID,YSName,YSType,ZJTIME,BZ from PZJData order by ID desc";
    String sqls="select ID as _id,LinkAID,x,y from PZJData order by ID desc";
    Cursor dlzjCursor;
    Cursor spaCursor;
    int linkid;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlzjselect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dlzjlistview= (ListView) findViewById(R.id.dlzjlistview);
        dlzjCursor=aexecQuery(sqla,null);
        adapter=new SimpleCursorAdapter(dlzjselect.this,R.layout.selectdlzj,dlzjCursor,new String[]{"LinkID","YSName","YSType"},
                new int[]{R.id.dlzjlinkid,R.id.dlzjname,R.id.dlzjtype}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        dlzjlistview.setAdapter(adapter);
        dlzjCursor.close();
        dlzjlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(dlzjselect.this);
                builder.setTitle("选择操作");
                final Cursor getid= (Cursor) adapter.getItem(position);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linkid=getid.getInt(1);
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
                        SQLiteDatabase databasespal=dlzjspatdb.getReadableDatabase();
                        Cursor getspalid=databasespal.rawQuery("select x,y from PZJData where LinkID=?", new String[]{String.valueOf(linkid)});
                        double zjx=getspalid.getDouble(1);
                        double zjy=getspalid.getDouble(2);
                        String featurename=getid.getString(2);
                        String typename=getid.getString(3);
                        String bz=getid.getString(5);
                        View zhujiview=getLayoutInflater().inflate(R.layout.dilizhuji, null);
                        final EditText zhujiLink= (EditText) zhujiview.findViewById(R.id.zhujiNum);
                        final EditText zhujiname= (EditText) zhujiview.findViewById(R.id.zhujiname);
                        final EditText zhujitype= (EditText) zhujiview.findViewById(R.id.zhujitype);
                        final EditText zhujix= (EditText) zhujiview.findViewById(R.id.zhujix);
                        final EditText zhujiy= (EditText) zhujiview.findViewById(R.id.zhujiy);
                        final EditText zhujibz= (EditText) zhujiview.findViewById(R.id.zhujibeizhu);
                        zhujiLink.setText(String.valueOf(linkid));
                        zhujitype.setText(typename);
                        zhujiname.setText(featurename);
                        zhujix.setText(String.valueOf(zjx));
                        zhujiy.setText(String.valueOf(zjy));
                        AlertDialog.Builder modify=new AlertDialog.Builder(dlzjselect.this);
                        modify.setView(zhujiview);
                        modify.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name=zhujiname.getText().toString();
                                String type=zhujitype.getText().toString();
                                String bz=zhujibz.getText().toString();
                                double x=Double.valueOf(zhujix.getText().toString());
                                double y=Double.valueOf(zhujiy.getText().toString());
                                SQLiteDatabase attributedb=dlzjattdb.getReadableDatabase();
                                attributedb.execSQL("update PZJData set YSName=?,YSType=?,ZJTIME=?,BZ=? where LinkID=?",
                                        new Object[]{name, type, zhujitime, bz, linkid});
                                attributedb.close();
                                dlzjCursor=aexecQuery(sqla,null);
                                adapter.changeCursor(dlzjCursor);
                                dlzjCursor.close();
                                SQLiteDatabase spatialdb=dlzjspatdb.getReadableDatabase();
                                spatialdb.execSQL("update PZJData set x=?,y=? where LinkAID=?",
                                        new Object[]{x,y,linkid});
                                spatialdb.close();
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
        SQLiteDatabase adb= dlzjattdb.getReadableDatabase();
        return adb.rawQuery(sql,params);
    }

    public Cursor sexecQuery(String sql,String[] params){
        SQLiteDatabase sdb=dlzjspatdb.getReadableDatabase();
        return sdb.rawQuery(sql,params);
    }

    public long aexecInsert(ContentValues values){
        SQLiteDatabase adb= dlzjattdb.getReadableDatabase();
        long linkid=adb.insert("PZJData", null, values);
        adb.close();
        return linkid;
    }

    public long bexecInsert(ContentValues values){
        SQLiteDatabase sdb=dlzjspatdb.getReadableDatabase();
        long linkid=sdb.insert("PZJData", null, values);
        sdb.close();
        return linkid;
    }

    public void aexecDelete(int LinkID){
        SQLiteDatabase adb= dlzjattdb.getReadableDatabase();
        adb.delete("PZJData","LinkID=?",new String[]{String.valueOf(LinkID)});
        adb.close();
    }

    public void sexecDelete(int LinkID){
        SQLiteDatabase sdb=dlzjspatdb.getReadableDatabase();
        sdb.delete("PZJData","LinkAID=?",new String[]{String.valueOf(LinkID)});
        sdb.close();
    }

    public void aexecUpdate(ContentValues values,String id){
        SQLiteDatabase adb= dlzjattdb.getReadableDatabase();
        adb.update("PZJData", values, "LinkID=?", new String[]{id});
        adb.close();
    }

    public void sexecUpdate(ContentValues values,String id){
        SQLiteDatabase sdb=dlzjspatdb.getReadableDatabase();
        sdb.update("PZJData",values,"LinkAID=?",new String[]{id});
        sdb.close();
    }


}
