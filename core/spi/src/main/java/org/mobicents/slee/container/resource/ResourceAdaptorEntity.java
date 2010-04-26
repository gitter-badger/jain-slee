/**
 * 
 */
package org.mobicents.slee.container.resource;

import java.util.Set;

import javax.slee.Address;
import javax.slee.EventTypeID;
import javax.slee.InvalidStateException;
import javax.slee.ServiceID;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.facilities.AlarmFacility;
import javax.slee.management.NotificationSource;
import javax.slee.management.ResourceAdaptorEntityState;
import javax.slee.management.ResourceUsageMBean;
import javax.slee.resource.ActivityHandle;
import javax.slee.resource.ConfigProperties;
import javax.slee.resource.FailureReason;
import javax.slee.resource.FireableEventType;
import javax.slee.resource.InvalidConfigurationException;
import javax.slee.resource.Marshaler;
import javax.slee.resource.ReceivableService;
import javax.slee.resource.ResourceAdaptor;
import javax.slee.resource.ResourceAdaptorID;
import javax.slee.resource.ResourceAdaptorTypeID;

import org.mobicents.slee.container.activity.ActivityContextHandle;
import org.mobicents.slee.container.component.ra.ResourceAdaptorComponent;

/**
 * @author martins
 * 
 */
public interface ResourceAdaptorEntity {

	/**
	 * Activates the ra entity
	 * 
	 * @throws InvalidStateException
	 *             if the entity is not in INACTIVE state
	 */
	public void activate() throws InvalidStateException;

	/**
	 * Callback to notify the entity and possibly the ra object, informing
	 * activity handled ended.
	 * 
	 * @see ResourceAdaptor#activityEnded(ActivityHandle)
	 * 
	 * @param handle
	 * @param activityFlags
	 */
	public void activityEnded(final ActivityHandle handle, int activityFlags);

	public void allActivitiesEnded();

	/**
	 * Deactivates the ra entity
	 * 
	 * @throws InvalidStateException
	 *             if the entity is not in ACTIVE state
	 * @throws TransactionRequiredLocalException
	 */
	public void deactivate() throws InvalidStateException,
			TransactionRequiredLocalException;

	/**
	 * Callback to notify the ra object that the processing for specified event
	 * failed.
	 * 
	 * @see ResourceAdaptor#eventProcessingFailed(ActivityHandle,
	 *      FireableEventType, Object, Address, ReceivableService, int,
	 *      FailureReason)
	 * 
	 * @param deferredEvent
	 * @param failureReason
	 */
	public void eventProcessingFailed(ActivityHandle handle,
			FireableEventType eventType, Object event, Address address,
			ReceivableService service, int flags, FailureReason reason);

	/**
	 * Callback to notify the ra object that the processing for specified event
	 * succeed
	 * 
	 * @see ResourceAdaptor#eventProcessingSuccessful(ActivityHandle,
	 *      FireableEventType, Object, Address, ReceivableService, int)
	 * 
	 * @param deferredEvent
	 */
	public void eventProcessingSucceed(ActivityHandle handle,
			FireableEventType eventType, Object event, Address address,
			ReceivableService service, int flags);

	/**
	 * Callback to notify the ra object that the specified event is now
	 * unreferenced.
	 * 
	 * @see ResourceAdaptor#eventUnreferenced(ActivityHandle, FireableEventType,
	 *      Object, Address, ReceivableService, int)
	 * 
	 * @param handle
	 * @param eventType
	 * @param event
	 * @param address
	 * @param service
	 * @param flags
	 */
	public void eventUnreferenced(ActivityHandle handle,
			FireableEventType eventType, Object event, Address address,
			ReceivableService service, int flags);

	/**
	 * @param activityHandle
	 * @return
	 */
	public ActivityContextHandle getActivityContextHandle(
			ActivityHandle activityHandle);

	public AlarmFacility getAlarmFacility();

	/**
	 * Retrieves a set containing event types allowed to be fire by this entity
	 * 
	 * @return null if the ra ignores event type checking
	 */
	public Set<EventTypeID> getAllowedEventTypes();

	/**
	 * Retrieves ra component related to this entity
	 * 
	 * @return
	 */
	public ResourceAdaptorComponent getComponent();

	/**
	 * Retrieves the active config properties for the entity
	 * 
	 * @return
	 */
	public ConfigProperties getConfigurationProperties();

	/**
	 * 
	 * @param eventTypeID
	 * @return
	 */
	public FireableEventType getFireableEventType(EventTypeID eventTypeID);

	/**
	 * Retrieves the marshaller from the ra object, if exists
	 * 
	 * @return
	 */
	public Marshaler getMarshaler();

	/**
	 * Retrieves the ra entity name
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Return Notification source representing this RA Entity
	 * 
	 * @return
	 */
	public NotificationSource getNotificationSource();

	/**
	 * 
	 * @param serviceID
	 * @return
	 */
	public ReceivableService getReceivableService(ServiceID serviceID);

	/**
	 * Retrieves the id of the resource adaptor for this entity
	 * 
	 * @return
	 */
	public ResourceAdaptorID getResourceAdaptorID();

	/**
	 * Retrieves the ra interface for this entity and the specified ra type
	 * 
	 * @param raType
	 * @return
	 */
	public Object getResourceAdaptorInterface(ResourceAdaptorTypeID raType);

	// --------- ra entity/object logic

	/**
	 * Retrieves the ra object
	 * 
	 * @return
	 */
	public ResourceAdaptorObject getResourceAdaptorObject();

	/**
	 * Retrieves the resource usage mbean for this ra, may be null
	 * 
	 * @return
	 */
	public ResourceUsageMBean getResourceUsageMBean();

	/**
	 * Retrieves the ra entity state
	 * 
	 * @return
	 */
	public ResourceAdaptorEntityState getState();

	/**
	 * Removes the entity, it will unconfigure and unset the ra context, the
	 * entity object can not be reused
	 * 
	 * @throws InvalidStateException
	 */
	public void remove() throws InvalidStateException;

	/**
	 * Indicates a service was activated, the entity will forward this
	 * notification to the ra object.
	 * 
	 * @param serviceInfo
	 */
	public void serviceActive(ServiceID serviceID);

	/**
	 * Indicates a service was deactivated, the entity will forward this
	 * notification to the ra object.
	 * 
	 * @param serviceInfo
	 */
	public void serviceInactive(ServiceID serviceID);

	/**
	 * Indicates a service is stopping, the entity will forward this
	 * notification to the ra object.
	 * 
	 * @param serviceInfo
	 */
	public void serviceStopping(ServiceID serviceID);
	
	/**
	 * Signals that the container is in RUNNING state
	 */
	public void sleeRunning() throws InvalidStateException;

	/**
	 * Signals that the container is in STOPPING state
	 * 
	 * @throws TransactionRequiredLocalException
	 */
	public void sleeStopping() throws InvalidStateException,
			TransactionRequiredLocalException;

	/**
	 * Updates the ra entity config properties
	 */
	public void updateConfigurationProperties(ConfigProperties properties)
			throws InvalidConfigurationException, InvalidStateException;

}
