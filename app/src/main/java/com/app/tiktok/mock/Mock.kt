package com.app.tiktok.mock

import android.content.Context
import com.app.tiktok.R
import com.app.tiktok.model.*
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

    fun loadMockGalleriesData(): ArrayList<Gallery>? {
        val mockData = context.resources.openRawResource(R.raw.galleries_data)
        val dataString = mockData.bufferedReader().readText()

        val gson = Gson()
        val galleriesType = object : TypeToken<ArrayList<Gallery>>() {}.type
        val galleriesDataModelList = gson.fromJson<ArrayList<Gallery>>(dataString, galleriesType)

        return galleriesDataModelList
    }

    fun loadMockPostsData(): ArrayList<Post>? {
        val mockData = context.resources.openRawResource(R.raw.posts_data)
        val dataString = mockData.bufferedReader().readText()

        val gson = Gson()
        val postsType = object : TypeToken<ArrayList<Post>>() {}.type
        val postsDataModelList = gson.fromJson<ArrayList<Post>>(dataString, postsType)

        return postsDataModelList
    }

    fun loadMockUsersData(): ArrayList<User>? {
        val mockData = context.resources.openRawResource(R.raw.users_data)
        val dataString = mockData.bufferedReader().readText()

        val gson = Gson()
        val usersType = object : TypeToken<ArrayList<User>>() {}.type
        val usersDataModelList = gson.fromJson<ArrayList<User>>(dataString, usersType)

        return usersDataModelList
    }


    fun loadMockProductsData(): ArrayList<Product>? {
        val mockData = context.resources.openRawResource(R.raw.products_data)
        val dataString = mockData.bufferedReader().readText()

        val gson = Gson()
        val productsType = object : TypeToken<ArrayList<Product>>() {}.type
        val productsDataModelList = gson.fromJson<ArrayList<Product>>(dataString, productsType)

        return productsDataModelList
    }

    fun loadMockEventsData(): ArrayList<Event>? {
        val mockData = context.resources.openRawResource(R.raw.events_data)
        val dataString = mockData.bufferedReader().readText()

        val gson = Gson()
        val eventsType = object : TypeToken<ArrayList<Event>>() {}.type
        val eventsDataModelList = gson.fromJson<ArrayList<Event>>(dataString, eventsType)

        return eventsDataModelList
    }
}