package com.example.tasktodoapplication.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktodoapplication.room.Task
import android.widget.TextView
import android.widget.ImageView
import com.example.tasktodoapplication.R

class TaskAdapter(private val tasks: ArrayList<Task>, private val listener: OnAdapterListener)
    : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.main_adapter, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.textTitle.text = task.title
        holder.textDate.text = task.date
        holder.textTime.text = task.time

        // Set menekan text title
        holder.textTitle.setOnClickListener {
            listener.onClick(task)
        }

        // Set ketika menekan ikomm edit
        holder.iconEdit.setOnClickListener {
            listener.onUpdate(task)
        }

        // Set listener ketika menekan ikon delete
        holder.iconDelete.setOnClickListener {
            listener.onDelete(task)
        }

        // Set listener ketik menekan mark as done
        holder.buttonMarkDone.setOnClickListener {
            listener.onMarkDone(task)
        }

        // mengupdate list task ketika menekan mark as done
        if (task.isDone) {
            holder.textTitle.paintFlags = holder.textTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textDate.paintFlags = holder.textDate.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textTime.paintFlags = holder.textTime.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.buttonMarkDone.visibility = View.GONE
            holder.iconEdit.visibility = View.GONE
        } else {
            holder.textTitle.paintFlags = holder.textTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.textDate.paintFlags = holder.textDate.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.textTime.paintFlags = holder.textTime.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.buttonMarkDone.visibility = View.VISIBLE
            holder.iconEdit.visibility = View.VISIBLE
        }
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitle: TextView = view.findViewById(R.id.text_title)
        val textDate: TextView = view.findViewById(R.id.text_date)
        val textTime: TextView = view.findViewById(R.id.text_time)
        val iconEdit: ImageView = view.findViewById(R.id.icon_edit)
        val iconDelete: ImageView = view.findViewById(R.id.icon_delete)
        val buttonMarkDone: Button = view.findViewById(R.id.button_mark_done) // Added Button
    }

    fun setData(list: List<Task>) {
        tasks.clear()
        tasks.addAll(list)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onClick(task: Task)
        fun onUpdate(task: Task)
        fun onDelete(task: Task)
        fun onMarkDone(task: Task)
    }
}
