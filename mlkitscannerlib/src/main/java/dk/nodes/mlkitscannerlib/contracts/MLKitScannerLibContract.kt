package dk.nodes.mlkitscannerlib.contracts

interface MLKitScannerLibContract {

    interface ProcessorOutput {
        fun onScannerResult(result: String?)
        fun onScannerError(result: String?)
    }

    interface CameraFragmentOutput {
        fun onScannerResult(result: String?)
        fun onScannerError(result: String?)
    }

}