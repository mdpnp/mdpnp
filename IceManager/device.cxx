
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from device.idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/


#ifndef NDDS_STANDALONE_TYPE
    #ifdef __cplusplus
        #ifndef ndds_cpp_h
            #include "ndds/ndds_cpp.h"
        #endif
        #ifndef dds_c_log_impl_h              
            #include "dds_c/dds_c_log_impl.h"                                
        #endif        
    #else
        #ifndef ndds_c_h
            #include "ndds/ndds_c.h"
        #endif
    #endif
    
    #ifndef cdr_type_h
        #include "cdr/cdr_type.h"
    #endif    

    #ifndef osapi_heap_h
        #include "osapi/osapi_heap.h" 
    #endif
#else
    #include "ndds_standalone_type.h"
#endif



#include "device.h"


namespace ice{
/* ========================================================================= */
const char *ImageTYPENAME = "ice::Image";

DDS_TypeCode* Image_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode Image_g_tc_raster_sequence = DDS_INITIALIZE_SEQUENCE_TYPECODE(65530,NULL);

    static DDS_TypeCode_Member Image_g_tc_members[3]=
    {
        {
            (char *)"raster",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"width",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"height",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        }
    };

    static DDS_TypeCode Image_g_tc =
    {{
        DDS_TK_STRUCT,/* Kind */
        DDS_BOOLEAN_FALSE, /* Ignored */
        -1,/* Ignored */
        (char *)"ice::Image", /* Name */
        NULL, /* Ignored */
        0, /* Ignored */
        0, /* Ignored */
        NULL, /* Ignored */
        3, /* Number of members */
        Image_g_tc_members, /* Members */
        DDS_VM_NONE /* Ignored */
    }}; /* Type code for Image*/

    if (is_initialized) {
        return &Image_g_tc;
    }

    Image_g_tc_raster_sequence._data._typeCode = (RTICdrTypeCode *)&DDS_g_tc_octet;

    Image_g_tc_members[0]._representation._typeCode = (RTICdrTypeCode *)&Image_g_tc_raster_sequence;
    Image_g_tc_members[1]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;
    Image_g_tc_members[2]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;

    is_initialized = RTI_TRUE;

    return &Image_g_tc;
}


RTIBool Image_initialize(
    Image* sample) {
  return ::ice::Image_initialize_ex(sample,RTI_TRUE,RTI_TRUE);
}
        
RTIBool Image_initialize_ex(
    Image* sample,RTIBool allocatePointers,RTIBool allocateMemory)
{

    void* buffer = NULL;
    if (buffer) {} /* To avoid warnings */
        
    
    if (allocatePointers) {} /* To avoid warnings */
    if (allocateMemory) {} /* To avoid warnings */

    if (allocateMemory) {
        DDS_OctetSeq_initialize(&sample->raster);
        if (!DDS_OctetSeq_set_maximum(&sample->raster,
                (65530))) {
            return RTI_FALSE;
        }
    } else {
        DDS_OctetSeq_set_length(&sample->raster, 0); 
    }
            

    if (!RTICdrType_initLong(&sample->width)) {
        return RTI_FALSE;
    }                
            

    if (!RTICdrType_initLong(&sample->height)) {
        return RTI_FALSE;
    }                
            


    return RTI_TRUE;
}

void Image_finalize(
    Image* sample)
{
    ::ice::Image_finalize_ex(sample,RTI_TRUE);
}
        
void Image_finalize_ex(
    Image* sample,RTIBool deletePointers)
{        
    if (sample) { } /* To avoid warnings */
    if (deletePointers) {} /* To avoid warnings */


    DDS_OctetSeq_finalize(&sample->raster);
            



}

RTIBool Image_copy(
    Image* dst,
    const Image* src)
{        

    if (!DDS_OctetSeq_copy_no_alloc(&dst->raster,
                                          &src->raster)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyLong(
        &dst->width, &src->width)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyLong(
        &dst->height, &src->height)) {
        return RTI_FALSE;
    }
            


    return RTI_TRUE;
}


/**
 * <<IMPLEMENTATION>>
 *
 * Defines:  TSeq, T
 *
 * Configure and implement 'Image' sequence class.
 */
#define T Image
#define TSeq ImageSeq
#define T_initialize_ex ::ice::Image_initialize_ex
#define T_finalize_ex   ::ice::Image_finalize_ex
#define T_copy       ::ice::Image_copy

#ifndef NDDS_STANDALONE_TYPE
#include "dds_c/generic/dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp/generic/dds_cpp_sequence_TSeq.gen"
#endif
#else
#include "dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp_sequence_TSeq.gen"
#endif
#endif

#undef T_copy
#undef T_finalize_ex
#undef T_initialize_ex
#undef TSeq
#undef T

/* ========================================================================= */
const char *DeviceIdentityTYPENAME = "ice::DeviceIdentity";

