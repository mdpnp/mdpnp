What's Available
========

The MD PnP lab is working on a wide variety of projects.  All will incubate in this main repository and some will eventually spin out into separate projects.  Here is what you will find in our code repository.

data-types/
-------
* The [Device Model Working Group](DeviceModel) is at work in this area.
* Work (in progress) on data type definitions
    * <code>x73-idl</code>  Prototype [Interface Description (Definition) Language](http://en.wikipedia.org/wiki/Interface_description_language) types
    * <code>x73-idl-rti-dds</code>  Code generation from IDL with rtiddsgen. Requires RTI DDS; register [here](http://www.rti.com/downloads/rti-dds.html)

devices/
------
* Implementation of device protocols for lab devices
* To be used in conjunction with other components that provide nomenclature and information model translation
* Current Devices
    * CardioPulmonary Corp Bernoulli
    * Draeger Medibus
    * Masimo Radical-7
    * Nellcor N-595
    * Nonin 9560 (OnyxII w/Bluetooth) / Nonin 3150 (WristOx<sub>2</sub>)
    * Oridion Capnostream20
    * Philips MP70

himss-2013/
--------
* Example of submitting a document to a [CONNECT 4.0](http://www.connectopensource.org) server
* Example of creating a custom [CONNECT 4.0](http://www.connectopensource.org) adapter to receive such documents

interop-lab/
-------- 
* Software demonstrations shared with visitors to the MD PnP Interoperability Lab.
    * __android-apps/__
        * Simple android demos ... currently connects to Nonin bluetooth pulse oximeters
    * __demo-apps/__
        * Demo Applications (currently demonstrates an ICE Supervisor and many ICE Device Interfaces)
    * __demo-datamodel/__
        * Speculative data model used in demonstration applications
    * __demo-devices/__
        * Binding of device protocol implementations (see above) to demo-nomenclature, demo-datamodel, and demo-messaging
    * __demo-guis/__
        * GUI components (mostly waveform related) that can be used with any framework (swing, jogl, android, etc)
    * __demo-guis-jogl/__
        * Components to bind demo-guis to Java OpenGL (JOGL)
    * __demo-guis-swing/__
        * Components to bind demo-guis to Swing
    * __demo-jgroups/__
        * A Binding of demo-messaging to JGroups reliable multicast
    * __demo-messaging/__
        * A generic messaging infrastructure for demo apps and devices
    * __demo-nomenclature/__
        * Speculative nomenclature used in demonstration applications
    * __demo-purejavacomm/__
        * An implementation of SerialProvider that uses Purejavacomm for serial port access
    * __demo-rtidds/__
        * A Binding of demo-messaging to RTI DDS middleware

![](http://arvi.mgh.harvard.edu:8080/ht/?SOURCEFORGEMAINCODE)