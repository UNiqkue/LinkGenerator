package com.nik.yourcodereview.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class Error {
    @NotNull
    @Valid
    private String message;
}