DDS_TypeCode* DeviceIdentity_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode DeviceIdentity_g_tc_universal_device_identifier_string = DDS_INITIALIZE_STRING_TYPECODE(256);
    static DDS_TypeCode DeviceIdentity_g_tc_manufacturer_string = DDS_INITIALIZE_STRING_TYPECODE(128);
    static DDS_TypeCode DeviceIdentity_g_tc_model_string = DDS_INITIALIZE_STRING_TYPECODE(128);

    static DDS_TypeCode_Member DeviceIdentity_g_tc_members[4]=
    {
        {
            (char *)"universal_device_identifier",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_TRUE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"manufacturer",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"model",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"icon",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        }
    };

    static DDS_TypeCode DeviceIdentity_g_tc =
    {{
        DDS_TK_STRUCT,/* Kind */
        DDS_BOOLEAN_FALSE, /* Ignored */
        -1,/* Ignored */
        (char *)"ice::DeviceIdentity", /* Name */
        NULL, /* Ignored */
        0, /* Ignored */
        0, /* Ignored */
        NULL, /* Ignored */
        4, /* Number of members */
        DeviceIdentity_g_tc_members, /* Members */
        DDS_VM_NONE /* Ignored */
    }}; /* Type code for DeviceIdentity*/

    if (is_initialized) {
        return &DeviceIdentity_g_tc;
    }


    DeviceIdentity_g_tc_members[0]._representation._typeCode = (RTICdrTypeCode *)&DeviceIdentity_g_tc_universal_device_identifier_string;
    DeviceIdentity_g_tc_members[1]._representation._typeCode = (RTICdrTypeCode *)&DeviceIdentity_g_tc_manufacturer_string;
    DeviceIdentity_g_tc_members[2]._representation._typeCode = (RTICdrTypeCode *)&DeviceIdentity_g_tc_model_string;
    DeviceIdentity_g_tc_members[3]._representation._typeCode = (RTICdrTypeCode *)ice::Image_get_typecode();

    is_initialized = RTI_TRUE;

    return &DeviceIdentity_g_tc;
}


RTIBool DeviceIdentity_initialize(
    DeviceIdentity* sample) {
  return ::ice::DeviceIdentity_initialize_ex(sample,RTI_TRUE,RTI_TRUE);
}
        
RTIBool DeviceIdentity_initialize_ex(
    DeviceIdentity* sample,RTIBool allocatePointers,RTIBool allocateMemory)
{
        
    
    if (allocatePointers) {} /* To avoid warnings */
    if (allocateMemory) {} /* To avoid warnings */

    if (allocateMemory) {
        sample->universal_device_identifier = DDS_String_alloc((256));
        if (sample->universal_device_identifier == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->universal_device_identifier != NULL) { 
            sample->universal_device_identifier[0] = '\0';
        }
    }
            

    if (allocateMemory) {
        sample->manufacturer = DDS_String_alloc((128));
        if (sample->manufacturer == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->manufacturer != NULL) { 
            sample->manufacturer[0] = '\0';
        }
    }
            

    if (allocateMemory) {
        sample->model = DDS_String_alloc((128));
        if (sample->model == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->model != NULL) { 
            sample->model[0] = '\0';
        }
    }
            

    if (!ice::Image_initialize_ex(&sample->icon,allocatePointers,allocateMemory)) {
        return RTI_FALSE;
    }
            


    return RTI_TRUE;
}

void DeviceIdentity_finalize(
    DeviceIdentity* sample)
{
    ::ice::DeviceIdentity_finalize_ex(sample,RTI_TRUE);
}
        
void DeviceIdentity_finalize_ex(
    DeviceIdentity* sample,RTIBool deletePointers)
{        
    if (sample) { } /* To avoid warnings */
    if (deletePointers) {} /* To avoid warnings */


    DDS_String_free(sample->universal_device_identifier);                
            

    DDS_String_free(sample->manufacturer);                
            

    DDS_String_free(sample->model);                
            

    ice::Image_finalize_ex(&sample->icon,deletePointers);
            

}

RTIBool DeviceIdentity_copy(
    DeviceIdentity* dst,
    const DeviceIdentity* src)
{        

    if (!RTICdrType_copyString(
        dst->universal_device_identifier, src->universal_device_identifier, (256) + 1)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyString(
        dst->manufacturer, src->manufacturer, (128) + 1)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyString(
        dst->model, src->model, (128) + 1)) {
        return RTI_FALSE;
    }
            

    if (!ice::Image_copy(
        &dst->icon, &src->icon)) {
        return RTI_FALSE;
    }
            


    return RTI_TRUE;
}


/**
 * <<IMPLEMENTATION>>
 *
 * Defines:  TSeq, T
 *
 * Configure and implement 'DeviceIdentity' sequence class.
 */
#define T DeviceIdentity
#define TSeq DeviceIdentitySeq
#define T_initialize_ex ::ice::DeviceIdentity_initialize_ex
#define T_finalize_ex   ::ice::DeviceIdentity_finalize_ex
#define T_copy       ::ice::DeviceIdentity_copy

#ifndef NDDS_STANDALONE_TYPE
#include "dds_c/generic/dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp/generic/dds_cpp_sequence_TSeq.gen"
#endif
#else
#include "dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp_sequence_TSeq.gen"
#endif
#endif

#undef T_copy
#undef T_finalize_ex
#undef T_initialize_ex
#undef TSeq
#undef T

/* ========================================================================= */
const char *ConnectionStateTYPENAME = "ice::ConnectionState";

