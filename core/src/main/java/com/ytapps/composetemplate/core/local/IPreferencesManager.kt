package com.ytapps.composetemplate.core.local

import kotlinx.coroutines.flow.Flow

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 * 
 * Interface for managing non-sensitive user preferences with DataStore.
 */
interface IPreferencesManager {
    
    fun getUUID(): String?
    fun hasUser(): Boolean
    
    suspend fun setUUID(uuid: String)
    suspend fun clear()
    
    val uuidFlow: Flow<String?>
}
