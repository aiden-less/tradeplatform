package com.converage.service.common;

import com.converage.architecture.exception.BusinessException;
import com.converage.client.RedisClient;
import com.converage.constance.RedisKeyConst;
import com.converage.entity.common.AllAddress;
import com.converage.mapper.common.AddressMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 国家省份城市地区
 */
@CacheConfig(cacheNames = "addressCache")
@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private RedisClient redisClient;

    /**
     * 通过 父 ID 获取列表
     */
    public Object getByPid(String pid) {
        Object hashList = redisClient.getHashKey(RedisKeyConst.ADDRESS_LIST, pid);
        if (hashList != null) {
            return (List<AllAddress>) hashList;
        }
        return Collections.emptyList();
    }

    /**
     * 通过最后一级ID 得到 国家省市区
     */
    public String getLocation(Integer location) {
        List<AllAddress> addressList = (List<AllAddress>) getByPid(String.valueOf(location));
        if (CollectionUtils.isNotEmpty(addressList)) {
            throw new BusinessException("地址数据有误");
        }
        AllAddress address;
        String str = "";
        address = getById(location);
        str = address.getName() + str;
        while (address.getPid() != 0) {
            address = getById(address.getPid());
            if (address == null) {
                break;
            }
            str = address.getName() + " " + str;
        }
        return str;
    }

    /**
     * 通过 主键ID 查询, 第一次查询进行缓存
     */
    @Cacheable(key = "#id")
    public AllAddress getById(Integer id) {
        return addressMapper.selectById(id);
    }

    /**
     * 清除所有缓存
     */
    @CacheEvict(allEntries = true)
    public void evict() {
    }


    /**
     * 缓存 至 redis
     * @param pid
     */
    public void cache(int pid) {
        List<AllAddress> addressList = addressMapper.selectListByPid(pid);
        if(CollectionUtils.isEmpty(addressList)) {
            return;
        }
        redisClient.put(RedisKeyConst.ADDRESS_LIST, String.valueOf(pid), addressList);
        for (AllAddress address : addressList) {
            cache(address.getId());
        }
    }

}
