package dev.latvian.kubejs.documentation;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @author LatvianModder
 */
public class NettyHttpServer
{
	private final int port;
	private ChannelFuture channel;
	private final EventLoopGroup masterGroup;
	private final EventLoopGroup slaveGroup;

	public NettyHttpServer(int p)
	{
		port = p;
		masterGroup = new NioEventLoopGroup();
		slaveGroup = new NioEventLoopGroup();
	}

	public void start()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

		try
		{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(masterGroup, slaveGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				public void initChannel(final SocketChannel ch)
				{
					ch.pipeline().addLast("codec", new HttpServerCodec());
					ch.pipeline().addLast("aggregator", new HttpObjectAggregator(512 * 1024));
					ch.pipeline().addLast("request", new ChannelInboundHandlerAdapter()
					{
						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
						{
							if (msg instanceof FullHttpRequest)
							{
								FullHttpRequest request = (FullHttpRequest) msg;
								String responseMessage = Documentation.get().handleHTTP(request);
								FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(responseMessage.getBytes()));

								if (HttpUtil.isKeepAlive(request))
								{
									response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
								}

								response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
								response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseMessage.length());

								ctx.writeAndFlush(response);
							}
							else
							{
								super.channelRead(ctx, msg);
							}
						}

						@Override
						public void channelReadComplete(ChannelHandlerContext ctx)
						{
							ctx.flush();
						}

						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
						{
							ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(cause.getMessage().getBytes())));
						}
					});
				}
			});

			bootstrap.option(ChannelOption.SO_BACKLOG, 128);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			channel = bootstrap.bind(port).sync();
		}
		catch (InterruptedException ignored)
		{
		}
	}

	public void shutdown()
	{
		slaveGroup.shutdownGracefully();
		masterGroup.shutdownGracefully();

		try
		{
			channel.channel().closeFuture().sync();
		}
		catch (InterruptedException ignored)
		{
		}
	}
}