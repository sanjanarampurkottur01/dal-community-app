package com.csci5708.dalcommunity
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.csci5708.dalcommunity.adapter.BroadcastAnswersAdapter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.example.dalcommunity.R

@RunWith(AndroidJUnit4::class)
class BroadcastAnswersAdapterTest {

    private lateinit var context: Context
    private lateinit var adapter: BroadcastAnswersAdapter
    private lateinit var questions: List<String>

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        questions = listOf("Answer 1", "Answer 2", "Answer 3")
        adapter = BroadcastAnswersAdapter(context, questions)
    }

    @Test
    fun testItemCount() {
        assertEquals(questions.size, adapter.itemCount)
    }

    @Test
    fun testViewHolder() {
        val parent = RecyclerView(context)
        val viewHolder = adapter.onCreateViewHolder(parent, 0)
        assert(viewHolder is BroadcastAnswersAdapter.ViewHolder)
    }

    @Test
    fun testViewHolderViewType() {
        val parent = RecyclerView(context)
        val viewType = adapter.getItemViewType(0)
        assertEquals(0, viewType)
    }

    @Test
    fun testViewHolderBindView() {
        val parent = RecyclerView(context)
        val viewHolder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(viewHolder, 0)
        val textView = viewHolder.itemView.findViewById<TextView>(R.id.answerText)
        assertEquals("Answer 1", textView.text)
    }

}