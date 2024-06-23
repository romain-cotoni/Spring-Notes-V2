package com.project.notes_v2.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Police {
    // MONOSPACED
    COURRIER_NEW("Courier New"),
    CONSOLAS("Consolas"),
    LUCIDA_CONSOLE("Lucida Console"),
    FIRA_CODE("Fira Code"), // popular among programmers due to its ligatures
    DEJA_VU_SANS_MONO("DejaVu Sans Mono"),
    MONACO("Monaco"), // commonly used on macOS
    MENLO("Menlo"); // also used on macOS

    private final String value;
}
