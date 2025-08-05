package com.ruoyi.system.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * description
 *
 * @author zl 2024/11/13
 * @version 1.0
 */

public class FillMetaObjectHandle implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        System.out.println("进入");
        // 创建人
//        this.strictInsertFill(metaObject, "id", String.class, UUID.fastUUID().toString(true));
        // 创建人
        this.strictInsertFill(metaObject, "createUser", String.class, this.getLoginUser());
        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 修改人
        this.strictInsertFill(metaObject, "updateUser", String.class, this.getLoginUser());
        // 修改时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 删除标志
        this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 修改人
        this.strictUpdateFill(metaObject, "updatedBy", String.class, this.getLoginUser());
        // 修改时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    private String getLoginUser() {
        return SecurityUtils.getUserId().toString();
    }

}
