package com.inventiapp.stocktrack.inventory.interfaces.rest.resources;

/**
 * Resource record returned by the on-demand expiration scan.
 *
 * @param created the number of new PENDING alerts created by this scan run
 */
public record ExpirationScanResultResource(int created) {}
