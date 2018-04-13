package com.download.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.download.entities.FileInfo;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sc on 2018/3/27.
 */

public class DownloadServices extends Service{
    public static final String DOWNLOAD_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/downloads/";
    public static final String ACTION_START="ACTION_START";
    public static final String ACTION_STOP="ACTION_STOP";
    public static final int MSG_INIT=0;
    public static final String ACTION_UPDATE="ACTION_UPDATE";
    private DownloadTask mTask=null;

    public int onStartCommand(Intent intent,int flags,int startId){
        //获得activity传来的参数
        if(ACTION_START.equals(intent.getAction())){
            FileInfo fileInfo=(FileInfo) intent.getSerializableExtra("fileInfo");
            //启动初始化线程
            new InitThread(fileInfo).start();
        }else if(ACTION_STOP.equals(intent.getAction())){
            FileInfo fileInfo=(FileInfo) intent.getSerializableExtra("fileInfo");
            if(mTask!=null){
                mTask.isPause=true;
            }
        }
        return super.onStartCommand(intent,flags,startId);
    }

    public IBinder onBind(Intent arg0){
        return null;
    }

    Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case MSG_INIT:
                    FileInfo fileInfo=(FileInfo)msg.obj;
                    Log.i("test","INIT:"+fileInfo);

                    //启动下载任务
                    mTask=new DownloadTask(DownloadServices.this,fileInfo);
                    mTask.download();
                    break;
            }
        }
    };

    //初始化子线程
    class InitThread extends Thread{
        private FileInfo mFileInfo=null;

        public InitThread(FileInfo mFileInfo){
            this.mFileInfo=mFileInfo;
        }

        public void run(){
            HttpURLConnection conn=null;
            RandomAccessFile raf=null;
            try {
                //连接网络文件，获得文件长度，在本地创建文件，设置文件长度
                URL url=new URL(mFileInfo.getUrl());
                conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length=-1;
                if(conn.getResponseCode()==200){
                    length=conn.getContentLength();
                }
                if(length<=0){
                    return;
                }
                File dir=new File(DOWNLOAD_PATH);
                if(!dir.exists()){
                    dir.mkdir();
                }
                File file=new File(dir,mFileInfo.getFilename());
                raf=new RandomAccessFile(file,"rwd");
                raf.setLength(length);
                mFileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT,mFileInfo).sendToTarget();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    raf.close();
                    conn.disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}