DDS_TypeCode* ConnectionState_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode_Member ConnectionState_g_tc_members[5] =
    {
        {
            (char *)"Connected",/* Member name */
            {
                0,/* Ignored */
                DDS_BOOLEAN_FALSE, /* Ignored */
                -1, /* Ignored */
                NULL /* Ignored */
            },
            Connected, /* Enumerator ordinal */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Ignored */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"Connecting",/* Member name */
            {
                0,/* Ignored */
                DDS_BOOLEAN_FALSE, /* Ignored */
                -1, /* Ignored */
                NULL /* Ignored */
            },
            Connecting, /* Enumerator ordinal */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Ignored */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"Negotiating",/* Member name */
            {
                0,/* Ignored */
                DDS_BOOLEAN_FALSE, /* Ignored */
                -1, /* Ignored */
                NULL /* Ignored */
            },
            Negotiating, /* Enumerator ordinal */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Ignored */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"Disconnecting",/* Member name */
            {
                0,/* Ignored */
                DDS_BOOLEAN_FALSE, /* Ignored */
                -1, /* Ignored */
                NULL /* Ignored */
            },
            Disconnecting, /* Enumerator ordinal */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Ignored */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"Disconnected",/* Member name */
            {
                0,/* Ignored */
                DDS_BOOLEAN_FALSE, /* Ignored */
                -1, /* Ignored */
                NULL /* Ignored */
            },
            Disconnected, /* Enumerator ordinal */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Ignored */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        }
    };

    static DDS_TypeCode ConnectionState_g_tc = 
    {{
        DDS_TK_ENUM, /* Kind */
        DDS_BOOLEAN_FALSE, /* Ignored */
        -1, /* Ignored */
        (char *)"ice::ConnectionState", /* Name */
        NULL, /* Ignored */
        0, /* Ignored */
        0, /* Ignored */
        NULL, /* Ignored */
        5, /* Number of enumerators */
        ConnectionState_g_tc_members, /* Enumerators */
        DDS_VM_NONE /* Ignored */
}    };

    if (is_initialized) {
        return &ConnectionState_g_tc;
    }

    is_initialized = RTI_TRUE;
    return &ConnectionState_g_tc;
}
 

RTIBool ConnectionState_initialize(
    ConnectionState* sample)
{
    *sample = Connected;
    return RTI_TRUE;
}
        
RTIBool ConnectionState_initialize_ex(
    ConnectionState* sample,RTIBool allocatePointers,RTIBool allocateMemory)
{
    /* The following method initializes the enum value to zero. However,
     * some enumerations may not have a member with the value zero. In such
     * cases, it may be desirable to modify the generated code to use a valid
     * enumeration member instead.
     */
    if (allocatePointers) {} /* To avoid warnings */
    if (allocateMemory) {} /* To avoid warnings */
    *sample = Connected;
    return RTI_TRUE;
}

void ConnectionState_finalize(
    ConnectionState* sample)
{
    if (sample) {} /* To avoid warnings */
    /* empty */
}
        
void ConnectionState_finalize_ex(
    ConnectionState* sample,RTIBool deletePointers)
{
    if (sample) {} /* To avoid warnings */
    if (deletePointers) {} /* To avoid warnings */
    /* empty */
}

RTIBool ConnectionState_copy(
    ConnectionState* dst,
    const ConnectionState* src)
{
    return RTICdrType_copyEnum((RTICdrEnum *)dst, (RTICdrEnum *)src);
}


RTIBool ConnectionState_getValues(ConnectionStateSeq * values) 
    
{
    int i = 0;
    ConnectionState * buffer;


    if (!values->maximum(5)) {
        return RTI_FALSE;
    }

    if (!values->length(5)) {
        return RTI_FALSE;
    }

    buffer = values->get_contiguous_buffer();
    
    buffer[i] = Connected;
    i++;
    
    buffer[i] = Connecting;
    i++;
    
    buffer[i] = Negotiating;
    i++;
    
    buffer[i] = Disconnecting;
    i++;
    
    buffer[i] = Disconnected;
    i++;
    

    return RTI_TRUE;
}

/**
 * <<IMPLEMENTATION>>
 *
 * Defines:  TSeq, T
 *
 * Configure and implement 'ConnectionState' sequence class.
 */
#define T ConnectionState
#define TSeq ConnectionStateSeq
#define T_initialize_ex ConnectionState_initialize_ex
#define T_finalize_ex   ConnectionState_finalize_ex
#define T_copy       ConnectionState_copy

#ifndef NDDS_STANDALONE_TYPE
#include "dds_c/generic/dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp/generic/dds_cpp_sequence_TSeq.gen"
#endif
#else
#include "dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp_sequence_TSeq.gen"
#endif
#endif

#undef T_copy
#undef T_finalize_ex
#undef T_initialize_ex
#undef TSeq
#undef T
/* ========================================================================= */
const char *ConnectionTypeTYPENAME = "ice::ConnectionType";

DDS_TypeCode* ConnectionType_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode_Member ConnectionType_g_tc_members[3] =
    {
        {
            (char *)"Serial",/* Member name */
            {
                0,/* Ignored */
                DDS_BOOLEAN_FALSE, /* Ignored */
                -1, /* Ignored */
                NULL /* Ignored */
            },
            Serial, /* Enumerator ordinal */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Ignored */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"Simulated",/* Member name */
            {
                0,/* Ignored */
                DDS_BOOLEAN_FALSE, /* Ignored */
                -1, /* Ignored */
                NULL /* Ignored */
            },
            Simulated, /* Enumerator ordinal */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Ignored */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"Network",/* Member name */
            {
                0,/* Ignored */
                DDS_BOOLEAN_FALSE, /* Ignored */
                -1, /* Ignored */
                NULL /* Ignored */
            },
            Network, /* Enumerator ordinal */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Ignored */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        }
    };

    static DDS_TypeCode ConnectionType_g_tc = 
    {{
        DDS_TK_ENUM, /* Kind */
        DDS_BOOLEAN_FALSE, /* Ignored */
        -1, /* Ignored */
        (char *)"ice::ConnectionType", /* Name */
        NULL, /* Ignored */
        0, /* Ignored */
        0, /* Ignored */
        NULL, /* Ignored */
        3, /* Number of enumerators */
        ConnectionType_g_tc_members, /* Enumerators */
        DDS_VM_NONE /* Ignored */
}    };

    if (is_initialized) {
        return &ConnectionType_g_tc;
    }

    is_initialized = RTI_TRUE;
    return &ConnectionType_g_tc;
}
 

