package org.mdpnp.rtiapi.data;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.Publisher;

public interface WriterInstanceModel<D extends Copyable, W extends DataWriter> {
    void startWriter(Publisher publisher, String qosLibrary, String qosProfile);
    void startWriter(Publisher publisher);
    void stopWriter();

    W getWriter();
    
    void write(D data);
}
