package dk.nodes.mlkitscannerlib.other

interface Contract {

    interface ProcessorOutput {
        fun onScannerResult(result: String?)
        fun onScannerError(result: String?)
    }

}