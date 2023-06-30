package com.maruchan.notes.base

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.crocodic.core.base.fragment.CoreFragment
import com.crocodic.core.data.CoreSession
import com.google.gson.Gson
import com.maruchan.notes.data.database.AppDatabase
import javax.inject.Inject

open class Fragment<vb : ViewDataBinding>(@LayoutRes private val layoutRes: Int) :
    CoreFragment<vb>(layoutRes) {
    protected val app: App by lazy { requireActivity().application as App }

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var session: CoreSession

    @Inject
    lateinit var appDatabase: AppDatabase

}