package com.ytapps.composetemplate.core.security

data class SecurityReport(
    val action: SecurityAction,
    val findings: Set<SecurityFinding>,
) {
    val isBlocked: Boolean = action == SecurityAction.Block
}
