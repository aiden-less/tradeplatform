package com.converage.mapper.common;


import com.converage.entity.common.AllAddress;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressMapper {

    List<AllAddress> selectListByPid(int pid);

    AllAddress selectById(int id);
}
