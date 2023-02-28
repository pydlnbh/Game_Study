import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * 客户端信道数组, 一定要定义为static, 否则无法实现群收
     */
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 用户组
     */
    private static final Map<Integer, User> _userMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        _channelGroup.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        _channelGroup.remove(ctx.channel());

        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        _userMap.remove(userId);

        GameMsgProtocol.UserQuitResult.Builder newBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        newBuilder.setQuitUserId(userId);

        GameMsgProtocol.UserQuitResult result = newBuilder.build();
        _channelGroup.writeAndFlush(result);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        System.out.println("收到的消息, msgClazz = " + msg.getClass().getSimpleName() + ", msg = " + msg);

        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            // 从指令对象获取信息
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            int userId = cmd.getUserId();
            String heroAvatar = cmd.getHeroAvatar();

            GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
            builder.setUserId(userId);
            builder.setHeroAvatar(heroAvatar);

            User user = new User();
            user.setUserId(userId);
            user.setHero(heroAvatar);
            _userMap.put(userId, user);

            channelHandlerContext.channel().attr(AttributeKey.valueOf("userId")).set(userId);

            // 构建结果并群发
            GameMsgProtocol.UserEntryResult result = builder.build();
            _channelGroup.writeAndFlush(result);

        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            GameMsgProtocol.WhoElseIsHereResult.Builder newBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

            for (User user : _userMap.values()) {
                if (user == null) {
                    continue;
                }

                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfo = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfo.setUserId(user.getUserId());
                userInfo.setHeroAvatar(user.getHero());

                newBuilder.addUserInfo(userInfo);
            }

            GameMsgProtocol.WhoElseIsHereResult result = newBuilder.build();
            channelHandlerContext.writeAndFlush(result);

        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            float moveToPosX = ((GameMsgProtocol.UserMoveToCmd) msg).getMoveToPosX();
            float moveToPosY = ((GameMsgProtocol.UserMoveToCmd) msg).getMoveToPosY();

            Integer userId = (Integer) channelHandlerContext.channel().attr(AttributeKey.valueOf("userId")).get();

            if (userId == null) {
                return;
            }

            GameMsgProtocol.UserMoveToResult.Builder newBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
            newBuilder.setMoveUserId(userId);
            newBuilder.setMoveToPosX(moveToPosX);
            newBuilder.setMoveToPosY(moveToPosY);

            GameMsgProtocol.UserMoveToResult result = newBuilder.build();
            _channelGroup.writeAndFlush(result);
        }
    }
}
