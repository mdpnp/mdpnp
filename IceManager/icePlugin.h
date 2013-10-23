
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from ice.idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/

#ifndef icePlugin_1627646664_h
#define icePlugin_1627646664_h

#include "ice.h"




struct RTICdrStream;

#ifndef pres_typePlugin_h
#include "pres/pres_typePlugin.h"
#endif


#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
/* If the code is building on Windows, start exporting symbols.
*/
#undef NDDSUSERDllExport
#define NDDSUSERDllExport __declspec(dllexport)
#endif


namespace ice{


#define ice_UniqueDeviceIdentifier_LAST_MEMBER_ID string_LAST_MEMBER_ID

#define UniqueDeviceIdentifierPlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define UniqueDeviceIdentifierPlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define UniqueDeviceIdentifierPlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define UniqueDeviceIdentifierPlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 
 

#define UniqueDeviceIdentifierPlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define UniqueDeviceIdentifierPlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern UniqueDeviceIdentifier*
UniqueDeviceIdentifierPluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern UniqueDeviceIdentifier*
UniqueDeviceIdentifierPluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
UniqueDeviceIdentifierPluginSupport_copy_data(
    UniqueDeviceIdentifier *out,
    const UniqueDeviceIdentifier *in);

NDDSUSERDllExport extern void 
UniqueDeviceIdentifierPluginSupport_destroy_data_ex(
    UniqueDeviceIdentifier *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
UniqueDeviceIdentifierPluginSupport_destroy_data(
    UniqueDeviceIdentifier *sample);

NDDSUSERDllExport extern void 
UniqueDeviceIdentifierPluginSupport_print_data(
    const UniqueDeviceIdentifier *sample,
    const char *desc,
    unsigned int indent);



NDDSUSERDllExport extern RTIBool 
UniqueDeviceIdentifierPlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    UniqueDeviceIdentifier *out,
    const UniqueDeviceIdentifier *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
UniqueDeviceIdentifierPlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const UniqueDeviceIdentifier *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
UniqueDeviceIdentifierPlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    UniqueDeviceIdentifier *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
UniqueDeviceIdentifierPlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
UniqueDeviceIdentifierPlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
UniqueDeviceIdentifierPlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
UniqueDeviceIdentifierPlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const UniqueDeviceIdentifier * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
UniqueDeviceIdentifierPlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
UniqueDeviceIdentifierPlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
UniqueDeviceIdentifierPlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const UniqueDeviceIdentifier *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
UniqueDeviceIdentifierPlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    UniqueDeviceIdentifier * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);



NDDSUSERDllExport extern RTIBool
UniqueDeviceIdentifierPlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    UniqueDeviceIdentifier *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);



#define ice_MetricIdentifier_LAST_MEMBER_ID string_LAST_MEMBER_ID

#define MetricIdentifierPlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define MetricIdentifierPlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define MetricIdentifierPlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define MetricIdentifierPlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 
 

#define MetricIdentifierPlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define MetricIdentifierPlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern MetricIdentifier*
MetricIdentifierPluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern MetricIdentifier*
MetricIdentifierPluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
MetricIdentifierPluginSupport_copy_data(
    MetricIdentifier *out,
    const MetricIdentifier *in);

NDDSUSERDllExport extern void 
MetricIdentifierPluginSupport_destroy_data_ex(
    MetricIdentifier *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
MetricIdentifierPluginSupport_destroy_data(
    MetricIdentifier *sample);

NDDSUSERDllExport extern void 
MetricIdentifierPluginSupport_print_data(
    const MetricIdentifier *sample,
    const char *desc,
    unsigned int indent);



NDDSUSERDllExport extern RTIBool 
MetricIdentifierPlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    MetricIdentifier *out,
    const MetricIdentifier *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
MetricIdentifierPlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const MetricIdentifier *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
MetricIdentifierPlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    MetricIdentifier *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
MetricIdentifierPlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
MetricIdentifierPlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
MetricIdentifierPlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
MetricIdentifierPlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const MetricIdentifier * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
MetricIdentifierPlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
MetricIdentifierPlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
MetricIdentifierPlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const MetricIdentifier *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
MetricIdentifierPlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    MetricIdentifier * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);



NDDSUSERDllExport extern RTIBool
MetricIdentifierPlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    MetricIdentifier *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);



#define ice_InstanceIdentifier_LAST_MEMBER_ID long_LAST_MEMBER_ID

#define InstanceIdentifierPlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define InstanceIdentifierPlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define InstanceIdentifierPlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define InstanceIdentifierPlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 
 

#define InstanceIdentifierPlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define InstanceIdentifierPlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern InstanceIdentifier*
InstanceIdentifierPluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern InstanceIdentifier*
InstanceIdentifierPluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
InstanceIdentifierPluginSupport_copy_data(
    InstanceIdentifier *out,
    const InstanceIdentifier *in);

NDDSUSERDllExport extern void 
InstanceIdentifierPluginSupport_destroy_data_ex(
    InstanceIdentifier *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
InstanceIdentifierPluginSupport_destroy_data(
    InstanceIdentifier *sample);

NDDSUSERDllExport extern void 
InstanceIdentifierPluginSupport_print_data(
    const InstanceIdentifier *sample,
    const char *desc,
    unsigned int indent);



NDDSUSERDllExport extern RTIBool 
InstanceIdentifierPlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    InstanceIdentifier *out,
    const InstanceIdentifier *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
InstanceIdentifierPlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const InstanceIdentifier *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
InstanceIdentifierPlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    InstanceIdentifier *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
InstanceIdentifierPlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
InstanceIdentifierPlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
InstanceIdentifierPlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
InstanceIdentifierPlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const InstanceIdentifier * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
InstanceIdentifierPlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
InstanceIdentifierPlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
InstanceIdentifierPlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const InstanceIdentifier *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
InstanceIdentifierPlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    InstanceIdentifier * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);



