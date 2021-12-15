package com.hz.redis.cluster.rediscluster.controller;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {


    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    // 0------------------------------------------------------------------------------------------------------------

    /**
     * 设置 redis 随机过期时间可以解决 缓存击穿问题
     * @param value
     * @return
     */
    @RequestMapping("/set")
    @ResponseBody
    public String setValue(String value){
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        int randomTime = random.nextInt(30000)+30000;

        //随机放入超时时间为31秒到60秒的数据
        redisTemplate.opsForValue().set("test", "12", randomTime, TimeUnit.SECONDS);
        return sb.toString();
    }

    // 0------------------------------------------------------------------------------------------------------------




    // 0------------------------------------------------------------------------------------------------------------

    /**
     * 缓存穿透解决办法
     *
     * 1、缓存空对象，数据库查询出来如果没有，放入空对象到缓存中
     * 2、使用布隆过滤器,需要引入redission
     */

    /**
     * 模拟缓存击穿
     * @param id
     * @return
     */
    @RequestMapping("/getOrder")
    @ResponseBody
    public String getOrder(String id){
        String order = redisTemplate.opsForValue().get(id);

        if(order == null){
            //跳转到主页或者查询数据库
            order = "";
            redisTemplate.opsForValue().set(id, order,1000, TimeUnit.SECONDS);
        }
        return order;
    }


    /**
     * 使用布隆过滤器
     * @param id
     * @return
     */
    @RequestMapping("/getOrderByBloom")
    @ResponseBody
    public String getOrderByBloom(String id){
        //从redisson中查看key是否存在
        String order ="";
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("order:id");
        if(bloomFilter.contains(id)){
            order= redisTemplate.opsForValue().get(id);
        }

        //不存在，查询数据库 并存放到布隆过滤器中
        return order;
    }

    // 0------------------------------------------------------------------------------------------------------------

}
