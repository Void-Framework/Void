package io.voidx.exception

/**
 * Thrown when the "useCarriers" feature is enabled but not supported.
 *
 * This is a joke feature that would theoretically send IP packets via
 * Avian Carriers (yes, carrier pigeons). Currently, the server cannot
 * actually transmit data this way, so enabling `useCarriers = true`
 * will immediately throw this exception when the server is run.
 */
class NotEnoughCarriers :
    Exception(
        "There aren't enough carriers available to support transmission of the packets.",
    )
