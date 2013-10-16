
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from ice.idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/

#ifndef iceSupport_1627646664_h
#define iceSupport_1627646664_h

/* Uses */
#include "ice.h"



#ifdef __cplusplus
#ifndef ndds_cpp_h
  #include "ndds/ndds_cpp.h"
#endif
#else
#ifndef ndds_c_h
  #include "ndds/ndds_c.h"
#endif
#endif


namespace ice{
        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(ImageTypeSupport, Image);

DDS_DATAWRITER_CPP(ImageDataWriter, Image);
DDS_DATAREADER_CPP(ImageDataReader, ImageSeq, Image);


#else

DDS_TYPESUPPORT_C(ImageTypeSupport, Image);
DDS_DATAWRITER_C(ImageDataWriter, Image);
DDS_DATAREADER_C(ImageDataReader, ImageSeq, Image);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(DeviceIdentityTypeSupport, DeviceIdentity);

DDS_DATAWRITER_CPP(DeviceIdentityDataWriter, DeviceIdentity);
DDS_DATAREADER_CPP(DeviceIdentityDataReader, DeviceIdentitySeq, DeviceIdentity);


#else

DDS_TYPESUPPORT_C(DeviceIdentityTypeSupport, DeviceIdentity);
DDS_DATAWRITER_C(DeviceIdentityDataWriter, DeviceIdentity);
DDS_DATAREADER_C(DeviceIdentityDataReader, DeviceIdentitySeq, DeviceIdentity);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(DeviceConnectivityTypeSupport, DeviceConnectivity);

DDS_DATAWRITER_CPP(DeviceConnectivityDataWriter, DeviceConnectivity);
DDS_DATAREADER_CPP(DeviceConnectivityDataReader, DeviceConnectivitySeq, DeviceConnectivity);


#else

DDS_TYPESUPPORT_C(DeviceConnectivityTypeSupport, DeviceConnectivity);
DDS_DATAWRITER_C(DeviceConnectivityDataWriter, DeviceConnectivity);
DDS_DATAREADER_C(DeviceConnectivityDataReader, DeviceConnectivitySeq, DeviceConnectivity);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(DeviceConnectivityObjectiveTypeSupport, DeviceConnectivityObjective);

DDS_DATAWRITER_CPP(DeviceConnectivityObjectiveDataWriter, DeviceConnectivityObjective);
DDS_DATAREADER_CPP(DeviceConnectivityObjectiveDataReader, DeviceConnectivityObjectiveSeq, DeviceConnectivityObjective);


#else

DDS_TYPESUPPORT_C(DeviceConnectivityObjectiveTypeSupport, DeviceConnectivityObjective);
DDS_DATAWRITER_C(DeviceConnectivityObjectiveDataWriter, DeviceConnectivityObjective);
DDS_DATAREADER_C(DeviceConnectivityObjectiveDataReader, DeviceConnectivityObjectiveSeq, DeviceConnectivityObjective);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(NumericTypeSupport, Numeric);

DDS_DATAWRITER_CPP(NumericDataWriter, Numeric);
DDS_DATAREADER_CPP(NumericDataReader, NumericSeq, Numeric);


#else

DDS_TYPESUPPORT_C(NumericTypeSupport, Numeric);
DDS_DATAWRITER_C(NumericDataWriter, Numeric);
DDS_DATAREADER_C(NumericDataReader, NumericSeq, Numeric);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(SampleArrayTypeSupport, SampleArray);

DDS_DATAWRITER_CPP(SampleArrayDataWriter, SampleArray);
DDS_DATAREADER_CPP(SampleArrayDataReader, SampleArraySeq, SampleArray);


#else

DDS_TYPESUPPORT_C(SampleArrayTypeSupport, SampleArray);
DDS_DATAWRITER_C(SampleArrayDataWriter, SampleArray);
DDS_DATAREADER_C(SampleArrayDataReader, SampleArraySeq, SampleArray);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(TextTypeSupport, Text);

DDS_DATAWRITER_CPP(TextDataWriter, Text);
DDS_DATAREADER_CPP(TextDataReader, TextSeq, Text);


#else

DDS_TYPESUPPORT_C(TextTypeSupport, Text);
DDS_DATAWRITER_C(TextDataWriter, Text);
DDS_DATAREADER_C(TextDataReader, TextSeq, Text);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(InfusionObjectiveTypeSupport, InfusionObjective);

DDS_DATAWRITER_CPP(InfusionObjectiveDataWriter, InfusionObjective);
DDS_DATAREADER_CPP(InfusionObjectiveDataReader, InfusionObjectiveSeq, InfusionObjective);


#else

DDS_TYPESUPPORT_C(InfusionObjectiveTypeSupport, InfusionObjective);
DDS_DATAWRITER_C(InfusionObjectiveDataWriter, InfusionObjective);
DDS_DATAREADER_C(InfusionObjectiveDataReader, InfusionObjectiveSeq, InfusionObjective);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(InfusionStatusTypeSupport, InfusionStatus);

DDS_DATAWRITER_CPP(InfusionStatusDataWriter, InfusionStatus);
DDS_DATAREADER_CPP(InfusionStatusDataReader, InfusionStatusSeq, InfusionStatus);


#else

DDS_TYPESUPPORT_C(InfusionStatusTypeSupport, InfusionStatus);
DDS_DATAWRITER_C(InfusionStatusDataWriter, InfusionStatus);
DDS_DATAREADER_C(InfusionStatusDataReader, InfusionStatusSeq, InfusionStatus);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(AlarmSettingsTypeSupport, AlarmSettings);

DDS_DATAWRITER_CPP(AlarmSettingsDataWriter, AlarmSettings);
DDS_DATAREADER_CPP(AlarmSettingsDataReader, AlarmSettingsSeq, AlarmSettings);


#else

DDS_TYPESUPPORT_C(AlarmSettingsTypeSupport, AlarmSettings);
DDS_DATAWRITER_C(AlarmSettingsDataWriter, AlarmSettings);
DDS_DATAREADER_C(AlarmSettingsDataReader, AlarmSettingsSeq, AlarmSettings);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(GlobalAlarmSettingsObjectiveTypeSupport, GlobalAlarmSettingsObjective);

DDS_DATAWRITER_CPP(GlobalAlarmSettingsObjectiveDataWriter, GlobalAlarmSettingsObjective);
DDS_DATAREADER_CPP(GlobalAlarmSettingsObjectiveDataReader, GlobalAlarmSettingsObjectiveSeq, GlobalAlarmSettingsObjective);


#else

DDS_TYPESUPPORT_C(GlobalAlarmSettingsObjectiveTypeSupport, GlobalAlarmSettingsObjective);
DDS_DATAWRITER_C(GlobalAlarmSettingsObjectiveDataWriter, GlobalAlarmSettingsObjective);
DDS_DATAREADER_C(GlobalAlarmSettingsObjectiveDataReader, GlobalAlarmSettingsObjectiveSeq, GlobalAlarmSettingsObjective);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

        

/* ========================================================================= */
/**
   Uses:     T

   Defines:  TTypeSupport, TDataWriter, TDataReader

   Organized using the well-documented "Generics Pattern" for
   implementing generics in C and C++.
*/

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)

