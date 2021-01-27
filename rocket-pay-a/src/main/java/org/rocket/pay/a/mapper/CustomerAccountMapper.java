package org.rocket.pay.a.mapper;

import org.apache.ibatis.annotations.Param;
import org.rocket.pay.a.entity.CustomerAccount;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yangxin
 * 1/24/21 4:14 PM
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface CustomerAccountMapper {

    int deleteByPrimaryKey(String accountId);

    int insert(CustomerAccount record);

    int insertSelective(CustomerAccount record);

    CustomerAccount selectByPrimaryKey(String accountId);

    int updateByPrimaryKeySelective(CustomerAccount record);

    int updateByPrimaryKey(CustomerAccount record);

    int updateBalance(@Param("accountId") String accountId,
                      @Param("newBalance") BigDecimal newBalance,
                      @Param("version") int currentVersion);
//    int updateBalance(@Param("accountId") String accountId,
//                      @Param("newBalance") BigDecimal newBalance,
//                      @Param("version") int currentVersion,
//                      @Param("updateTime") Date currentTime);
}
