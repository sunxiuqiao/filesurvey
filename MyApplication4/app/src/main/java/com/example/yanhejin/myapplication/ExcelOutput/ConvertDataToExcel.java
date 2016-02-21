package com.example.yanhejin.myapplication.ExcelOutput;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yanhejin.myapplication.Database.CreateSurveyDB;
import com.example.yanhejin.myapplication.MainActivity;
import com.example.yanhejin.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ConvertDataToExcel extends AppCompatActivity {

    String dbpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ArcGISSurvey";
    String attributedb="AttributeSurveyDB.db";
    String spatialdb="SpatialSurveyDB.db";
    EditText convername;
    Button cancelConvert;
    Button ConverToExcel;
    CreateSurveyDB createSurveyDB=new CreateSurveyDB(this,dbpath + "/" + attributedb, null,2);
    CreateSurveyDB createSpatialDB =new CreateSurveyDB(this,dbpath + "/" + spatialdb, null,2);
    WritableWorkbook writableWorkbook;
    WritableSheet writableSheet;
    String excelPath;
    File excelFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_data_to_excel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        convername= (EditText) findViewById(R.id.editeexcelname);
        cancelConvert= (Button) findViewById(R.id.cancelconvert);
        ConverToExcel= (Button) findViewById(R.id.convertToExcel);
        ConverToExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String name=convername.getText().toString();
                if (!name.equals("")){
                    try {
                        //WriteToAttributeDLExcel(name);
                        WriteSpatialToExcel(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    } catch (BiffException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        cancelConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ConvertDataToExcel.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    public String getExcelDir() {
        // SD卡指定文件夹
        String sdcardPath = Environment.getExternalStorageDirectory().toString();
        File dir = new File(sdcardPath + File.separator + "ArcGISSurvey");
        if (dir.exists()) {
            return dir.toString();

        } else {
            dir.mkdirs();
            Log.d("BAG", "保存路径不存在,");
            return dir.toString();
        }
    }
    public void CreateExcel(File file) throws IOException, WriteException {
        if (!file.exists()){
            writableWorkbook=Workbook.createWorkbook(file);
            writableSheet=writableWorkbook.createSheet("居民地", 0);
            writableSheet=writableWorkbook.createSheet("道路", 1);
            writableSheet=writableWorkbook.createSheet("水系",2);
            writableSheet=writableWorkbook.createSheet("管线",3);
            writableSheet=writableWorkbook.createSheet("植被",4);
            writableSheet=writableWorkbook.createSheet("土质地貌",5);
            writableSheet=writableWorkbook.createSheet("地理注记",6);
            writableSheet=writableWorkbook.createSheet("文字注记",7);
            writableSheet=writableWorkbook.createSheet("境界线",8);
            writableWorkbook.write();
            writableWorkbook.close();
        }
    }

    public void CreateSpatialExcel(File file) throws IOException, WriteException {
        if (!file.exists()){
            writableWorkbook=Workbook.createWorkbook(file);
            writableSheet=writableWorkbook.createSheet("居民地", 0);
            writableSheet=writableWorkbook.createSheet("道路", 1);
            writableSheet=writableWorkbook.createSheet("水系",2);
            writableSheet=writableWorkbook.createSheet("管线",3);
            writableSheet=writableWorkbook.createSheet("植被",4);
            writableSheet=writableWorkbook.createSheet("土质地貌",5);
            writableSheet=writableWorkbook.createSheet("地理注记",6);
            writableSheet=writableWorkbook.createSheet("文字注记",7);
            writableSheet=writableWorkbook.createSheet("境界线",8);
            writableWorkbook.write();
            writableWorkbook.close();
        }
    }
    public void WriteToAttributeDLExcel(String name) throws IOException, WriteException, BiffException {
        int ID;
        String FTName;
        int LinkID;
        String DLMC;
        String DLXH;
        String DJDM;
        String ZJTIME;
        String BZ;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Date currentdate = new Date(System.currentTimeMillis());
        final String zhujitime = format.format(currentdate);
        excelPath = getExcelDir()+File.separator+zhujitime+"属性数据"+name+".xls";
        excelFile = new File(excelPath);
        CreateExcel(excelFile);
        WorkbookSettings wbsetting=new WorkbookSettings();
        wbsetting.setLocale(new Locale("en","EN"));
        Workbook oldwwb= Workbook.getWorkbook(excelFile,wbsetting);
        WritableWorkbook workbook=Workbook.createWorkbook(excelFile, oldwwb);
        WritableSheet sheet = workbook.getSheet(1);
        sheet.addCell(new Label(0, 0, "ID号"));
        sheet.addCell(new Label(1, 0, "要素名称"));
        sheet.addCell(new Label(2, 0, "连接ID号"));
        sheet.addCell(new Label(3, 0, "道路名称"));
        sheet.addCell(new Label(4, 0, "道路线号"));
        sheet.addCell(new Label(5, 0, "道路代码"));
        sheet.addCell(new Label(6, 0, "时间"));
        sheet.addCell(new Label(7, 0, "备注"));
        SQLiteDatabase attribute = createSurveyDB.getReadableDatabase();
        Cursor dlCursor = attribute.rawQuery("select * from DLData", null);
        int j=dlCursor.getCount();
        dlCursor.moveToFirst();
        for (  int i=1;i<dlCursor.getCount()+1;i++){
            dlCursor.move(i-1);
            ID = dlCursor.getInt(0);
            FTName = dlCursor.getString(1);
            LinkID = dlCursor.getInt(2);
            DLMC = dlCursor.getString(3);
            DLXH = dlCursor.getString(4);
            DJDM = dlCursor.getString(5);
            ZJTIME = dlCursor.getString(6);
            BZ = dlCursor.getString(7);
            sheet.addCell(new jxl.write.Number(0, i, ID));
            sheet.addCell(new Label(1, i, FTName));
            sheet.addCell(new jxl.write.Number(2, i, LinkID));
            sheet.addCell(new Label(3, i, DLMC));
            sheet.addCell(new Label(4, i, DLXH));
            sheet.addCell(new Label(5, i, DJDM));
            sheet.addCell(new Label(6, i, ZJTIME));
            sheet.addCell(new Label(7, i, BZ));
        }
        workbook.write();
        workbook.close();
        dlCursor.close();
        Toast.makeText(this, "导出数据成功", Toast.LENGTH_LONG).show();
    }

    public void WriteSpatialToExcel(String name) throws IOException, WriteException, BiffException {
        int ID;
        int LinkID;
        double x;
        double y;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Date currentdate = new Date(System.currentTimeMillis());
        final String zhujitime = format.format(currentdate);
        excelPath = getExcelDir()+File.separator+zhujitime+"空间数据"+name+".xls";
        excelFile = new File(excelPath);
        CreateSpatialExcel(excelFile);
        WorkbookSettings wbsetting=new WorkbookSettings();
        wbsetting.setLocale(new Locale("en","EN"));
        Workbook oldwwb= Workbook.getWorkbook(excelFile, wbsetting);
        WritableWorkbook workbook=Workbook.createWorkbook(excelFile, oldwwb);
        WritableSheet sheet = workbook.getSheet(1);
        sheet.addCell(new Label(0, 0, "ID号"));
        sheet.addCell(new Label(1, 0, "连接ID号"));
        sheet.addCell(new Label(2, 0, "x"));
        sheet.addCell(new Label(3, 0, "y"));
        SQLiteDatabase spatialdb=createSpatialDB.getReadableDatabase();
        /*
        * order by ID desc降序排列
        * order by DLID asc升序排列
        * */
        Cursor cursor=spatialdb.rawQuery("select DLID,LinkAID,x,y from DLData order by DLID asc",null);
        int j=cursor.getCount();
        cursor.moveToFirst();
        for (int i=1;i<cursor.getCount()+1;i++){
            cursor.move(i-1);
            ID = cursor.getInt(0);
            LinkID=cursor.getInt(1);
            x=cursor.getDouble(2);
            y=cursor.getDouble(3);
            sheet.addCell(new jxl.write.Number(0, i, ID));
            sheet.addCell(new jxl.write.Number(1, i, LinkID));
            sheet.addCell(new jxl.write.Number(2, i, x));
            sheet.addCell(new jxl.write.Number(3, i, y));
        }
        workbook.write();
        workbook.close();
        cursor.close();
    }

}
