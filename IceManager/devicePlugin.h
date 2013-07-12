
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from device.idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/

#ifndef devicePlugin_780045361_h
#define devicePlugin_780045361_h

#include "device.h"




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

} /* namespace ice */

        
#if (defined(RTI_WIN32) || defined (RTI_WINCE)) && defined(NDDS_USER_DLL_EXPORT)
/* If the code is building on Windows, stop exporting symbols.
*/
#undef NDDSUSERDllExport
#define NDDSUSERDllExport
#endif        

#endif /* devicePlugin_780045361_h */
