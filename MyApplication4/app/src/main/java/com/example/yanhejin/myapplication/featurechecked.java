package com.example.yanhejin.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by licheetec on 2015/5/2.
 */
public class featurechecked extends LinearLayout implements Checkable {

    private TextView mText;
    private CheckBox mCheckBox;
    public featurechecked(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public featurechecked(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public featurechecked(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context){
        // 填充布局
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.features, featurechecked.this, true);
        mText = (TextView) v.findViewById(R.id.featuresname);
        mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
    }

    @Override
    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);

    }

    @Override
    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    @Override
    public void toggle() {
        mCheckBox.toggle();
    }

    public void setTitle(String text){
        mText.setText(text);
    }

}
