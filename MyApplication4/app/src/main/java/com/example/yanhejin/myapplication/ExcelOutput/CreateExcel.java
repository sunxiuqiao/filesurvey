package com.example.yanhejin.myapplication.ExcelOutput;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yanhejin.myapplication.Database.CreateSpatialDB;
import com.example.yanhejin.myapplication.Database.CreateSurveyDB;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by licheetec on 2015/5/2.
 */
public class CreateExcel extends Activity {

    String dbpath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    CreateSurveyDB createSurveyDB;
    CreateSpatialDB createSpatialDB;
    SQLiteDatabase attribute=SQLiteDatabase.openOrCreateDatabase(dbpath+"/"+attributedb,null);
    SQLiteDatabase spatial=SQLiteDatabase.openOrCreateDatabase(dbpath+"/"+spatialdb,null);
    WritableSheet sheet;
    WritableWorkbook workbook;
    String[] title={"居民地","道路","水系","土质地貌","管线和电力线","境界线"};

    public CreateExcel(){
        createExcel();
    }
    public void createExcel(){
        try{
            String filepath=android.os.Environment.getDataDirectory().getAbsolutePath()+"/"+"ArcGISSurvey";
            File file=new File(filepath,"attributedb.xls");
            if (file.exists()){
                file.createNewFile();
            }
            workbook= Workbook.createWorkbook(file);
            sheet=workbook.createSheet("居民地",0);
            sheet=workbook.createSheet("道路",0);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveDataToExcel(int index,String[] content) throws WriteException, IOException {
        Label label;
        for (int i=0;i<title.length;i++){
            label=new Label(i,0,title[i]);
            sheet.addCell(label);
        }
        for (int i=0;i<title.length;i++){
            Label lablei=new Label(i,index,content[i]);
            sheet.addCell(lablei);
        }
        workbook.write();
        workbook.close();

    }

    public void writeToAttributeJMDExcel() throws IOException, WriteException {

        WritableWorkbook workbook = null;
        int ID;
        String FTName;
        int LinkID;
        int FWCS;
        String FWCZ;
        double FYGZ;
        String ZJTIME;
        String BZ;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, "attributedb.xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("居民地", 0);
        attribute = createSurveyDB.getReadableDatabase();
        Cursor jmdCursor = attribute.rawQuery("select * from JMDData", null);
        while (jmdCursor.moveToFirst()) for (int i = 0; i < jmdCursor.getCount(); i++) {
            ID = jmdCursor.getInt(0);
            FTName = jmdCursor.getString(1);
            LinkID = jmdCursor.getInt(2);
            FWCS = jmdCursor.getInt(3);
            FWCZ = jmdCursor.getString(4);
            FYGZ = jmdCursor.getDouble(5);
            ZJTIME = jmdCursor.getString(6);
            BZ = jmdCursor.getString(7);
            sheet.addCell(new Number(0, i, ID));
            sheet.addCell(new Label(1, i, FTName));
            sheet.addCell(new Number(2, i, LinkID));
            sheet.addCell(new Number(3, i, FWCS));
            sheet.addCell(new Label(4, i, FWCZ));
            sheet.addCell(new Number(5, i, FYGZ));
            sheet.addCell(new Label(6, i, ZJTIME));
            sheet.addCell(new Label(7, i, BZ));
        }
        jmdCursor.close();
        workbook.write();
        if (workbook != null) {
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void WriteToAttributeDLExcel() throws IOException, WriteException {
        WritableWorkbook workbook=null;
        int ID;
        String FTName;
        int LinkID;
        String DLMC;
        String DLXH;
        String DJDM;
        String ZJTIME;
        String BZ;
        String filepath=android.os.Environment.getDataDirectory().getAbsolutePath()+"/"+"ArcGISSurvey";
        File file=new File(filepath,"attributedb.xls");
        if (file.exists()){
            file.createNewFile();
        }
        workbook=Workbook.createWorkbook(file);
        WritableSheet sheet=workbook.createSheet("道路", 1);
        attribute=createSurveyDB.getReadableDatabase();
        Cursor dlCursor=attribute.rawQuery("select * from DLData",null);
        while (dlCursor.moveToFirst()){
            for (int i=0;i<dlCursor.getCount();i++){
                ID = dlCursor.getInt(0);
                FTName = dlCursor.getString(1);
                LinkID = dlCursor.getInt(2);
                DLMC = dlCursor.getString(3);
                DLXH = dlCursor.getString(4);
                DJDM = dlCursor.getString(5);
                ZJTIME = dlCursor.getString(6);
                BZ = dlCursor.getString(7);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Label(1, i, FTName));
                sheet.addCell(new Number(2, i, LinkID));
                sheet.addCell(new Label(3, i, DLMC));
                sheet.addCell(new Label(4, i, DLXH));
                sheet.addCell(new Label(5, i, DJDM));
                sheet.addCell(new Label(6, i, ZJTIME));
                sheet.addCell(new Label(7, i, BZ));
            }
            dlCursor.close();
            workbook.write();
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void WriteToAttributeSXExcel() throws IOException, WriteException {
        WritableWorkbook workbook = null;
        int ID;
        String FTName;
        int LinkID;
        String YSMC;
        String FSSS;
        String ZJTIME;
        String BZ;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, "attributedb.xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet=workbook.createSheet("水系",2);
        attribute=createSurveyDB.getReadableDatabase();
        Cursor sxCursor=attribute.rawQuery("select * from SXData",null);
        while (!sxCursor.moveToFirst()){
            for (int i=0;i<sxCursor.getCount();i++){
                ID = sxCursor.getInt(0);
                FTName = sxCursor.getString(1);
                LinkID = sxCursor.getInt(2);
                YSMC = sxCursor.getString(3);
                FSSS = sxCursor.getString(4);
                ZJTIME = sxCursor.getString(5);
                BZ = sxCursor.getString(6);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Label(1, i, FTName));
                sheet.addCell(new Number(2, i, LinkID));
                sheet.addCell(new Label(3, i, YSMC));
                sheet.addCell(new Label(4, i, FSSS));
                sheet.addCell(new Label(5, i, ZJTIME));
                sheet.addCell(new Label(6, i, BZ));
            }
            sxCursor.close();
            workbook.write();
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void WriteToAttributeDMExcel() throws IOException, WriteException {
        WritableWorkbook workbook=null;
        int ID;
        String FTName;
        int LinkID;
        String DMMC;
        String ZJTIME;
        String BZ;
        String filepath=android.os.Environment.getDataDirectory().getAbsolutePath()+"/"+"ArcGISSurvey";
        File file=new File(filepath,"attributedb.xls");
        if (file.exists()){
            file.createNewFile();
        }
        workbook=Workbook.createWorkbook(file);
        WritableSheet sheet=workbook.createSheet("土质地貌", 3);
        attribute=createSurveyDB.getReadableDatabase();
        Cursor dmCursor= attribute.rawQuery("select * from DMData",null);
        while (dmCursor.moveToFirst()){
            for (int i=0;i<dmCursor.getCount();i++){
                ID = dmCursor.getInt(0);
                FTName = dmCursor.getString(1);
                LinkID = dmCursor.getInt(2);
                DMMC = dmCursor.getString(3);
                ZJTIME = dmCursor.getString(4);
                BZ = dmCursor.getString(5);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Label(1, i, FTName));
                sheet.addCell(new Number(2, i, LinkID));
                sheet.addCell(new Label(3, i, DMMC));
                sheet.addCell(new Label(4, i, ZJTIME));
                sheet.addCell(new Label(5, i, BZ));
            }
            dmCursor.close();
            workbook.write();
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
