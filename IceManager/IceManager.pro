#-------------------------------------------------
#
# Project created by QtCreator 2013-06-05T11:18:27
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = ICEManager
TEMPLATE = app
CONFIG += static

SOURCES += main.cpp\
    DeviceIdentityManager.cpp \
    form.cpp \
    Manager.cpp \
    PubManager.cpp \
    iceSupport.cxx \
    ice.cxx \
    icePlugin.cxx

HEADERS  +=\
    form.h \
    Manager.h \
    PubManager.h \
    iceSupport.h \
    icePlugin.h \
    ice.h \
    iceManager.h

FORMS    += widget.ui \
    form.ui

# QMAKE_LFLAGS += -Xlinker -Bstatic
# QMAKE_LFLAGS += -Xlinker -static
# QMAKE_LFLAGS += -static-libgcc -static-libstdc++

#DEFINES += RTI_CPP

unix {
  DEFINES += RTI_UNIX
}

# http://stackoverflow.com/questions/3984104/qmake-how-to-copy-a-file-to-the-output
OTHER_FILES += USER_QOS_PROFILES.xml \
ice.idl

# Copy qml files post build
win32 {
    DESTDIR_WIN = $${DESTDIR}
    DESTDIR_WIN ~= s,/,\\,g
    PWD_WIN = $${PWD}
    PWD_WIN ~= s,/,\\,g
    for(FILE, OTHER_FILES) {
        QMAKE_POST_LINK += $$quote(cmd /c copy /y $${PWD_WIN}\\$${FILE} $${DESTDIR_WIN}$$escape_expand(\\n\\t))
    }
}

unix {
  message ($(NDDSHOME))
  INCLUDEPATH += $(NDDSHOME)/include $(NDDSHOME)/include/ndds
  DEPENDPATH += $(NDDSHOME)/include $(NDDSHOME)/include/ndds
    for(FILE, OTHER_FILES) {
        message ($${OUT_PWD})
        QMAKE_POST_LINK += $$quote(cp $${PWD}/$${FILE} $${OUT_PWD}$$escape_expand(\n\t))
    }
}
message (HELLO WORLD)
win* {
#  message (HELLO WINDOWS)
#  NDDSHOME_WIN = $(NDDSHOME)
#  NDDSHOME_WIN = $$replace(NDDSHOME_WIN, \, /)
#  message (NDDSHOME_WIN=$${NDDSHOME_WIN})
  INCLUDEPATH += $(NDDSHOME)\\include $(NDDSHOME_WIN)\\include\\ndds
#  DEPENDPATH += $${NDDSHOME_WIN}/include $${NDDSHOME_WIN}/include/ndds
  message (INCLUDEPATH=$${INCLUDEPATH})
  DEFINES += WIN32
  DEFINES += RTI_WIN32
  DEFINES += _CONSOLE
  LIBS += -L$(NDDSHOME)\\lib\\i86Win32VS2010 -lnddscppz -lnddscz -lnddscorez
  LIBS += -lnetapi32 -ladvapi32 -luser32 -lWS2_32
  message (libs=$${LIBS})
}

linux {
  LIBS += -L$(NDDSHOME)/lib/x64Linux2.6gcc4.4.3/ -lnddscpp -lnddsc -lnddscore
  DEFINES += RTI_LINUX
  DEFINES += RTI_64BIT
  LIBS += -ldl -lnsl -lm -lpthread -lrt
}

macx {
  LIBS += -L$(NDDSHOME)/lib/x64Darwin10gcc4.2.1/ -lnddscppz -lnddscz -lnddscorez
  DEFINES += RTI_DARWIN
  DEFINES += RTI_DARWIN10
  DEFINES += RTI_64BIT
  CCFLAG += -m64
  CCFLAG += -static-libgcc
  LIBS += -ldl -lm -lpthread
  APP_QOS_FILES.files = USER_QOS_PROFILES.xml
  APP_QOS_FILES.path = Contents/MacOS
  QMAKE_BUNDLE_DATA += APP_QOS_FILES
  PRE_TARGETDEPS += $(NDDSHOME)/lib/x64Darwin10gcc4.2.1/libnddscppz.a
  PRE_TARGETDEPS += $(NDDSHOME)/lib/x64Darwin10gcc4.2.1/libnddscorez.a
}


