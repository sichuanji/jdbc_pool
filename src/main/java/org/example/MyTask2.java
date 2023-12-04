package org.example;


import org.example.poolUtil.PoolUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyTask2 extends Thread {
    PoolUtil poolUtil = null;

    public MyTask2(PoolUtil poolUtil) {
        this.poolUtil = poolUtil;
    }

    //获得连接，并执行数据库操作
    @Override
    public void run() {
        Connection con = null;
        con = poolUtil.getConnection();

        //注册和获取连接已经写好了
        // 1、定义statement 和 结果集函数
        String select = "SELECT * FROM user";
        String insert = "INSERT INTO user VALUES (\"shini\",\"123456\")";

        PreparedStatement pst = null;
        ResultSet rs = null;

        /**
         * 查询插入之间的结果
         */
        try {
            pst = con.prepareStatement(select);
            rs = pst.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setName(rs.getString("name"));
                user.setPwd(rs.getString("passwd"));
                //用户信息添加到集合
                System.out.println(user.toString());
            }

        }catch (Exception e) {
            System.out.println("查询失败");
        }
        /**
         * 插入数据
         */
        try {
            pst = con.prepareStatement(insert);
            if(pst.execute()){
                System.out.println("insert成功");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        /**
         * 插入之后的结果
         */
        System.out.println("插入之后的结果");
        try {
            pst = con.prepareStatement(select);
            rs = pst.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setName(rs.getString("name"));
                user.setPwd(rs.getString("passwd"));
                System.out.println(user.toString());
            }

        }catch (Exception e) {
            System.out.println("查询失败");
        }
        //5.释放资源 抛出来了错误
        try {
            poolUtil.close(con, pst, rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
