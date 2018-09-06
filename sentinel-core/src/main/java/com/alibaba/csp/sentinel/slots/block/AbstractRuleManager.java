package com.alibaba.csp.sentinel.slots.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.csp.sentinel.util.spring.AntPathMatcher;

/**
 * Description:
 * User: ouzhouyou@raycloud.com
 * Date: 18/9/6
 * Time: 上午11:29
 * Version: 1.0
 */
public abstract class AbstractRuleManager {
    private static final String GLOBAL = "*";

    /**
     * 主要是用来做缓存用的. 避免不必要的判断
     */
    public static Map<Map, Boolean> ruleBoolMap = new HashMap<Map, Boolean>();

    /**
     * 检查资源名称内是否包含通配符
     */
    public static <T extends AbstractRule> void checkResourceNameHasWildcard(Map<String, List<T>> rulesMap) {
        boolean localHasWildcard = false;
        for (String s : rulesMap.keySet()) {
            if (s.contains("*")) {
                localHasWildcard = true;
                break;
            }
        }
        ruleBoolMap.put(rulesMap, localHasWildcard);
    }


    /**
     * 支持通配符,先查看是否有匹配的
     */
    public static <T extends AbstractRule> List<T> getRules(Map<String, List<T>> rulesMap, String resourceName) {
        if (rulesMap == null || rulesMap.size() == 0) {
            return null;
        }
        List<T> rules = rulesMap.get(resourceName);
        if (rules != null) {
            return rules;
        }
        if (ruleBoolMap.get(rulesMap) == null || !ruleBoolMap.get(rulesMap)) {
            return null;
        }

        boolean hasWildcard = false;
        for (String key : rulesMap.keySet()) {
            if (GLOBAL.equals(key)) {
                hasWildcard = true;
                continue;
            }
            if (AntPathMatcher.antPathMatcher.match(key, resourceName)) {
                return rulesMap.get(key);
            }
        }
        return hasWildcard ? rulesMap.get("*") : null;
    }


}