NDDSUSERDllExport extern RTIBool
InstanceIdentifierPlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    InstanceIdentifier *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);


#define ice_Image_LAST_MEMBER_ID 0

#define ImagePlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define ImagePlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define ImagePlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define ImagePlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 
 

#define ImagePlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define ImagePlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern Image*
ImagePluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern Image*
ImagePluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
ImagePluginSupport_copy_data(
    Image *out,
    const Image *in);

NDDSUSERDllExport extern void 
ImagePluginSupport_destroy_data_ex(
    Image *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
ImagePluginSupport_destroy_data(
    Image *sample);

NDDSUSERDllExport extern void 
ImagePluginSupport_print_data(
    const Image *sample,
    const char *desc,
    unsigned int indent);


/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
ImagePlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
ImagePlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
ImagePlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
ImagePlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
ImagePlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    Image *out,
    const Image *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
ImagePlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const Image *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
ImagePlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    Image *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
ImagePlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    Image **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
ImagePlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
ImagePlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
ImagePlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
ImagePlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const Image * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
ImagePlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
ImagePlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
ImagePlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const Image *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
ImagePlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    Image * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
ImagePlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    Image ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
ImagePlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    Image *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
ImagePlugin_new(void);

NDDSUSERDllExport extern void
ImagePlugin_delete(struct PRESTypePlugin *);

#define ice_DeviceIdentity_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * DeviceIdentity.
 *
 * By default, this type is struct DeviceIdentity
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct DeviceIdentity)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct DeviceIdentity, the
 * following restriction applies: the key of struct
 * DeviceIdentity must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct DeviceIdentity.
*/
typedef  class DeviceIdentity DeviceIdentityKeyHolder;


#define DeviceIdentityPlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define DeviceIdentityPlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define DeviceIdentityPlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define DeviceIdentityPlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define DeviceIdentityPlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define DeviceIdentityPlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define DeviceIdentityPlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define DeviceIdentityPlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern DeviceIdentity*
DeviceIdentityPluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern DeviceIdentity*
DeviceIdentityPluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
DeviceIdentityPluginSupport_copy_data(
    DeviceIdentity *out,
    const DeviceIdentity *in);

NDDSUSERDllExport extern void 
DeviceIdentityPluginSupport_destroy_data_ex(
    DeviceIdentity *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
DeviceIdentityPluginSupport_destroy_data(
    DeviceIdentity *sample);

NDDSUSERDllExport extern void 
DeviceIdentityPluginSupport_print_data(
    const DeviceIdentity *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern DeviceIdentity*
DeviceIdentityPluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern DeviceIdentity*
DeviceIdentityPluginSupport_create_key(void);

NDDSUSERDllExport extern void 
DeviceIdentityPluginSupport_destroy_key_ex(
    DeviceIdentityKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
DeviceIdentityPluginSupport_destroy_key(
    DeviceIdentityKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
DeviceIdentityPlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
DeviceIdentityPlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
DeviceIdentityPlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
DeviceIdentityPlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    DeviceIdentity *out,
    const DeviceIdentity *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const DeviceIdentity *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    DeviceIdentity *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    DeviceIdentity **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
DeviceIdentityPlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
DeviceIdentityPlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
DeviceIdentityPlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
DeviceIdentityPlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const DeviceIdentity * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
DeviceIdentityPlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
DeviceIdentityPlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const DeviceIdentity *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    DeviceIdentity * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    DeviceIdentity ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
DeviceIdentityPlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    DeviceIdentity *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    DeviceIdentityKeyHolder *key, 
    const DeviceIdentity *instance);

NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    DeviceIdentity *instance, 
    const DeviceIdentityKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const DeviceIdentity *instance);

NDDSUSERDllExport extern RTIBool 
DeviceIdentityPlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
DeviceIdentityPlugin_new(void);

NDDSUSERDllExport extern void
DeviceIdentityPlugin_delete(struct PRESTypePlugin *);


/* ------------------------------------------------------------------------
 * (De)Serialization Methods
 * ------------------------------------------------------------------------ */

NDDSUSERDllExport extern RTIBool
ConnectionStatePlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const ConnectionState *sample, struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool
ConnectionStatePlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    ConnectionState *sample, 
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool
ConnectionStatePlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int
ConnectionStatePlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
ConnectionStatePlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
ConnectionStatePlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const ConnectionState * sample);


/* ------------------------------------------------------------------------
    Key Management functions:
 * ------------------------------------------------------------------------ */

NDDSUSERDllExport extern RTIBool 
ConnectionStatePlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const ConnectionState *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
ConnectionStatePlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    ConnectionState *sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
ConnectionStatePlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool
ConnectionStatePlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    ConnectionState *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 


/* ----------------------------------------------------------------------------
    Support functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern void
ConnectionStatePluginSupport_print_data(
    const ConnectionState *sample, const char *desc, int indent_level);



/* ------------------------------------------------------------------------
 * (De)Serialization Methods
 * ------------------------------------------------------------------------ */

NDDSUSERDllExport extern RTIBool
ConnectionTypePlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const ConnectionType *sample, struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool
ConnectionTypePlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    ConnectionType *sample, 
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool
ConnectionTypePlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int
ConnectionTypePlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
ConnectionTypePlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
ConnectionTypePlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const ConnectionType * sample);


