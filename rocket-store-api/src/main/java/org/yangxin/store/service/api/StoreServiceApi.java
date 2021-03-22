package org.yangxin.store.service.api;

import java.util.Date;

/**
 * @author yangxin
 * 2020/12/20 15:47
 */
public interface StoreServiceApi {

    /**
     * 获得版本号
     *
     * @param supplierId 提供商Id
     * @param goodIds 商品Id
     * @return 版本号
     */
    int selectVersion(String supplierId, String goodIds);

    /**
     * 通过版本号更新库存
     *
     * @param version 版本号
     * @param supplierId 提供商Id
     * @param goodIds 商品Id
     * @param updateBy 更新人
     * @param updateTime 更新时间
     * @return 受影响的行数
     */
    int updateStoreCountByVersion(int version, String supplierId, String goodIds, String updateBy, Date updateTime);

    /**
     * 获得库存
     *
     * @param supplierId 提供商Id
     * @param goodIds 商品Id
     * @return 库存数
     */
    int selectStoreCount(String supplierId, String goodIds);
}
