package com.download.services;

import android.content.Context;
import android.content.Intent;

import com.download.db.*;
import com.download.db.ThreadDAO;
import com.download.entities.FileInfo;
import com.download.entities.ThreadInfo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by sc on 2018/3/27.
 */

public class DownloadTask {
    private Context mContext=null;
    private FileInfo mFileInfo=null;
    private ThreadDAO mDAO=null;
    private int mFinished=0;
    public boolean isPause=false;

    public DownloadTask(Context mContext, FileInfo mFileInfo) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        mDAO=new ThraedDAOImpl(mContext);
    }

    public void download(){
        //读取数据库的线程信息
        List<ThreadInfo> threadInfos=mDAO.getThreads(mFileInfo.getUrl());
        ThreadInfo threadInfo=null;
        if(threadInfos.size()==0){
            //初始化线程信息
            threadInfo=new ThreadInfo(0,mFileInfo.getUrl(),0,mFileInfo.getLength(),0);
        }else {
            threadInfo=threadInfos.get(0);
        }

        //创建子线程进行下载
        new DownloadThread(threadInfo).start();
    }

    class DownloadThread extends Thread{
        private ThreadInfo mThreadInfo=null;

        public DownloadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }

        public void run(){
            //向数据库插入线程信息
            if(!mDAO.isExists(mThreadInfo.getUrl(),mThreadInfo.getId())){
                mDAO.insertThread(mThreadInfo);
            }

            //设置下载位置
            HttpURLConnection conn=null;
            RandomAccessFile raf=null;
            InputStream input=null;
            try {
                URL url=new URL(mThreadInfo.getUrl());
                conn=(HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");

                int start=mThreadInfo.getStart()+mThreadInfo.getFinished();
                conn.setRequestProperty("Range","bytes="+start+"-"+mThreadInfo.getEnd());

                //设置文件写入位置
                File file=new File(DownloadServices.DOWNLOAD_PATH,mFileInfo.getFilename());
                raf=new RandomAccessFile(file,"rwd");
                raf.seek(start);

                Intent intent =new Intent(DownloadServices.ACTION_UPDATE);
                mFinished+=mThreadInfo.getFinished();
                //开始下载
                if(conn.getResponseCode()==206){
                    //读取数据
                    input=conn.getInputStream();
                    byte[] buffer=new byte[1024*4];
                    int len=-1;

                    long time=System.currentTimeMillis();
                    while((len=input.read(buffer))!=-1){
                        //写入文件
                        raf.write(buffer,0,len);

                        //把下载进度发送广播给Activity
                        mFinished+=len;
                        if(System.currentTimeMillis()-time>500){
                            time=System.currentTimeMillis();
                            intent.putExtra("finished",(mFinished*100/mFileInfo.getLength()));
                            mContext.sendBroadcast(intent);
                        }

                        //在下载暂停时，保存下载进度
                        if(isPause){
                            mDAO.updateThread(mThreadInfo.getUrl(),mThreadInfo.getId(),mFinished);
                            return;
                        }
                    }
                }

                //删除线程信息
                mDAO.deleteThread(mThreadInfo.getUrl(),mThreadInfo.getId());
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    conn.disconnect();
                    raf.close();
                    input.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
