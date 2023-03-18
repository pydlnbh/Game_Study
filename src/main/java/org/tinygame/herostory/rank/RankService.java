package org.tinygame.herostory.rank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.async.impl.AsyncGetRank;

import java.util.List;
import java.util.function.Function;

/**
 * 排行榜服务
 */
public final class RankService {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    /**
     * 单例对象
     */
    private static final RankService _instance = new RankService();

    /**
     * 私有化默认构造方法
     */
    private RankService() {
    }

    /**
     * 获取单例对象
     *
     * @return 排行榜服务
     */
    public static RankService getInstance() {
        return _instance;
    }

    public void getRand(Function<List<RankItem>, Void> callBack) {
        if (callBack == null) {
            return;
        }

        IAsyncOperation asyncOp = new AsyncGetRank() {
            /**
             * 执行完成逻辑
             */
            @Override
            public void doFinish() {
                callBack.apply(this.getRankItemList());
            }
        };

        AsyncOperationProcessor.getInstance().process(asyncOp);
    }
}
