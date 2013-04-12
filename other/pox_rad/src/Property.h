/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    Property.h
 * @brief  Handle properties specified in an INI file.
 * @see     http://en.wikipedia.org/wiki/INI_file
 */
//=============================================================================
#ifndef PROPERTY_H_
#define PROPERTY_H_

#include <exception>
#include <string>
#include <iostream>
#include <fstream>
#include <stdlib.h>
#ifndef _WIN32
#include <stdexcept>
#endif

typedef struct
{
  long  sec;
  unsigned long nanosec;

} CFG_Duration_t;


#ifndef CFG_DOUBLE64
typedef double CFG_DOUBLE64;
#endif


/**
 * @ingroup Property
 * @brief Throw not_found when search results in key not found.
 */
struct not_found: public std::logic_error
{
  explicit not_found(const std::string &msg): std::logic_error(std::string("not_found: ").append(msg)) {}
};

/**
 * @ingroup Property
 * @brief Throw invalid_type during a generic data read operation
 *        between incompatible types.
 */
struct invalid_type: public std::logic_error
{
  explicit invalid_type(const std::string &msg): std::logic_error(std::string("invalid_type: ").append(msg)) {}
};

/**
 * @ingroup Property
 * @brief Throw read_only when changing a read-only object.
 */
struct read_only: public std::logic_error
{
  explicit read_only(const std::string &desc) throw(): logic_error(std::string("read_only: ").append(desc)) {}
};

/**
 * @ingroup Property
 * @brief Throw todo for section not yet completed.
 */
struct todo: public std::logic_error
{
  explicit todo(const std::string &desc) throw(): logic_error(std::string("todo: ").append(desc)) {}
};

/**
 * @ingroup Property
 * @brief Throw format_error when illegal characters or data detected
 * while performing formatting or parsing.
 */
struct format_error: public std::runtime_error
{
  explicit format_error(const std::string &desc) throw(): runtime_error(std::string("format_error ").append(desc)) {}
};

/* forward declarations */
class ValueMapImpl;
class CfgDictionaryImpl;

/**
  \ingroup Property

  @brief A simple set of utility classes to load .ini files.
*/

//=============================================================================
/**
 * @ingroup Property
 * @brief Use a simple key-value map for properties.
 *
 * Operator = and copy constructor create an exact copy of the map, including
 * the 'read_only' flag. To create a new writable map use the 'clone'
 * and 'clone_to' methods.
 */
