package com.maruchan.notes.ui.favourite

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.maruchan.notes.data.room.note.Note
import com.maruchan.notes.databinding.FragmentFavouriteBinding
import com.maruchan.notes.databinding.ItemNoteBinding
import com.maruchan.notes.ui.detail.AddNotesActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : CoreFragment<FragmentFavouriteBinding>(R.layout.fragment_favourite) {

    private val viewModel by activityViewModels<FavoriteViewModel>()
    private val favouride = ArrayList<Note?>()

    private val adapterFavourite by lazy {
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
        initClick()

    }

    private fun initClick() {
        binding?.swipeRefreshLayoutFavourite?.setOnRefreshListener {
            getNote()
        }
    }

    private fun search() {
        binding?.searchFavourite?.doOnTextChanged { text, start, before, count ->
            if (text?.isNotEmpty() == true) {
                val filter = favouride.filter { it?.title?.contains("$text", true) == true }
                adapterFavourite.submitList(filter)

                binding?.tvEmpty?.isVisible = filter.isEmpty()

            } else {
                adapterFavourite.submitList(favouride)
                binding?.tvEmpty?.isVisible = favouride.isEmpty()
            }

        }
    }

    private fun adapter() {
        binding?.recyclerViewFavourite?.adapter = adapterFavourite
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.saveFavourite.collect() { data ->
                        binding?.swipeRefreshLayoutFavourite?.isRefreshing = false
                        val listFavourite = data.filter {
                            it.favorite == true
                        }
                        favouride.clear()
                        favouride.addAll(listFavourite)
                        adapterFavourite.submitList(listFavourite)
                        if (favouride.isEmpty()) {
                            binding?.tvEmpty?.visibility = View.VISIBLE
                        } else {
                            binding?.tvEmpty?.visibility = View.GONE
                        }
                    }
                }
            }

        }


    }

    private fun getNote() {
        viewModel.getNote()
    }

}