package com.converage.architecture.mybatis.mapper;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.EntityLackTableAnnotationException;
import com.converage.architecture.mybatis.MybatisMapperParam;
import com.converage.architecture.mybatis.MybatisBuildUtil;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.utils.UUIDUtils;
import com.converage.utils.ValueCheckUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BaseMapper {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    public Integer insert(Class clazz, Object object) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            String tableName = MybatisBuildUtil.getTableName(clazz);
            List<String> dbColumnList = new ArrayList<>(20);
            List<Object> voAttrList = new ArrayList<>(20);
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                Column column = f.getAnnotation(Column.class);
                Id id = f.getAnnotation(Id.class);
                if (id != null) {
                    f.setAccessible(true);
                    f.set(object, UUIDUtils.createUUID());
                }
                if (column != null) {
                    dbColumnList.add(column.name());
                    f.setAccessible(true);
                    voAttrList.add(f.get(object));
                }
            }

            MybatisMapperParam mybatisMapperParam = new MybatisMapperParam();
            mybatisMapperParam.setTableName(tableName);
            mybatisMapperParam.setDbColumnList(dbColumnList);
            mybatisMapperParam.setVoAttrList(voAttrList);

            return sqlSession.insert("baseMapper.insertOne", mybatisMapperParam);
        } catch (EntityLackTableAnnotationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer insertIfNotNull(Class clazz, Object object) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            String tableName = MybatisBuildUtil.getTableName(clazz);
            List<String> dbColumnList = new ArrayList<>(20);
            List<Object> voAttrList = new ArrayList<>(20);
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                Column column = f.getAnnotation(Column.class);
                Id id = f.getAnnotation(Id.class);
                if (id != null) {
                    f.setAccessible(true);
                    f.set(object, UUIDUtils.createUUID());
                }
                if (column != null) {
                    f.setAccessible(true);
                    Object attrVal = fields[i].get(object);
                    if (attrVal != null) {
                        dbColumnList.add(column.name());
                        voAttrList.add(f.get(object));
                    }
                }
            }

            MybatisMapperParam mybatisMapperParam = new MybatisMapperParam();
            mybatisMapperParam.setTableName(tableName);
            mybatisMapperParam.setDbColumnList(dbColumnList);
            mybatisMapperParam.setVoAttrList(voAttrList);


            return sqlSession.insert("baseMapper.insertOne", mybatisMapperParam);
        } catch (EntityLackTableAnnotationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer delete(Class clazz, Object object) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            String tableName = MybatisBuildUtil.getTableName(clazz);
            Field[] fields = object.getClass().getDeclaredFields();
            Object idPoVal = null;
            String idDbColumn = "";
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                Id id_annotation = f.getAnnotation(Id.class);
                if (id_annotation != null) {
                    f.setAccessible(true);
                    Column column_annotation = f.getAnnotation(Column.class);
                    idDbColumn = column_annotation.name();
                    idPoVal = f.get(object);
                }
            }

            ValueCheckUtils.notEmpty(idPoVal, "Entity id value can't be null");

            MybatisMapperParam mybatisMapperParam = new MybatisMapperParam();
            mybatisMapperParam.setTableName(tableName);
            mybatisMapperParam.setIdDbColumn(idDbColumn);
            mybatisMapperParam.setIdPoVal(idPoVal);

            return sqlSession.delete("baseMapper.delete", mybatisMapperParam);
        } catch (EntityLackTableAnnotationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public Integer update(Class clazz, Object object) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            String tableName = MybatisBuildUtil.getTableName(clazz);
            Map<String, Object> setMap = new HashMap<>();
            String idDbColumn = "";
            Object idPoVal = null;
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                Column column = f.getAnnotation(Column.class);
                Id id_annotation = f.getAnnotation(Id.class);
                if (id_annotation != null) {
                    idDbColumn = column.name();
                    idPoVal = f.get(object);
                }
                if (column != null && id_annotation == null) {
                    setMap.put(column.name(), f.get(object));
                }

            }

            ValueCheckUtils.notEmpty(idPoVal, "Entity value can't be null");

            MybatisMapperParam mybatisMapperParam = new MybatisMapperParam();
            mybatisMapperParam.setTableName(tableName);
            mybatisMapperParam.setUpdateMap(setMap);
            mybatisMapperParam.setIdDbColumn(idDbColumn);
            mybatisMapperParam.setIdPoVal(idPoVal);

            return sqlSession.update("baseMapper.update", mybatisMapperParam);

        } catch (EntityLackTableAnnotationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer updateIfNotNull(Class clazz, Object object) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            String tableName = MybatisBuildUtil.getTableName(clazz);
            Map<String, Object> setMap = new HashMap<>();
            String idDbColumn = "";
            Object idPoVal = null;
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                Column column = f.getAnnotation(Column.class);
                Id id_annotation = f.getAnnotation(Id.class);
                if (id_annotation != null) {
                    idDbColumn = column.name();
                    idPoVal = f.get(object);
                }
                if (column != null && id_annotation == null) {
                    Object attrVal = f.get(object);
                    if (attrVal != null) {
                        setMap.put(column.name(), f.get(object));
                    }
                }
            }

            ValueCheckUtils.notEmpty(idPoVal, "Entity id value can't be null");

            MybatisMapperParam mybatisMapperParam = new MybatisMapperParam();
            mybatisMapperParam.setTableName(tableName);
            mybatisMapperParam.setUpdateMap(setMap);
            mybatisMapperParam.setIdDbColumn(idDbColumn);
            mybatisMapperParam.setIdPoVal(idPoVal);

            return sqlSession.update("baseMapper.update", mybatisMapperParam);

        } catch (EntityLackTableAnnotationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer selectCount(Class<?> clazz) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            MybatisMapperParam mybatisMapperParam = MybatisBuildUtil.buildMapperParam4Select(clazz);
            return sqlSession.selectOne("baseMapper.selectCountByWhere", mybatisMapperParam);
        } catch (EntityLackTableAnnotationException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer selectCountByWhereMap(Map<String, Object> whereMap, Class<T> clazz) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            MybatisMapperParam mybatisMapperParam = MybatisBuildUtil.buildMapperParam4Select(whereMap, clazz);
            return sqlSession.selectOne("baseMapper.selectCountByWhere", mybatisMapperParam);
        } catch (EntityLackTableAnnotationException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T selectById(List<String> selectiveFieldList, Serializable id, Class<T> clazz) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            MybatisMapperParam mybatisMapperParam = MybatisBuildUtil.buildMapperParam4SelectById(selectiveFieldList, id, clazz);
            if (selectiveFieldList != null) {
                mybatisMapperParam.setSelectiveFieldList(selectiveFieldList);
            }
            return sqlSession.selectOne("baseMapper.selectById", mybatisMapperParam);
        } catch (EntityLackTableAnnotationException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T selectOneByWhereMap(List<String> selectiveFieldList, Map<String, Object> whereMap, Class<T> clazz) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            MybatisMapperParam mybatisMapperParam = MybatisBuildUtil.buildMapperParam4SelectOne(selectiveFieldList, whereMap, clazz);
            if (selectiveFieldList != null) {
                mybatisMapperParam.setSelectiveFieldList(selectiveFieldList);
            }
            return sqlSession.selectOne("baseMapper.selectByWhere", mybatisMapperParam);
        } catch (EntityLackTableAnnotationException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> List<T> selectAll(List<String> selectiveFieldList, Class<T> clazz, Pagination pagination, Map<String, Object> orderMap) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            MybatisMapperParam mybatisMapperParam = MybatisBuildUtil.buildMapperParam4SelectAll(selectiveFieldList, pagination, clazz, orderMap);
            if (selectiveFieldList != null) {
                mybatisMapperParam.setSelectiveFieldList(selectiveFieldList);
            }
            return sqlSession.selectList("baseMapper.selectByWhere", mybatisMapperParam);
        } catch (EntityLackTableAnnotationException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> List<T> selectListByWhere(List<String> selectiveFieldList, Map<String, Object> whereMap, Class<T> clazz, Pagination pagination, Map<String, Object> orderMap) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            MybatisMapperParam mybatisMapperParam = MybatisBuildUtil.buildMapperParam4SelectListByWhere(selectiveFieldList, whereMap, pagination, clazz, orderMap);
            if (selectiveFieldList != null) {
                mybatisMapperParam.setSelectiveFieldList(selectiveFieldList);
            }
            return sqlSession.selectList("baseMapper.selectByWhere", mybatisMapperParam);
        } catch (EntityLackTableAnnotationException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
