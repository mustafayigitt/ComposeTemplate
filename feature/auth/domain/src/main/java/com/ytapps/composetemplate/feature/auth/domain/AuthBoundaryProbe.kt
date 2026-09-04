package com.ytapps.composetemplate.feature.auth.domain

import com.ytapps.composetemplate.feature.home.domain.HomeProbeContract

/**
 * TEMPORARY probe, reverted in the next commit.
 *
 * `feature:auth:*` may name its own sub-modules and any other feature's navigation module.
 * [HomeProbeContract] lives in another feature's domain layer, which is private, so
 * `checkModuleBoundary` has to reject this import. If it did not, two features could couple
 * through their domain layers and stop being removable independently - while the permitted
 * `feature.*.navigation.` pattern kept looking like it covered the cross-feature case.
 *
 * The import resolves and is used, so the boundary check is the only possible failure here.
 */
internal class AuthBoundaryProbe : HomeProbeContract {
    override val probeName: String = "auth-boundary-probe"
}
