package com.converage.mapper.user;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFreeCountMapper {

    Integer decreaseUserFreeCount(@Param("userId") String userId, @Param("field") String field, @Param("limitVal") Integer limitVal);

    Integer increaseUserFreeCount(@Param("userId") String userId, @Param("field") String field, @Param("limitVal") Integer limitVal);

    Integer initMaxCount(@Param("fields") List<String> fields, @Param("maxCount") Integer maxCount);

    Integer getCount(@Param("userId") String userId, @Param("field") String continuity_sign_count);

    Integer setUserFreeCount(@Param("userId") String userId, @Param("field") String continuity_sign_count, @Param("number") Integer i);
}
