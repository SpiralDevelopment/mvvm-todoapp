package com.spiraldev.todoapp.ui.todolist

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.spiraldev.todoapp.R
import com.spiraldev.todoapp.adapters.OnItemClickCallback
import com.spiraldev.todoapp.adapters.ToDoListAdapter
import com.spiraldev.todoapp.alarm.AlarmHelper
import com.spiraldev.todoapp.core.base.BaseFragment
import com.spiraldev.todoapp.data.FilterOption
import com.spiraldev.todoapp.data.ToDoStatus
import com.spiraldev.todoapp.data.database.ToDoEntity
import com.spiraldev.todoapp.databinding.FragmentToDoListBinding
import com.spiraldev.todoapp.ui.addedit.AddEditFragment
import com.spiraldev.todoapp.util.SwipeToDeleteCallback
import com.spiraldev.todoapp.util.doOnChange
import java.util.*

class ToDoListFragment : BaseFragment(), OnItemClickCallback {
    private val viewModel by viewModels<ToDoListViewModel> { viewModelFactory }
    private var todoListAdapter = ToDoListAdapter(this)
    private lateinit var binding: FragmentToDoListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToDoListBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initializeViews()
    }

    override fun initializeViews() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).actionBar?.setDisplayShowTitleEnabled(false)

        binding.fab.setOnClickListener {
            it.findNavController().navigate(R.id.action_toDoListFragment_to_addEditFragment)
        }

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoListAdapter
        }

        enableSwipeToDeleteAndUndo()
    }

    override fun observeViewModel() {
        viewModel.filteredToDoLD.doOnChange(viewLifecycleOwner) {
            Log.d("TAG", "filteredToDoLD ${it?.size}")
            todoListAdapter.updateList(it ?: emptyList())
        }
    }

    override fun onItemClick(todo: ToDoEntity) {
        val bundle = bundleOf(AddEditFragment.TODO to todo.copy())
        findNavController().navigate(R.id.action_toDoListFragment_to_addEditFragment, bundle)
    }

    override fun toggleCompletion(todo: ToDoEntity) {
        when (todo.status) {
            ToDoStatus.DONE -> {
                viewModel.toggleCompletion(todo.id, ToDoStatus.ACTIVE)
            }
            ToDoStatus.ACTIVE -> {
                viewModel.toggleCompletion(todo.id, ToDoStatus.DONE)
            }
            ToDoStatus.OVERDUE -> {
                showSnackBar(binding.coodLayout, "Task is overdue.")
            }
        }
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = todoListAdapter.getToDoByPosition(position)
                viewModel.deleteToDo(item)

                item.completionTime?.let {
                    AlarmHelper.cancelReminder(requireContext(), item.id)
                }

                showSnackBarWithAction(
                    binding.coodLayout,
                    "Task was removed from the list.",
                    "UNDO"
                ) {
                    viewModel.insert(item)
                    item.completionTime?.let {
                        if (item.notifyHourBefore > 0) {
                            it.add(Calendar.HOUR, -item.notifyHourBefore)
                            AlarmHelper.setReminder(
                                requireContext(),
                                it.timeInMillis,
                                item.id
                            )
                        }
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerview)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filterStatus -> {
                val listItems = FilterOption.values().map { it.name }.toTypedArray()
                val mBuilder = AlertDialog.Builder(this.requireActivity())

                mBuilder.setTitle("Filter Tasks")
                mBuilder.setSingleChoiceItems(
                    listItems,
                    viewModel.getFilterOption().ordinal
                ) { _, i ->
                    viewModel.setFilterOption(FilterOption.valueOf(listItems[i]))
                    viewModel.refreshToDoList()
                }

                val mDialog = mBuilder.create()
                mDialog.show()
                true
            }
            R.id.filterDateTime -> {
                val builder = MaterialDatePicker.Builder.dateRangePicker()
                val dRange = viewModel.getDateRange()
                builder.setSelection(Pair(dRange.first, dRange.second))
                val picker = builder.build()

                picker.addOnPositiveButtonClickListener {
                    viewModel.setDateRange(it.first ?: dRange.first, it.second ?: dRange.second)
                    viewModel.refreshToDoList()
                }

                picker.show(activity?.supportFragmentManager!!, picker.toString())
                true
            }
            else -> view?.let {
                NavigationUI.onNavDestinationSelected(
                    item,
                    it.findNavController()
                )
            } ?: super.onOptionsItemSelected(item)
        }
    }

}