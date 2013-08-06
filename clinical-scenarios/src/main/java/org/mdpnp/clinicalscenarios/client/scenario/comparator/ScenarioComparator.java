package org.mdpnp.clinicalscenarios.client.scenario.comparator;

import java.lang.reflect.Method;
import java.util.Comparator;

import org.mdpnp.clinicalscenarios.client.scenario.ScenarioProxy;

public class ScenarioComparator implements Comparator<ScenarioProxy> {
	private String scenarioProperty;//property name
	private boolean reverseOrder;
	
	public static final String PROPERTY_TITLE 		 = "title"; 
	public static final String PROPERTY_ID 			 = "id"; 
	public static final String PROPERTY_SUBMITTER	 = "submitter"; 
	public static final String PROPERTY_STATUS		 = "status"; 
	
	
	
	public ScenarioComparator(String prop){
		this.scenarioProperty = prop;
		reverseOrder = false;
	}
	
	public ScenarioComparator(String prop, boolean b){
		this.scenarioProperty = prop;
		reverseOrder = b;
	}
	
	//getter and setter
	public String getProperty() {
		return scenarioProperty;
	}

	public void setProperty(String property) {
		this.scenarioProperty = property;
	}
	
	public void switchReverseOrder(){
		reverseOrder = ! reverseOrder;
	}

	public int compare(ScenarioProxy o1, ScenarioProxy o2) {
		
		if(scenarioProperty!=null){
			if(scenarioProperty.equals(PROPERTY_ID)){
			      if(reverseOrder)
			    	  return -1*( o1.getId().compareTo(o2.getId()) );
			      else
			    	  return o1.getId().compareTo(o2.getId()) ; 
			}else if (scenarioProperty.equals(PROPERTY_TITLE)){
			      if(reverseOrder)
			    	  return -1*( o1.getTitle().compareTo(o2.getTitle()) );
			      else
			    	  return o1.getTitle().compareTo(o2.getTitle() ) ; 
			}else if (scenarioProperty.equals(PROPERTY_SUBMITTER)){
			      if(reverseOrder)
			    	  return -1*( o1.getSubmitter().compareTo(o2.getSubmitter()) ) ;
			      else
			    	  return o1.getSubmitter().compareTo(o2.getSubmitter()) ; 
			}else if(scenarioProperty.equals(PROPERTY_STATUS)){
			      if(reverseOrder)
			    	  return -1*( o1.getStatus().compareTo(o2.getStatus()) );
			      else
			    	  return o1.getStatus().compareTo(o2.getStatus()) ;
			}else
				return 0;
		}
		return 0;
//	      diego@mdpnp.org
//		Since GWT code is translated to Javascript direct usage of reflection API is not supported
//	      I don't really want to, but if reflexion doesn't work we are going to need to add
//	      one method/property for each property in the UserInfoProxy class, and maintain that when ned properties are added
//	      
//		 Class<?> myclass = o1.getClass();  
//		 String getter = "get" + Character.toUpperCase(this.scenarioProperty.charAt(0)) + scenarioProperty.substring(1);
//		 try {  
//			 Method getPropiedad = myclass.getMethod(getter);  
//		       
//		     Object property1 = getPropiedad.invoke(o1);  
//		     Object property2 = getPropiedad.invoke(o2);  
//		     			     
//		     if(property1 instanceof Comparable && property2 instanceof Comparable) {  
//		      Comparable prop1 = (Comparable)property1;  
//		      Comparable prop2 = (Comparable)property2;  
//		      if(reverseOrder)
//		    	  return -1*( prop1.compareTo(prop2) );  
//		      else
//		    	  return prop1.compareTo(prop2) ;  
//		     } 
//		     else { //IF THEY ARE NOT COMPARABLE 
//		    	 return 0;//doesn´t compare /sort		  
//		     }  		    
//		    }  
//		    catch(Exception e) {  
//		    	//NoSuchMethod
//		    	e.printStackTrace();  
//		    	return 0;//Doesn´t compare/sort
//		    } 
	}
}
