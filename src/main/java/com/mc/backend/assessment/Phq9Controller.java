package com.mc.backend.assessment;

import com.mc.backend.assessment.dto.Phq9Request;
import com.mc.backend.assessment.dto.Phq9Response;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assessments")
public class Phq9Controller {
    private final Phq9Service service;
    public Phq9Controller(Phq9Service service) { this.service = service; }

    @PostMapping("/phq9")
    public Phq9Response submit(@RequestBody @Valid Phq9Request req) {
        int score = service.sumScore(req.answers());
        String severity = service.severity(score);
        return new Phq9Response(score, severity);
    }
}
