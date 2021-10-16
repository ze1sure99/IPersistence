package com.ze1sure99.config;



import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.ze1sure99.io.Resources;
import com.ze1sure99.pojo.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XMLConfigBuilder {
    /**
     *
     * 该方法就是使用dom4j将配置文件进行解析，封装Configration
     */
    private  Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }


    public Configuration parseConfig(InputStream inputStream) throws DocumentException, PropertyVetoException {
        //4.通过dom4j对sqlMapConfig的文件输入流进行解析
        Document document = new SAXReader().read(inputStream);
        //拿到了configration对象
        Element rootElement = document.getRootElement();
        //selectNodes是根标签
        List<Element> list = rootElement.selectNodes("//property");
        Properties properties = new Properties();
        //attributeValue是根标签里面的属性
        for (Element element: list) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name,value);
        }
        //连接池
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));
        //填充 configuration
        configuration.setDataSource(comboPooledDataSource);

        //mapper.xml解析:拿到路径--字节输入流--dom4j进行解析
        //mapper 部分
        List<Element> mapperList = rootElement.selectNodes("//mapper");
        for (Element elment: mapperList) {
            String mapperPath = elment.attributeValue("resource");
            InputStream resourceAsSteam = Resources.getResourceAsSteam(mapperPath);
            XMLMapperBuilder xMlMapperBuilder = new XMLMapperBuilder(configuration);
            xMlMapperBuilder.parse(resourceAsSteam);
        }
        //所有的xml文件解析完成 返回
        return  configuration;
    }
}