RTIBool ConnectionType_initialize(
    ConnectionType* sample)
{
    *sample = Serial;
    return RTI_TRUE;
}
        
RTIBool ConnectionType_initialize_ex(
    ConnectionType* sample,RTIBool allocatePointers,RTIBool allocateMemory)
{
    /* The following method initializes the enum value to zero. However,
     * some enumerations may not have a member with the value zero. In such
     * cases, it may be desirable to modify the generated code to use a valid
     * enumeration member instead.
     */
    if (allocatePointers) {} /* To avoid warnings */
    if (allocateMemory) {} /* To avoid warnings */
    *sample = Serial;
    return RTI_TRUE;
}

void ConnectionType_finalize(
    ConnectionType* sample)
{
    if (sample) {} /* To avoid warnings */
    /* empty */
}
        
void ConnectionType_finalize_ex(
    ConnectionType* sample,RTIBool deletePointers)
{
    if (sample) {} /* To avoid warnings */
    if (deletePointers) {} /* To avoid warnings */
    /* empty */
}

RTIBool ConnectionType_copy(
    ConnectionType* dst,
    const ConnectionType* src)
{
    return RTICdrType_copyEnum((RTICdrEnum *)dst, (RTICdrEnum *)src);
}


RTIBool ConnectionType_getValues(ConnectionTypeSeq * values) 
    
{
    int i = 0;
    ConnectionType * buffer;


    if (!values->maximum(3)) {
        return RTI_FALSE;
    }

    if (!values->length(3)) {
        return RTI_FALSE;
    }

    buffer = values->get_contiguous_buffer();
    
    buffer[i] = Serial;
    i++;
    
    buffer[i] = Simulated;
    i++;
    
    buffer[i] = Network;
    i++;
    

    return RTI_TRUE;
}

/**
 * <<IMPLEMENTATION>>
 *
 * Defines:  TSeq, T
 *
 * Configure and implement 'ConnectionType' sequence class.
 */
#define T ConnectionType
#define TSeq ConnectionTypeSeq
#define T_initialize_ex ConnectionType_initialize_ex
#define T_finalize_ex   ConnectionType_finalize_ex
#define T_copy       ConnectionType_copy

#ifndef NDDS_STANDALONE_TYPE
#include "dds_c/generic/dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp/generic/dds_cpp_sequence_TSeq.gen"
#endif
#else
#include "dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp_sequence_TSeq.gen"
#endif
#endif

#undef T_copy
#undef T_finalize_ex
#undef T_initialize_ex
#undef TSeq
#undef T
/* ========================================================================= */
const char *DeviceConnectivityTYPENAME = "ice::DeviceConnectivity";

DDS_TypeCode* DeviceConnectivity_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode DeviceConnectivity_g_tc_universal_device_identifier_string = DDS_INITIALIZE_STRING_TYPECODE(256);
    static DDS_TypeCode DeviceConnectivity_g_tc_info_string = DDS_INITIALIZE_STRING_TYPECODE(128);
    static DDS_TypeCode DeviceConnectivity_g_tc_valid_targets_string = DDS_INITIALIZE_STRING_TYPECODE(128);
    static DDS_TypeCode DeviceConnectivity_g_tc_valid_targets_sequence = DDS_INITIALIZE_SEQUENCE_TYPECODE(128,NULL);

    static DDS_TypeCode_Member DeviceConnectivity_g_tc_members[5]=
    {
        {
            (char *)"universal_device_identifier",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_TRUE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"state",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"type",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"info",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"valid_targets",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        }
    };

    static DDS_TypeCode DeviceConnectivity_g_tc =
    {{
        DDS_TK_STRUCT,/* Kind */
        DDS_BOOLEAN_FALSE, /* Ignored */
        -1,/* Ignored */
        (char *)"ice::DeviceConnectivity", /* Name */
        NULL, /* Ignored */
        0, /* Ignored */
        0, /* Ignored */
        NULL, /* Ignored */
        5, /* Number of members */
        DeviceConnectivity_g_tc_members, /* Members */
        DDS_VM_NONE /* Ignored */
    }}; /* Type code for DeviceConnectivity*/

    if (is_initialized) {
        return &DeviceConnectivity_g_tc;
    }

    DeviceConnectivity_g_tc_valid_targets_sequence._data._typeCode = (RTICdrTypeCode *)&DeviceConnectivity_g_tc_valid_targets_string;

    DeviceConnectivity_g_tc_members[0]._representation._typeCode = (RTICdrTypeCode *)&DeviceConnectivity_g_tc_universal_device_identifier_string;
    DeviceConnectivity_g_tc_members[1]._representation._typeCode = (RTICdrTypeCode *)ice::ConnectionState_get_typecode();
    DeviceConnectivity_g_tc_members[2]._representation._typeCode = (RTICdrTypeCode *)ice::ConnectionType_get_typecode();
    DeviceConnectivity_g_tc_members[3]._representation._typeCode = (RTICdrTypeCode *)&DeviceConnectivity_g_tc_info_string;
    DeviceConnectivity_g_tc_members[4]._representation._typeCode = (RTICdrTypeCode *)&DeviceConnectivity_g_tc_valid_targets_sequence;

    is_initialized = RTI_TRUE;

    return &DeviceConnectivity_g_tc;
}


RTIBool DeviceConnectivity_initialize(
    DeviceConnectivity* sample) {
  return ::ice::DeviceConnectivity_initialize_ex(sample,RTI_TRUE,RTI_TRUE);
}
        
