package com.jools.rpc.fault.tolerant.queue;

import cn.hutool.core.util.ObjectUtil;
import com.jools.rpc.fault.tolerant.ErrorTolerantKeys;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/12 10:32
 * @description: TODO
 */
public class FailBackMessageQueueFactory {

    private static Map<String, FailTolerantMessageQueue> queueMap = new ConcurrentHashMap<>();

    static {
        //Fail Back 策略
        queueMap.put(ErrorTolerantKeys.FAIL_BACK, FailBackMessageQueue.getInstance());
    }


    public static FailTolerantMessageQueue getMessageQueue(String errorTolerantKey) {
        if (StringUtils.isBlank(errorTolerantKey)) {
            throw new RuntimeException("Empty error tolerant key" + errorTolerantKey);
        }
        FailTolerantMessageQueue queue = queueMap.get(errorTolerantKey);
        if (ObjectUtil.isNotNull(queue)) {
            return queue;
        }
        return null;
    }
}
