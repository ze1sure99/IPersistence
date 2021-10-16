package com.ze1sure99.pojo;


//一个mappedStatement对应一个select结果集
public class MappedStatement {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParamterType() {
        return paramterType;
    }

    public void setParamterType(String paramterType) {
        this.paramterType = paramterType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    // id标识
    private String id;
    // 返回值类型
    private String resultType;
    // 参数值类型
    private String paramterType;
    // sql语句
    private String sql;
}
