from ice import ice_DeviceIdentity
from get_bound_length import get_bound_length

import os
import platform
from pathlib import Path
import importlib.resources as resources
from typing import Final, Optional, Type, Any
import random
import string
import logging


class DeviceIdentityBuilder:
    """
    Helper class for building `ice_DeviceIdentity` instances
    """

    # Static variables
    __log: Final[logging.Logger] = logging.getLogger("DeviceIdentityBuilder")


    def __init__(self, pojo: Optional[ice_DeviceIdentity] = None) -> None:
        """
        Initialises a new `DeviceIdentityBuilder` instance

        :param pojo: An optional `ice_DeviceIdentity` to modify. If none provided, a blank one will be constructed.
        """

        self.__pojo = pojo if pojo else ice_DeviceIdentity()


    def build(self) -> ice_DeviceIdentity:
        """
        Populates the build field of the `ice_DeviceIdentity` being built and returns that instance

        :returns device_identity: The `ice_DeviceIdentity` instance with build field populated
        """

        #TODO: BuildInfo.getDescriptor()
        self.__pojo.build = "Python development build: yet to get BuildInfo class working"
        return self.__pojo
    

    def software_rev(self) -> 'DeviceIdentityBuilder':
        """
        Populates the build field of the 'ice_DeviceIdentity` being built and then returns the current `DeviceIdentityBuilder`

        :returns device_identity_builder: The current `DeviceIdentityBuilder` instance, to allow for chaining
        """

        #TODO: BuildInfo.getDescriptor()
        self.__pojo.build = "Python development build: yet to get BuildInfo class working"
        return self
    

    def model(self, model: str) -> 'DeviceIdentityBuilder':
        """
        Sets the model field of the `ice_DeviceIdentity` being built and then returns the current `DeviceIdentityBuilder`

        :param model: The model of the device
        :returns device_identity_builder: The current `DeviceIdentityBuilder` instance, to allow for chaining
        """

        self.__pojo.model = model
        return self
    

    def os_name(self) -> 'DeviceIdentityBuilder':
        """
        Retrieves the current operating system name and populates the operating_system field of the `ice_DeviceIdentity` being built.

        :returns device_identity_builder: The current `DeviceIdentityBuilder` instance, to allow for chaining
        """

        os_name = platform.system()

        max_operating_system_length: int = 0
        try:
            max_operating_system_length = get_bound_length(ice_DeviceIdentity, "operating-system")
        except Exception as e:
            self.__log.warning("Unable to find length of ice_DeviceIdentity.operating_system", exc_info=e)

        if max_operating_system_length > 0:
            if os_name == "Linux":
                OS_RELEASE_FILE = Path("/etc/os-release")
                if OS_RELEASE_FILE.exists() and OS_RELEASE_FILE.is_file() and os.access(OS_RELEASE_FILE, os.R_OK):
                    try:
                        with OS_RELEASE_FILE.open(encoding="utf-8") as f:
                            for line in f.readlines():
                                try:
                                    if line.startswith("PRETTY_NAME="):
                                        value = line.split("=", 1)[1].strip()
                                        if value.startswith('"') and value.endswith('"'):
                                            value = value[1:-1]  # remove surrounding quotes
                                        os_name = value
                                        break

                                except IndexError as e:
                                    self.__log.debug(str(e), exc_info=e)

                    except OSError as e:
                        self.__log.info(f"Unable to read {OS_RELEASE_FILE} on this Linux system", exc_info=e)

            operating_system = f"{os_name} {os.uname().machine} {os.uname().release}"

            if len(operating_system) > max_operating_system_length:
                operating_system = operating_system[:max_operating_system_length]

            self.__pojo.operating_system = operating_system
        else:
            self.__pojo.operating_system = ""

        return self


    def with_icon(self, icon_path: str) -> "DeviceIdentityBuilder":
        """
        Load an icon from a provided file path and populates the icon of the `ice_DeviceIdentity` being built.

        :param icon_path: The path to the file with the icon in
        :returns device_identity_builder: The current `DeviceIdentityBuilder` instance, to allow for chaining 
        """

        if icon_path:
            path = Path(icon_path)
        else:
            path = Path()

        if not path.exists() or not path.is_file():
            self.__log.warning("Icon file does not exist: %s", icon_path)
            self.__pojo.icon.image.value = b""
            return self

        try:
            self.__pojo.icon.image.value = path.read_bytes()
            self.__pojo.icon.content_type = "image/png"

        except OSError as e:
            self.__log.warning("Failed to read icon file: %s", icon_path, exc_info=e)
            self.__pojo.icon = b""

        return self
    

    @staticmethod
    def random_udi() -> str:
        """
        Generates a random ICE Unique Device Identifier

        :returns udi: The newly generated Unique Device Identifier
        """

        UDI_CHARS = list(string.ascii_uppercase + string.ascii_lowercase + string.digits)
        UDI_LENGTH = 36
        udi = os.getenv("randomUDI")

        if udi:
            return udi
        
        else:
            return ''.join(random.choice(UDI_CHARS) for _ in range(UDI_LENGTH))
