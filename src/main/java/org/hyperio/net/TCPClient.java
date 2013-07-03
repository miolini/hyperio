package org.hyperio.net;

import org.hyperio.core.Context;
import org.hyperio.core.Selectable;
import org.hyperio.event.EventEmitter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class TCPClient extends EventEmitter implements Selectable
{
    private static final String EVENT_CONNECT = "connect";
    private static final String EVENT_READ = "read";
    private SocketChannel client;
    private InetSocketAddress addr;
    private ByteBuffer buffer;
    private int readed;

    public TCPClient()
    {
        buffer = ByteBuffer.allocate(128);
    }

    public void connect(InetSocketAddress addr)
    {
        try
        {
            this.addr = addr;
            client = SocketChannel.open();
            client.configureBlocking(false);
            Context.getContext().registerConnect(client, this);
            client.connect(addr);
//            client.socket().setSendBufferSize(0x100000); // 1Mb
//            client.socket().setReceiveBufferSize(0x100000); // 1Mb
//            client.socket().setKeepAlive(true);
//            client.socket().setReuseAddress(true);
//            client.socket().setSoLinger(false, 0);
//            client.socket().setSoTimeout(0);
//            client.socket().setTcpNoDelay(true);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void write(ByteBuffer buf)
    {
        try
        {
            client.write(buf);
            Context.getContext().registerRead(client, this);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private int read(ByteBuffer buf)
    {
        try
        {
            return client.read(buf);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void select(SelectionKey key)
    {
        try
        {
            if (key.isConnectable())
            {
                System.out.println("connectable");
                client.finishConnect();
                Context.getContext().registerWrite(client, this);
                fire(EVENT_CONNECT, this, "client", this);
            } else if (key.isWritable())
            {
                System.out.println("writable");
            } else if (key.isReadable())
            {
                System.out.println("readable");
                while ((readed = read(buffer)) > 0)
                {
                    byte[] data = new byte[readed];
                    buffer.get(data, 0, readed);
                    fire(EVENT_READ);
                }
            } else if (key.isAcceptable())
            {
                System.out.println("acceptable");
            } else
            {
                System.out.println("unknown key");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public ByteBuffer getBuffer()
    {
        return buffer;
    }

    public int getReaded()
    {
        return readed;
    }
}
