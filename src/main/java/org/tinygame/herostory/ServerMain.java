package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdHandler.CmdHandleFactory;


/**
 * 服务器启动类
 */
public class ServerMain {

    /**
     * 服务器端口号
     */
    public static final int SERVER_PORT = 12345;

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    /**
     * 应用主函数
     *
     * @param args 命令行参数数组
     */
    public static void main(String[] args) {
        // 初始化
        CmdHandleFactory.init();
        GameMsgRecognizer.init();

        // 设置log4j配置文件
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boosGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addLast(
                        new HttpServerCodec(), // HTTP 服务器编解码器
                        new HttpObjectAggregator(65535), // 内容长度限制
                        new WebSocketServerProtocolHandler("/websocket"), // WebSocket 协议处理器, 在这里处理握手、ping、pong等消息
                        new GameMsgDecoder(), // 自定义消息解码器
                        new GameMsgEncoder(), // 自定义消息编码器
                        new GameMsgHandler() // 自定义消息处理器
                );
            }
        });

        try {
            // 绑定 12345 端口, 实际项目中会使用 args 中的参数来指定端口号
            ChannelFuture channelFuture = serverBootstrap.bind(SERVER_PORT).sync();

            if (channelFuture.isSuccess()) {
                LOGGER.info("服务器启动成功");
            }

            // 等待服务器信道关闭, 也就是不要立即退出应用程序, 让应用程序可以一直提供服务
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            // 关闭服务器
            workerGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }
}
