package com.example.sc.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    //发送Intent对应字符串内容的key
    public  static  final String Intent_key="MESSAGE";
    //EditText
    private EditText editText =null;
    private void initView(){
        editText= (EditText) findViewById(R.id.edit_message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局
        setContentView(R.layout.activity_main);
        initView();
    }

    //发送消息，启动secondActivity!
    public void sendMessage(View view){
        Intent intent = new Intent(this,SecondActivity.class);
        String text =editText.getText().toString();
        intent.putExtra(Intent_key,text);
        startActivityForResult(intent,0);//此处的requestCode应与下面结果处理函中调用的requestCode一致
    }
    //结果处理函数，当从secondActivity中返回时调用此函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK){
            Bundle bundle = data.getExtras();
            String text =null;
            if(bundle!=null)
                text=bundle.getString("second");
            Log.d("text",text);
            editText.setText(text);
        }
    }
}
