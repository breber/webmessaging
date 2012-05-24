/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.brianreber.messaging.server;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.google.android.c2dm.server.PMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


public class RegistrationInfo {

	private static final Logger log = Logger.getLogger(RegistrationInfo.class.getName());

	private static final int MAX_DEVICES = 5;

	String accountName;

	String deviceId;

	String deviceRegistrationId;

	public RegistrationInfo() {
	}

	public String getAccountName() {
		return accountName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public String getDeviceRegistrationId() {
		return deviceRegistrationId;
	}

	public void register() {
		log.log(Level.INFO, "register " + this);
		try {
			doRegister(getDeviceRegistrationId(), "ac2dm", getDeviceId(), getAccountName());
		} catch (Exception e) {
			log.log(Level.INFO, "Got exception in registration: " + e + " - " + e.getMessage());
			for (StackTraceElement ste : e.getStackTrace()) {
				log.log(Level.INFO, ste.toString());
			}
		}
		log.log(Level.INFO, "Successfully registered");
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setDeviceRegistrationId(String deviceRegistrationId) {
		this.deviceRegistrationId = deviceRegistrationId;
	}

	@Override
	public String toString() {
		return "RegistrationInfo [accountName=" + accountName + ", deviceId=" + deviceId
				+ ", deviceRegistrationId=" + deviceRegistrationId + "]";
	}

	public void unregister() {
		log.log(Level.INFO, "unregister " + this);
		try {
			doUnregister(getDeviceRegistrationId(), getAccountName());
		} catch (Exception e) {
			log.log(Level.INFO, "Got exception in unregistration: " + e + " - " + e.getMessage());
			for (StackTraceElement ste : e.getStackTrace()) {
				log.log(Level.INFO, ste.toString());
			}
		}
		log.log(Level.INFO, "Successfully unregistered");
	}

	private void doRegister(String deviceRegistrationId, String deviceType, String deviceId,
			String accountName) throws Exception {
		log.log(Level.INFO, "in register: accountName = " + accountName);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			List<DeviceInfo> registrations = DeviceInfo.getDeviceInfoForUser(accountName);

			log.log(Level.INFO, "got registrations");
			if (registrations.size() > MAX_DEVICES) {
				log.log(Level.INFO, "got registrations > MAX_DEVICES");
				// we could return an error - but user can't handle it yet.
				// we can't let it grow out of bounds.
				// TODO: we should also define a 'ping' message and expire/remove
				// unused registrations
				DeviceInfo oldest = registrations.get(0);
				if (oldest.getRegistrationTimestamp() == null) {
					pm.deletePersistent(oldest);
				} else {
					long oldestTime = oldest.getRegistrationTimestamp().getTime();
					for (int i = 1; i < registrations.size(); i++) {
						if (registrations.get(i).getRegistrationTimestamp().getTime() < oldestTime) {
							oldest = registrations.get(i);
							oldestTime = oldest.getRegistrationTimestamp().getTime();
						}
					}
					pm.deletePersistent(oldest);
				}
			}

			// Get device if it already exists, else create
			String suffix =
					(deviceId != null ? "#" + Long.toHexString(Math.abs(deviceId.hashCode())) : "");
			log.log(Level.INFO, "suffix = " + suffix);
			Key key = KeyFactory.createKey(DeviceInfo.class.getSimpleName(), accountName + suffix);
			log.log(Level.INFO, "key = " + key);

			DeviceInfo device = null;
			try {
				device = pm.getObjectById(DeviceInfo.class, key);
			} catch (JDOObjectNotFoundException e) {
				log.log(Level.INFO, "Caught JDOObjectNotFoundException");
			}
			if (device == null) {
				device = new DeviceInfo(key, deviceRegistrationId);
				device.setType(deviceType);
			} else {
				// update registration id
				device.setDeviceRegistrationID(deviceRegistrationId);
				device.setRegistrationTimestamp(new Date());
			}

			pm.makePersistent(device);
			return;
		} catch (Exception e) {
			log.log(Level.INFO, "Caught exception: " + e);
			throw e;
		} finally {
			pm.close();
		}
	}

	private void doUnregister(String deviceRegistrationID, String accountName) {
		log.log(Level.INFO, "in unregister: accountName = " + accountName);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			List<DeviceInfo> registrations = DeviceInfo.getDeviceInfoForUser(accountName);
			for (int i = 0; i < registrations.size(); i++) {
				DeviceInfo deviceInfo = registrations.get(i);
				if (deviceInfo.getDeviceRegistrationID().equals(deviceRegistrationID)) {
					pm.deletePersistent(deviceInfo);
					// Keep looping in case of duplicates
				}
			}
		} catch (JDOObjectNotFoundException e) {
			log.warning("User " + accountName + " unknown");
		} catch (Exception e) {
			log.warning("Error unregistering device: " + e.getMessage());
		} finally {
			pm.close();
		}
	}
}
