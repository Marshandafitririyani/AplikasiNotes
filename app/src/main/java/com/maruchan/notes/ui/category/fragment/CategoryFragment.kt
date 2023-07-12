package com.maruchan.notes.ui.category.fragment

import android.os.Bundle
import android.se.omapi.Session
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.base.fragment.CoreFragment
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.textOf
import com.maruchan.notes.R
import com.maruchan.notes.data.room.category.Category
import com.maruchan.notes.databinding.FragmentCategoryBinding
import com.maruchan.notes.databinding.ItemCategoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoryFragment : CoreFragment<FragmentCategoryBinding>(R.layout.fragment_category) {

    private val viewModel by activityViewModels<CategoryViewModel>()
    private val categoryAll = ArrayList<Category?>()

    private val adapterCategory by lazy {
        object : ReactiveListAdapter<ItemCategoryBinding, Category>(R.layout.item_category) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemCategoryBinding, Category>,
                position: Int
            ) {

                val item = getItem(position)
                val categoryId = item.id

                holder.binding.data = item
                holder.binding.imgDelete.setOnClickListener {
                    val builder = android.app.AlertDialog.Builder(requireContext())
                    builder.setMessage(R.string.delete_the_category)
                        .setCancelable(false)
                        .setPositiveButton("Delete") { dialog, id ->
                            categoryId.let {
                                viewModel.deleteCategory(id = it)
                            }
                        }
                        .setNegativeButton("Cancel") { dialog, id ->
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()

                }
                holder.binding.imgEdit.setOnClickListener {
                    val builder = AlertDialog.Builder(requireContext())
                    val customLayout: View =
                        layoutInflater.inflate(R.layout.popup_edit_categorry, null)
                    builder.setView(customLayout)


                    builder.setCancelable(true)
                    val dialog = builder.create()
                    dialog.window?.setBackgroundDrawableResource(R.color.transparent)

                    val editCategory = customLayout.findViewById<EditText>(R.id.et_edit_category)


                    val btnSave = customLayout.findViewById<TextView>(R.id.btn_save_edit_category)
                    btnSave.setOnClickListener {

                        val id = categoryId
                        val category = editCategory.textOf()
                        viewModel.editCategory(id.toString(), category)
                        dialog.dismiss()

                    }
                    dialog.show()
                }
            }


        }
    }
    @Inject
    lateinit var session: CoreSession

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        observe()
        adapter()
        initClick()
        search()
        getCategory()



    }

    private fun initClick() {
        binding?.btnAddCategory?.setOnClickListener {
            addCategory()
        }
        binding?.swipeRefreshCategory?.setOnRefreshListener {
            getCategory()
        }
    }

    private fun adapter() {
        binding?.rvCategory?.adapter = adapterCategory
    }

    private fun search() {
        binding?.searchCategory?.doOnTextChanged { text, start, before, count ->
            if (text?.isNotEmpty() == true) {
                val filter = categoryAll.filter { it?.category?.contains("$text", true) == true }
                adapterCategory.submitList(filter)

                binding?.tvEmpty?.isVisible = filter.isEmpty()

            } else {
                adapterCategory.submitList(categoryAll)
                binding?.tvEmpty?.isVisible = categoryAll.isEmpty()
            }

        }
    }

    private fun observe() {
        lifecycleScope.launch {
            launch {
                viewModel.saveGetCategory.collect { notes ->
                    adapterCategory.submitList(notes)
                    binding?.swipeRefreshCategory?.isRefreshing = false
                    categoryAll.clear()
                    categoryAll.addAll(notes)

                    if (notes.isEmpty()) {
                        binding?.tvEmpty?.visibility = View.VISIBLE
                    } else {
                        binding?.tvEmpty?.visibility = View.GONE
                    }

                }
            }
            launch {
                viewModel.saveCreatCategory.collect {
                    if (it.status == ApiStatus.SUCCESS) {
                        getCategory()
                    }
                }
            }


        }
    }

    private fun addCategory() {
        val builder = AlertDialog.Builder(requireContext())
        val customLayout: View = layoutInflater.inflate(R.layout.create_category, null)
        builder.setView(customLayout)


        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        val editCategory = customLayout.findViewById<EditText>(R.id.et_add_category)

        val btnSave = customLayout.findViewById<TextView>(R.id.btn_save_category)
        btnSave.setOnClickListener {

            val category = editCategory.textOf()
            viewModel.creatCategory(category)
            dialog.dismiss()

        }
        dialog.show()
    }

    private fun getCategory() {
        viewModel.getCategory()
    }


}

