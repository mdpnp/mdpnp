package org.mdpnp.devices.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface NetworkConnection {
    void read(SelectionKey sk) throws IOException;
    void write(SelectionKey sk) throws IOException;
    void registered(NetworkLoop networkLoop, SelectionKey sk);
    void unregistered(NetworkLoop networkLoop, SelectionKey sk);
}
