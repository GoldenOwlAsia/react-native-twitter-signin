package com.goldenowl.twittersignin;

public interface PermissionCallbackManager {


	public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

	public static class Factory {
		public static PermissionCallbackManager create() {
			return new PermissionCallbackManagerImpl();
		}
	}
}
