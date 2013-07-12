
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from device.idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/

#ifndef device_780045361_h
#define device_780045361_h

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
    
    char*  universal_device_identifier; /* maximum length = (256) */

    char*  manufacturer; /* maximum length = (128) */

    char*  model; /* maximum length = (128) */

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
    
    char*  universal_device_identifier; /* maximum length = (256) */

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
    
    char*  universal_device_identifier; /* maximum length = (256) */

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
static const DDS_Long MDC_PULS_OXIM_SAT_O2 = 19384;             
static const DDS_Long MDC_PULS_OXIM_PLETH = 19380;             
static const DDS_Long MDC_PULS_OXIM_PULS_RATE = 18458;             
static const DDS_Long MDC_PRESS_BLD_NONINV_DIA = 18950;             
static const DDS_Long MDC_PRESS_BLD_NONINV_MEAN = 18951;             
static const DDS_Long MDC_PRESS_CUFF = 19228;             
static const DDS_Long MDC_PRESS_CUFF_DIA = 19230;             
static const DDS_Long MDC_PRESS_CUFF_SYS = 19229;             
static const DDS_Long MDC_PRESS_CUFF_MEAN = 19231;             
static const DDS_Long MDC_RESP_RATE = 20490;             
static const DDS_Long MDC_PRESS_CUFF_NEXT_INFLATION = 65536;             
static const DDS_Long MDC_PRESS_CUFF_INFLATION = 65537;             
static const DDS_Long MDC_PULS_RATE_NON_INV = 18474;             
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
    
    char*  universal_device_identifier; /* maximum length = (256) */

    DDS_Long  name;

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
    
    char*  universal_device_identifier; /* maximum length = (256) */

    DDS_Long  name;

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
    
    char*  universal_device_identifier; /* maximum length = (256) */

    DDS_Long  name;

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
} /* namespace ice */


#endif /* device_780045361_h */
