package com.yunxi.common.tracer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.yunxi.common.tracer.appender.TracerAppender;

/**
 * 主要用于清理日志
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerDelete.java, v 0.1 2017年1月11日 下午3:03:24 leukony Exp $
 */
public class TracerDelete implements Runnable {

    private static final long           ONE_HOUR  = 60 * 60;
    private static long                 interval  = ONE_HOUR;
    private static AtomicBoolean        isRunning = new AtomicBoolean(false);
    private static List<TracerAppender> watched   = new CopyOnWriteArrayList<TracerAppender>();

    public void run() {
        while (true) {
            try {
                for (TracerAppender appender : watched) {
                    appender.clean();
                }
                TimeUnit.SECONDS.sleep(interval);
            } catch (Throwable e) {
                // TODO error
            }
        }
    }

    /**
     * 注册Appender
     * @param appender
     */
    public static void watch(TracerAppender appender) {
        watched.add(appender);
    }

    /**
     * 调整扫描周期
     * @param interval
     */
    public static void setScanInterval(long interval) {
        TracerDelete.interval = interval;
    }

    /**
     * 启动清理日志
     */
    public static void start() {
        if (isRunning.compareAndSet(false, true)) {
            Thread tracerDelete = new Thread(new TracerDelete());
            tracerDelete.setName("Tracer-Delete");
            tracerDelete.setDaemon(true);
            tracerDelete.start();
        }
    }
}