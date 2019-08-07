package net.youqu.micro.service.monitor;

import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DirectMemoryMonitor {
    private static final Logger logger = LoggerFactory.getLogger(DirectMemoryMonitor.class);
    private static final int _1k = 1024;
    private static final String BUSINESS_KEY = "netty_direct_memory";

    private AtomicLong directMemory;

    @PostConstruct
    public void init() {
        Field field = ReflectionUtils.findField(PlatformDependent.class, "DIRECT_MEMORY_COUNTER");
        field.setAccessible(true);

        try {
            directMemory = (AtomicLong) field.get(PlatformDependent.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::doReport, 0, 1, TimeUnit.MINUTES);
    }

    private void doReport() {
        int memoryInKb = (int) directMemory.get() / _1k;
        logger.warn("{}:{}k", BUSINESS_KEY, memoryInKb);
    }
}
