package org.hyperio.core;

import java.nio.channels.SelectionKey;

public interface Selectable
{
    public void select(SelectionKey key);
}
