package com.example.yanhejin.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.toolkit.analysis.MeasuringTool;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.example.yanhejin.myapplication.ChildActivity.FeatureType;
import com.example.yanhejin.myapplication.Database.CreateSpitalDB;
import com.example.yanhejin.myapplication.Database.CreateSurveyDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    CreateSurveyDB createSurveyDB;
    CreateSpitalDB createSpitalDB;
    private long exitTime;
    static final String mapURL = "http://cache1.arcgisonline.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer";
    MapView mapView;
    GeodatabaseFeatureTable geodatabaseFeatureTable;
    FeatureLayer featureLayer;
    EditText addurltext;
    ArcGISLocalTiledLayer tiledLayer;
    FloatingActionButton location;
    LocationManager locationManager;
    Object actionmode;
    FeatureType featureType;

    protected static final String TAG = "EditGraphicElements";

    private static final String TAG_DIALOG_FRAGMENTS = "dialog";

    private static final String KEY_MAP_STATE = "com.esri.MapState";



    private enum EditMode {NONE, POINT, POLYLINE, POLYGON, SAVING}

    Menu mOptionMenu;
    DialogFragment mDialogFragment;
    String mMapState;
    GraphicsLayer mGraphicLayer;
    SimpleFillSymbol mSimpleFillSymbol = null;
    SimpleMarkerSymbol markerSymbol;
    SimpleLineSymbol simpleLineSymbol;
    MapTouchListener mapTouchListener;
    private boolean isChoose;
    private boolean isMessageLength;

    PictureMarkerSymbol pictureMarkerSymbol;
    Point point;
    Point wgsPoint;
    Point mapPoint;
    Location loc;

    Uri imageUri;
    public static final int TAKE_PHOTO = 1;

    boolean wasAPEnabled=false;
    //static WiFiAP wiFiAP;
    private WifiManager wifiManager;
    ExpandableListView expandableListView;
    List<String> groupArray=new ArrayList<String>();
    List<List<String>> childArray=new ArrayList<List<String>>();
    ExpandableListViewAdapter adapter;
    View featureview;
    Dialog featureDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mMapState = null;
        } else {
            mMapState = savedInstanceState.getString(KEY_MAP_STATE);
            Fragment dialogFrag = getFragmentManager().findFragmentByTag(TAG_DIALOG_FRAGMENTS);
            if (dialogFrag != null) {
                ((DialogFragment) dialogFrag).dismiss();
            }
        }
        createSpitalDB=new CreateSpitalDB(MainActivity.this,"SpatialSurveyDB",null,1);
        createSurveyDB=new CreateSurveyDB(MainActivity.this,"AttributeSurveyDB",null,1);
        //监听图层
        OnStatusChangedListener statusChangedListener = new OnStatusChangedListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onStatusChanged(Object o, STATUS status) {

            }
        };
        //QueryUser();
        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mapView = (MapView) findViewById(R.id.mapview);
        ArcGISDynamicMapServiceLayer maplayeronline = new ArcGISDynamicMapServiceLayer(mapURL);
        mapView.addLayer(maplayeronline);
        /*String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fullpath = path + "/" + "ArcGISSurvey/Untitled.tpk";
        tiledLayer = new ArcGISLocalTiledLayer(fullpath);*/
        mapView.addLayer(tiledLayer);
        point = (Point) GeometryEngine.project(new Point(40.805, 111.661), SpatialReference.create(4326), mapView.getSpatialReference());
        mapView.centerAt(point, true);
        mapView.enableWrapAround(true);
        mapView.setEsriLogoVisible(true);
        mGraphicLayer = new GraphicsLayer();
        mapView.addLayer(mGraphicLayer);
        try {
            new Thread(){
                @Override
                public void run() {
                    inidate();
                }
            }.start();
        }catch (Exception e){
            Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
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
                        for (String provider : providers) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            loc = locationManager.getLastKnownLocation(provider);
                            LocationListener locationListener = new LocationListener() {
                                /**
                                 * 位置改变时调用
                                 */
                                @Override
                                public void onLocationChanged(Location location) {
                                    markLocation(location);
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
                            locationManager.requestLocationUpdates(provider, 100000, 10, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    if (loc!=null){
                                        double latitude=loc.getLatitude();
                                        double longitude=loc.getLongitude();
                                        Toast.makeText(MainActivity.this, "当前位置："+"东经："+String.valueOf(latitude)+"北纬："+String.valueOf(longitude),Toast.LENGTH_LONG).show();
                                        markLocation(loc);
                                    }
                                }

                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {

                                }

                                @Override
                                public void onProviderEnabled(String provider) {

                                }

                                @Override
                                public void onProviderDisabled(String provider) {

                                }
                            });
                        }
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
        wifiManager= (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    //标记位置
    private void markLocation(Location location){
        mGraphicLayer.removeAll();
        double locx=location.getLongitude();
        double locy=location.getLatitude();
        wgsPoint=new Point(locx,locy);
        mapPoint= (Point) GeometryEngine.project(wgsPoint, SpatialReference.create(4326), mapView.getSpatialReference());
        //创建图层
        Graphic graphic=new Graphic(mapPoint,pictureMarkerSymbol);
        mGraphicLayer.addGraphic(graphic);
        //画线
        Graphic graphicline=new Graphic(mapPoint,new SimpleLineSymbol(Color.BLUE,4, SimpleLineSymbol.STYLE.SOLID));
        mGraphicLayer.addGraphic(graphicline);

        mapView.centerAt(mapPoint, true);
        SQLiteDatabase GPSdb=createSpitalDB.getReadableDatabase();//数据库为空
        ContentValues GPSValues=new ContentValues();
        GPSValues.put("x",locx);
        GPSValues.put("y", locy);
        GPSdb.insert("GPSData", null, GPSValues);
        Toast.makeText(MainActivity.this,"GPS数据保存成功",Toast.LENGTH_LONG).show();
        GPSdb.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
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

        switch (id){
            case R.id.zoomin:
                mapView.zoomin();
                return true;
            case R.id.zoomout:
                mapView.zoomout();
                return true;
            case R.id.locationnow:
               mapView.setOnLongPressListener(new OnLongPressListener() {
                   String mapcenter=null;
                   String longtitude=null;
                   String latitude=null;
                   @Override
                   public boolean onLongPress(float x, float y) {
                       Point point=mapView.toMapPoint(x, y);
                       mapcenter="X:"+point.getX()+ "Y:"+point.getY();
                       longtitude="当前地图分辨率为："+mapView.getResolution();
                       latitude="当前地图比例尺为："+mapView.getScale();
                       Toast.makeText(MainActivity.this,mapcenter+longtitude+latitude,Toast.LENGTH_LONG).show();
                       return false;
                   }
               });
                return true;
            default:
                return false;
        }
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
                View addmapview=getLayoutInflater().inflate(R.layout.addmap,null,false);
                final RadioButton addonlinemap= (RadioButton) addmapview.findViewById(R.id.addonlinemap);
                final RadioButton addlocalmap= (RadioButton) addmapview.findViewById(R.id.addvectormap);
                final RadioButton addtiffmap= (RadioButton) addmapview.findViewById(R.id.addtiffmap);
                AlertDialog.Builder addmapbuilder=new AlertDialog.Builder(MainActivity.this);
                addmapbuilder.setView(addmapview);
                addmapbuilder.setTitle("选择添加的地图");
                addmapbuilder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (addonlinemap.isChecked()) {
                            addOnlineMap();
                        }
                        if (addlocalmap.isChecked()) {
                            addLocalMap();
                        }
                        if (addtiffmap.isChecked()) {
                            addTiledMap();
                        }
                    }
                });
                addmapbuilder.setPositiveButton("取消", null);
                addmapbuilder.create().show();
                break;
            case R.id.layercontrol:

                break;
            case R.id.startedit:
                actionmode=MainActivity.this.startActionMode(actioncallback);
                mapTouchListener=new MapTouchListener(MainActivity.this,mapView);
                mapView.setOnTouchListener(mapTouchListener);
                break;
            case R.id.camara:
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
                break;
            case R.id.surveydiatance:
                MeasureDistance();
                break;
            case R.id.WiFishare:
                break;
            case R.id.sharedata:
               // wiFiAP.toggleWiFiAP(wifiManager,MainActivity.this);//设置手机屏幕，键盘锁，屏幕长亮，弹出对话框背景颜色
                //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    //拍照
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        View photoview=getLayoutInflater().inflate(R.layout.takephoto,null,false);
        ImageView photo= (ImageView) photoview.findViewById(R.id.takephoto);
        final EditText photoname= (EditText) photoview.findViewById(R.id.photodiscrable);
        final EditText photodescrible= (EditText) photoview.findViewById(R.id.photolocation);

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

            FileOutputStream b = null;
            File file = new File("/sdcard/Image/");
            file.mkdirs();// 创建文件夹
            String fileName = "/sdcard/Image/"+name;

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
            try
            {
                AlertDialog.Builder photobuilder=new AlertDialog.Builder(MainActivity.this);
                photobuilder.setTitle("填写照片属性信息");
                photobuilder.setView(photoview);
                photo.setImageBitmap(bitmap);// 将图片显示在ImageView里
                photobuilder.setPositiveButton("取消", null);
                photobuilder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
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

            }catch(Exception e)
            {
                Log.e("error", e.getMessage());
            }

        }
    }

    //按下两次退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_LONG).show();
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
    //添加在线地图
    public void addOnlineMap() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请填入要加载的地图URL");
        View addurl = getLayoutInflater().inflate(R.layout.addmapurl, null, false);
        addurltext = (EditText) addurl.findViewById(R.id.url);
        builder.setView(addurl);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String MapURL = addurltext.getText().toString();
                if (MapURL.equals("")) {
                    Toast.makeText(MainActivity.this, "请输入URL链接", Toast.LENGTH_LONG).show();
                }
                ArcGISDynamicMapServiceLayer maplayeronline = new ArcGISDynamicMapServiceLayer(MapURL);
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

    //添加本地图层
    public void addLocalMap() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择添加的图层");
        final String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filename = "ArcGISSurvey/androiddb.geodatabase";
        String pathname = path + "/" + filename;
        try {
            Geodatabase geodatabase = new Geodatabase(pathname);
            geodatabaseFeatureTable = geodatabase.getGeodatabaseFeatureTableByLayerId(0);
            featureLayer = new FeatureLayer(geodatabaseFeatureTable);
            SimpleLineSymbol simpleMarkerSymbol = new SimpleLineSymbol(Color.BLUE, 3, SimpleLineSymbol.STYLE.SOLID);
            SimpleRenderer simpleRenderer = new SimpleRenderer(simpleMarkerSymbol);
            featureLayer.setRenderer(simpleRenderer);
            mapView.addLayer(featureLayer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "载入的地图无效", Toast.LENGTH_LONG).show();
        }
    }

    //添加瓦片地图（本地）
    public void addTiledMap() {
        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fullpath = path + "/" + "ArcGISSurvey/叠加图.tpk";
        tiledLayer = new ArcGISLocalTiledLayer(fullpath);
        try {
            //mapView.addLayer(tiledLayer);
            GettpkFileName();
        } catch (Exception e) {
            e.getMessage();
            Toast.makeText(MainActivity.this, "载入的地图无效", Toast.LENGTH_LONG).show();
        }
    }

    //自定义的工具栏在上方
    private ActionMode.Callback actioncallback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater=mode.getMenuInflater();
            inflater.inflate(R.menu.jmdmenu,menu);
            //mOptionMenu=menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            final String[] geometryType = new String[1];
            switch (item.getItemId()){
                case R.id.drawjmd:
                    PopupMenu jmdpopup=new PopupMenu(MainActivity.this,new View(MainActivity.this));
                    jmdpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch (id) {
                                case R.id.jiequshi:
                                    mapTouchListener.geoType = Geometry.Type.POLYGON;
                                    geometryType[0] = "Polygon";
                                    //mapTouchListener.getType();
                                    mSimpleFillSymbol = new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.BACKWARD_DIAGONAL);
                                    mSimpleFillSymbol.setAlpha(0);
                                    mapTouchListener.setType(geometryType[0]);
                                    return true;
                                case R.id.yaodongshi:
                                    mapTouchListener.geoType = Geometry.Type.POLYGON;
                                    geometryType[0] = "Polygon";
                                    mSimpleFillSymbol = new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.FORWARD_DIAGONAL);
                                    mSimpleFillSymbol.setAlpha(0);
                                    mapTouchListener.setType(geometryType[0]);
                                    return true;
                            }
                            return false;
                        }
                    });
                    MenuInflater jmdinflater=jmdpopup.getMenuInflater();
                    jmdinflater.inflate(R.menu.jmdmenu,jmdpopup.getMenu());
                    jmdpopup.show();
                    mapTouchListener.geoType=Geometry.Type.POINT;
                    geometryType[0] ="Point";
                    //mapTouchListener.getType();
                    markerSymbol=new SimpleMarkerSymbol(Color.YELLOW,8,SimpleMarkerSymbol.STYLE.CIRCLE);
                    mapTouchListener.setType(geometryType[0]);
                    return true;
                case  R.id.drawsx:
                    PopupMenu sxpopup=new PopupMenu(MainActivity.this,new View(MainActivity.this));
                    sxpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch (id) {
                                case R.id.gonglu:
                                    mapTouchListener.geoType = Geometry.Type.POLYLINE;
                                    geometryType[0] = "Polyline";
                                    simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 4, SimpleLineSymbol.STYLE.SOLID);
                                    mapTouchListener.setType(geometryType[0]);
                                    return true;
                                case R.id.jigenglu:
                                    mapTouchListener.geoType = Geometry.Type.POLYLINE;
                                    geometryType[0] = "Polyline";
                                    simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 3, SimpleLineSymbol.STYLE.DASH);
                                    mapTouchListener.setType(geometryType[0]);
                                    return true;
                            }
                            return false;
                        }
                    });
                    return true;
                case R.id.drawdl:
                    mapTouchListener.geoType=Geometry.Type.POLYGON;
                    geometryType[0] ="Polygon";
                    //mapTouchListener.getType();
                    mSimpleFillSymbol=new SimpleFillSymbol(Color.RED,SimpleFillSymbol.STYLE.BACKWARD_DIAGONAL);
                    mSimpleFillSymbol.setAlpha(0);
                    mapTouchListener.setType(geometryType[0]);
                    return true;
                default:
                    return false;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mode.finish();
            //actionmode=null;
        }
    };

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
    //地图触摸事件
    class MapTouchListener extends MapOnTouchListener {
        private Geometry.Type geoType = null;//用于判定当前选择的几何图形类型
        private Point ptStart = null;//起点
        private Point ptPrevious = null;//上一个点
        private ArrayList<Point> points = null;//记录全部点
        private Polygon tempPolygon = null;//记录绘制过程中的多边形
        EditMode mEditMode;

        public MapTouchListener(Context context, MapView view) {
            super(context, view);

            points = new ArrayList<Point>();
        }

        // 根据用户选择设置当前绘制的几何图形类型
        public void setType(String geometryType) {
            if(geometryType.equalsIgnoreCase("Point"))
                this.geoType = Geometry.Type.POINT;
            else if(geometryType.equalsIgnoreCase("Polyline"))
                this.geoType = Geometry.Type.POLYLINE;
            else if(geometryType.equalsIgnoreCase("Polygon"))
                this.geoType = Geometry.Type.POLYGON;
        }

        public Geometry.Type getType() {
            return this.geoType;
        }

        @Override
        public boolean onSingleTap(MotionEvent point) {

            Point ptCurrent = mapView.toMapPoint(new Point(point.getX(), point.getY()));
            //if(ptStart == null)
              //mGraphicLayer.removeAll();//第一次开始前，清空全部graphic

            if (geoType == Geometry.Type.POINT) {
            //直接画点
                //mGraphicLayer.removeAll();
                mEditMode=EditMode.POINT;
                ptStart = ptCurrent;

                Graphic graphic = new Graphic(ptStart,markerSymbol);
                mGraphicLayer.addGraphic(graphic);

                return true;
            }
            else//绘制线或多边形
            {
                points.add(ptCurrent);//将当前点加入点集合中

                if(ptStart == null){//画线或多边形的第一个点
                    ptStart = ptCurrent;

                    //绘制第一个点
                    Graphic graphic = new Graphic(ptStart,markerSymbol);
                    mGraphicLayer.addGraphic(graphic);
                }
                else{//画线或多边形的其他点
                    //绘制其他点
                    Graphic graphic = new Graphic(ptCurrent,markerSymbol);
                    mGraphicLayer.addGraphic(graphic);

                    //生成当前线段（由当前点和上一个点构成）
                    ptPrevious=ptStart;
                    Line line = new Line();
                    line.setStart(ptPrevious);
                    line.setEnd(ptCurrent);

                    if(geoType == Geometry.Type.POLYLINE){
                        //绘制当前线段
                        mEditMode=EditMode.POLYLINE;
                        Polyline polyline = new Polyline();
                        polyline.addSegment(line, true);

                        Graphic g = new Graphic(polyline, simpleLineSymbol);
                        mGraphicLayer.addGraphic(g);

                        // 计算当前线段的长度
                        String length = Double.toString(Math.round(line.calculateLength2D())) + " 米";

                        Toast.makeText(mapView.getContext(), length, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //绘制临时多边形
                        mEditMode=EditMode.POLYGON;
                        if(tempPolygon == null) tempPolygon = new Polygon();
                        tempPolygon.addSegment(line, false);

                        //mGraphicLayer.removeAll();
                        Graphic g = new Graphic(tempPolygon, mSimpleFillSymbol);
                        mGraphicLayer.addGraphic(g);

                        //计算当前面积
                        String sArea = getAreaString(tempPolygon.calculateArea2D());

                        Toast.makeText(mapView.getContext(), sArea, Toast.LENGTH_SHORT).show();
                    }
                }

                ptPrevious = ptCurrent;
                return true;
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent point) {
            //mGraphicLayer.removeAll();
            int pointid=0;
            int graphicid=0;

            if(isMessageLength == true){
                Polyline polyline = new Polyline();

                Point startPoint = null;
                Point endPoint = null;

                // 绘制完整的线段
                if (points.size()<2){
                    Toast.makeText(MainActivity.this,"绘制点数小于2不能形成直线段",Toast.LENGTH_LONG).show();
                }
                for(int i=1;i<points.size();i++){
                    startPoint = points.get(i-1);
                    endPoint = points.get(i);

                    Line line = new Line();
                    line.setStart(startPoint);
                    line.setEnd(endPoint);

                    polyline.addSegment(line, false);
                }

                Graphic g = new Graphic(polyline, simpleLineSymbol);
                mGraphicLayer.addGraphic(g);

                // 计算总长度
                String length = Double.toString(Math.round(polyline.calculateLength2D())) + " 米";

                Toast.makeText(mapView.getContext(), length, Toast.LENGTH_SHORT).show();
            }
            else{
                Polygon polygon = new Polygon();

                Point startPoint = null;
                Point endPoint = null;
                // 绘制完整的多边形
                for(int i=1;i<points.size();i++){
                    startPoint = points.get(i-1);
                    endPoint = points.get(i);

                    Line line = new Line();
                    line.setStart(startPoint);
                    line.setEnd(endPoint);

                    polygon.addSegment(line, false);
                    pointid+=points.size();
                    graphicid++;
                }
                final String fid=String.valueOf(pointid)+String.valueOf(graphicid);
                Graphic g = new Graphic(polygon, mSimpleFillSymbol);
                mGraphicLayer.addGraphic(g);

                // 计算总面积
                String sArea = getAreaString(polygon.calculateArea2D());

                Toast.makeText(mapView.getContext(), sArea, Toast.LENGTH_SHORT).show();

            }
            //双击获取要素信息
            //getfeaturelayout();
            //featureDialog.show();
            featureType=new FeatureType();
            featureType.getfeaturelayout();
            // 其他清理工作
            ptStart = null;
            ptPrevious = null;
            points.clear();
            tempPolygon = null;

            return false;
        }

        private String getAreaString(double dValue){
            long area = Math.abs(Math.round(dValue));
            String sArea = "";
            // 顺时针绘制多边形，面积为正，逆时针绘制，则面积为负
            if(area >= 1000000){
                double dArea = area / 1000000.0;
                sArea = Double.toString(dArea) ;
            }
            else
                sArea = Double.toString(area);

            return sArea;
        }
    }
    //量测距离
    private void MeasureDistance(){
        SimpleFillSymbol fillSymbol;
        Unit[] linearUnits=new Unit[]{
                Unit.create(LinearUnit.Code.CENTIMETER),
                Unit.create(LinearUnit.Code.METER),
                Unit.create(LinearUnit.Code.KILOMETER),
                Unit.create(LinearUnit.Code.INCH),
                Unit.create(LinearUnit.Code.FOOT),
                Unit.create(LinearUnit.Code.YARD),
                Unit.create(LinearUnit.Code.MILE_STATUTE)
        };

        SimpleMarkerSymbol markerSymbol=new SimpleMarkerSymbol(Color.BLUE,10,SimpleMarkerSymbol.STYLE.DIAMOND);
        SimpleLineSymbol LineSymbol=new SimpleLineSymbol(Color.YELLOW,3);
        fillSymbol= new SimpleFillSymbol(Color.argb(100, 0,225,255));
        fillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT,0));
       //创建工具栏选项
        MeasuringTool measuringTool=new MeasuringTool(mapView);
        //自定义样式
        measuringTool.setLinearUnits(linearUnits);
        measuringTool.setMarkerSymbol(markerSymbol);
        measuringTool.setLineSymbol(LineSymbol);
        measuringTool.setFillSymbol(fillSymbol);
        //开启新的工具栏
        startActionMode(measuringTool);
    }
    /*
    * 居民地属性信息
    * */
    public void setjumindi(){
        View fwview = getLayoutInflater().inflate(R.layout.jumindi, null, false);
        final EditText fwcstext = (EditText) fwview.findViewById(R.id.fwcs);
        final EditText fwcztext = (EditText) fwview.findViewById(R.id.fwcz);
        final EditText fygztext = (EditText) fwview.findViewById(R.id.fygz);
        final EditText bztext = (EditText) fwview.findViewById(R.id.bz);
        AlertDialog.Builder fwbuilder = new AlertDialog.Builder(MainActivity.this);
        fwbuilder.setView(fwview);
        fwbuilder.setTitle("填写房屋调绘属性表");
        fwbuilder.setNegativeButton("取消", null);
        fwbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase fwdb = createSurveyDB.getReadableDatabase();
                ContentValues values = new ContentValues();
                values.put("FWCS", Integer.valueOf(fwcstext.getText().toString()));
                values.put("FWCZ", fwcztext.getText().toString());
                values.put("FYGZ", Float.parseFloat(fygztext.getText().toString()));
                values.put("BZ", bztext.getText().toString());
                fwdb.insert("JMDData", null, values);
                fwdb.close();
            }
        });
        fwbuilder.create().show();
    }
    /*
    * 道路属性信息*/
    public void setdaolu(){
        final View daoluview = getLayoutInflater().inflate(R.layout.daolu_layout,null,false);
        final RadioButton tielubtn = (RadioButton) daoluview.findViewById(R.id.tielu);
        final RadioButton gonglubtn = (RadioButton) daoluview.findViewById(R.id.gonglu);
        final RadioButton jigengbtn = (RadioButton) daoluview.findViewById(R.id.jigenglu);
        RadioButton xiangcunbtn = (RadioButton) daoluview.findViewById(R.id.xiangcunlu);
        RadioButton xiaolubtn = (RadioButton) daoluview.findViewById(R.id.xiaolu);
        RadioButton neibulubtn = (RadioButton) daoluview.findViewById(R.id.neibudaolu);

        final View daolu = getLayoutInflater().inflate(R.layout.daolu, null, false);
        final EditText dlmctext = (EditText) daolu.findViewById(R.id.dlmc);
        final EditText dlxhlutext = (EditText) daolu.findViewById(R.id.dlxh);
        final EditText djdmtext = (EditText) daolu.findViewById(R.id.djdm);
        final EditText bztext= (EditText) daolu.findViewById(R.id.bz);
        AlertDialog.Builder tielubuider=new AlertDialog.Builder(MainActivity.this);
        tielubuider.setTitle("填写道路属性");
        tielubuider.setView(daolu);
        tielubuider.setPositiveButton("取消", null);
        tielubuider.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase daoludb = createSurveyDB.getReadableDatabase();
                ContentValues daoluValues = new ContentValues();
                daoluValues.put("DLMC", dlmctext.getText().toString());
                daoluValues.put("DLXH", Integer.valueOf(dlxhlutext.getText().toString()));
                daoluValues.put("DJDM", djdmtext.getText().toString());
                daoluValues.put("BZ", bztext.getText().toString());
                daoludb.insert("DLData", null, daoluValues);
                daoludb.close();
            }
        });
        tielubuider.create().show();
    }
    /*
    * 获取可展开列表的数据，判断类型
    * */
    public void getfeaturelayout(){
        featureview=MainActivity.this.getLayoutInflater().inflate(R.layout.collectfeature,null,false);
        featureDialog=new Dialog(MainActivity.this);
        featureDialog.setContentView(featureview);
        featureDialog.setTitle("属性信息");
      /*  Window window=featureDialog.getWindow();
        Display display=getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams layoutParams=window.getAttributes();
        window.setGravity(Gravity.LEFT|Gravity.TOP);
        layoutParams.height=(int)(display.getHeight()*0.6);
        layoutParams.width=(int)(display.getWidth()*0.95);
        window.setAttributes(layoutParams);*/
        expandableListView= (ExpandableListView) featureview.findViewById(R.id.featurelist);
        final ExpandableListViewAdapter adapter1=new ExpandableListViewAdapter(MainActivity.this,groupArray,childArray);
        expandableListView.setAdapter(adapter1);
        //expandableListView.setGroupIndicator(null);
        //expandableListView.setGroupIndicator(this.getResources().getDrawable(R.drawable.selector));
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                String feature = adapter1.getGroup(groupPosition).toString();
                switch (feature) {
                    case "居民地":
                        setjumindi();
                        break;
                    case "道路":
                        setdaolu();
                        break;
                    case "管线、垣栅":
                        break;
                    case "水系":
                        break;
                    case "植被":
                        break;
                    case "境界线":
                        break;
                    case "地貌和土质":
                        break;
                }
                featureDialog.dismiss();
                return false;
            }
        });
    }

    /*
    * 初始化采集要素数据
    * */
    public void inidate(){
        groupArray=new ArrayList<String>();
        childArray=new ArrayList<List<String>>();
        groupArray.add("居民地");
        groupArray.add("道路");
        groupArray.add("管线、垣栅");
        groupArray.add("水系");
        groupArray.add("植被");
        groupArray.add("境界线");
        groupArray.add("地貌和土质");
        groupArray.add("地理名称和注记");
        for (int i=0;i<groupArray.size();i++){
            ArrayList<String> childItem=new ArrayList<String>();
            if (i==0){
                childItem.add("独立房屋");
                childItem.add("街区式居民地");
                childItem.add("散列式居民地");
                childItem.add("窑洞式居民地");
                childItem.add("其他");
            }else if (i==1){
                childItem.add("铁路");
                childItem.add("公路");
                childItem.add("机耕路");
                childItem.add("乡村路");
                childItem.add("小路");
                childItem.add("内部道路");
            }else if (i==2){
                childItem.add("管线");
                childItem.add("电力线");
                childItem.add("通讯线");
                childItem.add("管道");
                childItem.add("垣栅");
                childItem.add("城墙");
                childItem.add("围墙");
                childItem.add("栅栏");
                childItem.add("铁丝网");
                childItem.add("篱笆");
                childItem.add("堤");
            }else if (i==3){
                childItem.add("河流");
                childItem.add("湖泊");
                childItem.add("水库");
                childItem.add("池塘");
                childItem.add("沟渠");
                childItem.add("水源");
                childItem.add("沼泽");
                childItem.add("海岸线");
                childItem.add("瀑布");
                childItem.add("石滩");
                childItem.add("陡岸");
            }else if (i==4){
                childItem.add("森林");
                childItem.add("疏林");
                childItem.add("苗圃");
                childItem.add("狭长树木");
                childItem.add("行数");
                childItem.add("零星树木");
                childItem.add("经济林");
                childItem.add("草地");
                childItem.add("高草地");
                childItem.add("半荒植物地");
                childItem.add("荒草地");
                childItem.add("经济作物地");
                childItem.add("水生作物地");
                childItem.add("耕地");
                childItem.add("菜地");
            }else if (i==5){
                childItem.add("国界");
                childItem.add("国内各种境界");
            }else if (i==6){
                childItem.add("干河床");
                childItem.add("干涸湖");
                childItem.add("冲沟");
                childItem.add("陡崖");
                childItem.add("崩崖");
                childItem.add("滑坡");
                childItem.add("溶洞");
                childItem.add("熔岩漏斗");
                childItem.add("岩峰");
                childItem.add("梯田坎");
                childItem.add("山隘");
                childItem.add("陡石山");
                childItem.add("露岩地");
                childItem.add("石块地");
                childItem.add("戈壁滩");
                childItem.add("盐碱地");
                childItem.add("龟裂地");
                childItem.add("沙地");
                childItem.add("雪山");
            }
            childArray.add(childItem);
        }
    }

    /*
    * 读取内存卡.tpk地图
    * */
    public List<String> GettpkFileName(){
        String fileAbsolutePath=android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"ArcGISSurvey";
        List<String> tpkvectorfile=new ArrayList<String>();
        ArrayAdapter<String> arrayAdapter;
        File file=new File(fileAbsolutePath);
        File[] subFile=file.listFiles();
        for (int i=0;i<subFile.length;i++){
            if (!subFile[i].isDirectory()){
                String filename=subFile[i].getName();
                if (filename.trim().toLowerCase().endsWith(".tpk")){
                    tpkvectorfile.add(filename);
                }
            }
        }
        Dialog mapDialog=new Dialog(MainActivity.this);
        mapDialog.setTitle("矢量地图列表");
        View maplistview=getLayoutInflater().inflate(R.layout.maplist,null);
        ListView maplist= (ListView) maplistview.findViewById(R.id.maplist);
        arrayAdapter=new ArrayAdapter<String>(this,R.layout.maplist,tpkvectorfile);
        maplist.setAdapter(arrayAdapter);
        maplist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        maplist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView= (TextView) view.findViewById(R.id.itemview);
                String pathname=textView.getText().toString();
                tiledLayer=new ArcGISLocalTiledLayer(pathname);
                mapView.addLayer(tiledLayer);
                Toast.makeText(MainActivity.this,"添加成功",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this,"未选择图层，添加失败",Toast.LENGTH_LONG).show();
            }
        });
        mapDialog.setContentView(maplistview);
        mapDialog.show();
        return tpkvectorfile;
    }
}
