package org.rocket.pay.b.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yangxin
 * 1/24/21 3:45 PM
 */
@Data
public class PlatformAccount implements Serializable {

    private static final long serialVersionUID = 4292853844873558910L;

    private String accountId;

    private String accountNo;

    private Date dateTime;

    private BigDecimal currentBalance;

    private Integer version;

    private Date createTime;

    private Date updateTime;
}
