package com.converage.architecture.utils;

import com.converage.architecture.mybatis.annotation.Column;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JDBCResultSetUtils {
    private JDBCResultSetUtils() {
    }

//    public static <T> List<T> getBeans(ResultSet resultSet, Class<T> className) {
//        List<T> list = new ArrayList<T>();
//        Field fields[] = className.getDeclaredFields();
//        try {
//            resultSet.first();
//            do {
//                T instance = className.newInstance();
//                for (Field field : fields) {
//                    if(field.getAnnotation(Column.class)==null){
//                        continue;
//                    }
//                    Object result = resultSet.getObject(field.getName());
//                    boolean flag = field.isAccessible();
//                    field.setAccessible(true);
//                    field.set(instance, result);
//                    field.setAccessible(flag);
//                }
//                list.add(instance);
//            }while (resultSet.next());
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }

    public static <T> T getBean(ResultSet resultSet, List<String> selectiveFileList, Boolean isSelective, Class<T> className) {
        T instance = null;
        try {
            instance = className.newInstance();
            Field fields[] = className.getDeclaredFields();
            for (Field field : fields) {
                Column fieldAnnotation = field.getAnnotation(Column.class);
                if (fieldAnnotation == null) {
                    continue;
                }
                if (isSelective) {
                    if (!selectiveFileList.contains(fieldAnnotation.name())) {
                        continue;
                    }
                }
                Object result = resultSet.getObject(field.getName());
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                field.set(instance, result);
                field.setAccessible(flag);
            }
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }

        return instance;
    }

//    public static <T> List<T>  result2List(ResultSet resultSet, Class<T> className) throws SQLException, IllegalAccessException, InstantiationException {
//        List<T> list = new ArrayList<T>();
//        Field fields[] = className.getDeclaredFields();
//        while (resultSet.next()){
//            T instance = className.newInstance();
//            for (Field field : fields) {
//                if(field.getAnnotation(Column.class)==null){
//                    continue;
//                }
//                Object result = resultSet.getObject(field.getName());
//                boolean flag = field.isAccessible();
//                field.setAccessible(true);
//                field.set(instance, result);
//                field.setAccessible(flag);
//            }
//            list.add(instance);
//        }
//        return list;
//    }


    public static String switchDataType(String dataType) {
        switch (dataType) {
            case "java.lang.String":
                return " varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '' ";

            case "java.lang.Integer":
                return " int(10) DEFAULT '0'";

            case "java.math.BigDecimal":
                return " decimal(32,8) DEFAULT '0.00000000'";

            case "java.lang.Long":
                return " bigint(20) DEFAULT '0'";

            case "java.lang.Double":
                return " double(12,6) DEFAULT '0.000000'";

            case "java.lang.Boolean":
                return " tinyint(20) DEFAULT '0'";

            case "java.sql.Timestamp":
                return " timestamp NULL DEFAULT CURRENT_TIMESTAMP";
        }
        return null;
    }
}
