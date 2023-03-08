package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 指令处理器工厂
 */
public final class CmdHandleFactory {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdHandleFactory.class);

    /**
     * 处理器字典
     */
    private static Map<Class<?>, ICmdHandle<? extends GeneratedMessageV3>> _handleMap = new HashMap<>();

    /**
     * 私有化类构造方法
     */
    private CmdHandleFactory() {
    }

    public static void init() {
        LOGGER.info("=== 完成命令与处理器的关联 ===");

        // 获取包名称
        final String packageName = CmdHandleFactory.class.getPackage().getName();
        // 获取 ICmdHandler 所有的实现类
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(
                packageName,
                true,
                ICmdHandle.class
        );

        for (Class<?> handlerClazz : clazzSet) {
            // 判断非空和抽象类型
            if (handlerClazz == null ||
               (handlerClazz.getModifiers() & Modifier.ABSTRACT) != 0) {
                continue;
            }

            // 获取方法数组
            Method[] methods = handlerClazz.getDeclaredMethods();
            // 消息类型
            Class<?> cmdClazz = null;

            for (Method method : methods) {
                if (method == null ||
                   !method.getName().equals("handle")) {
                    continue;
                }

                // 获取函数参数类型数组
                Class<?>[] parameterTypes = method.getParameterTypes();

                if (parameterTypes.length < 2 ||
                    parameterTypes[1] == GeneratedMessageV3.class ||
                    !GeneratedMessageV3.class.isAssignableFrom(parameterTypes[1])) {
                    continue;
                }

                cmdClazz = parameterTypes[1];
                break;
            }

            if (cmdClazz == null) {
                continue;
            }

            try {
                ICmdHandle<?> newInstance = (ICmdHandle<?>) handlerClazz.newInstance();

                LOGGER.info("{} <===> {}", cmdClazz.getName(), handlerClazz.getName());

                _handleMap.put(cmdClazz, newInstance);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static ICmdHandle<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (msgClazz == null) {
            return null;
        }

        return _handleMap.get(msgClazz);
    }
}
