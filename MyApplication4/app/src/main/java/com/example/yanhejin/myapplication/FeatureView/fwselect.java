package com.example.yanhejin.myapplication.FeatureView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.example.yanhejin.myapplication.Database.CreateSpatialDB;
import com.example.yanhejin.myapplication.Database.CreateSurveyDB;
import com.example.yanhejin.myapplication.MainActivity;
import com.example.yanhejin.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class fwselect extends AppCompatActivity {

    ListView fwlist;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    MainActivity main;
    List<Map<String, Object>> listfw;
    SimpleCursorAdapter adapter;
    Cursor jmdc;
    String sqla="select ID as _id,LinkID,FTName,FWCS,FWCZ from JMDData order by LinkID desc";
    String sqls="select JMDID as _id,LinkAID,x,y from JMDData order";
    CreateSurveyDB jmdattdb = new CreateSurveyDB(fwselect.this,dbpath + "/" + attributedb, null,2);
    CreateSpatialDB jmdspatdb =new  CreateSpatialDB(fwselect.this,dbpath + "/" + spatialdb, null,2);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(fwselect.this);
                builder.setTitle("选择操作");
                builder.setMessage("做你想做的事");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aexecDelete(linkid);
                        SQLiteDatabase sdb=jmdspatdb.getReadableDatabase();
                        sdb.execSQL("delete from JMDData where LinkAID="+linkid);
                        sdb.close();
                        jmdc = aexecQuery(sqla, null);
                        adapter.changeCursor(jmdc);
                    }
                });
                builder.setNegativeButton("图上查看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<com.esri.core.geometry.Point> points = new ArrayList<com.esri.core.geometry.Point>();
                        Point startpoint;
                        Point topoint;
                        Polygon polygon = new Polygon();
                        MapView mapview = main.getMapView();
                        SimpleLineSymbol simpleline = new SimpleLineSymbol(Color.BLACK, 4, SimpleLineSymbol.STYLE.SOLID);
                        SimpleFillSymbol simplefill = new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.SOLID);
                        simplefill.setAlpha(10);
                        simplefill.setOutline(simpleline);
                        Graphic g;
                        GraphicsLayer layer = new GraphicsLayer();
                        Cursor idCursor= (Cursor) adapter.getItem(position);
                        int id=idCursor.getInt(0);
                        if (idCursor.getCount()>1){
                            for (int i=0;i<idCursor.getCount();i++){
                                float x = idCursor.getInt(2);
                                float y = idCursor.getInt(3);
                                com.esri.core.geometry.Point point = new com.esri.core.geometry.Point(x, y);
                                points.add(point);
                            }
                        }
                      /*  spatialcursor = sexecQuery("select from JMDData where LinkAID=?", new String[]{String.valueOf(linkid)});
                        while (spatialcursor.moveToNext()) {
                            for (int i = 0; i < spatialcursor.getCount(); i++) {
                                float x = spatialcursor.getInt(2);
                                float y = spatialcursor.getInt(3);
                                com.esri.core.geometry.Point point = new com.esri.core.geometry.Point(x, y);
                                points.add(point);
                            }
                        }*/
                        /*jmdspatdb.close();
                        spatialcursor.close();*/
                        for (int i = 0; i < points.size(); i++) {
                            startpoint = points.get(i);
                            topoint = points.get(i + 1);
                            Line line = new Line();
                            line.setStart(startpoint);
                            line.setEnd(topoint);
                            polygon.addSegment(line, false);
                        }
                        g = new Graphic(polygon, simplefill);
                        layer.addGraphic(g);
                        mapview.addLayer(layer);
                    }
                });
                builder.create().show();
            }


        });


    }
    public void getdata(){
        jmdc=aexecQuery(sqla,null);
        adapter=new SimpleCursorAdapter(fwselect.this,R.layout.selectfw,jmdc,new String[]{"LinkID","FTName","FWCS","FWCZ"},
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
        jmdc=aexecQuery(sqla,null);
        adapter.changeCursor(jmdc);

    }

    public Cursor aexecQuery(String sql,String[] params){
        SQLiteDatabase adb=jmdattdb.getReadableDatabase();
        return adb.rawQuery(sql,params);
    }

    public Cursor sexecQuery(String sql,String[] params){
        SQLiteDatabase sdb=jmdspatdb.getReadableDatabase();
        return sdb.rawQuery(sql,params);
    }

    public long aexecInsert(ContentValues values){
        SQLiteDatabase adb=jmdattdb.getReadableDatabase();
        long linkid=adb.insert("JMDData", null, values);
        adb.close();
        return linkid;
    }

    public long bexecInsert(ContentValues values){
        SQLiteDatabase sdb=jmdspatdb.getReadableDatabase();
        long linkid=sdb.insert("JMDData", null, values);
        sdb.close();
        return linkid;
    }

    public void aexecDelete(int LinkID){
        SQLiteDatabase adb=jmdattdb.getReadableDatabase();
        adb.delete("JMDData","LinkID=?",new String[]{String.valueOf(LinkID)});
        adb.close();
    }

    public void sexecDelete(int LinkID){
        SQLiteDatabase sdb=jmdspatdb.getReadableDatabase();
        sdb.delete("JMDData","LinkAID=?",new String[]{String.valueOf(LinkID)});
        sdb.close();
    }

    public void aexecUpdate(ContentValues values,String id){
        SQLiteDatabase adb=jmdattdb.getReadableDatabase();
        adb.update("JMDData", values, "LinkID=?", new String[]{id});
        adb.close();
    }

    public void sexecUpdate(ContentValues values,String id){
        SQLiteDatabase sdb=jmdspatdb.getReadableDatabase();
        sdb.update("JMDData",values,"LinkAID=?",new String[]{id});
        sdb.close();
    }



}




