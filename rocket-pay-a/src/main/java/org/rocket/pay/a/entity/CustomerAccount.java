package org.rocket.pay.a.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yangxin
 * 1/24/21 3:38 PM
 */
@Data
public class CustomerAccount implements Serializable {

    private static final long serialVersionUID = 3750383295977748246L;

    private String accountId;

    private String accountNo;

    private Date dateTime;

    private BigDecimal currentBalance;

    private Integer version;

    private Date createTime;

    private Date updateTime;
}
