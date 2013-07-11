#include "DeviceIdentityManager.h"
#include "deviceSupport.h"
#include <iostream>
#include <sstream>
#include <iomanip>
#include <QPixmap>
#include <QDebug>
DeviceIdentityManager::DeviceIdentityManager(DDSSubscriber* subscriber_, QObject *parent) :
    QAbstractTableModel(parent), subscriber(subscriber_), testImage(NULL)
{
    DDSDomainParticipant *participant = subscriber->get_participant();
    if(DDS_RETCODE_OK != ice::DeviceIdentity::TypeSupport::register_type(participant, ice::DeviceIdentity::TypeSupport::get_type_name())) {
        throw new std::exception();
    }
    topic = participant->create_topic(ice::DeviceIdentityTopic, ice::DeviceIdentity::TypeSupport::get_type_name(), DDS_TOPIC_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);
    if(NULL == topic) {
        throw new std::exception();
    }
    reader = subscriber->create_datareader(topic, DDS_DATAREADER_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);
    device_reader = ice::DeviceIdentityDataReader::narrow(reader);


    _instanceAlive = reader->create_readcondition(DDS_ANY_SAMPLE_STATE, DDS_ANY_VIEW_STATE, DDS_ALIVE_INSTANCE_STATE);
    _instanceDisposed = reader->create_readcondition(DDS_ANY_SAMPLE_STATE, DDS_ANY_VIEW_STATE, DDS_NOT_ALIVE_DISPOSED_INSTANCE_STATE);
    _instanceNoWriters = reader->create_readcondition(DDS_ANY_SAMPLE_STATE, DDS_ANY_VIEW_STATE, DDS_NOT_ALIVE_NO_WRITERS_INSTANCE_STATE);

    waitSet.attach_condition(_instanceAlive);
    waitSet.attach_condition(_instanceDisposed);
    waitSet.attach_condition(_instanceNoWriters);

    testImage = new QImage();
    if(testImage->load("pulseox.png")) {

    } else {
        qDebug() << "Image not opened!";
        delete testImage;
        testImage = NULL;
    }

}

DeviceIdentityManager::~DeviceIdentityManager() {
    waitSet.detach_condition(_instanceAlive);
    waitSet.detach_condition(_instanceDisposed);
    waitSet.detach_condition(_instanceNoWriters);

    reader->delete_readcondition(_instanceAlive);
    _instanceAlive = NULL;
    reader->delete_readcondition(_instanceDisposed);
    _instanceDisposed = NULL;
    reader->delete_readcondition(_instanceNoWriters);
    _instanceNoWriters = NULL;

    subscriber->delete_datareader(reader);
    reader = NULL;
    device_reader = NULL;

    subscriber->get_participant()->delete_topic(topic);
    topic = NULL;

    while(!list.isEmpty()) {
        ice::DeviceIdentity* di = list.at(0);
        list.removeAt(0);
        DeviceIdentity_finalize(di);
        delete di;
    }
    delete testImage;
    testImage = NULL;
}