class ValueMap
{
private:
ValueMapImpl *  _theImpl;

protected:
bool _isReadOnly;

public:

/**
 * @brief Convert a string to an int.
 * The output value is optional and is not set if NULL.
 * @throws invalid_type if the string cannot convert to the given type.
 */
static void parse_as_int(const std::string &valueIn, int *valueOut = NULL);

/**
 * @brief Convert a string to an unsigned int.
 * The output value is optional and is not set if NULL.
 * @throws invalid_type if the string cannot convert to the given type.
 */
static void parse_as_uint(const std::string &valueIn, unsigned int *valueOut = NULL);

/**
 * @brief Convert a string to an unsigned int greater than 0.
 * The output value is optional and is not set if NULL.
 * @throws invalid_type if the string cannot convert to the given type.
 */
static void parse_as_uint_gt0(const std::string &valueIn, unsigned int *valueOut = NULL);

/**
 * @brief Convert a string to an unsigned int greater than or equal to 0.
 * The output value is optional and is not set if NULL.
 * @throws invalid_type if the string cannot convert to the given type.
 */
static void parse_as_uint_gte0(const std::string &valueIn, unsigned int *valueOut = NULL);

/**
 * @brief Convert a string to a bool.
 * The output value is optional and is not set if NULL.
 * @throws invalid_type if the string cannot convert to the given type.
 */
static void parse_as_bool(const std::string &valueIn, bool *valueOut = NULL);

/**
 * @brief Convert a string to a double.
 * The output value is optional and is not set if NULL.
 * @throws invalid_type if the string cannot convert to the given type.
 */
static void parse_as_double(const std::string &valueIn, double *valueOut = NULL);

/**
 * @brief Convert a string to a CFG_Duration_t.
 * The output value is optional and is not set if NULL.
 * @throws invalid_type if the string cannot convert to the given type.
 */
static void parse_as_duration(const std::string &valueIn, CFG_Duration_t *valueOut = NULL);

/**
 * @brief Convert a string to a string.
 * The output value is optional and is not set if NULL.
 * @throws invalid_type if the string cannot convert to the given type.
  */
static void parse_as_string(const std::string &valueIn, std::string *valueOut = NULL)
{
  if (valueOut) *valueOut = valueIn;
}

public:
/**
 * @brief Constructor.
 */
ValueMap();

/**
 * @brief Copy constructor.
 */
ValueMap(const ValueMap &ref);

/**
 * @brief Destructor.
 */
virtual ~ValueMap();

ValueMap & operator=(const ValueMap &ref);

/**
 * @brief Create a duplicate.
 */
ValueMap clone() const;

/**
 * @brief Duplicate self to reference.
 */
void clone_to(ValueMap &map) const;

/**
 * @brief Merge its contents to reference.
 */
void merge_to(ValueMap &map) const;

/**
 * @brief If is set to be read-only return true. Otherwise return false.
 */
bool is_read_only() const
{
  return _isReadOnly;
}

/**
 * @brief Set this map to read only.
 * Note: When a map becomes read-only it cannot revert to writable again.
 */
void set_read_only()
{
  _isReadOnly = true;
}

/**
 * @brief If the key exists in the map return true. Otherwise return false.
 */
bool is_set(const std::string &key) const;

/**
 * @brief Undefine a particular key.
 * @throws read_only - if map is in read-only state.
 */
bool unset(const std::string &key);

/**
 * @brief Set a bool value for the key and destroy previous value.
 * @throws read_only - if map is in read-only state.
 */
void set_bool(const std::string &key, bool value);

/**
 * @brief Set an int value for the key and destroy previous value.
 * @throws read_only - if map is in read-only state.
 */
void set_int(const std::string &key, int value);

/**
 * @brief Set a string value for the key and destroy previous value.
 * @throws read_only - if map is in read-only state.
 */
void set_string(const std::string &key, const std::string &value) ;

/**
 * @brief Set a double value for the key and destroy previous value.
 * @throws read_only - if map is in read-only state
 */
void set_double(const std::string &key, double value);

/**
 * @brief Set a CFG_Duration_t value for the key and destroy previous value.
 * @throws read_only - if map is in read-only state.
 */
void set_duration(const std::string &key, const CFG_Duration_t &value);

/**
 * @brief Return value for the key as a bool.
 * @throws not_found - if key is not defined.
 * @throws invalid_type - if corresponding value is not a bool.
*/
bool get_bool(const std::string &key) const;

/**
 * @brief Return value for the key as an unsigned int.
 * @throws not_found - if key is not defined.
 * @throws invalid_type - if corresponding value is not an unsigned int.
*/
unsigned int get_uint(const  std::string &key) const;

/**
 * @brief Return value for the key as an int.
 * @throws not_found - if key is not defined.
 * @throws invalid_type - if corresponding value is not an int.
*/
int get_int(const  std::string &key) const;

/**
 * @brief Return value for the key as a string.
 * @throws not_found - if key is not defined.
 * @throws invalid_type - if corresponding value is not a string.
*/
const std::string &get_string(const std::string &key) const;

/**
 * @brief Return value for the key as a double.
 * @throws not_found - if key is not defined.
 * @throws invalid_type - if corresponding value is not a double.
*/
double get_double(const std::string &key) const;

/**
 * @brief Return value for the key as a CFG_Duration_t.
 * @throws not_found - if key is not defined.
 * @throws invalid_type - if corresponding value is not a CFG_Duration_t.
*/
void get_duration(const std::string &key, CFG_Duration_t &durationOut) const;

/**
 * @brief Return value for the key as a bool.
 * If not found, return the default_value passed in.
 * @throws invalid_type - if corresponding value is not a bool.
 */
bool get_bool(const std::string &key, bool default_value) const
{
  return (is_set(key) ? get_bool(key) : default_value);
}

/**
 * @brief Return value for the key as an unsigned int.
 * If not found, return the default_value passed in.
 * @throws invalid_type - if corresponding value is not an unsigned int.
 */
unsigned int get_uint(const std::string &key, unsigned int default_value) const
{
  return (is_set(key) ? get_uint(key) : default_value);
}

/**
 * @brief Return value for the key as an int.
 * If not found, return the default_value passed in.
 * @throws invalid_type - if corresponding value is not an int.
 */
int get_int(const std::string &key, int default_value) const
{
  return (is_set(key) ? get_int(key) : default_value);
}

/**
 * @brief Return value for the key as a string.
 * If not found, return the default_value passed in.
 * @throws invalid_type - if corresponding value is not a string.
 */
const std::string &get_string(const std::string &key, const std::string &default_value) const
{
  return (is_set(key) ? get_string(key) : default_value);
}

/**
 * @brief Return value for the key as a string.
 * If not found, return the default_value passed in.
 * @throws invalid_type - if corresponding value is not a string.
 */
const char *get_string(const std::string &key, const char *default_value) const
{
  return (is_set(key) ? get_string(key).c_str() : default_value);
}

/**
 * @brief Return value for the key as a double.
 * If not found, return the default_value passed in.
 * @throws invalid_type - if corresponding value is not a double.
 */
double get_double(const std::string &key, CFG_DOUBLE64 default_value) const
{
  return (is_set(key) ? get_double(key) : default_value);
}

/**
 * @brief Return the number of keys stored in this map.
 */
unsigned int size() const;

/**
 * @brief Emptie the contents of the map.
 */
void clear();

public:

// Set Validator
class Validator
{
public:
  virtual ~Validator() {}
  virtual bool validate(const std::string &key, const std::string &val) const = 0;
};

bool validate_set(const Validator &validator) const;

public:
void serialize_to(std::ostream &os) const;
void deserialize_from(std::istream &is);

static const CFG_Duration_t CFG_DURATION_INFINITE;
static const long CFG_DURATION_INFINITY_SEC;
static const unsigned long CFG_DURATION_INFINITY_NSEC;

};

