package com.project.notes_v2.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Right {
    READ(0,"read"),
    WRITE(1,"write"),
    SHARE(2,"share"),
    DELETE(3,"delete"),
    OWNER(4,"owner");

    private final int value;
    private final String label;
}
