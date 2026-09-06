package com.afrofuturists.rsfinancial.domain;

/**
 * Maps directly to the 10-step "what happens after submission" list in
 * the problem statement. Declaration order matters here - ordinal()
 * gives each status its position in the lifecycle, which the frontend
 * can use to render a progress tracker (step 4 of 10) without this
 * enum needing an explicit index field.
 */
public enum ClaimStatus {
    SUBMITTED,                  // 0. client has registered the claim
    CLAIM_NUMBER_ISSUED,        // 1. insurer returns a claim number and handler
    VEHICLE_ASSESSMENT_SCHEDULED, // 2. client takes vehicle for assessment
    ASSESSMENT_SUBMITTED,       // 3. assessment goes to insurer and to us
    REPAIR_QUOTES_SUBMITTED,    // 4. repair quotes go to the insurer
    REPAIR_AUTHORISED,          // 5. insurer authorises repair
    VEHICLE_DROPOFF_SCHEDULED,  // 6. client picks a date; we arrange car hire + delivery
    IN_REPAIR,                  // 7. weekly repair updates pushed to us
    HIRE_CAR_RETURNED,          // 8. we arrange collection/return of hire car
    CLOSED                      // 9. client review submitted, transaction closed
}
