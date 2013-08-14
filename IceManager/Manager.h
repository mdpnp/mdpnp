#ifndef MANAGER_H
#define MANAGER_H

#include <QObject>
#include <QList>
#include <QImage>
#include <QAbstractTableModel>
#include <ndds/ndds_cpp.h>

class Manager : public QAbstractTableModel
{
    Q_OBJECT
public:
    explicit Manager(DDSDomainParticipant* participant, const char *topic, QObject *parent = 0);
    virtual ~Manager();
    int rowCount(const QModelIndex &parent) const;
    int columnCount(const QModelIndex &parent) const;
    QVariant data(const QModelIndex &index, int role) const;
    QVariant headerData(int section, Qt::Orientation orientation, int role) const;
//    bool insertRows(int row, int count, const QModelIndex &parent);

signals:

public slots:
    void wait();
private:
    DDSDomainParticipant *participant;
    DDSWaitSet waitSet;
    QList<DDS_DynamicData*> list;
    const char * topicName;

    // Deal with publication subscription
    DDSPublicationBuiltinTopicDataDataReader *publicationReader;
    DDSReadCondition *publicationReadCondition;

    // Deal with subscription for actual data
    DDSSubscriber *dataSubscriber;
    DDSTopicDescription *dataTopic;
    DDSDynamicDataReader *dataReader;
    DDSDynamicDataTypeSupport *dataTypeSupport;
    DDSReadCondition *dataReadCondition;

};

#endif // MANAGER_H
