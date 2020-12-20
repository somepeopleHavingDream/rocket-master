package org.rocket.store.service.provider;

import com.alibaba.dubbo.config.annotation.Service;
import org.rocket.store.mapper.StoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.yangxin.store.service.api.StoreServiceAPI;

import java.util.Date;

/**
 * @author yangxin
 * 2020/12/20 15:59
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@Service(version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}")
//@org.springframework.stereotype.Service
public class StoreServiceProvider implements StoreServiceAPI {

    @Autowired
    private StoreMapper storeMapper;

    @Override
    public int selectVersion(String supplierId, String goodIds) {
        return storeMapper.selectVersion(supplierId, goodIds);
    }

    @Override
    public int updateStoreCountByVersion(int version, String supplierId, String goodIds, String updateBy, Date updateTime) {
        return storeMapper.updateStoreCountByVersion(version, supplierId, goodIds, updateBy, updateTime);
    }

    @Override
    public int selectStoreCount(String supplierId, String goodIds) {
        return storeMapper.selectStoreCount(supplierId, goodIds);
    }
}
