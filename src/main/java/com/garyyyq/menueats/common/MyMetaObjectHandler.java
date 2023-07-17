package com.garyyyq.menueats.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    // Autofill when insert
    @Override
    public void insertFill(MetaObject metaObject) {

        log.info("start insert fill ....");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());


    }

    @Override
    public void updateFill(MetaObject metaObject) {

        log.info("start update fill ....");

        long id = Thread.currentThread().getId();
        log.info("Thread id in updateFill: {}", id);
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());


    }
}
