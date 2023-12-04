package org.example;


import org.example.poolUtil.PoolUtil;
import java.sql.Connection;
import java.sql.SQLException;

public class MyTask1 implements Runnable{
    PoolUtil poolUtil = null;
    public MyTask1(PoolUtil poolUtil) {
        this.poolUtil = poolUtil;
    }
    //获得连接，并模仿处理
    @Override
    public void run() {
        Connection con = null;
        con = poolUtil.getConnection();
        try {
            Thread.currentThread().sleep( (long)(Math.random()*3000)+1000);//1s 到4秒
            System.out.println("task thread-----"+Thread.currentThread().getName());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            poolUtil.close(con,null,null);//动态代理处理
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
