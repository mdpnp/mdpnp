/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    Property.cxx
 * @brief   Handle properties specified in an INI file.
 */
//=============================================================================
#include <sstream>
#include <iostream>
#include <fstream>
#include <memory>
#include <cstring>
#include "Property.h"

#ifdef _WIN32

#include <hash_map>
#include <hash_set>

#else
#include <ext/hash_map>
#include <ext/hash_set>

// Template specialization for hash<T>() to handle std::string objects
namespace __gnu_cxx
{
  template<> struct hash<std::string>
  {
    size_t operator()(const std::string &__x) const
    {
      return hash<const char *>()(__x.c_str());
    }
  };
}

#endif

static const int MAX_LINE(512);

#ifdef _WIN32
#pragma warning(push)
#pragma warning (disable:4996)
#endif

// Platform neutral (PN) string functions
#ifdef _WIN32
#define PN_SNPRINTF        _snprintf
#define PN_STRTOULL        _strtoui64
#else
#define PN_SNPRINTF        snprintf
#define PN_STRTOULL        strtoull
#endif

//=============================================================================
// Private constants
//=============================================================================
static const std::string STRING_TRUE("true");
static const std::string STRING_FALSE("false");
static const int MAX_TEMP_PROPERTY_BUFFER = 64;

const long ValueMap::CFG_DURATION_INFINITY_SEC(0x7fffffff);
const unsigned long ValueMap::CFG_DURATION_INFINITY_NSEC(0x7fffffffU);
const CFG_Duration_t ValueMap::CFG_DURATION_INFINITE =
{
  CFG_DURATION_INFINITY_SEC,
  CFG_DURATION_INFINITY_NSEC
};

//=============================================================================
// ValueMapImpl
//=============================================================================
#ifdef _WIN32
class ValueMapImpl : public
  stdext::hash_map<std::string, std::string, stdext::hash_compare<std::string> >
{ };
#else
class ValueMapImpl : public
  __gnu_cxx::hash_map<std::string, std::string, __gnu_cxx::hash<std::string> >
{ };
#endif

//=============================================================================
// Private static functions
//=============================================================================
// throws not_found if the key is not defined in map
inline static const std::string & _getKey(
  ValueMapImpl *map,
  const std::string &key)
{
  ValueMapImpl::const_iterator iter = map->find(key);
  if (iter == map->end())
  {
    char buf[128] = { 0 };
    PN_SNPRINTF(buf, 128, "Key not found in ValueMap: %s", key.c_str());
    throw not_found(std::string(buf));
  }
  return iter->second;
}

//=============================================================================
inline static char * _trim_line(char *ptr)
{
  // Strip spaces at the end of the line
  char *temp = ptr + strlen(ptr) - 1;
  while (temp > ptr && isspace(*temp))
  {
    *temp = '\0'; --temp;
  }
    
  // Strip spaces at the beginning of the line
  while (*ptr && isspace(*ptr)) ++ptr;
  return ptr;
}

//=============================================================================
// Return false if the line doesn't contain the '=' sign (invalid format)
inline static bool _insertLineInValueMap(ValueMap *map, char *line)
{
  bool  rval = false;   // Assume invalid line
  char *sep = strchr(line, '=');

  if (sep)
  {
    // Valid line
    char *key = line;
    char *val = sep + 1;
    *sep = '\0';

    // Strip spaces before and after key and val
    key = _trim_line(key);
    val = _trim_line(val);
    map->set_string(std::string(key), std::string(val));
    rval = true;
  }
  return rval;
}

//=============================================================================
// ValueMap static functions
//=============================================================================
void ValueMap::parse_as_bool(const std::string &valueIn, bool *valueOut)
{
  if (  (valueIn[0] == '1' && valueIn.size() == 1)
    ||  !strcmp(valueIn.c_str(), STRING_TRUE.c_str()))
  {
    if (valueOut) *valueOut = true;
    return;
  }

  if (  (valueIn[0] == '0' && valueIn.size() == 1)
    ||  !strcmp(valueIn.c_str(), STRING_FALSE.c_str()))
  {
    if (valueOut) *valueOut = false;
    return; 
  }
  std::ostringstream os;
  os << "unable to convert value to bool: " << valueIn;
  std::cout << "ValueMap::parse_as_bool: " << os.str() << std::endl;
  throw invalid_type(os.str());
}

