package com.example.tasktodoapplication.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.tasktodoapplication.room.Task
import com.example.tasktodoapplication.room.TaskDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tasktodoapplication.room.Constant
import kotlinx.coroutines.withContext
import android.widget.ImageView
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.DatePicker
import android.widget.TimePicker
import com.example.tasktodoapplication.R
import java.util.Calendar

class TaskActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editTask: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonUpdate: Button
    private lateinit var editDate: EditText
    private lateinit var editTime: EditText
    private lateinit var iconDatePicker: ImageView
    private lateinit var iconTimePicker: ImageView

    private val db by lazy { TaskDB.getDatabase(this) }
    private var taskId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        editTitle = findViewById(R.id.edit_title)
        editTask = findViewById(R.id.edit_task)
        buttonSave = findViewById(R.id.button_save)
        buttonUpdate = findViewById(R.id.button_update)
        editDate = findViewById(R.id.edit_date)
        editTime = findViewById(R.id.edit_time)
        iconDatePicker = findViewById(R.id.icon_date_picker)
        iconTimePicker = findViewById(R.id.icon_time_picker)

        setupListener()
        setupView()
        setupTextWatchers()
        toggleSaveButton() // Ensure the button is set correctly on startup
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val intentType = intent.getIntExtra("intent_type", 0)
        when (intentType) {
            Constant.TYPE_CREATE -> {
                buttonUpdate.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                buttonSave.visibility = View.GONE
                buttonUpdate.visibility = View.GONE
                getTask()
                disableEditing()
            }
            Constant.TYPE_UDPATE -> {
                buttonSave.visibility = View.GONE
                getTask()
            }
        }
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                toggleSaveButton()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        editTitle.addTextChangedListener(textWatcher)
        editTask.addTextChangedListener(textWatcher)
        editDate.addTextChangedListener(textWatcher)
        editTime.addTextChangedListener(textWatcher)
    }

    private fun toggleSaveButton() {
        val title = editTitle.text.toString().trim()
        val content = editTask.text.toString().trim()
        val date = editDate.text.toString().trim()
        val time = editTime.text.toString().trim()

        // Enable the button only if all fields are filled
        val isAllFieldsFilled = title.isNotEmpty() && content.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()
        buttonSave.isEnabled = isAllFieldsFilled
    }

    private fun setupListener() {
        buttonSave.setOnClickListener {
            saveTask()
        }
        buttonUpdate.setOnClickListener {
            updateTask()
        }
        iconDatePicker.setOnClickListener {
            showDatePicker()
        }
        editDate.setOnClickListener {
            showDatePicker()
        }
        iconTimePicker.setOnClickListener {
            showTimePicker()
        }
        editTime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun saveTask() {
        CoroutineScope(Dispatchers.IO).launch {
            db.taskDao().addTask(
                Task(
                    0,
                    editTitle.text.toString(),
                    editTask.text.toString(),
                    editDate.text.toString(),
                    editTime.text.toString()
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TaskActivity, "Task created successfully", Toast.LENGTH_SHORT).show()
                finish()
                Log.d("TaskActivity", "Task added")
            }
        }
    }

    private fun updateTask() {
        CoroutineScope(Dispatchers.IO).launch {
            db.taskDao().updateTask(
                Task(
                    taskId,
                    editTitle.text.toString(),
                    editTask.text.toString(),
                    editDate.text.toString(),
                    editTime.text.toString()
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TaskActivity, "Task updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun getTask() {
        taskId = intent.getIntExtra("intent_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = db.taskDao().getTask(taskId)
            if (tasks.isNotEmpty()) {
                val task = tasks[0]
                withContext(Dispatchers.Main) {
                    editTitle.setText(task.title)
                    editTask.setText(task.content)
                    editDate.setText(task.date)
                    editTime.setText(task.time)
                }
            }
        }
    }

    private fun disableEditing() {
        editTitle.isEnabled = false
        editTask.isEnabled = false
        editDate.isEnabled = false
        editTime.isEnabled = false
        iconTimePicker.isEnabled = false
        iconDatePicker.isEnabled = false
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear, selectedMonth, selectedDay ->
            val date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            editDate.setText(date)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _: TimePicker, selectedHour, selectedMinute ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            editTime.setText(time)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}

