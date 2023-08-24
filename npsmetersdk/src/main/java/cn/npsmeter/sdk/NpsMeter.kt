package cn.npsmeter.sdk

import android.content.Context
import cn.npsmeter.sdk.api.ServiceApi
import cn.npsmeter.sdk.utils.ThanksIconManager
import cn.npsmeter.sdk.manager.SharedPreferencesManager
import cn.npsmeter.sdk.view.NpsQuestionAlertView

enum class NPSCloseType {
    User,
    Finish,
    DownFail,
    HaveShowForId,
    MinFatigue,
    FirstDay,
    RequestAnswerError,
    OtherError,
    AppCancel
}


object NpsMeter {

    private var showId: String? = null

    fun cancel() {
        showId = null
    }

    fun show(
        id: String,
        userid: String?,
        username: String?,
        remark: String?,
        fragmentManager: androidx.fragment.app.FragmentManager,
        context: Context,
        bottomPaddingIfShowInBottom: Int,
        showSuccess: (() -> Unit),
        closeAction: ((NPSCloseType) -> Unit)
    ) {
        try {
            showId = id
            SharedPreferencesManager.saveRequestConfig(context)
            ServiceApi.config(id, context) { configModel, _ ->
                try {
                    if (id != showId) {
                        closeAction(NPSCloseType.AppCancel)
                        return@config
                    }
                    if (configModel != null) {
                        if (configModel.custom_fatigue == 1) {
                            val canShow =
                                SharedPreferencesManager.canShowWithHaveShow(
                                    context,
                                    configModel
                                )
                            if (!canShow) {
                                closeAction(NPSCloseType.HaveShowForId)
                                return@config
                            }
                        }

                        if (configModel.open_cookies_config == 1) {
                            val canShow = SharedPreferencesManager.canShowWithFirstRequest(
                                context,
                                configModel
                            )
                            if (!canShow) {
                                closeAction(NPSCloseType.FirstDay)
                                return@config
                            }
                        }
                        if (configModel.is_open_min_fatigue == 1) {
                            val canShow =
                                SharedPreferencesManager.canShowWithLastShow(
                                    context,
                                    configModel
                                )
                            if (!canShow) {
                                closeAction(NPSCloseType.MinFatigue)
                                return@config
                            }
                        }

                        val userConfig = UserConfig()
                        userConfig.bottomPadding = bottomPaddingIfShowInBottom
                        ServiceApi.openView(
                            id,
                            userid,
                            username,
                            remark,
                            context
                        ) { questionModel ->
                            try {
                                if (id != showId) {
                                    closeAction(NPSCloseType.AppCancel)
                                    return@openView
                                }
                                if (questionModel != null) {
                                    if (questionModel.canShow()) {
                                        NpsQuestionAlertView.newInstance(
                                            questionModel,
                                            configModel,
                                            userConfig,
                                            closeAction
                                        )
                                            .apply {

                                            }.show(fragmentManager, "")
                                        showSuccess()
                                        SharedPreferencesManager.show(context)
                                        SharedPreferencesManager.saveHaveShow(
                                            context,
                                            configModel
                                        )
                                        Thread {
                                            ThanksIconManager.downIcon(configModel.thanks_icon)
                                        }.start()
                                    } else {
                                        closeAction(NPSCloseType.OtherError)
                                    }
                                } else {
                                    closeAction(NPSCloseType.DownFail)
                                }
                            } catch (exception: Exception) {
                                ServiceApi.errorLog(exception.toString(), context)
                                closeAction(NPSCloseType.OtherError)
                            }
                        }
                    } else {
                        closeAction(NPSCloseType.DownFail)
                    }
                } catch (exception: Exception) {
                    ServiceApi.errorLog(exception.toString(), context)
                    closeAction(NPSCloseType.OtherError)
                }
            }
        } catch (exception: Exception) {
            ServiceApi.errorLog(exception.toString(), context)
            closeAction(NPSCloseType.OtherError)
        }
    }
}