package com.mtis.gowith.view.activity

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mtis.gowith.R
import com.mtis.gowith.base.BaseActivity
import com.mtis.gowith.databinding.ActivityMainBinding
import com.mtis.gowith.domain.model.ImageFile
import com.mtis.gowith.viewmodel.MainViewModel
import com.mtis.gowith.widget.utils.P
import com.mtis.gowith.widget.utils.webview.MainJavascriptInterface
import com.mtis.gowith.widget.utils.webview.jsbridge.OnBridgeCallback
import dagger.hilt.android.AndroidEntryPoint
import org.xml.sax.helpers.DefaultHandler

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val TAG: String = "MainActivity"
    private val mainViewModel by viewModels<MainViewModel>()

    private lateinit var mJSInterface: MainJavascriptInterface

    override fun onResume() {
        super.onResume()

        mainViewModel.startRealTimeLocationCheck()
    }

    override fun onStop() {
        super.onStop()

        mainViewModel.stopRealTimeLocationCheck()
    }

    override fun init() {
        binding.mainWebView.isHorizontalScrollBarEnabled = false
        binding.mainWebView.settings.javaScriptEnabled = true
        binding.mainWebView.settings.setSupportZoom(false)
        binding.mainWebView.settings.builtInZoomControls = false
        binding.mainWebView.settings.displayZoomControls = false
        binding.mainWebView.settings.textZoom = 100

        binding.mainWebView.webViewClient = webViewClient
        binding.mainWebView.webChromeClient = chromeClient

        if (Build.VERSION.SDK_INT >= 19) {
            binding.mainWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            binding.mainWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        mJSInterface =
            MainJavascriptInterface(binding.mainWebView.callbacks, binding.mainWebView, jsInterfaceListener, this)
        binding.mainWebView.addJavascriptInterface(mJSInterface, "WebViewJavascriptBridge")
        binding.mainWebView.setGson(Gson())

        binding.mainWebView.loadUrl("file:///android_asset/demo.html")
        val serverUrl: String = P.getServerUrl(this)
        if (serverUrl == null || serverUrl == "" || !serverUrl.startsWith("http")) shortShowToast("서버 주소가 적합하게 설정되더 있지 않습니다. 현재 서버 주소: $serverUrl")
        else binding.mainWebView.loadUrl(
            serverUrl
        )

        setObserver()
    }

    fun setObserver() {
        lifecycleScope.launchWhenCreated {
            mainViewModel.realTimeLocation.collect { loc ->
                Log.e(TAG, "loc : lat -> ${loc?.latitude} lng -> ${loc?.longitude} ")
                binding.tvMainDebug.setText("loc : lat -> ${loc?.latitude} lng -> ${loc?.longitude} ")
                mJSInterface.recivedLocation(loc)
            }
        }
    }

    private val webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }
    }

    private val chromeClient = object : WebChromeClient() {

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            consoleMessage?.let {
                Log.e(TAG, consoleMessage.message())
            }
            return true
        }
    }

    private val handler: DefaultHandler = DefaultHandler()


    /**
     * Javascript Interface Listener 구현
     * 기능 중 Activity와 연결 되어야 하는 기능을 구현하기 위해 Listener 구현
     */

    private val jsInterfaceListener:MainJavascriptInterface.MainJavaScriptInterfaceListener = object : MainJavascriptInterface.MainJavaScriptInterfaceListener {
        override fun singlePickPhoto() {
            pickMedia?.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    .build()
            )
        }

        override fun multiPickPhoto() {
            pickMultipleMedia?.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    .build()
            )
        }

    }

    var pickMedia = registerForActivityResult<PickVisualMediaRequest, Uri>(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        // Callback is invoked after the user selects a media item or closes the photo picker.
        val imf = ImageFile()
        if (uri != null) {
            val f: ImageFile._File = ImageFile._File()
            f.uri = uri.toString()
            f.path = uri.path.toString()
            imf.files.add(f)
        }
        val re = Gson().toJson(imf)
        Log.e(TAG, "[responseImageFileInJs] $re")
        binding.mainWebView.callHandler("responseImageFileInJs", re, object : OnBridgeCallback {
            override fun onCallBack(data: String) {
                Log.e(TAG, "[responseImageFileInJs] $data")
            }
        })
    }



    private val MAX_NUM_PHOTOS = 10

    var pickMultipleMedia = registerForActivityResult<PickVisualMediaRequest, List<Uri>>(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_NUM_PHOTOS)
    ) { uris: List<Uri> ->
        // Callback is invoked after the user selects media items or closes the photo picker.
        val imf = ImageFile()
        if (!uris.isEmpty()) {
            for (i in uris.indices) {
                val f: ImageFile._File = ImageFile._File()
                f.uri = uris[i].toString()
                f.path = uris[i].path.toString()
                imf.files.add(f)
            }
        }
        val re = Gson().toJson(imf)
        Log.e(TAG, "[responseImageFileInJs] $re")
        binding.mainWebView.callHandler("responseImageFileInJs", re, object : OnBridgeCallback {
            override fun onCallBack(data: String) {
                Log.e(TAG, "[responseImageFileInJs] $data")
            }
        })
    }
}

