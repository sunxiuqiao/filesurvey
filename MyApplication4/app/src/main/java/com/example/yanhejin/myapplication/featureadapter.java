package com.example.yanhejin.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by licheetec on 2015/5/2.
 */
public class featureadapter extends BaseAdapter {
    private String[] features;
    private Context c;

    public featureadapter(Context c, String[] features) {
        super();
        this.c = c;
        this.features = features;
    }

    @Override
    public int getCount() {
        return features.length;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        //返回每一条Item的Id
        return arg0;
    }

    @Override
    public boolean hasStableIds() {
        //getCheckedItemIds()方法要求此处返回为真
        return true;
    }
    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        featurechecked featurechecked = new featurechecked(c, null);
        featurechecked.setTitle(features[arg0]);
        return featurechecked;
    }

}
