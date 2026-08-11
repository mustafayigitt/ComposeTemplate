package com.ytapps.composetemplate.core.security

enum class SecurityFinding {
    SignatureMismatch,
    PackageNameMismatch,
    UnexpectedInstaller,
    Emulator,
    DebuggerAttached,
    RootSignals,
    HookingSignals,
}
