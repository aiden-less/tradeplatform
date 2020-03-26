package com.converage.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.converage.entity.TradePairNews;
import com.converage.jdbc.annotation.Column;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by 旺旺 on 2020/3/26.
 */
public class JdbcModel<T> {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public int insert(Object o) throws IOException {
        StringBuffer sql = new StringBuffer("INSERT INTO table_name");
        StringBuffer columnSql = new StringBuffer("(");
        StringBuffer valueSql = new StringBuffer("(");

        ClassReader classReader = new ClassReader(TradePairNews.class.getName());
        ClassNode cn = new ClassNode();//创建ClassNode,读取的信息会封装到这个类里面
        classReader.accept(cn, 0);
        List<FieldNode> fields = cn.fields;
        for (FieldNode fieldNode : fields) {
            String fieldName = fieldNode.name;
            Object fieldValue = fieldNode.value;

            List<AnnotationNode> annotationNodes = fieldNode.visibleAnnotations;

            for(AnnotationNode annotationNode :annotationNodes){
                System.out.println(1);
            }

            System.out.println(JSONObject.toJSON(annotationNodes));
//            columnSql.append();

        }

//        jdbcTemplate.update


        return 0;
    }


    public static void main(String[] args) throws IOException {
        ClassReader classReader = new ClassReader(TradePairNews.class.getName());
        ClassNode cn = new ClassNode();//创建ClassNode,读取的信息会封装到这个类里面
        classReader.accept(cn, 0);
        List<FieldNode> fields = cn.fields;
        for (FieldNode fieldNode : fields) {
            List<AnnotationNode> annotationNodes = fieldNode.visibleAnnotations;

            for(AnnotationNode annotationNode: annotationNodes){
                System.out.println(1);
            }
            System.out.println(JSONObject.toJSON(annotationNodes));
        }
    }
}