#ifdef __cplusplus
  /* If we're building on Windows, explicitly import the superclasses of
   * the types declared below.
   */        
  class __declspec(dllimport) ::DDSTypeSupport;
  class __declspec(dllimport) ::DDSDataWriter;
  class __declspec(dllimport) ::DDSDataReader;
#endif

#endif

#ifdef __cplusplus

DDS_TYPESUPPORT_CPP(LocalAlarmSettingsObjectiveTypeSupport, LocalAlarmSettingsObjective);

DDS_DATAWRITER_CPP(LocalAlarmSettingsObjectiveDataWriter, LocalAlarmSettingsObjective);
DDS_DATAREADER_CPP(LocalAlarmSettingsObjectiveDataReader, LocalAlarmSettingsObjectiveSeq, LocalAlarmSettingsObjective);


#else

DDS_TYPESUPPORT_C(LocalAlarmSettingsObjectiveTypeSupport, LocalAlarmSettingsObjective);
DDS_DATAWRITER_C(LocalAlarmSettingsObjectiveDataWriter, LocalAlarmSettingsObjective);
DDS_DATAREADER_C(LocalAlarmSettingsObjectiveDataReader, LocalAlarmSettingsObjectiveSeq, LocalAlarmSettingsObjective);

#endif

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif


} /* namespace ice */


#endif  /* iceSupport_1627646664_h */
