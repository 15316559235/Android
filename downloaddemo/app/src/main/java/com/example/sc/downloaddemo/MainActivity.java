package com.example.sc.downloaddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.download.entities.FileInfo;
import com.download.services.DownloadServices;

public class MainActivity extends AppCompatActivity {

    private TextView mFileName=null;
    private ProgressBar mprogressBar=null;
    private Button mbtstop=null;
    private Button mbtstart=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化组件
        mFileName=(TextView)findViewById(R.id.FileName);
        mprogressBar=(ProgressBar)findViewById(R.id.progressBar);
        mbtstop=(Button)findViewById(R.id.btstop);
        mbtstart=(Button)findViewById(R.id.btstart);
        mprogressBar.setMax(100);

        //创建文件信息对象
        final FileInfo fileInfo=new FileInfo(0,
                "http://jwc.usst.edu.cn/picture/article/9/67/3d/e525bfaf4569a9ed57429aafe32c/1b7436c3-8056-4c14-8642-13a34258926c.doc",
                "1b7436c3-8056-4c14-8642-13a34258926c.doc",0,0);

        //添加事件监听
        mbtstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //通过Intent传递参数给Service
                Intent intent=new Intent(MainActivity.this, DownloadServices.class);
                intent.setAction(DownloadServices.ACTION_START);
                intent.putExtra("fileInfo",fileInfo);
                startService(intent);
            }
        });

        mbtstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //通过Intent传递参数给Service
                Intent intent=new Intent(MainActivity.this, DownloadServices.class);
                intent.setAction(DownloadServices.ACTION_STOP);
                intent.putExtra("fileInfo",fileInfo);
                startService(intent);
            }
        });

        //注册广播接收器
        IntentFilter filter=new IntentFilter();
        filter.addAction(DownloadServices.ACTION_UPDATE);
        registerReceiver(mReceiver,filter);
    }

    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    //更新UI广播的接收器
    BroadcastReceiver mReceiver =new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(DownloadServices.ACTION_UPDATE.equals(intent.getAction())){
                int finished=intent.getIntExtra("finished",0);
                mprogressBar.setProgress(finished);
            }
        }
    };
}
