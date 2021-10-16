package com.ze1sure99.sqlSession;

import com.ze1sure99.pojo.Configuration;
import com.ze1sure99.pojo.MappedStatement;
import com.ze1sure99.utils.GenericTokenParser;
import com.ze1sure99.utils.ParameterMapping;
import com.ze1sure99.utils.ParameterMappingTokenHandler;
import com.ze1sure99.utils.TokenHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class simpleExecutor implements Executor{
    @Override                                                                                      //参数：user
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IntrospectionException, InstantiationException, InvocationTargetException {
        //1.注册驱动，获取链接
        Connection connection = configuration.getDataSource().getConnection();
        //2.获取sql语句:select * from user where id = #{id} and username = #{username}
           //转换sql语句 select * from user where id=? and username=?,转换的过程中，还需要对#{}里面的值进行解析存储
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        //3.获取预编译preparedStatement对象
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());
        //4.设置参数(先拿到参数集合)
            //获取到参数的全路径
        String paramterType = mappedStatement.getParamterType();
        //获取传⼊参数类型
        Class<?>  paramtertypeClass = getClassType(paramterType);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            //content即为 #{}里面的值
            String content = parameterMapping.getContent();
            //反射 获取属性对象
            Field declaredField = paramtertypeClass.getDeclaredField(content);
            //暴力访问
            declaredField.setAccessible(true);
            //params[0]拿到的就是user 参数的值
            Object o = declaredField.get(params[0]);
            //给占位符赋值
            preparedStatement.setObject(i+1,o);
        }
        //5.执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        // 拿到返回结果的全路径
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);
        ArrayList<Object> objects = new ArrayList<>();
        //6.封装返回结果集
        while (resultSet.next()){
            Object o = resultTypeClass.newInstance();
           //元数据  取元数据是因为元数据中包含数据库中查询结果的字段名
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <=metaData.getColumnCount() ; i++) {
                //属性名
                String columnName = metaData.getColumnName(i);
                //属性值
                Object value = resultSet.getObject(columnName);
                 //创建属性描述器，为属性⽣成读写⽅法
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                //获取写⽅法
                Method writeMethod = propertyDescriptor.getWriteMethod();
                //向类中写⼊值
                writeMethod.invoke(o,value);
            }
           objects.add(o);
        }
        return (List<E>) objects;
    }

    private Class<?> getClassType(String paramterType) throws ClassNotFoundException {
        if(paramterType != null){
            Class<?> aClass = Class.forName(paramterType);
            return  aClass;
        }
        return null;
    }

    /**
     * 完成对#{}的解析工作：1.将#{}使用？进行代替2.解析出#{}里面的值进行存储
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
       // 标记处理类：主要是配合通⽤标记解析器GenericTokenParser类完成对配置⽂件等的解 析⼯作，其中TokenHandler主要完成处理
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        //GenericTokenParser :通⽤的标记解析器，完成了代码⽚段中的占位符的解析，然后再根 据给定的标记处理器(TokenHandler)来进⾏表达式的处理
       //三个参数：分别为openToken (开始标记)、closeToken (结束标记)、handler (标记处 理器)
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
       /** 该方法主要实现了配置文件、脚本等片段中占位符的解析、处理工作，并返回最终需要的数据。
        其中，解析工作由该方法完成，处理工作是由处理器handler的handleToken()方法来实现
        **/
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        BoundSql boundSql = new BoundSql(parseSql,parameterMappings);
        return  boundSql;

    }
}
