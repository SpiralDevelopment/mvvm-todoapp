package com.spiraldev.todoapp.ui.addedit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.spiraldev.todoapp.BuildConfig
import com.spiraldev.todoapp.R
import com.spiraldev.todoapp.alarm.AlarmHelper
import com.spiraldev.todoapp.core.base.BaseFragment
import com.spiraldev.todoapp.data.ToDoStatus
import com.spiraldev.todoapp.data.database.ToDoEntity
import com.spiraldev.todoapp.databinding.FragmentAddEditBinding
import com.spiraldev.todoapp.util.DateHelper.calendarToString
import com.spiraldev.todoapp.util.customAfterTextChanged
import com.spiraldev.todoapp.util.doOnChange
import com.spiraldev.todoapp.views.DateTimePicker
import kotlinx.android.synthetic.main.dialog_image_source_selector.view.*
import kotlinx.android.synthetic.main.fragment_add_edit.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddEditFragment : BaseFragment() {

    companion object {
        const val TODO = "todo"

        const val GALLERY_PICK_RC = 2
        const val CAMERA_CAPTURE_RC = 1
    }

    private val viewModel by viewModels<AddEditViewModel> { viewModelFactory }
    private lateinit var binding: FragmentAddEditBinding
    private var todoNHB: Int = -1
    private var todoId: Int = 0
    private var todoCompTime: Calendar? = null
    private var todoStatus: ToDoStatus = ToDoStatus.ACTIVE
    private var todoTitle: String = ""
    private var todoDesc: String? = ""
    private var todoImagePath: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditBinding.inflate(layoutInflater)

        savedInstanceState?.getParcelable<ToDoEntity>(TODO)?.let {
            setToDo(it)
        } ?: arguments?.getParcelable<ToDoEntity>(TODO)?.let {
            setToDo(it)
        }

        observeViewModel()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(TODO, getToDo())
    }

    override fun initializeViews() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_cancel)
        binding.toolbar.setNavigationOnClickListener {
            hideKeyboard()
            it.findNavController().popBackStack()
        }

        binding.dateTime.setText(calendarToString(todoCompTime))
        binding.description.setText(todoDesc)
        binding.title.setText(todoTitle)

        Glide.with(this)
            .load(todoImagePath)
            .placeholder(R.drawable.no_image)
            .into(binding.imagePath)

        binding.imagePath.setOnClickListener {
            showImageLoadingDialog(it)
        }

        binding.title.customAfterTextChanged {
            todoTitle = it.toString()
        }

        binding.description.customAfterTextChanged {
            todoDesc = it.toString()
        }

        binding.dateTime.setOnClickListener {
            DateTimePicker().showDateTimePickerDialog(
                requireContext(),
                todoCompTime
            ) { c ->
                todoNHB = -1
                todoCompTime = c
                binding.dateTime.setText(calendarToString(c))
                todoStatus =
                    if (c < Calendar.getInstance()) ToDoStatus.OVERDUE else ToDoStatus.ACTIVE

                toggleNotificationIcon()
            }
        }

        binding.submit.setOnClickListener {
            hideKeyboard()

            if (TextUtils.isEmpty(binding.title.text)) {
                showSnackBar(binding.submit, "Title filed cannot be empty")
            } else {
                viewModel.saveToDo(getToDo())
            }
        }
    }

    private fun setToDo(it: ToDoEntity) {
        todoId = it.id
        todoCompTime = it.completionTime
        todoNHB = it.notifyHourBefore
        todoStatus = it.status
        todoTitle = it.title
        todoDesc = it.description
        todoImagePath = it.imagePath
    }

    private fun getToDo() = ToDoEntity(
        todoTitle,
        todoDesc,
        todoCompTime,
        todoNHB,
        todoStatus,
        todoImagePath,
        id = todoId
    )

    override fun observeViewModel() {
        viewModel.newToDoLD.doOnChange(viewLifecycleOwner) {
            Log.d("TAG", "newToDoLD onchange ${it?.id}")

            it?.let { todo ->
                todo.completionTime?.let { c ->
                    if (todo.notifyHourBefore > 0) {
                        c.add(Calendar.HOUR, -todo.notifyHourBefore)
                        AlarmHelper.setReminder(requireContext(), c.timeInMillis, todo.id)
                    }
                }
            }

            findNavController().navigate(R.id.action_addEditFragment_to_toDoListFragment)
        }
    }

    private fun toggleNotificationIcon() {
        if (todoStatus != ToDoStatus.OVERDUE && todoCompTime != null) {
            toolbar.menu.findItem(R.id.notification).isVisible = true

            if (todoNHB > 0) {
                toolbar.menu.findItem(R.id.notification).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_notification)
            } else {
                toolbar.menu.findItem(R.id.notification).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_notification_none)
            }
        } else {
            toolbar.menu.findItem(R.id.notification).isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_menu, menu)
        toggleNotificationIcon()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notification -> {
                hideKeyboard()
                showSingleChoiceDialog()
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

    private fun showSingleChoiceDialog() {
        val listItems = resources.getStringArray(R.array.ntf_options)
        val listItemsValues = resources.getIntArray(R.array.ntf_options_values)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Notify me:")
            .setPositiveButton("Ok") { dialog, _ ->
                val idx = (dialog as AlertDialog).listView.checkedItemPosition
                todoNHB = listItemsValues[idx]
            }

        builder.setSingleChoiceItems(
            listItems,
            listItemsValues.indexOf(todoNHB),
            null
        )

        builder.create().show()
    }

    private fun hideKeyboard() {
        view?.let {
            val imm = context?.getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_CAPTURE_RC)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            todoImagePath = absolutePath
        }
    }

    private fun showImageLoadingDialog(v: View) {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_image_source_selector, null)
        val cameraTv = view.camera_tv
        val galleryTv = view.gallery_tv

        val dialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setTitle("Choose an action")
            .setView(view)
            .create()

        dialog.show()

        cameraTv.setOnClickListener {
            takePicture()
            dialog.dismiss()
        }

        galleryTv.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_PICK_RC)
            dialog.dismiss()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_PICK_RC -> {
                    val selectedImage = data?.data
                    todoImagePath = selectedImage.toString()
                    Glide.with(this).load(selectedImage).into(binding.imagePath)
                }
                CAMERA_CAPTURE_RC -> {
                    Glide.with(this).load(todoImagePath).into(binding.imagePath)
                }
            }
        }
    }
}