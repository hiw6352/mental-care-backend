package com.mc.backend.assessment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record Phq9Request(
    @NotNull @Size(min = 9, max = 9) List<Integer> answers
) {}