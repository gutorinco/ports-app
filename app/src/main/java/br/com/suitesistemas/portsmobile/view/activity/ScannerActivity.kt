package br.com.suitesistemas.portsmobile.view.activity

import android.app.Activity
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.suitesistemas.portsmobile.R
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_scanner.*
import me.dm7.barcodescanner.core.CameraUtils
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        hideActionBar()
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        scanner_view.setResultHandler(this)
        scanner_view.setBorderColor(R.color.colorAccent)
        scanner_view.setLaserColor(R.color.colorSecondary)
        scanner_view.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scanner_view.stopCamera()
        val camera = CameraUtils.getCameraInstance()
        if (camera != null)
            (camera as Camera).release()
    }

    override fun handleResult(rawResult: Result?) {
        rawResult?.text?.let {
            with(Intent()) {
                putExtra("scanner_response", it)
                setResult(Activity.RESULT_OK, this)
                finish()
            }
        }
        scanner_view.resumeCameraPreview(this)
    }

}
