package com.ze1sure99.sqlSession;

import com.ze1sure99.utils.ParameterMapping;

import java.util.ArrayList;
import java.util.List;

public class BoundSql {
    private  String sqlText;//解析过后的sql
    private List<ParameterMapping> parameterMappingList = new ArrayList<>();//解析出来的参数

    public BoundSql(String sqlText, List<ParameterMapping> parameterMappingList) {
        this.sqlText = sqlText;
        this.parameterMappingList = parameterMappingList;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public List<ParameterMapping> getParameterMappingList() {
        return parameterMappingList;
    }

    public void setParameterMappingList(List<ParameterMapping> parameterMappingList) {
        this.parameterMappingList = parameterMappingList;
    }
}
