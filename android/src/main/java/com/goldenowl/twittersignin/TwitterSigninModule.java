package com.goldenowl.twittersignin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;


public class TwitterSigninModule extends ReactContextBaseJavaModule implements ActivityEventListener {

	public static String TAG = TwitterSigninModule.class.getName();

	// Storage Permissions
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	private final int RESULT_CANCELED = 0;
    TwitterAuthClient twitterAuthClient;

	public static Callback shareCallback = null;
	private ReadableMap shareContent;

    public TwitterSigninModule(ReactApplicationContext reactContext, PermissionCallbackManager permissionCallbackManager) {
        super(reactContext);

        reactContext.addActivityEventListener(this);
		((PermissionCallbackManagerImpl)permissionCallbackManager).registerCallback(REQUEST_EXTERNAL_STORAGE, new PermissionCallbackManagerImpl.Callback() {
			@Override
			public boolean onRequestPermissionsResult(String[] permissions, int[] grantResults) {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission was granted, yay! Do the
					// contacts-related task you need to do.
					downloadImageAndPostTweet();

				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					composeTweet(null);
				}
				return true;
			}
		});
    }

    @Override
    public String getName() {
        return "TwitterSignin";
    }

    @ReactMethod
    public void logIn(String consumerKey, String consumerSecret,  final Callback callback) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(consumerKey, consumerSecret);
        Fabric.with(getReactApplicationContext(), new Twitter(authConfig));
        twitterAuthClient = new TwitterAuthClient();

        Twitter.logIn(getCurrentActivity(), new com.twitter.sdk.android.core.Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                final TwitterSession session = result.data;
                TwitterAuthToken twitterAuthToken = session.getAuthToken();
                final WritableMap map = Arguments.createMap();
                map.putString("authToken", twitterAuthToken.token);
                map.putString("authTokenSecret", twitterAuthToken.secret);
                map.putString("name", session.getUserName());
                map.putString("userID", Long.toString(session.getUserId()));
                map.putString("userName", session.getUserName());
                callback.invoke(null, map);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("failure", exception.toString());
                callback.invoke(exception.toString(), null);
            }
        });
    }

	@ReactMethod
	public void getFriendsListWithCallback(final Callback callback) {
		TwitterSession session = Twitter.getSessionManager().getActiveSession();
		MyTwitterApiClient twitterApiClient = new MyTwitterApiClient(session);
		FriendsService friendsService = twitterApiClient.getFriendsService();
		Call<Friends> call = friendsService.getFriends(session.getUserId(), 200);
		call.enqueue(new com.twitter.sdk.android.core.Callback<Friends>() {
			@Override
			public void success(Result<Friends> result) {
				callback.invoke(null, usersArrayToWritableArray(result.data.users));
			}

			@Override
			public void failure(TwitterException exception) {
				callback.invoke(formatErrorMessage(exception), null);
			}
		});
	}

	@ReactMethod
	public void showTweetComposerWithSharingContent(ReadableMap shareContent, final Callback shareCallback) {
		TwitterSigninModule.shareCallback = shareCallback;
		this.shareContent = shareContent;

		if(verifyStoragePermissions(getCurrentActivity())) {
			downloadImageAndPostTweet();
		}
	}

	private void downloadImageAndPostTweet() {
		DisplayImageOptions opts = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getReactApplicationContext())
				.diskCacheFileNameGenerator(new FileNameGenerator() {
					@Override
					public String generate(String imageUri) {
						return String.valueOf(imageUri.hashCode()) + ".jpg";
					}
				})
				.defaultDisplayImageOptions(opts).build();


		ImageLoader imageLoader = ImageLoader.getInstance();
		if (!imageLoader.isInited())
			ImageLoader.getInstance().init(config);

		ImageLoader.getInstance().loadImage(shareContent.getString("imageURL"), new ImageLoadingListener() {
			ProgressDialog progress;

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progress = new ProgressDialog(getCurrentActivity());
				progress.setMessage("Loading...");
				progress.show();
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				if (progress != null && progress.isShowing()) {
					progress.dismiss();

					if(shareCallback != null) {
						shareCallback.invoke("Failed to download image", null);
						TwitterSigninModule.shareCallback = null;
						shareContent = null;
					}
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (progress != null && progress.isShowing()) {
					progress.dismiss();

					File file = ImageLoader.getInstance().getDiskCache().get(imageUri);
					String path = file.getAbsolutePath();

					Log.w(TAG, "image: " + path);

					composeTweet(Uri.parse(path));
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				if (progress != null) {
					progress.dismiss();
				}
			}
		});
	}

	private void composeTweet(@Nullable Uri image) {
		try {
			Activity act = getCurrentActivity();
			if (act != null) {
				TweetComposer.Builder builder = new TweetComposer.Builder(act)
						.text(shareContent.getString("text"))
						.url(new URL(shareContent.getString("linkURL")));
				if (image != null) {
					builder.image(image);
				}
				if (shareCallback != null) {
					shareCallback.invoke(null, "success");
					shareCallback = null;
				}
				builder.show();
			}
			else {
				if(shareCallback != null) {
					shareCallback.invoke("Tweet composing failed", null);
					TwitterSigninModule.shareCallback = null;
					shareContent = null;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			if(shareCallback != null) {
				shareCallback.invoke("Incorrect link URL", null);
				TwitterSigninModule.shareCallback = null;
				shareContent = null;
			}
		}
	}

	private Bitmap getBitmapFromUrl(String Url) {
		try {
			URL url = new URL(Url);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			return bitmap;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private String formatErrorMessage(TwitterException exception) {
		String errorMessage = exception.getMessage();

		if (errorMessage == null || errorMessage.isEmpty())
			errorMessage = "Unknown twitter error";

		return errorMessage;
	}

	public static WritableArray usersArrayToWritableArray(List<User> usersArray) {
		WritableArray writableArray = new WritableNativeArray();

		for (User user : usersArray) {
			if(user != null)
				writableArray.pushMap(userToWritableMap(user));
		}

		return writableArray;
	}

	public static WritableMap userToWritableMap(User user) {
		WritableMap writableMap = new WritableNativeMap();

		if (user == null) {
			return null;
		}

		writableMap.putString("id", String.valueOf(user.id));
		writableMap.putString("name", user.name);
		writableMap.putString("profile_image_url", user.profileImageUrl);

		return writableMap;
	}

	public static boolean verifyStoragePermissions(Activity activity) {
		// Check if we have write permission
		int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			// We don't have permission so prompt the user
			ActivityCompat.requestPermissions(
					activity,
					PERMISSIONS_STORAGE,
					REQUEST_EXTERNAL_STORAGE
			);

			return false;
		}

		return true;
	}

	@Override
	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		if(twitterAuthClient != null && twitterAuthClient.getRequestCode()==requestCode) {
			boolean twitterLoginWasCanceled = (resultCode == RESULT_CANCELED);
			twitterAuthClient.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onNewIntent(Intent intent) {

	}
}