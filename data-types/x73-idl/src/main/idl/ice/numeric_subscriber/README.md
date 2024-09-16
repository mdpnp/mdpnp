# Example Python integration with OpenICE data


This directory contains a sample integration between OpenICE and Python.  It uses the RTI Python library, and a pre-generated .py file that represents the Numeric data type in OpenICE.

Prerequisites
=
You must install the RTI Python library.  This is most easily done by using the pip command as RTI have published the library

```
pip install rti.connext
```
 
you also need to configure the environment variable RTI_LICENSE_FILE to point to the supplied license file.  This is platform specific.
It's important to use a full path.

__Windows__

Assuming that the git repository is cloned into C:\Users\simonkelly1973\mdpnp

```
set RTI_LICENSE_FILE=C:\Users\simonkelly1973\mdpnp\interop-lab\demo-apps\src\main\resources\OpenICE_license.dat
```

__Linux/Mac__

Assuming that the git repository is cloned into /home/simonkelly1973/mdpnp

```
export RTI_LICENSE_FILE=/home/simonkelly1973/mdpnp/interop-lab/demo-apps/src/main/resources/OpenICE_license.dat

```

Running the sample program
=
The Python code uses a default domain of 0. If you are using a different domain when running OpenICE, you must match the domain in the Python application with the domain you
are running with OpenICE - otherwise you will not see any data in the Python code.

To run without specifying a domain, and defaulting to domain 0

```
python read_numerics.py
```

To specify a different domain (for example, domain id 10)

```
python read_numerics.py -d 10
```

By default, the code receives all numerics.  It does not filter according to device identifier, metric id or anything else.  Records are dumped to standard output.

Note that the program is only designed to represent the bare minimum code that is required to prove that the Python application is receiving data from the OpenICE
system.  More complex uses can be derived from this base.
