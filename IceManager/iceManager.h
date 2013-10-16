#ifndef DEVICE_IDENTITY_MANAGER_H
#define DEVICE_IDENTITY_MANAGER_H

#include <QObject>
#include <QList>
#include <QImage>
#include <QAbstractTableModel>
#include <ndds/ndds_cpp.h>
#include "device.h"

class DeviceIdentityManager : public QAbstractTableModel
{
    Q_OBJECT
public:
    explicit DeviceIdentityManager(DDSSubscriber* subscriber, QObject *parent = 0);
    virtual ~DeviceIdentityManager();
    int rowCount(const QModelIndex &parent) const;
    int columnCount(const QModelIndex &parent) const;
    QVariant data(const QModelIndex &index, int role) const;
    QVariant headerData(int section, Qt::Orientation orientation, int role) const;
    bool insertRows(int row, int count, const QModelIndex &parent);
signals:
    void instanceAlive(DDS_InstanceHandle_t& handle, ice::DeviceIdentity* sample);
    void instanceDisposed(DDS_InstanceHandle_t& handle);
    void instanceNotAlive(DDS_InstanceHandle_t& handle);
public slots:
    void wait();
private:
    DDSSubscriber* subscriber;
    DDSTopic *topic;
    DDSDataReader * reader;
    ice::DeviceIdentityDataReader *device_reader;
    DDSWaitSet waitSet;
    DDSReadCondition *_instanceAlive;
    DDSReadCondition *_instanceDisposed;
    DDSReadCondition *_instanceNoWriters;
    QList<ice::DeviceIdentity*> list;
    QImage *testImage;
};

#endif // DEVICE_IDENTITY_MANAGER_H
