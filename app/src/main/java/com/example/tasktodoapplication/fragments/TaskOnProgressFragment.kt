package com.example.tasktodoapplication.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktodoapplication.R
import com.example.tasktodoapplication.activity.MainActivity
import com.example.tasktodoapplication.activity.TaskActivity
import com.example.tasktodoapplication.adapters.TaskAdapter
import com.example.tasktodoapplication.room.Constant
import com.example.tasktodoapplication.room.Task
import com.example.tasktodoapplication.room.TaskDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskFragment : Fragment() {

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

    override fun onResume() {
        super.onResume()
        loadData()
    }


    private fun setupRecyclerView(view: View) {
        taskAdapter = TaskAdapter(arrayListOf(), object : TaskAdapter.OnAdapterListener {
            override fun onClick(task: Task) {
                intentEdit(Constant.TYPE_READ, task.id)
            }

            override fun onUpdate(task: Task) {
                intentEdit(Constant.TYPE_UDPATE, task.id)

            }

            override fun onDelete(task: Task) {
                deleteAlert(task)
            }

            override fun onMarkDone(task: Task) {
                // Handle mark done
                val dialog = AlertDialog.Builder(this@TaskFragment.requireContext())
                dialog.apply {
                    setTitle("Mark as Done")
                    setMessage("Are you sure you want to mark '${task.title}' as done?")
                    setNegativeButton("Cancel") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    setPositiveButton("Mark Done") { dialogInterface, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            task.isDone = true
                            db.taskDao().updateTask(task)
                            withContext(Dispatchers.Main) {
                                if (isAdded) {

                                    (activity as? MainActivity)?.refreshFragments()
                                    //loadData() // Refresh the data in the fragment
                                    dialogInterface.dismiss()
                                }
                            }
                        }
                    }
                }
                dialog.show()
            }
        })

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun deleteAlert(task: Task) {
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

    private fun intentEdit(intentType: Int, taskId: Int) {
        val intent = Intent(requireContext(), TaskActivity::class.java)
        intent.putExtra("intent_type", intentType)
        intent.putExtra("intent_id", taskId)
        startActivity(intent)
    }

    // jangan private
    fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = db.taskDao().getTasks()
            withContext(Dispatchers.Main) {
                if (isAdded) {
                    taskAdapter.setData(tasks)
                    taskAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}