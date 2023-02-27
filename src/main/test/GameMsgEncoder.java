import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }

        int msgCode = -1;

        if (msg instanceof GameMsgProtocol.UserEntryResult) {
            msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
        } else {
            System.out.println("Oops1");
            return;
        }

        byte[] byteArray = ((GeneratedMessageV3) msg).toByteArray();

        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeShort((short)0);
        buffer.writeShort((short) msgCode);
        buffer.writeBytes(byteArray);

        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(buffer);
        super.write(ctx, binaryWebSocketFrame, promise);
    }
}
