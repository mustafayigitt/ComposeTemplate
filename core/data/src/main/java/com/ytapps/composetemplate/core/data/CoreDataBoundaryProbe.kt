package com.ytapps.composetemplate.core.data

import com.ytapps.composetemplate.core.network.BaseRepository

/**
 * TEMPORARY probe, reverted in the next commit.
 *
 * `core:data` is one of the four modules that survive every plug-out combination, so it may
 * only name the others. Importing [BaseRepository] from the optional `core:network` module is
 * the violation `checkModuleBoundary` has to reject: nothing deletes `core:data`, so an import
 * from here would make `core:network` undeletable in every configuration.
 *
 * The import resolves to a real public symbol and is used by the declaration below, so the
 * boundary check is the only thing that can fail on this file.
 */
internal abstract class CoreDataBoundaryProbe : BaseRepository()
