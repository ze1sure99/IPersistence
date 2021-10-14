package com.ze1sure99.sqlSession;

import com.ze1sure99.pojo.Configuration;
import com.ze1sure99.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementid, Object... params) throws SQLException, IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, InvocationTargetException, ClassNotFoundException {
        //将要去完成对对simpleExecutor里的query方法进行调用
        simpleExecutor simpleExecutor = new simpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        List<Object> list = simpleExecutor.query(configuration, mappedStatement, params);
        return (List<E>) list;
    }

    @Override
    public <T> T selectOne(String statementid, Object... params) throws SQLException, IllegalAccessException, IntrospectionException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchFieldException {
        List<Object> objects = selectList(statementid, params);
        if(objects.size()==1){
            return (T) objects.get(0);
        }else {
            throw new RuntimeException("查询结果为空或者查询结果过多");
        }

    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        //使用jdk动态代理来为Dao接口生成代理对象，并返回
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //无论如何封装，底层都还是实现jdbc代码 // 根据不同情况，来调用selectList或者selectOne
                //准备参数 1:statementid:sql 语句的唯一标识 namespace.id= 接口全限定名.方法名
                //拿到的是方法名：findAll
                String methodName = method.getName();
                //该类的全限定名
                String className = method.getDeclaringClass().getName();
                //拼接statementID
                String statementId = className+"."+methodName;
                //准备参数 2 :params 传递参数 比如说传递过来一个findByCondition(user),会被args接收到
                //获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();
                //判断是否进行了范性类型参数化 如果有泛型我就认为你是个集合,如果没有泛型,那就是个实体
                if(genericReturnType instanceof ParameterizedType){
                    //调用最底层的jdbc代码进行操作
                    List<Object> objects = selectList(statementId, args);
                    return objects;
                }
                return selectOne(statementId,args);
            }
        });
        return (T) proxyInstance;
    }
}
