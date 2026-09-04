package com.ytapps.composetemplate.feature.home.domain

/**
 * TEMPORARY probe symbol, reverted in the next commit.
 *
 * Exists only so that the cross-feature import in `feature:auth:domain` resolves. Without a
 * real public symbol to import, a failing build could be an unresolved reference rather than
 * the boundary check firing, and the two are indistinguishable from the check run summary.
 */
interface HomeProbeContract {
    val probeName: String
}
