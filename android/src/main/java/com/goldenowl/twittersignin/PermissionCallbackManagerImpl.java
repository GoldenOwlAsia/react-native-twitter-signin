package com.goldenowl.twittersignin;

import java.util.HashMap;
import java.util.Map;

public final class PermissionCallbackManagerImpl implements PermissionCallbackManager {
	private static Map<Integer, Callback> staticCallbacks = new HashMap<>();

	/**
	 * If there is no explicit callback, but we still need to call the Facebook component,
	 * because it's going to update some state, e.g., login, like. Then we should register a
	 * static callback that can still handle the response.
	 * @param requestCode The request code.
	 * @param callback The callback for the feature.
	 */
	public synchronized static void registerStaticCallback(
			int requestCode,
			Callback callback) {
		if(callback == null)
			throw new NullPointerException("Container 'callback' cannot contain null values");

		if (staticCallbacks.containsKey(requestCode)) {
			return;
		}
		staticCallbacks.put(requestCode, callback);
	}

	private static synchronized Callback getStaticCallback(Integer requestCode) {
		return staticCallbacks.get(requestCode);
	}

	private static boolean runStaticCallback(int requestCode, String[] permissions, int[] grantResults) {
		Callback callback = getStaticCallback(requestCode);
		if (callback != null) {
			return callback.onRequestPermissionsResult(permissions, grantResults);
		}
		return false;
	}

	private Map<Integer, Callback> callbacks = new HashMap<>();

	public void registerCallback(int requestCode, Callback callback) {
		if(callback == null)
			throw new NullPointerException("Container 'callback' cannot contain null values");
		callbacks.put(requestCode, callback);
	}

	@Override
	public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		Callback callback = callbacks.get(requestCode);
		if (callback != null) {
			return callback.onRequestPermissionsResult(permissions, grantResults);
		}
		return runStaticCallback(requestCode, permissions, grantResults);
	}

	public interface Callback {
		public boolean onRequestPermissionsResult(String[] permissions, int[] grantResults);
	}
}
