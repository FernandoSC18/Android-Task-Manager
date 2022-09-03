package com.fscsoftware.managetasks.ui.finish_tasks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.fscsoftware.managetasks.R
import com.fscsoftware.managetasks.TaskDetailActivity
import com.fscsoftware.managetasks.adapter.TasksAdapter
import com.fscsoftware.managetasks.controller.TaskController
import com.fscsoftware.managetasks.databinding.FragmentFinishTasksBinding
import com.fscsoftware.managetasks.databinding.TaskSectionBinding
import com.fscsoftware.managetasks.helpers.SwipeHelperTaskFinish
import com.fscsoftware.managetasks.model.TaskFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FinishTasksFragment : Fragment() {

    private var _binding: FragmentFinishTasksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFinishTasksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bindingTask = TaskSectionBinding.inflate(
            LayoutInflater.from(binding.root.context)
            , binding.taskFinishSection
            , true)

        val tasksFinishAdapter = initTaskFinishAdapter( mutableListOf() , bindingTask.emptyFinish)

        bindingTask.rvTasks.adapter = tasksFinishAdapter
        bindingTask.title.text = getString(R.string.title_finish)

        getTasks(tasksFinishAdapter)

        //DELETE IMPORTANT ITEM AND MOVE TOUCH SWIPE
        val swipeHelper = SwipeHelperTaskFinish (tasksFinishAdapter )
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(bindingTask.rvTasks)

        return root
    }

    private fun initTaskFinishAdapter (tasks : MutableList<TaskFields>, emptyLayout : LinearLayout) : TasksAdapter {
        return TasksAdapter(tasks, emptyLayout ) { task ->
            val intent = Intent( binding.root.context , TaskDetailActivity::class.java)
            intent.putExtra(TaskDetailActivity.INTENT_TASK_DATA , Json.encodeToString( task.toTaskFinish() ))
            startActivity(intent)
        }
    }

    private fun getTasks(tasksAdapter: TasksAdapter) {
        lifecycleScope.launch (Dispatchers.Default) {
            val tasks = TaskController.getRoomTaskFinishDao(binding.root.context).getAll()
            withContext(Dispatchers.Main)  {
                tasksAdapter.setTasks(tasks)
                tasksAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}