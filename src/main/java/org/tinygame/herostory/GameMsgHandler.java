package org.tinygame.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的消息处理器
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);

    /**
     * 信道组, 注意这里一定要用static, 否则无法实现群发
     */
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 用户字典
     */
    private static final Map<Integer, User> userMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }

        try {
            super.channelActive(ctx);
            channelGroup.add(ctx.channel());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        channelGroup.remove(ctx.channel());

        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (null == userId) {
            return;
        }

        userMap.remove(userId);

        GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        resultBuilder.setQuitUserId(userId);

        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
        channelGroup.writeAndFlush(newResult);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        LOGGER.info("收到客户端消息, msgClazz = {}, msgBody = {}", msg.getClass().getSimpleName(), msg);

        try {
            if (msg instanceof GameMsgProtocol.UserEntryCmd) {
                // 用户入场消息
                GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
                int userId = cmd.getUserId();
                String heroAvatar = cmd.getHeroAvatar();

                GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
                resultBuilder.setUserId(userId);
                resultBuilder.setHeroAvatar(heroAvatar);

                // 将用户加入字典
                User newUser = new User();
                newUser.userId = userId;
                newUser.heroAvatar = heroAvatar;
                userMap.put(userId, newUser);

                // 将用户id附着Channel
                ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

                // 构建结果并广播
                GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
                channelGroup.writeAndFlush(newResult);
            } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
                // 还有谁在场
                GameMsgProtocol.WhoElseIsHereResult.Builder resultBuider = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

                for (User curUser : userMap.values()) {
                    if (null == curUser) {
                        continue;
                    }

                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                    userInfoBuilder.setUserId(curUser.userId);
                    userInfoBuilder.setHeroAvatar(curUser.heroAvatar);
                    resultBuider.addUserInfo(userInfoBuilder);
                }

                GameMsgProtocol.WhoElseIsHereResult newResult = resultBuider.build();
                ctx.writeAndFlush(newResult);
            } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
                Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

                if (null == userId) {
                    return;
                }

                GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg;

                GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
                resultBuilder.setMoveUserId(userId);
                resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
                resultBuilder.setMoveToPosY(cmd.getMoveToPosY());

                GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
                channelGroup.writeAndFlush(newResult);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
