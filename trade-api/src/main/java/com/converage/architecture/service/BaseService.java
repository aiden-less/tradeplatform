package com.converage.architecture.service;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.mybatis.mapper.BaseMapper;
import com.converage.utils.ValueCheckUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.ss.formula.functions.T;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BaseService {
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    @Autowired
    protected BaseMapper baseMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public <T> Integer insert(T entity) {
        return baseMapper.insert(entity.getClass(), entity);
    }

    public <T> Integer insertIfNotNull(T entity) {
        return baseMapper.insertIfNotNull(entity.getClass(), entity);
    }

    public <T> Integer insertBatch(List<T> entityList, Boolean insertIfNull) {
        Integer flag;
        if (entityList == null) {
            return 0;
        }
        if (entityList.size() == 0) {
            return 0;
        }

        Class clazz = entityList.get(0).getClass();
        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        int size = 100;
        int j = 0;
        try {
            for (int i = 0; i < entityList.size(); i++) {
                if (insertIfNull) {
                    baseMapper.insert(clazz, entityList.get(i));
                } else {
                    baseMapper.insertIfNotNull(clazz, entityList.get(i));
                }
                j++;
                if (j % size == 0 || i == entityList.size() - 1) {
                    session.commit();
                    session.clearCache();
                }
            }
            flag = 1;
        } catch (Exception e) {
            logger.error(e.getMessage());
            flag = 0;
            session.rollback();
        } finally {
            session.close();
        }
        return flag;
    }

    public Integer delete(Object entity) {
        return baseMapper.delete(entity.getClass(), entity);
    }

    public Integer update(Object entity) {
        return baseMapper.update(entity.getClass(), entity);
    }

    public Integer updateIfNotNull(Object entity) {
        return baseMapper.updateIfNotNull(entity.getClass(), entity);
    }

    public Integer selectCount(Class<?> tClass) {
        try {
            return baseMapper.selectCount(tClass);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Integer selectCountByWhereMap(Map<String, Object> whereMap, Class<T> tClass) {
        try {
            return baseMapper.selectCountByWhereMap(whereMap, tClass);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public <T> T selectOneById(Serializable id, Class<T> tClass) {
        try {
            return baseMapper.selectById(null, id, tClass);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public <T> T selectiveOneById(List<String> selectiveFieldList, Serializable id, Class<T> tClass) {
        try {
            return baseMapper.selectById(selectiveFieldList, id, tClass);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public <T> T selectOneByWhereString(String whereStr, Object whereValue, Class<T> tClass) {
        try {
            Map<String, Object> whereMap = ImmutableMap.of(whereStr, whereValue);
            return baseMapper.selectOneByWhereMap(null, whereMap, tClass);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public <T> T selectiveOneByWhereString(List<String> selectiveFieldList, String whereStr, Object whereValue, Class<T> tClass) {
        try {
            Map<String, Object> whereMap = ImmutableMap.of(whereStr, whereValue);
            return baseMapper.selectOneByWhereMap(selectiveFieldList, whereMap, tClass);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public <T> T selectOneByWhereMap(Map<String, Object> whereMap, Class<T> tClass) {
        try {
            return baseMapper.selectOneByWhereMap(null, whereMap, tClass);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public <T> T selectiveOneByWhereMap(List<String> selectiveFieldList, Map<String, Object> whereMap, Class<T> tClass) {
        try {
            return baseMapper.selectOneByWhereMap(selectiveFieldList, whereMap, tClass);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public <T> List<T> selectiveAll(List<String> selectiveFieldList, Pagination pagination, Class<T> tClass) {
        try {
            T o = baseMapper.selectAll(selectiveFieldList, tClass, pagination, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectiveAll(List<String> selectiveFieldList, Pagination pagination, Class<T> tClass, Map<String, Object> orderMap) {
        try {
            T o = baseMapper.selectAll(selectiveFieldList, tClass, pagination, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectiveAll(List<String> selectiveFieldList, Class<T> tClass) {
        try {
            T o = baseMapper.selectAll(selectiveFieldList, tClass, null, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectAll(Class<T> tClass) {
        try {
            T o = baseMapper.selectAll(null, tClass, null, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectAll(Pagination pagination, Class<T> tClass) {
        try {
            T o = baseMapper.selectAll(null, tClass, pagination, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectAll(Class<T> tClass, Map<String, Object> orderMap) {
        try {
            T o = baseMapper.selectAll(null, tClass, null, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectAll(Pagination pagination, Class<T> tClass, Map<String, Object> orderMap) {
        try {
            T o = baseMapper.selectAll(null, tClass, pagination, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectiveAll(List<String> selectiveFieldList, Class<T> tClass, Map<String, Object> orderMap) {
        try {
            T o = baseMapper.selectAll(selectiveFieldList, tClass, null, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectListByWhereString(String whereStr, Object whereValue, Pagination pagination, Class<T> tClass) {
        try {
            ValueCheckUtils.notEmpty(whereValue, "参数有误");
            Map<String, Object> whereMap = ImmutableMap.of(whereStr, whereValue);
            T o = baseMapper.selectListByWhere(null, whereMap, tClass, pagination, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectListByWhereString(String whereStr, Object whereValue, Class<T> tClass) {
        try {
            ValueCheckUtils.notEmpty(whereValue, "参数有误");
            Map<String, Object> whereMap = ImmutableMap.of(whereStr, whereValue);
            T o = baseMapper.selectListByWhere(null, whereMap, tClass, null, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectiveListByWhereString(List<String> selectiveFieldList, String whereStr, Object whereValue, Pagination pagination, Class<T> tClass) {
        try {
            ValueCheckUtils.notEmpty(whereValue, "参数有误");
            Map<String, Object> whereMap = ImmutableMap.of(whereStr, whereValue);
            T o = baseMapper.selectListByWhere(selectiveFieldList, whereMap, tClass, pagination, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectListByWhereString(String whereStr, Object whereValue, Pagination pagination, Class<T> tClass, Map<String, Object> orderMap) {
        try {
            ValueCheckUtils.notEmpty(whereValue, "参数有误");
            Map<String, Object> whereMap = ImmutableMap.of(whereStr, whereValue);
            T o = baseMapper.selectListByWhere(null, whereMap, tClass, pagination, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectListByWhereString(String whereStr, Object whereValue, Class<T> tClass, Map<String, Object> orderMap) {
        try {
            ValueCheckUtils.notEmpty(whereValue, "参数有误");
            Map<String, Object> whereMap = ImmutableMap.of(whereStr, whereValue);
            T o = baseMapper.selectListByWhere(null, whereMap, tClass, null, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectiveListByWhereString(List<String> selectiveFieldList, String whereStr, Object whereValue, Pagination pagination, Class<T> tClass, Map<String, Object> orderMap) {
        try {
            ValueCheckUtils.notEmpty(whereValue, "参数有误");
            Map<String, Object> whereMap = ImmutableMap.of(whereStr, whereValue);
            T o = baseMapper.selectListByWhere(selectiveFieldList, whereMap, tClass, pagination, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectiveListByWhereString(List<String> selectiveFieldList, String whereStr, Object whereValue, Class<T> tClass) {
        try {
            ValueCheckUtils.notEmpty(whereValue, "参数有误");
            Map<String, Object> whereMap = ImmutableMap.of(whereStr, whereValue);
            T o = baseMapper.selectListByWhere(selectiveFieldList, whereMap, tClass, null, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectListByWhereMap(Map<String, Object> whereMap, Class<T> clazz) {
        try {
            T o = baseMapper.selectListByWhere(null, whereMap, clazz, null, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectListByWhereMap(Map<String, Object> whereMap, Pagination pagination, Class<T> clazz) {
        try {
            T o = baseMapper.selectListByWhere(null, whereMap, clazz, pagination, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectiveListByWhereMap(List<String> selectiveFieldList, Map<String, Object> whereMap, Pagination pagination, Class<T> clazz) {
        try {
            T o = baseMapper.selectListByWhere(selectiveFieldList, whereMap, clazz, pagination, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectListByWhereMap(Map<String, Object> whereMap, Pagination pagination, Class<T> clazz, Map<String, Object> orderMap) {
        try {
            T o = baseMapper.selectListByWhere(null, whereMap, clazz, pagination, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectListByWhereMap(Map<String, Object> whereMap, Class<T> clazz, Map<String, Object> orderMap) {
        try {
            T o = baseMapper.selectListByWhere(null, whereMap, clazz, null, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectiveListByWhereMap(List<String> selectiveFieldList, Map<String, Object> whereMap, Pagination pagination, Class<T> clazz, Map<String, Object> orderMap) {
        try {
            T o = baseMapper.selectListByWhere(selectiveFieldList, whereMap, clazz, pagination, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectiveListByWhereMap(List<String> selectiveFieldList, Map<String, Object> whereMap, Class<T> clazz, Map<String, Object> orderMap) {
        try {
            T o = baseMapper.selectListByWhere(selectiveFieldList, whereMap, clazz, null, orderMap).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public <T> List<T> selectiveListByWhereMap(List<String> selectiveFieldList, Map<String, Object> whereMap, Class<T> clazz) {
        try {
            T o = baseMapper.selectListByWhere(selectiveFieldList, whereMap, clazz, null, null).get(0);
            if (!(o instanceof List)) {
                return Arrays.asList(o);
            }
            return (List<T>) o;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

}
