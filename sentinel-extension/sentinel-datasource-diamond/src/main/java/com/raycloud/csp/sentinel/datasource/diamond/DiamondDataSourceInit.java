package com.raycloud.csp.sentinel.datasource.diamond;

import java.util.List;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * Description:
 * User: ouzhouyou@raycloud.com
 * Date: 18/9/4
 * Time: 下午2:02
 * Version: 1.0
 */
public class DiamondDataSourceInit implements InitFunc {
    @Override
    public void init() throws Exception {
        String dataId = System.getProperty("dubboAppName");

        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new DiamondDataSource(dataId + "-" + "flow",
                new Converter<String, List<FlowRule>>() {
                    @Override
                    public List<FlowRule> convert(String source) {
                        return JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                        });
                    }
                });

        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new DiamondDataSource(dataId + "-" + "degrade",
                new Converter<String, List<DegradeRule>>() {
                    @Override
                    public List<DegradeRule> convert(String source) {
                        return JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
                        });
                    }
                });

        ReadableDataSource<String, List<AuthorityRule>> authRuleDataSource = new DiamondDataSource(dataId + "-" + "auth",
                new Converter<String, List<AuthorityRule>>() {
                    @Override
                    public List<AuthorityRule> convert(String source) {
                        return JSON.parseObject(source, new TypeReference<List<AuthorityRule>>() {
                        });
                    }
                });

        ReadableDataSource<String, List<SystemRule>> sysRuleDataSource = new DiamondDataSource(dataId + "-" + "sys",
                new Converter<String, List<SystemRule>>() {
                    @Override
                    public List<SystemRule> convert(String source) {
                        return JSON.parseObject(source, new TypeReference<List<SystemRule>>() {
                        });
                    }
                });

        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
        AuthorityRuleManager.register2Property(authRuleDataSource.getProperty());
        SystemRuleManager.register2Property(sysRuleDataSource.getProperty());

    }
}
