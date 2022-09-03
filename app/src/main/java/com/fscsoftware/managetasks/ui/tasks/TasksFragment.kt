package com.fscsoftware.managetasks.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.fscsoftware.managetasks.R
import com.fscsoftware.managetasks.TaskDetailActivity
import com.fscsoftware.managetasks.adapter.TasksAdapter
import com.fscsoftware.managetasks.controller.TaskController
import com.fscsoftware.managetasks.databinding.DialogAlertAddTaskBinding
import com.fscsoftware.managetasks.databinding.FragmentTasksBinding
import com.fscsoftware.managetasks.databinding.TaskSectionBinding
import com.fscsoftware.managetasks.helpers.SwipeHelperTask
import com.fscsoftware.managetasks.model.Task
import com.fscsoftware.managetasks.model.TaskFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*


class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var priorities = listOf<String>()
    private var listTasks = hashMapOf<String, TasksAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        lifecycleScope.launch (Dispatchers.Default) {
            val prioritiesObject = TaskController.getRoomPrioritiesDao(binding.root.context).getAll()
            priorities = prioritiesObject.map { p ->

                val bindingTask : TaskSectionBinding
                val tasksAdapter : TasksAdapter
                withContext(Dispatchers.Main){

                    bindingTask = inflateTaskSection()
                    bindingTask.title.text = p.name

                    tasksAdapter = initTaskAdapter( mutableListOf(), bindingTask.empty)
                    bindingTask.rvTasks.adapter = tasksAdapter

                    //DELETE IMPORTANT ITEM AND MOVE TOUCH SWIPE
                    val swipeHelperTask = SwipeHelperTask (tasksAdapter )
                    val itemTouchHelper = ItemTouchHelper(swipeHelperTask)
                    itemTouchHelper.attachToRecyclerView(bindingTask.rvTasks)
                }

                listTasks.put(p.name, tasksAdapter)
                getTasks(tasksAdapter, p.name)
                p.name
            }
        }

        binding.fabAdd.setOnClickListener{
            fabAddClickListener ( it )
        }

        return root
    }

    private fun inflateTaskSection () : TaskSectionBinding {
        return TaskSectionBinding.inflate(
            LayoutInflater.from(binding.root.context)
            , binding.taskSection
            , true
        )
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch (Dispatchers.Default) {
            if (priorities.isNotEmpty() && listTasks.isNotEmpty() ){
                for (priority in priorities) {
                    getTasks(listTasks.get(priority), priority)
                }
            }
        }

    }

    private fun getTasks(tasksAdapter: TasksAdapter?, priority: String) {
        if (tasksAdapter == null ) return
        lifecycleScope.launch (Dispatchers.Default) {
            val tasks = TaskController.getRoomTaskDao(binding.root.context).getAllByPriority(priority)
            withContext(Dispatchers.Main)  {
                tasksAdapter.setTasks(tasks)
                tasksAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initTaskAdapter (tasks: MutableList<TaskFields>, empty: LinearLayout) : TasksAdapter {
        return TasksAdapter(tasks, empty) { task ->
            val intent = Intent( binding.root.context , TaskDetailActivity::class.java)
            //intent.putExtra(TaskDetailActivity.INTENT_TASK_DATA , Json.encodeToString( task ))
            intent.putExtra(TaskDetailActivity.INTENT_TASK_DATA , Json.encodeToString( task.toTask() ) )
            startActivity(intent)
        }
    }

    private fun fabAddClickListener(view : View) {

            val bindingDialogAlert = DialogAlertAddTaskBinding.inflate(
                LayoutInflater.from(binding.root.context)
                , binding.root
                , false)

            createAlertListPriority(bindingDialogAlert)

            val alert = AlertDialog.Builder(ContextThemeWrapper (binding.root.context, R.style.AlertDialogTheme))
            .setTitle( R.string.add_new_task )
            .setCancelable(false)
            .setNegativeButton(R.string.cancel ) { _, _ -> }
            .setPositiveButton(R.string.ok, null)
            .setView(bindingDialogAlert.root)

            val alertDialog = alert.create();
            alertDialog.show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                onCLickOkAlertAdd(alertDialog, bindingDialogAlert)
            }

    }

    private fun createAlertListPriority(bindingDialogAlert: DialogAlertAddTaskBinding) {

        ArrayAdapter (bindingDialogAlert.root.context
            //, android.R.layout.simple_spinner_item
            , R.layout.spinner_item
            , priorities ).also {
            it.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item)
            bindingDialogAlert.spinnerPriority.adapter = it
        }

    }

    private fun onCLickOkAlertAdd(
        alertDialog: AlertDialog,
        bindingDialogAlert: DialogAlertAddTaskBinding
    ) {
        lifecycleScope.launch (Dispatchers.Main) {

            val name = bindingDialogAlert.etTitle.text.toString()
            val details = bindingDialogAlert.etDetails.text.toString()
            val priority = bindingDialogAlert.spinnerPriority.selectedItem.toString()
            val currentDate = Date().time

            val isEmptyName = name.isEmpty() && name.compareTo("") == 0

            if (isEmptyName){
                bindingDialogAlert.tilTitle.error = getString(R.string.name_empty)
                return@launch
            }

            val task = Task(
                null, name
                , priority, 0
                , details
                //TYPE text now is hard-data but in future release it should be get from TaskTypes tables
                , "text", null
                ,currentDate,null
            )

            withContext (Dispatchers.Default) {
                val db = TaskController.getRoomDb(binding.root.context)
                val taskDao = db.taskDao()

                val idTask = taskDao.insertTransaction(priority, task)
                task.id = idTask.toInt()
                db.close()
            }

            val taskAdapter = listTasks.get(priority)
            if (taskAdapter != null) {
                taskAdapter.addTask(task)
                taskAdapter.notifyItemInserted(0)
            }

            withContext(Dispatchers.Main){
                alertDialog.dismiss()
            }

        }
    }

    private fun isEmptyField (value: String) : Boolean{
        return value.isEmpty() && value.compareTo("") == 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}