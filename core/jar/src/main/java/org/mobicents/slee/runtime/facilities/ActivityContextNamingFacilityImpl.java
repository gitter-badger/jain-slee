/*
 * Created on Aug 11, 2004
 *
 * The Open SLEE project
 * 
 * A SLEE for the People!
 * 
 * The source code contained in this file is in in the public domain.          
 * It can be used in any project or product without prior permission, 	      
 * license or royalty payments. There is no claim of correctness and
 * NO WARRANTY OF ANY KIND provided with this code.
 * 
 */
package org.mobicents.slee.runtime.facilities;

import java.util.HashMap;
import java.util.Map;

import javax.slee.ActivityContextInterface;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.FacilityException;
import javax.slee.facilities.NameAlreadyBoundException;
import javax.slee.facilities.NameNotBoundException;
import javax.transaction.SystemException;

import org.jboss.logging.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.runtime.ActivityContext;
import org.mobicents.slee.runtime.ActivityContextIDInterface;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;
import org.mobicents.slee.runtime.cache.CacheableMap;
import org.mobicents.slee.runtime.transaction.TransactionManagerImpl;

/*
 * Ranga - Initial and refactored for Tx isolation.
 * Tim  
 */

/**
 * Activity Context Naming facility implementation.
 * 
 * @author M. Rangananthan.
 * @author Tim 
 * 
 *  
 */
public class ActivityContextNamingFacilityImpl implements
        ActivityContextNamingFacility {

    private static final String tcache = TransactionManagerImpl.RUNTIME_CACHE;
   
    private static Logger log = Logger.getLogger(ActivityContextNamingFacilityImpl.class);
    
    private static String MAP_NODE_NAME = "namedActivityContexts";
    
    /**
     * tx isolated cache map. 
     * the map as a whole is isolated from other transactions. phantom reads for map entries are not allowed.
     */      
    private Map getNameMap() throws Exception {
    	   // phantom reads are not allowed for ACI access
    	   CacheableMap cmap = new CacheableMap(tcache + "-" + MAP_NODE_NAME);
    	   cmap.setAllowPhantomReads(false);
    	   return cmap;
   }
    
    /**
     * constructor.
     *  
     */
    public ActivityContextNamingFacilityImpl() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.slee.facilities.ActivityContextNamingFacility#bind(javax.slee.ActivityContextInterface,
     *      java.lang.String)
     */
    public void bind(ActivityContextInterface aci, String aciName)
            throws NullPointerException, IllegalArgumentException,
            TransactionRequiredLocalException, NameAlreadyBoundException,
            FacilityException {
        // Check if we are in the context of a transaction.
        
        SleeContainer.getTransactionManager().mandateTransaction();
            
        if (aciName == null)
            throw new NullPointerException("null aci name");
        else if (aciName.equals(""))
            throw new IllegalArgumentException("empty name");
        else if (aci == null )
            throw new NullPointerException ("Null ACI! ");
        
      
        
        
        try {        
            Map hmap = this.getNameMap();
            
            String nodeName = aciName;
            if ( hmap.containsKey(nodeName)) 
                throw new NameAlreadyBoundException("Name already bound ! " + aciName);	
	       
	        //Creating a new node per binding allows fine grained locking	        
	        String acId = ((ActivityContextIDInterface)aci).retrieveActivityContextID();  
	        hmap.put(nodeName,acId);
           
        	
        	SleeContainer.lookupFromJndi().getActivityContextFactory()
        		.getActivityContextById(acId).addNameBinding(aciName);
        } catch (NameAlreadyBoundException ex) {
            if (log.isDebugEnabled()) {
                log.debug("name already bound " + aciName);
            }
            throw ex;
        } catch (Exception e) {
        	throw new FacilityException("Failed to put ac name binding in cache", e);
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.slee.facilities.ActivityContextNamingFacility#unbind(java.lang.String)
     */
    public void unbind(String aciName) throws NullPointerException,
            TransactionRequiredLocalException, NameNotBoundException,
            FacilityException {
        //Check if we are in the context of a transaction.
        
        SleeContainer.getTransactionManager().mandateTransaction();

        if (aciName == null)
            throw new NullPointerException("null activity context name!");
        
        String nodeName =  aciName;
        
        try {       
            
            Map acNames = this.getNameMap();
            if (! acNames.containsKey(aciName)) 
                throw new NameNotBoundException ("Name not bound " + aciName);
           
            String acId = (String) acNames.get(aciName);
            acNames.remove(nodeName);
            ActivityContext ac = SleeContainer.lookupFromJndi().
            	getActivityContextFactory().getActivityContextById(acId);
            if ( ac != null )
                ac.removeNameBinding(aciName);
        
        } catch ( NameNotBoundException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Name not bound " + aciName);
            }
            throw ex;
        } catch (Exception e) {
        	throw new FacilityException("Failed to remove ac name binding from cache", e);
        }
	      
    }
        
    public void unbindWithoutCheck(String aciName) throws NullPointerException,
		    TransactionRequiredLocalException, NameNotBoundException, FacilityException 
    {
		//Check if we are in the context of a transaction.
		
		SleeContainer.getTransactionManager().mandateTransaction();
		if (log.isDebugEnabled()) {
	        log.debug("Unbinding: aci " + aciName);
		}
		
		if (aciName == null)
		    throw new NullPointerException("null activity context name!");
		
		
		try {        		    
		    Map acNames = this.getNameMap();
          
            String acId = (String) acNames.get(aciName);
            acNames.remove(aciName);
            ActivityContext ac = SleeContainer.lookupFromJndi().getActivityContextFactory()
    		.getActivityContextById(acId);
            if ( ac != null )
                ac.removeNameBinding(aciName);
        
        
		} catch (Exception e) {
			throw new FacilityException("Failed to remove ac name binding from cache", e);
		}
  
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.slee.facilities.ActivityContextNamingFacility#lookup(java.lang.String)
     */
    public ActivityContextInterface lookup(String acName)
            throws NullPointerException, TransactionRequiredLocalException,
            FacilityException {
        
        SleeContainer.getTransactionManager().mandateTransaction();
       
        if (acName == null)
            throw new NullPointerException("null ac name");
        
        try {        
            
            Map acNames = this.getNameMap();
            String acId =  (String) acNames.get(acName);
 
        	if (acId == null ) return null;
        	SleeContainer sleeContainer = SleeContainer.lookupFromJndi();
        	return new ActivityContextInterfaceImpl(acId);        
        } catch (Exception e) {
        	throw new FacilityException("Failed to look-up ac name binding", e);
        }
        
    }
    
    public Map getBindings()
    {
    	
    	try {
			Map acNames = this.getNameMap();
			return new HashMap(acNames);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return null;
    	
    	
    }
}