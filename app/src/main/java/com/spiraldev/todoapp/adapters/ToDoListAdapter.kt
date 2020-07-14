package com.spiraldev.todoapp.adapters

import android.content.Context
import android.graphics.Paint
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spiraldev.todoapp.R
import com.spiraldev.todoapp.data.ToDoStatus
import com.spiraldev.todoapp.data.database.ToDoEntity
import com.spiraldev.todoapp.databinding.ItemRowBinding
import com.spiraldev.todoapp.util.DateHelper
import kotlinx.android.synthetic.main.item_row.view.*
import java.util.*
import kotlin.collections.ArrayList


interface OnItemClickCallback {
    fun onItemClick(todo: ToDoEntity)
    fun toggleCompletion(todo: ToDoEntity)
}

class ToDoListAdapter(private val onItemClickCallback: OnItemClickCallback) :
    RecyclerView.Adapter<ToDoListAdapter.ViewHolder>() {
    private val todoList: ArrayList<ToDoEntity> = arrayListOf()

    override fun getItemCount(): Int = todoList.size

    fun getToDoByPosition(pst: Int) = todoList[pst]

    fun updateList(todoList: List<ToDoEntity>) {
        this.todoList.clear()
        this.todoList.addAll(todoList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRowBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(todoList[position], onItemClickCallback)
    }

    class ViewHolder(binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ToDoEntity, onItemClickCallback: OnItemClickCallback) {
            itemView.title.text = item.title
            itemView.time.text = ""

            if (item.completionTime != null) {
                itemView.time.text = DateHelper.calendarToString(item.completionTime)
            }

            itemView.setOnClickListener {
                onItemClickCallback.onItemClick(item)
            }

            itemView.doneToggle.buttonDrawable =
                ContextCompat.getDrawable(itemView.context, R.drawable.custom_checkbox)

            itemView.time.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.textColor
                )
            )

            when (item.status) {
                ToDoStatus.DONE -> {
                    itemView.title.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.colorAccent
                        )
                    )
                    itemView.title.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                ToDoStatus.ACTIVE -> {
                    itemView.title.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.textColor
                        )
                    )
                    itemView.title.paintFlags =
                        itemView.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                else -> {
                    itemView.title.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.greyTextColor
                        )
                    )

                    itemView.time.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.greyTextColor
                        )
                    )

                    itemView.title.paintFlags = Paint.ANTI_ALIAS_FLAG
                    itemView.doneToggle.buttonDrawable =
                        ContextCompat.getDrawable(itemView.context, R.drawable.ic_overdue)
                }
            }

            itemView.doneToggle.setOnCheckedChangeListener(null)
            itemView.doneToggle.isChecked = item.status == ToDoStatus.DONE

            itemView.doneToggle.setOnCheckedChangeListener { _, _ ->
                onItemClickCallback.toggleCompletion(item)
            }
        }
    }
}