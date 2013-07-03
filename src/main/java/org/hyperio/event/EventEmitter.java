package org.hyperio.event;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.hyperio.core.Callback;

public class EventEmitter
{
    private Multimap<String, Callback<Event>> callbacks;

    public EventEmitter()
    {
        callbacks = LinkedListMultimap.create();
    }

    public void on(String eventName, Callback<Event> callback)
    {
        callbacks.put(eventName, callback);
    }

    public void fire(String event)
    {
        fire(event, null);
    }

    public void fire(String eventName, Event event)
    {
        for (Callback<Event> callback : callbacks.get(eventName))
        {
            callback.call(event);
        }
    }

    public void fire(String eventName, Object source, Object... args)
    {
        Event event = new Event();
        for (int i = 0; i < args.length; i += 2)
        {
            event.put(args[i].toString(), args[i + 1]);
        }
        fire(eventName, event);
    }

    public void remove(String eventName, Event event)
    {
        callbacks.remove(eventName, event);
    }

    public void removeAll(String event)
    {
        callbacks.removeAll(event);
    }
}
