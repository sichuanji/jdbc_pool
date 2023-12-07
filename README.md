# jdbc_pool

集合jdbc手动构造的一个数据库连接池，实现了连接池的基本管理，主要包含新建连接，连接获取，连接的销毁，实现了池化技术的核心参数，核心线程+最大线程+等待时常

## 项目结构

```

----bin                                 jdbc的jar 包

----db                                  一个测试表

----main  

    ----java 
  
        ----org.example                 测试用例
    
             ---- org.example.pool.util 数据库连接池
  
    ----resources                       配置文件的位置
  
----test

```
## 测试效果
### 压力测试
![image](https://github.com/sichuanji/jdbc_pool/assets/52628782/886a5a4f-dafe-47f5-8437-2af477a050ff)

### 功能测试
![Uploading image.png…]()
