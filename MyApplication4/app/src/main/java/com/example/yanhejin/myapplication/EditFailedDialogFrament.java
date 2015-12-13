package com.example.yanhejin.myapplication;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
/**
 * Created by licheetec on 2015/5/2.
 */
public class EditFailedDialogFrament  extends DialogFragment{
    String mMessage;
    public EditFailedDialogFrament(){

    }

    public void setMessage(String message){
        mMessage=message;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.edit_failed,container,false);
        getDialog().setTitle(R.string.title_edit_failed);
        Button button= (Button) view.findViewById(R.id.yes_key);
        TextView textView= (TextView) view.findViewById(R.id.edit_failed_msg);
        textView.setText(mMessage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}
