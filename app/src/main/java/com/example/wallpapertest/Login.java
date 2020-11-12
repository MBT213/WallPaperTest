package com.example.wallpapertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends BaseActivity {

    private MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myDatabaseHelper = new MyDatabaseHelper(this,"WallPaper.db",null,1);
        final Button login = (Button)findViewById(R.id.login);
        final Button register = (Button)findViewById(R.id.register);
        final EditText usernameEdit = (EditText)findViewById(R.id.user_name);
        final EditText passwordEdit = (EditText)findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseHelper.getWritableDatabase();
                final String usename = usernameEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                if (login(usename,password)){
                    SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
                    String Query = "Select email from user where username =? and password =?";
                    Cursor cursor = db.rawQuery(Query,new String[]{usename,password});
                    cursor.moveToFirst();
                    String email = cursor.getString(cursor.getColumnIndex("email"));
                    Intent intent = new Intent(Login.this,MainActivity.class);
                    Log.d("passinfor1",usename+","+password);
                    intent.putExtra("USER_NAME",usename);
                    intent.putExtra("EMAIL",email);
                    startActivity(intent);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
    }

    public boolean login(String username,String password){
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        String Query = "Select * from user where username =? and password =?";
        Cursor cursor = db.rawQuery(Query,new String[]{username,password});
        if (cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        return false;
    }
}