package org.yangxin.store.service.api;

import java.util.Date;

/**
 * @author yangxin
 * 2020/12/20 15:47
 */
public interface StoreServiceAPI {

    int selectVersion(String supplierId, String goodIds);

    int updateStoreCountByVersion(int version, String supplierId, String goodIds, String updateBy, Date updateTime);

    int selectStoreCount(String supplierId, String goodIds);
}
