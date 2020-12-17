package org.rocket.store.mapper;

import org.apache.ibatis.annotations.Param;
import org.rocket.store.entity.Store;

import java.util.Date;

/**
 * @author yangxin
 * 2020/12/17 22:19
 */
public interface StoreMapper {

    int deleteByPrimaryKey(String storeId);

    int insert(Store record);

    int insertSelective(Store record);

    Store selectByPrimaryKey(String storeId);

    int updateByPrimaryKeySelective(Store record);

    int updateByPrimaryKey(Store record);

    int selectVersion(@Param("supplierId") String supplierId, @Param("goodsId") String goodsId);

    int updateStoreCountByVersion(@Param("version") int version,
                                  @Param("supplierId") String supplierId,
                                  @Param("goodsId") String goodsId,
                                  @Param("updateBy") String updateBy,
                                  @Param("updateTime") Date updateTime);

    int selectStoreCount(@Param("supplierId") String supplierId, @Param("goodsId") String goodsId);
}
