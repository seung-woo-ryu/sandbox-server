package com.sandbox.commonsecurity.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityExcludePathProvider {
    private final Map<PathPattern, Set<HttpMethod>> compiledPatterns;

    public SecurityExcludePathProvider(List<SecurityExcludePathContributor> contributors) {
        PathPatternParser parser = new PathPatternParser();
        this.compiledPatterns = contributors.stream()
                .map(SecurityExcludePathContributor::getExcludePaths)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        entry -> parser.parse(entry.getKey()),
                        Map.Entry::getValue,
                        (existing, incoming) -> {
                            Set<HttpMethod> merged = new HashSet<>(existing);
                            merged.addAll(incoming);
                            return merged;
                        }
                ));
    }

    public Map<PathPattern, Set<HttpMethod>> getExcludePatterns() {
        return compiledPatterns;
    }

    public boolean isExcluded(PathContainer pathContainer, HttpMethod method) {
        for (Map.Entry<PathPattern, Set<HttpMethod>> entry : compiledPatterns.entrySet()) {
            if (entry.getKey().matches(pathContainer) && entry.getValue().contains(method)) {
                return true;
            }
        }
        return false;
    }
}
