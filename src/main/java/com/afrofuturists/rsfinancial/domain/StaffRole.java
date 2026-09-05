package com.afrofuturists.rsfinancial.domain;

/**
 * The three staff roles evidenced so far: advisers give advice and hold
 * assigned clients; the compliance officer (the FAIS Disclosure names a
 * specific individual in that role) needs broader read access for
 * oversight; admin is a technical/superuser role for account
 * provisioning. Client-facing portal access, if that gets built later,
 * is deliberately not a value here - clients are not staff users (see
 * the note in the V1 migration).
 */
public enum StaffRole {
    ADVISER,
    COMPLIANCE_OFFICER,
    ADMIN
}