//=============================================================================
void ValueMap::parse_as_int(const std::string &valueIn, int *valueOut)
{
  char *ptr = 0;
  long retVal = strtol(valueIn.c_str(), &ptr, 10);
  if (*ptr)
  {
    std::ostringstream os;
    os << "unable to convert value to int: " << valueIn;
    std::cout << "ValueMap::parse_as_int: " << os.str() << std::endl;
    throw invalid_type(os.str());
  }
  if (valueOut) *valueOut = (int)retVal;
}

//=============================================================================
void ValueMap::parse_as_uint(const std::string &valueIn, unsigned int *valueOut)
{
  char *ptr = 0;
  unsigned long retVal = strtoul(valueIn.c_str(), &ptr, 10);

  if (*ptr)
  { 
    std::ostringstream os;
    os << "unable to convert value to int: " << valueIn;
    std::cout << "ValueMap::parse_as_uint: " << os.str() << std::endl;
    throw invalid_type(os.str());
  }
  if (valueOut) *valueOut = (unsigned int)retVal;
}

//=============================================================================
void ValueMap::parse_as_uint_gt0(
  const std::string &valueIn,
  unsigned int *valueOut)
{
  unsigned int temp = 0;
  parse_as_uint(valueIn, &temp);

  if (!temp)
  {
    std::ostringstream os;
    os << "unable to convert value to int_gt0: " << valueIn;
    std::cout << "ValueMap::parse_as_uint_gt0: " << os.str() << std::endl;
    throw invalid_type(os.str());
  }
  if (valueOut) *valueOut = temp;
}

//=============================================================================
void ValueMap::parse_as_uint_gte0(
  const std::string &valueIn,
  unsigned int *valueOut)
{
  unsigned int temp = 0;
  parse_as_uint(valueIn, &temp);
  if (valueOut) *valueOut = temp;
}

//=============================================================================
void ValueMap::parse_as_double(const std::string &valueIn, double *valueOut)
{
  char *ptr = 0;
  double retVal = (double)strtod(valueIn.c_str(), &ptr);
  if (*ptr)
  {
      std::ostringstream os;
      os << "unable to convert value to double: " << valueIn;
      std::cout << "ValueMap::parse_as_double: " << os.str() << std::endl;
      throw invalid_type(os.str());
  }
  if (valueOut) *valueOut = retVal;
}

//=============================================================================
void ValueMap::parse_as_duration(
  const std::string &valueIn,
  CFG_Duration_t *valueOut)
{
  if (valueIn == "I")
  {
    if (valueOut) *valueOut = CFG_DURATION_INFINITE;
    return;
  }

  char *ptr = 0;
  unsigned long long usec = PN_STRTOULL(valueIn.c_str(), &ptr, 10);
  if (*ptr)
  {
    std::ostringstream os;
    os << "unable to convert value to duration: " << valueIn;
    std::cout << "ValueMap::parse_as_duration: " << os.str() << std::endl;
    throw invalid_type(os.str());
  }
  if (valueOut)
  {
    valueOut->sec = static_cast<long>(usec / 1000000ull);
    valueOut->nanosec = static_cast<unsigned long>((usec % 1000000ull) * 1000ull);
  }
}

//=============================================================================
// ValueMap Methods
//=============================================================================

ValueMap::ValueMap()
  : _theImpl(new ValueMapImpl()),
    _isReadOnly(false)
{ }

//=============================================================================
ValueMap::ValueMap(const ValueMap &ref)
  : _theImpl(new ValueMapImpl(*ref._theImpl)),
   _isReadOnly(ref._isReadOnly)
{ }

