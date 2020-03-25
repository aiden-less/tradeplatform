package com.converage.mapper.common;

import com.converage.service.common.GlobalConfigService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalConfigMapper {

    Integer updateValue(@Param("key") GlobalConfigService.Enum key, @Param("value") String value);

    String getByDb(@Param("key") Enum key);
}
