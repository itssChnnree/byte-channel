package com.ruoyi.system.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * description
 *
 * @author zl 2024/11/13
 * @version 1.0
 */

public class FillMetaObjectHandle implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.getOriginalObject().getClass() == SysUser.class){
            return;
        }
        // 创建人
//        this.strictInsertFill(metaObject, "id", String.class, UUID.fastUUID().toString(true));
        // 创建人
        if (metaObject.getValue("createUser") == null){
            this.strictInsertFill(metaObject, "createUser", String.class, this.getLoginUser());
        }
        if (metaObject.getValue("createTime") == null){
            // 创建时间
            this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        }
        if (metaObject.getValue("updateUser") == null){
            // 修改人
            this.strictInsertFill(metaObject, "updateUser", String.class, this.getLoginUser());
        }
        if (metaObject.getValue("updateTime") == null){
            // 修改时间
            this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        }
        if (metaObject.getValue("isDeleted") == null){
            // 删除标志
            this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.getValue("updateUser") == null){
            // 修改人
            this.strictInsertFill(metaObject, "updateUser", String.class, this.getLoginUser());
        }
        if (metaObject.getValue("updateTime") == null){
            // 修改时间
            this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        }
    }

    private String getLoginUser() {
        return SecurityUtils.getUserId().toString();
    }

}
