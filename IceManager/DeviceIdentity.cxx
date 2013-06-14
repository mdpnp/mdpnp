
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from DeviceIdentity.idl using "rtiddsgen".
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



#include "DeviceIdentity.h"

/* ========================================================================= */
const char *DeviceIdentityTYPENAME = "DeviceIdentity";

DDS_TypeCode* DeviceIdentity_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode DeviceIdentity_g_tc_universal_device_identifier_array = DDS_INITIALIZE_ARRAY_TYPECODE(1,64,NULL,NULL);
    static DDS_TypeCode DeviceIdentity_g_tc_manufacturer_string = DDS_INITIALIZE_STRING_TYPECODE(128);
    static DDS_TypeCode DeviceIdentity_g_tc_model_string = DDS_INITIALIZE_STRING_TYPECODE(128);

    static DDS_TypeCode_Member DeviceIdentity_g_tc_members[3]=
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
        }
    };

    static DDS_TypeCode DeviceIdentity_g_tc =
    {{
        DDS_TK_STRUCT,/* Kind */
        DDS_BOOLEAN_FALSE, /* Ignored */
        -1,/* Ignored */
        (char *)"DeviceIdentity", /* Name */
        NULL, /* Ignored */
        0, /* Ignored */
        0, /* Ignored */
        NULL, /* Ignored */
        3, /* Number of members */
        DeviceIdentity_g_tc_members, /* Members */
        DDS_VM_NONE /* Ignored */
    }}; /* Type code for DeviceIdentity*/

    if (is_initialized) {
        return &DeviceIdentity_g_tc;
    }

    DeviceIdentity_g_tc_universal_device_identifier_array._data._typeCode = (RTICdrTypeCode *)&DDS_g_tc_octet;

    DeviceIdentity_g_tc_members[0]._representation._typeCode = (RTICdrTypeCode *)&DeviceIdentity_g_tc_universal_device_identifier_array;
    DeviceIdentity_g_tc_members[1]._representation._typeCode = (RTICdrTypeCode *)&DeviceIdentity_g_tc_manufacturer_string;
    DeviceIdentity_g_tc_members[2]._representation._typeCode = (RTICdrTypeCode *)&DeviceIdentity_g_tc_model_string;

    is_initialized = RTI_TRUE;

    return &DeviceIdentity_g_tc;
}


RTIBool DeviceIdentity_initialize(
    DeviceIdentity* sample) {
  return DeviceIdentity_initialize_ex(sample,RTI_TRUE,RTI_TRUE);
}
        
RTIBool DeviceIdentity_initialize_ex(
    DeviceIdentity* sample,RTIBool allocatePointers,RTIBool allocateMemory)
{
        
    
    if (allocatePointers) {} /* To avoid warnings */
    if (allocateMemory) {} /* To avoid warnings */
    
    if (!RTICdrType_initArray(
        sample->universal_device_identifier, (64), RTI_CDR_OCTET_SIZE)) {
        return RTI_FALSE;
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
            


    return RTI_TRUE;
}

void DeviceIdentity_finalize(
    DeviceIdentity* sample)
{
    DeviceIdentity_finalize_ex(sample,RTI_TRUE);
}
        
void DeviceIdentity_finalize_ex(
    DeviceIdentity* sample,RTIBool deletePointers)
{        
    if (sample) { } /* To avoid warnings */
    if (deletePointers) {} /* To avoid warnings */



    DDS_String_free(sample->manufacturer);                
            

    DDS_String_free(sample->model);                
            

}

RTIBool DeviceIdentity_copy(
    DeviceIdentity* dst,
    const DeviceIdentity* src)
{        

    if (!RTICdrType_copyArray(
        dst->universal_device_identifier, src->universal_device_identifier, (64), RTI_CDR_OCTET_SIZE)) {
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
#define T_initialize_ex DeviceIdentity_initialize_ex
#define T_finalize_ex   DeviceIdentity_finalize_ex
#define T_copy       DeviceIdentity_copy

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

