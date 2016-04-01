package com.example.yanhejin.myapplication.ChildActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yanhejin.myapplication.Database.CreateSurveyDB;
import com.example.yanhejin.myapplication.MainActivity;
import com.example.yanhejin.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUserName;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    CreateSurveyDB createSurveyDB;
    private CheckBox rememberpassword;
    private CheckBox autologin;
    String user="admin";
    String password="admin";
    String logtime;
    int permission;
    String path=android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"ArcGISSurvey/AttributeSurveyDB.db";
    boolean isRemmbered=false;
    View focusView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createSurveyDB=new CreateSurveyDB(LoginActivity.this,path,null,2);
        // Set up the login form.
        mUserName = (EditText) findViewById(R.id.username);
        rememberpassword= (CheckBox) findViewById(R.id.rememberpw);
        autologin= (CheckBox) findViewById(R.id.autologin);
        //populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        checkIsRenmmber();
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    Toast.makeText(LoginActivity.this,"create success!",Toast.LENGTH_LONG).show();
                    //attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                LoginActivity.this.onDestroy();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
    }

   public void checkIsRenmmber(){
       SQLiteDatabase check=createSurveyDB.getReadableDatabase();
       Cursor checkcursor=check.rawQuery("select * from user ", null);
       if (checkcursor.getCount()>0){

           String cusername=checkcursor.getString(1);
           String cpassword=checkcursor.getString(2);
           while (checkcursor.moveToFirst()){
               int remmber=checkcursor.getInt(checkcursor.getColumnIndex("admin"));
               if (remmber==1){
                   mUserName.setText(cusername);
                   mPasswordView.setText(cpassword);
               }else {
                   focusView=mUserName;
                   focusView.requestFocus();
               }
           }
       }else {
           focusView=mUserName;
           focusView.requestFocus();
       }

   }

   //登录信息
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserName.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserName.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;


        // 检查用户名是否为空
        if (TextUtils.isEmpty(username)) {
            mUserName.setError(getString(R.string.error_field_required));
            focusView = mUserName;
            cancel = true;
        }
        //检查密码是否为空
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //如果不为空的话登录
            SQLiteDatabase userdb=createSurveyDB.getReadableDatabase();
            Cursor usercursor=userdb.rawQuery("select * from user",null);
            if (usercursor.getCount()!=0){
                if (usercursor.moveToFirst()){
                    String cusername=usercursor.getString(usercursor.getColumnIndexOrThrow("UserName"));
                    String cpassword=usercursor.getString(usercursor.getColumnIndexOrThrow("PassWord"));
                    if (username.equals(cusername)&&password.equals(cpassword)){
                        /*Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);*/
                        Toast.makeText(LoginActivity.this,"验证成功！",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"请输入正确的用户名或密码！",Toast.LENGTH_LONG).show();
                    }
                }
            }
            else if (usercursor.getCount()==0){
                CreateDefaultUser();
                if (rememberpassword.isChecked()){
                   String UserName=usercursor.getString(usercursor.getColumnIndexOrThrow("UserName"));
                   String PassWord=usercursor.getString(usercursor.getColumnIndexOrThrow("PassWord"));
                    SQLiteDatabase db=createSurveyDB.getReadableDatabase();
                    db.execSQL("update user set isRemmber=? where name=?", new String[]{"1", "admin"});
                    db.close();
                    mUserName.setText(UserName);
                    mPasswordView.setText(PassWord);
                    /*Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.onDestroy();*/
                }
                else {
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.onDestroy();
                }
            }
            usercursor.close();
            userdb.close();
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            //添加验证用户名和密码的片段
            //attemptLogin();

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }



   /* public void IsRememberAndAutologin() {
        if (rememberpassword.isChecked() && autologin.isChecked() == false) {
            SQLiteDatabase userdb = createSurveyDB.getReadableDatabase();
            Cursor usercursor = userdb.rawQuery("select * from user", null);
            SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm");
            Date currentdate = new Date(System.currentTimeMillis());
            String logtime = format.format(currentdate);
            ContentValues uservalues = new ContentValues();
            while (!usercursor.moveToNext()) {
                String UserName = usercursor.getString(usercursor.getColumnIndex("UserName"));
                String PassWord = usercursor.getString(usercursor.getColumnIndex("PassWord"));
                if (mUserName.getText().equals(UserName) && mPasswordView.getText().equals(PassWord)) {
                    uservalues.put("UserName", mUserName.getText().toString());
                    uservalues.put("PassWord", mPasswordView.getText().toString());
                    uservalues.put("Permission", 1);
                    uservalues.put("LogTime", logtime);
                    userdb.insert("user", null, uservalues);
                    userdb.close();
                } else {
                    Toast.makeText(LoginActivity.this, "该用户名已存在！", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if (rememberpassword.isChecked()==false&&autologin.isChecked()){
                String UserName = usercursor.getString(usercursor.getColumnIndex("UserName"));
                String PassWord = usercursor.getString(usercursor.getColumnIndex("PassWord"));
                uservalues.put("UserName", mUserName.getText().toString());
                uservalues.put("PassWord", mPasswordView.getText().toString());
                uservalues.put("Permission", 1);
                uservalues.put("LogTime", logtime);
                userdb.insert("user", null, uservalues);
                mUserName.setText(UserName);
                mPasswordView.setText(PassWord);
                userdb.close();
            }
            if (rememberpassword.isChecked()&&autologin.isChecked()){
                String UserName = usercursor.getString(usercursor.getColumnIndex("UserName"));
                String PassWord = usercursor.getString(usercursor.getColumnIndex("PassWord"));
                if (mUserName.getText().equals(UserName) && mPasswordView.getText().equals(PassWord)) {
                    uservalues.put("UserName", mUserName.getText().toString());
                    uservalues.put("PassWord", mPasswordView.getText().toString());
                    uservalues.put("Permission", 1);
                    uservalues.put("LogTime", logtime);
                    userdb.insert("user", null, uservalues);
                    mUserName.setText(UserName);
                    mPasswordView.setText(PassWord);
                    userdb.close();
                } else {
                    Toast.makeText(LoginActivity.this, "该用户名已存在！", Toast.LENGTH_LONG).show();
                    return;
                }
            }

        }
    }*/

    public void CreateDefaultUser(){
        SQLiteDatabase db=createSurveyDB.getReadableDatabase();
        ContentValues values=new ContentValues();
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm");
        Date currentdate = new Date(System.currentTimeMillis());
        String logtime = format.format(currentdate);
        values.put("UserName", "admin");
        values.put("LogTime",logtime);
        db.insert("user",null,values);
        db.close();
    }
}





