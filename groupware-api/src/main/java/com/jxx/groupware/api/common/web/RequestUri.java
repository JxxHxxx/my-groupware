package com.jxx.groupware.api.common.web;

public record RequestUri(
        String host,
        int port,
        String path
) {
}
