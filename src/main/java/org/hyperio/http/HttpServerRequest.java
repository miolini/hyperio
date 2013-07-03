package org.hyperio.http;

import org.hyperio.core.Context;
import org.hyperio.core.Selectable;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class HttpServerRequest implements Selectable
{
    private final Context context;
    private SocketChannel client;
    private HttpServer httpServer;
    private ByteBuffer buffer;
    private char[] CRLF = {'\r', '\n', '\r', '\n'};

    public HttpServerRequest(SocketChannel client, HttpServer httpServer)
    {
        try
        {
            this.client = client;
            this.httpServer = httpServer;
            this.context = httpServer.getContext();
            client.configureBlocking(false);
            context.registerRead(client, this);
            buffer = ByteBuffer.allocateDirect(1024 * 10);
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
            System.out.println(buffer);
            int readed = client.read(buffer);
            System.out.println(buffer);
            if (readed < 4) return;
            Buffer header = buffer.mark();
            System.out.println(header.toString());
            for (int i = 0; i < readed - 4; i++)
            {
                if (buffer.get(i) == CRLF[0] &&
                        buffer.get(i+1) == CRLF[1] &&
                        buffer.get(i+2) == CRLF[2] &&
                        buffer.get(i+3) == CRLF[3])
                {
                    parseHeaders(i);
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void parseHeaders(int len)
    {
        System.out.println(len);
    }
}
