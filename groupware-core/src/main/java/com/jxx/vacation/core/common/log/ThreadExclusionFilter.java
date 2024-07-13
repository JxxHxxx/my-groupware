package com.jxx.vacation.core.common.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class ThreadExclusionFilter extends Filter<ILoggingEvent> {
    private String threadName;

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().contains(threadName)) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}