//=============================================================================
ValueMap::~ValueMap()
{
  if (_theImpl) delete _theImpl;
  _theImpl = 0;
}

//=============================================================================
ValueMap & ValueMap::operator=(const ValueMap &ref)
{
  if (this != &ref)
  {
    if (_theImpl)
    {
      // Delete the current map
      delete _theImpl;
    }
    _theImpl = new ValueMapImpl(*ref._theImpl);
    _isReadOnly = ref._isReadOnly;
  }
  return *this;
}

//=============================================================================
ValueMap ValueMap::clone() const
{
  ValueMap retVal;
  *(retVal._theImpl) = *_theImpl;
  return retVal;
}

//=============================================================================
void ValueMap::clone_to(ValueMap &map) const
{
  map._isReadOnly = false;
  *(map._theImpl) = *_theImpl;
}

//=============================================================================
void ValueMap::merge_to(ValueMap &map) const
{
  map._isReadOnly = false;
  for (ValueMapImpl::const_iterator iter=_theImpl->begin(); 
    iter != _theImpl->end();
    ++iter)
  {
    (*(map._theImpl))[iter->first] = iter->second;
  }
}

//=============================================================================
bool ValueMap::is_set(const std::string &key) const
{
  return _theImpl->find(key) != _theImpl->end();
}

//=============================================================================
bool ValueMap::unset(const std::string &key)
{
  if (_isReadOnly) throw read_only(key);
    
  ValueMapImpl::iterator iter = _theImpl->find(key);
  if (iter != _theImpl->end())
  {
    _theImpl->erase(iter);
    return true;
  }
  return false;
}

//=============================================================================
void ValueMap::set_bool(const std::string &key, bool value)
{
  if (_isReadOnly) throw read_only(key);
  (*_theImpl)[key] = (value ? STRING_TRUE : STRING_FALSE);
}

//=============================================================================
void ValueMap::set_int(const std::string &key, int value)
{
  if (_isReadOnly) throw read_only(key);
  char buf[MAX_TEMP_PROPERTY_BUFFER] = { 0 };
  PN_SNPRINTF(buf, MAX_TEMP_PROPERTY_BUFFER, "%d", value);
  (*_theImpl)[key] = std::string(buf);
}

//=============================================================================
void ValueMap::set_string(const std::string &key, const std::string &value)
{
  if (_isReadOnly) throw read_only(key);
  (*_theImpl)[key] = value;
}
    
//=============================================================================
void ValueMap::set_double(const std::string &key, double value)
{
  if (_isReadOnly) throw read_only(key);
  char buf[MAX_TEMP_PROPERTY_BUFFER] = { 0 };
  PN_SNPRINTF(buf, MAX_TEMP_PROPERTY_BUFFER, "%g", value);
  (*_theImpl)[key] = buf;
}

//=============================================================================
void ValueMap::set_duration(const std::string &key, const CFG_Duration_t &value)
{
  if (_isReadOnly) throw read_only(key);
  char buf[MAX_TEMP_PROPERTY_BUFFER] = { 0 };
  if (  value.sec== CFG_DURATION_INFINITY_SEC
    &&  value.nanosec == CFG_DURATION_INFINITY_NSEC)
  {
    // Represent infinite duration with "I" for now.
    PN_SNPRINTF(buf, MAX_TEMP_PROPERTY_BUFFER, "I");
  }
  else
  {
    unsigned long long usec = value.sec * 1000000ull + value.nanosec / 1000ull;
    // Store as string in microsecond resolution.
    PN_SNPRINTF(buf, MAX_TEMP_PROPERTY_BUFFER, "%llu", usec);
  }
  (*_theImpl)[key] = buf;
}

//=============================================================================
bool ValueMap::get_bool(const std::string &key) const
{
  bool tempVal  = false;
  parse_as_bool(_getKey(_theImpl, key), &tempVal);
  return tempVal;
}

