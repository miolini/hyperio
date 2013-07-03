package org.hyperio;

import org.hyperio.core.Context;
import org.hyperio.http.HttpServer;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.jboss.netty.util.CharsetUtil;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.jboss.netty.channel.Channels.pipeline;

public class HttpServerTest
{
    private static final String CONTENT_TYPE = "Content-Type";

    @Test
    public void test()
    {
        Context context = Context.getContext();
        HttpServer httpServer = new HttpServer(context);
        httpServer.start();
        context.exec();
    }

    @Test
    public void nettyTest()
    {
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory()
        {
            public ChannelPipeline getPipeline() throws Exception
            {
                // Create a default pipeline implementation.
                ChannelPipeline pipeline = pipeline();

                // Uncomment the following line if you want HTTPS
                //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
                //engine.setUseClientMode(false);
                //pipeline.addLast("ssl", new SslHandler(engine));

                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

                pipeline.addLast("handler", new SimpleChannelUpstreamHandler()
                {
                    @Override
                    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
                    {
                        HttpRequest request = (HttpRequest) e.getMessage();
                        if (request.getMethod() != HttpMethod.GET)
                        {
                            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
                            return;
                        }

                        System.out.println(request.getUri());
                        //final String path = sanitizeUri(request.getUri());
//                        if (path == null)
//                        {
//                            sendError(ctx, FORBIDDEN);
//                            return;
//                        }
                    }

                    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status)
                    {
                        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
                        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
                        response.setContent(ChannelBuffers.copiedBuffer(
                                "Failure: " + status.toString() + "\r\n",
                                CharsetUtil.UTF_8));

                        // Close the connection as soon as the error message is sent.
                        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
                    }
                });
                return pipeline;
            }
        });

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(8080));
    }
}
