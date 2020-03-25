package com.converage.architecture.mybatis;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.entity.user.User;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MybatisMapperParam {
    //data table name
    private String tableName;

    //data table primary key name
    private String idDbColumn;

    //data table primary key value
    private Object idPoVal;

    //where sql map --
    private Map<String, Object> whereMap;

    //where sql map --
    private Map<String, Object> orderMap;

    //update data table column name and value
    private Map<String, Object> updateMap;

    //data table column name
    private List<String> dbColumnList;

    //data table column value
    private List<Object> voAttrList;

    //selective fieldList
    private List<String> selectiveFieldList;

    //pagination
    private Pagination pagination;

    //return data class
    private Class clazz;

    //sql select field transfer to po field
    public void transferFieldList() {
        Field[] poFields = this.clazz.getDeclaredFields();
        String[] sqlFiledArr = (String[]) this.selectiveFieldList.toArray();
        for (int i = 0; i < sqlFiledArr.length; i++) {
            for (int j = 0; j < poFields.length; j++) {
                Column poFieldAnnotation = poFields[j].getAnnotation(Column.class);
                if (poFieldAnnotation == null) continue;
                String poFieldStr = poFieldAnnotation.name();
                if (sqlFiledArr[i].equals(poFieldStr)) {
                    sqlFiledArr[i] = poFields[j].getName();
                }
            }
        }
        this.selectiveFieldList = Arrays.asList(sqlFiledArr);
    }

    public static void main(String[] args) throws NoSuchFieldException {
        List<String> selectField = Arrays.asList(User.Id, User.Phone_number);
        MybatisMapperParam mybatisMapperParam = new MybatisMapperParam();
        mybatisMapperParam.transferFieldList();
        System.out.println(mybatisMapperParam.getSelectiveFieldList());
    }
}
