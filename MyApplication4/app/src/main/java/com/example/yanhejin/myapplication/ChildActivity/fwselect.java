package com.example.yanhejin.myapplication.ChildActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.example.yanhejin.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fwselect extends AppCompatActivity {

    ListView fwlist;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fwselect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fwlist= (ListView) findViewById(R.id.fwlist);
        SimpleAdapter adapter=new SimpleAdapter(fwselect.this,getData(),R.layout.fwselect,new String[]{"linkid","fwname","fwcs","fwcz"},new int[]{R.id.linkid,R.id.fwname,R.id.fwcselect,R.id.fwczselect});
        fwlist.setAdapter(adapter);
        fwlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Object> hashMap= (HashMap<String, Object>) fwlist.getItemAtPosition(position);
                final int linkid= (int) hashMap.get("linkid");
                Toast.makeText(fwselect.this,String.valueOf(linkid),Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(fwselect.this);
                builder.setTitle("选择操作");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase jmdattdb=SQLiteDatabase.openOrCreateDatabase(dbpath + "/" + attributedb, null);
                        SQLiteDatabase jmdspatdb=SQLiteDatabase.openOrCreateDatabase(dbpath + "/" + spatialdb, null);
                        /*String delete="delete from JMDData where LinkID=?";
                        Cursor deletecursor=jmdattdb.rawQuery("delete from JMDData where LinkID=?",new String[]{String.valueOf(linkid)});*/
                        jmdattdb.delete("JMDData","delete from JMDData where LinkID=?",new String[]{String.valueOf(linkid)});
                        jmdspatdb.delete("JMDData","delete from JMDData where LinkID=?",new String[]{String.valueOf(linkid)});
                        jmdattdb.close();
                        jmdspatdb.close();
                    }
                });
                builder.setPositiveButton("图上查看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<com.esri.core.geometry.Point> points=new ArrayList<com.esri.core.geometry.Point>();
                        Point startpoint;
                        Point topoint;
                        Polygon polygon=new Polygon();
                        View view=getLayoutInflater().inflate(R.layout.content_main,null);
                        MapView mapview= (MapView) view.findViewById(R.id.mapview);
                        SimpleLineSymbol simpleline=new SimpleLineSymbol(Color.BLACK,4, SimpleLineSymbol.STYLE.SOLID);
                        SimpleFillSymbol simplefill=new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.SOLID);
                        simplefill.setAlpha(10);
                        simplefill.setOutline(simpleline);
                        Graphic g;
                        GraphicsLayer layer=new GraphicsLayer();
                        SQLiteDatabase jmdspatdb=SQLiteDatabase.openOrCreateDatabase(dbpath + "/" + spatialdb, null);
                        Cursor spatialcursor=jmdspatdb.rawQuery("select from JMDData where LinkID=?",new String[]{String.valueOf(linkid)});
                        while (spatialcursor!=null){
                            for (int i=0;i<spatialcursor.getCount();i++){
                                float x=spatialcursor.getInt(2);
                                float y=spatialcursor.getInt(3);
                                com.esri.core.geometry.Point point=new com.esri.core.geometry.Point(x,y);
                                points.add(point);
                            }
                        }
                        jmdspatdb.close();
                        spatialcursor.close();
                        for (int i=0;i<points.size();i++){
                            startpoint=points.get(i);
                            topoint=points.get(i+1);
                            Line line=new Line();
                            line.setStart(startpoint);
                            line.setEnd(topoint);
                            polygon.addSegment(line, false);
                        }
                        g=new Graphic(polygon,simplefill);
                        layer.addGraphic(g);
                        mapview.addLayer(layer);
                    }
                });
                builder.create().show();
            }
        });
    }

    private List<Map<String, Object>> getData(){
        List<Map<String,Object>> listfw=new ArrayList<Map<String,Object>>();

        String select="select * from JMDData";
        SQLiteDatabase jmddb=SQLiteDatabase.openOrCreateDatabase(dbpath + "/" + attributedb, null);
        Cursor jmdc=jmddb.rawQuery(select, null);
        if (jmdc.getCount()>0){
            while (jmdc.moveToNext()){
                Map<String,Object> map=new HashMap<String,Object>();
                int linkID=jmdc.getInt(jmdc.getColumnIndex("LinkID"));
                String fwname=jmdc.getString(jmdc.getColumnIndex("FTName"));
                int fwcs=jmdc.getInt(jmdc.getColumnIndex("FWCS"));
                String fwcz=jmdc.getString(jmdc.getColumnIndex("FWCZ"));
                map.put("linkid",linkID);
                map.put("fwname",fwname);
                map.put("fwcs",fwcs);
                map.put("fwcz",fwcz);
                listfw.add(map);
            }
            jmdc.close();
            jmddb.close();
        }
        return listfw;
    }


}
