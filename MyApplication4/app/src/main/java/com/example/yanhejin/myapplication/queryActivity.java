package com.example.yanhejin.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;


public class queryActivity extends AppCompatActivity implements BaiduMap.OnMapClickListener,OnGetRoutePlanResultListener {

    Button mBtnPre=null;
    Button mBtnNext=null;
    int nodeIndex=-1;
    RouteLine route=null;
    OverlayOptions routeOverlay=null;
    boolean useDefaultIcon=false;
    TextView popupText=null;
    MapView mapView=null;
    BaiduMap baiduMap=null;
    RoutePlanSearch mSearch=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        SDKInitializer.initialize(queryActivity.this);
        CharSequence titleLable="路径规划功能";
        setTitle(titleLable);
        mapView= (MapView) findViewById(R.id.map);
        baiduMap=mapView.getMap();
        LatLng cenpt=new LatLng(37.52,121.39);
        MapStatus mapStatus=new MapStatus.Builder().target(cenpt).build();
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);
        mBtnPre= (Button) findViewById(R.id.pre);
        mBtnNext= (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        baiduMap.setOnMapClickListener(this);
        mSearch=RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    public void SearchButtonProcess(View view){
        route=null;
    }
}

