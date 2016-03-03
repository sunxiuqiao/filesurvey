package com.example.yanhejin.myapplication.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by licheetec on 2015/5/2.
 */
public class CreateSurveyDB extends SQLiteOpenHelper {

    Context mcontext;


    public CreateSurveyDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name,factory, version);
        mcontext=context;
    }


    public static final String CreateUserDB="create table user("+"ID integer primary key autoincrement,"
            +"UserName varchar(50),"
            +"PassWord varchar(50),"
            +"Permission integer,"
            +"isRemmber integer,"
            +"LogTime varchar(50))";

    public static final String CreatePhotoDB="create table PhotoData("+"PhotoID integer primary key autoincrement,"
            +"photoname varchar(50),"
            +"photodescrible varchar(100),"
            +"phototime varchar(50))";

    public static final String CreateSurveyDB="create table surveydk("+"FID integer primary key,"
            +"BSM integer,"
            +"YSDM varchar(50),"
            +"YSBM varchar(50),"
            +"DKMC varchar(50),"
            +"SYQXZ varchar(50),"
            +"DKLB varchar(50),"
            +"TDSYLX varchar(50),"
            +"DLDJ varchar(50),"
            +"TDYT varchar(50),"
            +"SFJBNT varchar(50),"
            +"SCMJ REAL,"
            +"DKDZ varchar(50),"
            +"DKXZ varchar(50),"
            +"DKNZ varchar(50),"
            +"DKBZ varchar(50),"
            +"DKBZXX varchar(50),"
            +"ZJRXM varchar(50))";

    public static final String CreateDKDataDB="create table DKData("+"pointID integer,"
            +"x REAL,"
            +"y REAL,"
            +"graphicID integer,"
            +"DKDataID integer primary key(pointID,graphicID))";



    public static final String CreateJMDDB="create table JMDData("+"ID integer primary key autoincrement,"
            +"FTName varchar(50),"
            +"LinkID integer,"
            +"FWCS integer,"
            +"FWCZ varchar(50),"
            +"FYGZ REAL,"
            +"ZJTIME varchar(50),"
            +"BZ varchar(100))";

    public static  final String CreateSXDB="create table SXData("+" ID integer primary key autoincrement,"
            +"FTName varchar(50),"
            +"LinkID integer,"
            +"YSMC varchar(50),"
            +"FSSS varchar(50),"
            +"ZJTIME varchar(50),"
            +"BZ varchar(100))";
    public  static final String CreateDLDB="create table DLData("+"ID integer primary key autoincrement,"
            +"FTName varchar(50),"
            +"LinkID integer,"
            +"DLMC varchar(50),"
            +"DLXH varchar(50),"
            +"DJDM varchar(50),"
            +"ZJTIME varchar(50),"
            +"BZ varchar(100))";

    public static final String CreateZBDB="create table ZBData("+"ID integer primary key autoincrement,"
            +"FTName varchar(50),"
            +"LinkID integer,"
            +"YSMC varchar(50),"
            +"YSZL varchar(50),"
            +"SSLC varchar(50),"
            +"ZJTIME varchar(50),"
            +"BZ varchar(100))";
    public static final String CreateGXDB="create table GXData("+"ID integer primary key autoincrement,"
            +"FTName varchar(50),"
            +"LinkID integer,"
            +"DLXZX varchar(50),"
            +"DLXFS varchar(50),"
            +"ZJTIME varchar(50),"
            +"BZ varchar(100))";

    public static final  String CreateJJXDB="create table JJXData("+"ID integer primary key autoincrement,"
            +"FTName varchar(50),"
            +"LinkID integer,"
            +"GJ varchar(50),"
            +"NBJJX varchar(50),"
            +"ZJTIME varchar(50),"
            +"BZ varchar(100))";

    public static final String CreateZJDB="create table ZJData("+"ID integer primary key autoincrement,"
            +"FTName varchar(50),"
            +"LinkID integer,"
            +"ZJMC varchar(50),"
            +"ZJTIME varchar(50),"
            +"BZ varchar(100))";

    public static final String CreateDMDB="create table DMData("+"ID integer primary key autoincrement,"
            +"FTName varchar(50),"
            +"LinkID integer,"
            +"DMMC varchar(50),"
            +"ZJTIME varchar(50),"
            +"BZ varchar(100))";

    public static final String CreatePZJDB="create table PZJData("+"ID integer primary key autoincrement,"
            +"LinkID integer,"
            +"YSName varchar(50),"
            +"YSType varchar(50),"
            +"ZJTIME varchar(50),"
            +"BZ varchar(100))";

    public static final String CreateWZBZDB="create table WZBZData("+"ID integer primary key autoincrement,"
            +"LinkID integer,"
            +"YSName varchar(50),"
            +"YSType varchar(50),"
            +"ZJTIME varchar(50),"
            +"BZ varchar(100))";

    public static  final String CreateRecorderDB="create table RecorderDB("+"ID integer primary key autoincrement,"
            +"Name varchar(50),"
            +"descibe varchar(100),"
            +"recordetime varchar(50))";

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CreateUserDB);
        db.execSQL(CreateSurveyDB);
        //db.execSQL(CreateDKDataDB);
        db.execSQL(CreatePhotoDB);
        db.execSQL(CreateJMDDB);
        db.execSQL(CreateDLDB);
        db.execSQL(CreateSXDB);
        db.execSQL(CreateGXDB);
        db.execSQL(CreateJJXDB);
        db.execSQL(CreateZJDB);
        db.execSQL(CreateZBDB);
        db.execSQL(CreateDMDB);
        db.execSQL(CreatePZJDB);
        db.execSQL(CreateWZBZDB);
        db.execSQL(CreateRecorderDB);
        Toast.makeText(mcontext, "create success!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
        db.execSQL("drop table if exists surveydk");
        db.execSQL("drop table if exists DKData");
        db.execSQL("drop table if exists GPSData");
        db.execSQL("drop table if exists PhotoData");
        db.execSQL("drop table if exists JMDData");
        db.execSQL("drop table if exists DLData");
        db.execSQL("drop table if exists SXData");
        db.execSQL("drop table if exists GXData");
        db.execSQL("drop table if exists JJXData");
        db.execSQL("drop table if exists ZJData");
        db.execSQL("drop table if exists ZBData");
        db.execSQL("drop table if exists DMData");
        db.execSQL("drop table if exists PZJData");
        db.execSQL("drop table if exists WZBZData");
        db.execSQL("drop table if exists RecorderDB");
        onCreate(db);
    }
}
