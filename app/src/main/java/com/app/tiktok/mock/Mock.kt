package com.app.tiktok.mock

import android.content.Context
import com.app.tiktok.R
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.ui.user.UserDataModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


class Mock @Inject constructor(private val context: Context) {
    fun loadMockData(): ArrayList<StoriesDataModel>? {
        val mockData = context.resources.openRawResource(R.raw.stories_data)
        val dataString = mockData.bufferedReader().readText()

        val gson = Gson()
        val storiesType = object : TypeToken<ArrayList<StoriesDataModel>>() {}.type
        val storiesDataModelList = gson.fromJson<ArrayList<StoriesDataModel>>(dataString, storiesType)

        return storiesDataModelList
    }

    fun loadMockUsersData(): ArrayList<UserDataModel>? {
        val mockData = context.resources.openRawResource(R.raw.users_data)
        val dataString = mockData.bufferedReader().readText()

        val gson = Gson()
        val usersType = object : TypeToken<ArrayList<UserDataModel>>() {}.type
        val usersDataModelList = gson.fromJson<ArrayList<UserDataModel>>(dataString, usersType)

        return usersDataModelList
    }
}