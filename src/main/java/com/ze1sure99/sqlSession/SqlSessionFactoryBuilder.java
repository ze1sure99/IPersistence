package com.ze1sure99.sqlSession;

import com.ze1sure99.config.XMLConfigBuilder;
import com.ze1sure99.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public  SqlSessionFactory build(InputStream in) throws DocumentException, PropertyVetoException {
        //第一：使用dom4j解析配置文件，将解析出来的内容封装到Configuration中
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        //3.对传递过来的sqlMapConfig.xml字节输入流进行解析
        Configuration configuration = xmlConfigBuilder.parseConfig(in);
        // 第二：创建sqlSessionFactory对象:这是一个工厂类：主要的作用就是用来生产sqlSession的,sqlSession他是会话对象，与数据库交互的增删改查方法
        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        return  defaultSqlSessionFactory;
    }
}
