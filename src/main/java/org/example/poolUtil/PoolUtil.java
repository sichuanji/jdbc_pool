package org.example.poolUtil;

import com.sun.jmx.remote.internal.ArrayQueue;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.Properties;
import java.util.Queue;

public class PoolUtil {
    //连接的管理 添加 删除 维护，需要继承于DataSource接口
//之前是使用的list
    private static Queue<Connection> cons = new ArrayDeque<>();
    private static int coreSize;
    private static int maxSize;
    private static int maxWait;
    private volatile int rest = 5;
    private volatile int createCount = 5;
    // 可以使用原子变量 volatile 和 设置同步方法来实现同步
    private volatile int waitCount = 0;
    private static PoolFactory poolFactory = new PoolFactory() ;

    /**
     * 使用单列模式，维护为一个对象
     */
    private static PoolUtil poolUtil = null;

    private PoolUtil() {

    }

    //返回单列
    public static PoolUtil getInstance(){
        if(poolUtil==null) return new PoolUtil();
        return poolUtil;
    }


    /**
     * 读取配置文件，同时初始化核心线程
     */
    static{
        //1 读取配置文件
        InputStream in = PoolFactory.class.getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        try {
            properties.load(in);
            coreSize = Integer.parseInt(properties.getProperty("coreSize"));
            maxSize = Integer.parseInt(properties.getProperty("maxSize"));
            maxWait = Integer.parseInt(properties.getProperty("maxWait"));
        } catch (Exception e) {
            System.out.println("加载 inputstream出现错误");
        }
        //2初始化核心线程
        poolFactory = new PoolFactory();
        for (int i = 0; i < coreSize; i++) {
            cons.add(poolFactory.getConnection());
        }
    }

    /**
     * 提供连接
     * <p>
     * case1 当前有剩余连接
     * case2 没有剩余连接 可以创建
     * case3 没有剩余连接 等待
     */
    public Connection getConnection() {
        System.out.println(Thread.currentThread().getName() + " : " + "----------获取连接");
        Connection con = null;
        if (rest > 0) {
            System.out.println(Thread.currentThread().getName() + " : " + "当前剩余线程数为" + rest);
            con = cons.peek();
            cons.remove();
            rest--;
        } else if (createCount < maxSize) {
            System.out.println(Thread.currentThread().getName() + " : " + "当前剩余数为0，正在创建");
            con = poolFactory.getConnection();
            createCount++;
        } else {
            //阻塞
            try {
                //等待被唤醒，或者超时
                System.out.println("当前剩余连接为0，等待中");
                waitCount++;
                wait(maxWait);
                if (rest == 0) {
                    System.out.println(Thread.currentThread().getName() + " : " + "当前剩余连接数为 " + maxSize + " ,连接不可用");
                } else {
                    con = cons.peek();
                    cons.remove();
                    rest--;
                    System.out.println("有连接归还");
                }
                waitCount--;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return con;
    }

    /**
     * 归还连接，需要销毁state资源和resultSet资源
     * 重载实现多种可能，或者使用判断或可变参数也可
     */
    public void close(Connection con, Statement sta, ResultSet res) throws SQLException {
        System.out.println(Thread.currentThread().getName() + " : " + "----------归还连接");
        try {
            if(sta!=null)
            sta.close();
            if(res!=null)
            res.close();
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(Thread.currentThread().getName() + " : " + "连接归还失败");
        }
        if (rest >= 5) {
            con.close();
            createCount--;
        } else {
            cons.add(con);
            rest++;
        }
        System.out.println(Thread.currentThread().getName() + " : " + "连接归还成功,当前剩余线程数" + rest);
        if (waitCount > 0) {
            //随机通知一个线程
            notify();
        }
    }


    /**
     * 连接销毁
     */
    public void destroy() throws SQLException {
        while (!cons.isEmpty()) {
            cons.poll().close();
        }
        System.out.println("连接释放");
    }
    /**
     * 回收问题
     * 1、设置一个定时器，回收超出核心线程的连接
     * 2、直接在归还的时候判定即可
     */


}
