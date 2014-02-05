/*********************************************************************************************
(c) 2005-2013 Copyright, Real-Time Innovations, Inc.  All rights reserved.    	                             
RTI grants Licensee a license to use, modify, compile, and create derivative works 
of the Software.  Licensee has the right to distribute object form only for use with RTI 
products.  The Software is provided “as is”, with no warranty of any type, including 
any warranty for fitness for any purpose. RTI is under no obligation to maintain or 
support the Software.  RTI shall not be liable for any incidental or consequential 
damages arising out of the use or inability to use the software.
**********************************************************************************************/

#ifndef DDS_TYPE_WRAPPER_H
#define DDS_TYPE_WRAPPER_H

// ------------------------------------------------------------------------- //
//
// DdsAutoType
// This is a wrapper to RTI Connext DDS types, which includes a default 
// constructor, a copy constructor, the = operator, and a destructor.  This
// allows us to use RTI Connext DDS types in common C++ patterns, and ensures
// and ensures that you do a deep copy of the contents of the data types.
//
// ------------------------------------------------------------------------- //

template<typename T>
class DdsAutoType : public T 
{
public:

	// --- Constructor type for your generated data type ---
    DdsAutoType<T>() 
	{
        if (T::TypeSupport::initialize_data(this) != DDS_RETCODE_OK) 
		{
			throw std::bad_alloc();
		}
	}

	// --- Copy constructor --- 

	// This copy constructor calls the TypeSupport::initialize_data for your
	// generated data type (generated from IDL)
	DdsAutoType<T>(const DdsAutoType<T> &rhs) 
	{
		if (T::TypeSupport::initialize_data(this) != DDS_RETCODE_OK) 
		{
			throw std::bad_alloc();
        }
        if (T::TypeSupport::copy_data(this, &rhs) != DDS_RETCODE_OK) 
		{
			throw std::bad_alloc();
		}
	}

    // --- Constructor to initialize from base type

	// This constructor takes in a structure of the base type, and does a deep
	// copy of the data into the DdsAutoType
    DdsAutoType<T>(const T &rhs) {

        if (T::TypeSupport::initialize_data(this) != DDS_RETCODE_OK) 
		{
            throw std::bad_alloc();
        }
		if (T::TypeSupport::copy_data(this, &rhs) != DDS_RETCODE_OK) 
		{
            throw std::bad_alloc();
        }
    }

	// --- Assignment operator --- 

	// = operator that allows assignment between two generated types.  
	// This calls FooTypeSupport::copy_data to do a deep copy of the 
	// data type, including pointers.
	DdsAutoType<T> operator=(const DdsAutoType<T> &rhs) 
	{
		if (T::TypeSupport::copy_data(this, &rhs) != DDS_RETCODE_OK) 
		{
			throw std::bad_alloc();
		}

		return *this;
    }

	// --- Destructor --- 

	// Destroy the data type, including any allocated pointers, etc.
	~DdsAutoType<T>() 
	{
		// in the RTI current implementation the finalize call  never fails
		T::TypeSupport::finalize_data(this);
	}
};

#endif
