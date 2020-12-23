package org.rocket.order.constants;

import lombok.Getter;

/**
 * @author yangxin
 * 12/23/20 4:59 PM
 */
@Getter
public enum OrderStatusEnum {

    ORDER_CREATED("1"),
    ORDER_PAYED("2"),
    ORDER_FAIL("3");

    private final String value;

    OrderStatusEnum(String value) {
        this.value = value;
    }
}