RTIBool DeviceConnectivity_initialize_ex(
    DeviceConnectivity* sample,RTIBool allocatePointers,RTIBool allocateMemory)
{

    void* buffer = NULL;
    if (buffer) {} /* To avoid warnings */
        
    
    if (allocatePointers) {} /* To avoid warnings */
    if (allocateMemory) {} /* To avoid warnings */

    if (allocateMemory) {
        sample->universal_device_identifier = DDS_String_alloc((256));
        if (sample->universal_device_identifier == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->universal_device_identifier != NULL) { 
            sample->universal_device_identifier[0] = '\0';
        }
    }
            

    if (!ice::ConnectionState_initialize_ex(&sample->state,allocatePointers,allocateMemory)) {
        return RTI_FALSE;
    }
            

    if (!ice::ConnectionType_initialize_ex(&sample->type,allocatePointers,allocateMemory)) {
        return RTI_FALSE;
    }
            

    if (allocateMemory) {
        sample->info = DDS_String_alloc((128));
        if (sample->info == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->info != NULL) { 
            sample->info[0] = '\0';
        }
    }
            

    if (allocateMemory) {    
        DDS_StringSeq_initialize(&sample->valid_targets);
        if (!DDS_StringSeq_set_maximum(&sample->valid_targets,
                                       (128))) {
            return RTI_FALSE;
        }
        buffer = DDS_StringSeq_get_contiguous_bufferI(&sample->valid_targets);
        if (buffer == NULL) {
            return RTI_FALSE;
        }
        if (!RTICdrType_initStringArray(buffer, (128),(128)+1,
            RTI_CDR_CHAR_TYPE)) {
            return RTI_FALSE;
        }
    } else {
        DDS_StringSeq_set_length(&sample->valid_targets, 0);
    }
            


    return RTI_TRUE;
}

void DeviceConnectivity_finalize(
    DeviceConnectivity* sample)
{
    ::ice::DeviceConnectivity_finalize_ex(sample,RTI_TRUE);
}
        
void DeviceConnectivity_finalize_ex(
    DeviceConnectivity* sample,RTIBool deletePointers)
{        
    if (sample) { } /* To avoid warnings */
    if (deletePointers) {} /* To avoid warnings */


    DDS_String_free(sample->universal_device_identifier);                
            

    ice::ConnectionState_finalize_ex(&sample->state,deletePointers);
            

    ice::ConnectionType_finalize_ex(&sample->type,deletePointers);
            

    DDS_String_free(sample->info);                
            

    DDS_StringSeq_finalize(&sample->valid_targets);
            

}

RTIBool DeviceConnectivity_copy(
    DeviceConnectivity* dst,
    const DeviceConnectivity* src)
{        

    if (!RTICdrType_copyString(
        dst->universal_device_identifier, src->universal_device_identifier, (256) + 1)) {
        return RTI_FALSE;
    }
            

    if (!ice::ConnectionState_copy(
        &dst->state, &src->state)) {
        return RTI_FALSE;
    }
            

    if (!ice::ConnectionType_copy(
        &dst->type, &src->type)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyString(
        dst->info, src->info, (128) + 1)) {
        return RTI_FALSE;
    }
            

    if (!DDS_StringSeq_copy_no_alloc(&dst->valid_targets,
                                      &src->valid_targets)) {
        return RTI_FALSE;
    }
            


    return RTI_TRUE;
}


/**
 * <<IMPLEMENTATION>>
 *
 * Defines:  TSeq, T
 *
 * Configure and implement 'DeviceConnectivity' sequence class.
 */
#define T DeviceConnectivity
#define TSeq DeviceConnectivitySeq
#define T_initialize_ex ::ice::DeviceConnectivity_initialize_ex
#define T_finalize_ex   ::ice::DeviceConnectivity_finalize_ex
#define T_copy       ::ice::DeviceConnectivity_copy

#ifndef NDDS_STANDALONE_TYPE
#include "dds_c/generic/dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp/generic/dds_cpp_sequence_TSeq.gen"
#endif
#else
#include "dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp_sequence_TSeq.gen"
#endif
#endif

#undef T_copy
#undef T_finalize_ex
#undef T_initialize_ex
#undef TSeq
#undef T

/* ========================================================================= */
const char *DeviceConnectivityObjectiveTYPENAME = "ice::DeviceConnectivityObjective";

DDS_TypeCode* DeviceConnectivityObjective_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode DeviceConnectivityObjective_g_tc_universal_device_identifier_string = DDS_INITIALIZE_STRING_TYPECODE(256);
    static DDS_TypeCode DeviceConnectivityObjective_g_tc_target_string = DDS_INITIALIZE_STRING_TYPECODE(128);

    static DDS_TypeCode_Member DeviceConnectivityObjective_g_tc_members[3]=
    {
        {
            (char *)"universal_device_identifier",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_TRUE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"connected",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"target",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        }
    };

    static DDS_TypeCode DeviceConnectivityObjective_g_tc =
    {{
        DDS_TK_STRUCT,/* Kind */
        DDS_BOOLEAN_FALSE, /* Ignored */
        -1,/* Ignored */
        (char *)"ice::DeviceConnectivityObjective", /* Name */
        NULL, /* Ignored */
        0, /* Ignored */
        0, /* Ignored */
        NULL, /* Ignored */
        3, /* Number of members */
        DeviceConnectivityObjective_g_tc_members, /* Members */
        DDS_VM_NONE /* Ignored */
    }}; /* Type code for DeviceConnectivityObjective*/

    if (is_initialized) {
        return &DeviceConnectivityObjective_g_tc;
    }


    DeviceConnectivityObjective_g_tc_members[0]._representation._typeCode = (RTICdrTypeCode *)&DeviceConnectivityObjective_g_tc_universal_device_identifier_string;
    DeviceConnectivityObjective_g_tc_members[1]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_boolean;
    DeviceConnectivityObjective_g_tc_members[2]._representation._typeCode = (RTICdrTypeCode *)&DeviceConnectivityObjective_g_tc_target_string;

    is_initialized = RTI_TRUE;

    return &DeviceConnectivityObjective_g_tc;
}