//=============================================================================
unsigned int ValueMap::get_uint(const std::string &key) const
{
  unsigned int tempVal = 0;
  parse_as_uint(_getKey(_theImpl, key), &tempVal);
  return tempVal;
}

//=============================================================================
int ValueMap::get_int(const std::string &key) const
{
  int tempVal = 0;
  parse_as_int(_getKey(_theImpl, key), &tempVal);
  return tempVal;
}

//=============================================================================
const std::string &ValueMap::get_string(const std::string &key) const
{
  return _getKey(_theImpl, key);
}

//=============================================================================
double ValueMap::get_double(const std::string &key) const
{
  double tempVal = 0;
  parse_as_double(_getKey(_theImpl, key), &tempVal);
  return tempVal;
}

//=============================================================================
void ValueMap::get_duration(
  const std::string &key,
  CFG_Duration_t &durationOut) const
{
  parse_as_duration(_getKey(_theImpl, key), &durationOut);
}

//=============================================================================
bool ValueMap::validate_set(const Validator &validator) const
{
  for (
    ValueMapImpl::const_iterator iter =
    _theImpl->begin(); iter != _theImpl->end();
    ++iter)
  {
    if (!validator.validate(iter->first, iter->second)) return false;
  }
  return true;
}

//=============================================================================
unsigned int ValueMap::size() const
{
  return (unsigned int)_theImpl->size();
}

//=============================================================================
void ValueMap::clear()
{
  _theImpl->clear();
}

//=============================================================================
void ValueMap::serialize_to(std::ostream &os) const
{
  for (
    ValueMapImpl::const_iterator iter = _theImpl->begin(); 
    iter != _theImpl->end(); 
    ++iter)
  {
    os << iter->first << '=' << iter->second << std::endl;
  }
}


void ValueMap::deserialize_from(std::istream &is)
{
  char line[MAX_LINE] = { 0 };
  while(!is.eof())
  {
    is.getline(line, MAX_LINE);
    _insertLineInValueMap(this, line);
  }
}


//=============================================================================
// CfgProfile
//=============================================================================
const CfgProfile::CfgProfileName CfgProfile::ANONYMOUS_NAME("__anonname__");
const CfgProfile CfgProfile::EMPTY_PROFILE(ANONYMOUS_NAME);


//=============================================================================
// CfgDictionaryImpl
//=============================================================================
#ifdef _WIN32
typedef stdext::hash_map<std::string,
    CfgProfile, stdext::hash_compare<std::string> > _CfgProfileMap;
#else
typedef __gnu_cxx::hash_map<std::string,
    CfgProfile, __gnu_cxx::hash<std::string> >  _CfgProfileMap;
#endif

class CfgDictionaryImpl: public _CfgProfileMap
{
public:

CfgDictionaryImpl()
  : _CfgProfileMap()
{}
    
void read_from_stream(std::istream &is);

};


//=============================================================================
// CfgDictionary Methods
//=============================================================================

CfgDictionary::CfgDictionary()
  : _theImpl(new CfgDictionaryImpl())
{ }

//=============================================================================
CfgDictionary::CfgDictionary(const CfgProfile &profile)
  : _theImpl(new CfgDictionaryImpl())
{
  add(profile);
}

//=============================================================================
CfgDictionary::CfgDictionary(const std::string &source)
  : _theImpl(0)
{
  // Use auto_ptr so exceptions won't cause memory leaks
  std::auto_ptr<CfgDictionaryImpl> impl(new CfgDictionaryImpl());
  const char * fName = source.c_str();    
  std::ifstream fs;
  fs.open(fName);
  if (!fs.is_open())  throw not_found(fName);
  impl->read_from_stream(fs);
  fs.close();
  _theImpl = impl.release();
}

//=============================================================================
CfgDictionary::CfgDictionary(std::istream &is)
  : _theImpl(0)
{
  // Use auto_ptr so exceptions won't cause memory leaks
  std::auto_ptr<CfgDictionaryImpl> impl(new CfgDictionaryImpl());
  impl->read_from_stream(is);
  _theImpl = impl.release();
}

