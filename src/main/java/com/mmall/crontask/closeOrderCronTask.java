package com.mmall.crontask;

import com.mmall.common.Const;
import com.mmall.common.RedissonManager;
import com.mmall.service.OrderService;
import com.mmall.utils.PropertiesUtil;
import com.mmall.utils.RedisShardedUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class closeOrderCronTask {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedissonManager redissonManager;

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderWithLockPurely() {
        long lockExpireTime = Long.parseLong(PropertiesUtil.getProperties("lock.expire.time"));
        log.info("开始定时关闭订单");
        Long result = RedisShardedUtil.setnx(Const.CloseOrder.LOCK_KEY, String.valueOf(System.currentTimeMillis() + lockExpireTime));
        if (result != null && result.intValue() == 1) {
            closeOrder(Const.CloseOrder.LOCK_KEY);
        } else {
            String expireTime = RedisShardedUtil.get(Const.CloseOrder.LOCK_KEY);
            if (expireTime != null && System.currentTimeMillis() > Long.parseLong(expireTime)) {
                String expireTimeChangeble = RedisShardedUtil.getset(Const.CloseOrder.LOCK_KEY, String.valueOf(System.currentTimeMillis() + lockExpireTime));
                if (expireTimeChangeble == null || expireTime.equals(expireTimeChangeble)) closeOrder(Const.CloseOrder.LOCK_KEY);
                else log.info("无法获取锁，竞争失败");
            } else
                log.info("无法重置锁,因为当前锁仍未过期");
        }
        log.info("过期订单关闭任务已结束");
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderWithLockRedisson() {
        RLock lock = redissonManager.getRedisson().getLock(Const.CloseOrder.LOCK_KEY);
        boolean flag = false;
        try {
            flag = lock.tryLock(0, 5, TimeUnit.SECONDS);
            if (flag) {
                log.info("Redisson获取分布式锁成功");
                int hour = Integer.parseInt(PropertiesUtil.getProperties("close.order.cron.time"));
                orderService.closeOrder(hour);
            } else
                log.info("Redisson获取分布式锁失败");

        } catch (InterruptedException e) {
            log.info("Redisson获取分布式锁出现异常", e);
        } finally {
            if (flag) {
                lock.unlock();
                log.info("Redisson释放分布式锁");
            }
        }
    }

    private void closeOrder(String lockKey) {
        RedisShardedUtil.expire(lockKey, 5000);
        log.info("设置{}的过期时间", lockKey);
        int hour = Integer.parseInt(PropertiesUtil.getProperties("close.order.cron.time"));
        orderService.closeOrder(hour);
        RedisShardedUtil.del(lockKey);
        log.info("关闭订单执行完毕,删除{}对应的锁", lockKey);
    }
}
