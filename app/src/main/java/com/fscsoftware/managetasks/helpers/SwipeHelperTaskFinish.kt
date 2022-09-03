package com.fscsoftware.managetasks.helpers

import android.graphics.*
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fscsoftware.managetasks.R
import com.fscsoftware.managetasks.adapter.TasksAdapter
import com.fscsoftware.managetasks.controller.TaskController
import com.fscsoftware.managetasks.databinding.DialogAlertDeleteMoveTaskBinding
import com.fscsoftware.managetasks.model.TaskFields
import com.fscsoftware.managetasks.utils.Converter
import com.fscsoftware.managetasks.utils.SharedPreferencesManage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

//swipeType is two types for delete or change task based on activity
class SwipeHelperTaskFinish (private val tasksAdapter: TasksAdapter ) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP.or(ItemTouchHelper.DOWN)
    , ItemTouchHelper.LEFT ) {

    lateinit var recyclerView : RecyclerView

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val iconDrawable = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete_forever)
        val paint = Paint()
        paint.color = Color.RED

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView: View = viewHolder.itemView

            //RIGHT_DIRECTION
            if (dX > 0) {
                Log.e("onChildDraw : ", "RIGHT_DIRECTION")
            //LEFT_DIRECTION
            } else if (dX < 0) {
                val background = RectF( itemView.right + dX
                    , itemView.top + 0F
                    , itemView.right + 0F
                    , itemView.bottom + 0F)

                c.drawRect(background, paint)

                val outColor = TypedValue()
                recyclerView.context.theme.resolveAttribute (androidx.appcompat.R.attr.background, outColor, true)
                iconDrawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(outColor.data, BlendModeCompat.SRC_ATOP)
                val bitmap = Converter.drawableToBitmap(iconDrawable)

                if (bitmap != null){
                    c.drawBitmap(bitmap
                        , (itemView.right - (iconDrawable!!.intrinsicWidth / 2)) + ( dX / 4 )
                        , itemView.top + ((itemView.bottom - itemView.top) / 2) - (iconDrawable!!.intrinsicHeight / 2) + 0F
                        , null)
                }
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX / 2, dY, actionState, isCurrentlyActive);
        this.recyclerView = recyclerView
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPos = viewHolder.adapterPosition
        val toPos = target.adapterPosition

        taskPositionMove(toPos, fromPos)

        recyclerView.adapter?.notifyItemMoved(toPos, fromPos)

        return true
    }

    private fun taskPositionMove(toPos: Int, fromPos: Int) {

        TaskController.getViewModelScope().launch (Dispatchers.Default) {
            val taskFrom = tasksAdapter.getIList()[toPos]

            if (taskFrom.id != null ){
                val db = TaskController.getRoomDb(recyclerView.context)
                val taskDao = db.taskDao()

                //Call transaction to update position of another tasks
                taskDao.updatePositionTransaction(taskFrom.priority, taskFrom.id!!, toPos, fromPos)
                db.close()
            }
        }

        Collections.swap(tasksAdapter.getIList(), toPos, fromPos)
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        val position = viewHolder.adapterPosition

        when (direction) {
            ItemTouchHelper.LEFT -> {

                //Go to shared preference and get settings if show or not a dialog alert or not before to delete
                if (SharedPreferencesManage.getSwipeDelete(recyclerView.context)) {

                    val bindingDialogAlert = DialogAlertDeleteMoveTaskBinding.inflate(
                        LayoutInflater.from(recyclerView.context), null, false
                    )

                    bindingDialogAlert.tvMessage.text =
                        recyclerView.context.getString(R.string.alert_delete)

                    val alert = AlertDialog.Builder( ContextThemeWrapper( recyclerView.context, R.style.AlertDialogTheme ) )
                        .setTitle(R.string.delete)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok) { _, _ -> taskDelete(position) }
                        .setView(bindingDialogAlert.root)

                    val alertDialog = alert.create();
                    alertDialog.show()
                }else{
                    taskDelete(position)
                }

            }
        }

    }


    private fun taskDelete ( position: Int) {

        TaskController.getViewModelScope().launch (Dispatchers.Default) {
            val db = TaskController.getRoomDb(recyclerView.context)

            val deleteItem = tasksAdapter.getIList()[position]
            val taskFinishDao = db.taskFinishDao()
            taskFinishDao.deleteTransaction(deleteItem.toTaskFinish(), position)
            db.close()

            withContext(Dispatchers.Main){
                tasksAdapter.deleteTask(position)
                recyclerView.adapter?.notifyItemRemoved(position)
            }

        }


    }



}