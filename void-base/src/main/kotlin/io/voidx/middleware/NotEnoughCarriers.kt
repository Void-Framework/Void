package io.voidx.middleware

class NotEnoughCarriers : Exception("There isn't enough carriers available to support transmition of the packets.") {
}