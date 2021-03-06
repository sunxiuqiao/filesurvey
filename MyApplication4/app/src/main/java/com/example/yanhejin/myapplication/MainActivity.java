package com.example.yanhejin.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.toolkit.analysis.MeasuringTool;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Feature;
import com.esri.core.map.Field;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.table.FeatureTable;
import com.example.yanhejin.myapplication.ChildActivity.newuser;
import com.example.yanhejin.myapplication.Database.CreateSpatialDB;
import com.example.yanhejin.myapplication.Database.CreateSurveyDB;
import com.example.yanhejin.myapplication.ExcelOutput.ConvertDataToExcel;
import com.example.yanhejin.myapplication.OfflineEdit.GDBUtil;
import com.example.yanhejin.myapplication.OfflineEdit.TemplatePicker;
import com.example.yanhejin.myapplication.OfflineEdit.layerlistadapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CreateSurveyDB createSurveyDB;
    CreateSpatialDB createSpatialDB;
    String dbpath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
    SQLiteDatabase spatialdb;
    SQLiteDatabase attributedb;
    private long exitTime;
    static final String mapURL = "http://cache1.arcgisonline.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer";
    static final String XuanEnURL = "http://192.168.106.89:6080/arcgis/rest/services/xuanendata/MapServer";
    static final String wuhanurl = "http://192.168.1.127:6080/arcgis/rest/services/wuhanroad/MapServer";
    MapView mapView;
    FeatureLayer featureLayer;
    ArcGISLocalTiledLayer tiledLayer;
    FloatingActionButton location;
    LocationManager locationManager;

    ActionMode actionmode;
    ActionMode recorderMode;
    protected static final String TAG = "EditGraphicElements";

    private static final String TAG_DIALOG_FRAGMENTS = "dialog";

    private static final String KEY_MAP_STATE = "com.esri.MapState";

    GraphicsLayer mGraphicLayer;
    SimpleFillSymbol mSimpleFillSymbol = null;

    SimpleLineSymbol simpleLineSymbol;
    MapTouchListener mapTouchListener;

    boolean isMessageLength;

    PictureMarkerSymbol pictureMarkerSymbol;
    Point wgsPoint;
    Point mapPoint;
    Location loc;
    Map<String, Object> atts;
    ArcGISDynamicMapServiceLayer dmsLayer;

    String mMapState;
    final int STATE_ADD_GRAPHIC = 1;//进入 “添加graphics状态，这时候单击地图时操作就添加graphics
    final int STATE_SHOW = 2;//“选中graphics状态“，这时候单击地图时操作就选择一个graphics，并显示该graphics的附加信息”
    int m_State;//状态
    Graphic textgraphic;

    TemplatePicker tp;
    public static ProgressDialog progress;
    public boolean onlineData = true;
    MediaRecorder mr;
    MyTask myTask;
    //Handler mhandler;
    Handler mHandelr = new Handler();
    private SimpleLineSymbol lineSymbol;
    private SimpleMarkerSymbol markerSymbol;
    private SimpleFillSymbol fillSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_State = STATE_SHOW;
        if (savedInstanceState == null) {
            mMapState = null;
        } else {
            mMapState = savedInstanceState.getString(KEY_MAP_STATE);
            Fragment dialogFrag = getFragmentManager().findFragmentByTag(TAG_DIALOG_FRAGMENTS);
            if (dialogFrag != null) {
                ((DialogFragment) dialogFrag).dismiss();
            }
        }

        String spatialpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ArcGISSurvey" + "/" + "SpatialSurveyDB.db";
        String attributepath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ArcGISSurvey" + "/" + "AttributeSurveyDB.db";
        createSpatialDB = new CreateSpatialDB(MainActivity.this, spatialpath, null, 2);
        createSurveyDB = new CreateSurveyDB(MainActivity.this, attributepath, null, 2);
       /* File spatialp=new File(dbpath);
        File spatialf=new File(spatialpath);
        File attributef=new File(attributepath);
        if (!spatialp.exists()){
            spatialp.mkdirs();
        }
        boolean isSpatialCreateSuccess=false;
        boolean isAttributrCreateSuccess=false;
        if (!spatialf.exists()&&!attributef.exists()){
            try {
                isSpatialCreateSuccess=spatialf.createNewFile();
                isAttributrCreateSuccess=attributef.createNewFile();
            }catch (IOException ioex){
                Toast.makeText(MainActivity.this, ioex.toString(), Toast.LENGTH_LONG).show();
            }
        }else {
            isSpatialCreateSuccess=true;
            isAttributrCreateSuccess=true;
        }if (isSpatialCreateSuccess&&isAttributrCreateSuccess){
            spatialdb=SQLiteDatabase.openOrCreateDatabase(spatialf,null);
            attributedb=SQLiteDatabase.openOrCreateDatabase(attributef,null);
        }*/

        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mapView = (MapView) findViewById(R.id.mapview);
        ArcGISTiledMapServiceLayer maplayeronline = new ArcGISTiledMapServiceLayer(mapURL);
        //mapView.addLayer(maplayeronline);
        //mapView.setAlpha((float) 0.5);
        Envelope initextext = new Envelope(12748887.542, 3609934.261, 12713168.721, 3546235.696);
        mapView.setExtent(initextext);
        setSupportActionBar(toolbar);
        mr = new MediaRecorder();

        /*point = (Point) GeometryEngine.project(new Point(40.805, 111.661), SpatialReference.create(4326), mapView.getSpatialReference());
        mapView.centerAt(point, true);
        mapView.enableWrapAround(true);
        mapView.setEsriLogoVisible(true);*/
        /*
        * GPS定位
        * */
        pictureMarkerSymbol = new PictureMarkerSymbol(this.getResources().getDrawable(R.drawable.location));
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //设置GPS
        location = (FloatingActionButton) findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE).setAction("GPS定位", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final List<String> providers = locationManager.getProviders(true);
                        new Thread() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                for (String provider : providers) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for Activity#requestPermissions for more details.
                                            return;
                                        }
                                    }
                                    LocationListener locationListener = new LocationListener() {
                                        /**
                                         * 位置改变时调用
                                         */
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            if (location != null) {
                                                double latitude = location.getLatitude() - 0.0025;
                                                double longitude = location.getLongitude() + 0.005465;
                                                Message msg = handler.obtainMessage();
                                                Point point = new Point(latitude, longitude);
                                                msg.obj = point;
                                                handler.sendMessage(msg);
                                                mGraphicLayer = new GraphicsLayer();
                                                markLocation(location);
                                            }
                                        }

                                        @Override//状态改变时
                                        public void onStatusChanged(String provider, int status, Bundle extras) {

                                        }


                                        @Override  //provider失效时
                                        public void onProviderEnabled(String provider) {

                                        }

                                        @Override  //provider生效时
                                        public void onProviderDisabled(String provider) {

                                        }
                                    };
                                    locationManager.requestLocationUpdates(provider, 100000, 10, locationListener);
                                    loc = locationManager.getLastKnownLocation(provider);
                                    if (loc != null) {
                                        double latitude = loc.getLatitude() - 0.0025;
                                        double longitude = loc.getLongitude() + 0.005465;
                                        Point point = new Point(latitude, longitude);
                                        Message msg = handler.obtainMessage();
                                        msg.obj = point;
                                        handler.sendMessage(msg);
                                        mGraphicLayer = new GraphicsLayer();
                                        markLocation(loc);
                                    }
                                    locationManager.requestLocationUpdates(provider, 60000, 5, locationListener, Looper.myLooper());
                                    Looper.loop();
                                }
                            }
                        }.start();

                    }

                }).show();

            }

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
       /* mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (o==mapView&&status==STATUS.INITIALIZED){
                    mapView.setOnSingleTapListener(new mySelectFeatureSingleTap());
                    SimpleFillSymbol selectsymbol=new SimpleFillSymbol(Color.BLUE);
                    selectsymbol.setAlpha(50);
                    selectsymbol.setOutline(new SimpleLineSymbol(Color.RED, 3));
                    //agflayer.setSelectionSymbol(selectsymbol);
                }
            }
        });*/
    }

    public android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            Point point = (Point) msg.obj;
            double x = point.getX();
            double y = point.getY();
            Toast.makeText(MainActivity.this, "当前位置：" + "东经：" + String.valueOf(y) + "北纬：" + String.valueOf(x), Toast.LENGTH_LONG).show();
        }
    };

    //标记位置
    private void markLocation(Location location) {
        //mGraphicLayer.removeAll();
        List<Point> gpspoint = new ArrayList<Point>();
        Polyline poly = new Polyline();
        Point startpoint;
        Point topoint;
        double locx = location.getLongitude() + 0.005465;
        double locy = location.getLatitude() - 0.0025;
        wgsPoint = new Point(locx, locy);
        gpspoint.add(wgsPoint);
        mapPoint = (Point) GeometryEngine.project(wgsPoint, SpatialReference.create(4326), mapView.getSpatialReference());
        //创建图层
        Graphic graphic = new Graphic(mapPoint, pictureMarkerSymbol);
        mGraphicLayer.addGraphic(graphic);

        while (gpspoint.size() > 1) {
            for (int i = 0; i < gpspoint.size(); i++) {
                startpoint = gpspoint.get(i);
                topoint = gpspoint.get(i + 1);
                poly.startPath(startpoint);
                poly.lineTo(topoint);
                //画线
                Graphic graphicline = new Graphic(poly, new SimpleLineSymbol(Color.BLUE, 4, SimpleLineSymbol.STYLE.SOLID));
                mGraphicLayer.addGraphic(graphicline);
                mapView.centerAt(mapPoint, true);
                mapView.addLayer(mGraphicLayer);
            }
        }
        mapView.centerAt(mapPoint, true);
        mapView.addLayer(mGraphicLayer);
        SQLiteDatabase GPSdb = createSpatialDB.getReadableDatabase();//数据库为空
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA);
        Date currentdate = new Date(System.currentTimeMillis());
        final String GPSdate = format.format(currentdate);
        ContentValues GPSValues = new ContentValues();
        GPSValues.put("x", locx);
        GPSValues.put("y", locy);
        GPSValues.put("GPSdate", GPSdate);
        GPSdb.insert("GPSData", null, GPSValues);
        Toast.makeText(MainActivity.this, "GPS数据保存成功", Toast.LENGTH_LONG).show();
        GPSdb.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.zoomin:
                mapView.zoomin();
                return true;
            case R.id.zoomout:
                mapView.zoomout();
                return true;
            case R.id.locationnow:

                /*Envelope envelope=spatialReference.getPannableExtent();
                mapView.setExtent(envelope);*/
                return true;
            case R.id.download:
                ArcGISDynamicMapServiceLayer xuanenmap = new ArcGISDynamicMapServiceLayer(XuanEnURL);
                mapView.addLayer(xuanenmap);
                return true;

            case R.id.editfeature:
            /*    AlertDialog.Builder location = new AlertDialog.Builder(MainActivity.this);
                View loca = getLayoutInflater().inflate(R.layout.location, null);
                location.setView(loca);
                final EditText jingdu = (EditText) loca.findViewById(R.id.jingdu);
                final EditText weid = (EditText) loca.findViewById(R.id.weidu);
                location.setTitle("自定义位置");
                location.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double x = Double.parseDouble(jingdu.getText().toString());
                        double y = Double.parseDouble(weid.getText().toString());
                        Point point = new Point(x, y);
                        mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), mapView.getSpatialReference());
                        Graphic graphic = new Graphic(mapPoint, pictureMarkerSymbol);
                        mGraphicLayer.addGraphic(graphic);
                        mapView.centerAt(mapPoint, true);
                        mapView.addLayer(mGraphicLayer);

                    }
                });*/
                Intent editfeature=new Intent();
                editfeature.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                editfeature.setClass(MainActivity.this, editfeatureActivity.class);
                startActivity(editfeature);
                return true;
            case R.id.action_settings:
                Intent newuser=new Intent();
                newuser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newuser.setClass(MainActivity.this, newuser.class );
                startActivity(newuser);
                return true;
            case R.id.renwuguanli:
                final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("任务日志");
                View view=getLayoutInflater().inflate(R.layout.renwurizhi,null);
                builder.setView(view);
                EditText renwuname= (EditText) view.findViewById(R.id.dioahuiname);
                EditText renwumiaoshu= (EditText) view.findViewById(R.id.dioahuidiscribe);
                EditText diaohuirenyuan= (EditText) view.findViewById(R.id.dioahuipeople);
                EditText diaohuishebei= (EditText) view.findViewById(R.id.dioahuisehbei);
                EditText diaohuirenwu= (EditText) view.findViewById(R.id.dioahuirenwu);
                builder.setPositiveButton("保存日志", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("下载调绘任务", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addOnlineMap();
                    }
                });
                builder.create().show();
                return true;
            default:
                return false;
        }
    }





    //Toast弹出对话框，自定义弹出内容
    public void AlretMsg(String str, Object... arg) {
        String msg = String.format(str, arg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        Log.i("AlertMsg", msg);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    //选择地图的编辑项
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.addmap:
                View addmapview = getLayoutInflater().inflate(R.layout.addmap, null, false);
                final RadioButton addonlinemap = (RadioButton) addmapview.findViewById(R.id.addonlinemap);
                final RadioButton addlocalmap = (RadioButton) addmapview.findViewById(R.id.addvectormap);
                final AlertDialog.Builder addmapbuilder = new AlertDialog.Builder(MainActivity.this);
                addmapbuilder.setView(addmapview);
                addmapbuilder.setTitle("选择添加的地图");
                addmapbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (addonlinemap.isChecked()) {
                            addOnlineMap();
                        } else if (addlocalmap.isChecked()) {
                            addLocalMap();
                            //getMapPathFromSD();
                        }
                    }
                });
                addmapbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addmapbuilder.setCancelable(true);
                    }
                });
                addmapbuilder.create().show();
                break;
            case R.id.layercontrol:
                LayerControl();
                break;
            case R.id.startedit:
                actionmode = MainActivity.this.startActionMode(actioncallback);
                break;
            case R.id.camara:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
                break;
            case R.id.soundRecord:
                recorderMode=MainActivity.this.startActionMode(MediaRecorderCallback);
                //Intent soundIntent=new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                //startActivity(soundIntent);
                    break;
            case R.id.surveydiatance:
                MeasureDistance();
                break;
            case R.id.biaozhu:
                actionmode=MainActivity.this.startActionMode(zhujiCallback);
                break;
            case R.id.search:
                /*actionmode=MainActivity.this.startActionMode(featureSearch);*/
                Intent selectIntent=new Intent();
                selectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                selectIntent.setClass(MainActivity.this, featurelist.class);
                startActivity(selectIntent);
                break;
            case R.id.datashare:
                Intent dataintent=new Intent();
                dataintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dataintent.setClass(MainActivity.this, ConvertDataToExcel.class);
                startActivity(dataintent);
                break;
            case R.id.featureedit:
                Intent queryIntent=new Intent();
                queryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                queryIntent.setClass(MainActivity.this,queryActivity.class);
                startActivity(queryIntent);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*
    * 图层控制
    * */
   public void LayerControl(){
       ArrayAdapter<String> layerAdpter;
       List<String> layers=new ArrayList<String>();
       final View layerlistview = getLayoutInflater().inflate(R.layout.maplist, null);
       final ListView layerslist = (ListView) layerlistview.findViewById(R.id.maplist);
       for(Layer layer:mapView.getLayers()){
           String name=layer.getName();
            if (name == null)
                name = "宣恩.tpk";
            if(name.equals("Layers"))
                name="宣恩县矢量图";

           String title=layer.getTitle();
           layers.add(name);
       }
       String[] features={"删除","上移","下移","不可见"};
       ListView listView1;
       final View layercontrlw = getLayoutInflater().inflate(R.layout.layercontrollayout, null);
       listView1= (ListView) layercontrlw.findViewById(R.id.listview);
       featureadapter adapter1=new featureadapter(MainActivity.this,features);
       listView1.setAdapter(adapter1);
       layerlistadapter adapter=new layerlistadapter(MainActivity.this,layers);
       layerAdpter=new ArrayAdapter<String>(MainActivity.this,R.layout.item,layers);
       layerslist.setAdapter(adapter);
       AlertDialog.Builder layerbuider=new AlertDialog.Builder(MainActivity.this);
       layerbuider.setTitle("图层控制");
       layerbuider.setView(layerlistview);
       layerbuider.create();
       layerbuider.show();
       layerslist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
       layerslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
               AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
               builder.setView(layercontrlw);
               builder.setTitle("选择图层操作");
               builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               });
               /*builder.setPositiveButton("上移", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       for (Layer layer : mapView.getLayers()) {
                           int index = (int) layer.getID();
                           String url=layer.getUrl();
                           String name=layer.getName();
                           if(url.equals(mapURL)){
                               Toast.makeText(MainActivity.this, "该层不能移动！", Toast.LENGTH_LONG).show();
                           }
                           else if (index >= 1&&!url.equals(mapURL)) {
                               mapView.removeLayer(index);
                               mapView.addLayer(layer, index + 1);
                           } else {
                               Toast.makeText(MainActivity.this, "该层不能移动！", Toast.LENGTH_LONG).show();
                           }
                       }
                   }
               });
               builder.setNeutralButton("下移", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       for (Layer layer : mapView.getLayers()) {
                           int index = (int) layer.getID();
                           String name=layer.getName();
                           String url=layer.getUrl();
                           if(url.equals(mapURL)){
                               Toast.makeText(MainActivity.this, "该层不能移动！", Toast.LENGTH_LONG).show();
                           }
                           if (index >= 1&&!url.equals(mapURL)) {
                               mapView.removeLayer(index);
                               mapView.addLayer(layer, index - 1);
                           } else {
                               Toast.makeText(MainActivity.this, "该层不能移动！", Toast.LENGTH_LONG).show();
                           }
                       }
                   }
               });
               builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       for (Layer layer : mapView.getLayers()) {
                           if (layerslist.getItemAtPosition(position).equals(layer.getName())) {
                               mapView.removeLayer(layer);
                           }
                       }
                       Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_LONG).show();
                   }
               });*/
               builder.create().show();
           }
       });
   }

    /*
    * 单击地图时标记地理注记
    * */

    OnSingleTapListener m_OnSingleTapListener=new OnSingleTapListener() {
        @Override
        public void onSingleTap(float x, float y) {
            if (!mapView.isLoaded())  {
                return;
            }
            if (m_State==STATE_ADD_GRAPHIC){
                AddNewGraphic(x,y);
            }else {
                SelectOnGraphic(x,y);
            }
        }

        private void SelectOnGraphic(float x, float y){
            GraphicsLayer layer=getGraphicLayer();
            if (layer!=null&&layer.isInitialized()&&layer.isVisible()){
                Graphic result=null;
                result=getGraphicFromLayer(x,y,layer);
                if (result!=null){
                    String msgTag= (String) result.getAttributeValue("tag");
                    AlretMsg("注记的要素名称： "+msgTag);
                }
            }
        }

        private void AddNewGraphic(final float x,final float y){
            final SQLiteDatabase zhujiattrDB=createSurveyDB.getReadableDatabase();
            final SQLiteDatabase zhujispatDB=createSpatialDB.getReadableDatabase();
            View zhujiview=getLayoutInflater().inflate(R.layout.dilizhuji, null);
            final EditText zhujiLink= (EditText) zhujiview.findViewById(R.id.zhujiNum);
            final EditText zhujiname= (EditText) zhujiview.findViewById(R.id.zhujiname);
            final EditText zhujitype= (EditText) zhujiview.findViewById(R.id.zhujitype);
            final EditText zhujix= (EditText) zhujiview.findViewById(R.id.zhujix);
            final EditText zhujiy= (EditText) zhujiview.findViewById(R.id.zhujiy);
            final EditText zhujibz= (EditText) zhujiview.findViewById(R.id.zhujibeizhu);
            zhujix.setText(String.valueOf(x));
            zhujiy.setText(String.valueOf(y));
            SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm",Locale.CHINA);
            Date currentdate = new Date(System.currentTimeMillis());
            final String zhujitime = format.format(currentdate);
            AlertDialog.Builder featureBuilder=new AlertDialog.Builder(MainActivity.this);
            featureBuilder.create();
            featureBuilder.setTitle("填写地理注记");
            featureBuilder.setView(zhujiview);
            featureBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String type=zhujitype.getText().toString();
                    String name = zhujiname.getText().toString();
                    String bz=zhujibz.getText().toString();
                    float xatt=Float.parseFloat(zhujix.getText().toString());
                    float yatt=Float.parseFloat(zhujiy.getText().toString());
                    if (zhujiLink.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                    }
                    if (zhujix.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this, "x坐标为空！", Toast.LENGTH_LONG).show();
                    }
                    if (zhujiy.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this, "y坐标为空！", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Cursor zhujinum=zhujiattrDB.rawQuery("select LinkID from PZJData where LinkID=?", new String[]{zhujiLink.getText().toString()});
                        if (!zhujinum.moveToFirst()){
                            ContentValues arrValues=new ContentValues();
                            arrValues.put("LinkID",Integer.valueOf(zhujiLink.getText().toString()));
                            arrValues.put("YSName",name);
                            arrValues.put("YSType", type);
                            arrValues.put("ZJTIME", zhujitime);
                            arrValues.put("BZ", bz);
                            zhujiattrDB.insert("PZJData", null, arrValues);
                            zhujiattrDB.close();
                            Toast.makeText(MainActivity.this, "属性数据保存成功", Toast.LENGTH_LONG).show();
                            ContentValues spatValues=new ContentValues();
                            spatValues.put("LinkID",Integer.valueOf(zhujiLink.getText().toString()));
                            spatValues.put("x",xatt);
                            spatValues.put("y",yatt);
                            zhujispatDB.insert("PZJData", null, spatValues);
                            zhujispatDB.close();
                            Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                            GraphicsLayer layer = getGraphicLayer();
                            if (layer != null && layer.isInitialized() && layer.isVisible()) {
                                Point point = mapView.toMapPoint(new Point(x, y));
                                Map<String, Object> mapname = new HashMap<String, Object>();
                                mapname.put("tag", "" + name);
                                Graphic graphic = CreateGraphic(point, mapname);
                                layer.addGraphic(graphic);
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "连接号重复，重新输入！", Toast.LENGTH_LONG).show();
                        }
                        zhujinum.close();
                    }
                }
            });
            featureBuilder.setNegativeButton("取消", null);
            featureBuilder.show();
        }
    };

    OnSingleTapListener wenzi_OnSingleTapListener=new OnSingleTapListener() {
        @Override
        public void onSingleTap(float x, float y) {
            if (!mapView.isLoaded()) {
                return;
            }
            if (m_State == STATE_ADD_GRAPHIC) {
                AddNewGraphic(x, y);
            } else {
            }
        }
        //文字标注
        private void AddNewGraphic(final float x, final float y) {
            final GraphicsLayer layer = getGraphicLayer();
            final SQLiteDatabase wzdb=createSurveyDB.getReadableDatabase();
            final SQLiteDatabase spatialdb=createSpatialDB.getReadableDatabase();
            if (layer != null && layer.isInitialized() && layer.isVisible()) {
                final Point point=mapView.toMapPoint(new Point(x,y));
                AlertDialog.Builder textBuilder=new AlertDialog.Builder(MainActivity.this);
                View textview=getLayoutInflater().inflate(R.layout.biaozhu, null);
                final EditText ysdmtext= (EditText) textview.findViewById(R.id.featureDM);
                final EditText text= (EditText) textview.findViewById(R.id.zjmc);
                final EditText zhujix= (EditText) textview.findViewById(R.id.zhujix);
                final EditText zhujiy= (EditText) textview.findViewById(R.id.zhujiy);
                final EditText yslxtext= (EditText) textview.findViewById(R.id.ftName);
                final EditText bztext= (EditText) textview.findViewById(R.id.bz);
                zhujix.setText(String.valueOf(x));
                zhujiy.setText(String.valueOf(y));
                SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm",Locale.CHINA);
                Date currentdate = new Date(System.currentTimeMillis());
                final String zhujitime = format.format(currentdate);
                textBuilder.setView(textview);
                textBuilder.create();
                textBuilder.setTitle("填写标注文字");
                textBuilder.setPositiveButton("确定标注", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ysdmtext.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this, "属性连接号为空！", Toast.LENGTH_LONG).show();
                        }
                        if (zhujix.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this, "x坐标为空！", Toast.LENGTH_LONG).show();
                        }
                        if (zhujiy.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this, "y坐标为空！", Toast.LENGTH_LONG).show();
                        }else {
                            Cursor textnumb=wzdb.rawQuery("select LinkID from WZBZData where LinkID=?",new String[]{ysdmtext.getText().toString()});
                            if (!textnumb.moveToFirst()){
                                ContentValues wzValues = new ContentValues();
                                String ysdm = ysdmtext.getText().toString();
                                String name = text.getText().toString();
                                String type = yslxtext.getText().toString();
                                String bz = bztext.getText().toString();
                                TextSymbol textSymbol = new TextSymbol(14, name, Color.BLUE);
                                textSymbol.setFontFamily("DroidSansFallback.ttf");
                                textgraphic = new Graphic(point, textSymbol);
                                layer.addGraphic(textgraphic);
                                wzValues.put("LinkID", ysdm);
                                wzValues.put("YSName", name);
                                wzValues.put("YSType", type);
                                wzValues.put("ZJTIME", zhujitime);
                                wzValues.put("BZ", bz);
                                wzdb.insert("WZBZData", null, wzValues);
                                wzdb.close();
                                Toast.makeText(MainActivity.this, "属性数据保存成功", Toast.LENGTH_LONG).show();
                                textnumb.close();
                                ContentValues spatialValues=new ContentValues();
                                float xatt=Float.parseFloat(zhujix.getText().toString());
                                float yatt=Float.parseFloat(zhujiy.getText().toString());
                                spatialValues.put("LinkAID",ysdm);
                                spatialValues.put("x",xatt);
                                spatialValues.put("y",yatt);
                                spatialdb.insert("WZBZData", null, spatialValues);
                                spatialdb.close();
                                Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "要素代码重复输入！", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                textBuilder.setNegativeButton("取消标注", null);
                textBuilder.show();

            }

        }

    };

    OnSingleTapListener featureedit_OnSingleTapListener=new OnSingleTapListener() {
        @Override
        public void onSingleTap(float x, float y) {
            for(int id:mGraphicLayer.getGraphicIDs())  {
                Graphic graphic= mGraphicLayer.getGraphic(id);
                Geometry.Type type=  graphic.getGeometry().getType();
                double mindistance=5;
                if(type==Geometry.Type.POINT){
                    Point currentpoint= (Point) graphic.getGeometry();
                    double distancex=Math.abs(x-currentpoint.getX());
                    double distancey=Math.abs(y-currentpoint.getY());
                    double distace=Math.sqrt(distancex*distancex+distancey*distancey);
                    if (distace<=mindistance){
                        SimpleMarkerSymbol symbol=new SimpleMarkerSymbol(Color.BLUE,8, SimpleMarkerSymbol.STYLE.CROSS);
                        Graphic pointGraphic=new Graphic(currentpoint,symbol);
                        mGraphicLayer.addGraphic(pointGraphic);

                    }
                }
            }
        }
    };
    /*
    * 在图层中查找获得Graphic对象，x，y是屏幕坐标，layer是GraphicLayer目标图层，相差的距离是50像素
    * */

    private Graphic getGraphicFromLayer(double xScreen,double yScreen,GraphicsLayer layer){
        Graphic result=null;
        try {
            int[] idsArr=layer.getGraphicIDs();
            double x=xScreen;
            double y=yScreen;
            for(int i=0;i<idsArr.length;i++){
                Graphic gpVar=layer.getGraphic(idsArr[i]);
                if (gpVar!=null){
                    Point pointVar= (Point) gpVar.getGeometry();
                    pointVar=mapView.toScreenPoint(pointVar);
                    double x1=pointVar.getX();
                    double y1=pointVar.getY();
                    if (Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1))<50){
                        result =gpVar;
                        break;
                    }
                }
            }
        }catch (Exception e){
            Log.i(TAG,e.toString());
        }
        return result;
    }
    /*
    * 创建图层标注样式
    * */

    private Graphic CreateGraphic(Point geometry,Map<String,Object> map){
        GraphicsLayer layer=getGraphicLayer();
        Drawable image=MainActivity.this.getBaseContext().getResources().getDrawable(R.drawable.pop);
        PictureMarkerSymbol symbol=new PictureMarkerSymbol(image);
        Graphic graphic=new Graphic(geometry,symbol,map);
        layer.addGraphic(graphic);
        return graphic;
    }
    /*
    * 获取编辑的图层
    * */
    private GraphicsLayer getGraphicLayer(){
        if (mGraphicLayer==null){
            mGraphicLayer=new GraphicsLayer();
            mapView.addLayer(mGraphicLayer);
        }
        return mGraphicLayer;
    }


    //拍照
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        View photoview = getLayoutInflater().inflate(R.layout.takephoto, null, false);
        ImageView photo = (ImageView) photoview.findViewById(R.id.takephoto);
        final EditText photoname = (EditText) photoview.findViewById(R.id.photodiscrable);
        final EditText photodescrible = (EditText) photoview.findViewById(R.id.photolocation);

        if (resultCode == Activity.RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.i("TestFile", "SD card is not avaiable/writeable right now.");
                return;
            }
            new DateFormat();
            final String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Toast.makeText(this, name, Toast.LENGTH_LONG).show();
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            //String path=android.os.Environment.getDataDirectory().getAbsolutePath()+"/"+"ArcGISSurvey";
            FileOutputStream b = null;
            File file = new File("/sdcard/ArcGISSurvey/Image/");
            //File file = new File(path+"/SurveyImage/");
            file.mkdirs();// 创建文件夹
            //String fileName =file.getPath()+"/"+name;
            String fileName = "/sdcard/ArcGISSurvey/Image/"+name;
            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                AlertDialog.Builder photobuilder = new AlertDialog.Builder(MainActivity.this);
                photobuilder.setTitle("填写照片属性信息");
                photobuilder.setView(photoview);
                photo.setImageBitmap(bitmap);// 将图片显示在ImageView里
                photobuilder.setNegativeButton("取消", null);
                photobuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase photodb = createSurveyDB.getReadableDatabase();
                        ContentValues photovalues = new ContentValues();
                        photovalues.put("photoname", photoname.getText().toString());
                        photovalues.put("photodescrible", photodescrible.getText().toString());
                        photovalues.put("phototime", name);
                        photodb.insert("PhotoData", null, photovalues);
                        photodb.close();
                        Toast.makeText(MainActivity.this, "保存照片信息成功", Toast.LENGTH_LONG).show();
                    }
                });
                photobuilder.create().show();

            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        }
    }

    //录音
    public ActionMode.Callback MediaRecorderCallback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.mediarecorder, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.startrecorde:
                    mode.setTitle("录音");
                    try {
                        mr.reset();
                        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        File file=new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()+".3gp");
                        //file.mkdirs();
                        mr.setOutputFile(file.getAbsolutePath());
                        mr.prepare();
                        mr.start();
                        myTask=new MyTask();
                        mHandelr.post(myTask);
                        mode.setTitle("录音中...");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.stoprecorde:
                    mr.stop();
                    mr.release();
                    mHandelr.removeCallbacks(myTask);
                    final String time = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)).toString();
                    View recordeView=getLayoutInflater().inflate(R.layout.takerecoder,null);
                    final EditText rename= (EditText) recordeView.findViewById(R.id.recorderlocation);
                    final EditText describe= (EditText) recordeView.findViewById(R.id.recorderdiscrable);
                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    builder.setView(recordeView);
                    builder.setTitle("填写录音信息");
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SQLiteDatabase db = createSurveyDB.getReadableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("Name", rename.getText().toString());
                            values.put("descibe", describe.getText().toString());
                            values.put("recordetime", time);
                            db.insert("RecorderDB", null, values);
                            db.close();
                        }
                    });
                    builder.create().show();
                    mode.setTitle("录音");
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
                mode.finish();
        }
    };

    public class MyTask implements Runnable{

        int times;
        @Override
        public void run() {
            mHandelr.postDelayed(this,1000);
            times++;
        }
    }


    //按下两次退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出外业调绘", Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //查询用户信息
    public void QueryUser() {
        SQLiteDatabase userdb = createSurveyDB.getReadableDatabase();
        Cursor usercursor = userdb.rawQuery("select * from user ", null);
        while (!usercursor.moveToNext()) {
            String UserName = usercursor.getString(usercursor.getColumnIndex("UserName"));
            String PassWord = usercursor.getString(usercursor.getColumnIndex("PassWord"));
            //int Permission=usercursor.getInt(usercursor.getColumnIndex("Permission"));

        }
        usercursor.close();
        userdb.close();
    }

    //添加在线地图2
    public void addOnlineMap() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请填入要加载的地图URL");
        View addurl = getLayoutInflater().inflate(R.layout.addmapurl, null, false);
        final EditText addurltext = (EditText) addurl.findViewById(R.id.url);
        builder.setView(addurl);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String MapURL = addurltext.getText().toString();
                if (MapURL.equals("")) {
                    Toast.makeText(MainActivity.this, "请输入URL链接", Toast.LENGTH_LONG).show();
                }
                ArcGISTiledMapServiceLayer maplayeronline = new ArcGISTiledMapServiceLayer(MapURL);
                try {
                    mapView.addLayer(maplayeronline);
                } catch (Exception e) {
                    e.getMessage();
                    Toast.makeText(MainActivity.this, "请输入有效的URL链接", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.create().show();
    }

    //添加本地图层3
    public void addLocalMap() {
        getMapPathFromSD();
        /*final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filename = "ArcGISSurvey/android.geodatabase";
        String pathname = path + "/" + filename;
        try {

            Geodatabase geodatabase = new Geodatabase(pathname);
            geodatabaseFeatureTable = geodatabase.getGeodatabaseFeatureTableByLayerId(0);
            featureLayer = new FeatureLayer(geodatabaseFeatureTable);
            SimpleLineSymbol simpleMarkerSymbol = new SimpleLineSymbol(Color.BLUE, 3, SimpleLineSymbol.STYLE.SOLID);
            SimpleRenderer simpleRenderer = new SimpleRenderer(simpleMarkerSymbol);
            featureLayer.setRenderer(simpleRenderer);
            mapView.addLayer(featureLayer);
            Toast.makeText(MainActivity.this,"成功",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "载入的地图无效", Toast.LENGTH_LONG).show();
        }*/
    }

    //添加瓦片地图（本地）4

    String Type = null;

    public void setType(String featureType) {
        this.Type = featureType;
    }

    public String getType() {
        return this.Type;
    }

    String featureName;

    public void setFNType(String fnType) {
        this.featureName = fnType;
    }

    public String getFNType() {
        return this.featureName;
    }

    //要素编辑自定义的工具栏在上方
    public ActionMode.Callback actioncallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.featuremenu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.drawjmd:

                    actionmode.setTitle("居民地");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if(m_State==STATE_ADD_GRAPHIC)
                        item.setTitle("停止采集");
                    else {
                        item.setTitle("居民地");
                    }
                    jmdPopup();
                    mapView.setOnTouchListener(mapTouchListener);
                    break;
                case R.id.drawsx:
                    actionmode.setTitle("水系设施");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if(m_State==STATE_ADD_GRAPHIC)
                        item.setTitle("停止采集");
                    else {
                        item.setTitle("水系设施");
                    }
                    sxPopup();
                    mapView.setOnTouchListener(mapTouchListener);
                    break;
                case R.id.drawdl:
                    actionmode.setTitle("交通设施");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if(m_State==STATE_ADD_GRAPHIC)
                        item.setTitle("停止采集");
                    else {
                        item.setTitle("交通设施");
                    }
                    dlPopup();
                    mapView.setOnTouchListener(mapTouchListener);
                    break;
                case R.id.drawgx:
                    actionmode.setTitle("管线设施");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if(m_State==STATE_ADD_GRAPHIC)
                        item.setTitle("停止采集");
                    else {
                        item.setTitle("管线设施");
                    }
                    gxPopup();
                    mapView.setOnTouchListener(mapTouchListener);
                    break;
                case R.id.drawjjl:
                    actionmode.setTitle("境界线");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if(m_State==STATE_ADD_GRAPHIC)
                        item.setTitle("停止采集");
                    else {
                        item.setTitle("境界线");
                    }
                    jjxPopup();
                    mapView.setOnTouchListener(mapTouchListener);
                    break;
                case R.id.drawdm:
                    actionmode.setTitle("地貌土质");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if(m_State==STATE_ADD_GRAPHIC)
                        item.setTitle("停止采集");
                    else {
                        item.setTitle("地貌土质");
                    }
                    dmPopup();
                    mapView.setOnTouchListener(mapTouchListener);
                    break;
                case R.id.drawzb:
                    actionmode.setTitle("植被园林");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if(m_State==STATE_ADD_GRAPHIC)
                        item.setTitle("停止采集");
                    else {
                        item.setTitle("植被园林");
                    }
                    zbPopup();
                    mapView.setOnTouchListener(mapTouchListener);
                    break;
                case R.id.drawzjmc:
                    actionmode.setTitle("文字注记");
                    //actionmode.setTitle("独立地物");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if(m_State==STATE_ADD_GRAPHIC)
                        item.setTitle("停止采集");
                    else {
                        item.setTitle("文字注记");
                    }
                    zhujiPopup();
                    mapView.setOnTouchListener(mapTouchListener);
                    break;
                case R.id.drawdldw:
                    actionmode.setTitle("独立地物");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if(m_State==STATE_ADD_GRAPHIC)
                        item.setTitle("停止采集");
                    else {
                        item.setTitle("独立地物");
                    }
                    dldwPopup();
                    mapView.setOnTouchListener(mapTouchListener);
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mode.finish();
            //依然解决不了和onsingletouchlistener冲突的问题
            //mapView.setOnTouchListener(new MapTouchListener(MainActivity.this,mapView));
        }
    };

    public void showEditMode(){
        int layerCount=0;
        for (Layer layer:mapView.getLayers()){
            if (layer instanceof FeatureLayer){
                layerCount++;
            }
        }
        if (layerCount>0){
            if (mapTouchListener==null){
                mapTouchListener=new MapTouchListener(MainActivity.this,mapView);
                mapView.setOnTouchListener(mapTouchListener);
            }
        }
    }
    /*
    * 注记自定义的工具栏在上方
    * */
    public ActionMode.Callback zhujiCallback =new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater biaozhuinflater=mode.getMenuInflater();
            biaozhuinflater.inflate(R.menu.biaozhumenu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()){

                case R.id.diwubiaozu:
                    mode.setTitle("地理标注");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if (m_State==STATE_ADD_GRAPHIC){
                        item.setTitle("结束标注");
                    }else {
                        item.setTitle("地物标注");
                    }
                    mapView.setOnSingleTapListener(m_OnSingleTapListener);
                    break;
                case R.id.wenzibiaozu:
                    mode.setTitle("文字标注");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if (m_State==STATE_ADD_GRAPHIC){
                        item.setTitle("结束标注");
                    }else {
                        item.setTitle("文字标注");
                    }
                    mapView.setOnSingleTapListener(wenzi_OnSingleTapListener);
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mode.finish();
        }
    };

    /*
    * 编辑地物
    * */
    public ActionMode.Callback editfeaturecallback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.editfeature, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){

                case R.id.startedit:
                    mode.setTitle("开始编辑");
                    m_State=m_State==STATE_ADD_GRAPHIC?STATE_SHOW:STATE_ADD_GRAPHIC;
                    if (m_State==STATE_ADD_GRAPHIC){
                        item.setTitle("停止编辑");
                    }else {
                        item.setTitle("开始编辑");
                    }
                    mapView.setOnSingleTapListener(featureedit_OnSingleTapListener);
                    break;
                case R.id.endedit:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };




    //地图触摸事件
    class MapTouchListener extends MapOnTouchListener {

        private Geometry.Type geoType = null;//用于判定当前选择的几何图形类型
        public Point ptStart = null;//起点
        private Point ptPrevious = null;//上一个点
        private ArrayList<Point> points = null;//记录全部点
        private Polygon tempPolygon = null;//记录绘制过程中的多边形
        final String tag = "TAG";
        GraphicsLayer layer;
        MapView map;
        Context context;
        int j=1;

        private void initSymbols()
        {
            lineSymbol = new SimpleLineSymbol(Color.RED, 2, SimpleLineSymbol.STYLE.DASH);
            markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
            fillSymbol = new SimpleFillSymbol(Color.RED);
            fillSymbol.setAlpha(33);
        }
        public MapTouchListener(Context context, MapView view) {
            super(context, view);
            this.context=context;
            map=view;
            points = new ArrayList<Point>();
            //markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
            this.initSymbols();
        }

        // 根据用户选择设置当前绘制的几何图形类型
        public void setType(String geometryType) {
            if (geometryType.equalsIgnoreCase("Point")) {
                this.geoType = Geometry.Type.POINT;
            } else if (geometryType.equalsIgnoreCase("Polyline")) {
                this.geoType = Geometry.Type.POLYLINE;
            } else if (geometryType.equalsIgnoreCase("Polygon")) {
                this.geoType = Geometry.Type.POLYGON;
            }
        }


        public Geometry.Type getType() {
            return this.geoType;
        }

        @Override
        public boolean onSingleTap(MotionEvent point) {
            lineSymbol = new SimpleLineSymbol(Color.RED, 2, SimpleLineSymbol.STYLE.DASH);
            markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
            fillSymbol = new SimpleFillSymbol(Color.RED);
            GraphicsLayer layer=getGraphicLayer();
                if (geoType != null) {
                    Point ptCurrent = map.toMapPoint(new Point(point.getX(), point.getY()));
                    points.add(ptCurrent);
                    if (ptStart == null) {//起始点为空
                        ptStart = ptCurrent;
                        if (geoType == Geometry.Type.POINT) {//直接画点
                            Graphic graphic = new Graphic(ptCurrent, markerSymbol);
                            layer.addGraphic(graphic);
                        }
                    } else {//起始点不为空
                        if (geoType == Geometry.Type.POINT) {
                            Graphic graphic = new Graphic(ptCurrent, markerSymbol);
                            layer.addGraphic(graphic);
                        }
                        //生成当前线段（由当前点和上一个点构成）
                        if (geoType == Geometry.Type.POLYLINE) {
                            //绘制当前线段
                            Polyline polyline = new Polyline();
                            Line line = new Line();
                            line.setStart(ptPrevious);
                            line.setEnd(ptCurrent);
                            polyline.addSegment(line, true);
                            Graphic g = new Graphic(polyline, lineSymbol);
                            layer.addGraphic(g);
                            String length = Double.toString(Math.round(polyline.calculateLength2D())) + " 米";

                            Toast.makeText(map.getContext(), length, Toast.LENGTH_SHORT).show();
                        } else if (geoType == Geometry.Type.POLYGON) {
                            Line line = new Line();
                            line.setStart(ptPrevious);
                            line.setEnd(ptCurrent);
                            //绘制临时多边形
                            if (tempPolygon == null) tempPolygon = new Polygon();
                            tempPolygon.addSegment(line, true);
                            Graphic g = new Graphic(tempPolygon, fillSymbol);
                            layer.addGraphic(g);
                       /* String sArea = getAreaString(tempPolygon.calculateArea2D()) + " 米";
                        Toast.makeText(mapView.getContext(), sArea, Toast.LENGTH_SHORT).show();*/
                        }
                    }
                    ptPrevious = ptCurrent;
                }

            else {
                return true;
            }
            return true;
        }

        /*
        * 双节结束要素采集并且要素信息入库
        * */
        //@Override
        public boolean onDoubleTap(MotionEvent point) {
            j++;
            SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm",Locale.CHINA);
            Date currentdate = new Date(System.currentTimeMillis());
            final String zhujitime = format.format(currentdate);
            GraphicsLayer layer=getGraphicLayer();
            int pointid = 0;
            int graphicid = 0;

            if (geoType == Geometry.Type.POINT) {
                for (int i = 0; i < points.size(); i++) {
                    Point point1 = points.get(i);
                    double x = point1.getX();
                    double y = point1.getY();
                    Toast.makeText(MainActivity.this, "当前坐标：x:" + x + "," + "y:" + y, Toast.LENGTH_LONG).show();
                }
            }
            if (geoType == Geometry.Type.POLYLINE) {
                Polyline polyline = new Polyline();
                Point startPoint = null;
                Point endPoint = null;
                // 绘制完整的线段
                if (points.size() < 2) {
                    Toast.makeText(MainActivity.this, "绘制点数小于2不能形成线段", Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    for (int i = 1; i < points.size(); i++) {
                        startPoint = points.get(i - 1);
                        endPoint = points.get(i);
                        Line line = new Line();
                        line.setStart(startPoint);
                        line.setEnd(endPoint);
                        polyline.addSegment(line, false);
                    }
                    Graphic g = new Graphic(polyline, lineSymbol);
                    layer.addGraphic(g);
                    map.addLayer(layer);
                    // 计算总长度
                    String length = Double.toString(Math.round(polyline.calculateLength2D())) + " 米";

                    Toast.makeText(map.getContext(), length, Toast.LENGTH_SHORT).show();
                }

            } else if (geoType == Geometry.Type.POLYGON) {
                Polygon polygon = new Polygon();

                Point startPoint = null;
                Point endPoint = null;
                // 绘制完整的多边形
                if (points.size() < 2) {
                    Toast.makeText(MainActivity.this, "绘制点数小于3不能形成闭合面", Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    for (int i = 1; i < points.size(); i++) {
                        startPoint = points.get(i - 1);
                        endPoint = points.get(i);

                        Line line = new Line();
                        line.setStart(startPoint);
                        line.setEnd(endPoint);

                        polygon.addSegment(line, false);
                        pointid += points.size();
                        graphicid++;
                    }
                    final String fid = String.valueOf(pointid) + String.valueOf(graphicid);
                    Graphic g = new Graphic(polygon, fillSymbol);
                    layer.addGraphic(g);
                    map.addLayer(layer);
                    // 计算总面积
                    String sArea = getAreaString(polygon.calculateArea2D()) + " 米";

                    Toast.makeText(map.getContext(), sArea, Toast.LENGTH_SHORT).show();
                }

            } else {
                return true;
            }
            if (geoType==null){
                return true;
            }
            else {
                switch (Type) {
                    case "jmdmenu":
                        AddJMDFeatureData(zhujitime,j);
                        break;
                    case "shuiximenu":
                        AddSXFeatureData(zhujitime);
                        break;
                    case "daolumenu":
                        AddDLFeatureData(zhujitime);
                        break;
                    case "zhibeimenu":
                        AddZBFeatureData(zhujitime);
                        break;
                    case "guanxianmenu":
                        AddGXFeatureData(zhujitime);
                        break;
                    case "jjxmenu":
                        AddJJXFeatureData(zhujitime);
                        break;
                    case "zjmenu":
                        AddZJFeatureData(zhujitime);
                        break;
                    case "dimaopmenu":
                        AddDMFeatureData(zhujitime);
                        break;
                    case "dulidiwumenu":
                        AddDLDWFeatureData(zhujitime);
                        break;
                    default:
                        break;

                }
                ptStart = null;
                ptPrevious = null;
                tempPolygon = null;
                return true;
            }
        }
        /*
        * 居民地信息入库
        * */
        public void AddJMDFeatureData(final String zhujitime,int i){
            final AlertDialog.Builder jmddata = new AlertDialog.Builder(MainActivity.this);
            View jmdview = getLayoutInflater().inflate(R.layout.jumindi, null);
            jmddata.setView(jmdview);
            jmddata.setTitle("录入居民地属性信息");
            final EditText linkData = (EditText) jmdview.findViewById(R.id.linkID);
            final EditText fnameText = (EditText) jmdview.findViewById(R.id.ftName);
            final EditText fwcstext = (EditText) jmdview.findViewById(R.id.fwcs);
            final EditText fwcztext = (EditText) jmdview.findViewById(R.id.fwcz);
            final EditText fygztext = (EditText) jmdview.findViewById(R.id.fygz);
            final EditText bztext = (EditText) jmdview.findViewById(R.id.bz);
            fnameText.setText(featureName);
            linkData.setText(String.valueOf(001+i));
            final SQLiteDatabase jmdattributedb = createSurveyDB.getReadableDatabase();
            jmddata.setNegativeButton("取消录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    points.clear();
                }
            });
            jmddata.setPositiveButton("确定录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (fwcstext.getText().toString().equals("") || linkData.getText().toString().equals("")) {
                            if (linkData.getText().toString().equals("")) {
                                Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                            }
                            if (fwcstext.getText().toString().equals("")) {
                                Toast.makeText(MainActivity.this, "房屋层数为空！", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Cursor linkc = jmdattributedb.rawQuery("select LinkID from JMDData where LinkID=?", new String[]{linkData.getText().toString()});
                            if (!linkc.moveToFirst()) {
                                ContentValues jmdattrvalues = new ContentValues();
                                jmdattrvalues.put("FTName", featureName);
                                jmdattrvalues.put("LinkID", Integer.parseInt(linkData.getText().toString()));
                                jmdattrvalues.put("FWCS", Integer.parseInt(fwcstext.getText().toString()));
                                jmdattrvalues.put("FWCZ", fwcztext.getText().toString());
                                jmdattrvalues.put("FYGZ", Float.valueOf(fygztext.getText().toString()));
                                jmdattrvalues.put("ZJTIME",zhujitime);
                                jmdattrvalues.put("BZ", bztext.getText().toString());
                                jmdattributedb.insert("JMDData", null, jmdattrvalues);
                                jmdattributedb.close();
                                Toast.makeText(MainActivity.this, "属性数据存储成功", Toast.LENGTH_LONG).show();
                                try {
                                    int linkID = Integer.parseInt(linkData.getText().toString());
                                    SQLiteDatabase jmdspatialdb = createSpatialDB.getReadableDatabase();
                                    Point currentPoint = null;
                                    if (String.valueOf(linkID).equals("")) {
                                        Toast.makeText(MainActivity.this, "linkID不存在，已取消空间数据录入！", Toast.LENGTH_LONG).show();
                                    } else {
                                        if (points.size() <= 2) {
                                            Toast.makeText(MainActivity.this, "没有要存储的空间数据", Toast.LENGTH_LONG).show();
                                        }
                                        if (points.size() > 2) {
                                            for (int i = 1; i < points.size(); i++) {
                                                currentPoint = points.get(i - 1);
                                                float x = (float) currentPoint.getX();
                                                float y = (float) currentPoint.getY();
                                                ContentValues jmdvalues = new ContentValues();
                                                jmdvalues.put("LinkAID", linkID);
                                                jmdvalues.put("x", x);
                                                jmdvalues.put("y", y);
                                                jmdspatialdb.insert("JMDData", null, jmdvalues);
                                            }
                                            Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                                            points.clear();
                                            jmdspatialdb.close();
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.i(tag, e.toString());
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "连接号重复，重新输入！", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.i(tag, e.toString());
                    }
                }
            });
            jmddata.create().show();
        }

        /*
        * 水系信息入库
        * */
        public void AddSXFeatureData(final String zhujitime){
            AlertDialog.Builder shuixiBuilder = new AlertDialog.Builder(MainActivity.this);
            View shuixiView = getLayoutInflater().inflate(R.layout.shuixi, null);
            final EditText ftnametext = (EditText) shuixiView.findViewById(R.id.ftName);
            final EditText LinkID = (EditText) shuixiView.findViewById(R.id.linkID);
            final EditText featurename = (EditText) shuixiView.findViewById(R.id.featurename);
            final EditText fssstext = (EditText) shuixiView.findViewById(R.id.fsss);
            final EditText sxbztext = (EditText) shuixiView.findViewById(R.id.bz);
            final SQLiteDatabase shuixidb = createSurveyDB.getReadableDatabase();
            ftnametext.setText(featureName);
            shuixiBuilder.setTitle("填写水系属性信息");
            shuixiBuilder.setView(shuixiView);
            shuixiBuilder.setNegativeButton("取消录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    points.clear();
                }
            });
            shuixiBuilder.setPositiveButton("确定录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (LinkID.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                        } else {
                            Cursor linkc = shuixidb.rawQuery("select LinkID from JMDData where LinkID=?", new String[]{LinkID.getText().toString()});
                            if (!linkc.moveToFirst()) {
                                ContentValues shuixiValues = new ContentValues();
                                shuixiValues.put("FTName", featureName);
                                shuixiValues.put("LinkID", Integer.parseInt(LinkID.getText().toString()));
                                shuixiValues.put("YSMC", featurename.getText().toString());
                                shuixiValues.put("FSSS", fssstext.getText().toString());
                                shuixiValues.put("ZJTIME",zhujitime);
                                shuixiValues.put("BZ", sxbztext.getText().toString());
                                shuixidb.insert("SXData", null, shuixiValues);
                                shuixidb.close();
                                Toast.makeText(MainActivity.this, "属性数据存储成功！", Toast.LENGTH_LONG).show();
                                try {
                                    int linkID = Integer.parseInt(LinkID.getText().toString());
                                    SQLiteDatabase shuixispatialdb = createSpatialDB.getReadableDatabase();
                                    Point currentPoint = null;
                                    if (LinkID.getText().toString().equals("")) {
                                        Toast.makeText(MainActivity.this, "linkID不存在，已取消空间数据录入！", Toast.LENGTH_LONG).show();
                                    } else {
                                        if (points.size() < 3) {
                                            Toast.makeText(MainActivity.this, "没有要存储的空间数据", Toast.LENGTH_LONG).show();
                                        } else if (points.size() > 2) {
                                            ContentValues shuixivalues = new ContentValues();
                                            for (int i = 0; i < points.size(); i++) {
                                                currentPoint = points.get(i);
                                                shuixivalues.put("LinkAID", linkID);
                                                shuixivalues.put("x", currentPoint.getX());
                                                shuixivalues.put("y", currentPoint.getY());
                                                shuixispatialdb.insert("SXData", null, shuixivalues);
                                            }
                                            Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                                            points.clear();
                                            shuixispatialdb.close();
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.i(tag, e.toString());
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "连接号重复，重新输入！", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.i(tag, e.toString());
                    }
                }
            });
            shuixiBuilder.create().show();
        }

        /*
        * 道路信息入库
        * */
        public void AddDLFeatureData(final String zhujitime){
            AlertDialog.Builder dlBuildr = new AlertDialog.Builder(MainActivity.this);
            View dlView = getLayoutInflater().inflate(R.layout.daolu, null);
            EditText fdlName = (EditText) dlView.findViewById(R.id.ftName);
            final EditText dlLinkId = (EditText) dlView.findViewById(R.id.linkID);
            final EditText dlmctext = (EditText) dlView.findViewById(R.id.dlmc);
            final EditText dlxhtext = (EditText) dlView.findViewById(R.id.dlxh);
            final EditText djdmtext = (EditText) dlView.findViewById(R.id.djdm);
            final EditText dlbztext= (EditText) dlView.findViewById(R.id.bz);
            fdlName.setText(featureName);
            dlBuildr.setView(dlView);
            dlBuildr.setTitle("填写道路属性信息");
            final SQLiteDatabase daoludb=createSurveyDB.getReadableDatabase();
            dlBuildr.setNegativeButton("取消录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    points.clear();
                }
            });
            dlBuildr.setPositiveButton("确定录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dlLinkId.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                    } else {
                        Cursor dllink = daoludb.rawQuery("select LinkID from DLData where LinkID=?", new String[]{dlLinkId.getText().toString()});
                        if (!dllink.moveToFirst()) {
                            ContentValues dlValues = new ContentValues();
                            dlValues.put("FTName", featureName);
                            dlValues.put("LinkID", Integer.valueOf(dlLinkId.getText().toString()));
                            dlValues.put("DLMC", dlmctext.getText().toString());
                            dlValues.put("DLXH", dlxhtext.getText().toString());
                            dlValues.put("DJDM", djdmtext.getText().toString());
                            dlValues.put("ZJTIME",zhujitime);
                            dlValues.put("BZ", dlbztext.getText().toString());
                            daoludb.insert("DLData", null, dlValues);
                            daoludb.close();
                            Toast.makeText(MainActivity.this, "属性数据存储成功！", Toast.LENGTH_LONG).show();
                            try {
                                int linkID = Integer.valueOf(dlLinkId.getText().toString());
                                SQLiteDatabase daoluspatialdb = createSpatialDB.getReadableDatabase();
                                Point currentpoint;
                                if (points.size() < 2) {
                                    Toast.makeText(MainActivity.this, "没有要存储的空间数据", Toast.LENGTH_LONG).show();
                                } else if (points.size() > 1) {
                                    ContentValues dlspatialValues = new ContentValues();
                                    for (int i = 0; i < points.size(); i++) {
                                        currentpoint = points.get(i);
                                        dlspatialValues.put("LinkAID", linkID);
                                        dlspatialValues.put("x", currentpoint.getX());
                                        dlspatialValues.put("y", currentpoint.getY());
                                        daoluspatialdb.insert("DLData", null, dlspatialValues);
                                    }
                                    daoluspatialdb.close();
                                    Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                                    points.clear();
                                    dllink.close();
                                }
                            } catch (Exception e) {
                                Log.i(tag, e.toString());
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "连接号重复，重新输入！", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            dlBuildr.create().show();
        }

        /*
        * 植被信息入库
        * */
        public void AddZBFeatureData(final String zhujitime){
            AlertDialog.Builder zbBuilder=new AlertDialog.Builder(MainActivity.this);
            View zbView=getLayoutInflater().inflate(R.layout.zhibei,null);
            EditText zbFName= (EditText) zbView.findViewById(R.id.ftName);
            final EditText zbLinkID= (EditText) zbView.findViewById(R.id.linkID);
            final EditText zbmctext= (EditText) zbView.findViewById(R.id.zbmc);
            final EditText zbzltext= (EditText) zbView.findViewById(R.id.zbzl);
            final EditText sslctext= (EditText) zbView.findViewById(R.id.sslc);
            final EditText zbbztext= (EditText) zbView.findViewById(R.id.bz);
            zbBuilder.setView(zbView);
            zbBuilder.setTitle("填写植被属性信息");
            final SQLiteDatabase zbattributedb=createSurveyDB.getReadableDatabase();
            zbFName.setText(featureName);
            zbBuilder.setNegativeButton("取消录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    points.clear();
                }
            });
            zbBuilder.setPositiveButton("确定录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (zbLinkID.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                    }else {
                        Cursor zbCousor=zbattributedb.rawQuery("select LinkID from ZBData where LinkID=?",new String[]{zbLinkID.getText().toString()});
                        if (!zbCousor.moveToFirst()){
                            ContentValues zbattriValues=new ContentValues();
                            zbattriValues.put("FTName", featureName);
                            zbattriValues.put("LinkID",Integer.valueOf(zbLinkID.getText().toString()));
                            zbattriValues.put("YSMC",zbmctext.getText().toString());
                            zbattriValues.put("YSZL",zbzltext.getText().toString());
                            zbattriValues.put("SSLC",sslctext.getText().toString());
                            zbattriValues.put("ZJTIME",zhujitime);
                            zbattriValues.put("BZ", zbbztext.getText().toString());
                            zbattributedb.insert("ZBData", null, zbattriValues);
                            zbattributedb.close();
                            zbCousor.close();
                            Toast.makeText(MainActivity.this, "属性数据存储成功！", Toast.LENGTH_LONG).show();
                            try {
                                int linkID=Integer.valueOf(zbLinkID.getText().toString());
                                SQLiteDatabase zbspatialdb=createSpatialDB.getReadableDatabase();
                                Point currentpoint;
                                if (points.size()<3){
                                    Toast.makeText(MainActivity.this, "没有要存储的空间数据", Toast.LENGTH_LONG).show();
                                }else if (points.size()>2){
                                    for (int i=0;i<points.size();i++){
                                        currentpoint=points.get(i);
                                        ContentValues zbspatialValues=new ContentValues();
                                        zbspatialValues.put("LinkAID",linkID);
                                        zbspatialValues.put("x",currentpoint.getX());
                                        zbspatialValues.put("y",currentpoint.getY());
                                        zbspatialdb.insert("ZBData",null,zbspatialValues);
                                    }
                                    zbspatialdb.close();
                                    Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                                    points.clear();
                                }
                            }catch (Exception e){
                                Log.i(tag,e.toString());
                            }
                        }
                    }
                }
            });
            zbBuilder.create().show();
        }

        /*
        * 管线和电力线信息入库
        * */
        public void AddGXFeatureData(final String zhujitime){
            AlertDialog.Builder gxBuilder=new AlertDialog.Builder(MainActivity.this);
            View gxView=getLayoutInflater().inflate(R.layout.guanxian,null);
            EditText gxname= (EditText) gxView.findViewById(R.id.ftName);
            final EditText gxLinkID= (EditText) gxView.findViewById(R.id.linkID);
            final EditText dlxzx= (EditText) gxView.findViewById(R.id.dlxzx);
            final EditText dlxfs= (EditText) gxView.findViewById(R.id.dlxfs);
            final EditText gxbz= (EditText) gxView.findViewById(R.id.bz);
            gxname.setText(featureName);
            gxBuilder.setView(gxView);
            gxBuilder.setTitle("填写管线和电力线属性信息");
            final SQLiteDatabase gxattributedb=createSurveyDB.getReadableDatabase();
            gxBuilder.setNegativeButton("取消录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    points.clear();
                }
            });
            gxBuilder.setPositiveButton("确定录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (gxLinkID.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                    } else {
                        Cursor gxCursor = gxattributedb.rawQuery("select LinkID from GXData where LinkID=?", new String[]{gxLinkID.getText().toString()});
                        if (!gxCursor.moveToFirst()) {
                            ContentValues gxattriValues = new ContentValues();
                            gxattriValues.put("FTName", featureName);
                            gxattriValues.put("LinkID", Integer.valueOf(gxLinkID.getText().toString()));
                            gxattriValues.put("DLXZX", dlxzx.getText().toString());
                            gxattriValues.put("DLXFS", dlxfs.getText().toString());
                            gxattriValues.put("ZJTIME", zhujitime);
                            gxattriValues.put("BZ", gxbz.getText().toString());
                            gxattributedb.insert("GXData", null, gxattriValues);
                            gxattributedb.close();
                            try {
                                int LinkID = Integer.valueOf(gxLinkID.getText().toString());
                                if (points.size() < 3) {
                                    Toast.makeText(MainActivity.this, "没有要存储的空间数据", Toast.LENGTH_LONG).show();
                                } else if (points.size() > 2) {
                                    Point currrentPoint;
                                    SQLiteDatabase gxspatialdb = createSpatialDB.getReadableDatabase();
                                    for (int i = 0; i < points.size(); i++) {
                                        currrentPoint = points.get(i);
                                        ContentValues gxspatialValues = new ContentValues();
                                        gxspatialValues.put("LinkAID", LinkID);
                                        gxspatialValues.put("x", currrentPoint.getX());
                                        gxspatialValues.put("y", currrentPoint.getY());
                                        gxspatialdb.insert("GXData", null, gxattriValues);
                                    }
                                    gxspatialdb.close();
                                    Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                                    points.clear();
                                }
                            } catch (Exception e) {
                                Log.i(tag, e.toString());
                            }
                        }
                        gxCursor.close();
                    }
                }
            });
            gxBuilder.create().show();
        }

        /*
        * 境界线信息入库
        * */
        public void AddJJXFeatureData(final String zhujitime){
            AlertDialog.Builder jjxBuilder=new AlertDialog.Builder(MainActivity.this);
            View jjxView=getLayoutInflater().inflate(R.layout.jingjie,null);
            final EditText jjxlinkID= (EditText) jjxView.findViewById(R.id.linkID);
            EditText jjxname= (EditText) jjxView.findViewById(R.id.ftName);
            final EditText jjxgj= (EditText) jjxView.findViewById(R.id.gj);
            final EditText jjxnbjx= (EditText) jjxView.findViewById(R.id.nbjjx);
            final EditText jjxbz= (EditText) jjxView.findViewById(R.id.bz);
            jjxname.setText(featureName);
            final SQLiteDatabase jjxattributedb=createSurveyDB.getReadableDatabase();
            jjxBuilder.setView(jjxView);
            jjxBuilder.setTitle("填写境界线属性信息");
            jjxBuilder.setNegativeButton("取消录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    points.clear();
                }
            });
            jjxBuilder.setPositiveButton("确定录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (jjxlinkID.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                    } else {
                        Cursor jjxcursor = jjxattributedb.rawQuery("select LinkID from JJXData where LinkAID=?", new String[]{jjxlinkID.getText().toString()});
                        if (!jjxcursor.moveToFirst()) {
                            SQLiteDatabase jjxattributedb = createSurveyDB.getReadableDatabase();
                            ContentValues jjxattriValues = new ContentValues();
                            jjxattriValues.put("FTName", featureName);
                            jjxattriValues.put("LinkID", Integer.valueOf(jjxlinkID.getText().toString()));
                            jjxattriValues.put("GJ", jjxgj.getText().toString());
                            jjxattriValues.put("NBJJX", jjxnbjx.getText().toString());
                            jjxattriValues.put("ZJTIME",zhujitime);
                            jjxattriValues.put("BZ", jjxbz.getText().toString());
                            jjxattributedb.insert("JJXData", null, jjxattriValues);
                            jjxattributedb.close();
                            try {
                                int LinkID = Integer.valueOf(jjxlinkID.getText().toString());
                                if (points.size() < 3) {
                                    Toast.makeText(MainActivity.this, "没有要存储的空间数据", Toast.LENGTH_LONG).show();
                                } else if (points.size() > 2) {
                                    SQLiteDatabase jjxspatialdb = createSpatialDB.getReadableDatabase();
                                    Point currrentPoint;
                                    for (int i = 0; i < points.size(); i++) {
                                        currrentPoint = points.get(i);
                                        ContentValues jjxspatialValues = new ContentValues();
                                        jjxattriValues.put("LinkAID", LinkID);
                                        jjxattriValues.put("x", currrentPoint.getX());
                                        jjxspatialValues.put("y", currrentPoint.getY());
                                        jjxspatialdb.insert("JJXData", null, jjxspatialValues);
                                    }
                                    jjxspatialdb.close();
                                    Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                                    points.clear();
                                }
                            } catch (Exception e) {
                                Log.i(tag, e.toString());
                            }
                        }
                        jjxcursor.close();
                    }
                }
            });
            jjxBuilder.create().show();
        }

        /*
        * 注记信息入库
        * */
        public void AddZJFeatureData(final String zhujitime){
            AlertDialog.Builder zjBuilder=new AlertDialog.Builder(MainActivity.this);
            View zjView=getLayoutInflater().inflate(R.layout.zhuji,null);
            final EditText zjtype= (EditText) zjView.findViewById(R.id.ftName);
            final EditText zjLinkID= (EditText) zjView.findViewById(R.id.linkID);
            final EditText zhmc= (EditText) zjView.findViewById(R.id.zjmc);
            final EditText ZJBZ= (EditText) zjView.findViewById(R.id.bz);
            final SQLiteDatabase zjattributedb=createSurveyDB.getReadableDatabase();
            zjBuilder.setTitle("填写地理注记属性信息");
            zjBuilder.setView(zjView);
            zjBuilder.setNegativeButton("取消录入", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    points.clear();
                }
            });
            zjBuilder.setPositiveButton("确定录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (zjLinkID.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                    }else {
                        Cursor zjCursor=zjattributedb.rawQuery("select LinkID from ZJData where LinkID=?",new String[]{zjLinkID.getText().toString()});
                        if (!zjCursor.moveToFirst()){
                            ContentValues zjattriValues=new ContentValues();
                            zjattriValues.put("FTName",zjtype.getText().toString());
                            zjattriValues.put("LinkID",Integer.valueOf(zjLinkID.getText().toString()));
                            zjattriValues.put("ZJMC",zhmc.getText().toString());
                            zjattriValues.put("ZJTIME",zhujitime);
                            zjattriValues.put("BZ", ZJBZ.getText().toString());
                            zjattributedb.insert("ZJData", null, zjattriValues);
                            zjattributedb.close();
                            try {
                                if (points.size()<3){
                                    Toast.makeText(MainActivity.this, "没有要存储的空间数据", Toast.LENGTH_LONG).show();
                                }
                                else if (points.size()>2){
                                    SQLiteDatabase zjspatial=createSpatialDB.getReadableDatabase();
                                    ContentValues zjspatialValues=new ContentValues();
                                    Point currentPoints;
                                    for (int i=0;i<points.size();i++){
                                        currentPoints=points.get(i);
                                        zjspatialValues.put("LinkAID",Integer.valueOf(zjLinkID.getText().toString()));
                                        zjspatialValues.put("x",currentPoints.getX());
                                        zjspatialValues.put("y",currentPoints.getY());
                                        zjspatial.insert("ZJData", null, zjspatialValues);
                                    }
                                    zjspatial.close();
                                    Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                                    points.clear();
                                }
                            }catch (Exception e){
                                Log.i(tag,e.toString());
                            }
                        }
                        zjCursor.close();
                    }
                }
            });
            zjBuilder.create().show();
        }

        /*
        * 地貌信息入库
        * */
        public void AddDMFeatureData(final String zhujitime){
            AlertDialog.Builder dmBuilder=new AlertDialog.Builder(MainActivity.this);
            View dmView=getLayoutInflater().inflate(R.layout.dimao,null);
            EditText dmName= (EditText) dmView.findViewById(R.id.ftName);
            final EditText dmLinkID= (EditText) dmView.findViewById(R.id.linkID);
            final EditText dmMC= (EditText) dmView.findViewById(R.id.dmmc);
            final EditText dmbz= (EditText) dmView.findViewById(R.id.bz);
            dmName.setText(featureName);
            dmBuilder.setView(dmView);
            dmBuilder.setTitle("填写独立地物属性信息");
            dmBuilder.setNegativeButton("取消录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    points.clear();
                }
            });
            dmBuilder.setPositiveButton("确定录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SQLiteDatabase dmattributedb=createSurveyDB.getReadableDatabase();
                    if (dmLinkID.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                    }else {
                        Cursor dmC=dmattributedb.rawQuery("select LinkID from DMData where LinkID=?",new String[]{dmLinkID.getText().toString()});
                        if (!dmC.moveToFirst()){
                            ContentValues dmaValues=new ContentValues();
                            dmaValues.put("FTName",featureName);
                            dmaValues.put("LinkID",Integer.valueOf(dmLinkID.getText().toString()));
                            dmaValues.put("DMMC",dmMC.getText().toString());
                            dmaValues.put("ZJTIME",zhujitime);
                            dmaValues.put("BZ", dmbz.getText().toString());
                            dmattributedb.insert("DMData", null, dmaValues);
                            dmattributedb.close();
                            try {
                                if (points.size()<3){
                                    Toast.makeText(MainActivity.this, "没有要存储的空间数据", Toast.LENGTH_LONG).show();
                                }else if (points.size()>2){
                                    SQLiteDatabase dmsdb=createSpatialDB.getReadableDatabase();
                                    Point cPoint;
                                    for (int i=0;i<points.size();i++){
                                        cPoint=points.get(i);
                                        ContentValues dmsValues=new ContentValues();
                                        dmsValues.put("LinkAID",Integer.valueOf(dmLinkID.getText().toString()));
                                        dmsValues.put("x",cPoint.getX());
                                        dmsValues.put("y",cPoint.getY());
                                        dmsdb.insert("DMData",null,dmsValues);
                                    }
                                    dmsdb.close();
                                    Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                                    points.clear();
                                }
                            }catch (Exception e){
                                Log.i(tag,e.toString());
                            }
                        }
                        dmC.close();
                    }
                }
            });
            dmBuilder.create().show();
        }

        public void AddDLDWFeatureData(final String zhujitime){
            AlertDialog.Builder dmBuilder=new AlertDialog.Builder(MainActivity.this);
            View dmView=getLayoutInflater().inflate(R.layout.dulidiwu,null);
            EditText dmName= (EditText) dmView.findViewById(R.id.ftName);
            final EditText dlduLinkID= (EditText) dmView.findViewById(R.id.linkID);
            final EditText dwMC= (EditText) dmView.findViewById(R.id.dwmc);
            final EditText dwMS= (EditText) dmView.findViewById(R.id.dwms);
            final EditText dmbz= (EditText) dmView.findViewById(R.id.bz);
            dmName.setText(featureName);
            dmBuilder.setView(dmView);
            dmBuilder.setTitle("填写地貌属性信息");
            dmBuilder.setNegativeButton("取消录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    points.clear();
                }
            });
            dmBuilder.setPositiveButton("确定录入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SQLiteDatabase dmattributedb=createSurveyDB.getReadableDatabase();
                    if (dlduLinkID.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                    }else {
                        Cursor dmC=dmattributedb.rawQuery("select LinkID from DLDWData where LinkID=?",new String[]{dlduLinkID.getText().toString()});
                        if (!dmC.moveToFirst()){
                            ContentValues dmaValues=new ContentValues();
                            dmaValues.put("FTName",featureName);
                            dmaValues.put("LinkID",Integer.valueOf(dlduLinkID.getText().toString()));
                            dmaValues.put("YSName",dwMC.getText().toString());
                            dmaValues.put("YSDscibe",dwMS.getText().toString());
                            dmaValues.put("ZJTIME",zhujitime);
                            dmaValues.put("BZ", dmbz.getText().toString());
                            dmattributedb.insert("DLDWData", null, dmaValues);
                            dmattributedb.close();
                            try {
                                if (points.size()<3){
                                    Toast.makeText(MainActivity.this, "没有要存储的空间数据", Toast.LENGTH_LONG).show();
                                }else if (points.size()>2){
                                    SQLiteDatabase dmsdb=createSpatialDB.getReadableDatabase();
                                    Point cPoint;
                                    for (int i=0;i<points.size();i++){
                                        cPoint=points.get(i);
                                        ContentValues dmsValues=new ContentValues();
                                        dmsValues.put("LinkAID",Integer.valueOf(dlduLinkID.getText().toString()));
                                        dmsValues.put("x",cPoint.getX());
                                        dmsValues.put("y",cPoint.getY());
                                        dmsdb.insert("DLDWData",null,dmsValues);
                                    }
                                    dmsdb.close();
                                    Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                                    points.clear();
                                }
                            }catch (Exception e){
                                Log.i(tag,e.toString());
                            }
                        }
                        dmC.close();
                    }
                }
            });
            dmBuilder.create().show();
        }

        /*
        * 计算面积和长度
        * */
        private String getAreaString(double dValue) {
            long area = Math.abs(Math.round(dValue));
            String sArea = "";
            // 顺时针绘制多边形，面积为正，逆时针绘制，则面积为负
            if (area >= 1000000) {
                double dArea = area / 1000000.0;
                sArea = Double.toString(dArea);
            } else
                sArea = Double.toString(area);

            return sArea;
        }


    }

    //量测距离
    private void MeasureDistance() {

        SimpleFillSymbol fillSymbol;
        Unit[] linearUnits = new Unit[]{
                Unit.create(LinearUnit.Code.CENTIMETER),
                Unit.create(LinearUnit.Code.METER),
                Unit.create(LinearUnit.Code.KILOMETER),
                Unit.create(LinearUnit.Code.INCH),
                Unit.create(LinearUnit.Code.FOOT),
                Unit.create(LinearUnit.Code.YARD),
                Unit.create(LinearUnit.Code.MILE_STATUTE)
        };

        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.DIAMOND);
        SimpleLineSymbol LineSymbol = new SimpleLineSymbol(Color.YELLOW, 3);
        fillSymbol = new SimpleFillSymbol(Color.argb(100, 0, 225, 255));
        fillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));
        //创建工具栏选项
        MeasuringTool measuringTool = new MeasuringTool(mapView);
        //自定义样式
        measuringTool.setLinearUnits(linearUnits);
        measuringTool.setMarkerSymbol(markerSymbol);
        measuringTool.setLineSymbol(LineSymbol);
        measuringTool.setFillSymbol(fillSymbol);
        //开启新的工具栏
        startActionMode(measuringTool);
    }

    /*
    * 读取本地.geodatabase数据库要素信息
    * */
    public void getfeatures(String path) throws FileNotFoundException {
        Geodatabase featuredatabase=new Geodatabase(path);
        FeatureLayer featureLayer;
        List<Field> fields=new ArrayList<Field>();
        Map<String,Object> mapvalues;
        FeatureTable table;
        Feature feature;
        Long ids;
        List<GeodatabaseFeatureTable> featureTables=featuredatabase.getGeodatabaseTables();
        for (GeodatabaseFeatureTable geofeatureTable:featureTables){
            featureLayer=new FeatureLayer(geofeatureTable);
            featureLayer.setEnableLabels(true);

            for (int i=0;i<geofeatureTable.getNumberOfFeatures();i++){
                feature=featureLayer.getFeature(i);
                mapvalues=feature.getAttributes();

            }
            table=featureLayer.getFeatureTable();

            fields=table.getFields();
           for (Field field:fields){
               for (int i=0;i<fields.size();i++){
                   field=fields.get(i);
               }
           }
        }
    }

    /*
    * 读取本地地图信息，列表显示在listview中
    * */

    public List<String> getMapPathFromSD(){
        final int layerindex=1;
        final List<String> maplayerList=new ArrayList<String>();
        View maplistview = getLayoutInflater().inflate(R.layout.maplist, null);
        final ListView maplist = (ListView) maplistview.findViewById(R.id.maplist);
        String mappath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";

        File mFile=new File(mappath);
        File[] files=mFile.listFiles();

        ArrayAdapter<String> arrayAdapter;

        for (int i=0;i<files.length;i++){
            File file=files[i];
            if (checkIsMapFile(file.getPath())){
                maplayerList.add(file.getPath());
            }
        }
        final View itemView=getLayoutInflater().inflate(R.layout.item,null);
        layerlistadapter layerlistadapter=new layerlistadapter(MainActivity.this,maplayerList);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item, maplayerList);
        maplist.setAdapter(layerlistadapter);
        final AlertDialog.Builder mapBuidler=new AlertDialog.Builder(MainActivity.this);
        mapBuidler.create();
        mapBuidler.setTitle("本地地图列表");
        mapBuidler.setView(maplistview);
        maplist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        maplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.itemview);
                String pathname = maplayerList.get(position);

                position = maplist.getCheckedItemPosition();
                String fileEnd = pathname.substring(pathname.lastIndexOf(".") + 1, pathname.length()).toLowerCase();
                if (fileEnd.equals("geodatabase")) {
                    Geodatabase geodatabase = null;
                    try {
                        geodatabase = new Geodatabase(pathname);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (geodatabase != null) {
                      /*  GeodatabaseFeatureTable table=geodatabase.getGeodatabaseFeatureTableByLayerId(0);
                        featureLayer=new FeatureLayer(table);
                        mapView.addLayer(featureLayer);*/
                        for (GeodatabaseFeatureTable geotable : geodatabase.getGeodatabaseTables()) {
                            featureLayer = new FeatureLayer(geotable);
                            //featureLayer.setOpacity(50);
                            mapView.addLayer(featureLayer);
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "添加数据库地图失败！", Toast.LENGTH_LONG).show();
                    }
                } else if (fileEnd.equals("tpk")) {
                    tiledLayer = new ArcGISLocalTiledLayer(pathname);
                    mapView.addLayer(tiledLayer);
                    Toast.makeText(MainActivity.this, "添加图层成功!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "没有找到对应图层!", Toast.LENGTH_LONG).show();
                }
                /*try {
                    getfeatures(pathname);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/

            }
        });
        mapBuidler.show();
        return maplayerList;
    }

    public boolean checkIsMapFile(String mapname){
        boolean isMapFile=false;

        String fileEnd=mapname.substring(mapname.lastIndexOf(".")+1,mapname.length()).toLowerCase();
        if (fileEnd.equals("tpk")||fileEnd.equals("geodatabase")){
            isMapFile=true;
        }else {
            isMapFile=false;
        }
        return isMapFile;
    }

    /*
    * 居民地要素
    * */
    public void jmdPopup() {
        final String[] geometryType = {""};
        Type = "jmdmenu";
        setType(Type);
        mapTouchListener=new MapTouchListener(MainActivity.this,mapView);
        //mapView.setOnTouchListener(mapTouchListener);
        PopupMenu jmdpopup = new PopupMenu(MainActivity.this, new View(MainActivity.this));
        jmdpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawjqs:
                        featureName = "普通房屋";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mapTouchListener.getType();
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(100);
                        mapTouchListener.setType(geometryType[0]);
                        break;
                    case R.id.drawdl:
                        featureName = "一般房屋";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mapTouchListener.getType();
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(100);
                        mapTouchListener.setType(geometryType[0]);
                        break;

                    case R.id.drawsls:
                        featureName = "特殊房屋";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(100);
                        mapTouchListener.setType(geometryType[0]);
                        break;
                    case R.id.drawzzd:
                        featureName = "支柱墩";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(100);
                        mapTouchListener.setType(geometryType[0]);
                        break;
                    case R.id.drawys:
                        featureName = "垣栅";
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.RED, 5, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        break;
                    case R.id.drawqt:
                        featureName = "其他房屋";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(100);
                        mapTouchListener.setType(geometryType[0]);
                        break;

                }
                return false;
            }
        });
        MenuInflater jmdinflater = jmdpopup.getMenuInflater();
        jmdinflater.inflate(R.menu.jmdmenu, jmdpopup.getMenu());
        jmdpopup.show();
    }

    /*
    * 水系要素
    * */
    public void sxPopup() {
        mapTouchListener = new MapTouchListener(MainActivity.this, mapView);
        //mapView.setOnTouchListener(mapTouchListener);
        final String[] geometryType = new String[1];
        Type = "shuiximenu";
        setType(Type);
        PopupMenu sxpopup = new PopupMenu(MainActivity.this, new View(MainActivity.this));
        sxpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawhl:
                        featureName = "河流溪流";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 5, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawhp:
                        featureName = "湖泊";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.BLUE, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(20);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawsk:
                        featureName = "水库";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.BLUE, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(40);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawct:
                        featureName = "池塘";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.BLUE, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(30);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawgq:
                        featureName = "沟渠";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawhax:
                        featureName = "海岸线";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        break;
                    case R.id.drawsy:
                        featureName = "水源";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        geometryType[0] = "Point";
                        markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                }
                return false;
            }
        });
        MenuInflater sxinflater = sxpopup.getMenuInflater();
        sxinflater.inflate(R.menu.shuiximenu, sxpopup.getMenu());
        sxpopup.show();
    }

    /*
    * 道路要素
    * */
    public void dlPopup() {
        final String[] geometryType = new String[1];
        Type = "daolumenu";
        setType(Type);
        mapTouchListener = new MapTouchListener(MainActivity.this, mapView);
        //mapView.setOnTouchListener(mapTouchListener);
        PopupMenu dlpopup = new PopupMenu(MainActivity.this, new View(MainActivity.this));
        dlpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawtl:
                        featureName = "铁路";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        //simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 4, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawtlfs:
                        featureName = "铁路附属";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 4, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        break;
                    case R.id.drawgl:
                        featureName = "公路";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 4, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        return true;

                    case R.id.drawql:
                        featureName = "桥梁";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 4, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        break;

                    case R.id.drawdkmt:
                        featureName = "渡口码头";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 3, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        break;
                    case R.id.drawjgl:
                        featureName = "机耕路";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 3, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawxl:
                        featureName = "小路";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 1, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawxcl:
                        featureName = "乡村小路";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawnbdl:
                        featureName = "内部道路";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 1, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                }
                return false;
            }
        });
        MenuInflater dlinflater = dlpopup.getMenuInflater();
        dlinflater.inflate(R.menu.dlmenu, dlpopup.getMenu());
        dlpopup.show();
    }

    /*
    * 管线要素
    * */
    public void gxPopup() {
        mapTouchListener = new MapTouchListener(MainActivity.this, mapView);
        //mapView.setOnTouchListener(mapTouchListener);
        final String[] geometryType = new String[1];
        Type = "guanxianmenu";
        setType(Type);
        PopupMenu gxpopup = new PopupMenu(MainActivity.this, new View(MainActivity.this));
        gxpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawgxys:
                        featureName = "管线";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 2, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawdlx:
                        featureName = "电力线";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 1, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawtxx:
                        featureName = "通讯线";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 1, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawgd:
                        featureName = "管道";
                        setFNType(featureName);
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 3, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        break;

                    case R.id.drawgdfs:
                        featureName="管道附属";
                        setFNType(featureName);
                        mapTouchListener.geoType=Geometry.Type.POINT;
                        geometryType[0] = "Point";
                        setType(geometryType[0]);
                        markerSymbol = new SimpleMarkerSymbol(Color.YELLOW, 5, SimpleMarkerSymbol.STYLE.CROSS);
                        mapTouchListener.setType(geometryType[0]);
                        break;
                }
                return false;
            }
        });
        MenuInflater gxinflater = gxpopup.getMenuInflater();
        gxinflater.inflate(R.menu.gxmenu, gxpopup.getMenu());
        gxpopup.show();
    }

    /*
    * 植被要素
    * */
    public void zbPopup() {
        mapTouchListener = new MapTouchListener(MainActivity.this, mapView);

        final String[] geometryType = new String[1];
        Type = "zhibeimenu";
        PopupMenu zhibeiPopup = new PopupMenu(MainActivity.this, new View(MainActivity.this));
        zhibeiPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawsl:
                        featureName = "森林";
                        geometryType[0] = "Polygon";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GREEN);
                        mSimpleFillSymbol.setColor(Color.GREEN);
                        mSimpleFillSymbol.setAlpha(50);
                        break;

                    case R.id.drawyl:
                        featureName = "园林";
                        geometryType[0] = "Polygon";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GREEN);
                        mSimpleFillSymbol.setColor(Color.GREEN);
                        mSimpleFillSymbol.setAlpha(50);
                        break;

                    case R.id.drawdlfh:
                        featureName = "地类防火";
                        geometryType[0] = "Polyline";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        simpleLineSymbol = new SimpleLineSymbol(Color.GREEN, 4, SimpleLineSymbol.STYLE.DOT);
                        break;
                    case R.id.drawmp:
                        featureName = "苗圃";
                        geometryType[0] = "Polygon";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GREEN);
                        mSimpleFillSymbol.setColor(Color.GREEN);
                        mSimpleFillSymbol.setAlpha(20);
                        break;
                    case R.id.drawhs:
                        featureName = "行树";
                        geometryType[0] = "Polyline";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        simpleLineSymbol = new SimpleLineSymbol(Color.GREEN, 4, SimpleLineSymbol.STYLE.DOT);
                        break;
                    case R.id.drawlxsm:
                        featureName = "零星树木";
                        geometryType[0] = "Point";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        markerSymbol = new SimpleMarkerSymbol(Color.GREEN, 4, SimpleMarkerSymbol.STYLE.CIRCLE);
                        break;
                    case R.id.drawjjl:
                        featureName = "经济林";
                        geometryType[0] = "Polygon";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GREEN);
                        mSimpleFillSymbol.setColor(Color.GREEN);
                        mSimpleFillSymbol.setAlpha(20);
                        break;
                    case R.id.drawcdd:
                        featureName = "菜地";
                        geometryType[0] = "Polygon";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GREEN);
                        mSimpleFillSymbol.setColor(Color.GREEN);
                        mSimpleFillSymbol.setAlpha(20);
                        break;
                    case R.id.drawcd:
                        featureName = "草地";
                        geometryType[0] = "Polygon";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GREEN);
                        mSimpleFillSymbol.setColor(Color.GREEN);
                        mSimpleFillSymbol.setAlpha(20);
                        break;
                    case R.id.drawgcd:
                        featureName = "高草地";
                        geometryType[0] = "Polygon";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GREEN);
                        mSimpleFillSymbol.setColor(Color.GREEN);
                        mSimpleFillSymbol.setAlpha(40);
                        break;
                    case R.id.drawhcd:
                        featureName = "荒草地";
                        geometryType[0] = "Polygon";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GRAY);
                        mSimpleFillSymbol.setColor(Color.GRAY);
                        mSimpleFillSymbol.setAlpha(20);
                        break;
                    case R.id.drawjjzwd:
                        featureName = "经济作物";
                        geometryType[0] = "Polygon";
                        mapTouchListener.setType(geometryType[0]);
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GREEN);
                        mSimpleFillSymbol.setColor(Color.GREEN);
                        mSimpleFillSymbol.setAlpha(40);
                        break;
                }
                return false;
            }
        });
        MenuInflater zbInflater = zhibeiPopup.getMenuInflater();
        zbInflater.inflate(R.menu.zhibeimenu, zhibeiPopup.getMenu());
        zhibeiPopup.show();
    }

    /*
    * 地貌土质要素
    * */
    public void dmPopup() {
        mapTouchListener = new MapTouchListener(MainActivity.this, mapView);
       // mapView.setOnTouchListener(mapTouchListener);
        Type = "dimaopmenu";
        setType(Type);
        final String[] geometryType = {""};
        PopupMenu dmPopup = new PopupMenu(MainActivity.this, new View(MainActivity.this));
        dmPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawghc:
                        featureName = "干河床";
                        setFNType(featureName);
                        geometryType[0] = "Polyline";
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        mapTouchListener.setType(geometryType[0]);
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 5, SimpleLineSymbol.STYLE.SOLID);
                        break;
                    case R.id.drawghh:
                        featureName = "干涸湖";
                        setFNType(featureName);
                        geometryType[0] = "Polygon";
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mapTouchListener.setType(geometryType[0]);
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.BLUE);
                        mSimpleFillSymbol.setAlpha(30);
                        mSimpleFillSymbol.setOutline(simpleLineSymbol);
                        break;
                    case R.id.drawcg:
                        featureName = "冲沟";
                        setFNType(featureName);
                        geometryType[0] = "Polyline";
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        mapTouchListener.setType(geometryType[0]);
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 5, SimpleLineSymbol.STYLE.SOLID);
                        break;
                    case R.id.drawda:
                        featureName = "陡崖";
                        setFNType(featureName);
                        geometryType[0] = "Polyline";
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        mapTouchListener.setType(geometryType[0]);
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 5, SimpleLineSymbol.STYLE.SOLID);
                        break;
                    case R.id.drawttk:
                        featureName = "梯田坎";
                        setFNType(featureName);
                        geometryType[0] = "Polyline";
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        mapTouchListener.setType(geometryType[0]);
                        simpleLineSymbol = new SimpleLineSymbol(Color.GREEN, 5, SimpleLineSymbol.STYLE.SOLID);
                        break;
                    case R.id.drawlyd:
                        featureName = "露岩地";
                        setFNType(featureName);
                        geometryType[0] = "Polygon";
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mapTouchListener.setType(geometryType[0]);
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GRAY);
                        mSimpleFillSymbol.setAlpha(30);
                        mSimpleFillSymbol.setOutline(simpleLineSymbol);
                        break;
                    case R.id.drawskd:
                        featureName = "石块地";
                        setFNType(featureName);
                        geometryType[0] = "Polygon";
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mapTouchListener.setType(geometryType[0]);
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GRAY);
                        mSimpleFillSymbol.setAlpha(30);
                        mSimpleFillSymbol.setOutline(simpleLineSymbol);
                        break;
                    case R.id.drawsd:
                        featureName = "沙地";
                        setFNType(featureName);
                        geometryType[0] = "Polygon";
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        mapTouchListener.setType(geometryType[0]);
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.GRAY);
                        mSimpleFillSymbol.setAlpha(40);
                        mSimpleFillSymbol.setOutline(simpleLineSymbol);
                        break;
                    case R.id.drawxs:
                        featureName = "雪山";
                        setFNType(featureName);
                        geometryType[0] = "Point";
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        markerSymbol = new SimpleMarkerSymbol(Color.BLACK, 5, SimpleMarkerSymbol.STYLE.DIAMOND);
                        break;
                }
                return false;
            }
        });
        MenuInflater dmInflater = new MenuInflater(MainActivity.this);
        dmInflater.inflate(R.menu.dimaomenu, dmPopup.getMenu());
        dmPopup.show();
    }

    /*
    * 境界线要素
    * */
    public void jjxPopup() {
        mapTouchListener = new MapTouchListener(MainActivity.this, mapView);
        //mapView.setOnTouchListener(mapTouchListener);
        Type = "jjxmenu";
        setType(Type);
        final String[] geometryType = {""};
        final PopupMenu jjxPopup = new PopupMenu(MainActivity.this, new View(MainActivity.this));
        jjxPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawgj:
                        featureName = "国界";
                        setFNType(featureName);
                        geometryType[0] = "Polyline";
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        mapTouchListener.setType(geometryType[0]);
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 4, SimpleLineSymbol.STYLE.DOT);
                        break;
                    case R.id.drawgnjjx:
                        featureName = "国内境界线";
                        setFNType(featureName);
                        geometryType[0] = "Polyline";
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        mapTouchListener.setType(geometryType[0]);
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 3, SimpleLineSymbol.STYLE.DOT);
                        break;
                }
                return false;
            }
        });
        MenuInflater jjxInflater = new MenuInflater(MainActivity.this);
        jjxInflater.inflate(R.menu.jjxmenu, jjxPopup.getMenu());
        jjxPopup.show();
    }

    public void dldwPopup(){
        mapTouchListener = new MapTouchListener(MainActivity.this, mapView);
        //mapView.setOnTouchListener(mapTouchListener);
        Type = "dulidiwumenu";
        setType(Type);
        final String[] geometryType = {""};
        final PopupMenu jjxPopup = new PopupMenu(MainActivity.this, new View(MainActivity.this));
        jjxPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawkskc:
                        featureName = "矿山开采";
                        setFNType(featureName);
                        geometryType[0] = "Point";
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        mapTouchListener.setType(geometryType[0]);
                        markerSymbol = new SimpleMarkerSymbol(Color.YELLOW, 8, SimpleMarkerSymbol.STYLE.CROSS);
                        break;
                    case R.id.drawgyss:
                        featureName = "工业设施";
                        setFNType(featureName);
                        geometryType[0] = "Point";
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        mapTouchListener.setType(geometryType[0]);
                        markerSymbol = new SimpleMarkerSymbol(Color.YELLOW, 8, SimpleMarkerSymbol.STYLE.CROSS);
                        break;
                    case R.id.drawnyss:
                        featureName = "农业设施";
                        setFNType(featureName);
                        geometryType[0] = "Point";
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        mapTouchListener.setType(geometryType[0]);
                        markerSymbol = new SimpleMarkerSymbol(Color.YELLOW, 8, SimpleMarkerSymbol.STYLE.CROSS);
                        break;
                    case R.id.drawkwwt:
                        featureName = "科文卫体";
                        setFNType(featureName);
                        geometryType[0] = "Point";
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        mapTouchListener.setType(geometryType[0]);
                        markerSymbol = new SimpleMarkerSymbol(Color.YELLOW, 8, SimpleMarkerSymbol.STYLE.CROSS);
                        break;
                    case R.id.drawggss:
                        featureName = "公共设施";
                        setFNType(featureName);
                        geometryType[0] = "Point";
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        mapTouchListener.setType(geometryType[0]);
                        markerSymbol = new SimpleMarkerSymbol(Color.YELLOW, 8, SimpleMarkerSymbol.STYLE.CROSS);
                        break;
                    case R.id.drawbsdt:
                        featureName = "碑塑墩亭";
                        setFNType(featureName);
                        geometryType[0] = "Point";
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        mapTouchListener.setType(geometryType[0]);
                        markerSymbol = new SimpleMarkerSymbol(Color.YELLOW, 3, SimpleMarkerSymbol.STYLE.CROSS);
                        break;
                    case R.id.drawwwzj:
                        featureName = "文物宗教";
                        setFNType(featureName);
                        geometryType[0] = "Point";
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        mapTouchListener.setType(geometryType[0]);
                        markerSymbol = new SimpleMarkerSymbol(Color.YELLOW, 8, SimpleMarkerSymbol.STYLE.CROSS);
                        break;
                    case R.id.drawqtss:
                        featureName = "其他设施";
                        setFNType(featureName);
                        geometryType[0] = "Point";
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        mapTouchListener.setType(geometryType[0]);
                        markerSymbol = new SimpleMarkerSymbol(Color.YELLOW, 8, SimpleMarkerSymbol.STYLE.CROSS);
                        break;
                }
                return false;
            }
        });
        MenuInflater jjxInflater = new MenuInflater(MainActivity.this);
        jjxInflater.inflate(R.menu.dldwmenu, jjxPopup.getMenu());
        jjxPopup.show();
    }


    /*
    * 注记要素
    * */
    public void zhujiPopup(){
        mapTouchListener = new MapTouchListener(MainActivity.this, mapView);
        //mapView.setOnTouchListener(mapTouchListener);
        Type = "zjmenu";
        setType(Type);
        final String[] geometryType = {""};
        PopupMenu zhujimenu=new PopupMenu(MainActivity.this,new View(MainActivity.this));
        zhujimenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawzj:
                        mapTouchListener.geoType = Geometry.Type.POINT;
                        geometryType[0] = "Point";
                        setType(geometryType[0]);
                        markerSymbol = new SimpleMarkerSymbol(Color.BLACK, 5, SimpleMarkerSymbol.STYLE.CROSS);
                }
                return false;
            }
        });
        MenuInflater zjInflater=zhujimenu.getMenuInflater();
        zjInflater.inflate(R.menu.zhujimenu, zhujimenu.getMenu());
        zhujimenu.show();
    }

    /**
     * This is responsible for retrieving the template types for the edits.
     */
    public class TemplatePickerTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params) {

            setTemplatePicker(new TemplatePicker(MainActivity.this,
                    mapView));
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog
                    .show(MainActivity.this,
                            "Loading Edit Templates",
                            "Might take more time for layers with many templates",
                            true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            getTemplatePicker().showAtLocation(mapView, Gravity.BOTTOM, 0, 0);

            super.onPostExecute(result);
        }

    }

    public TemplatePicker getTemplatePicker() {
        return tp;
    }

    public void setTemplatePicker(TemplatePicker tp) {
        this.tp = tp;
    }

    /**
     * Shows the edit templates for all the feature layers in the map
     */
    public void showEditTemplatePicker() {

        GDBUtil.showProgress(MainActivity.this, true);
        //clear();
        int layerCount = 0;
        for (Layer layer : mapView.getLayers()) {
            if (layer instanceof FeatureLayer) {
                layerCount++;
            }
        }
        if (layerCount > 0) {
            if (mapTouchListener == null) {
                mapTouchListener = new MapTouchListener(MainActivity.this,mapView);
                mapView.setOnTouchListener(mapTouchListener);
            }
            if (getTemplatePicker() != null) {
                getTemplatePicker().showAtLocation(mapView, Gravity.BOTTOM, 0,0);
            } else {
                new TemplatePickerTask().execute();
            }
        } else {
            GDBUtil.showMessage(MainActivity.this,"No Editable Local Feature Layers.");

        }
        GDBUtil.showProgress(MainActivity.this, false);

    }

    public MapView getMapView() {
        return mapView;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.unpause();

    }


}
