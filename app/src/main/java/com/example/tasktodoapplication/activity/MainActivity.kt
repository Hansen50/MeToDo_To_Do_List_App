package com.example.tasktodoapplication.activity

import com.example.tasktodoapplication.adapter.TaskAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktodoapplication.R
import com.example.tasktodoapplication.room.Constant
import com.example.tasktodoapplication.room.Task
import com.example.tasktodoapplication.room.TaskDB
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val db by lazy { TaskDB.getDatabase(this) }
    //value nilai database untuk diinisialisasi pada saat deklarasi
    private lateinit var taskAdapter: TaskAdapter
    //late ini menginisialisasi taskAdapter sebelum mengaksesnya,
    // sehingga Anda tidak perlu melakukan pengecekan null:
    //memastikan bahwa taskAdapter diinisialisasi setelah semua elemen UI tersedia dan siap digunakan.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupListener()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = db.taskDao().getTasks()
            withContext(Dispatchers.Main) {
                taskAdapter.setData(tasks)
                taskAdapter.notifyDataSetChanged()
                // Di sini untuk mengatur visibilitas dari TextView
                val noDataText = findViewById<TextView>(R.id.no_data_text)
                val listTask = findViewById<RecyclerView>(R.id.list_task)
                if (tasks.isEmpty()) {
                    noDataText.visibility = View.VISIBLE
                    listTask.visibility = View.GONE
                } else {
                    noDataText.visibility = View.GONE
                    listTask.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun setupListener() {
        val iconAdd = findViewById<FloatingActionButton>(R.id.icon_add)
        iconAdd.setOnClickListener {
            intentEdit(Constant.TYPE_CREATE, 0)
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            arrayListOf(),
            object : TaskAdapter.OnAdapterListener {
                override fun onClick(task: Task) {
                    intentEdit(Constant.TYPE_READ, task.id)
                }
//objek messaging yang digunakan untuk melakukan permintaan sebuah aks
                override fun onUpdate(task: Task) {
                    intentEdit(Constant.TYPE_UDPATE, task.id)
                }

                override fun onDelete(task: Task) {
                    deleteAlert(task)
                }

                override fun onMarkDone(task: Task) {
                    // Berikut alert dialog ketika user mengklik mark as done
                    val dialog = AlertDialog.Builder(this@MainActivity)
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
                                    loadData()
                                    dialogInterface.dismiss()
                                }
                            }
                        }
                    }
                    dialog.show()
                }
            })

        val listTask = findViewById<RecyclerView>(R.id.list_task)
        listTask.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }

    private fun intentEdit(intentType: Int, taskId: Int) {
        startActivity(
            Intent(this, TaskActivity::class.java)
                .putExtra("intent_type", intentType)
                .putExtra("intent_id", taskId)
        )
    }

    private fun deleteAlert(task: Task) {
        // Berikut alert dialog ketika user mengklik delete pada suatu data task
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            setTitle("Delete Confirmation")
            setMessage("Are you sure to delete ${task.title}?")
            setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Delete") { dialogInterface, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.taskDao().deleteTask(task)
                    withContext(Dispatchers.Main) {
                        dialogInterface.dismiss()
                        loadData()
                    }
                }
            }
        }
        dialog.show()
    }
}


