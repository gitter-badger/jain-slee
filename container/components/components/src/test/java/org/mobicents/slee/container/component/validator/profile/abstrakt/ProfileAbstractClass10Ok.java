/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/**
 * Start time:16:31:35 2009-02-14<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.container.component.validator.profile.abstrakt;

import javax.slee.CreateException;
import javax.slee.SLEEException;
import javax.slee.profile.ProfileContext;
import javax.slee.profile.ProfileID;
import javax.slee.profile.ProfileVerificationException;
import javax.slee.usage.UnrecognizedUsageParameterSetNameException;

/**
 * Start time:16:31:35 2009-02-14<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class ProfileAbstractClass10Ok implements
		javax.slee.profile.ProfileManagement, ManagementInterfaceOk,
		ProfileBaseCMPInterface {

  // FIXME: Alexandre: Removed due to Section 10.8 of JAIN SLEE 1.0 Specification:
  //
  // The isProfileDirty, markProfileDirty and  isProfileValid methods must not be 
  // implemented as they are implemented by the SLEE. These three methods are implemented by the 
  // SLEE at deployment time.
  //
	//public boolean isProfileDirty() {
	//	// TODO Auto-generated method stub
	//	return false;
	//}
  //
	//public boolean isProfileValid(ProfileID arg0) throws NullPointerException,
	//		SLEEException {
	//	// TODO Auto-generated method stub
	//	return false;
	//}
  //
	//public void markProfileDirty() {
	//	// TODO Auto-generated method stub
  //
	//}

	public void profileInitialize() {
		// TODO Auto-generated method stub

	}

	public void profileLoad() {
		// TODO Auto-generated method stub

	}

	public void profileStore() {
		// TODO Auto-generated method stub

	}

	public void profileVerify() throws ProfileVerificationException {
		// TODO Auto-generated method stub

	}

	// profile LO methods

	public void makeDogBark(int xxxx) {
	}

	public void dontLookAtMeImUglyDefinedMethodWithLongNameAndSpankTheCat(
			java.io.Serializable cheese) {
	}

	// Management interface
	public void doSomeTricktMGMTMagic(int xxxx) {
	}

	public void dontLookAtMeImUglyDefinedMethodWithLongName(
			java.io.Serializable cheese) {
	}

	// USAGE

	public abstract UsageOkInterface getDefaultUsageParameterSet();

	public abstract UsageOkInterface getUsageParameterSet(String x)
			throws UnrecognizedUsageParameterSetNameException;

}
