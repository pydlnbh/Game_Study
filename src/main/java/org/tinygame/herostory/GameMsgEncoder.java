package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 消息编码器
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    /**
     * 日志对象
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        try {
            if (!(msg instanceof GeneratedMessageV3)) {
                super.write(ctx, msg, promise);
                return;
            }

            // 消息编码
            int msgCode;

            if (msg instanceof GameMsgProtocol.UserEntryResult) {
                msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult) {
                msgCode = GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.UserMoveToResult) {
                msgCode = GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.UserQuitResult) {
                msgCode = GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
            } else {
                LOGGER.error("无法识别的消息类型, msgClazz = {}", msg.getClass().getSimpleName());
                super.write(ctx, msg, promise);
                return;
            }

            // 消息体
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

            ByteBuf byteBuf = ctx.alloc().buffer();
            // 消息长度
            byteBuf.writeShort((short) msgBody.length);
            // 消息编号
            byteBuf.writeShort((short) msgCode);
            // 消息体
            byteBuf.writeBytes(msgBody);

            // 写出 ByteBuf
            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuf);
            super.write(ctx, outputFrame, promise);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }
}
