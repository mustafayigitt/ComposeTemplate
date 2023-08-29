package com.ytapps.composetemplate.core.base

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */
open class BaseUiState(
    open val isLoading: Boolean = true,
    open val error: String? = null
)