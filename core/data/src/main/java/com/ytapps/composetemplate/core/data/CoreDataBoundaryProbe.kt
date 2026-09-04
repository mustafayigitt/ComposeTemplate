package com.ytapps.composetemplate.core.data

import com.ytapps.composetemplate.core.config.IConfigManager

/**
 * TEMPORARY probe, reverted in the next commit.
 *
 * `core:data` is one of the four modules that survive every plug-out combination, so it may
 * only name the others. Importing [IConfigManager] from the optional `core:config` module is
 * the violation `checkModuleBoundary` has to reject: nothing deletes `core:data`, so an import
 * from here would make `core:config` undeletable in every configuration.
 *
 * The import resolves to a real public interface and is implemented below, so neither an
 * unresolved reference nor a ktlint unused-import can be mistaken for the boundary check
 * firing.
 */
internal class CoreDataBoundaryProbe : IConfigManager {
    override fun getBoolean(key: String): Boolean = false

    override fun getString(key: String): String = ""

    override fun getLong(key: String): Long = 0L
}
