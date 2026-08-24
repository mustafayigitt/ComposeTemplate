# Runtime Integrity Signals on Android

Runtime integrity checks are useful, but they should not be treated as final security decisions.

## Problem

Root detection, debugger detection, emulator checks, and hook detection can all be bypassed by a motivated attacker. If the client makes the final decision, the attacker can patch the client.

## ComposeTemplate approach

`core:security` provides runtime signals such as debugger attachment, emulator heuristics, root indicators, hook indicators, installer source, signature mismatch, and tamper signals.

## Takeaway

Runtime integrity should raise risk awareness, not pretend to be unbreakable client-side security.
