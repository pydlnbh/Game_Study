package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.impl.CmdHandlerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主线程处理器
 */
public final class MainThreadProcessor {

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);

    /**
     * 单例对象
     */
    private static final MainThreadProcessor _instance = new MainThreadProcessor();

    /**
     * 创建一个单线程
     */
    private final ExecutorService _es = Executors.newSingleThreadExecutor((r) -> {
        Thread newThread = new Thread(r);
        newThread.setName("MainThreadProcessor");
        return newThread;
    });

    /**
     * 私有化类默认构造器
     */
    private MainThreadProcessor() {
    }

    /**
     * 获取单例对象
     *
     * @return 主线程处理器
     */
    public static MainThreadProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理消息
     *
     * @param ctx 客户端信道上下文
     * @param msg 消息对象
     */
    public void process(ChannelHandlerContext ctx, GeneratedMessageV3 msg) {
        if (null == ctx || null == msg) {
            return;
        }

        this._es.submit(() -> {
            ICmdHandler<? extends GeneratedMessageV3> cmd = CmdHandlerFactory.create(msg.getClass());

            if (cmd != null) {
                cmd.handle(ctx, cast(msg));
            } else {
                LOGGER.error("未找到相相对应的指令处理器, msgClazz = {}", msg.getClass().getName());
            }
        });
    }

    /**
     * 处理消息
     *
     * @param r 实例
     */
    public void process(Runnable r) {
        if (r != null) {
            _es.submit(r);
        }
    }

    private static <T extends GeneratedMessageV3> T cast(Object msg) {
        if (msg == null) {
            return null;
        } else {
            return (T) msg;
        }
    }
}
