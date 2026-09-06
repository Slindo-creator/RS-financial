package com.afrofuturists.rsfinancial.domain;

/**
 * Who a reminder goes to when it fires - matches the problem statement's
 * examples directly: valuation certs go to us AND the client, license
 * expiry to the client alone, annual review to us alone.
 */
public enum RecipientType {
    ADVISER,
    CLIENT,
    BOTH
}
