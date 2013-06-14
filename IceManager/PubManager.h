#ifndef PUBMANAGER_H
#define PUBMANAGER_H

#include <QObject>
#include <ndds/ndds_cpp.h>

class PubManager : public QObject
{
    Q_OBJECT
public:
    explicit PubManager(DDSDomainParticipant* participant, QObject *parent = 0);
    virtual ~PubManager();
signals:
    void alivePublication(DDS_PublicationBuiltinTopicData& data);
    void notAlivePublication();
public slots:
    void wait();
private:
    DDSDomainParticipant *participant;
    DDSWaitSet waitSet;

    // Deal with publication subscription
    DDSPublicationBuiltinTopicDataDataReader *publicationReader;
    DDSReadCondition *publicationAliveCondition;
    DDSReadCondition *publicationDeadCondition;
};

class Foo : public QObject
{
    Q_OBJECT

public:
    explicit Foo(QObject* parent = 0);
    virtual ~Foo();
public slots:
    void print(DDS_PublicationBuiltinTopicData& data);
};

#endif // PUBMANAGER_H
