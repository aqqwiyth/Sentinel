package com.raycloud.csp.sentinel.datasource.diamond;

import java.util.concurrent.*;

import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.fastjson.JSONException;
import com.raycloud.diamond.manager.ConfigSubListener;
import com.raycloud.diamond.manager.impl.ConfigSubManager;

/**
 * Description:
 * User: ouzhouyou@raycloud.com
 * Date: 18/9/4
 * Time: 下午2:02
 * Version: 1.0
 */
public class DiamondDataSource<T extends AbstractRule> extends AbstractDataSource<String, T> {

    private String groupId = "sentinel";
    private ConfigSubManager configSubManager;

    private static final ExecutorService pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(1), new NamedThreadFactory("sentinel-diamond-ds-update"),
            new ThreadPoolExecutor.DiscardOldestPolicy());


    public DiamondDataSource(String dataId, final Converter<String, T> parser) {
        super(parser);
        configSubManager = new ConfigSubManager(groupId, dataId, new ConfigSubListener() {
            @Override
            public Executor getExecutor() {
                return DiamondDataSource.pool;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                try {
                    T newValue = parser.convert(configInfo);
                    getProperty().updateValue(newValue);
                } catch (JSONException e) {
                    //json格式不对
                    RecordLog.info("[DiamondDataSource] WARN: receiveConfigInfo config is error, you may have to check your data source", e);
                }
            }
        });

        loadInitialConfig();
    }

    private void loadInitialConfig() {
        try {
            T newValue = loadConfig();
            if (newValue == null) {
                RecordLog.info("[DiamondDataSource] WARN: initial config is null, you may have to check your data source");
            }
            getProperty().updateValue(newValue);
        } catch (Exception ex) {
            RecordLog.info("[DiamondDataSource] Error when loading initial config", ex);
        }
    }

    @Override
    public String readSource() throws Exception {
        return configSubManager.getAvailableConfigureInfomation(10000);
    }

    @Override
    public void close() throws Exception {
        try {
            configSubManager.close();
        } catch (Exception e) {
            ;
        }
    }
}
