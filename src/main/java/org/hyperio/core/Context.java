package org.hyperio.core;

import org.hyperio.event.EventEmitter;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class Context extends EventEmitter
{
    private Selector selector;
    private static final ThreadLocal<Context> context = new ThreadLocal<>();

    public Context()
    {
        try
        {
            this.selector = Selector.open();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Selector getSelector()
    {
        return selector;
    }

    public static Context getContext()
    {
        Context context = Context.context.get();
        if (context == null)
        {
            context = new Context();
            Context.context.set(context);
        }
        return context;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void exec()
    {
        try
        {
            while (true)
            {
                selector.select();
                for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext(); )
                {
                    SelectionKey key = i.next();
                    i.remove();
                    Selectable selectable = (Selectable) key.attachment();
                    if (selectable == null)
                    {
                        System.out.println("selectable is null");
                        continue;
                    }
                    selectable.select(key);
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void registerAccept(SelectableChannel channel, Selectable selectable)
    {
        register(channel, SelectionKey.OP_ACCEPT, selectable);
    }

    private void register(SelectableChannel channel, int op, Selectable selectable)
    {
        try
        {
            channel.register(selector, op, selectable);
        }
        catch (ClosedChannelException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void registerRead(SelectableChannel channel, Selectable selectable)
    {
        register(channel, SelectionKey.OP_READ, selectable);
    }

    public void registerWrite(SelectableChannel channel, Selectable selectable)
    {
        register(channel, SelectionKey.OP_WRITE, selectable);
    }

    public void registerConnect(SelectableChannel channel, Selectable selectable)
    {
        register(channel, SelectionKey.OP_CONNECT, selectable);
    }
}
