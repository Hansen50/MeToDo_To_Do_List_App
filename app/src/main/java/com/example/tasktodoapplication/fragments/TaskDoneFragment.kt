package com.example.tasktodoapplication.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktodoapplication.R
import com.example.tasktodoapplication.adapters.TaskAdapter
import com.example.tasktodoapplication.room.Task
import com.example.tasktodoapplication.room.TaskDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DoneTaskFragment : Fragment() {

    private val db by lazy { TaskDB.getDatabase(requireContext()) }
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_on_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        loadData()
    }

    private fun setupRecyclerView(view: View) {
        taskAdapter = TaskAdapter(arrayListOf(), object : TaskAdapter.OnAdapterListener {
            override fun onClick(task: Task) {
                // Handle item click
            }

            override fun onUpdate(task: Task) {
                // Handle item update
            }

            override fun onDelete(task: Task) {
                deleteAlert(task)
            }

            override fun onMarkDone(task: Task) {
                // Optionally handle mark done in the completed tasks fragment
            }
        })

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = db.taskDao().getCompletedTasks() // Ensure this method is defined in your DAO
            withContext(Dispatchers.Main) {
                if (isAdded) {
                    taskAdapter.setData(tasks)
                    taskAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun deleteAlert(task: Task) {
        // Implement the delete confirmation dialog similarly to TaskFragment
        val dialog = AlertDialog.Builder(requireContext())
        dialog.apply {
            setTitle("Delete Confirmation")
            setMessage("Are you sure you want to delete '${task.title}'?")
            setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Delete") { dialogInterface, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.taskDao().deleteTask(task) // Make sure your DAO has this method
                    withContext(Dispatchers.Main) {
                        loadData() // Refresh the data in the fragment
                        dialogInterface.dismiss()
                    }
                }
            }
        }
        dialog.show()
    }
}