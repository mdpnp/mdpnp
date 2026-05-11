import rti.types as idl
import rti.connextdds as dds
from typing import Any

def get_bound_length(cls: Any, field_name: str) -> int:
    """
    Retrieves the maximum bound length for a bounded string field
    defined via RTI IDL for the given class and field name.

    :param cls: The class that you want to search the IDL bound of
    :param field_name: The field_name that you want to find the bound of
    :returns length: The idl bound length for the specified field in the provided class
    """

    support = idl.get_type_support(cls)
    dynamic_type = support.dynamic_type

    for member in dynamic_type.members():
        if member.name == field_name and isinstance(member.type, dds.StringType):
            return member.type.bounds

    return 0
