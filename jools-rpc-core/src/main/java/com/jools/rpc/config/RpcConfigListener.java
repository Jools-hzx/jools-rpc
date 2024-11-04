package com.jools.rpc.config;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 19:21
 * @description: 配置类监听器，打印输出日志
 */
@Slf4j
public class RpcConfigListener {

    public static void init() {
        File file = FileUtil.file("application.properties");
//        //这里只监听文件或目录的修改事件
//        WatchMonitor watchMonitor = WatchMonitor.create(file, WatchMonitor.ENTRY_MODIFY);
//        watchMonitor.setWatcher(new Watcher() {
//            @Override
//            public void onCreate(WatchEvent<?> event, Path currentPath) {
//                Object obj = event.context();
//                log.info("创建：{}-> {}", currentPath, obj);
//            }
//
//            @Override
//            public void onModify(WatchEvent<?> event, Path currentPath) {
//                Object obj = event.context();
//                log.info("修改：{}-> {}", currentPath, obj);
//            }
//
//            @Override
//            public void onDelete(WatchEvent<?> event, Path currentPath) {
//                Object obj = event.context();
//                log.info("删除：{}-> {}", currentPath, obj);
//            }
//
//            @Override
//            public void onOverflow(WatchEvent<?> event, Path currentPath) {
//                Object obj = event.context();
//                log.info("Overflow：{}-> {}", currentPath, obj);
//            }
//        });
//        //启动监听
//        watchMonitor.start();
    }

}
