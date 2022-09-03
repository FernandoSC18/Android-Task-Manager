package com.fscsoftware.managetasks.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.fscsoftware.managetasks.AboutActivity
import com.fscsoftware.managetasks.R
import com.fscsoftware.managetasks.TaskDetailActivity
import com.fscsoftware.managetasks.adapter.PrioritiesAdapter
import com.fscsoftware.managetasks.controller.TaskController
import com.fscsoftware.managetasks.databinding.DialogAlertAddEditPriorityBinding
import com.fscsoftware.managetasks.databinding.FragmentSettingsBinding
import com.fscsoftware.managetasks.helpers.SwipeHelperPriority
import com.fscsoftware.managetasks.model.Priority
import com.fscsoftware.managetasks.utils.SharedPreferencesManage
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var prioritiesAdapter : PrioritiesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Set values from user preferences to switch items
        //binding.swLockOrientation.isChecked = SharedPreferencesManage.getOrientation(binding.root.context)
        binding.swAskFinish.isChecked = SharedPreferencesManage.getSwipeFinish(binding.root.context)
        binding.swAskDelete.isChecked = SharedPreferencesManage.getSwipeDelete(binding.root.context)

        lifecycleScope.launch (Dispatchers.Default) {
            val prioritiesList = TaskController.getRoomPrioritiesDao(binding.root.context).getAll()

            prioritiesAdapter = initPrioritiesAdapter(prioritiesList.toMutableList())
            withContext(Dispatchers.Main){

                binding.rvPriorities.adapter = prioritiesAdapter

                //DELETE IMPORTANT ITEM AND MOVE TOUCH SWIPE
                val swipeHelperPriority= SwipeHelperPriority (prioritiesAdapter!! )
                val itemTouchHelper = ItemTouchHelper(swipeHelperPriority)
                itemTouchHelper.attachToRecyclerView(binding.rvPriorities)
            }

        }



        //Buttons Click event function assign
        binding.btnAbout.setCallbackOnTouch { openAboutActivity() }
        binding.btnSystemSettings.setCallbackOnTouch { openSettingsSystemApp() }
        binding.btnAddPriority.setCallbackOnTouch { eventAddPriority() }
        binding.btnEnableDelete.setCallbackOnTouch { enableOrDisableDelete() }

        //Switch Buttons change their value to SharedPreferences
        /*binding.swLockOrientation.setOnCheckedChangeListener { compoundButton, bool ->
            SharedPreferencesManage.setOrientation(binding.root.context, bool)
        }*/
        binding.swAskFinish.setOnCheckedChangeListener { compoundButton, bool ->
            SharedPreferencesManage.setSwipeFinish(binding.root.context, bool)
        }
        binding.swAskDelete.setOnCheckedChangeListener { compoundButton, bool ->
            SharedPreferencesManage.setSwipeDelete(binding.root.context, bool)
        }

        return root
    }

    private fun openAboutActivity() {
        val intent = Intent( activity , AboutActivity::class.java)
        startActivity(intent)
    }

    private fun openSettingsSystemApp (){
        startActivity(
            Intent( Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity?.packageName, null))
        )
    }

    /*
    * initPrioritiesAdapter
    * pass callback action to Priority adapter
    * Depending action value it action executes an delete or edit process to priorities
    * */
    private fun initPrioritiesAdapter (priority: MutableList<Priority> ) : PrioritiesAdapter {
        return PrioritiesAdapter(priority) { priority, action ->
            val stringIdDelete = R.string.delete
            if (action == stringIdDelete) {
                //DELETE OPTION
                deletePriority(priority)
            }else{
                //EDIT OPTION
                eventEditPriority(priority)
            }
        }
    }

    /*
    * enableOrDisableDelete
    * control action and icon of reciclerview priority item
    * only change action flag and icon to PrioritiesAdapter class
    * */
    private fun enableOrDisableDelete( ) {

        val stringIdEdit = R.string.edit
        val stringIdDelete = R.string.delete
        if (PrioritiesAdapter.action == stringIdDelete) {
            PrioritiesAdapter.action = stringIdEdit
            binding.tvEnableDelete.text = getString(R.string.priorities_e)
        }else if (PrioritiesAdapter.action == stringIdEdit) {
            PrioritiesAdapter.action = stringIdDelete
            binding.tvEnableDelete.text = getString(R.string.priorities_d)
        }

        prioritiesAdapter?.notifyDataSetChanged()

    }


    private fun eventAddPriority (){
        val bindingDialogAlert = DialogAlertAddEditPriorityBinding.inflate(
            LayoutInflater.from(activity as Context), binding.root, false
        )

        val alert = AlertDialog.Builder( ContextThemeWrapper( activity as Context, R.style.AlertDialogTheme ) )
            .setTitle(R.string.priorities_add)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok, null)
            .setView(bindingDialogAlert.root)

        val alertDialog = alert.create();
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = bindingDialogAlert.etName.text.toString()
            addPriority(name, bindingDialogAlert.tilName, alertDialog)
        }
    }

    private fun addPriority(
        name: String,
        textInputLayout: TextInputLayout,
        alertDialog: AlertDialog
    ) {

        val isEmptyName = name.isEmpty() && name.compareTo("") == 0
        if (isEmptyName) {
            textInputLayout.error = getString(R.string.name_priority_empty)
            return
        }

        TaskController.getViewModelScope().launch (Dispatchers.Default) {

            val db = TaskController.getRoomDb(activity as Context)
            val priorityDao = db.priorityDao()

            //Check no repet name, this validate constraint unique
            var exists = false
            priorityDao.getAll().forEach{
                if (it.name.compareTo(name) == 0) {
                    exists = true
                    return@forEach
                }
            }

            if (exists){
                withContext(Dispatchers.Main){
                    textInputLayout.error = getString(R.string.name_priority_unique)
                }
                return@launch
            }
            val newPriority = Priority(null, 0, name, null)
            val newId = priorityDao.insertTransaction(newPriority)
            newPriority.id = newId.toInt()

            withContext(Dispatchers.Main) {
                prioritiesAdapter?.insert(newPriority)
                prioritiesAdapter?.notifyItemInserted(0)
                alertDialog.dismiss()
            }

            db.close()
        }

    }

    private fun deletePriority (priority : Priority){

        TaskController.getViewModelScope().launch (Dispatchers.Default) {
            val db = TaskController.getRoomDb(activity as Context)
            val taskDao = db.taskDao()

            //Validate all tasks are complete for this priority
            val tasksOfThisPriority = taskDao.getAllByPriority(priority.name)
            if (tasksOfThisPriority.isNotEmpty()){
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, getString(R.string.name_priority_in_use), Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            //Validate exist at leat one or more priorities, should be one exist at least
            val listPriorities = prioritiesAdapter?.getIList()
            if (listPriorities == null || listPriorities.size <= 1){
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, getString(R.string.name_priority_one_at_least), Toast.LENGTH_SHORT ).show()
                }
                return@launch
            }

            val index = listPriorities.indexOf(priority)
            val priorityDao = db.priorityDao()
            priorityDao.deleteTransaction(priority, index)
            db.close()

            withContext(Dispatchers.Main){
                prioritiesAdapter?.deleteTask(priority)
                prioritiesAdapter?.notifyItemRemoved(index)
            }

        }
    }


    private fun eventEditPriority(priority: Priority) {
        val bindingDialogAlert = DialogAlertAddEditPriorityBinding.inflate(
            LayoutInflater.from(activity as Context), binding.root, false
        )

        bindingDialogAlert.etName.setText(priority.name)

        val alert = AlertDialog.Builder( ContextThemeWrapper( activity as Context, R.style.AlertDialogTheme ) )
            .setTitle(R.string.priorities_edit)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok, null)
            .setView(bindingDialogAlert.root)

        val alertDialog = alert.create();
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = bindingDialogAlert.etName.text.toString()
            editPriority(name, priority, bindingDialogAlert.tilName, alertDialog)
        }
    }

    private fun editPriority(
        name: String,
        priority: Priority,
        textInputLayout: TextInputLayout,
        alertDialog: AlertDialog
    ) {

        val isEmptyName = name.isEmpty() && name.compareTo("") == 0
        if (isEmptyName) {
            textInputLayout.error = getString(R.string.name_priority_empty)
            return
        }

        TaskController.getViewModelScope().launch (Dispatchers.Default) {

            val db = TaskController.getRoomDb(activity as Context)
            val priorityDao = db.priorityDao()
            val taskDao = db.taskDao()

            //Check no repeat name, this validate constraint unique
            var exists = false
            priorityDao.getAll().forEach{
                if ( it.name.compareTo(name) == 0 && it.id != priority.id) {
                    exists = true
                    return@forEach
                }
            }

            if (exists){
                withContext(Dispatchers.Main){
                    textInputLayout.error = getString(R.string.name_priority_unique)
                }
                return@launch
            }

            val indexChanged = prioritiesAdapter!!.getIList().indexOf(priority)
            val beforePriority = priority.name
            priority.name = name

            db.runInTransaction{
                priorityDao.update(priority)
                taskDao.updatePriorityName(beforePriority, name)
            }
            db.close()

            withContext(Dispatchers.Main) {
                prioritiesAdapter?.notifyItemChanged(indexChanged)
                alertDialog.dismiss()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}