package com.app.tiktok.repository

import com.app.tiktok.mock.Mock
import com.app.tiktok.model.*
import javax.inject.Inject

class DataRepository @Inject constructor(private val mock: Mock) {
    fun getStoriesData(): ArrayList<StoriesDataModel>? {
        val dataList = mock.loadMockData()
        return dataList
    }

    fun getGalleriesData(): ArrayList<Gallery>? {
        val dataList = mock.loadMockGalleriesData()
        return dataList
    }

    fun getPostsData(): ArrayList<Post>? {
        val dataList = mock.loadMockPostsData()
        return dataList
    }

    fun getUsersData(): ArrayList<User>? {
        val dataList = mock.loadMockUsersData()
        return dataList
    }

    fun getProductsData(): ArrayList<Product>? {
        val dataList = mock.loadMockProductsData()
        return dataList
    }

}