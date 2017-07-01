package com.goldenowl.twittersignin;

import com.google.gson.annotations.SerializedName;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

class MyTwitterApiClient extends TwitterApiClient {
	public MyTwitterApiClient(TwitterSession session) {
		super(session);
	}
	public FriendsService getFriendsService() {
		return getService(FriendsService.class);
	}
}

interface FriendsService {
	@GET("/1.1/friends/list.json")
	Call<Friends> getFriends(@Query("user_id") long id, @Query("count") int count);
}

class Friends {
	@SerializedName("users")
	public final List<User> users;

	public Friends(List<User> users) {
		this.users = users;
	}
}