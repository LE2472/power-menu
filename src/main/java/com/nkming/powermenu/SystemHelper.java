/*
 * SystemHelper.java
 *
 * Author: Ming Tsang
 * Copyright (c) 2015 Ming Tsang
 * Refer to LICENSE for details
 */

package com.nkming.powermenu;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.nkming.utils.Res;

import java.lang.reflect.Field;

public class SystemHelper
{
	public static boolean shutdown(Context context)
	{
		try
		{
			Field fieldACTION_REQUEST_SHUTDOWN = Intent.class.getDeclaredField(
					"ACTION_REQUEST_SHUTDOWN");
			fieldACTION_REQUEST_SHUTDOWN.setAccessible(true);
			String ACTION_REQUEST_SHUTDOWN =
					(String)fieldACTION_REQUEST_SHUTDOWN.get(null);

			Field fieldEXTRA_KEY_CONFIRM = Intent.class.getDeclaredField(
					"EXTRA_KEY_CONFIRM");
			fieldACTION_REQUEST_SHUTDOWN.setAccessible(true);
			String EXTRA_KEY_CONFIRM = (String)fieldEXTRA_KEY_CONFIRM.get(null);

			Intent shutdown = new Intent(ACTION_REQUEST_SHUTDOWN);
			shutdown.putExtra(EXTRA_KEY_CONFIRM, false);
			shutdown.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(shutdown);
			return true;
		}
		catch (Exception e)
		{
			Log.e(LOG_TAG + ".shutdown", "Error while reflection", e);
			return false;
		}
	}

	/**
	 * Put the device to sleep
	 *
	 * @param context
	 * @return
	 */
	public static boolean sleep(Context context)
	{
		try
		{
			DevicePolicyManager dpm = (DevicePolicyManager)context
					.getSystemService(Context.DEVICE_POLICY_SERVICE);
			dpm.lockNow();
			return true;
		}
		catch (Exception e)
		{
			Log.e(LOG_TAG + ".sleep", "Error while invoking DevicePolicyManager",
					e);
			return false;
		}
	}

	/**
	 * Return if this app is activated as device admin
	 *
	 * @param context
	 * @return
	 */
	public static boolean isDeviceAdmin(Context context)
	{
		DevicePolicyManager dpm = (DevicePolicyManager)context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName receiverCom = new ComponentName(context,
				DeviceAdminReceiver.class);
		return dpm.isAdminActive(receiverCom);
	}

	/**
	 * Start the device admin activation activity, to ask for user's permission
	 *
	 * @param context
	 */
	public static void enableDeviceAdmin(Context context)
	{
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		ComponentName receiverCom = new ComponentName(context,
				DeviceAdminReceiver.class);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, receiverCom);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				context.getString(R.string.device_admin_description));
		context.startActivity(intent);
	}

	private static final String LOG_TAG = Res.LOG_TAG + "."
			+ SystemHelper.class.getSimpleName();
}