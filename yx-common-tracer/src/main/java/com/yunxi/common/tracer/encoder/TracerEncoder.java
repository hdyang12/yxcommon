package com.yunxi.common.tracer.encoder;

import java.io.IOException;

import com.yunxi.common.tracer.appender.TracerAppender;
import com.yunxi.common.tracer.context.TracerContext;

/**
 * Tracer日志格式编码
 * <p>不允许多线程并发调用</p>
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerEncoder.java, v 0.1 2017年1月11日 下午3:42:56 leukony Exp $
 */
public interface TracerEncoder<T extends TracerContext> {
    
    /**
     * 转换日志格式编码
     * @param ctx
     * @param appender
     * @throws IOException
     */
    void encode(T ctx, TracerAppender appender) throws IOException;
}