RTIBool DeviceConnectivityObjective_initialize(
    DeviceConnectivityObjective* sample) {
  return ::ice::DeviceConnectivityObjective_initialize_ex(sample,RTI_TRUE,RTI_TRUE);
}
        
RTIBool DeviceConnectivityObjective_initialize_ex(
    DeviceConnectivityObjective* sample,RTIBool allocatePointers,RTIBool allocateMemory)
{
        
    
    if (allocatePointers) {} /* To avoid warnings */
    if (allocateMemory) {} /* To avoid warnings */

    if (allocateMemory) {
        sample->universal_device_identifier = DDS_String_alloc((256));
        if (sample->universal_device_identifier == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->universal_device_identifier != NULL) { 
            sample->universal_device_identifier[0] = '\0';
        }
    }
            

    if (!RTICdrType_initBoolean(&sample->connected)) {
        return RTI_FALSE;
    }                
            

    if (allocateMemory) {
        sample->target = DDS_String_alloc((128));
        if (sample->target == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->target != NULL) { 
            sample->target[0] = '\0';
        }
    }
            


    return RTI_TRUE;
}

void DeviceConnectivityObjective_finalize(
    DeviceConnectivityObjective* sample)
{
    ::ice::DeviceConnectivityObjective_finalize_ex(sample,RTI_TRUE);
}
        
void DeviceConnectivityObjective_finalize_ex(
    DeviceConnectivityObjective* sample,RTIBool deletePointers)
{        
    if (sample) { } /* To avoid warnings */
    if (deletePointers) {} /* To avoid warnings */


    DDS_String_free(sample->universal_device_identifier);                
            


    DDS_String_free(sample->target);                
            

}

RTIBool DeviceConnectivityObjective_copy(
    DeviceConnectivityObjective* dst,
    const DeviceConnectivityObjective* src)
{        

    if (!RTICdrType_copyString(
        dst->universal_device_identifier, src->universal_device_identifier, (256) + 1)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyBoolean(
        &dst->connected, &src->connected)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyString(
        dst->target, src->target, (128) + 1)) {
        return RTI_FALSE;
    }
            


    return RTI_TRUE;
}


/**
 * <<IMPLEMENTATION>>
 *
 * Defines:  TSeq, T
 *
 * Configure and implement 'DeviceConnectivityObjective' sequence class.
 */
#define T DeviceConnectivityObjective
#define TSeq DeviceConnectivityObjectiveSeq
#define T_initialize_ex ::ice::DeviceConnectivityObjective_initialize_ex
#define T_finalize_ex   ::ice::DeviceConnectivityObjective_finalize_ex
#define T_copy       ::ice::DeviceConnectivityObjective_copy

#ifndef NDDS_STANDALONE_TYPE
#include "dds_c/generic/dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp/generic/dds_cpp_sequence_TSeq.gen"
#endif
#else
#include "dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp_sequence_TSeq.gen"
#endif
#endif

#undef T_copy
#undef T_finalize_ex
#undef T_initialize_ex
#undef TSeq
#undef T

/* ========================================================================= */
const char *NumericTYPENAME = "ice::Numeric";

DDS_TypeCode* Numeric_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode Numeric_g_tc_universal_device_identifier_string = DDS_INITIALIZE_STRING_TYPECODE(256);

    static DDS_TypeCode_Member Numeric_g_tc_members[3]=
    {
        {
            (char *)"universal_device_identifier",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_TRUE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"name",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_TRUE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"value",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        }
    };

    static DDS_TypeCode Numeric_g_tc =
    {{
        DDS_TK_STRUCT,/* Kind */
        DDS_BOOLEAN_FALSE, /* Ignored */
        -1,/* Ignored */
        (char *)"ice::Numeric", /* Name */
        NULL, /* Ignored */
        0, /* Ignored */
        0, /* Ignored */
        NULL, /* Ignored */
        3, /* Number of members */
        Numeric_g_tc_members, /* Members */
        DDS_VM_NONE /* Ignored */
    }}; /* Type code for Numeric*/

    if (is_initialized) {
        return &Numeric_g_tc;
    }


    Numeric_g_tc_members[0]._representation._typeCode = (RTICdrTypeCode *)&Numeric_g_tc_universal_device_identifier_string;
    Numeric_g_tc_members[1]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;
    Numeric_g_tc_members[2]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_float;

    is_initialized = RTI_TRUE;

    return &Numeric_g_tc;
}


RTIBool Numeric_initialize(
    Numeric* sample) {
  return ::ice::Numeric_initialize_ex(sample,RTI_TRUE,RTI_TRUE);
}
        
RTIBool Numeric_initialize_ex(
    Numeric* sample,RTIBool allocatePointers,RTIBool allocateMemory)
{
        
    
    if (allocatePointers) {} /* To avoid warnings */
    if (allocateMemory) {} /* To avoid warnings */

    if (allocateMemory) {
        sample->universal_device_identifier = DDS_String_alloc((256));
        if (sample->universal_device_identifier == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->universal_device_identifier != NULL) { 
            sample->universal_device_identifier[0] = '\0';
        }
    }
            

    if (!RTICdrType_initLong(&sample->name)) {
        return RTI_FALSE;
    }                
            

    if (!RTICdrType_initFloat(&sample->value)) {
        return RTI_FALSE;
    }                
            


    return RTI_TRUE;
}

