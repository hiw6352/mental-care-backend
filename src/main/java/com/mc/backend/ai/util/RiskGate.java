package com.mc.backend.ai.util;

import com.mc.backend.ai.enums.RiskLevel;

public final class RiskGate {

    private final double threshold;

    public RiskGate(double threshold) {
        this.threshold = threshold;
    }

    public static boolean shouldEscalate(RiskLevel level, Double conf, double threshold) {
        if (level == null || conf == null) {
            return false;
        }
        boolean risky = (level == RiskLevel.CONCERN || level == RiskLevel.CRISIS);
        return risky && conf >= threshold;
    }
}
