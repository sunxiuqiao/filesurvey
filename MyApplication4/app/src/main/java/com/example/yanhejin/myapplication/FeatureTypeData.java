package com.example.yanhejin.myapplication;

import android.graphics.Bitmap;

import com.esri.core.symbol.Symbol;

/**
 * Created by licheetec on 2015/5/2.
 */
public class FeatureTypeData  {
    private Bitmap bitmap;
    private String name;
    private Symbol symbol;

    public FeatureTypeData(Bitmap bitmap,String name,Symbol symbol){
        this.bitmap=bitmap;
        this.name=name;
        this.symbol=symbol;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public void  setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
    }
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public Symbol getSymbol(){
        return symbol;
    }

    public void setSymbol(Symbol symbol){
        this.symbol=symbol;
    }


}
