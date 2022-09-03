package com.fscsoftware.managetasks.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Query
import com.fscsoftware.managetasks.R
import com.fscsoftware.managetasks.databinding.PriorityItemBinding
import com.fscsoftware.managetasks.model.Priority
import java.util.*


open class PrioritiesAdapter(
    private var priorities: MutableList<Priority>,
    private val clickListener: (Priority, Int) -> Unit
)
: RecyclerView.Adapter<PrioritiesAdapter.ViewHolder> () {

    companion object {
        //R.string.edit or R.string.delete
        var action = R.string.edit //EDIT OR DELETE
    }

    class ViewHolder (private val binding : PriorityItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind (priority : Priority, clickListener : (Priority, Int) -> Unit){
            binding.tvName.text = priority.name

            //Set iccon of current acion
            if (action == R.string.delete ) {
                val drawableDelete = AppCompatResources.getDrawable(binding.root.context, R.drawable.ic_delete_forever)
                binding.ivPriorityItem.setImageDrawable(drawableDelete)
            }else{
                val drawableEdit = AppCompatResources.getDrawable(binding.root.context, R.drawable.ic_edit)
                binding.ivPriorityItem.setImageDrawable(drawableEdit)
            }

            binding.ivPriorityItem.setOnClickListener { clickListener(priority, action) }
        }

        fun getBinding () = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PriorityItemBinding.inflate(
            LayoutInflater.from( parent.context ),
            parent,
            false)

        return ViewHolder (binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPriority = priorities[position]
        holder.bind(currentPriority, clickListener)
    }


    override fun getItemCount() = priorities.size

    fun getIList ( ) = priorities

    fun deleteTask(priority: Priority) {
        priorities.remove(priority)
    }

    fun insert(priority: Priority) {
        //Update position id plus 1
        for (i in 0 until priorities.size){
            priorities[i].position = priorities[i].position?.plus(1)
        }

        priorities.add(0, priority)
    }

    fun move( ){
        for (index in priorities.indices ) {
            priorities[index].position = index
        }
    }

    fun setList(newList: List<Priority>) {
        priorities.clear()
        priorities.addAll(newList)
    }

}