void Numeric_finalize(
    Numeric* sample)
{
    ::ice::Numeric_finalize_ex(sample,RTI_TRUE);
}
        
void Numeric_finalize_ex(
    Numeric* sample,RTIBool deletePointers)
{        
    if (sample) { } /* To avoid warnings */
    if (deletePointers) {} /* To avoid warnings */


    DDS_String_free(sample->universal_device_identifier);                
            



}

RTIBool Numeric_copy(
    Numeric* dst,
    const Numeric* src)
{        

    if (!RTICdrType_copyString(
        dst->universal_device_identifier, src->universal_device_identifier, (256) + 1)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyLong(
        &dst->name, &src->name)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyFloat(
        &dst->value, &src->value)) {
        return RTI_FALSE;
    }
            


    return RTI_TRUE;
}


/**
 * <<IMPLEMENTATION>>
 *
 * Defines:  TSeq, T
 *
 * Configure and implement 'Numeric' sequence class.
 */
#define T Numeric
#define TSeq NumericSeq
#define T_initialize_ex ::ice::Numeric_initialize_ex
#define T_finalize_ex   ::ice::Numeric_finalize_ex
#define T_copy       ::ice::Numeric_copy

#ifndef NDDS_STANDALONE_TYPE
#include "dds_c/generic/dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp/generic/dds_cpp_sequence_TSeq.gen"
#endif
#else
#include "dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp_sequence_TSeq.gen"
#endif
#endif

#undef T_copy
#undef T_finalize_ex
#undef T_initialize_ex
#undef TSeq
#undef T

/* ========================================================================= */
const char *SampleArrayTYPENAME = "ice::SampleArray";

DDS_TypeCode* SampleArray_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode SampleArray_g_tc_universal_device_identifier_string = DDS_INITIALIZE_STRING_TYPECODE(256);
    static DDS_TypeCode SampleArray_g_tc_values_sequence = DDS_INITIALIZE_SEQUENCE_TYPECODE(100,NULL);

    static DDS_TypeCode_Member SampleArray_g_tc_members[4]=
    {
        {
            (char *)"universal_device_identifier",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_TRUE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"name",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_TRUE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"values",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"millisecondsPerSample",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        }
    };

    static DDS_TypeCode SampleArray_g_tc =
    {{
        DDS_TK_STRUCT,/* Kind */
        DDS_BOOLEAN_FALSE, /* Ignored */
        -1,/* Ignored */
        (char *)"ice::SampleArray", /* Name */
        NULL, /* Ignored */
        0, /* Ignored */
        0, /* Ignored */
        NULL, /* Ignored */
        4, /* Number of members */
        SampleArray_g_tc_members, /* Members */
        DDS_VM_NONE /* Ignored */
    }}; /* Type code for SampleArray*/

    if (is_initialized) {
        return &SampleArray_g_tc;
    }

    SampleArray_g_tc_values_sequence._data._typeCode = (RTICdrTypeCode *)&DDS_g_tc_float;

    SampleArray_g_tc_members[0]._representation._typeCode = (RTICdrTypeCode *)&SampleArray_g_tc_universal_device_identifier_string;
    SampleArray_g_tc_members[1]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;
    SampleArray_g_tc_members[2]._representation._typeCode = (RTICdrTypeCode *)&SampleArray_g_tc_values_sequence;
    SampleArray_g_tc_members[3]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;

    is_initialized = RTI_TRUE;

    return &SampleArray_g_tc;
}


RTIBool SampleArray_initialize(
    SampleArray* sample) {
  return ::ice::SampleArray_initialize_ex(sample,RTI_TRUE,RTI_TRUE);
}
        
RTIBool SampleArray_initialize_ex(
    SampleArray* sample,RTIBool allocatePointers,RTIBool allocateMemory)
{

    void* buffer = NULL;
    if (buffer) {} /* To avoid warnings */
        
    
    if (allocatePointers) {} /* To avoid warnings */
    if (allocateMemory) {} /* To avoid warnings */

    if (allocateMemory) {
        sample->universal_device_identifier = DDS_String_alloc((256));
        if (sample->universal_device_identifier == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->universal_device_identifier != NULL) { 
            sample->universal_device_identifier[0] = '\0';
        }
    }
            

    if (!RTICdrType_initLong(&sample->name)) {
        return RTI_FALSE;
    }                
            

    if (allocateMemory) {
        DDS_FloatSeq_initialize(&sample->values);
        if (!DDS_FloatSeq_set_maximum(&sample->values,
                (100))) {
            return RTI_FALSE;
        }
    } else {
        DDS_FloatSeq_set_length(&sample->values, 0); 
    }
            

    if (!RTICdrType_initLong(&sample->millisecondsPerSample)) {
        return RTI_FALSE;
    }                
            


    return RTI_TRUE;
}

void SampleArray_finalize(
    SampleArray* sample)
{
    ::ice::SampleArray_finalize_ex(sample,RTI_TRUE);
}
        
void SampleArray_finalize_ex(
    SampleArray* sample,RTIBool deletePointers)
{        
    if (sample) { } /* To avoid warnings */
    if (deletePointers) {} /* To avoid warnings */


    DDS_String_free(sample->universal_device_identifier);                
            


    DDS_FloatSeq_finalize(&sample->values);
            


}

RTIBool SampleArray_copy(
    SampleArray* dst,
    const SampleArray* src)
{        

    if (!RTICdrType_copyString(
        dst->universal_device_identifier, src->universal_device_identifier, (256) + 1)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyLong(
        &dst->name, &src->name)) {
        return RTI_FALSE;
    }
            

    if (!DDS_FloatSeq_copy_no_alloc(&dst->values,
                                          &src->values)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyLong(
        &dst->millisecondsPerSample, &src->millisecondsPerSample)) {
        return RTI_FALSE;
    }
            


    return RTI_TRUE;
}


