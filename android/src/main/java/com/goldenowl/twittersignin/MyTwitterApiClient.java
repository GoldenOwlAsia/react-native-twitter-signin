package com.goldenowl.twittersignin;

import com.google.gson.annotations.SerializedName;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

class MyTwitterApiClient extends TwitterApiClient {
	public MyTwitterApiClient(TwitterSession session) {
		super(session);
	}
	public FriendsService getFriendsService() {
		return getService(FriendsService.class);
	}
	public ImagesService getImagesService() {
		return getService(ImagesService.class);
	}
}

interface FriendsService {
	@GET("/1.1/friends/list.json")
	void getFriends(@Query("user_id") long id, @Query("count") int count, Callback<Friends> cb);
}

interface ImagesService {
	@POST("1.1/media/upload.json")
	void uploadImage(@Query("media_data") String base64Image, Callback<com.twitter.sdk.android.core.models.Media> cb);
}

class Friends {
	@SerializedName("users")
	public final List<User> users;

	public Friends(List<User> users) {
		this.users = users;
	}
}