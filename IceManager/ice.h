
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from ice.idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/

#ifndef ice_1627646664_h
#define ice_1627646664_h

#ifndef NDDS_STANDALONE_TYPE
    #ifdef __cplusplus
        #ifndef ndds_cpp_h
            #include "ndds/ndds_cpp.h"
        #endif
    #else
        #ifndef ndds_c_h
            #include "ndds/ndds_c.h"
        #endif
    #endif
#else
    #include "ndds_standalone_type.h"
#endif


namespace ice{

typedef char *  UniqueDeviceIdentifier;                
        
        
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols. */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* UniqueDeviceIdentifier_get_typecode(void); /* Type code */
    
DDS_SEQUENCE(UniqueDeviceIdentifierSeq, UniqueDeviceIdentifier);                                        
            
NDDSUSERDllExport
RTIBool UniqueDeviceIdentifier_initialize(
        UniqueDeviceIdentifier* self);
            
NDDSUSERDllExport
RTIBool UniqueDeviceIdentifier_initialize_ex(
        UniqueDeviceIdentifier* self,RTIBool allocatePointers,RTIBool allocateMemory);
                    
NDDSUSERDllExport
void UniqueDeviceIdentifier_finalize(
        UniqueDeviceIdentifier* self);
            
NDDSUSERDllExport
void UniqueDeviceIdentifier_finalize_ex(
        UniqueDeviceIdentifier* self,RTIBool deletePointers);
                    
NDDSUSERDllExport
RTIBool UniqueDeviceIdentifier_copy(
        UniqueDeviceIdentifier* dst,
        const UniqueDeviceIdentifier* src);

    

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols. */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif


typedef char *  MetricIdentifier;                
        
        
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols. */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* MetricIdentifier_get_typecode(void); /* Type code */
    
DDS_SEQUENCE(MetricIdentifierSeq, MetricIdentifier);                                        
            
NDDSUSERDllExport
RTIBool MetricIdentifier_initialize(
        MetricIdentifier* self);
            
NDDSUSERDllExport
RTIBool MetricIdentifier_initialize_ex(
        MetricIdentifier* self,RTIBool allocatePointers,RTIBool allocateMemory);
                    
NDDSUSERDllExport
void MetricIdentifier_finalize(
        MetricIdentifier* self);
            
NDDSUSERDllExport
void MetricIdentifier_finalize_ex(
        MetricIdentifier* self,RTIBool deletePointers);
                    
NDDSUSERDllExport
RTIBool MetricIdentifier_copy(
        MetricIdentifier* dst,
        const MetricIdentifier* src);

    

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols. */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif


typedef DDS_Long  InstanceIdentifier;
        
        
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols. */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* InstanceIdentifier_get_typecode(void); /* Type code */
    
DDS_SEQUENCE(InstanceIdentifierSeq, InstanceIdentifier);                                        
            
NDDSUSERDllExport
RTIBool InstanceIdentifier_initialize(
        InstanceIdentifier* self);
            
NDDSUSERDllExport
RTIBool InstanceIdentifier_initialize_ex(
        InstanceIdentifier* self,RTIBool allocatePointers,RTIBool allocateMemory);
                    
NDDSUSERDllExport
void InstanceIdentifier_finalize(
        InstanceIdentifier* self);
            
NDDSUSERDllExport
void InstanceIdentifier_finalize_ex(
        InstanceIdentifier* self,RTIBool deletePointers);
                    
NDDSUSERDllExport
RTIBool InstanceIdentifier_copy(
        InstanceIdentifier* dst,
        const InstanceIdentifier* src);

    

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols. */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif


        
extern const char *ImageTYPENAME;
        


#ifdef __cplusplus
    struct ImageSeq;

#ifndef NDDS_STANDALONE_TYPE
    class ImageTypeSupport;
    class ImageDataWriter;
    class ImageDataReader;
#endif

#endif

            
    
class Image                                        
{
public:            
#ifdef __cplusplus
    typedef struct ImageSeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef ImageTypeSupport TypeSupport;
    typedef ImageDataWriter DataWriter;
    typedef ImageDataReader DataReader;
#endif

#endif
    
     DDS_OctetSeq  raster;

    DDS_Long  width;

