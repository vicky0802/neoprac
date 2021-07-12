package com.example.practical.core

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tap.support.events.Event
import com.tap.support.ktx.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class CoreFragment<VM : CoreViewModel, DB : ViewDataBinding> : Fragment() {

    protected lateinit var viewModel: VM
    private lateinit var binding: DB
    private var reference = false
    protected var coreCoreVM: CoreViewModel? = null
    private val PERMISSION_CODE = 101
    private var permissionCallBack: PermissionCallBack? = null

    var coreActivity: CoreActivity<*, *>? = null
        private set

    open val isBackEnable: Boolean?
        get() = null

    open val title: String?
        get() = null

    open val isHomeEnable: Boolean?
        get() = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CoreActivity<*, *>) {
            val activity = context as CoreActivity<*, *>?
            this.coreActivity = activity
        }
    }

    override fun onDetach() {
        coreActivity = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
            activity?.let {
                coreCoreVM =
                    ViewModelProvider(it).get(CoreViewModel::class.java)
            }
            viewModel = ViewModelProvider(this).get(createViewModel())
            viewModel.mActivity = this.activity as CoreActivity<*, *>?
            setVM(binding)
            binding.lifecycleOwner = this
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (coreActivity != null) {
            if (coreActivity?.supportActionBar != null) {
                coreActivity?.initToolbarBack(isBackEnable)
                coreActivity?.initToolbarTitle(title)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstancState: Bundle?) {
        super.onViewCreated(view, savedInstancState)
        initData()
        isHomeEnable?.let { viewModel.isHomeVisible.set(it) }
        if (!reference) {
            createReference()
            reference = true
        }
    }

    private fun initData() {
        viewModel.title.observe(viewLifecycleOwner, Observer {
            coreActivity?.initToolbarTitle(it)
        })
        viewModel.showContent.observe(viewLifecycleOwner, Observer {
            coreActivity?.loadingDialog(false)
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            coreActivity?.loadingDialog(viewModel.isLoading.value)
        })

        viewModel.toastMessage.observe(viewLifecycleOwner, Observer {
            coreActivity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            toastMessage(it)
        })

        viewModel.snackMessage.observe(viewLifecycleOwner, Observer {
            coreActivity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            snackMessage(it)
        })

    }

    fun toastMessage(message: String?) {
        message?.let {
            toast(message)
        }
    }

    fun snackMessage(message: String?) {
        message?.let {
            binding.root.apply {
                snack(it) {}
            }
        }
    }

    @LayoutRes
    abstract fun getLayout(): Int

    abstract fun createViewModel(): Class<out VM>

    abstract fun setVM(binding: DB)

    abstract fun createReference()

    fun getBinding(): DB = binding


    protected fun requestPermissionsIfRequired(
        permissions: ArrayList<String>,
        permissionCallBack: PermissionCallBack?
    ) {
        this.permissionCallBack = permissionCallBack
        if (checkSelfPermissions(permissions)) {
            permissionCallBack?.permissionGranted()
        } else {
            requestAllPermissions(permissions, PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCallBack?.permissionGranted()
                } else {
                    if (checkPermissionRationale(permissions)) {
                        permissionCallBack?.permissionDenied()
                    } else {
                        permissionCallBack?.onPermissionDisabled()
                    }
                }
            }
        }
    }

    protected fun onBack() {
        activity?.onBackPressed()
    }

    protected fun onBackExclusive(fragment: Class<out Fragment>) {
        activity?.supportFragmentManager?.popBackStackImmediate(fragment.name, 0)
    }

    protected fun onBackInclusive(fragment: Class<out Fragment>) {
        activity?.supportFragmentManager?.popBackStackImmediate(
            fragment.name,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    protected fun onBack(@IntRange(from = 1, to = 100) steps: Int) {
        for (i in 1..steps) {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    fun post(data: Any) {
        EventBus.getDefault().post(data)
    }

    fun postSticky(data: Any) {
        EventBus.getDefault().postSticky(data)
    }

    fun repostSticky(data: Any) {
        EventBus.getDefault().removeAllStickyEvents()
        EventBus.getDefault().postSticky(data)
    }

    fun removeStickyEvent(data: Any) {
        EventBus.getDefault().removeStickyEvent(data)
    }

    @Subscribe
    open fun onEvent(event: Event) {
    }

    override fun onResume() {
        super.onResume()
        if (coreActivity != null) {
            if (coreActivity?.supportActionBar != null) {
                coreActivity?.initToolbarBack(isBackEnable)
                coreActivity?.initToolbarTitle(title)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}