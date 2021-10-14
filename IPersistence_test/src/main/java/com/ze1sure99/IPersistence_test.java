package com.ze1sure99;

import com.ze1sure99.io.Resources;
import com.ze1sure99.pojo.User;
import com.ze1sure99.sqlSession.SqlSession;
import com.ze1sure99.sqlSession.SqlSessionFactory;
import com.ze1sure99.sqlSession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class IPersistence_test {
    @Test
    public void test() throws DocumentException, PropertyVetoException, SQLException, IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, InvocationTargetException, ClassNotFoundException {
        /**
         * 1.调用ResourceAsSteam方法获取sqlMapConfig.xml的流文件存储在内存中
         * 2.调用SqlSessionFactoryBuild 方法来解析这个流文件
         * 3.通过dom4j document.getRootElement()来获取sqlMapperconfig的property标签对象
         * 4.读sqlMapConfig.xml的跟标签并加入到Element里
         * 5.用java的utils包里的properties对象存储读取的sqlMapConfig的值
         * 6.用c3p0数据库连接池（datasource）放置properties里的数据内容,再封装到configuration类里面
         * 7.用document.getRootElement()来获取mapper标签里的映射文件的路径
         * 8.创建一个XMLMapperBuilder解析userMapper，并把configuration这个类传到resouce里面
         * 9.把解析userMapper的内容再放到mappedStatement里面
         * 10.再把映射配置类封装到Configuration里面，至此Configuration里面既有sqlMapConfig里面的datasource又有映射文件的mappedStatement
         */
        //1.调用Resources类的getResoureceAsSteam方法，使用反射技术得到sqlMapconfig.xml的字节流并保存到内存中
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        //2.把内存中存的文件输入流通过sqlSessionFactoryBuilder的build方法传递过去
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        //拿到一个生产出来的sqlSession对象
        SqlSession sqlSession  = sqlSessionFactory.openSession();

        //调用
        User user = new User();
        user.setId(1);
        user.setUsername("lucy");
//        User user2 = sqlSession.selectOne("user.selectOne", user);
//        System.out.println(user2);
        List<User> users = sqlSession.selectList("user.selectList");
        for (User user1: users) {
            System.out.println(user1);
        }
    }

}