//=============================================================================
CfgDictionary::~CfgDictionary()
{
  if (_theImpl) delete _theImpl;
  _theImpl = 0;
}

//=============================================================================
void CfgDictionary::add(const CfgProfile &profile)
{
  (*_theImpl)[profile.get_name()] = profile;
}

//=============================================================================
CfgProfile *CfgDictionary::get(const CfgProfile::CfgProfileName &name) const
{
  CfgDictionaryImpl::iterator iter = _theImpl->find(name);
  if (iter == _theImpl->end())
  {
    std::cout << "CfgDictionary::get: profile not found: " << name << std::endl;
    throw not_found(name);
  }
  return &iter->second;
}
    
//=============================================================================
bool CfgDictionary::remove(const CfgProfile::CfgProfileName &name)
{
  CfgDictionaryImpl::iterator iter = _theImpl->find(name);
  if (iter == _theImpl->end()) return false;
  _theImpl->erase(iter);
  return true;
}

//=============================================================================
CfgDictionary CfgDictionary::clone() const
{
  CfgDictionary retVal;

  for (
    CfgDictionaryImpl::iterator iter = _theImpl->begin();
    iter != _theImpl->end();
    ++iter)
  {
    retVal.add(iter->second.clone());
  }
  return retVal;
}

//=============================================================================
unsigned int CfgDictionary::size() const
{
  return (unsigned int)_theImpl->size();
}

//=============================================================================
// CfgDictionaryImpl Methods
//=============================================================================

void CfgDictionaryImpl::read_from_stream(std::istream &is)
{
  // Read line by line a file in the following format:
  //      [name]
  //      key = value
  //      key = value
  //      [name]
  //      ...
  // Comments starts with character '#' or ';' until the end of the line
  CfgProfile profile; // The first one is all anonymous name
  const char *const METHOD_NAME("CfgDictionaryImpl::read_from_stream");
  char line[MAX_LINE] = { 0 };
  char *ptr = 0;

  for (int lineCount = 1; !is.eof(); ++lineCount)
  {
    is.getline(line, MAX_LINE);

    // Remove comments
    ptr = line;
    while (*ptr)
    {
      if (*ptr == '#' || *ptr == ';')
      {
        *ptr ='\0';
        break;
      }
      ++ptr;
    }

    // Strip spaces before and after
    ptr = _trim_line(line);

    // Remove empty lines
    if (ptr[0] == '\0') continue;
        
      // Is a profile definition?
    if (ptr[0] == '[' && ptr[strlen(ptr)-1] == ']')
    {
      if (strlen(ptr) <= 2)
      {
        std::ostringstream os;
        os << "invalid profile name on line " << lineCount;
        std::cout << METHOD_NAME << ": " << os.str() << std::endl;
        throw format_error(os.str());
      }
      // Removes the '[' and ']' from the line
      ++ptr;
      ptr[strlen(ptr)-1] = '\0';
      ptr = _trim_line(ptr);

      // Insert current profile in map
      if (profile.get_name() != CfgProfile::ANONYMOUS_NAME)
      {
        _CfgProfileMap::operator[](profile.get_name()) = profile;
      }

      // Create the new profile
      profile = CfgProfile(CfgProfile::CfgProfileName(ptr));
      continue;
    }

    if (_insertLineInValueMap(&profile, ptr) == false)
    {
      std::ostringstream os;
      os << "invalid declaration, missing '=' separator on line " << lineCount;
      std::cout << METHOD_NAME << ": " << os.str() << std::endl;
      throw format_error(os.str());
    }
  }

  // Finally store the last profile
  if (profile.get_name() != CfgProfile::ANONYMOUS_NAME)
  {
    // Don't add if is the default one
    _CfgProfileMap::operator[](profile.get_name()) = profile;
  }
}

#ifdef _WIN32
#pragma warning(pop)
#endif
