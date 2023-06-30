package com.maruchan.notes.ui.detail

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.crocodic.core.helper.BitmapHelper
import com.maruchan.notes.R
import com.maruchan.notes.base.BaseActivity
import com.maruchan.notes.const.Const
import com.maruchan.notes.data.helper.ViewBindingHelper.Companion.writeBitmap
import com.maruchan.notes.data.room.category.Category
import com.maruchan.notes.data.room.category.Category_by_one
import com.maruchan.notes.data.room.note.Note
import com.maruchan.notes.databinding.ActivityAddNotesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class AddNotesActivity :
    BaseActivity<ActivityAddNotesBinding, AddNotesViewModel>(R.layout.activity_add_notes) {

    private var notes: Note? = null
    private var category: Category? = null
    private var oldTitle: String? = null
    private var oldContent: String? = null
    private var oldCategory: String? = null
    private val categoryByOne = ArrayList<Category_by_one?>()
    private var categoryId: String? = null
    private var notePhoto: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClick()
        observe()
        getCategory()
        getCategoryName(category?.category.toString())
        Log.d("cek category get name", "$category")

        notes = intent.getParcelableExtra(Const.NOTE.NOTE)
        oldTitle = notes?.title
        oldContent = notes?.content
        oldCategory = notes?.categories_id
        Log.d("cek category id to string", "$oldCategory")
//        oldCategory = category?.category
//        binding.data = notes
        binding.photo = notes?.photo
        binding.character.text = notes?.content?.length.toString()
        binding.imageNote.isVisible = notes?.photo?.isNullOrEmpty() == false

//        getCategoryName(notes?.categories_id.toString())
//getCategoryName(oldCategory.toString())
//        Log.d("cek category by id", "${getCategoryName(oldCategory.toString())}")

    }

    private fun initClick() {
        binding.btnBold.setOnClickListener {
            binding.root.snacked("Features not yet available")
        }
        binding.btnUnderlined.setOnClickListener {
            binding.root.snacked("Features not yet available")
        }
        binding.btnItalic.setOnClickListener {
            binding.root.snacked("Features not yet available")
        }
        binding.btnFormatStrikethrough.setOnClickListener {
            binding.root.snacked("Features not yet available")
        }
        binding.btnSave.setOnClickListener {
            validateNote()
        }
        binding.btnDelet.setOnClickListener {
            deleteNotes()
        }

        binding.btnFavourite.setOnClickListener {
            getFavourite()
        }
        binding.btnShare.setOnClickListener {
            shareText()
        }
        binding.categoryAdd.setOnClickListener {
            autocompleteSpinner()
        }

        binding.tvAddImage.setOnClickListener {
            gallery()
        }
        binding.fabDeleteImage.setOnClickListener {
            binding.imageNote.setImageBitmap(null)
            binding.imageNote.setVisibility(View.GONE)
            binding.fabDeleteImage.setVisibility(View.GONE)

        }
    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collect {
                        binding.data = notes

                    }
                }
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Loading...")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                setResult(456)
                                finish()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
                launch {
                    viewModel.saveFavourite.collect {
                        if (it.status == ApiStatus.SUCCESS) {
                            notes?.favorite = it.message == "favorite"
                            binding.data = notes
                            setResult(456)
                        } else if (it.status == ApiStatus.ERROR) {
                            loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
                launch {
                    viewModel.saveGetCategory.collect { category ->
                        Log.d("cek category", "$category")
                        categoryByOne.addAll(category)

                    }
                }
                launch {
                    viewModel.saveGetCategoryName.collect { categoryById ->
                        binding.categoryAdd.text(categoryById.category)
                        Log.d("cek by id observe", "$categoryById")
                    }

                }
            }
        }
    }

    private fun gallery() {
        activityLauncher.openGallery(this) { file, exception ->
            binding.imageNote.visibility = View.VISIBLE
            binding.fabDeleteImage.visibility = View.VISIBLE

            val bitmap = BitmapFactory.decodeFile(file?.absolutePath)
            val resizeBitmap = BitmapHelper.resizeBitmap(bitmap, 512f)

            notePhoto = createImageFile().also { it.writeBitmap(resizeBitmap) }
            binding.imageFile = notePhoto
            file?.delete()
        }
    }

    private fun validateNote() {
        val title = binding.etAddTitle.textOf()
        val content = binding.etAddContent.textOf()
        if (binding.etAddTitle.isEmptyRequired(R.string.label_must_fill) ||
            binding.etAddContent.isEmptyRequired(R.string.label_must_fill) ||
            binding.categoryAdd.isEmptyRequired(R.string.label_must_fill))
        {
            return
        }
        when {
            notes == null && notePhoto == null -> {
                viewModel.createNote(title, content, categoryId)
            }
            notes == null && notePhoto != null -> {
                viewModel.createNote(title, content, categoryId, notePhoto)
            }
            notes != null && notePhoto == null -> {
                viewModel.updateNote(notes?.id, title, content, categoryId)
            }
            notes != null && notePhoto != null -> {
                viewModel.updateNote(notes?.id, title, content, categoryId, notePhoto)
            }

        }

    }

    private fun shareText() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, binding.etAddContent.getText().toString())
        sendIntent.type = "text/plain"
        val shareIntent = Intent.createChooser(sendIntent, "chat")
        startActivity(shareIntent)

    }

    private fun getFavourite() {
        val favouriteId = notes?.id
        if (favouriteId != null) {
            if (notes?.favorite == true) {
                viewModel.unFavourite(favouriteId)
            } else {
                viewModel.favourite(favouriteId)
            }
            setResult(Const.LIST.RELOAD)
        }
    }

    private fun getCategory() {
        viewModel.getCategory()
    }

    private fun getCategoryName(id: String) {
        viewModel.getCategoryName(id)
    }

    private fun autocompleteSpinner() {
        val autoCompleteSpinner = findViewById<AutoCompleteTextView>(R.id.category_add)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryByOne)
        autoCompleteSpinner.setAdapter(adapter)

        autoCompleteSpinner.setOnClickListener {
            autoCompleteSpinner.showDropDown()
            autoCompleteSpinner.setDropDownVerticalOffset(autoCompleteSpinner.height)
        }
        autoCompleteSpinner.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = categoryByOne[position]
            Log.d("cek category 2", "${categoryByOne.size}")

            categoryId = selectedItem?.id!!
            Log.d("cek category id", "${categoryId}")

        }
    }

    private fun deleteNotes() {
        val builder = AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.popup_delete, null)
        builder.setView(customLayout)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnDelete = customLayout.findViewById<TextView>(R.id.btn_delete_notes)
        btnDelete.setOnClickListener {
            if (notes != null) {
                viewModel.deleteNote(notes!!.id)
            }
        }

        val btnCancle = customLayout.findViewById<TextView>(R.id.btn_cancle_delete)
        btnCancle.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}