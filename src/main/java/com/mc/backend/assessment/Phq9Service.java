package com.mc.backend.assessment;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class Phq9Service {
    public int sumScore(List<Integer> answers) {
        return answers.stream().mapToInt(Integer::intValue).sum();
    }
    public String severity(int score) {
        if (score <= 4)  return "MINIMAL";
        if (score <= 9)  return "MILD";
        if (score <= 14) return "MODERATE";
        if (score <= 19) return "MODERATELY_SEVERE";
        return "SEVERE";
    }
}