void DeviceIdentityManager::wait() {
    static DDSConditionSeq seq;
    static ice::DeviceIdentitySeq deviceIds;
    static DDS_SampleInfoSeq info_seq;

    DDS_ReturnCode_t retcode = waitSet.wait(seq, DDS_DURATION_ZERO);

    if(retcode == DDS_RETCODE_TIMEOUT) {
        return;
    }

    if(retcode != DDS_RETCODE_OK) {
        std::cerr << "ERROR IN WAITSET " << retcode << std::endl;
    }
    DDS_InstanceHandle_t handle;

    std::cerr << "Count:" << seq.length() << std::endl;
    for(int i = 0; i < seq.length(); ++i) {

        if(seq[i] == _instanceAlive) {
            if(DDS_RETCODE_OK == device_reader->take_w_condition(deviceIds, info_seq, DDS_LENGTH_UNLIMITED, _instanceAlive)) {
                for(int j = 0; j < deviceIds.length(); ++j) {
                    instanceAlive(info_seq[j].instance_handle, &deviceIds[j]);
                    beginInsertRows(QModelIndex(), 0, 0);
                    std::cout << "inserting " << deviceIds[j].manufacturer << std::endl;
                    ice::DeviceIdentity *di = new ice::DeviceIdentity();
                    ice::DeviceIdentity_initialize(di);
                    ice::DeviceIdentity_copy(di, &deviceIds[j]);
                    list.insert(0, di);
                    endInsertRows();
//                    dataChanged(index(0,0), index(list.size(),2));
                    for(int k = 0; k < list.size(); k++) {
                        std::cout << k << "\t" << list.at(k)->manufacturer << std::endl;
//                        DeviceIdentity::TypeSupport::print_data(&list.at(k));
                    }
                }
            }
            device_reader->return_loan(deviceIds, info_seq);
            std::cerr << "INSTANCE ALIVE" << std::endl;
        } else if(seq[i] == _instanceDisposed) {
            if(DDS_RETCODE_OK == device_reader->take_w_condition(deviceIds, info_seq, DDS_LENGTH_UNLIMITED, _instanceDisposed)) {
                for(int j = 0; j < deviceIds.length(); ++j) {
                    instanceDisposed(info_seq[j].instance_handle);
                    for(int k = 0; k < list.length(); k++) {
                        handle = device_reader->lookup_instance(*list.at(k));
//                        device_reader->get_key_value((DeviceIdentity&)list.at(k), handle);
                        if(DDS_InstanceHandle_equals(&handle, &info_seq[j].instance_handle)) {
                            beginRemoveRows(QModelIndex(), k, k);
                            list.removeAt(k);
                            endRemoveRows();
                            break;
                        }
                    }
                }
            }
            device_reader->return_loan(deviceIds, info_seq);
            std::cerr << "INSTANCE DISPOSED" << std::endl;
        } else if(seq[i] == _instanceNoWriters) {
            if(DDS_RETCODE_OK == device_reader->take_w_condition(deviceIds, info_seq, DDS_LENGTH_UNLIMITED, _instanceNoWriters)) {
                for(int j = 0; j < deviceIds.length(); ++j) {
                    instanceNotAlive(info_seq[j].instance_handle);
                    for(int k = 0; k < list.length(); k++) {
                        handle = device_reader->lookup_instance(*list.at(k));
//                        device_reader->get_key_value((DeviceIdentity&)list.at(k), handle);
                        if(DDS_InstanceHandle_equals(&handle, &info_seq[j].instance_handle)) {
                            beginRemoveRows(QModelIndex(), k, k);
                            list.removeAt(k);
                            endRemoveRows();
                            break;
                        }
                    }
                }
            }
            device_reader->return_loan(deviceIds, info_seq);
            std::cerr << "INSTANCE NO WRITERS" << std::endl;

        }
    }
}

int DeviceIdentityManager::rowCount(const QModelIndex &) const
{
//    std::cout << "rowCount:" << list.size() << std::endl;
    return list.size();
//    return 1;
}

int DeviceIdentityManager::columnCount(const QModelIndex &) const
{
        return 4;

}

QVariant DeviceIdentityManager::headerData(int section, Qt::Orientation, int role) const
{
    switch(role) {
    case Qt::DisplayRole:
        switch(section)
        {
        case 0:
            return "UDI";
        case 1:
            return "Manufacturer";
        case 2:
            return "Model";
        case 3:
            return "Icon";
        default:
            return QVariant();
        }
    default:
        return QVariant();
    }
}

bool DeviceIdentityManager::insertRows(int /*row*/, int /*count*/, const QModelIndex &/*parent*/) {
    return false;
}

QString hexStr(DDS_Octet udi[])
{
    std::stringstream ss;
    ss<<std::hex;
    int len = sizeof(udi) / sizeof(DDS_Octet);
    for(int i(0);i<len;++i)
        ss<<(int)udi[i];
    return QString::fromStdString(ss.str());

}

QVariant DeviceIdentityManager::data(const QModelIndex &index, int role) const
{
//    std::cout << "Data Slot:" << index.row() << "," << index.column() << " " << role << std::endl;
    switch(role) {
    case Qt::DisplayRole:
        if(index.row()<list.length()) {
            switch(index.column()) {
            case 0:
                return QVariant(list.at(index.row())->universal_device_identifier);
            case 1:
                return QVariant(list.at(index.row())->manufacturer);
            case 2:
                return QVariant(list.at(index.row())->model);
            case 3:
            default:
                return QVariant();
            }
        }
    case Qt::DecorationRole:
        switch(index.column()) {
        case 3:
            return NULL == testImage ? QVariant() : QPixmap::fromImage(*testImage).scaled(50,50);
        }

    default:
        return QVariant();
    }
}

