package com.hz.redis.cluster.rediscluster.controller.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;
import java.util.logging.Logger;


@Controller
public class LockController {


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    public static final Logger logger = Logger.getLogger("LockController");

    @RequestMapping("/openView")
    public String OpenView(String userId,String billno){
        //查看redis中是否有该单据编号的持有者

        //如果有提示 正在编辑中
        return "";
    }


    /**
     * 获取关注我的人，比如我是一个大V
     */
    @RequestMapping("/getFollowByMe")
    @ResponseBody
    public String getFollowByMe(String id){
        Thread thread = Thread.currentThread();
        logger.info(thread.getName()+":进入到查看关注列表");
        //查询缓存
        Set<String> members = stringRedisTemplate.opsForSet().members("follow:" + id);
        if(members.size()==0){
            RLock fairLock = redissonClient.getFairLock("c-f-ml:" + id);
            //创建公平锁
            fairLock.lock();
            logger.info(thread.getName()+"竞争到锁");
            //再次查询缓存中是否有
            Set<String> members1 = stringRedisTemplate.opsForSet().members("follow:" + id);
            //模拟从数据库查询数据
            if(members1.size()==0){
                //存放到缓存中
                logger.info(thread.getName()+"查询数据库，放入缓存");
                stringRedisTemplate.opsForSet().add("follow:" + id,"huazhen");
                stringRedisTemplate.opsForSet().add("follow:" + id,"weiqiyan");
                stringRedisTemplate.opsForSet().add("follow:" + id,"zhangsan");
                stringRedisTemplate.opsForSet().add("follow:" + id,"lisi");
            }else{
                logger.info(thread.getName()+":查看缓存中是否有：没有，从新开始");
                getFollowByMe(id);
            }
            //解锁
            fairLock.unlock();
        }

        return getFollowByMe(id);
    }
}
