#include "Manager.h"

#include <iostream>
#include <sstream>
#include <iomanip>
#include <QPixmap>
#include <QDebug>
#include <string.h>
#include <stdio.h>


Manager::Manager(DDSDomainParticipant* participant_, const char *topic_, QObject *parent) :
    QAbstractTableModel(parent), participant(participant_), waitSet(), list(),
    publicationReader(NULL), publicationReadCondition(NULL),
    dataSubscriber(NULL), topicName(topic_), dataTopic(NULL),
    dataReader(NULL), dataTypeSupport(NULL), dataReadCondition(NULL)
{
//    DDS_StringSeq * params = new DDS_StringSeq(1);
    DDS_StringSeq params(1);
    params.ensure_length(1 , 1);
    int n = strlen(topic_) + 3;
    params[0] = DDS_String_alloc(n);
    snprintf(params[0], n, "'%s'", topic_);
    params[0][n] = '\0';

    DDSPublicationBuiltinTopicDataTypeSupport::register_type(participant);
    DDSStringTypeSupport::register_type(participant, DDSStringTypeSupport::get_type_name());
    publicationReader = (DDSPublicationBuiltinTopicDataDataReader*) participant->get_builtin_subscriber()->lookup_datareader(DDS_PUBLICATION_TOPIC_NAME);
//    DDSTopicDescription *td = participant->get_builtin_subscriber()->lookup_datareader(DDS_PUBLICATION_TOPIC_NAME)->get_topicdescription();
//    DDSTopic *topic = participant->create_topic(DDS_PUBLICATION_TOPIC_NAME, DDSPublicationBuiltinTopicDataTypeSupport::get_type_name(), DDS_TOPIC_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);
//    DDSSubscriber *subscriber = participant->create_subscriber(DDS_SUBSCRIBER_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);
//    publicationReader = (DDSPublicationBuiltinTopicDataDataReader*) subscriber->create_datareader(td, DDS_DATAREADER_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);
//    DDSContentFilteredTopic *mytopic = participant->create_contentfilteredtopic("MYTOPIC", topicReader->get_topicdescription(), "topic_name MATCH %0", params);
//    topicReader = participant->create_datareader(topic, DDS_DATAREADER_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);
    std::cerr << "Params(" << params.length() << "): " << params[0] << std::endl;

//    publicationReadCondition = publicationReader->create_querycondition(DDS_NOT_READ_SAMPLE_STATE, DDS_ANY_VIEW_STATE, DDS_ALIVE_INSTANCE_STATE, "topic_name = %0", params);
    publicationReadCondition = publicationReader->create_readcondition(DDS_NOT_READ_SAMPLE_STATE, DDS_ANY_VIEW_STATE, DDS_ALIVE_INSTANCE_STATE);
    waitSet.attach_condition(publicationReadCondition);

}

Manager::~Manager() {
    if(dataReader) {
        dataSubscriber->delete_datareader(dataReader);
        dataReader = NULL;
    }
    if(dataSubscriber) {
        participant->delete_subscriber(dataSubscriber);
        dataSubscriber = NULL;
    }

    while(!list.isEmpty()) {
        DDS_DynamicData* dd = list.at(0);
        list.removeAt(0);
        delete dd;
    }

}



