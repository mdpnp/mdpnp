import sys
import importlib.resources
from typing import Optional
from email.parser import HeaderParser


class BuildInfo:
    """
    Holds build metadata extracted from the META-INF/MANIFEST.MF file (if available) and extracts:
    - Implementation-Version
    - Build-Date
    - Build-Time
    - Build-Number
    """

    # Static initialisation
    try:
        _version = None
        _date = None
        _time = None
        _build = None
        _descriptor = None

        try:
            with importlib.resources.open_text('META_INF', 'MANIFEST.MF') as mf:
                manifest_text = mf.read()
                parser = HeaderParser()
                main_attrs = parser.parsestr(manifest_text)

                if main_attrs.get('Implementation-Title') == 'demo-apps':
                    _version = main_attrs.get('Implementation-Version')
                    _date = main_attrs.get('Build-Date')
                    _time = main_attrs.get('Build-Time')
                    _build = main_attrs.get('Build-Number')
        except FileNotFoundError:
            pass

        if _version is None:
            _descriptor = f"Development Version on {sys.version.split()[0]}"
        else:
            _descriptor = f"v{_version} built: {_date} {_time} on {sys.version.split()[0]}"

        __version = _version
        __build = _build
        __date = _date
        __time = _time
        __descriptor = _descriptor

    except Exception as e:
        print(F"Exception occurred in static BuildInfo initialisation: {e}")


    # Getter methods
    @classmethod
    def get_version(cls) -> Optional[str]:
        """
        Gets the current build version

        :returns version: The current build version
        """

        return cls.__version


    @classmethod
    def get_date(cls) -> Optional[str]:
        """
        Gets the date the build was completed on

        :returns data: The build date
        """

        return cls.__date


    @classmethod
    def get_build(cls) -> Optional[str]:
        """
        Gets the current build number

        :returns build: The current build number as a string
        """

        return cls.__build


    @classmethod
    def get_time(cls) -> Optional[str]:
        """
        Gets the current build time

        :returns time: The build time
        """

        return cls.__time


    @classmethod
    def get_descriptor(cls) -> Optional[str]:
        """
        Gets the build descriptor string. This enumerates some of the other build information into single string

        :returns descriptor: The build descriptor string
        """

        return cls.__descriptor

