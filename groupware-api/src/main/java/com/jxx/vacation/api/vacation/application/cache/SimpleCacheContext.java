package com.jxx.vacation.api.vacation.application.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleCacheContext {

    private static final List<String> COMPANY_CODES = List.of("JXX", "BNG", "SPY");

    public static boolean notExistCompany(String companyId) {
        return !COMPANY_CODES.contains(companyId);
    }
}
