package com.fscsoftware.managetasks.helpers

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fscsoftware.managetasks.adapter.PrioritiesAdapter
import com.fscsoftware.managetasks.controller.TaskController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SwipeHelperPriority (private val prioritiesAdapter: PrioritiesAdapter ) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
    , 0 ) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        val fromPos = viewHolder.adapterPosition
        val toPos = target.adapterPosition

        if (fromPos < toPos) {
            for (i in fromPos until toPos) {
                Collections.swap(prioritiesAdapter.getIList(), i, i + 1)
            }
        } else {
            for (i in fromPos downTo toPos + 1) {
                Collections.swap(prioritiesAdapter.getIList(), i, i - 1)
            }
        }

        recyclerView.adapter?.notifyItemMoved(fromPos, toPos)

        return true
    }

    override fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        fromPos: Int,
        target: RecyclerView.ViewHolder,
        toPos: Int,
        x: Int,
        y: Int
    ) {

        prioritiesAdapter.move()

        //Update all table with the new list */
        TaskController.getViewModelScope().launch (Dispatchers.Default) {
            val db = TaskController.getRoomDb(recyclerView.context)
            val priorityDao = db.priorityDao()

            priorityDao.update(prioritiesAdapter.getIList())
            db.close()
        }


        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
    }



}