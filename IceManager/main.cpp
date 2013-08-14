#include "form.h"
#include <QApplication>
#include <ndds/ndds_cpp.h>
#include <iostream>
#include "device.h"
#include "deviceSupport.h"
#include <QThread>
#include <QTimer>
#include <QObject>
#include "DeviceIdentityManager.h"
#include "Manager.h"
#include "PubManager.h"





int main(int argc, char *argv[])
{
//   NDDSConfigLogger::get_instance()->set_verbosity(NDDS_Config_LogVerbosity::NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
//   NDDS_Config_Logger_set_verbosity(NDDS_Config_Logger_get_instance(), NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
    QApplication a(argc, argv);
    Form w;
    a.setApplicationName("ICE Manager");
    w.show();
    
    DDSDomainParticipant *participant = DDSTheParticipantFactory->create_participant(0, DDS_PARTICIPANT_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);
//    DDSSubscriber *subscriber = participant->create_subscriber(DDS_SUBSCRIBER_QOS_DEFAULT, NULL, DDS_STATUS_MASK_NONE);

    {
//        DeviceIdentityManager dim(subscriber);
        Manager dim1(participant, ice::DeviceIdentityTopic);
//        Manager dim2(participant, ice::DeviceConnectivityTopic);
//        Foo foo;
//        PubManager pm(participant);
//        QObject::connect(&pm, SIGNAL(alivePublication(DDS_PublicationBuiltinTopicData&)), &foo, SLOT(print(DDS_PublicationBuiltinTopicData&)));
        QTimer timer1;
//        DeviceIdentityManager *dim = new DeviceIdentityManager(subscriber);
//        QTimer *timer = new QTimer();
        timer1.setSingleShot(false);
        timer1.setInterval(100);
//        QObject::connect(&timer1, SIGNAL(timeout()), &pm, SLOT(wait()));
        QObject::connect(&timer1, SIGNAL(timeout()), &dim1, SLOT(wait()));
//        QTimer timer2;
//        timer2.setSingleShot(false);
//        timer2.setInterval(100);
//        QObject::connect(&timer2, SIGNAL(timeout()), &dim2, SLOT(wait()));
        w.setModel(&dim1);
//        w.setModel2(&dim2);
//        QObject::connect(&dim, SIGNAL(instanceAlive(DDS_InstanceHandle_t&,DeviceIdentity*)), &w, SLOT(instanceAlive(DDS_InstanceHandle_t&,DeviceIdentity*)));
        timer1.start();
//        timer2.start();

        a.exec();

        timer1.stop();
//        timer2.stop();
    }
//    delete timer;
//    delete dim;

    std::cerr << "Deleting the participant" << std::endl;
    participant->delete_contained_entities();
    DDSTheParticipantFactory->delete_participant(participant);
    DDSTheParticipantFactory->finalize_instance();
    std::cerr << "Really Exiting" << std::endl;
    return 0;
}
