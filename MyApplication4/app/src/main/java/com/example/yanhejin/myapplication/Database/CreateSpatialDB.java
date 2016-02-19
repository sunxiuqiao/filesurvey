package com.example.yanhejin.myapplication.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by licheetec on 2015/5/2.
 */
public class CreateSpatialDB extends SQLiteOpenHelper {
    CreateSpatialDB createSpatialDB;
    Context mcontext;
    String spatialdbname=android.os.Environment.getDataDirectory().getAbsolutePath()+"/"+"ArcGISSurvey"+"SpatialSurveyDB.db";
    SQLiteDatabase db;
    public CreateSpatialDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mcontext=context;
        spatialdbname=name;
    }



    public String CreateGPSData="create table GPSData("+"GPSID integer primary key autoincrement,"
            +"x REAL,"
            +"y REAL,"
            +"GPSdate REAL)";

    public String CreateDLData="create table DLData("+"DLID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateJMDData="create table JMDData("+"JMDID integer primary key autoincrement,"
            + "LinkAID integer,"
            + "x REAL,"
            + "y REAL)";

    public String CreateSXData="create table SXData("+"SXID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateGXData="create table GXData("+"GXID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateZBData="create table ZBData("+"ZBID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateJJXData="create table JJXData("+"JJXID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateDMData="create table DMData("+"DMID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateZJData="create table ZJData("+"ZJID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreatePZJData="create table PZJData("+"PZJID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateWZZJData="create table WZBZData("+"WZZJID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateJMDData);
        db.execSQL(CreateDLData);
        db.execSQL(CreateGPSData);
        db.execSQL(CreateSXData);
        db.execSQL(CreateDMData);
        db.execSQL(CreateZBData);
        db.execSQL(CreateGXData);
        db.execSQL(CreateJJXData);
        db.execSQL(CreateZJData);
        db.execSQL(CreatePZJData);
        db.execSQL(CreateWZZJData);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="drop table if exists GPSData";
        String jmdsql="drop table if exists JMDData";
        String dlsql="drop table if exists DLData";
        db.execSQL("drop table if exists SXData");
        db.execSQL("drop table if exists GXData");
        db.execSQL("drop table if exists JJXData");
        db.execSQL("drop table if exists ZJData");
        db.execSQL("drop table if exists ZBData");
        db.execSQL("drop table if exists DMData");
        db.execSQL("drop table if exists PZJData");
        db.execSQL("drop table if exists WZBZData");
        db.execSQL(sql);
        db.execSQL(jmdsql);
        db.execSQL(dlsql);
        onCreate(db);
    }
}
