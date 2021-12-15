package com.hz.redis.cluster.rediscluster.redisson.configuration;


import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.util.Set;


/**
 * 初始化布隆过滤器
 */
@Configuration
public class InitBoolmFilterConfigration {


    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("order:id");
        bloomFilter.tryInit(100000L, 0.03);
        Set<String> keys = stringRedisTemplate.keys("*"); //这里可以改成需要匹配的字符

        for (String key : keys) {
            bloomFilter.add(key);
        }
    }

}
