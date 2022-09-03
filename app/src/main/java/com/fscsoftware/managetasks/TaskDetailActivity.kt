package com.fscsoftware.managetasks

import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fscsoftware.managetasks.controller.TaskController
import com.fscsoftware.managetasks.databinding.ActivityTaskDetailBinding
import com.fscsoftware.managetasks.databinding.DialogAlertAddTaskBinding
import com.fscsoftware.managetasks.databinding.DialogAlertDeleteMoveTaskBinding
import com.fscsoftware.managetasks.model.Task
import com.fscsoftware.managetasks.model.TaskFields
import com.fscsoftware.managetasks.utils.DateManage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*


class TaskDetailActivity : AppCompatActivity() {

    companion object {
        val INTENT_TASK_DATA = "${TaskDetailActivity::class.simpleName}:Task"
    }

    private val TAG = TaskDetailActivity::class.simpleName

    private lateinit var binding: ActivityTaskDetailBinding

    private var priorities = listOf<String>()
    private var arrayStatus = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, INTENT_TASK_DATA)

        val taskJson = intent.getStringExtra(INTENT_TASK_DATA)

        if (taskJson != null) {
            arrayStatus = listOf(getString(R.string.active), getString(R.string.finalized))
            val task : TaskFields = Json.decodeFromString<Task>(taskJson)
            val isFinish = task.dateFinish != null

            binding.etTitle.setText(task.title)
            binding.etDetails.setText(task.detail)

            //Enable or disable editText if the task has a dateFinish or it not
            binding.etTitle.isEnabled = !isFinish
            binding.etDetails.isEnabled = !isFinish
            binding.spinnerStatus.isEnabled = !isFinish
            binding.spinnerDetailPriority.isEnabled = !isFinish

            createListStatus( isFinish )
            createListPriority(task )

            binding.tvCreated.text = DateManage.longToStringDate(task.dateCreated, DateManage.FORMAT_FULL)

            binding.btnBack.setCallbackOnTouch { onBackPressed() }
            //binding.btnCopy.setCallbackOnTouch { onClickBtnCopy() }

            //Show different text, events, or views if the task has a dateFinish or it not
            if (isFinish){
                binding.textSaveOrDelete.text = getString(R.string.delete)
                binding.tvLabelFinished.visibility = LinearLayout.VISIBLE
                binding.tvFinished.text = DateManage.longToStringDate(task.dateFinish, DateManage.FORMAT_FULL)

                binding.btnSaveOrDelete.setCallbackOnTouch { onClickBtnDelete( task ) }

            }else{
                binding.textSaveOrDelete.text = getString(R.string.save)
                binding.btnSaveOrDelete.setCallbackOnTouch { onClickBtnSave( task) }
            }

        }
    }


    //Create List selectable to finish or not finish one specific task in detail view
    //If one task is selectable as finished, it task is delete from task to task_finish table
    //This below function works with hard-data due to try to works regardless of the status positions
    private fun createListStatus(isFinish: Boolean) {

        if (arrayStatus.isEmpty()) return

        createSpinnerAdapter(arrayStatus, binding.spinnerStatus)

        val indexFinish = if (arrayStatus[0].compareTo(getString(R.string.finalized)) == 0) 0 else 1
        val indexActive = if (arrayStatus[0].compareTo(getString(R.string.active)) == 0) 0 else 1
        if (isFinish) {
            binding.spinnerStatus.setSelection(indexFinish)
        }else{
            binding.spinnerStatus.setSelection(indexActive)
        }
    }

    private fun createListPriority(task: TaskFields ) {
        lifecycleScope.launch (Dispatchers.Default) {
            val prioritiesObject =
                TaskController.getRoomPrioritiesDao(binding.root.context).getAll()

            priorities = prioritiesObject.map { p -> p.name }

            withContext(Dispatchers.Main){

                createSpinnerAdapter(priorities, binding.spinnerDetailPriority)

                var index = 0;
                for (i in 0 until priorities.size){
                    if (priorities[i].compareTo(task.priority) == 0){
                        index = i;
                        break;
                    }
                }

                binding.spinnerDetailPriority.setSelection(index)
            }
        }

    }

    //Create an adapter to manage some Spinner View, it works assign an array to the spinner and
    //and then is assigned a default theme with setDropDownViewResource
    private fun createSpinnerAdapter(array: List<String>, spinner : Spinner) : ArrayAdapter<String> {
        return ArrayAdapter (binding.root.context
            , R.layout.spinner_item
            , array
        ).also {
            it.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }
    }

    private fun onClickBtnCopy ( ) {


    }

    private fun onClickBtnDelete(task: TaskFields) {

        val bindingDialogAlert = DialogAlertDeleteMoveTaskBinding.inflate(
            LayoutInflater.from(binding.root.context)
            , binding.root
            , false)

        bindingDialogAlert.tvMessage.text = getString(R.string.alert_delete)

        val alert = AlertDialog.Builder(ContextThemeWrapper (binding.root.context, R.style.AlertDialogTheme))
        .setTitle( R.string.delete )
        .setNegativeButton(R.string.cancel , null)
        .setPositiveButton(R.string.ok) { _, _ -> taskDelete(task) }
        .setView(bindingDialogAlert.root)

        val alertDialog = alert.create();
        alertDialog.show()

    }

    private fun taskDelete(task: TaskFields) {
        lifecycleScope.launch (Dispatchers.Default) {
            val db = TaskController.getRoomDb(binding.root.context)
            val taskFinishDao = db.taskFinishDao()

            taskFinishDao.deleteTransaction(task.toTaskFinish(), task.position ?: 0)

            db.close()
            withContext(Dispatchers.Main){
                onBackPressed()
            }
        }
    }

    private fun onClickBtnSave ( task: TaskFields ) {

        val finalStatus = binding.spinnerStatus.selectedItem.toString()
        val finalPriority = binding.spinnerDetailPriority.selectedItem.toString()
        val finalTitle = binding.etTitle.text.toString()
        val finalDetail = binding.etDetails.text.toString()

        val isEmptyName = finalTitle.isEmpty() && finalTitle.compareTo("") == 0

        if (isEmptyName) {
            binding.tilTitle.error = getString(R.string.name_empty)
            return
        }

        lifecycleScope.launch (Dispatchers.Default) {

            val db = TaskController.getRoomDb(binding.root.context)
            val taskDao = db.taskDao()

            val updatePriority = task.priority.compareTo(finalPriority) != 0
            val updateStatus = finalStatus.compareTo(getString(R.string.finalized)) == 0

            task.title = finalTitle
            task.detail = finalDetail
            val beforePriority = task.priority
            task.priority = finalPriority

            if (updateStatus){
                val taskFinishDao = db.taskFinishDao()
                val lastPosition = task.position ?: 0
                task.position = 0
                task.dateFinish = Date().time

                taskFinishDao.insertTransaction(task.toTaskFinish() )
                //Call transaction to delete and update position of another tasks
                taskDao.deleteTransaction(task.toTask() , task.priority, lastPosition)
            }else{
                taskDao.updateTask(task.toTask())
                //If position was update move to another priority and set it task in position 0
                if (updatePriority) {
                    taskDao.deleteUpdate(beforePriority, task.position ?: 0)
                    taskDao.updatePositionTransaction(finalPriority, task.id!!, 0, task.position ?: 0)
                }
            }

            db.close()
            withContext(Dispatchers.Main){
                onBackPressed()
            }
        }

    }

}