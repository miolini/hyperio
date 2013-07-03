package org.hyperio;

import org.hyperio.core.Callback;
import org.hyperio.core.Context;
import org.hyperio.event.Event;
import org.hyperio.net.TCPClient;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class TCPClientTest
{
    @Test
    public void test() throws IOException
    {
        final TCPClient client = new TCPClient();
        client.connect(new InetSocketAddress("192.168.100.2", 80));
        client.on("connect", new Callback<Event>()
        {
            @Override
            public void call(Event data)
            {
                ByteBuffer buf = ByteBuffer.wrap("GET / HTTP/1.1\r\nHost: 192.168.100.2\r\n\r\n".getBytes());
                client.write(buf);
            }
        });
        client.on("read", new Callback<Event>()
        {
            @Override
            public void call(Event data)
            {
                System.out.println();
            }
        });
        Context.getContext().exec();
    }
}