    DDS_Long  height;

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* Image_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(ImageSeq, Image);
        
NDDSUSERDllExport
RTIBool Image_initialize(
        Image* self);
        
NDDSUSERDllExport
RTIBool Image_initialize_ex(
        Image* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void Image_finalize(
        Image* self);
                        
NDDSUSERDllExport
void Image_finalize_ex(
        Image* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool Image_copy(
        Image* dst,
        const Image* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif


        
extern const char *DeviceIdentityTYPENAME;
        


#ifdef __cplusplus
    struct DeviceIdentitySeq;

#ifndef NDDS_STANDALONE_TYPE
    class DeviceIdentityTypeSupport;
    class DeviceIdentityDataWriter;
    class DeviceIdentityDataReader;
#endif

#endif

            
    
class DeviceIdentity                                        
{
public:            
#ifdef __cplusplus
    typedef struct DeviceIdentitySeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef DeviceIdentityTypeSupport TypeSupport;
    typedef DeviceIdentityDataWriter DataWriter;
    typedef DeviceIdentityDataReader DataReader;
#endif

#endif
    
    ice::UniqueDeviceIdentifier  unique_device_identifier;

    char*  manufacturer; /* maximum length = (128) */

    char*  model; /* maximum length = (128) */

    char*  serial_number; /* maximum length = (128) */

    ice::Image  icon;

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* DeviceIdentity_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(DeviceIdentitySeq, DeviceIdentity);
        
NDDSUSERDllExport
RTIBool DeviceIdentity_initialize(
        DeviceIdentity* self);
        
NDDSUSERDllExport
RTIBool DeviceIdentity_initialize_ex(
        DeviceIdentity* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void DeviceIdentity_finalize(
        DeviceIdentity* self);
                        
NDDSUSERDllExport
void DeviceIdentity_finalize_ex(
        DeviceIdentity* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool DeviceIdentity_copy(
        DeviceIdentity* dst,
        const DeviceIdentity* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

             
static const char * DeviceIdentityTopic = "ice::DeviceIdentity"; 
typedef enum ConnectionState
{
    Connected,
    Connecting,
    Negotiating,
    Disconnecting,
    Disconnected
} ConnectionState;
    

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* ConnectionState_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(ConnectionStateSeq, ConnectionState);
        
NDDSUSERDllExport
RTIBool ConnectionState_initialize(
        ConnectionState* self);
        
NDDSUSERDllExport
RTIBool ConnectionState_initialize_ex(
        ConnectionState* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void ConnectionState_finalize(
        ConnectionState* self);
                        
NDDSUSERDllExport
void ConnectionState_finalize_ex(
        ConnectionState* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool ConnectionState_copy(
        ConnectionState* dst,
        const ConnectionState* src);


NDDSUSERDllExport
RTIBool ConnectionState_getValues(ConnectionStateSeq * values);
    

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

 
typedef enum ConnectionType
{
    Serial,
    Simulated,
    Network
} ConnectionType;
    

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* ConnectionType_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(ConnectionTypeSeq, ConnectionType);
        
NDDSUSERDllExport
RTIBool ConnectionType_initialize(
        ConnectionType* self);
        
NDDSUSERDllExport
RTIBool ConnectionType_initialize_ex(
        ConnectionType* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void ConnectionType_finalize(
        ConnectionType* self);
                        
NDDSUSERDllExport
void ConnectionType_finalize_ex(
        ConnectionType* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool ConnectionType_copy(
        ConnectionType* dst,
        const ConnectionType* src);


NDDSUSERDllExport
RTIBool ConnectionType_getValues(ConnectionTypeSeq * values);
    

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif


        
extern const char *DeviceConnectivityTYPENAME;
        


#ifdef __cplusplus
    struct DeviceConnectivitySeq;

#ifndef NDDS_STANDALONE_TYPE
    class DeviceConnectivityTypeSupport;
    class DeviceConnectivityDataWriter;
    class DeviceConnectivityDataReader;
#endif

#endif

            
    
class DeviceConnectivity                                        
{
public:            
#ifdef __cplusplus
    typedef struct DeviceConnectivitySeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef DeviceConnectivityTypeSupport TypeSupport;
    typedef DeviceConnectivityDataWriter DataWriter;
    typedef DeviceConnectivityDataReader DataReader;
#endif

#endif
    
    ice::UniqueDeviceIdentifier  unique_device_identifier;

    ice::ConnectionState  state;

    ice::ConnectionType  type;

    char*  info; /* maximum length = (128) */

     DDS_StringSeq  valid_targets;

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* DeviceConnectivity_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(DeviceConnectivitySeq, DeviceConnectivity);
        
NDDSUSERDllExport
RTIBool DeviceConnectivity_initialize(
        DeviceConnectivity* self);
        
NDDSUSERDllExport
RTIBool DeviceConnectivity_initialize_ex(
        DeviceConnectivity* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void DeviceConnectivity_finalize(
        DeviceConnectivity* self);
                        
NDDSUSERDllExport
void DeviceConnectivity_finalize_ex(
        DeviceConnectivity* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool DeviceConnectivity_copy(
        DeviceConnectivity* dst,
        const DeviceConnectivity* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif


        
extern const char *DeviceConnectivityObjectiveTYPENAME;
        


#ifdef __cplusplus
    struct DeviceConnectivityObjectiveSeq;

#ifndef NDDS_STANDALONE_TYPE
    class DeviceConnectivityObjectiveTypeSupport;
    class DeviceConnectivityObjectiveDataWriter;
    class DeviceConnectivityObjectiveDataReader;
#endif

#endif

            
    
class DeviceConnectivityObjective                                        
{
public:            
#ifdef __cplusplus
    typedef struct DeviceConnectivityObjectiveSeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef DeviceConnectivityObjectiveTypeSupport TypeSupport;
    typedef DeviceConnectivityObjectiveDataWriter DataWriter;
    typedef DeviceConnectivityObjectiveDataReader DataReader;
#endif

#endif
    
    ice::UniqueDeviceIdentifier  unique_device_identifier;

    DDS_Boolean  connected;

    char*  target; /* maximum length = (128) */

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* DeviceConnectivityObjective_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(DeviceConnectivityObjectiveSeq, DeviceConnectivityObjective);
        
NDDSUSERDllExport
RTIBool DeviceConnectivityObjective_initialize(
        DeviceConnectivityObjective* self);
        
NDDSUSERDllExport
RTIBool DeviceConnectivityObjective_initialize_ex(
        DeviceConnectivityObjective* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void DeviceConnectivityObjective_finalize(
        DeviceConnectivityObjective* self);
                        
NDDSUSERDllExport
void DeviceConnectivityObjective_finalize_ex(
        DeviceConnectivityObjective* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool DeviceConnectivityObjective_copy(
        DeviceConnectivityObjective* dst,
        const DeviceConnectivityObjective* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

             
static const char * DeviceConnectivityTopic = "ice::DeviceConnectivity";             
static const char * DeviceConnectivityObjectiveTopic = "ice::DeviceConnectivityObjective";             
static const ice::MetricIdentifier MDC_PRESS_CUFF_NEXT_INFLATION = "MDC_PRESS_CUFF_NEXT_INFLATION";             
static const ice::MetricIdentifier MDC_PRESS_CUFF_INFLATION = "MDC_PRESS_CUFF_INFLATION";             
static const ice::MetricIdentifier MDC_HR_ECG_MODE = "MDC_HR_ECG_MODE";             
static const ice::MetricIdentifier MDC_RR_APNEA = "MDC_RR_APNEA";             
static const ice::MetricIdentifier MDC_SPO2_C_LOCK = "MDC_SPO2_C_LOCK";             
static const ice::MetricIdentifier MDC_TIME_PD_INSPIRATORY = "MDC_TIME_PD_INSPIRATORY";             
static const ice::MetricIdentifier MDC_TIME_MSEC_SINCE_EPOCH = "MDC_TIME_MSEC_SINCE_EPOCH";             
static const ice::MetricIdentifier MDC_CAPNOGRAPH = "MDC_CAPNOGRAPH";             
static const ice::MetricIdentifier MDC_START_OF_BREATH = "MDC_START_OF_BREATH";             
static const ice::MetricIdentifier MDC_VENT_TIME_PD_PPV = "MDC_VENT_TIME_PD_PPV";             
static const DDS_Long MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP = 6250;             
static const DDS_Long MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS = 6222;             
static const DDS_Long MDC_EVT_STAT_OFF = 6226;
        
extern const char *NumericTYPENAME;
        


#ifdef __cplusplus
    struct NumericSeq;

#ifndef NDDS_STANDALONE_TYPE
    class NumericTypeSupport;
    class NumericDataWriter;
    class NumericDataReader;
#endif

#endif

            
    
class Numeric                                        
{
public:            
#ifdef __cplusplus
    typedef struct NumericSeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef NumericTypeSupport TypeSupport;
    typedef NumericDataWriter DataWriter;
    typedef NumericDataReader DataReader;
#endif

#endif
    
    ice::UniqueDeviceIdentifier  unique_device_identifier;

    ice::MetricIdentifier  metric_id;

    ice::InstanceIdentifier  instance_id;

    DDS_Float  value;

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* Numeric_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(NumericSeq, Numeric);
        
NDDSUSERDllExport
RTIBool Numeric_initialize(
        Numeric* self);
        
NDDSUSERDllExport
RTIBool Numeric_initialize_ex(
        Numeric* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void Numeric_finalize(
        Numeric* self);
                        
NDDSUSERDllExport
void Numeric_finalize_ex(
        Numeric* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool Numeric_copy(
        Numeric* dst,
        const Numeric* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

             
static const char * NumericTopic = "ice::Numeric";
        
extern const char *SampleArrayTYPENAME;
        


#ifdef __cplusplus
    struct SampleArraySeq;

#ifndef NDDS_STANDALONE_TYPE
    class SampleArrayTypeSupport;
    class SampleArrayDataWriter;
    class SampleArrayDataReader;
#endif

#endif

            
    
class SampleArray                                        
{
public:            
#ifdef __cplusplus
    typedef struct SampleArraySeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef SampleArrayTypeSupport TypeSupport;
    typedef SampleArrayDataWriter DataWriter;
    typedef SampleArrayDataReader DataReader;
#endif

#endif
    
    ice::UniqueDeviceIdentifier  unique_device_identifier;

    ice::MetricIdentifier  metric_id;

    ice::InstanceIdentifier  instance_id;

     DDS_FloatSeq  values;

    DDS_Long  millisecondsPerSample;

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* SampleArray_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(SampleArraySeq, SampleArray);
        
NDDSUSERDllExport
RTIBool SampleArray_initialize(
        SampleArray* self);
        
NDDSUSERDllExport
RTIBool SampleArray_initialize_ex(
        SampleArray* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void SampleArray_finalize(
        SampleArray* self);
                        
NDDSUSERDllExport
void SampleArray_finalize_ex(
        SampleArray* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool SampleArray_copy(
        SampleArray* dst,
        const SampleArray* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

             
static const char * SampleArrayTopic = "ice::SampleArray";
        
extern const char *TextTYPENAME;
        


#ifdef __cplusplus
    struct TextSeq;

#ifndef NDDS_STANDALONE_TYPE
    class TextTypeSupport;
    class TextDataWriter;
    class TextDataReader;
#endif

#endif

            
    
class Text                                        
{
public:            
#ifdef __cplusplus
    typedef struct TextSeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef TextTypeSupport TypeSupport;
    typedef TextDataWriter DataWriter;
    typedef TextDataReader DataReader;
#endif

#endif
    
    ice::UniqueDeviceIdentifier  unique_device_identifier;

    ice::MetricIdentifier  metric_id;

    ice::InstanceIdentifier  instance_id;

    char*  value; /* maximum length = (256) */

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* Text_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(TextSeq, Text);
        
NDDSUSERDllExport
RTIBool Text_initialize(
        Text* self);
        
NDDSUSERDllExport
RTIBool Text_initialize_ex(
        Text* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void Text_finalize(
        Text* self);
                        
NDDSUSERDllExport
void Text_finalize_ex(
        Text* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool Text_copy(
        Text* dst,
        const Text* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

             
static const char * TextTopic = "ice::Text";
        
extern const char *InfusionObjectiveTYPENAME;
        


#ifdef __cplusplus
    struct InfusionObjectiveSeq;

#ifndef NDDS_STANDALONE_TYPE
    class InfusionObjectiveTypeSupport;
    class InfusionObjectiveDataWriter;
    class InfusionObjectiveDataReader;
#endif

#endif

            
    
class InfusionObjective                                        
{
public:            
#ifdef __cplusplus
    typedef struct InfusionObjectiveSeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef InfusionObjectiveTypeSupport TypeSupport;
    typedef InfusionObjectiveDataWriter DataWriter;
    typedef InfusionObjectiveDataReader DataReader;
#endif

#endif
    
    ice::UniqueDeviceIdentifier  unique_device_identifier;

    char*  requestor; /* maximum length = (128) */

    DDS_Boolean  stopInfusion;

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* InfusionObjective_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(InfusionObjectiveSeq, InfusionObjective);
        
NDDSUSERDllExport
RTIBool InfusionObjective_initialize(
        InfusionObjective* self);
        
NDDSUSERDllExport
RTIBool InfusionObjective_initialize_ex(
        InfusionObjective* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void InfusionObjective_finalize(
        InfusionObjective* self);
                        
NDDSUSERDllExport
void InfusionObjective_finalize_ex(
        InfusionObjective* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool InfusionObjective_copy(
        InfusionObjective* dst,
        const InfusionObjective* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

             
static const char * InfusionObjectiveTopic = "ice::InfusionObjective";
        
extern const char *InfusionStatusTYPENAME;
        


#ifdef __cplusplus
    struct InfusionStatusSeq;

#ifndef NDDS_STANDALONE_TYPE
    class InfusionStatusTypeSupport;
    class InfusionStatusDataWriter;
    class InfusionStatusDataReader;
#endif

#endif

            
    
class InfusionStatus                                        
{
public:            
#ifdef __cplusplus
    typedef struct InfusionStatusSeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef InfusionStatusTypeSupport TypeSupport;
    typedef InfusionStatusDataWriter DataWriter;
    typedef InfusionStatusDataReader DataReader;
#endif

#endif
    
    ice::UniqueDeviceIdentifier  unique_device_identifier;

    DDS_Boolean  infusionActive;

    char*  drug_name; /* maximum length = (256) */

    DDS_Long  drug_mass_mcg;

    DDS_Long  solution_volume_ml;

    DDS_Long  volume_to_be_infused_ml;

    DDS_Long  infusion_duration_seconds;

    DDS_Float  infusion_fraction_complete;

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* InfusionStatus_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(InfusionStatusSeq, InfusionStatus);
        
NDDSUSERDllExport
RTIBool InfusionStatus_initialize(
        InfusionStatus* self);
        
NDDSUSERDllExport
RTIBool InfusionStatus_initialize_ex(
        InfusionStatus* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void InfusionStatus_finalize(
        InfusionStatus* self);
                        
NDDSUSERDllExport
void InfusionStatus_finalize_ex(
        InfusionStatus* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool InfusionStatus_copy(
        InfusionStatus* dst,
        const InfusionStatus* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

             
static const char * InfusionStatusTopic = "ice::InfusionStatus";
        
extern const char *AlarmSettingsTYPENAME;
        


#ifdef __cplusplus
    struct AlarmSettingsSeq;

#ifndef NDDS_STANDALONE_TYPE
    class AlarmSettingsTypeSupport;
    class AlarmSettingsDataWriter;
    class AlarmSettingsDataReader;
#endif

#endif

            
    
class AlarmSettings                                        
{
public:            
#ifdef __cplusplus
    typedef struct AlarmSettingsSeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef AlarmSettingsTypeSupport TypeSupport;
    typedef AlarmSettingsDataWriter DataWriter;
    typedef AlarmSettingsDataReader DataReader;
#endif

#endif
    
    ice::UniqueDeviceIdentifier  unique_device_identifier;

    ice::MetricIdentifier  metric_id;

    DDS_Float  lower;

    DDS_Float  upper;

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* AlarmSettings_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(AlarmSettingsSeq, AlarmSettings);
        
NDDSUSERDllExport
RTIBool AlarmSettings_initialize(
        AlarmSettings* self);
        
NDDSUSERDllExport
RTIBool AlarmSettings_initialize_ex(
        AlarmSettings* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void AlarmSettings_finalize(
        AlarmSettings* self);
                        
NDDSUSERDllExport
void AlarmSettings_finalize_ex(
        AlarmSettings* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool AlarmSettings_copy(
        AlarmSettings* dst,
        const AlarmSettings* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

             
static const char * AlarmSettingsTopic = "ice::AlarmSettings";
        
extern const char *GlobalAlarmSettingsObjectiveTYPENAME;
        


#ifdef __cplusplus
    struct GlobalAlarmSettingsObjectiveSeq;

#ifndef NDDS_STANDALONE_TYPE
    class GlobalAlarmSettingsObjectiveTypeSupport;
    class GlobalAlarmSettingsObjectiveDataWriter;
    class GlobalAlarmSettingsObjectiveDataReader;
#endif

#endif

            
    
class GlobalAlarmSettingsObjective                                        
{
public:            
#ifdef __cplusplus
    typedef struct GlobalAlarmSettingsObjectiveSeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef GlobalAlarmSettingsObjectiveTypeSupport TypeSupport;
    typedef GlobalAlarmSettingsObjectiveDataWriter DataWriter;
    typedef GlobalAlarmSettingsObjectiveDataReader DataReader;
#endif

#endif
    
    ice::MetricIdentifier  metric_id;

    DDS_Float  lower;

    DDS_Float  upper;

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* GlobalAlarmSettingsObjective_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(GlobalAlarmSettingsObjectiveSeq, GlobalAlarmSettingsObjective);
        
NDDSUSERDllExport
RTIBool GlobalAlarmSettingsObjective_initialize(
        GlobalAlarmSettingsObjective* self);
        
NDDSUSERDllExport
RTIBool GlobalAlarmSettingsObjective_initialize_ex(
        GlobalAlarmSettingsObjective* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void GlobalAlarmSettingsObjective_finalize(
        GlobalAlarmSettingsObjective* self);
                        
NDDSUSERDllExport
void GlobalAlarmSettingsObjective_finalize_ex(
        GlobalAlarmSettingsObjective* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool GlobalAlarmSettingsObjective_copy(
        GlobalAlarmSettingsObjective* dst,
        const GlobalAlarmSettingsObjective* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

             
static const char * GlobalAlarmSettingsObjectiveTopic = "ice:GlobalAlarmSettingsObjective";
        
extern const char *LocalAlarmSettingsObjectiveTYPENAME;
        


#ifdef __cplusplus
    struct LocalAlarmSettingsObjectiveSeq;

#ifndef NDDS_STANDALONE_TYPE
    class LocalAlarmSettingsObjectiveTypeSupport;
    class LocalAlarmSettingsObjectiveDataWriter;
    class LocalAlarmSettingsObjectiveDataReader;
#endif

#endif

            
    
class LocalAlarmSettingsObjective                                        
{
public:            
#ifdef __cplusplus
    typedef struct LocalAlarmSettingsObjectiveSeq Seq;

#ifndef NDDS_STANDALONE_TYPE
    typedef LocalAlarmSettingsObjectiveTypeSupport TypeSupport;
    typedef LocalAlarmSettingsObjectiveDataWriter DataWriter;
    typedef LocalAlarmSettingsObjectiveDataReader DataReader;
#endif

#endif
    
    ice::UniqueDeviceIdentifier  unique_device_identifier;

    ice::MetricIdentifier  metric_id;

    DDS_Float  lower;

    DDS_Float  upper;

            
};                        
    
                            
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, start exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport __declspec(dllexport)
#endif

    
NDDSUSERDllExport DDS_TypeCode* LocalAlarmSettingsObjective_get_typecode(void); /* Type code */
    

DDS_SEQUENCE(LocalAlarmSettingsObjectiveSeq, LocalAlarmSettingsObjective);
        
NDDSUSERDllExport
RTIBool LocalAlarmSettingsObjective_initialize(
        LocalAlarmSettingsObjective* self);
        
NDDSUSERDllExport
RTIBool LocalAlarmSettingsObjective_initialize_ex(
        LocalAlarmSettingsObjective* self,RTIBool allocatePointers,RTIBool allocateMemory);

NDDSUSERDllExport
void LocalAlarmSettingsObjective_finalize(
        LocalAlarmSettingsObjective* self);
                        
NDDSUSERDllExport
void LocalAlarmSettingsObjective_finalize_ex(
        LocalAlarmSettingsObjective* self,RTIBool deletePointers);
        
NDDSUSERDllExport
RTIBool LocalAlarmSettingsObjective_copy(
        LocalAlarmSettingsObjective* dst,
        const LocalAlarmSettingsObjective* src);

#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
  /* If the code is building on Windows, stop exporting symbols.
   */
  #undef NDDSUSERDllExport
  #define NDDSUSERDllExport
#endif

             
static const char * LocalAlarmSettingsObjectiveTopic = "ice::LocalAlarmSettingsObjective";
} /* namespace ice */


#endif /* ice_1627646664_h */
