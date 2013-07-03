package org.hyperio.event;

import java.util.HashMap;

public class Event extends HashMap<String, Object>
{
    private Object sender;

    public Event()
    {
    }

    public Event(Object sender)
    {
        this.sender = sender;
    }

    public Event(Object... args)
    {
        for (int i = 0; i < args.length - 1; i += 2)
        {
            put(args[i].toString(), args[i + 1]);
        }
    }
}
