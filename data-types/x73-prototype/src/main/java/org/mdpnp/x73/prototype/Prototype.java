package org.mdpnp.x73.prototype;

import org.mdpnp.rti.dds.DDS;
import org.mdpnp.types.normalized.NuObsValue;
import org.mdpnp.types.normalized.NuObsValueTypeCode;
import org.mdpnp.types.normalized.NuObsValueTypeSupport;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.StatusKind;

public class Prototype {
	public static void main(String[] args) {
		if(!DDS.init()) {
			System.err.println("Unable to init DDS");
			return;
		}
		

	   DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(
                0,
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, 
                null,
                StatusKind.STATUS_MASK_NONE);
	   
	   participant.register_type(NuObsValueTypeSupport.get_type_name(), NuObsValueTypeSupport.get_instance(), null);
	   
        try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		DomainParticipantFactory.get_instance().delete_participant(participant);
	}
}
