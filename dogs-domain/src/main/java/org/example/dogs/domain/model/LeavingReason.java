package org.example.dogs.domain.model;

public enum LeavingReason {
    TRANSFERRED("Transferred"),
    RETIRED_PUT_DOWN("Retired (Put Down)"),
    KIA("KIA"),
    REJECTED("Rejected"),
    RETIRED_REHOUSED("Retired (Re-housed)"),
    DIED("Died");

    private final String displayName;

    LeavingReason(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
