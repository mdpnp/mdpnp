#include "PubManager.h"

PubManager::PubManager(DDSDomainParticipant* participant_, QObject *parent) :
    QObject(parent), participant(participant_), waitSet(),
    publicationReader(NULL), publicationAliveCondition(NULL),
    publicationDeadCondition(NULL)
{
    DDSPublicationBuiltinTopicDataTypeSupport::register_type(participant);
    publicationReader = (DDSPublicationBuiltinTopicDataDataReader*) participant->get_builtin_subscriber()->lookup_datareader(DDS_PUBLICATION_TOPIC_NAME);

    publicationAliveCondition = publicationReader->create_readcondition(DDS_ANY_SAMPLE_STATE, DDS_ANY_VIEW_STATE, DDS_ALIVE_INSTANCE_STATE);
    publicationDeadCondition = publicationReader->create_readcondition(DDS_ANY_SAMPLE_STATE, DDS_ANY_VIEW_STATE, DDS_NOT_ALIVE_DISPOSED_INSTANCE_STATE | DDS_NOT_ALIVE_NO_WRITERS_INSTANCE_STATE);
    waitSet.attach_condition(publicationAliveCondition);
    waitSet.attach_condition(publicationDeadCondition);
}

PubManager::~PubManager() {
    waitSet.detach_condition(publicationAliveCondition);
    waitSet.detach_condition(publicationDeadCondition);

    publicationReader->delete_readcondition(publicationAliveCondition);
    publicationReader->delete_readcondition(publicationDeadCondition);

    DDSPublicationBuiltinTopicDataTypeSupport::unregister_type(participant);

}

void PubManager::wait() {
    static DDSConditionSeq cond_seq;
    static DDS_PublicationBuiltinTopicDataSeq data_seq;
    static DDS_SampleInfoSeq info_seq;

    DDS_ReturnCode_t retcode = waitSet.wait(cond_seq, DDS_DURATION_ZERO);

    if(retcode == DDS_RETCODE_TIMEOUT) {
        return;
    }

    if(retcode != DDS_RETCODE_OK) {
        return;
//        std::cerr << "ERROR IN WAITSET " << retcode << std::endl;
    }

    for(int i = 0; i < cond_seq.length(); ++i) {

        if(cond_seq[i] == publicationAliveCondition) {
            if(DDS_RETCODE_OK == publicationReader->take_w_condition(data_seq, info_seq, DDS_LENGTH_UNLIMITED, publicationAliveCondition)) {
                for(int j = 0; j < data_seq.length(); ++j) {
                    if(info_seq[j].valid_data) {
                        alivePublication(data_seq[j]);
                    }
                }
            }
        } else if(cond_seq[i] == publicationDeadCondition) {
            if(DDS_RETCODE_OK == publicationReader->take_w_condition(data_seq, info_seq, DDS_LENGTH_UNLIMITED, publicationAliveCondition)) {
                for(int j = 0; j < data_seq.length(); ++j) {
                    notAlivePublication();
                }
            }
        }
    }
}


Foo::Foo(QObject* parent) : QObject(parent) {

}

Foo::~Foo() {
}


void Foo::print(DDS_PublicationBuiltinTopicData& data) {
    DDS_PublicationBuiltinTopicDataTypeSupport_print_data(&data);
}

