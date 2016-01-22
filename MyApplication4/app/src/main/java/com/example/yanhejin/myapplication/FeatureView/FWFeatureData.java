package com.example.yanhejin.myapplication.FeatureView;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.yanhejin.myapplication.Database.CreateSpatialDB;
import com.example.yanhejin.myapplication.Database.CreateSurveyDB;

/**
 * Created by licheetec on 2015/5/2.
 */
public class FWFeatureData {

    CreateSurveyDB createSurveyDB;
    CreateSpatialDB createSpatialDB;
    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    SQLiteDatabase adb;
    SQLiteDatabase sdb;
    public FWFeatureData(Context context){
        createSurveyDB=new CreateSurveyDB(context,dbpath+"/"+attributedb,null,1);
        createSpatialDB=new CreateSpatialDB(context,dbpath+"/"+spatialdb,null,1);
    }

}
