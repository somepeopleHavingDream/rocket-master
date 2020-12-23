package org.rocket.order.service;

/**
 * @author yangxin
 * 2020/12/23 16:35
 */
public interface OrderService {

    boolean createOrder(String cityId, String platformId, String userId, String supplierId, String goodIds);
}
