package org.rocket.order.service;

/**
 * @author yangxin
 * 2020/12/23 16:35
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param cityId 城市Id
     * @param platformId 平台Id
     * @param userId 用户Id
     * @param supplierId 供应商Id
     * @param goodIds 商品Id
     * @return 是否创建订单成功
     */
    boolean createOrder(String cityId, String platformId, String userId, String supplierId, String goodIds);

    /**
     * 顺序地发送消息给xxx
     *
     * @param userId 用户Id
     * @param orderId 订单Id
     */
    void sendOrderlyMessage4Pkg(String userId, String orderId);
}
