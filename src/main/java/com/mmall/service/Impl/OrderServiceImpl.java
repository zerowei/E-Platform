package com.mmall.service.Impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.OrderService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.DateUtil;
import com.mmall.utils.FtpUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.OrderItemVO;
import com.mmall.vo.OrderProductVO;
import com.mmall.vo.OrderVO;
import com.mmall.vo.ShippingVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

@Service("OrderService")
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    PayInfoMapper payInfoMapper;

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ShippingMapper shippingMapper;

    public ReturnResponse createOrder(Integer userId, Integer shippingId) {
        List<Cart> cartList = cartMapper.getCartsCheckedByuserId(userId);
        if (cartList == null) return ReturnResponse.ReturnErrorByMessage("购物车为空");

        ReturnResponse response = this.getOrderItems(userId, cartList);
        if (!response.isSuccess()) return response;
        List<OrderItem> orderItemList = (List<OrderItem>)response.getData();

        BigDecimal totalPrice = this.getTotalPrice(orderItemList);

        Order order = this.getOrder(userId, shippingId, totalPrice);
        if (order == null) return ReturnResponse.ReturnErrorByMessage("订单生成失败");

        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        orderItemMapper.batchInsert(orderItemList);

        this.reduceStock(orderItemList);
        this.cleanCarts(cartList);

        OrderVO orderVO = this.getOrderVO(order, orderItemList);
        return ReturnResponse.ReturnSuccessByData(orderVO);
    }

    public ReturnResponse<String> cancelOrder(Integer userId, Long orderNum) {
        Order order = orderMapper.selectByUserIdOrderNum(userId, orderNum);
        if (order == null) return ReturnResponse.ReturnErrorByMessage("该用户无此订单");
        if (Const.OrderStatus.WAIT_BUYER_PAY.getCode() != order.getStatus())
            return ReturnResponse.ReturnErrorByMessage("订单已付款，无法进行取消");
        Order newOrder = new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(Const.OrderStatus.CANCEL.getCode());
        int count = orderMapper.updateByPrimaryKeySelective(newOrder);
        if (count == 0) return ReturnResponse.ReturnError();
        return ReturnResponse.ReturnSuccessByMessage("订单取消成功");
    }

    public ReturnResponse<OrderProductVO> getOrderProducts(Integer userId) {
        OrderProductVO orderProductVO = new OrderProductVO();
        List<Cart> cartList = cartMapper.getCartsCheckedByuserId(userId);
        ReturnResponse response = this.getOrderItems(userId, cartList);
        if (!response.isSuccess()) return response;

        List<OrderItem> orderItemList = (List<OrderItem>) response.getData();
        BigDecimal totalPrice = new BigDecimal("0");
        List<OrderItemVO> orderItemVOList = new ArrayList<>();

        for (OrderItem orderItem : orderItemList) {
            totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVOList.add(this.getOrderItemVO(orderItem));
        }
        orderProductVO.setProductTotalPrice(totalPrice);
        orderProductVO.setOrderItemVoList(orderItemVOList);
        orderProductVO.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix"));
        return ReturnResponse.ReturnSuccessByData(orderProductVO);
    }

    public ReturnResponse<OrderVO> getOrderDetail(Integer userId, Long orderNum) {
        Order order = orderMapper.selectByUserIdOrderNum(userId, orderNum);
        if (order == null) return ReturnResponse.ReturnErrorByMessage("该用户无此订单");
        List<OrderItem> orderItemList = orderItemMapper.getByUserIdOrderNum(userId, orderNum);
        OrderVO orderVO = this.getOrderVO(order, orderItemList);
        return ReturnResponse.ReturnSuccessByData(orderVO);
    }

    public ReturnResponse<PageInfo> orderList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList;
        if (userId == null) orderList = orderMapper.selectAll();
        else orderList = orderMapper.selectByUserId(userId);
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orderList) {
            List<OrderItem> orderItemList;
            if (userId == null) orderItemList = orderItemMapper.getByOrderNum(order.getOrderNo());
            else orderItemList = orderItemMapper.getByUserIdOrderNum(userId, order.getOrderNo());
            OrderVO orderVO = this.getOrderVO(order, orderItemList);
            orderVOList.add(orderVO);
        }
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return ReturnResponse.ReturnSuccessByData(pageInfo);
    }

    private OrderVO getOrderVO(Order order, List<OrderItem> orderItemList) {
        OrderVO orderVo = new OrderVO();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentType.getNameByCode(order.getPaymentType()).getName());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatus.getNameByCode(order.getStatus()).getName());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        orderVo.setShippingVo(this.getShippingVO(shipping));

        //虽然这里order的时间Date都是null，但是用Datetime的构造器生成的datetime是正常的现在时间，详情见test里的test3
        orderVo.setPaymentTime(DateUtil.transfer2Str(order.getPaymentTime()));
        orderVo.setSendTime(DateUtil.transfer2Str(order.getSendTime()));
        orderVo.setEndTime(DateUtil.transfer2Str(order.getEndTime()));
        orderVo.setCreateTime(DateUtil.transfer2Str(order.getCreateTime()));
        orderVo.setCloseTime(DateUtil.transfer2Str(order.getCloseTime()));

        orderVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix"));

        orderVo.setReceiverName(shipping.getReceiverName());

        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = this.getOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }
        orderVo.setOrderItemVoList(orderItemVOList);
        return orderVo;

    }

    private OrderItemVO getOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVo = new OrderItemVO();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DateUtil.transfer2Str(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private ShippingVO getShippingVO(Shipping shipping){
        ShippingVO shippingVo = new ShippingVO();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }

    private void cleanCarts(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKey(product);
        }
    }

    private Order getOrder(Integer userId, Integer shippingId, BigDecimal totalPrice) {
        Order order = new Order();
        order.setOrderNo(this.generateOrderNum());
        order.setStatus(Const.OrderStatus.WAIT_BUYER_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PaymentType.ONLINE.getCode());
        order.setPayment(totalPrice);
        order.setShippingId(shippingId);
        order.setUserId(userId);
        int count = orderMapper.insert(order);
        if (count == 0) return null;
        return order;
    }

    private long generateOrderNum() {
        long currentTime = System.currentTimeMillis();
        Random random = new Random();
        return currentTime + random.nextInt(100);
    }

    private BigDecimal getTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal res = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            res = BigDecimalUtil.add(res.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return res;
    }

    private ReturnResponse<List<OrderItem>> getOrderItems(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product.getStatus() != Const.SaleStatus.ON_SALE.getCode())
                return ReturnResponse.ReturnErrorByMessage("商品" + product.getName() + "已下架");
            if (product.getStock() < cart.getQuantity())
                return ReturnResponse.ReturnErrorByMessage("商品" + product.getName() + "库存不足");
            orderItem.setUserId(cart.getUserId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), cart.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ReturnResponse.ReturnSuccessByData(orderItemList);
    }


    public ReturnResponse payOrder(Integer userId, String path, Long orderNum) {

        Map<String, String> res = new HashMap<>();
        Order order = orderMapper.selectByUserIdOrderNum(userId, orderNum);
        if (order == null) {
            return ReturnResponse.ReturnErrorByMessage("该用户没有此订单");
        }
        res.put("orderNum", String.valueOf(order.getOrderNo()));

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "mmall扫码支付";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品共" + totalAmount + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> itemList = orderItemMapper.getByUserIdOrderNum(userId, orderNum);
        for (OrderItem orderItem : itemList) {
            GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(), BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(), (double)100).longValue(), orderItem.getQuantity());
            goodsDetailList.add(goodsDetail);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperties("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String QRPath = String.format(path + "/qr-%s.png",
                        response.getOutTradeNo());
                String QRName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, QRPath);

                File target = new File(path, QRName);
                try {
                    FtpUtil.uploadFile(new ArrayList<File>(Arrays.asList(target)));
                } catch (Exception e) {
                    log.error("上传二维码到FTP服务器失败", e);
                }
                log.info("QRCodePath:" + QRPath);
                String url = PropertiesUtil.getProperties("ftp.server.http.prefix") + target.getName();
                res.put("qrCodeUrl", url);
                return ReturnResponse.ReturnSuccess("生成二维码成功", res);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ReturnResponse.ReturnErrorByMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ReturnResponse.ReturnErrorByMessage("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ReturnResponse.ReturnErrorByMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    public ReturnResponse aliPayCallback(Map<String, String> res) {
         Long orderNum = Long.parseLong(res.get("out_trade_no"));
         String tradeNum = res.get("trade_no");
         String tradeStatus = res.get("trade_status");

         Order order = orderMapper.selectByOrderNum(orderNum);
         if (order == null) {
             return ReturnResponse.ReturnErrorByMessage("非此电商平台的订单，忽略此通知");
         }
         if (order.getStatus() >= Const.OrderStatus.PAID.getCode()) {
             return ReturnResponse.ReturnSuccessByMessage("支付宝重复通知，忽略");
         }

         if (tradeStatus.equals(Const.TradeStatus.TRADE_SUCCESS)) {
             order.setPaymentTime(DateUtil.transfer2Date(res.get("gmt_payment")));
             order.setStatus(Const.OrderStatus.PAID.getCode());
             orderMapper.updateByPrimaryKeySelective(order);
         }

         PayInfo payInfo = new PayInfo();
         payInfo.setUserId(order.getUserId());
         payInfo.setOrderNo(order.getOrderNo());
         payInfo.setPayPlatform(Const.OrderPlatform.ALIPAY.getCode());
         payInfo.setPlatformNumber(tradeNum);
         payInfo.setPlatformStatus(tradeStatus);
         payInfoMapper.insert(payInfo);
         return ReturnResponse.ReturnSuccess();
    }

    public ReturnResponse queryOrderStatus(Integer userId, Long orderNum) {
        Order order = orderMapper.selectByUserIdOrderNum(userId, orderNum);
        if (order == null) {
            return ReturnResponse.ReturnErrorByMessage("当前用户没有此订单");
        }
        if (order.getStatus() >= Const.OrderStatus.PAID.getCode()) {
            return ReturnResponse.ReturnSuccess();
        }
        return ReturnResponse.ReturnError();
    }

    //后台管理
    public ReturnResponse<OrderVO> getOrderDetailManage(Long orderNum) {
        Order order = orderMapper.selectByOrderNum(orderNum);
        if (order == null) return ReturnResponse.ReturnErrorByMessage("没有此订单");
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNum(orderNum);
        OrderVO orderVO = this.getOrderVO(order, orderItemList);
        return ReturnResponse.ReturnSuccessByData(orderVO);
    }

    public ReturnResponse<PageInfo> searchOrderManage(Long orderNum, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNum(orderNum);
        if (order == null) return ReturnResponse.ReturnErrorByMessage("没有此订单");
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNum(orderNum);
        OrderVO orderVO = this.getOrderVO(order, orderItemList);
        PageInfo pageInfo = new PageInfo(Arrays.asList(order));
        pageInfo.setList(Arrays.asList(orderVO));
        return ReturnResponse.ReturnSuccessByData(pageInfo);
    }

    public ReturnResponse<String> sendGoods(Long orderNum) {
        Order order = orderMapper.selectByOrderNum(orderNum);
        if (order == null) return ReturnResponse.ReturnErrorByMessage("没有此订单");
        if (order.getStatus() == Const.OrderStatus.PAID.getCode()) {
            order.setStatus(Const.OrderStatus.SHIPPED.getCode());
            order.setSendTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
            return ReturnResponse.ReturnSuccessByMessage("发货成功");
        }
        return ReturnResponse.ReturnErrorByMessage("订单处于不可发货状态");
    }
}
