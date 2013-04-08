Demo Communications
=================

Speculative implementation (used in lab demonstrations) of some communication features such as

* __Nomenclature__  
  <code>org.mdpnp.comms.Identifier</code> specifies a generic naming type  
  Package <code>org.mdpnp.comms.nomenclature</code> contains interfaces that declare specific names as fields.
  
* __Data Model__  
  <code>org.mdpnp.comms.IdentifiableUpdate</code> specifies a generic data update mechanism  
  Packages under <code>org.mdpnp.comms.data</code> define specific data types that can pass through the system.
  
* __Gateway__  
  <code>org.mdpnp.comms.Gateway</code>  
  A generic mechanism for passing messages; intended for bridging to a transport (or several)
  
* __Transport(s)__  
  Package <code>org.mdpnp.transport</code> contains implementations for bridging to several types transport  
  Here 'transport' is used more generically and is not referring to a specific OSI layer
  
* __Default Implementations__    
  <code>AbstractDevice</code>, <code>AbstractConnectedDevice</code>, etc.  
  are meant to be useful supertypes for specific device implementations
  
* __Serial Comm Utilities__  
  <code>org.mdpnp.comms.serial</code> provides serial utilities
  