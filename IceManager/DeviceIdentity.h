
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from DeviceIdentity.idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/

#ifndef DeviceIdentity_1392361367_h
#define DeviceIdentity_1392361367_h

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


#ifdef __cplusplus
extern "C" {
#endif

        
extern const char *DeviceIdentityTYPENAME;
        

#ifdef __cplusplus
}
#endif


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
    
    DDS_Octet  universal_device_identifier[64];

    char*  manufacturer; /* maximum length = (128) */

    char*  model; /* maximum length = (128) */

            
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



#endif /* DeviceIdentity_1392361367_h */
