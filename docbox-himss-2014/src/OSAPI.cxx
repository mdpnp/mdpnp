/*********************************************************************************************
(c) 2005-2013 Copyright, Real-Time Innovations, Inc.  All rights reserved.    	                             
RTI grants Licensee a license to use, modify, compile, and create derivative works 
of the Software.  Licensee has the right to distribute object form only for use with RTI 
products.  The Software is provided “as is”, with no warranty of any type, including 
any warranty for fitness for any purpose. RTI is under no obligation to maintain or 
support the Software.  RTI shall not be liable for any incidental or consequential 
damages arising out of the use or inability to use the software.
**********************************************************************************************/
#include "OSAPI.h"

OSThread::OSThread(
	ThreadFunction function, 
	void *functionParam)
{
	_function = function;
	_functionParam = functionParam;
}

void OSThread::Run()
{

#ifdef RTI_WIN32
    _thread = (HANDLE) _beginthread(
		(void(__cdecl*)(void*))_function,
        0, (void*)_functionParam);
#else
    pthread_attr_t threadAttr;
    pthread_attr_init(&threadAttr);
    pthread_attr_setdetachstate(&threadAttr, PTHREAD_CREATE_JOINABLE);
    int error = pthread_create(
                &_thread, 
                &threadAttr, 
                _function,
                (void *)_functionParam);
    pthread_attr_destroy(&threadAttr);
  #endif
}

OSMutex::OSMutex()
{
#ifdef RTI_WIN32
	InitializeCriticalSection(&_handleCriticalSection);
#else
	pthread_mutex_init(&_mutex, NULL);
#endif
}

OSMutex::~OSMutex()
{
#ifdef RTI_WIN32
	DeleteCriticalSection(&_handleCriticalSection);
#else
	pthread_mutex_destroy(&_mutex);
#endif
}

void OSMutex::Lock()
{
#ifdef RTI_WIN32
        EnterCriticalSection(&_handleCriticalSection);
#else
	pthread_mutex_lock(&_mutex);
#endif


}

void OSMutex::Unlock()
{
#ifdef RTI_WIN32
	LeaveCriticalSection(&_handleCriticalSection);
#else
	pthread_mutex_unlock(&_mutex);
#endif
}

