package com.converage.mapper.sys;

import com.converage.architecture.constance.MybatisConst;
import com.converage.architecture.dto.Pagination;
import com.converage.entity.sys.Subscriber;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubscriberMapper {

    List<Subscriber> selectList(@Param(MybatisConst.PAGINATION) Pagination pagination, @Param("subscriber") Subscriber subscriber);

    Subscriber selectSubscriberByLogin(String userName);

    Subscriber listRole(@Param("subscriberId") String subscriberId);

    Integer deleteRoles(@Param("subscriberId") String subscriberId);
}
