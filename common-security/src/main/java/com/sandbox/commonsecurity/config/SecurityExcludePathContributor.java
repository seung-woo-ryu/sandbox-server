package com.sandbox.commonsecurity.config;

import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.Set;

@FunctionalInterface
public interface SecurityExcludePathContributor {
    Map<String, Set<HttpMethod>> getExcludePaths();
}
