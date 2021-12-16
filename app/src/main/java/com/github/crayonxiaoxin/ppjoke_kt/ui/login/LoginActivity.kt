package com.github.crayonxiaoxin.ppjoke_kt.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.github.crayonxiaoxin.lib_common.dialog.LoadingDialog
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityLoginBinding
import com.github.crayonxiaoxin.ppjoke_kt.utils.UserManager
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService
import com.tencent.connect.UserInfo
import com.tencent.connect.auth.QQToken
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog
    private var tencent: Tencent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBar.fitSystemBar(this, darkIcons = true)
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_login
        )

        loadingDialog = LoadingDialog(this)
        loadingDialog.setLoadingText("正在登陆")

        Tencent.setIsPermissionGranted(true)

        binding.actionClose.setOnClickListener {
            finish()
        }
        binding.actionLogin.setOnClickListener {
            loginByQQ()
        }
    }

    private fun loginByQQ() {
        if (tencent == null) {
            tencent = Tencent.createInstance("101980089", this)
        }
        tencent?.let {
            if (!it.isSessionValid) {
                loadingDialog.show()
                it.login(this, "all", qqLoginListener)
            }
        }
    }

    private val qqLoginListener: IUiListener = object : IUiListener {
        override fun onComplete(p0: Any?) {
            try {
                val response = p0 as JSONObject
                val openid = response.getString("openid")
                val access_token = response.getString("access_token")
                val expires_in = response.getString("expires_in")
                tencent?.setAccessToken(access_token, expires_in)
                tencent?.openId = openid
                getUserInfo(tencent?.qqToken, expires_in, openid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onError(uiError: UiError?) {
            loadingDialog.hide()
            toast("登录失败，原因：${uiError?.errorMessage}")
        }

        override fun onCancel() {
            loadingDialog.hide()
            toast("登录取消")
        }

        override fun onWarning(p0: Int) {

        }

    }

    private fun getUserInfo(qqToken: QQToken?, expiresIn: String, openid: String) {
        qqToken?.let {
            val userInfo = UserInfo(this, it)
            userInfo.getUserInfo(object : IUiListener {
                override fun onComplete(p0: Any?) {
                    try {
                        val response = p0 as JSONObject
                        val nickname = response.getString("nickname")
                        val figureurl_2 = response.getString("figureurl_2")
                        save(nickname, figureurl_2, openid, expiresIn)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(uiError: UiError?) {
                    loadingDialog.hide()
                    toast("登录失败，原因：${uiError?.errorMessage}")
                }

                override fun onCancel() {
                    loadingDialog.hide()
                    toast("登录取消")
                }

                override fun onWarning(p0: Int) {

                }
            })
        }
    }

    private fun save(nickname: String, figureurl2: String, openid: String, expiresIn: String) {
        lifecycleScope.launch {
            val response =
                prepare { apiService.insertUser(figureurl2, expiresIn, nickname, openid) }
            loadingDialog.hide()
            if (response.isSuccess) {
                val user = response.getOrNull()
                if (user == null) {
                    toast("登录失败")
                } else {
                    UserManager.set(user)
                    toast("登录成功")
                    finish()
                }
            } else {
                toast("登录失败")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqLoginListener)
        }
    }
}