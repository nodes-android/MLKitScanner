package dk.nodes.mlkitscannerlib.contracts

interface Contract {

    interface ProcessorOutput {
        fun onScannerResult(result: String?)
        fun onScannerError(result: String?)
    }

    interface FragmentOutput {
        fun onScannerResult(result: String?)
        fun onScannerError(result: String?)
    }

}