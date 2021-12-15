package com.hz.redis.cluster.rediscluster.redisson.configuration;

import org.apache.catalina.core.ApplicationContext;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;


@Configuration
public class RedissonConfiguration {

    @Value("${spring.redis.cluster.nodes}")
    private String redisCluster;

    @Value("${spring.redis.password}")
    private String password;


    @Bean
    public RedissonClient getClient(){
        Config config = new Config();




        ClusterServersConfig clusterServersConfig = config.useClusterServers();
        String[] split = redisCluster.split(",");

        List<String> clusterAddresss = Arrays.asList(split);

        String [] address = new String[clusterAddresss.size()];

        for (int i =0;i<clusterAddresss.size();i++){
            address[i] = "redis://"+clusterAddresss.get(i);
        }

        clusterServersConfig.addNodeAddress(address);
        clusterServersConfig.setPassword(password);
        return Redisson.create(config);
    }



}
