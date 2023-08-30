package com.ytapps.composetemplate.core.base

/**
 * Created by mustafa.yigit on 30/08/2023
 * mustafa.yt65@gmail.com
 */

interface IMapper<in I, out O> {
    fun map(input: I): O
}