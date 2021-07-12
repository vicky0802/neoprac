package com.example.practical.core

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tap.R
import com.tap.module.localizationUtil.MyContextWrapper
import com.tap.support.events.Maintenance
import com.tap.support.events.ShowError
import com.tap.support.ktx.*
import com.tap.support.utilise.LocaleHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class CoreActivity<VM : CoreViewModel, DB : ViewDataBinding> : AppCompatActivity() {


    lateinit var viewModel: VM
    private lateinit var binding: DB
    private val PERMISSION_CODE = 111
    private var permissionCallBack: PermissionCallBack? = null
    var dialog: Dialog? = null

    open val isBackEnable: Boolean?
        get() = null

    open val title: String?
        get() = null

    open val toolbar: Toolbar?
        get() = null

    open val isHomeEnable: Boolean?
        get() = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoreApp.INSTANCE.getSelectedLanguage()?.let { LocaleHelper.onCreate(this, it) }
        binding = DataBindingUtil.setContentView(this, getLayout())
        viewModel = ViewModelProvider(this).get(createViewModel())
        viewModel.savedInstanceState = savedInstanceState
        viewModel.mActivity = this
        setVM(binding)
        isHomeEnable?.let { viewModel.isHomeVisible.set(it) }
        initToolbar(toolbar)
        initData()
        binding.lifecycleOwner = this
        createReference()
    }

    private fun initData() {
        viewModel.showContent.observe(this, Observer {
            loadingDialog(false)
        })

        viewModel.isLoading.observe(this, Observer {
            loadingDialog(viewModel.isLoading.value)
        })
        viewModel.toastMessage.observe(this, Observer {
            toastMessage(it)
        })

        viewModel.snackMessage.observe(this, Observer {
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

    fun initToolbar(toolbar: Toolbar?) {
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            initToolbarBack(isBackEnable)
            initToolbarTitle(title)
            viewModel.title.observe(this, Observer {
                initToolbarTitle(it)
            })
        }
    }

    fun initToolbarBack(isEnable: Boolean?) {
        isEnable?.let {
            this.supportActionBar?.setHomeButtonEnabled(it)
            this.supportActionBar?.setDisplayHomeAsUpEnabled(it)
            if (getSelectedLanguage().equals("ar")) {
                this.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_forward_arrow)
            } else {
                this.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            }
        }

    }

    fun initToolbarTitle(title: String?) {
        if (this.supportActionBar != null && title?.isNotEmpty() == true) {
            this.supportActionBar?.title = title
            this.toolbar?.title = title
        }
    }

    @LayoutRes
    abstract fun getLayout(): Int

    abstract fun createViewModel(): Class<out VM>

    abstract fun setVM(binding: DB)

    abstract fun createReference()

    fun getBinding(): DB = binding

    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0)
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    fun post(data: Any) {
        EventBus.getDefault().post(data)
    }

    fun postSticky(data: Any) {
        EventBus.getDefault().postSticky(data)
    }

    fun removeStickyEvent(data: Any) {
        EventBus.getDefault().removeStickyEvent(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    open fun showDialog(show: String) {
        when (show) {
            SHOW_PROGRESS -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                viewModel.loading()
                viewModel.progressBar.set(View.VISIBLE)
            }
            DISMISS_PROGRESS -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                viewModel.content()
                viewModel.progressBar.set(View.GONE)
            }
        }
        EventBus.getDefault().removeStickyEvent(show)
    }

    var isShowing = false

    @Subscribe
    fun showError(error: ShowError) {
        if (!isShowing) {
            isShowing = true
            simpleAlert(error.error) { isShowing = false }
        }
        //binding.root.snackBar(error.error)
    }

    @Subscribe
    fun <T : Activity> showMaintenance(m: Maintenance<T>) {
        viewModel.dismissProgress()
        startActivity(Intent(this, m.aClass).putExtra(MAINTENANCE_END_TIME, m.endTime))
    }

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

    override fun attachBaseContext(newBase: Context?) {
//        CoreApp.INSTANCE.getSelectedLanguage()?.let {
//            if (newBase != null) super.attachBaseContext(MyContextWrapper.wrap(newBase, it))
//        }
        CoreApp.INSTANCE.getSelectedLanguage()?.let {
            if (newBase != null) super.attachBaseContext(MyContextWrapper.wrap1(newBase, it))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    /**
     * Display progress dialog on screen when this method call with true flag
     * Dismiss progress dialog if dialog isshowing and flag is false
     * @param b     Dialog display control flag
     */
    fun loadingDialog(b: Boolean?) {
        if (dialog == null) {
            dialog = AppCompatDialog(this/*, R.style.progress_bar_style*/)
            dialog?.setContentView(R.layout.layout_progressbar)
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setCancelable(false)
            dialog?.setCanceledOnTouchOutside(false)
        }
        if (b == true) {
            dialog?.show()
        } else {
            if (dialog?.isShowing == true)
                dialog?.dismiss()
        }
    }
}
