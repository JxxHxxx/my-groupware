package com.jxx.vacation.api.common.web;

public record RequestUri(
        String host,
        int port,
        String path
) {
}
