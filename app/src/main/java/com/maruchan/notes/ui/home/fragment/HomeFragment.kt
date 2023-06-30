package com.maruchan.notes.ui.home.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.library.baseAdapters.BR.data
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.base.fragment.CoreFragment
import com.crocodic.core.extension.createIntent
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
import com.maruchan.notes.R
import com.maruchan.notes.const.Const
import com.maruchan.notes.data.room.note.Note
import com.maruchan.notes.data.room.user.User
import retrofit2.Call
import com.maruchan.notes.databinding.FragmentHomeBinding
import com.maruchan.notes.databinding.ItemNoteBinding
import com.maruchan.notes.ui.detail.AddNotesActivity
//import com.maruchan.notes.home.HomeViewModel
import com.maruchan.notes.ui.home.activity.HomeViewModel
import com.maruchan.notes.ui.profile.ProfileActivity
import com.maruchan.notes.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : CoreFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val notesAll = ArrayList<Note?>()
    private val viewModel by activityViewModels<HomeViewModel>()

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
        if (resultCode == 456){
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
    private fun initClick(){
        binding?.btnAddNote?.setOnClickListener {
            val toAdd = Intent(requireActivity(), AddNotesActivity::class.java).apply {
                putExtra(Const.NOTE.NOTE, data)
            }
            startActivityForResult(toAdd,987 )
        }
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            getNote()
        }
    }

    private fun search() {
        binding?.searchHome?.doOnTextChanged { text, start, before, count ->
            if (text?.isNotEmpty()==true) {
                val filter = notesAll.filter { it?.title?.contains("$text", true) == true }
                adapter.submitList(filter)

                binding?.tvEmpty?.isVisible =filter.isEmpty()

            } else {
                adapter.submitList(notesAll)
                binding?.tvEmpty?.isVisible =notesAll.isEmpty()
            }

        }
    }
    private fun adapter() {
        binding?.recyclerViewHome?.adapter = adapter
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.savegetNote.collect { notes ->
                        Log.d("cek note 2", "${notes.size}")
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
            }
        }
    }



    private fun getNote() {
        viewModel.getNote()
        Log.d("cek get", "ttt")

    }


}