void Manager::wait() {
    static DDSConditionSeq seq;
    static DDS_PublicationBuiltinTopicDataSeq data_seq;
    static DDS_DynamicDataSeq mdata_seq;
    static DDS_SampleInfoSeq info_seq;

    DDS_ReturnCode_t retcode = waitSet.wait(seq, DDS_DURATION_ZERO);

    if(retcode == DDS_RETCODE_TIMEOUT) {
        return;
    }

    if(retcode != DDS_RETCODE_OK) {
        std::cerr << "ERROR IN WAITSET " << retcode << std::endl;
    }
    DDS_InstanceHandle_t handle;
    DDS_ExceptionCode_t ex;
    std::cerr << "Count:" << seq.length() << std::endl;
    for(int i = 0; i < seq.length(); ++i) {

        if(seq[i] == publicationReadCondition) {
            if(DDS_RETCODE_OK == publicationReader->read_w_condition(data_seq, info_seq, DDS_LENGTH_UNLIMITED, publicationReadCondition)) {
                for(int j = 0; j < data_seq.length(); ++j) {
                    if(info_seq[j].valid_data) {
//                    beginInsertRows(QModelIndex(), 0, 0);

//                    std::cout << "inserting " << deviceIds[j].manufacturer << std::endl;
                    DDS_PublicationBuiltinTopicData* d = &data_seq[j];
                    if(d->type_code != NULL && d->type_name != NULL && 0 == strcmp(d->topic_name, topicName)) {
                        std::cout << "Received topic: " << d->topic_name << std::endl;
                        d->type_code->print_IDL(1, ex);
                        if(NULL == dataTypeSupport) {
                            beginResetModel();
                            dataTypeSupport = new DDSDynamicDataTypeSupport(d->type_code, DDS_DYNAMIC_DATA_TYPE_PROPERTY_DEFAULT);
                            endResetModel();
        //                    topic = participant->lookup_topicdescription(d->topic_name);
                            dataTypeSupport->register_type(participant, d->type_name);
                            dataTopic = participant->create_topic(d->topic_name, d->type_name, DDS_TOPIC_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);
                            dataSubscriber = participant->create_subscriber(DDS_SUBSCRIBER_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);
                            dataReader = (DDSDynamicDataReader*) dataSubscriber->create_datareader(dataTopic, DDS_DATAREADER_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);
                            dataReadCondition = dataReader->create_readcondition(DDS_NOT_READ_SAMPLE_STATE, DDS_ANY_VIEW_STATE, DDS_ALIVE_INSTANCE_STATE);
                            waitSet.attach_condition(dataReadCondition);
                        }
                    } else {
                        std::cerr << "No typecode received for " << d->topic_name << std::endl;
                    }
                   }
//                    endInsertRows();
                }
            }
            publicationReader->return_loan(data_seq, info_seq);
        } else if(seq[i] == dataReadCondition) {
            if(DDS_RETCODE_OK == dataReader->take_w_condition(mdata_seq, info_seq, DDS_LENGTH_UNLIMITED, dataReadCondition)) {
                for(int j = 0; j < mdata_seq.length(); ++j) {
                    if(info_seq[j].valid_data) {
//                        mdata_seq[j].print(stdout, 1);
                        DDS_DynamicData *data = dataTypeSupport->create_data();
                        dataTypeSupport->copy_data(data, &mdata_seq[j]);
                        beginInsertRows(QModelIndex(), 0, 0);
                        char* str = new char[1024];
                        DDS_UnsignedLong length = 1024;
                        data->get_string(str, &length, NULL, 1);
                        std::cerr << "Insert data " << str << std::endl;
                        delete str;
                        list.insert(0, data);
                        endInsertRows();
                    }
                }
            }
            dataReader->return_loan(mdata_seq, info_seq);
        }
    }
}

int Manager::rowCount(const QModelIndex &) const
{
//    std::cout << "rowCount:" << list.size() << std::endl;
    return list.size();
//    return 1;
}

int Manager::columnCount(const QModelIndex &) const
{
    if(dataTypeSupport) {
        DDS_ExceptionCode_t ex;
        int cnt = dataTypeSupport->get_data_type()->member_count(ex);
        std::cerr << "count=" << cnt << std::endl;
        return cnt;
    } else {
        return 0;
    }
}

QVariant Manager::headerData(int section, Qt::Orientation, int role) const
{
    if(dataTypeSupport) {
        DDS_ExceptionCode_t ex;
        switch(role) {
        case Qt::DisplayRole:
            return dataTypeSupport->get_data_type()->member_name(section, ex);
        default:
            return QVariant();
        }
    } else {
        return QVariant();
    }
}

QVariant dynamicData(DDS_DynamicData* data, int column) {
    const DDS_TypeCode *tc = new DDS_TypeCode();
    DDS_DynamicDataMemberInfo mi;
    data->get_member_info_by_index(mi, column);

    data->get_member_type(tc, NULL, mi.member_id);
    DDS_ExceptionCode_t ex;
    unsigned int n;
    char * str;

    switch(tc->kind(ex)) {
        case DDS_TK_STRING:
            n = tc->length(ex) + 1;
            str = DDS_String_alloc(n);
            n--;
            data->get_string(str, &n, NULL, mi.member_id);
            return str;
        default:
            return QVariant();
    }
}

QVariant Manager::data(const QModelIndex &index, int role) const
{
//    std::cout << "Data Slot:" << index.row() << "," << index.column() << " " << role << std::endl;
    switch(role) {
    case Qt::DisplayRole:
        if(index.row()<list.length()) {
            return dynamicData(list.at(index.row()), index.column());
        }
    default:
        return QVariant();
    }
}


