package com.example.yanhejin.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureEditResult;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.tasks.SpatialRelationship;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.identify.IdentifyParameters;
import com.esri.core.tasks.identify.IdentifyResult;
import com.esri.core.tasks.identify.IdentifyTask;
import com.example.yanhejin.myapplication.Database.CreateSpatialDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class editfeatureActivity extends AppCompatActivity {

    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String spatialdb="SpatialSurveyDB.db";
    CreateSpatialDB createSpatialDB=new CreateSpatialDB(this,dbpath + "/" + spatialdb, null,2);
    MapView mapView;
    static final String XuanEnURL = "http://192.168.106.89:6080/arcgis/rest/services/xuanendata/MapServer";
    String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey"+"/宣恩.tpk";
    GraphicsLayer layer;
    SimpleFillSymbol fillSymbol;
    SimpleLineSymbol lineSymbol;
    boolean isSelected=false;
    int obID;
    String TAG="出错了";
    ArcGISDynamicMapServiceLayer xuanenmap;
    Graphic[] highlightGraphics;
    GraphicsLayer graphicsLayer;
    ArcGISFeatureLayer agflayer=new ArcGISFeatureLayer(XuanEnURL, ArcGISFeatureLayer.MODE.ONDEMAND);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editfeature);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mapView= (MapView) findViewById(R.id.mapview);
        ArcGISLocalTiledLayer localTiledLayer=new ArcGISLocalTiledLayer(path);
        mapView.addLayer(localTiledLayer);
        xuanenmap = new ArcGISDynamicMapServiceLayer(XuanEnURL);
        mapView.addLayer(xuanenmap);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mySelectFeatureSingleTap singleTap=new mySelectFeatureSingleTap();
        mapView.setOnLongPressListener(longPressListener);
        mapView.setOnSingleTapListener(singleTap);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.startedit:
                startEdit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startEdit(){
        SQLiteDatabase sqLiteDatabase=createSpatialDB.getReadableDatabase();
        //Cursor jmdCursor=sqLiteDatabase.rawQuery("select [distinct]  LinkAID as _ID from JMDData", null);
        Cursor jmdCursor=sqLiteDatabase.query(true, "JMDData", new String[]{"LinkAID"}, null, null, null, null, null, null);
        Point startPoint;
        Point endPoint;
        Polygon polygon=new Polygon();
        Polyline polyline=new Polyline();
        int id=jmdCursor.getCount();
        if (id>0){
            jmdCursor.moveToFirst();
            int linkid=jmdCursor.getInt(jmdCursor.getColumnIndex("LinkAID"));
            Cursor cursorjmd=sqLiteDatabase.rawQuery("select * from  JMDData where LinkAID=? order by ID desc",new String[]{String.valueOf(linkid)});
            if (cursorjmd.getCount()>0){
                List<Point> points=new ArrayList<Point>();
                for (int i=0;i<cursorjmd.getCount();i++){
                    cursorjmd.moveToFirst();
                    double x=cursorjmd.getDouble(2);
                    double y=cursorjmd.getDouble(3);
                    Point point=new Point(x,y);
                    points.add(point);
                }
                for (int i=1;i<points.size();i++){
                    startPoint = points.get(i - 1);
                    endPoint = points.get(i);
                    Line line = new Line();
                    line.setStart(startPoint);
                    line.setEnd(endPoint);
                    polygon.addSegment(line, false);
                    fillSymbol=new SimpleFillSymbol(Color.RED);
                    lineSymbol=new SimpleLineSymbol(Color.RED,4, SimpleLineSymbol.STYLE.SOLID);
                    Graphic graphic=new Graphic(polygon,lineSymbol,3);
                    layer=new GraphicsLayer();
                    layer.addGraphic(graphic);
                    mapView.addLayer(layer);
                }
            }
            cursorjmd.close();
        }
        jmdCursor.close();
        Cursor dlCursor=sqLiteDatabase.query(true, "SXData", new String[]{"LinkAID"}, null, null, null, null, null, null);
        int dlid=dlCursor.getCount();
        if (dlid>0){
            dlCursor.moveToFirst();
            int linkid=jmdCursor.getInt(1);
            Cursor cursordl=sqLiteDatabase.rawQuery("select * from  SXData where LinkAID=?",new String[]{String.valueOf(linkid)});
            if (cursordl.getCount()>0){
                cursordl.moveToFirst();
                List<Point> points=new ArrayList<Point>();
                for (int i=0;i<cursordl.getCount();i++){
                    double x=cursordl.getDouble(2);
                    double y=cursordl.getDouble(3);
                    Point point=new Point(x,y);
                    points.add(point);
                }
                for (int i=1;i<points.size();i++){
                    startPoint=points.get(i-1);
                    endPoint=points.get(i);
                    Line line=new Line();
                    line.setStart(startPoint);
                    line.setEnd(endPoint);
                    polyline.addSegment(line, false);
                    lineSymbol=new SimpleLineSymbol(Color.BLACK,4, SimpleLineSymbol.STYLE.SOLID);
                    Graphic graphic=new Graphic(polyline,lineSymbol,3);
                    layer=new GraphicsLayer();
                    layer.addGraphic(graphic);
                    mapView.addLayer(layer);
                }
            }
            cursordl.close();
        }

        dlCursor.close();
        sqLiteDatabase.close();
    }

    class mySelectFeatureSingleTap implements OnSingleTapListener {


        @Override
        public void onSingleTap(float x, float y) {
            Point point=mapView.toMapPoint(x,y);
            Query query=new Query();
            query.setInSpatialReference(mapView.getSpatialReference());
            query.setReturnGeometry(true);
            query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
            query.setGeometry(point);
            agflayer.selectFeatures(query, ArcGISFeatureLayer.SELECTION_METHOD.NEW,new queryCallback());
        }
    }


    class queryCallback implements CallbackListener<FeatureSet> {

        Map<String,Object> atts=new HashMap<String, Object>();
        @Override
        public void onCallback(FeatureSet featureSet) {
            if (featureSet.getGraphics().length>0){
                isSelected=true;
                Graphic graphic=featureSet.getGraphics()[0];
                obID= (int) graphic.getAttributeValue(agflayer.getObjectIdField());
                atts=graphic.getAttributes();
                Set<Map.Entry<String,Object>> entrySet=atts.entrySet();
                for (Map.Entry<String,Object> entry:entrySet){
                    Log.i("Attribute", entry.getKey() + ":" + entry.getValue());
                }
            }
        }

        @Override
        public void onError(Throwable throwable) {
            Log.i(TAG, "qCallBackLis 出错啦"+throwable.getMessage());

        }
    }

    class myEdits implements CallbackListener<FeatureEditResult[][]>{
        @Override
        public void onCallback(FeatureEditResult[][] featureEditResults) {
            if (featureEditResults[2]!=null&&featureEditResults[2][0].isSuccess()){
                Log.i(TAG, "edit 成功啦");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        agflayer.refresh();
                        //dmsLayer.refresh();
                        Toast.makeText(editfeatureActivity.this, "edit 成功", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        public void onError(Throwable throwable) {
            Log.i(TAG, "edit 出错啦"+throwable.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(editfeatureActivity.this,"edit 出错了",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    OnLongPressListener longPressListener=new OnLongPressListener() {
        @Override
        public boolean onLongPress(float x, float y) {
            try{
                if (xuanenmap.isInitialized()){
                    layer.removeAll();
                    Point pointClick=mapView.toMapPoint(x,y);
                    IdentifyParameters inputParameters=new IdentifyParameters();
                    inputParameters.setGeometry(pointClick);
                    inputParameters.setLayers(new int[]{1});
                    Envelope env=new Envelope();
                    mapView.getExtent().queryEnvelope(env);
                    inputParameters.setSpatialReference(mapView.getSpatialReference());
                    inputParameters.setMapExtent(env);
                    inputParameters.setDPI(96);
                    inputParameters.setMapHeight(mapView.getHeight());
                    inputParameters.setMapWidth(mapView.getWidth());
                    inputParameters.setTolerance(10);
                    MyIdentifyTask mIdentify=new MyIdentifyTask();
                    mIdentify.equals(inputParameters);
                }else {
                    Toast.makeText(editfeatureActivity.this,"选择要查询图层",Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return false;
        }
    };

    class  MyIdentifyTask extends AsyncTask<IdentifyParameters,Void,IdentifyResult[]>{
        IdentifyTask mIdentifyTask;
        @Override
        protected IdentifyResult[] doInBackground(IdentifyParameters... params) {
            IdentifyResult[] mResult=null;
            if (params!=null&&params.length>0){
                IdentifyParameters mParams=params[0];
                try {
                    mResult=mIdentifyTask.execute(mParams);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return mResult;

        }

        @Override
        protected void onPostExecute(IdentifyResult[] results) {
            if (results != null && results.length > 0) {

                //生成要素对象数组

                highlightGraphics = new Graphic[results.length];

                Toast toast = Toast.makeText(getApplicationContext(), results.length + " features identified\n", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                for (int i = 0; i < results.length; i++) {
                    Geometry geom = results[i].getGeometry();
                    String typeName = geom.getType().name();
                    //在这里我们进行要素的高亮显示，也就是要素渲染工作

                    Random r = new Random();
                    int color = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));
                    if (typeName.equalsIgnoreCase("point")) {
                        SimpleMarkerSymbol sms = new SimpleMarkerSymbol(color, 20, SimpleMarkerSymbol.STYLE.SQUARE);
                        highlightGraphics[i] = new Graphic(geom, sms);
                    } else if (typeName.equalsIgnoreCase("polyline")) {
                        SimpleLineSymbol sls = new SimpleLineSymbol(color, 5);
                        highlightGraphics[i] = new Graphic(geom, sls);
                    } else if (typeName.equalsIgnoreCase("polygon")) {
                        SimpleFillSymbol sfs = new SimpleFillSymbol(color);
                        sfs.setAlpha(75);
                        highlightGraphics[i] = new Graphic(geom, sfs);
                    }
                    graphicsLayer.addGraphic(highlightGraphics[i]);
                    //clearButton.setEnabled(true);
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No features identified.", Toast.LENGTH_SHORT);
                toast.show();

            }
        }

        @Override
        protected void onPreExecute() {
            mIdentifyTask=new IdentifyTask(XuanEnURL);
        }
    }



}
