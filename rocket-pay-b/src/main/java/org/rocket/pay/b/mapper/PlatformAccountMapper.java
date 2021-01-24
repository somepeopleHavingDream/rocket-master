package org.rocket.pay.b.mapper;

import org.rocket.pay.b.entity.PlatformAccount;

/**
 * @author yangxin
 * 1/24/21 10:02 PM
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface PlatformAccountMapper {

    int deleteByPrimaryKey(String accountId);

    int insert(PlatformAccount record);

    int insertSelective(PlatformAccount record);

    PlatformAccount selectByPrimaryKey(String accountId);

    int updateByPrimaryKeySelective(PlatformAccount record);

    int updateByPrimaryKey(PlatformAccount record);
}
