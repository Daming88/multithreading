package com.daming.multithreading;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Test20 {
    // 线程1等待线程2的下载结果
    public static void main(String[] args) {

        GuardedObject guardedObject = new GuardedObject();

        new Thread(() -> {
            // 等待结果
            log.info("等待下载结果");
            List<String> res = (List<String>) guardedObject.get(2000);
            log.info("获取到下载结果,现在控制台输出:{}", res);
        }, "t1").start();

        new Thread(() -> {
            log.info("开始下载");
            try {
                List<String> download = Downloader.download();
                guardedObject.complete(download);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }
}


class GuardedObject {

    // 结果
    private Object response;

    // 获取结果
    public Object get() {
        synchronized (this){
            // 还没有结果
            while (response == null){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    // 获取结果,增加超时时间
    public Object get(long timeout) {
        synchronized (this){
            // 开始时间
            long begin = System.currentTimeMillis();
            // 经历时间
            long passedTime = 0;
            while (response == null){
                // 这轮循环应该等待的时间
                long waitTime = timeout-passedTime;
                if (waitTime<=0){
                    break;
                }
                try {
                    this.wait(waitTime); // 避免虚假唤醒，继续等待的时间边长
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passedTime = System.currentTimeMillis() - begin;
            }
            return response;
        }
    }

    public void complete(Object response){
        synchronized (this){
            this.response = response;
            this.notifyAll();
        }
    }
}

class Downloader {
   public static List<String> download() throws Exception {
       URLConnection conn = new URL("https://www.youtube.com/").openConnection();
       ArrayList<String> lines = new ArrayList<>();
       try(BufferedReader reader=new BufferedReader((new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)))){
           String line;
           while ((line=reader.readLine())!=null){
               lines.add(line);
           }
       }
       return lines;
   }
}