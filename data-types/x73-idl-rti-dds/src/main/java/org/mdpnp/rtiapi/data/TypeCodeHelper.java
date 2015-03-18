package org.mdpnp.rtiapi.data;

import com.rti.dds.infrastructure.BadKind;
import com.rti.dds.infrastructure.Bounds;
import com.rti.dds.typecode.TCKind;
import com.rti.dds.typecode.TypeCode;

public class TypeCodeHelper {
    private TypeCodeHelper() {
        
    }
    
    public static int fieldLength(final TypeCode typeCode, final String fieldName) throws BadKind, Bounds {
        TCKind kind = typeCode.kind();
        
        // These are the only kinds for which member_type(...) is legal
        if(TCKind.TK_STRUCT.equals(kind) ||
           TCKind.TK_UNION.equals(kind) ||
           TCKind.TK_VALUE.equals(kind) ||
           TCKind.TK_SPARSE.equals(kind)) {
            int memberId = typeCode.find_member_by_name(fieldName);
            TypeCode memberType = typeCode.member_type(memberId);

            // Follow aliases (IDL typedefs)
            while(TCKind.TK_ALIAS.equals(memberType.kind())) {
                memberType = memberType.content_type();
            }
            return memberType.length();
        } else {
            throw new IllegalArgumentException(typeCode.name() + " of kind " + kind + " does not have any fields");
        }
        
    }
}
