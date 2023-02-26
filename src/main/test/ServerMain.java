import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class ServerMain {
    public static void main(String[] args) {
        // 实例化bossGroup(拉客的美女)
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        // 实例化workGroup(服务员小哥)
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        // 实例化启动器
        ServerBootstrap bootstrap = new ServerBootstrap();

        // 把boosGroup和workGroup通过group方法放入启动器中
        bootstrap.group(boosGroup, workGroup);
        // 通过channel方法给启动器设置NioServerSocketChannel.class
        bootstrap.channel(NioServerSocketChannel.class);
        // 通过childHandler初始化channel
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(
                        // Http服务器编解码
                        new HttpServerCodec(),
                        // 内容长度限制, 两个字节
                        new HttpObjectAggregator(65535),
                        new WebSocketServerProtocolHandler("/websocket"),
                        new GameMsgDecoder(),
                        new GameMsgHandler()
                );
            }
        });

        try {
            // 使用启动器绑定端口, 一定使用sync()方法, 负责启动不成功
            ChannelFuture channelFuture = bootstrap.bind(1234).sync();

            if (channelFuture.isSuccess()) {
                System.out.println("服务器启动成功");
            }

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
