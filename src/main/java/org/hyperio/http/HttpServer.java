package org.hyperio.http;

import org.hyperio.core.Context;
import org.hyperio.core.Selectable;
import org.hyperio.event.EventEmitter;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class HttpServer extends EventEmitter implements Selectable
{
    private ServerSocketChannel channel;
    private InetSocketAddress addr;
    private final Context context;

    public HttpServer(Context context)
    {
        this.context = context;
        addr = new InetSocketAddress("localhost", 8080);
    }

    public InetSocketAddress getAddr()
    {
        return addr;
    }

    public void setAddr(InetSocketAddress addr)
    {
        this.addr = addr;
    }

    public void start()
    {
        try
        {
            channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.bind(addr);
            context.registerAccept(channel, this);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void select(SelectionKey key)
    {
        try
        {
            if (key.isAcceptable())
            {
                System.out.println("accept");
                SocketChannel client = channel.accept();
                new HttpServerRequest(client, this);
            }
            else
            {
                System.out.println("unknown");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Context getContext()
    {
        return context;
    }
}
