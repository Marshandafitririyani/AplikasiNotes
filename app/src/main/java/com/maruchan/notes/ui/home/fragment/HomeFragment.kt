package com.maruchan.notes.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.base.fragment.CoreFragment
import com.maruchan.notes.R
import com.maruchan.notes.const.Const
import com.maruchan.notes.data.room.category.Category
import com.maruchan.notes.data.room.note.Note
import com.maruchan.notes.databinding.FragmentHomeBinding
import com.maruchan.notes.databinding.ItemNoteBinding
import com.maruchan.notes.ui.detail.AddNotesActivity
import com.maruchan.notes.ui.home.activity.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : CoreFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val notesAll = ArrayList<Note?>()
    private val viewModel by activityViewModels<HomeViewModel>()
    private val categoryByOne = ArrayList<Category?>()
    private var categoryId: String? = null


    private val adapter by lazy {
        ReactiveListAdapter<ItemNoteBinding, Note>(R.layout.item_note).initItem { position, data ->
            val toEdit = Intent(requireActivity(), AddNotesActivity::class.java).apply {
                putExtra(Const.NOTE.NOTE, data)
            }
            startActivityForResult(toEdit, 123)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 456) {
            getNote()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        observe()
        adapter()
        getNote()
        search()
        categoryHome()
        getCategory()
        initClick()

    }

    private fun initClick() {
        binding?.btnAddNote?.setOnClickListener {
            val toAdd = Intent(requireActivity(), AddNotesActivity::class.java).apply {
                putExtra(Const.NOTE.NOTE, data)
            }
            startActivityForResult(toAdd, 987)
        }
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            getNote()
        }
        binding?.searchCategoryHome?.setOnClickListener {
            autocompleteSpinner()
        }
    }

    private fun search() {
        binding?.searchHome?.doOnTextChanged { text, start, before, count ->
            if (text?.isNotEmpty() == true) {
                val filter = notesAll.filter { it?.title?.contains("$text", true) == true }
                adapter.submitList(filter)

                binding?.tvEmpty?.isVisible = filter.isEmpty()

            } else {
                adapter.submitList(notesAll)
                binding?.tvEmpty?.isVisible = notesAll.isEmpty()
            }

        }
    }

    private fun categoryHome() {
        binding?.searchCategoryHome?.doOnTextChanged { text, start, before, count ->
            if (text?.isNotEmpty() == true) {
                val filter = notesAll.filter { it?.categories_name?.contains("$text", true) == true }
                adapter.submitList(filter)

                binding?.tvEmpty?.isVisible = filter.isEmpty()

            } else {
                adapter.submitList(notesAll)
                binding?.tvEmpty?.isVisible = notesAll.isEmpty()
            }

        }
    }

    private fun adapter() {
        binding?.recyclerViewHome?.adapter = adapter
    }

    private fun getCategory() {
        viewModel.getCategory()
    }

    private fun autocompleteSpinner() {
        val autoCompleteSpinner = binding?.searchCategoryHome
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryByOne)
        autoCompleteSpinner?.setAdapter(adapter)

        autoCompleteSpinner?.setOnClickListener {
            autoCompleteSpinner.showDropDown()
            autoCompleteSpinner.setDropDownVerticalOffset(autoCompleteSpinner.height)
        }
        autoCompleteSpinner?.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = categoryByOne[position]
             categoryId= selectedItem?.id!!

        }
    }
    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.savegetNote.collect { notes ->
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        notesAll.clear()
                        notesAll.addAll(notes)
                        adapter.submitList(notes)
                        if (notes.isEmpty()) {
                            binding?.tvEmpty?.visibility = View.VISIBLE
                        } else {
                            binding?.tvEmpty?.visibility = View.GONE
                        }

                    }

                }
                launch {
                    viewModel.saveGetCategory.collect { category ->
                        categoryByOne.addAll(category)

                    }
                }
            }
        }
    }


    private fun getNote() {
        viewModel.getNote()
    }


}





