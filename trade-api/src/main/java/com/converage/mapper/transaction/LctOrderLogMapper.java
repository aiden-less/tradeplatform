package com.converage.mapper.transaction;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface LctOrderLogMapper {
    int updateIfPayFlag(@Param("uid") String uid, @Param("flag") Boolean flag, @Param("whereStatus") int whereStatus);

    int updateStatus(@Param("uid") String uid, @Param("status") int status, @Param("whereStatus") int whereStatus);
}