//=============================================================================
/**
 * @ingroup Property
*  @brief A CfgProfile is simply ValueMap with a name.
*
*/
class CfgProfile: public ValueMap
{
public:
/**
* typedef for profile name
*/
typedef std::string CfgProfileName;

/** ANONYMOUS_NAME */
static const CfgProfileName ANONYMOUS_NAME;

/** EMPTY_PROFILE */
static const CfgProfile EMPTY_PROFILE;

protected:
CfgProfileName _theName;

public:
/**
 * @brief Create a CfgProfile with ANONYMOUS_NAME.
 */
CfgProfile()
  : ValueMap(),
    _theName(ANONYMOUS_NAME)
 {
 }

/**
 * @brief Constructor.
 */
CfgProfile(const CfgProfileName &name):
    ValueMap(),
    _theName(name)
{
}

/**
 * @brief Copy constructor.
 */
CfgProfile(const CfgProfile &ref)
  : ValueMap(ref),
    _theName(ref._theName)
{
}

/**
 * @brief Destructor.
 */
virtual ~CfgProfile() { }

CfgProfile & operator=(const CfgProfile &ref)
{
  ValueMap::operator=(ref);
  _theName = ref._theName;
  return *this;
}

/**
 * @brief Clone.
 */
CfgProfile clone() const
{
  CfgProfile retVal(_theName);
  ValueMap::clone_to(retVal);
  return retVal;
}

/**
 * @brief Clone.
 */
void clone_to(CfgProfile &map) const
{
  ValueMap::clone_to(map);
  map._theName = _theName;
}

/**
 * @brief Merge.
 */
void merge_to(CfgProfile &map) const
{
  ValueMap::merge_to(map);
  map._theName = _theName;
}

/**
 * @brief Set the name.
 * @throws read_only - if the ValueMap is read-only.
 */
void set_name(const CfgProfileName &name)
{
  if (_isReadOnly)
  {
    throw read_only(name);
  }
  _theName = name;
}

/**
 * @brief Get the name.
 */
CfgProfileName get_name() const
{
  return _theName;
}

};

//=============================================================================
/**
 * @ingroup Property
* @brief A CfgDictionary is a collection of CfgProfile objects.
*
* Profiles are organized inside a INI dictionary by name. You can add, search
* or delete CfgProfile objects.
*
* All the objects are passed by value.
*
* A CfgDictionary implements value copy by a destructive copy (like auto_ptr).
* Use the clone() method to make a physical copy.
*/
class CfgDictionary
{
private:
CfgDictionaryImpl * _theImpl;

/**
 * @brief Prohibit copy construction.
 */
CfgDictionary(const CfgDictionary &/*ref*/) { }

/**
 * @brief Prohibit assignment. Consider using clone().
 */
CfgDictionary &operator=(const CfgDictionary &ref);

public:
/**
 * @brief Build an empty Cfg dictionary.
 */
CfgDictionary();

/**
 * @brief Build a Cfg dictionary with only one CfgProfile object.
 */
CfgDictionary(const CfgProfile &profile);

/**
 * @brief Build a Cfg dictionary from the given file.
 *
 *   This supports INI style format.
 *   Read the entire content to memory.
 */
CfgDictionary(const std::string &filename);

/**
 * @brief Build a Cfg dictionary from the given input stream
 *  The stream is parsed and interpreted as an INI file. For exmaple:
 *
 *  <pre>
 *       # This is a comment
 *       ; This is also a comment
 *       [ MyProfileName ]
 *           key = value
 *           key = value
 *       [ MyName ]
 *           key = value
 *  </pre>
 *
 *  This ignores whitespaces.
 */
CfgDictionary(std::istream &source);

/**
 * @brief Destructor
 */
virtual ~CfgDictionary();

/**
 * @brief Clone the current dictionary.
 */
CfgDictionary clone() const;

/**
 * @brief Add or replace a profile.
 */
void add(const CfgProfile &profile);

/**
 * @brief Get an existing profile.
 * @return NULL if name not defined.
 */
CfgProfile *get(const CfgProfile::CfgProfileName &name) const;

/**
 * @brief Remove an existing.
 * @return true if removed, false if not found.
 */
bool remove(const CfgProfile::CfgProfileName &name);

/**
 * @brief Return the number of profiles stored in this dictionaryl
 */
unsigned int size() const;

};

#endif
