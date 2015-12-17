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
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
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
import com.example.yanhejin.myapplication.Database.CreateSpatialDB;
import com.example.yanhejin.myapplication.Database.CreateSurveyDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CreateSurveyDB createSurveyDB;
    CreateSpatialDB createSpatialDB;
    private long exitTime;
    static final String mapURL = "http://cache1.arcgisonline.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer";
    MapView mapView;
    GeodatabaseFeatureTable geodatabaseFeatureTable;
    FeatureLayer featureLayer;
    ArcGISLocalTiledLayer tiledLayer;
    FloatingActionButton location;
    LocationManager locationManager;
    Object actionmode;
    protected static final String TAG = "EditGraphicElements";

    private static final String TAG_DIALOG_FRAGMENTS = "dialog";

    private static final String KEY_MAP_STATE = "com.esri.MapState";


    private enum EditMode {NONE, POINT, POLYLINE, POLYGON, SAVING}

    String mMapState;
    GraphicsLayer mGraphicLayer;
    SimpleFillSymbol mSimpleFillSymbol = null;
    SimpleMarkerSymbol markerSymbol;
    SimpleLineSymbol simpleLineSymbol;
    MapTouchListener mapTouchListener;

    boolean isMessageLength;

    PictureMarkerSymbol pictureMarkerSymbol;
    Point point;
    Point wgsPoint;
    Point mapPoint;
    Location loc;

    EditMode mEditMode;
    GraphicsLayer tempGraphic;

    boolean isDraw;
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
        createSpatialDB = new CreateSpatialDB(MainActivity.this, "SpatialSurveyDB", null, 1);
        createSurveyDB = new CreateSurveyDB(MainActivity.this, "AttributeSurveyDB", null, 1);
        //QueryUser();
        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mapView = (MapView) findViewById(R.id.mapview);
        ArcGISTiledMapServiceLayer maplayeronline=new ArcGISTiledMapServiceLayer(mapURL);
        mapView.addLayer(maplayeronline);
        mapView.addLayer(tiledLayer,0);
        point = (Point) GeometryEngine.project(new Point(40.805, 111.661), SpatialReference.create(4326), mapView.getSpatialReference());
        mapView.centerAt(point, true);
        mapView.enableWrapAround(true);
        mapView.setEsriLogoVisible(true);
        mGraphicLayer = new GraphicsLayer();
        mapView.addLayer(mGraphicLayer,1);
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
                                    if (loc != null) {
                                        double latitude = loc.getLatitude();
                                        double longitude = loc.getLongitude();
                                        Toast.makeText(MainActivity.this, "当前位置：" + "东经：" + String.valueOf(latitude) + "北纬：" + String.valueOf(longitude), Toast.LENGTH_LONG).show();
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
    }

    //标记位置
    private void markLocation(Location location) {
        //mGraphicLayer.removeAll();
        double locx = location.getLongitude();
        double locy = location.getLatitude();
        wgsPoint = new Point(locx, locy);
        mapPoint = (Point) GeometryEngine.project(wgsPoint, SpatialReference.create(4326), mapView.getSpatialReference());
        //创建图层
        Graphic graphic = new Graphic(mapPoint, pictureMarkerSymbol);
        mGraphicLayer.addGraphic(graphic);
        //画线
        Graphic graphicline = new Graphic(mapPoint, new SimpleLineSymbol(Color.BLUE, 4, SimpleLineSymbol.STYLE.SOLID));
        mGraphicLayer.addGraphic(graphicline);

        mapView.centerAt(mapPoint, true);
        SQLiteDatabase GPSdb = createSpatialDB.getReadableDatabase();//数据库为空
        ContentValues GPSValues = new ContentValues();
        GPSValues.put("x", locx);
        GPSValues.put("y", locy);
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

                if (mapView.isLoaded()) {
                    Polygon polygon = mapView.getExtent();
                    int dimension = polygon.getDimension();
                    Geometry.Type type = polygon.getType();
                    AlretMsg("地图范围dimension=%s,type=%s", dimension, type.value());
                }
                return true;
            default:
                return false;
        }
    }

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
                final RadioButton addtiffmap = (RadioButton) addmapview.findViewById(R.id.addtiffmap);
                AlertDialog.Builder addmapbuilder = new AlertDialog.Builder(MainActivity.this);
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
                actionmode = MainActivity.this.startActionMode(actioncallback);
                //mapTouchListener = new MapTouchListener(MainActivity.this, mapView);
                //mapView.setOnTouchListener(mapTouchListener);
                break;
            case R.id.camara:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
                break;
            case R.id.surveydiatance:
                MeasureDistance();
                break;
            case R.id.WiFishare:
                break;
            case R.id.sharedata:

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
            final String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Toast.makeText(this, name, Toast.LENGTH_LONG).show();
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

            FileOutputStream b = null;
            File file = new File("/sdcard/Image/");
            file.mkdirs();// 创建文件夹
            String fileName = "/sdcard/Image/" + name;

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

            } catch (Exception e) {
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
                ArcGISDynamicMapServiceLayer maplayeronline = new ArcGISDynamicMapServiceLayer(MapURL);
                try {
                    mapView.addLayer(maplayeronline,2);
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
            mapView.addLayer(featureLayer,3);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "载入的地图无效", Toast.LENGTH_LONG).show();
        }
    }

    //添加瓦片地图（本地）4
    public void addTiledMap() {
        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fullpath = path + "/" + "ArcGISSurvey/叠加图.tpk";
        tiledLayer = new ArcGISLocalTiledLayer(fullpath);
        try {
            mapView.addLayer(tiledLayer,4);
            GettpkFileName();
        } catch (Exception e) {
            e.getMessage();
            Toast.makeText(MainActivity.this, "载入的地图无效", Toast.LENGTH_LONG).show();
        }
    }

    String Type = null;

    public void setType(String featureType) {
        this.Type = featureType;
    }

    public String getType() {
        return this.Type;
    }

    //自定义的工具栏在上方
    private ActionMode.Callback actioncallback = new ActionMode.Callback() {
        /*PopupMenu zbpopup=new PopupMenu(MainActivity.this,new View(MainActivity.this));
        MenuInflater zbinflater=zbpopup.getMenuInflater();
        PopupMenu jjxpopup=new PopupMenu(MainActivity.this,new View(MainActivity.this));
        MenuInflater jjxinflater=jjxpopup.getMenuInflater();
        PopupMenu dmpopup=new PopupMenu(MainActivity.this,new View(MainActivity.this));
        MenuInflater dminflater=dmpopup.getMenuInflater();*/

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
                    jmdPopup();
                   break;
                case R.id.drawsx:
                    sxPopup();
                    return true;
                case R.id.drawdl:
                    dlPopup();
                    return true;
                case R.id.drawgx:
                    gxPopup();
                    return true;
                case R.id.drawjjl:
                    return true;
                case R.id.drawdm:
                    return true;
                case R.id.drawzb:
                    return true;

                default:
                    return false;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mode.finish();
        }
    };

    public void jmdPopup(){
        mapTouchListener = new MapTouchListener(MainActivity.this, mapView);
        mapView.setOnTouchListener(mapTouchListener);
        final String[] geometryType = {""};
        Type="jmdmenu";
        setType(Type);
        PopupMenu jmdpopup = new PopupMenu(MainActivity.this, new View(MainActivity.this));
        jmdpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawjqs:
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mapTouchListener.getType();
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(100);
                        mapTouchListener.setType(geometryType[0]);
                        break;
                    case R.id.drawyds:
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

    public void sxPopup(){
        mapTouchListener = new MapTouchListener(MainActivity.this, mapView);
        mapView.setOnTouchListener(mapTouchListener);
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
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 5, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawhp:
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.BLUE, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(20);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawsk:
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.BLUE, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(40);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawct:
                        mapTouchListener.geoType = Geometry.Type.POLYGON;
                        geometryType[0] = "Polygon";
                        mSimpleFillSymbol = new SimpleFillSymbol(Color.BLUE, SimpleFillSymbol.STYLE.SOLID);
                        mSimpleFillSymbol.setAlpha(30);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawgq:
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawsy:
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

    public void dlPopup(){
        final String[] geometryType = new String[1];
        Type = "daolumenu";
        setType(Type);
        PopupMenu dlpopup = new PopupMenu(MainActivity.this, new View(MainActivity.this));
        dlpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.drawtl:
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 4, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawgl:
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 4, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawjgl:
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 3, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawxl:
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 1, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawxcl:
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 2, SimpleLineSymbol.STYLE.DASH);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawnbdl:
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

    public void gxPopup(){
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
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 2, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawdlx:
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 1, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        return true;
                    case R.id.drawtxx:
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 1, SimpleLineSymbol.STYLE.SOLID);
                        return true;
                    case R.id.drawgd:
                        mapTouchListener.geoType = Geometry.Type.POLYLINE;
                        geometryType[0] = "Polyline";
                        simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 3, SimpleLineSymbol.STYLE.SOLID);
                        mapTouchListener.setType(geometryType[0]);
                        break;
                }
                return false;
            }
        });
        MenuInflater gxinflater = gxpopup.getMenuInflater();
        gxinflater.inflate(R.menu.gxmenu, gxpopup.getMenu());
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

    public void checkListener(){
        if (mapTouchListener==null){
            mapTouchListener=new MapTouchListener(MainActivity.this,mapView);
            mapView.setOnTouchListener(mapTouchListener);
        }
    }


    //地图触摸事件
    class MapTouchListener extends MapOnTouchListener {
        private Geometry.Type geoType = null;//用于判定当前选择的几何图形类型
        private Point ptStart = null;//起点
        private Point ptPrevious = null;//上一个点
        private ArrayList<Point> points = null;//记录全部点
        private Polygon tempPolygon = null;//记录绘制过程中的多边形
        final String tag = "TAG";

        public MapTouchListener(Context context, MapView view) {
            super(context, view);
            points = new ArrayList<Point>();
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
            if (geoType!=null){
                Point ptCurrent=mapView.toMapPoint(new Point(point.getX(),point.getY()));
                points.add(ptCurrent);
                if (ptStart==null){//起始点为空
                    ptStart=ptCurrent;
                    if (geoType == Geometry.Type.POINT ) {//直接画点
                        Graphic graphic = new Graphic(ptCurrent, markerSymbol);
                        mGraphicLayer.addGraphic(graphic);
                    }
                }
                else
                {//起始点不为空
                    if (geoType==Geometry.Type.POINT){
                        Graphic graphic = new Graphic(ptCurrent, markerSymbol);
                        mGraphicLayer.addGraphic(graphic);
                    }
                        //生成当前线段（由当前点和上一个点构成）
                        if (geoType == Geometry.Type.POLYLINE) {
                            //绘制当前线段
                                Polyline polyline = new Polyline();
                                Line line = new Line();
                                line.setStart(ptPrevious);
                                line.setEnd(ptCurrent);
                                polyline.addSegment(line,true);
                                Graphic g = new Graphic(polyline, simpleLineSymbol);
                                mGraphicLayer.addGraphic(g);
                                if (isMessageLength==true){
                                    // 计算当前线段的长度
                                    String length = Double.toString(Math.round(polyline.calculateLength2D())) + " 米";
                                    Toast.makeText(mapView.getContext(), length, Toast.LENGTH_SHORT).show();
                                }
                        }
                        else if (geoType == Geometry.Type.POLYGON) {
                            Line line = new Line();
                            line.setStart(ptPrevious);
                            line.setEnd(ptCurrent);
                            //绘制临时多边形
                            if (tempPolygon == null) tempPolygon = new Polygon();
                            tempPolygon.addSegment(line, true);
                            Graphic g = new Graphic(tempPolygon, mSimpleFillSymbol);
                            mGraphicLayer.addGraphic(g);
                            //计算当前面积
                            String sArea = getAreaString(tempPolygon.calculateArea2D()) + " 米";

                            Toast.makeText(mapView.getContext(), sArea, Toast.LENGTH_SHORT).show();
                        }
                    }
                    ptPrevious = ptCurrent;
                }
            return false;
        }


        //@Override
        public boolean onDoubleTap(MotionEvent point) {
            int pointid=0;
            int graphicid=0;

            if (geoType==Geometry.Type.POINT){
                for (int i=0;i<points.size();i++){
                    Point point1=points.get(i);
                    double x=point1.getX();
                    double y=point1.getY();
                    Toast.makeText(MainActivity.this,"当前坐标：x:"+x+","+"y:"+y,Toast.LENGTH_LONG).show();
                }
            }
            if(geoType==Geometry.Type.POLYLINE){
                Polyline polyline = new Polyline();
                Point startPoint = null;
                Point endPoint = null;
                // 绘制完整的线段
                if (points.size()<2){
                    Toast.makeText(MainActivity.this,"绘制点数小于2不能形成线段",Toast.LENGTH_LONG).show();
                    return false;
                }else {
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

                    Toast.makeText(mapView.getContext(), length, Toast.LENGTH_SHORT).show();                }

            }
            else if (geoType==Geometry.Type.POLYGON){
                Polygon polygon = new Polygon();

                Point startPoint = null;
                Point endPoint = null;
                // 绘制完整的多边形
                if (points.size()<2){
                    Toast.makeText(MainActivity.this,"绘制点数小于3不能形成闭合面",Toast.LENGTH_LONG).show();
                    return false;
                }else {
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
                    String sArea = getAreaString(polygon.calculateArea2D())+ " 米";

                    Toast.makeText(mapView.getContext(), sArea, Toast.LENGTH_SHORT).show();                }

            }else {
                return false;
            }
            //双击获取要素信息
            switch (Type){
                case "jmdmenu":
                    AlertDialog.Builder jmddata=new AlertDialog.Builder(MainActivity.this);
                    View jmdview=getLayoutInflater().inflate(R.layout.jumindi,null);
                    jmddata.setView(jmdview);
                    jmddata.setTitle("录入居民地属性信息");
                    final EditText linkData= (EditText) jmdview.findViewById(R.id.linkID);
                    final EditText fwcstext= (EditText) jmdview.findViewById(R.id.fwcs);
                    final EditText fwcztext= (EditText) jmdview.findViewById(R.id.fwcz);
                    final EditText fygztext= (EditText) jmdview.findViewById(R.id.fygz);
                    final EditText bztext= (EditText) jmdview.findViewById(R.id.bz);
                    final SQLiteDatabase jmdattributedb=createSurveyDB.getReadableDatabase();
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
                                if (fwcstext.getText().toString().equals("")||linkData.getText().equals("")) {
                                    if (linkData.getText().equals("")){
                                        Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();                                    }
                                    if (fwcstext.getText().toString().equals("")){
                                        Toast.makeText(MainActivity.this, "房屋层数为空！", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    ContentValues jmdattrvalues = new ContentValues();
                                    jmdattrvalues.put("LinkID", Integer.parseInt(linkData.getText().toString()));
                                    jmdattrvalues.put("FWCS", Integer.parseInt(fwcstext.getText().toString()));
                                    jmdattrvalues.put("FWCZ", fwcztext.getText().toString());
                                    jmdattrvalues.put("FYGZ", Float.valueOf(fygztext.getText().toString()));
                                    jmdattrvalues.put("BZ", bztext.getText().toString());
                                    jmdattributedb.insert("JMDData", null, jmdattrvalues);
                                }
                            } catch (Exception e) {
                                Log.i(tag, e.toString());
                            }
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
                        }
                    });
                    jmddata.create().show();
                    break;
                case "shuiximenu":
                    AlertDialog.Builder shuixiBuilder=new AlertDialog.Builder(MainActivity.this);
                    View shuixiView=getLayoutInflater().inflate(R.layout.shuixi,null);
                    final EditText LinkID= (EditText) shuixiView.findViewById(R.id.linkID);
                    final EditText featurename= (EditText) shuixiView.findViewById(R.id.featurename);
                    final EditText fssstext= (EditText) shuixiView.findViewById(R.id.fsss);
                    final EditText sxbztext= (EditText) shuixiView.findViewById(R.id.bz);
                    final SQLiteDatabase shuixidb=createSurveyDB.getReadableDatabase();
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
                                if (LinkID.getText().equals("")) {
                                    Toast.makeText(MainActivity.this, "连接号为空！", Toast.LENGTH_LONG).show();
                                } else {
                                    ContentValues shuixiValues = new ContentValues();
                                    shuixiValues.put("LinkID", Integer.parseInt(LinkID.getText().toString()));
                                    shuixiValues.put("FTName", featurename.getText().toString());
                                    shuixiValues.put("FSSS", fssstext.getText().toString());
                                    shuixiValues.put("BZ", sxbztext.getText().toString());
                                    shuixidb.insert("SXData", null, shuixiValues);
                                }
                            } catch (Exception e) {
                                Log.i(tag, e.toString());
                            }
                            shuixidb.close();
                            Toast.makeText(MainActivity.this, "属性数据存储成功！", Toast.LENGTH_LONG).show();
                            try {
                                int linkID = Integer.parseInt(LinkID.getText().toString());
                                SQLiteDatabase shuixispatialdb = createSpatialDB.getReadableDatabase();
                                Point currentPoint = null;
                                if (LinkID.getText().equals("")) {
                                    Toast.makeText(MainActivity.this, "linkID不存在，已取消空间数据录入！", Toast.LENGTH_LONG).show();
                                } else {
                                    if (points.size() < 2) {
                                        Toast.makeText(MainActivity.this, "没有要存储的空间数据", Toast.LENGTH_LONG).show();
                                    } else if (points.size() > 2) {
                                        ContentValues shuixiValues = new ContentValues();
                                        for (int i = 0; i < points.size(); i++) {
                                            currentPoint = points.get(i);
                                            shuixiValues.put("LinkAID",linkID);
                                            shuixiValues.put("x", currentPoint.getX());
                                            shuixiValues.put("y", currentPoint.getY());
                                            shuixispatialdb.insert("SXData", null, shuixiValues);
                                        }
                                        Toast.makeText(MainActivity.this, "空间数据保存成功", Toast.LENGTH_LONG).show();
                                        points.clear();
                                        shuixispatialdb.close();
                                    }
                                }
                            } catch (Exception e) {
                                Log.i(tag, e.toString());
                            }
                        }
                    });
                    shuixiBuilder.create().show();
                    break;
                default:
                    break;
            }
            ptStart = null;
            ptPrevious = null;
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
        fillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));
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