/* ------------------------------------------------------------------------
    Key Management functions:
 * ------------------------------------------------------------------------ */

NDDSUSERDllExport extern RTIBool 
ConnectionTypePlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const ConnectionType *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
ConnectionTypePlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    ConnectionType *sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
ConnectionTypePlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool
ConnectionTypePlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    ConnectionType *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 


/* ----------------------------------------------------------------------------
    Support functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern void
ConnectionTypePluginSupport_print_data(
    const ConnectionType *sample, const char *desc, int indent_level);


#define ice_DeviceConnectivity_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * DeviceConnectivity.
 *
 * By default, this type is struct DeviceConnectivity
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct DeviceConnectivity)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct DeviceConnectivity, the
 * following restriction applies: the key of struct
 * DeviceConnectivity must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct DeviceConnectivity.
*/
typedef  class DeviceConnectivity DeviceConnectivityKeyHolder;


#define DeviceConnectivityPlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define DeviceConnectivityPlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define DeviceConnectivityPlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define DeviceConnectivityPlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define DeviceConnectivityPlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define DeviceConnectivityPlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define DeviceConnectivityPlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define DeviceConnectivityPlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern DeviceConnectivity*
DeviceConnectivityPluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern DeviceConnectivity*
DeviceConnectivityPluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPluginSupport_copy_data(
    DeviceConnectivity *out,
    const DeviceConnectivity *in);

