package javax.slee.management;

/**
 * The <code>AlarmMBean</code> interface defines the management interface for the
 * SLEE alarm subsystem.  Alarm sources such as SBBs and Resource Adaptor Entities
 * use the {@link javax.slee.facilities.AlarmFacility} to raise and clear alarms.
 * Using the <code>AlarmMBean</code>, a management client may obtain information
 * about the alarms currently active in the SLEE, and may selectively clear alarms
 * as required.  Whenever an alarm is raised or cleared an {@link AlarmNotification}
 * of the relevant type is generated.
 * <p>
 * The JMX Object Name of the <code>AlarmMBean</code> object is specified by the
 * {@link #OBJECT_NAME} constant.  The Object Name can also be obtained by
 * a management client via the {@link SleeManagementMBean#getAlarmMBean()} method.
 * <p>
 * <b>Alarm Identification</b><br>
 * Alarms in the SLEE are uniquely identified by three attributes:
 * <ul>
 *   <li>a <code>NotificationSource</code> object identifying the object in the SLEE
 *       that raised the alarm.  Typically this object is provided by the <code>AlarmFacility</code>
 *       used to raise the alarm.
 *   <li>an alarm type, specified by the notification source that raised the alarm.
 *       This attribute identifies the particular type of alarm, e.g. a database
 *       connection failure.
 *   <li>an instance ID, specified by the notification source that raised the alarm.
 *       This attribute identifies a particular instance of the alarm type, e.g.
 *       the name of the host where connection failures are being experienced.
 * </ul>
 * <p>
 * <b>Notifications</b><br>
 * Since an <code>AlarmMBean</code> object can emit alarm notifications, it is
 * required that the <code>AlarmMBean</code> object implement the
 * <code>javax.management.NotificationBroadcaster</code> interface.
 * <p>
 * <b>Note:</b> In SLEE 1.0 an Alarm MBean emitted only one type of notification,
 * defined by {@link #ALARM_NOTIFICATION_TYPE}.  As of SLEE 1.1, an <code>AlarmMBean</code>
 * object may emit alarm notifications of different types, depending on the
 * notification source of the object that raised the alarm.  The SLEE-defined
 * classes that implement {@link NotificationSource} each specify the type of
 * alarm notification that is generated for the notification source.
 */
public interface AlarmMBean {
    /**
     * The JMX Object Name string of the SLEE Alarm MBean, equal to the string
     * "javax.slee.management:name=Alarm".
     * @since SLEE 1.1
     */
    public static final String OBJECT_NAME = "javax.slee.management:name=Alarm";

    /**
     * The notification type of {@link AlarmNotification Alarm} notifications
     * emitted by this MBean.  The notification type is equal to the string
     * "javax.slee.management.alarm".
     * @deprecated Different notification types may be generated by the Alarm MBean depending
     * on where the alarm state was modified.  The SLEE-defined classes that implement
     * {@link NotificationSource} each specify a particular alarm notification type for the
     * notification source.
     */
    public static final String ALARM_NOTIFICATION_TYPE = "javax.slee.management.alarm";


    /**
     * Get the unique alarm identifiers for stateful alarms currently active in the
     * SLEE that were raised by the specified notification source.
     * @param notificationSource the component or subsystem in the SLEE of interest.
     * @return an array containing the alarm identifiers of all the alarms currently
     *        active in the SLEE that were raised by the specified notification source.
     * @throws NullPointerException if <code>notificationSource</code> is <code>null</code>.
     * @throws UnrecognizedNotificationSourceException if <code>notificationSource</code>
     *        does not identify a notification source recognizable by the SLEE.
     * @throws ManagementException if the alarm identifiers could not be obtained
     *        due to a system-level failure.
     * @since SLEE 1.1
     */
    public String[] getAlarms(NotificationSource notificationSource)
        throws NullPointerException, UnrecognizedNotificationSourceException, ManagementException;

    /**
     * Get the unique alarm identifiers of all stateful alarms currently active
     * in the SLEE.
     * @return an array containing the alarm identifiers of all the alarms currently
     *        active in the SLEE.
     * @throws ManagementException if the alarm identifier could not be obtained due
     *        to a system-level failure.
     * @since SLEE 1.1
     */
    public String[] getAlarms()
        throws ManagementException;

