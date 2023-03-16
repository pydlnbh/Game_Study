package org.tinygame.herostory.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MainThreadProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作服务类
 */
public final class AsyncOperationProcessor {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    /**
     * 单例对象
     */
    private static final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    /**
     * 创建一个单线程数组
     */
    private final ExecutorService[] _esArray = new ExecutorService[8];

    /**
     * 私有化默认构造方法
     */
    private AsyncOperationProcessor() {
        for (int i = 0; i < _esArray.length; i++) {
            // 线程名称
            final String threadName = "AsyncOperationProcessor" + i;
            // 创建一个单线程
            _esArray[i] = Executors.newSingleThreadExecutor((r) -> {
                Thread newTread = new Thread(r);
                newTread.setName(threadName);
                return newTread;
            });
        }
    }

    /**
     * 获取单例对象
     *
     * @return 异步操作器
     */
    public static AsyncOperationProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理异步操作
     *
     * @param asyncOp 异步操作
     */
    public void process(IAsyncOperation asyncOp) {
        if (asyncOp == null) {
            return;
        }

        // 根据 bindId 获取线程索引
        int bindId = Math.abs(asyncOp.getBindId());
        int esIndex = bindId % _esArray.length;

        _esArray[esIndex].submit(() -> {
            try {
                // 执行异步操作
                asyncOp.doSync();

                // 返回主线程执行完成逻辑
                MainThreadProcessor.getInstance().process(asyncOp::doFinish);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });

    }
}
