# MD PnP OpenICE

[![Join the chat at https://gitter.im/mdpnp/mdpnp](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mdpnp/mdpnp?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://jenkins.openice.info/job/mdpnp-demoapp/badge/icon)](https://jenkins.openice.info/job/mdpnp-demoapp)
[![Latest Release](https://img.shields.io/github/release/mdpnp/mdpnp.svg)](https://github.com/mdpnp/mdpnp/releases/latest)
Getting Started
---------------

You can build and run the main OpenICE application with one gradle task.

    ./gradlew :interop-lab:demo-apps:run


Code Repository
---------------

__interop-lab/__
* OpenICE device adapters and user interfaces
    * __demo-apps/__
        * Demo Applications
    * __demo-devices/__
        * Demo Device Interfaces
    * __demo-guis/__
        * GUI components
    * __demo-guis-javafx/__
        * Components to bind demo-guis to JavaFX

__data-types/__
* Work (in progress) on data type definitions
    * <code>x73-idl</code>  Prototype [Interface Description (Definition) Language](http://en.wikipedia.org/wiki/Interface_description_language) types
    * <code>x73-idl-rti-dds</code>  Code generation from IDL with rtiddsgen for use with RTI DDS; register [here](http://www.rti.com/downloads/rti-dds.html)
    * <code>x73-idl-ospl-dds</code>  Code generation from IDL with idlpp for use with PrismTech OpenSplice DDS; community edition [here](http://www.prismtech.com/dds-community)

__devices/__
* Implementation of device protocols for lab devices.
* To be used in conjunction with other components that provide nomenclature, information model translation, and messaging

<a href="https://iconscout.com/icons/infusion-pumps" target="_blank">Infusion pumps Icon</a> by <a href="https://iconscout.com/contributors/icons8">Icons8</a> on <a href="https://iconscout.com">Iconscout</a>

Licensed Components
-------------------

The code in this branch of the repository relies on an implementation of the DDS standard from [RTI](https://www.rti.com).  RTI have kindly provided licensed materials to support this.  Your use of this version of the code relies on you accepting the terms of this license.  You can read the license [in this file](interop-lab/demo-apps/src/main/resources/RTI_LICENSE.TXT)

Encryption Technologies
-----------------------

One of the major features of this release is the support of encryption for data transport via the DDS implementation.  Encryption is enabled by default in this release in the QoS (Quality of Service) file [here](data-types/x73-idl-rti-dds/src/main/resources/META-INF/ice_library.xml).  This means that OpenICE will require encryption support libraries.  Due to export restrictions on encryption technnologies, the MDPNP program have chosen not to bundle these required files as part of this release.  The required library is libcrypto.so.1.1 on Linux and libcrypto-1_1-x64.dll on Windows.  You must obtain these libraries separately from this code (although many modern systems will have them installed).

Preset Values
-------------
Because encryption is configured out of the box for this version, certain files in the build tree are cryptographically signed.  This means that with encryption enabled, certain settings are effectively fixed.  You must use

10

as the value for the Domain ID when starting the supervisor application.
