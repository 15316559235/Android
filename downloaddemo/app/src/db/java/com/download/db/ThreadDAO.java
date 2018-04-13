package com.download.db;

import com.download.entities.ThreadInfo;

import java.util.List;

/**
 * Created by sc on 2018/3/27.
 */

public interface ThreadDAO {
    //插入线程信息
    public void insertThread(ThreadInfo threadInfo);
    //删除线程
    public void deleteThread(String url,int thread_id);
    //更新线程
    public void updateThread(String url,int thread_id,int finished);
    //查询文件线程信息
    public List<ThreadInfo> getThreads(String url);
    //判断线程信息已经存在
    public boolean isExists(String url,int thread_id);
}
