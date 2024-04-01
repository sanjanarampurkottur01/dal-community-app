package com.csci5708.dalcommunity.tests

import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.activity.CommonInterestsActivity
import com.csci5708.dalcommunity.adapter.CommonInterestsUsersAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.example.dalcommunity.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
class CommonInterestsActivityTest {
    private lateinit var activity: CommonInterestsActivity
    private lateinit var recyclerView: RecyclerView
    private val mockFirebaseAuth = mock(FirebaseAuth::class.java)
    private val mockFirebaseUser = mock(FirebaseUser::class.java)
    private val mockFirestore = mock(FirebaseFirestore::class.java)
    private val mockQuerySnapshot = mock(QuerySnapshot::class.java)

    @Before
    fun setUp() {
        // Mock FirebaseAuth and FirebaseUser
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.email).thenReturn("test@example.com")

        // Initialize activity and its components
        activity = CommonInterestsActivity()
        activity.onCreate(null)

        // Mock FireStoreSingleton
        Mockito.`when`(FireStoreSingleton.getData(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(),
            Mockito.any()
        )).thenAnswer {
            val onSuccess = it.arguments[2] as (Map<String, Any>) -> Unit
            val onFailure = it.arguments[3] as (Exception) -> Unit
            val document = mutableMapOf<String, Any>()
            document["firstInterest"] = "Programming"
            document["secondInterest"] = "Hiking"
            document["thirdInterest"] = "Arts"
            onSuccess.invoke(document)
        }

        // Mock FireStoreSingleton.getAllDocumentsOfCollection
        Mockito.`when`(FireStoreSingleton.getAllDocumentsOfCollection(
            Mockito.anyString(),
            Mockito.any(),
            Mockito.any(),
        )).thenAnswer {
            val onSuccess = it.arguments[2] as (List<Map<String, Any>>) -> Unit
            val onFailure = it.arguments[3] as (Exception) -> Unit
            val documents = mutableListOf<Map<String, Any>>()
            val document1 = mutableMapOf<String, Any>()
            document1["email"] = "user1@example.com"
            document1["name"] = "User1"
            document1["firstInterest"] = "Programming"
            val document2 = mutableMapOf<String, Any>()
            document2["email"] = "user2@example.com"
            document2["name"] = "User2"
            document2["secondInterest"] = "Hiking"
            val document3 = mutableMapOf<String, Any>()
            document3["email"] = "user3@example.com"
            document3["name"] = "User3"
            document3["thirdInterest"] = "Arts"
            documents.add(document1)
            documents.add(document2)
            documents.add(document3)
            onSuccess.invoke(documents)
        }

        recyclerView = activity.findViewById(R.id.recyclerView)
    }

    /**
     * Test fetching current user's interests.
     */
    @Test
    fun testFetchCurrentUserInterests() {
        // Verify that current user's interests are fetched successfully
        activity.fetchCurrentUserInterests()
        val currentUserInterests = mutableListOf<String>("Programming", "Hiking", "Arts")
        val activityCurrentUserInterests = mutableListOf<String>("Hiking")
        Mockito.verify(FireStoreSingleton::getData).invoke(
            "users",
            "test@example.com",
            Mockito.any(),
            Mockito.any()
        )
        assertTrue(activityCurrentUserInterests.containsAll(currentUserInterests))
    }

    /**
     * Test fetching users with similar interests.
     */
    @Test
    fun testFetchAllUsersWithSimilarInterests() {
        // Verify that users with similar interests are fetched successfully
        val activityCurrentUserInterests = mutableListOf<String>("Hiking")
        activity.fetchAllUsersWithSimilarInterests(activityCurrentUserInterests)
        Mockito.verify(FireStoreSingleton::getAllDocumentsOfCollection).invoke(
            "users",
            Mockito.any(),
            Mockito.any(),
        )
        val adapter = recyclerView.adapter as CommonInterestsUsersAdapter
        assertTrue(adapter.itemCount == 3)
    }
}
