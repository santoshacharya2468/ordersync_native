package com.orderpush.app.features.printer.domain

import com.orderpush.app.features.printer.data.model.BasePrinter

interface PrinterRepository<T: BasePrinter> {
    suspend fun setPrinter(printer: T)
}