/**
 * <<IMPLEMENTATION>>
 *
 * Defines:  TSeq, T
 *
 * Configure and implement 'SampleArray' sequence class.
 */
#define T SampleArray
#define TSeq SampleArraySeq
#define T_initialize_ex ::ice::SampleArray_initialize_ex
#define T_finalize_ex   ::ice::SampleArray_finalize_ex
#define T_copy       ::ice::SampleArray_copy

#ifndef NDDS_STANDALONE_TYPE
#include "dds_c/generic/dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp/generic/dds_cpp_sequence_TSeq.gen"
#endif
#else
#include "dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp_sequence_TSeq.gen"
#endif
#endif

#undef T_copy
#undef T_finalize_ex
#undef T_initialize_ex
#undef TSeq
#undef T

/* ========================================================================= */
const char *TextTYPENAME = "ice::Text";

DDS_TypeCode* Text_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode Text_g_tc_universal_device_identifier_string = DDS_INITIALIZE_STRING_TYPECODE(256);
    static DDS_TypeCode Text_g_tc_value_string = DDS_INITIALIZE_STRING_TYPECODE(256);

    static DDS_TypeCode_Member Text_g_tc_members[3]=
    {
        {
            (char *)"universal_device_identifier",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_TRUE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"name",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_TRUE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        },
        {
            (char *)"value",/* Member name */
            {
                0,/* Representation ID */
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            DDS_BOOLEAN_FALSE, /* Is a key? */
            DDS_PRIVATE_MEMBER,/* Ignored */
            0,/* Ignored */
            NULL/* Ignored */
        }
    };

    static DDS_TypeCode Text_g_tc =
    {{
        DDS_TK_STRUCT,/* Kind */
        DDS_BOOLEAN_FALSE, /* Ignored */
        -1,/* Ignored */
        (char *)"ice::Text", /* Name */
        NULL, /* Ignored */
        0, /* Ignored */
        0, /* Ignored */
        NULL, /* Ignored */
        3, /* Number of members */
        Text_g_tc_members, /* Members */
        DDS_VM_NONE /* Ignored */
    }}; /* Type code for Text*/

    if (is_initialized) {
        return &Text_g_tc;
    }


    Text_g_tc_members[0]._representation._typeCode = (RTICdrTypeCode *)&Text_g_tc_universal_device_identifier_string;
    Text_g_tc_members[1]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;
    Text_g_tc_members[2]._representation._typeCode = (RTICdrTypeCode *)&Text_g_tc_value_string;

    is_initialized = RTI_TRUE;

    return &Text_g_tc;
}


RTIBool Text_initialize(
    Text* sample) {
  return ::ice::Text_initialize_ex(sample,RTI_TRUE,RTI_TRUE);
}
        
RTIBool Text_initialize_ex(
    Text* sample,RTIBool allocatePointers,RTIBool allocateMemory)
{
        
    
    if (allocatePointers) {} /* To avoid warnings */
    if (allocateMemory) {} /* To avoid warnings */

    if (allocateMemory) {
        sample->universal_device_identifier = DDS_String_alloc((256));
        if (sample->universal_device_identifier == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->universal_device_identifier != NULL) { 
            sample->universal_device_identifier[0] = '\0';
        }
    }
            

    if (!RTICdrType_initLong(&sample->name)) {
        return RTI_FALSE;
    }                
            

    if (allocateMemory) {
        sample->value = DDS_String_alloc((256));
        if (sample->value == NULL) {
            return RTI_FALSE;
        }
    } else {
        if (sample->value != NULL) { 
            sample->value[0] = '\0';
        }
    }
            


    return RTI_TRUE;
}

void Text_finalize(
    Text* sample)
{
    ::ice::Text_finalize_ex(sample,RTI_TRUE);
}
        
void Text_finalize_ex(
    Text* sample,RTIBool deletePointers)
{        
    if (sample) { } /* To avoid warnings */
    if (deletePointers) {} /* To avoid warnings */


    DDS_String_free(sample->universal_device_identifier);                
            


    DDS_String_free(sample->value);                
            

}

RTIBool Text_copy(
    Text* dst,
    const Text* src)
{        

    if (!RTICdrType_copyString(
        dst->universal_device_identifier, src->universal_device_identifier, (256) + 1)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyLong(
        &dst->name, &src->name)) {
        return RTI_FALSE;
    }
            

    if (!RTICdrType_copyString(
        dst->value, src->value, (256) + 1)) {
        return RTI_FALSE;
    }
            


    return RTI_TRUE;
}


/**
 * <<IMPLEMENTATION>>
 *
 * Defines:  TSeq, T
 *
 * Configure and implement 'Text' sequence class.
 */
#define T Text
#define TSeq TextSeq
#define T_initialize_ex ::ice::Text_initialize_ex
#define T_finalize_ex   ::ice::Text_finalize_ex
#define T_copy       ::ice::Text_copy

#ifndef NDDS_STANDALONE_TYPE
#include "dds_c/generic/dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp/generic/dds_cpp_sequence_TSeq.gen"
#endif
#else
#include "dds_c_sequence_TSeq.gen"
#ifdef __cplusplus
#include "dds_cpp_sequence_TSeq.gen"
#endif
#endif

#undef T_copy
#undef T_finalize_ex
#undef T_initialize_ex
#undef TSeq
#undef T


} /* namespace ice */
