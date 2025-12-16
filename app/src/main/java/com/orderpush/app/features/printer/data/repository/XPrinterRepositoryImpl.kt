package com.orderpush.app.features.printer.data.repository

import com.orderpush.app.features.printer.data.model.XPrinter
import com.orderpush.app.features.printer.domain.PrinterRepository

class XPrinterRepositoryImpl: PrinterRepository<XPrinter> {
    override suspend fun setPrinter(printer: XPrinter) {

    }

}