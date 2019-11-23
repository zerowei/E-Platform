package com.mmall.crontask;

import com.mmall.service.OrderService;
import com.mmall.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class closeOrderCronTask {

    @Autowired
    private OrderService orderService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrder() {
        int hour = Integer.parseInt(PropertiesUtil.getProperties("close.order.cron.time"));
        log.info("开始定时关闭订单");
        orderService.closeOrder(hour);
        log.info("过期订单已关闭");
    }
}
