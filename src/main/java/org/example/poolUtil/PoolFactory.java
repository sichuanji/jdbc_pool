package org.example.poolUtil;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.sql.DriverManager;

public class PoolFactory {
    //该类负责生产数据量连接线程
    private static String name;
    private static String passwd;
    private static String url;
    private static String driverClassName;

    /**
     * read config file
     * register dirver
     */
    static{
        //获取配置文件的流对象 反射来获取 这段代码是使用Java的类加载器（ClassLoader）来获取一个输入流（InputStream），该输入流用于读取位于类路径下的 "config.properties" 文件。
        InputStream in = PoolFactory.class.getClassLoader().getResourceAsStream("config.properties");
        System.out.println(in);
        //定义一个配置文件类来加载读取
        Properties properties = new Properties();
        try {
            properties.load(in);
            name = properties.getProperty("name");
            passwd = properties.getProperty("passwd");
            url = properties.getProperty("url");
            driverClassName = properties.getProperty("driverClassName");
            // 注册驱动
            Class.forName(driverClassName);

        } catch (Exception e) {
            System.out.println("加载 inputstream出现错误");
        }

    }
    /**
     * 创建连接并返回
     */
    public Connection getConnection(){
        // 获取连接
        try {
            return DriverManager.getConnection(url,name,passwd);
        }catch (Exception e){
            System.out.println(e);
            System.out.println("获取数据库新建连接失败");
        }
        return null;
    }

}
