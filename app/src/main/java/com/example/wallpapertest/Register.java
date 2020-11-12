package com.example.wallpapertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends BaseActivity {

    private MyDatabaseHelper myDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myDatabaseHelper = new MyDatabaseHelper(this,"WallPaper.db",null,1);
        final EditText usernameEdit = (EditText)findViewById(R.id.user_name);
        final EditText passwordEdit = (EditText)findViewById(R.id.password);
        final EditText useremailEdit = (EditText)findViewById(R.id.user_email);
        Button register = (Button)findViewById(R.id.register);

        //Log.d("information2",usename+","+password+","+useremail);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usename = usernameEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                final String useremail = useremailEdit.getText().toString();
                if (CheckIsDataAlreadyInDBorNot(usename)){
                    Toast.makeText(Register.this,"该用户名已被注册",Toast.LENGTH_SHORT);
                }else if (register_information(usename,password,useremail)){
                    //Log.d("information",usename+","+password+","+useremail);
                    Toast.makeText(Register.this,"注册成功",Toast.LENGTH_SHORT);
                    Intent intent = new Intent(Register.this,Login.class);
                    startActivity(intent);
                }
            }
        });
    }
    public boolean register_information(String usename,String password,String useremail){
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        //db.execSQL("delete from user where username = ?",new String[]{" "});
        ContentValues values = new ContentValues();
        values.put("username",usename);
        values.put("password",password);
        values.put("email",useremail);
        db.insert("user",null,values);
        Log.d("information2",usename+","+password+","+useremail);
        return true;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String value){
        SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
        String Query = "Select * from user where username =?";
        Cursor cursor = db.rawQuery(Query,new String[] { value });
        if (cursor.getCount()>0){
            cursor.close();
            return  true;
        }
        cursor.close();
        return false;
    }
}