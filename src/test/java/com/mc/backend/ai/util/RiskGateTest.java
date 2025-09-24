package com.mc.backend.ai.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mc.backend.ai.enums.RiskLevel;
import org.junit.jupiter.api.Test;

class RiskGateTest {

    @Test
    void escalateOnlyWhenRiskyAndConfident() {
        var gate = new RiskGate(0.6);
        assertTrue(gate.shouldEscalate(RiskLevel.CONCERN, 0.7, 0.6));
        assertFalse(gate.shouldEscalate(RiskLevel.OK, 0.9, 0.6));
        assertFalse(gate.shouldEscalate(RiskLevel.CRISIS, 0.3, 0.6));
    }
}
