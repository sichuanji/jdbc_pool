package org.example;

import org.example.poolUtil.PoolUtil;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        PoolUtil poolUtil = PoolUtil.getInstance();
//        /**
//         * 测试并发
//         */
//        MyTask1 r = new MyTask1(poolUtil);
//        for (int i = 0; i < 3; i++) {
//            Thread.sleep(10);
//            new Thread(r).start();
//        }
        /**
         * 测试功能是否实现
         */
        MyTask2 task2 = new MyTask2(poolUtil);
        task2.run();
    }
}