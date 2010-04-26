package org.mobicents.slee.container.management.jmx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.management.NotCompliantMBeanException;
import javax.slee.ServiceID;
import javax.slee.management.ManagementException;
import javax.slee.management.ServiceState;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.container.component.sbb.GetChildRelationMethodDescriptor;
import org.mobicents.slee.container.sbbentity.ChildRelation;
import org.mobicents.slee.container.sbbentity.SbbEntity;
import org.mobicents.slee.container.sbbentity.SbbEntityFactory;
import org.mobicents.slee.container.service.Service;

public class SbbEntitiesMBeanImpl extends MobicentsServiceMBeanSupport implements
		SbbEntitiesMBeanImplMBean {
	
	private final SbbEntityFactory sbbEntityFactory;
	
	public SbbEntitiesMBeanImpl(SleeContainer sleeContainer) throws NotCompliantMBeanException {
		super(sleeContainer,SbbEntitiesMBeanImplMBean.class);
		this.sbbEntityFactory = sleeContainer.getSbbEntityFactory();
	}

	public Object[] retrieveSbbEntitiesBySbbId(String sbbId) {
		// FIXME retrieveSbbEntitiesBySbbId
		// emmartins: this one is even uglier than going through all sbb
		// entity trees, because you need to find all child relations with this
		// Id, in all services, and then collect all child sbb entities, why not
		// only by service ID, code in
		// retreiveAllSbbEntitiesIds can be reused
		return null;
	}

	public Object[] retrieveAllSbbEntities() throws ManagementException {
		ArrayList result = new ArrayList();
		try {

			Iterator<String> sbbes = retrieveAllSbbEntitiesIds().iterator();
			while (sbbes.hasNext()) {
				try {
					SbbEntity sbbe = sbbEntityFactory.getSbbEntity(sbbes.next(),false);
					result.add(sbbEntityToArray(sbbe));
				} catch (Exception e) {
					// ignore
				}
			}
			return result.toArray();
		} catch (Exception e) {
			throw new ManagementException(
					"Failed to build set of existent sbb entities", e);
		}
	}

	private Set<String> retrieveAllSbbEntitiesIds() throws SystemException, NullPointerException, ManagementException, NotSupportedException {
		Set<String> result = new HashSet<String>();

		final SleeContainer sleeContainer = getSleeContainer();

		try {
			sleeContainer.getTransactionManager().begin();

			for (ServiceID serviceID : sleeContainer.getServiceManagement().getServices(ServiceState.ACTIVE)) {
				try {
					Service service = sleeContainer.getServiceManagement()
							.getService(serviceID);
					for (String rootSbbId : (Collection<String>) service
							.getChildObj()) {
						result.addAll(getChildSbbEntities(rootSbbId));
					}
				} catch (Exception e) {
					// ignore
				}
			}
		} finally {
			try {
				sleeContainer.getTransactionManager().rollback();
			} catch (SystemException e) {
				// ignore
			}
		}

		return result;
	}

	private Set<String> getChildSbbEntities(String sbbEntityId) {

		Set<String> result = new HashSet<String>();

		try {
			SbbEntity sbbEntity = getSbbEntityById(sbbEntityId);
			for (GetChildRelationMethodDescriptor method : sbbEntity.getSbbComponent().getDescriptor().getGetChildRelationMethodsMap().values()) {
				ChildRelation childRelationImpl = sbbEntity.getChildRelation(method.getChildRelationMethodName());
				if (childRelationImpl != null) {
					for (String childSbbEntityId : childRelationImpl.getSbbEntitySet()) {
						result.addAll(getChildSbbEntities(childSbbEntityId));
					}
				}
			}
			result.add(sbbEntityId);
		} catch (Exception e) {
			// ignore
		}

		return result;
	}

	private Object[] sbbEntityToArray(SbbEntity entity) {
		Object[] info = new Object[10];
		try {
			SleeContainer sleeContainer = getSleeContainer();
			sleeContainer.getTransactionManager().begin();
			if (entity == null)
				return null;
			info[0] = entity.getSbbEntityId();
			info[1] = entity.getParentSbbEntityId();
			info[2] = entity.getRootSbbId();
			info[3] = entity.getSbbId().toString();
			info[4] = Byte.toString(entity.getPriority());
			info[5] = entity.getServiceConvergenceName();
			// FIXME to remove in mmc
			info[6] = null;
			if (entity.getServiceId() != null)
				info[7] = entity.getServiceId().toString();
			// FIXME to remove in mmc
			info[8] = null;
			Set acsSet = entity.getActivityContexts();
			if (acsSet != null && acsSet.size() > 0) {
				Object[] acsArray = acsSet.toArray();
				String[] acs = new String[acsArray.length];
				info[9] = acs;
			}
			sleeContainer.getTransactionManager().commit();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return info;
	}

	private SbbEntity getSbbEntityById(String sbbeId) {
		try {
			return sbbEntityFactory.getSbbEntity(sbbeId, false);
		} catch (Exception e) {
			return null;
		}

	}

	public Object[] retrieveSbbEntityInfo(String sbbeId) {
		SbbEntity entity = getSbbEntityById(sbbeId);
		return sbbEntityToArray(entity);
	}

	public void removeSbbEntity(String sbbeId) {
		try {
			SleeContainer sleeContainer = getSleeContainer();
			sleeContainer.getTransactionManager().begin();
			sbbEntityFactory.removeSbbEntity(getSbbEntityById(sbbeId), true,false);
			sleeContainer.getTransactionManager().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
