/** 
 *
 * Disclaimer: Please note that this does NOT use a read BRMS system. There is no integration with any 
 * formal rule engines, thought it is a direction that could be pursued. 
 *
 * Rule-Based-Safety application is intended to extend PCA safety demo and give user the ability to 
 * load an arbitrary rule into the system and provide custom evaluation and trigger handlers. 
 *
 * The application builds on the notion that a collection of vital signs (vital model) is evaluated 
 * in real-time to ensure that all parameters are within their respective safety ranges. 
 * Once any of the vitals go outside of normal (into ‘warning’ or ‘alarm’) zones, evaluation 
 * function can run its algorithm and determine if the entire model should move from ‘normal’ into
 * ‘warning’  or ‘alarm’ states. VitalModel acts as a container for a single rule, a state machine,
 * and a mechanism  to notify external observers about state transitions. 
 *
 * The configuration of the rule and evaluation handlers are defined in the javascript file that is 
 * required to implement the following functions: 
 *
 * <code> 
 * var VitalSign   = org.mdpnp.apps.testapp.vital.VitalSign; 
 * var State       = org.mdpnp.apps.testapp.vital.VitalModel.State; 
 *
 * // Function called once at the create time. Should populate the model with the 
 * // required vitals and set up the thresholds. Returns a json object containing 
 * // version information and the html file with the description of the rule. 
 * //
 * var create = function (model) { 
 *
 *      var temperature = VitalSign.Temperature.addToModel(model); 
 *      temperature.setCriticalLow(20); 
 *      temperature.setWarningLow(32.0); 
 *      temperature.setWarningHigh(39.0); 
 *      temperature.setCriticalHigh(42.0); 
 *      temperature.setRequired(true); 
 *
 *      var obj =  *  { 
 *          "version":1, 
 *          "ruleDescription":"temperature-monitor-info.html" 
 *      }; 
 *      return obj; 
 *  }; 
 *
 *  // Evaluation function called every time the vitals change their values. System logic evaluates
 *  // the values against the threshold limits and calls the evaluate method to calculate the new
 *  // state of the system. A collection of advisories is passed in for every vital that is not in the
 *  // ‘normal’ state. 
 *  //
 *  var evaluate = function(advisories) { 
 *      var temperature = advisories.get(VitalSign.Temperature); 
 *      if(temperature!= null && temperature.state == State.Alarm) {
 *          return State.Alarm; 
 *      }
 *      else { 
 *          return State.Normal; 
 *     } 
 *  }; 
 *
 *  // Function called once at the create time. Returns a json object containing 
 *  // reference to a the html file with the description of the emergency procedure. 
 *  // 
 *  var handleAlarm = function() { 
 *      var obj =  { 
 *          "statusInformation": " temperature-monitor-alarm.html" 
 *          }; 
 *      return obj; 
 *  };
 *
 * </code>
 *
 */ 
package org.mdpnp.apps.testapp.rbs;