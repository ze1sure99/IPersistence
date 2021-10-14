package com.ze1sure99.sqlSession;

import com.ze1sure99.pojo.Configuration;

public class DefaultSqlSessionFactory implements SqlSessionFactory{
    private Configuration configuration;
    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public SqlSession openSession() {
        //SqlSession接口的实现类
        return new DefaultSqlSession(configuration);
    }
}