NDDSUSERDllExport extern void 
DeviceConnectivityPluginSupport_destroy_data_ex(
    DeviceConnectivity *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
DeviceConnectivityPluginSupport_destroy_data(
    DeviceConnectivity *sample);

NDDSUSERDllExport extern void 
DeviceConnectivityPluginSupport_print_data(
    const DeviceConnectivity *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern DeviceConnectivity*
DeviceConnectivityPluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern DeviceConnectivity*
DeviceConnectivityPluginSupport_create_key(void);

NDDSUSERDllExport extern void 
DeviceConnectivityPluginSupport_destroy_key_ex(
    DeviceConnectivityKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
DeviceConnectivityPluginSupport_destroy_key(
    DeviceConnectivityKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
DeviceConnectivityPlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
DeviceConnectivityPlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
DeviceConnectivityPlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
DeviceConnectivityPlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivity *out,
    const DeviceConnectivity *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const DeviceConnectivity *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivity *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivity **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
DeviceConnectivityPlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
DeviceConnectivityPlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
DeviceConnectivityPlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
DeviceConnectivityPlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const DeviceConnectivity * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
DeviceConnectivityPlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
DeviceConnectivityPlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const DeviceConnectivity *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivity * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivity ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
DeviceConnectivityPlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivity *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivityKeyHolder *key, 
    const DeviceConnectivity *instance);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivity *instance, 
    const DeviceConnectivityKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const DeviceConnectivity *instance);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityPlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
DeviceConnectivityPlugin_new(void);

NDDSUSERDllExport extern void
DeviceConnectivityPlugin_delete(struct PRESTypePlugin *);

#define ice_DeviceConnectivityObjective_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * DeviceConnectivityObjective.
 *
 * By default, this type is struct DeviceConnectivityObjective
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct DeviceConnectivityObjective)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct DeviceConnectivityObjective, the
 * following restriction applies: the key of struct
 * DeviceConnectivityObjective must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct DeviceConnectivityObjective.
*/
typedef  class DeviceConnectivityObjective DeviceConnectivityObjectiveKeyHolder;


#define DeviceConnectivityObjectivePlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define DeviceConnectivityObjectivePlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define DeviceConnectivityObjectivePlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define DeviceConnectivityObjectivePlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define DeviceConnectivityObjectivePlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define DeviceConnectivityObjectivePlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define DeviceConnectivityObjectivePlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define DeviceConnectivityObjectivePlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern DeviceConnectivityObjective*
DeviceConnectivityObjectivePluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern DeviceConnectivityObjective*
DeviceConnectivityObjectivePluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePluginSupport_copy_data(
    DeviceConnectivityObjective *out,
    const DeviceConnectivityObjective *in);

NDDSUSERDllExport extern void 
DeviceConnectivityObjectivePluginSupport_destroy_data_ex(
    DeviceConnectivityObjective *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
DeviceConnectivityObjectivePluginSupport_destroy_data(
    DeviceConnectivityObjective *sample);

NDDSUSERDllExport extern void 
DeviceConnectivityObjectivePluginSupport_print_data(
    const DeviceConnectivityObjective *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern DeviceConnectivityObjective*
DeviceConnectivityObjectivePluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern DeviceConnectivityObjective*
DeviceConnectivityObjectivePluginSupport_create_key(void);

NDDSUSERDllExport extern void 
DeviceConnectivityObjectivePluginSupport_destroy_key_ex(
    DeviceConnectivityObjectiveKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
DeviceConnectivityObjectivePluginSupport_destroy_key(
    DeviceConnectivityObjectiveKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
DeviceConnectivityObjectivePlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
DeviceConnectivityObjectivePlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
DeviceConnectivityObjectivePlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
DeviceConnectivityObjectivePlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivityObjective *out,
    const DeviceConnectivityObjective *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const DeviceConnectivityObjective *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivityObjective *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivityObjective **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
DeviceConnectivityObjectivePlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
DeviceConnectivityObjectivePlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
DeviceConnectivityObjectivePlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
DeviceConnectivityObjectivePlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const DeviceConnectivityObjective * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
DeviceConnectivityObjectivePlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
DeviceConnectivityObjectivePlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const DeviceConnectivityObjective *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivityObjective * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivityObjective ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
DeviceConnectivityObjectivePlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivityObjective *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivityObjectiveKeyHolder *key, 
    const DeviceConnectivityObjective *instance);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    DeviceConnectivityObjective *instance, 
    const DeviceConnectivityObjectiveKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const DeviceConnectivityObjective *instance);

NDDSUSERDllExport extern RTIBool 
DeviceConnectivityObjectivePlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
DeviceConnectivityObjectivePlugin_new(void);

NDDSUSERDllExport extern void
DeviceConnectivityObjectivePlugin_delete(struct PRESTypePlugin *);

#define ice_Numeric_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * Numeric.
 *
 * By default, this type is struct Numeric
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct Numeric)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct Numeric, the
 * following restriction applies: the key of struct
 * Numeric must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct Numeric.
*/
typedef  class Numeric NumericKeyHolder;


#define NumericPlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define NumericPlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define NumericPlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define NumericPlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define NumericPlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define NumericPlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define NumericPlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define NumericPlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern Numeric*
NumericPluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern Numeric*
NumericPluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
NumericPluginSupport_copy_data(
    Numeric *out,
    const Numeric *in);

NDDSUSERDllExport extern void 
NumericPluginSupport_destroy_data_ex(
    Numeric *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
NumericPluginSupport_destroy_data(
    Numeric *sample);

NDDSUSERDllExport extern void 
NumericPluginSupport_print_data(
    const Numeric *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern Numeric*
NumericPluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern Numeric*
NumericPluginSupport_create_key(void);

NDDSUSERDllExport extern void 
NumericPluginSupport_destroy_key_ex(
    NumericKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
NumericPluginSupport_destroy_key(
    NumericKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
NumericPlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
NumericPlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
NumericPlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
NumericPlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
NumericPlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    Numeric *out,
    const Numeric *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
NumericPlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const Numeric *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
NumericPlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    Numeric *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
NumericPlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    Numeric **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
NumericPlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
NumericPlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
NumericPlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
NumericPlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const Numeric * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
NumericPlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
NumericPlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
NumericPlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const Numeric *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
NumericPlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    Numeric * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
NumericPlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    Numeric ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
NumericPlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    Numeric *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
NumericPlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    NumericKeyHolder *key, 
    const Numeric *instance);

NDDSUSERDllExport extern RTIBool 
NumericPlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    Numeric *instance, 
    const NumericKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
NumericPlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const Numeric *instance);

NDDSUSERDllExport extern RTIBool 
NumericPlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
NumericPlugin_new(void);

NDDSUSERDllExport extern void
NumericPlugin_delete(struct PRESTypePlugin *);

#define ice_SampleArray_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * SampleArray.
 *
 * By default, this type is struct SampleArray
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct SampleArray)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct SampleArray, the
 * following restriction applies: the key of struct
 * SampleArray must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct SampleArray.
*/
typedef  class SampleArray SampleArrayKeyHolder;


#define SampleArrayPlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define SampleArrayPlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define SampleArrayPlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define SampleArrayPlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define SampleArrayPlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define SampleArrayPlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define SampleArrayPlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define SampleArrayPlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern SampleArray*
SampleArrayPluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern SampleArray*
SampleArrayPluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
SampleArrayPluginSupport_copy_data(
    SampleArray *out,
    const SampleArray *in);

NDDSUSERDllExport extern void 
SampleArrayPluginSupport_destroy_data_ex(
    SampleArray *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
SampleArrayPluginSupport_destroy_data(
    SampleArray *sample);

NDDSUSERDllExport extern void 
SampleArrayPluginSupport_print_data(
    const SampleArray *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern SampleArray*
SampleArrayPluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern SampleArray*
SampleArrayPluginSupport_create_key(void);

NDDSUSERDllExport extern void 
SampleArrayPluginSupport_destroy_key_ex(
    SampleArrayKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
SampleArrayPluginSupport_destroy_key(
    SampleArrayKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
SampleArrayPlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
SampleArrayPlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
SampleArrayPlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
SampleArrayPlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    SampleArray *out,
    const SampleArray *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const SampleArray *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    SampleArray *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    SampleArray **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
SampleArrayPlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
SampleArrayPlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
SampleArrayPlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
SampleArrayPlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const SampleArray * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
SampleArrayPlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
SampleArrayPlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const SampleArray *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    SampleArray * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    SampleArray ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
SampleArrayPlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    SampleArray *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    SampleArrayKeyHolder *key, 
    const SampleArray *instance);

NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    SampleArray *instance, 
    const SampleArrayKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const SampleArray *instance);

NDDSUSERDllExport extern RTIBool 
SampleArrayPlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
SampleArrayPlugin_new(void);

NDDSUSERDllExport extern void
SampleArrayPlugin_delete(struct PRESTypePlugin *);

#define ice_Text_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * Text.
 *
 * By default, this type is struct Text
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct Text)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct Text, the
 * following restriction applies: the key of struct
 * Text must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct Text.
*/
typedef  class Text TextKeyHolder;


#define TextPlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define TextPlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define TextPlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define TextPlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define TextPlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define TextPlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define TextPlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define TextPlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern Text*
TextPluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern Text*
TextPluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
TextPluginSupport_copy_data(
    Text *out,
    const Text *in);

NDDSUSERDllExport extern void 
TextPluginSupport_destroy_data_ex(
    Text *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
TextPluginSupport_destroy_data(
    Text *sample);

NDDSUSERDllExport extern void 
TextPluginSupport_print_data(
    const Text *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern Text*
TextPluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern Text*
TextPluginSupport_create_key(void);

NDDSUSERDllExport extern void 
TextPluginSupport_destroy_key_ex(
    TextKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
TextPluginSupport_destroy_key(
    TextKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
TextPlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
TextPlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
TextPlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
TextPlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
TextPlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    Text *out,
    const Text *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
TextPlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const Text *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
TextPlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    Text *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
TextPlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    Text **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
TextPlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
TextPlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
TextPlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
TextPlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const Text * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
TextPlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
TextPlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
TextPlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const Text *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
TextPlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    Text * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
TextPlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    Text ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
TextPlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    Text *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
TextPlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    TextKeyHolder *key, 
    const Text *instance);

NDDSUSERDllExport extern RTIBool 
TextPlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    Text *instance, 
    const TextKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
TextPlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const Text *instance);

NDDSUSERDllExport extern RTIBool 
TextPlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
TextPlugin_new(void);

NDDSUSERDllExport extern void
TextPlugin_delete(struct PRESTypePlugin *);

#define ice_InfusionObjective_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * InfusionObjective.
 *
 * By default, this type is struct InfusionObjective
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct InfusionObjective)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct InfusionObjective, the
 * following restriction applies: the key of struct
 * InfusionObjective must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct InfusionObjective.
*/
typedef  class InfusionObjective InfusionObjectiveKeyHolder;


#define InfusionObjectivePlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define InfusionObjectivePlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define InfusionObjectivePlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define InfusionObjectivePlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define InfusionObjectivePlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define InfusionObjectivePlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define InfusionObjectivePlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define InfusionObjectivePlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern InfusionObjective*
InfusionObjectivePluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern InfusionObjective*
InfusionObjectivePluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
InfusionObjectivePluginSupport_copy_data(
    InfusionObjective *out,
    const InfusionObjective *in);

NDDSUSERDllExport extern void 
InfusionObjectivePluginSupport_destroy_data_ex(
    InfusionObjective *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
InfusionObjectivePluginSupport_destroy_data(
    InfusionObjective *sample);

NDDSUSERDllExport extern void 
InfusionObjectivePluginSupport_print_data(
    const InfusionObjective *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern InfusionObjective*
InfusionObjectivePluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern InfusionObjective*
InfusionObjectivePluginSupport_create_key(void);

NDDSUSERDllExport extern void 
InfusionObjectivePluginSupport_destroy_key_ex(
    InfusionObjectiveKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
InfusionObjectivePluginSupport_destroy_key(
    InfusionObjectiveKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
InfusionObjectivePlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
InfusionObjectivePlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
InfusionObjectivePlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
InfusionObjectivePlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    InfusionObjective *out,
    const InfusionObjective *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const InfusionObjective *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    InfusionObjective *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    InfusionObjective **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
InfusionObjectivePlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
InfusionObjectivePlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
InfusionObjectivePlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
InfusionObjectivePlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const InfusionObjective * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
InfusionObjectivePlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
InfusionObjectivePlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const InfusionObjective *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    InfusionObjective * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    InfusionObjective ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
InfusionObjectivePlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    InfusionObjective *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    InfusionObjectiveKeyHolder *key, 
    const InfusionObjective *instance);

NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    InfusionObjective *instance, 
    const InfusionObjectiveKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const InfusionObjective *instance);

NDDSUSERDllExport extern RTIBool 
InfusionObjectivePlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
InfusionObjectivePlugin_new(void);

NDDSUSERDllExport extern void
InfusionObjectivePlugin_delete(struct PRESTypePlugin *);

#define ice_InfusionStatus_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * InfusionStatus.
 *
 * By default, this type is struct InfusionStatus
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct InfusionStatus)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct InfusionStatus, the
 * following restriction applies: the key of struct
 * InfusionStatus must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct InfusionStatus.
*/
typedef  class InfusionStatus InfusionStatusKeyHolder;


#define InfusionStatusPlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define InfusionStatusPlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define InfusionStatusPlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define InfusionStatusPlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define InfusionStatusPlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define InfusionStatusPlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define InfusionStatusPlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define InfusionStatusPlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern InfusionStatus*
InfusionStatusPluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern InfusionStatus*
InfusionStatusPluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
InfusionStatusPluginSupport_copy_data(
    InfusionStatus *out,
    const InfusionStatus *in);

NDDSUSERDllExport extern void 
InfusionStatusPluginSupport_destroy_data_ex(
    InfusionStatus *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
InfusionStatusPluginSupport_destroy_data(
    InfusionStatus *sample);

NDDSUSERDllExport extern void 
InfusionStatusPluginSupport_print_data(
    const InfusionStatus *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern InfusionStatus*
InfusionStatusPluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern InfusionStatus*
InfusionStatusPluginSupport_create_key(void);

NDDSUSERDllExport extern void 
InfusionStatusPluginSupport_destroy_key_ex(
    InfusionStatusKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
InfusionStatusPluginSupport_destroy_key(
    InfusionStatusKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
InfusionStatusPlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
InfusionStatusPlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
InfusionStatusPlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
InfusionStatusPlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    InfusionStatus *out,
    const InfusionStatus *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const InfusionStatus *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    InfusionStatus *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    InfusionStatus **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
InfusionStatusPlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
InfusionStatusPlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
InfusionStatusPlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
InfusionStatusPlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const InfusionStatus * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
InfusionStatusPlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
InfusionStatusPlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const InfusionStatus *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    InfusionStatus * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    InfusionStatus ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
InfusionStatusPlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    InfusionStatus *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    InfusionStatusKeyHolder *key, 
    const InfusionStatus *instance);

NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    InfusionStatus *instance, 
    const InfusionStatusKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const InfusionStatus *instance);

NDDSUSERDllExport extern RTIBool 
InfusionStatusPlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
InfusionStatusPlugin_new(void);

NDDSUSERDllExport extern void
InfusionStatusPlugin_delete(struct PRESTypePlugin *);

#define ice_AlarmSettings_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * AlarmSettings.
 *
 * By default, this type is struct AlarmSettings
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct AlarmSettings)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct AlarmSettings, the
 * following restriction applies: the key of struct
 * AlarmSettings must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct AlarmSettings.
*/
typedef  class AlarmSettings AlarmSettingsKeyHolder;


#define AlarmSettingsPlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define AlarmSettingsPlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define AlarmSettingsPlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define AlarmSettingsPlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define AlarmSettingsPlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define AlarmSettingsPlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define AlarmSettingsPlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define AlarmSettingsPlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern AlarmSettings*
AlarmSettingsPluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern AlarmSettings*
AlarmSettingsPluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
AlarmSettingsPluginSupport_copy_data(
    AlarmSettings *out,
    const AlarmSettings *in);

NDDSUSERDllExport extern void 
AlarmSettingsPluginSupport_destroy_data_ex(
    AlarmSettings *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
AlarmSettingsPluginSupport_destroy_data(
    AlarmSettings *sample);

NDDSUSERDllExport extern void 
AlarmSettingsPluginSupport_print_data(
    const AlarmSettings *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern AlarmSettings*
AlarmSettingsPluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern AlarmSettings*
AlarmSettingsPluginSupport_create_key(void);

NDDSUSERDllExport extern void 
AlarmSettingsPluginSupport_destroy_key_ex(
    AlarmSettingsKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
AlarmSettingsPluginSupport_destroy_key(
    AlarmSettingsKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
AlarmSettingsPlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
AlarmSettingsPlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
AlarmSettingsPlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
AlarmSettingsPlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    AlarmSettings *out,
    const AlarmSettings *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const AlarmSettings *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    AlarmSettings *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    AlarmSettings **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
AlarmSettingsPlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
AlarmSettingsPlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
AlarmSettingsPlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
AlarmSettingsPlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const AlarmSettings * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
AlarmSettingsPlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
AlarmSettingsPlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const AlarmSettings *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    AlarmSettings * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    AlarmSettings ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
AlarmSettingsPlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    AlarmSettings *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    AlarmSettingsKeyHolder *key, 
    const AlarmSettings *instance);

NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    AlarmSettings *instance, 
    const AlarmSettingsKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const AlarmSettings *instance);

NDDSUSERDllExport extern RTIBool 
AlarmSettingsPlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
AlarmSettingsPlugin_new(void);

NDDSUSERDllExport extern void
AlarmSettingsPlugin_delete(struct PRESTypePlugin *);

#define ice_GlobalAlarmSettingsObjective_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * GlobalAlarmSettingsObjective.
 *
 * By default, this type is struct GlobalAlarmSettingsObjective
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct GlobalAlarmSettingsObjective)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct GlobalAlarmSettingsObjective, the
 * following restriction applies: the key of struct
 * GlobalAlarmSettingsObjective must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct GlobalAlarmSettingsObjective.
*/
typedef  class GlobalAlarmSettingsObjective GlobalAlarmSettingsObjectiveKeyHolder;


#define GlobalAlarmSettingsObjectivePlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define GlobalAlarmSettingsObjectivePlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define GlobalAlarmSettingsObjectivePlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define GlobalAlarmSettingsObjectivePlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define GlobalAlarmSettingsObjectivePlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define GlobalAlarmSettingsObjectivePlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define GlobalAlarmSettingsObjectivePlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define GlobalAlarmSettingsObjectivePlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern GlobalAlarmSettingsObjective*
GlobalAlarmSettingsObjectivePluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern GlobalAlarmSettingsObjective*
GlobalAlarmSettingsObjectivePluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePluginSupport_copy_data(
    GlobalAlarmSettingsObjective *out,
    const GlobalAlarmSettingsObjective *in);

NDDSUSERDllExport extern void 
GlobalAlarmSettingsObjectivePluginSupport_destroy_data_ex(
    GlobalAlarmSettingsObjective *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
GlobalAlarmSettingsObjectivePluginSupport_destroy_data(
    GlobalAlarmSettingsObjective *sample);

NDDSUSERDllExport extern void 
GlobalAlarmSettingsObjectivePluginSupport_print_data(
    const GlobalAlarmSettingsObjective *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern GlobalAlarmSettingsObjective*
GlobalAlarmSettingsObjectivePluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern GlobalAlarmSettingsObjective*
GlobalAlarmSettingsObjectivePluginSupport_create_key(void);

NDDSUSERDllExport extern void 
GlobalAlarmSettingsObjectivePluginSupport_destroy_key_ex(
    GlobalAlarmSettingsObjectiveKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
GlobalAlarmSettingsObjectivePluginSupport_destroy_key(
    GlobalAlarmSettingsObjectiveKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
GlobalAlarmSettingsObjectivePlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
GlobalAlarmSettingsObjectivePlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
GlobalAlarmSettingsObjectivePlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
GlobalAlarmSettingsObjectivePlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    GlobalAlarmSettingsObjective *out,
    const GlobalAlarmSettingsObjective *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const GlobalAlarmSettingsObjective *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    GlobalAlarmSettingsObjective *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    GlobalAlarmSettingsObjective **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
GlobalAlarmSettingsObjectivePlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
GlobalAlarmSettingsObjectivePlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
GlobalAlarmSettingsObjectivePlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
GlobalAlarmSettingsObjectivePlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const GlobalAlarmSettingsObjective * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
GlobalAlarmSettingsObjectivePlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
GlobalAlarmSettingsObjectivePlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const GlobalAlarmSettingsObjective *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    GlobalAlarmSettingsObjective * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    GlobalAlarmSettingsObjective ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
GlobalAlarmSettingsObjectivePlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    GlobalAlarmSettingsObjective *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    GlobalAlarmSettingsObjectiveKeyHolder *key, 
    const GlobalAlarmSettingsObjective *instance);

NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    GlobalAlarmSettingsObjective *instance, 
    const GlobalAlarmSettingsObjectiveKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const GlobalAlarmSettingsObjective *instance);

NDDSUSERDllExport extern RTIBool 
GlobalAlarmSettingsObjectivePlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
GlobalAlarmSettingsObjectivePlugin_new(void);

NDDSUSERDllExport extern void
GlobalAlarmSettingsObjectivePlugin_delete(struct PRESTypePlugin *);

#define ice_LocalAlarmSettingsObjective_LAST_MEMBER_ID 0
/* The type used to store keys for instances of type struct
 * LocalAlarmSettingsObjective.
 *
 * By default, this type is struct LocalAlarmSettingsObjective
 * itself. However, if for some reason this choice is not practical for your
 * system (e.g. if sizeof(struct LocalAlarmSettingsObjective)
 * is very large), you may redefine this typedef in terms of another type of
 * your choosing. HOWEVER, if you define the KeyHolder type to be something
 * other than struct LocalAlarmSettingsObjective, the
 * following restriction applies: the key of struct
 * LocalAlarmSettingsObjective must consist of a
 * single field of your redefined KeyHolder type and that field must be the
 * first field in struct LocalAlarmSettingsObjective.
*/
typedef  class LocalAlarmSettingsObjective LocalAlarmSettingsObjectiveKeyHolder;


#define LocalAlarmSettingsObjectivePlugin_get_sample PRESTypePluginDefaultEndpointData_getSample 
#define LocalAlarmSettingsObjectivePlugin_return_sample PRESTypePluginDefaultEndpointData_returnSample 
#define LocalAlarmSettingsObjectivePlugin_get_buffer PRESTypePluginDefaultEndpointData_getBuffer 
#define LocalAlarmSettingsObjectivePlugin_return_buffer PRESTypePluginDefaultEndpointData_returnBuffer 

#define LocalAlarmSettingsObjectivePlugin_get_key PRESTypePluginDefaultEndpointData_getKey 
#define LocalAlarmSettingsObjectivePlugin_return_key PRESTypePluginDefaultEndpointData_returnKey
 

#define LocalAlarmSettingsObjectivePlugin_create_sample PRESTypePluginDefaultEndpointData_createSample 
#define LocalAlarmSettingsObjectivePlugin_destroy_sample PRESTypePluginDefaultEndpointData_deleteSample 

/* --------------------------------------------------------------------------------------
    Support functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern LocalAlarmSettingsObjective*
LocalAlarmSettingsObjectivePluginSupport_create_data_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern LocalAlarmSettingsObjective*
LocalAlarmSettingsObjectivePluginSupport_create_data(void);

NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePluginSupport_copy_data(
    LocalAlarmSettingsObjective *out,
    const LocalAlarmSettingsObjective *in);

NDDSUSERDllExport extern void 
LocalAlarmSettingsObjectivePluginSupport_destroy_data_ex(
    LocalAlarmSettingsObjective *sample,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
LocalAlarmSettingsObjectivePluginSupport_destroy_data(
    LocalAlarmSettingsObjective *sample);

NDDSUSERDllExport extern void 
LocalAlarmSettingsObjectivePluginSupport_print_data(
    const LocalAlarmSettingsObjective *sample,
    const char *desc,
    unsigned int indent);


NDDSUSERDllExport extern LocalAlarmSettingsObjective*
LocalAlarmSettingsObjectivePluginSupport_create_key_ex(RTIBool allocate_pointers);

NDDSUSERDllExport extern LocalAlarmSettingsObjective*
LocalAlarmSettingsObjectivePluginSupport_create_key(void);

NDDSUSERDllExport extern void 
LocalAlarmSettingsObjectivePluginSupport_destroy_key_ex(
    LocalAlarmSettingsObjectiveKeyHolder *key,RTIBool deallocate_pointers);

NDDSUSERDllExport extern void 
LocalAlarmSettingsObjectivePluginSupport_destroy_key(
    LocalAlarmSettingsObjectiveKeyHolder *key);

/* ----------------------------------------------------------------------------
    Callback functions:
 * ---------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginParticipantData 
LocalAlarmSettingsObjectivePlugin_on_participant_attached(
    void *registration_data, 
    const struct PRESTypePluginParticipantInfo *participant_info,
    RTIBool top_level_registration, 
    void *container_plugin_context,
    RTICdrTypeCode *typeCode);

NDDSUSERDllExport extern void 
LocalAlarmSettingsObjectivePlugin_on_participant_detached(
    PRESTypePluginParticipantData participant_data);
    
NDDSUSERDllExport extern PRESTypePluginEndpointData 
LocalAlarmSettingsObjectivePlugin_on_endpoint_attached(
    PRESTypePluginParticipantData participant_data,
    const struct PRESTypePluginEndpointInfo *endpoint_info,
    RTIBool top_level_registration, 
    void *container_plugin_context);

NDDSUSERDllExport extern void 
LocalAlarmSettingsObjectivePlugin_on_endpoint_detached(
    PRESTypePluginEndpointData endpoint_data);


NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_copy_sample(
    PRESTypePluginEndpointData endpoint_data,
    LocalAlarmSettingsObjective *out,
    const LocalAlarmSettingsObjective *in);

/* --------------------------------------------------------------------------------------
    (De)Serialize functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_serialize(
    PRESTypePluginEndpointData endpoint_data,
    const LocalAlarmSettingsObjective *sample,
    struct RTICdrStream *stream, 
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_deserialize_sample(
    PRESTypePluginEndpointData endpoint_data,
    LocalAlarmSettingsObjective *sample, 
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_deserialize(
    PRESTypePluginEndpointData endpoint_data,
    LocalAlarmSettingsObjective **sample, 
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_sample, 
    void *endpoint_plugin_qos);




NDDSUSERDllExport extern RTIBool
LocalAlarmSettingsObjectivePlugin_skip(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    RTIBool skip_encapsulation,  
    RTIBool skip_sample, 
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern unsigned int 
LocalAlarmSettingsObjectivePlugin_get_serialized_sample_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int 
LocalAlarmSettingsObjectivePlugin_get_serialized_sample_min_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern unsigned int
LocalAlarmSettingsObjectivePlugin_get_serialized_sample_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment,
    const LocalAlarmSettingsObjective * sample);



/* --------------------------------------------------------------------------------------
    Key Management functions:
 * -------------------------------------------------------------------------------------- */

NDDSUSERDllExport extern PRESTypePluginKeyKind 
LocalAlarmSettingsObjectivePlugin_get_key_kind(void);

NDDSUSERDllExport extern unsigned int 
LocalAlarmSettingsObjectivePlugin_get_serialized_key_max_size(
    PRESTypePluginEndpointData endpoint_data,
    RTIBool include_encapsulation,
    RTIEncapsulationId encapsulation_id,
    unsigned int current_alignment);

NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_serialize_key(
    PRESTypePluginEndpointData endpoint_data,
    const LocalAlarmSettingsObjective *sample,
    struct RTICdrStream *stream,
    RTIBool serialize_encapsulation,
    RTIEncapsulationId encapsulation_id,
    RTIBool serialize_key,
    void *endpoint_plugin_qos);

NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_deserialize_key_sample(
    PRESTypePluginEndpointData endpoint_data,
    LocalAlarmSettingsObjective * sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_deserialize_key(
    PRESTypePluginEndpointData endpoint_data,
    LocalAlarmSettingsObjective ** sample,
    RTIBool * drop_sample,
    struct RTICdrStream *stream,
    RTIBool deserialize_encapsulation,
    RTIBool deserialize_key,
    void *endpoint_plugin_qos);


NDDSUSERDllExport extern RTIBool
LocalAlarmSettingsObjectivePlugin_serialized_sample_to_key(
    PRESTypePluginEndpointData endpoint_data,
    LocalAlarmSettingsObjective *sample,
    struct RTICdrStream *stream, 
    RTIBool deserialize_encapsulation,  
    RTIBool deserialize_key, 
    void *endpoint_plugin_qos);

 
NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_instance_to_key(
    PRESTypePluginEndpointData endpoint_data,
    LocalAlarmSettingsObjectiveKeyHolder *key, 
    const LocalAlarmSettingsObjective *instance);

NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_key_to_instance(
    PRESTypePluginEndpointData endpoint_data,
    LocalAlarmSettingsObjective *instance, 
    const LocalAlarmSettingsObjectiveKeyHolder *key);

NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_instance_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    DDS_KeyHash_t *keyhash,
    const LocalAlarmSettingsObjective *instance);

NDDSUSERDllExport extern RTIBool 
LocalAlarmSettingsObjectivePlugin_serialized_sample_to_keyhash(
    PRESTypePluginEndpointData endpoint_data,
    struct RTICdrStream *stream, 
    DDS_KeyHash_t *keyhash,
    RTIBool deserialize_encapsulation,
    void *endpoint_plugin_qos); 
     
/* Plugin Functions */
NDDSUSERDllExport extern struct PRESTypePlugin*
LocalAlarmSettingsObjectivePlugin_new(void);

NDDSUSERDllExport extern void
LocalAlarmSettingsObjectivePlugin_delete(struct PRESTypePlugin *);

} /* namespace ice */

        
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
/* If the code is building on Windows, stop exporting symbols.
*/
#undef NDDSUSERDllExport
#define NDDSUSERDllExport
#endif        

#endif /* icePlugin_1627646664_h */
