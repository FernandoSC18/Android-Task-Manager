package com.fscsoftware.managetasks.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.fscsoftware.managetasks.databinding.TaskItemBinding
import com.fscsoftware.managetasks.model.TaskFields

open class TasksAdapter(
    private val tasks: MutableList<TaskFields>,
    private val empty: LinearLayout,
    private val clickListener: (TaskFields) -> Unit
)
: RecyclerView.Adapter<TasksAdapter.ViewHolder> () {

    class ViewHolder (private val binding : TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (task : TaskFields){
            binding.tvTitle.text = task.title
            binding.tvDetails.text = task.detail
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TaskItemBinding.inflate(
            LayoutInflater.from( parent.context ),
            parent,
            false)

        return ViewHolder (binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.bind(currentTask)

        holder.itemView.setOnClickListener { clickListener(currentTask) }
    }


    //Control visibility of textview to check if show one message or not
    fun notifyArrayDataChanged (){

        empty.visibility = if (tasks.isEmpty()) LinearLayout.VISIBLE else LinearLayout.GONE

    }

    override fun getItemCount() = tasks.size

    fun getIList ( ) = tasks

    fun setTasks (newTasks : List<TaskFields>) {
        tasks.clear()
        tasks.addAll(newTasks)

        notifyArrayDataChanged()
    }

    fun addTask (newTask : TaskFields) {

        //Update position id plus 1
        for (i in 0 until tasks.size){
            tasks[i].position = tasks[i].position?.plus(1)
        }

        //Add the new task at first position
        tasks.add(0, newTask)

        notifyArrayDataChanged()
    }

    fun deleteTask (index : Int) {

        tasks.removeAt(index)

        notifyArrayDataChanged()
    }



}