    /**
     * Determine if a specified alarm is (still) currently active in the SLEE.
     * @param alarmID the unique alarm identifier of the alarm.
     * @return <code>true</code> if an alarm with the specified alarm identifier is
     *        currently active in the SLEE, <code>false</code> otherwise.
     * @throws NullPointerException if <code>alarmID</code> is <code>null</code>.
     * @throws ManagementException if the existence of the alarm could not be determined
     *        due to a system-level failure.
     * @since SLEE 1.1
     */
    public boolean isActive(String alarmID)
        throws NullPointerException, ManagementException;

    /**
     * Get the alarm descriptor for an alarm identifier.
     * @param alarmID the unique alarm identifier.
     * @return the alarm descriptor for the alarm, or <code>null</code> if the
     *        specified alarm is not currently active in the SLEE.
     * @throws NullPointerException if <code>alarmID</code> is <code>null</code>.
     * @throws ManagementException if the alarm descriptor could not be obtained
     *        due to a system-level failure.
     * @since SLEE 1.1
     */
    public Alarm getDescriptor(String alarmID)
        throws NullPointerException, ManagementException;

    /**
     * Get an array of alarm descriptors corresponding to an arary of alarm identifiers.
     * @param alarmIDs an array of alarm identifiers.
     * @return an array of alarm descriptor objects.  This array will be the same length
     *        as the supplied array, and if <code>descriptors = getDescriptors(alarmIDs)</code>
     *        then <code>descriptors[i] == getDescriptor(alarmIDs[i])</code>.  Any
     *        alarm identifier present in <code>ids</code> that does not identify a
     *        activty alarm in the SLEE results in a <code>null</code> value at the
     *        corresponding array index in this array.
     * @throws NullPointerException if <code>alarmIDs</code> is <code>null</code>.
     * @throws ManagementException if the alarm descriptors could not be obtained
     *        due to a system-level failure.
     * @since SLEE 1.1
     */
    public Alarm[] getDescriptors(String[] alarmIDs)
        throws NullPointerException, ManagementException;

    /**
     * Clear the specified alarm.  If the alarm has already been cleared, or no such
     * alarm exists in the SLEE, this method returns with no effect.
     * @param alarmID the unique alarm identifier for the alarm.
     * @return <code>true</code> if the specified alarm existed and was cleared as a
     *        result of this method, <code>false</code> otherwise.
     * @throws NullPointerException if <code>alarmID</code> is <code>null</code>.
     * @throws ManagementException if the alarm could not be cleared due to a system-level
     *        failure.
     * @since SLEE 1.1
     */
    public boolean clearAlarm(String alarmID)
        throws NullPointerException, ManagementException;

    /**
     * Clear all active alarms of the specified type raised by the specified notification
     * source.  If no active alarms of the specified type are found, this method has
     * no effect.
     * @param notificationSource the component or subsystem in the SLEE which raised
     *        the alarms.
     * @param alarmType the type of the alarms to clear.
     * @return the number of alarms cleared by this method call.  May be zero if
     *        no alarms were cleared.
     * @throws NullPointerException if either argument is <code>null</code>.
     * @throws UnrecognizedNotificationSourceException if <code>notificationSource</code>
     *        does not identify a notification source recognizable by the SLEE.
     * @throws ManagementException if the alarms could not be cleared due to a system-level
     *        failure.
     * @since SLEE 1.1
     */
    public int clearAlarms(NotificationSource notificationSource, String alarmType)
        throws NullPointerException, UnrecognizedNotificationSourceException, ManagementException;

    /**
     * Clear all active alarms raised by the specified notification source.  If no
     * active alarms exist for the notification source, this method has no effect.
     * @param notificationSource the component or subsystem in the SLEE which raised
     *        the alarms.
     * @return the number of alarms cleared by this method call.  May be zero if
     *        no alarms were cleared.
     * @throws NullPointerException if <code>notificationSource</code> is <code>null</code>.
     * @throws UnrecognizedNotificationSourceException if <code>notificationSource</code>
     *        does not identify a notification source recognizable by the SLEE.
     * @throws ManagementException if the alarms could not be cleared due to a system-level
     *        failure.
     * @since SLEE 1.1
     */
    public int clearAlarms(NotificationSource notificationSource)
        throws NullPointerException, UnrecognizedNotificationSourceException, ManagementException;

}
