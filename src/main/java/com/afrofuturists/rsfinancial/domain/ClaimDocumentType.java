package com.afrofuturists.rsfinancial.domain;

/**
 * Matches the "uploads to the cloud" list from claim registration
 * directly - vehicle/damage/road-surface photos, registration discs and
 * licenses, plus the accident sketch.
 */
public enum ClaimDocumentType {
    VEHICLE_PHOTO,
    DAMAGE_PHOTO,
    ROAD_SURFACE_PHOTO,
    REGISTRATION_DISC,
    DRIVERS_LICENSE,
    ACCIDENT_SKETCH,
    OTHER
}
