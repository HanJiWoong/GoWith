package com.mtis.gowith.view.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.loader.app.LoaderManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.mtis.gowith.BuildConfig
import com.mtis.gowith.R
import com.mtis.gowith.base.BaseActivity
import com.mtis.gowith.databinding.ActivityMainBinding
import com.mtis.gowith.jsbridge.BridgeWebView
import com.mtis.gowith.jsbridge.OnBridgeCallback
import com.mtis.gowith.viewmodel.MainViewModel
import com.mtis.gowith.widget.utils.Utils.BASE_URL
import dagger.hilt.android.AndroidEntryPoint
import org.xml.sax.helpers.DefaultHandler

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun init() {
        binding.mainWebView.isHorizontalScrollBarEnabled = false
        binding.mainWebView.settings.javaScriptEnabled = true
        binding.mainWebView.settings.setSupportZoom(false)
        binding.mainWebView.settings.builtInZoomControls = false
        binding.mainWebView.settings.displayZoomControls = false
        binding.mainWebView.settings.textZoom = 100

        binding.mainWebView.webViewClient = webViewClient
        binding.mainWebView.webChromeClient = chromeClient

        binding.mainWebView.addJavascriptInterface(MainJavascriptInterface(binding.mainWebView.callbacks,binding.mainWebView), "WebViewJavascriptBridge")
        binding.mainWebView.setGson(Gson())

        binding.mainWebView.loadUrl(BASE_URL)


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

    }

    private val handler: DefaultHandler = DefaultHandler()
}

class MainJavascriptInterface : BridgeWebView.BaseJavascriptInterface {
    private val TAG: String = "MainInterface"

    private var mWebView: BridgeWebView? = null

    constructor(callbacks: Map<String?, OnBridgeCallback?>?, webView: BridgeWebView?) : super(
        callbacks
    ) {
        mWebView = webView
    }

    constructor(callbacks: Map<String?, OnBridgeCallback?>?) : super(callbacks) {}

    // receive case 1
    override fun defaultRespons(data: String): String {
        Log.e(TAG, "[defaultRespons] $data")
        return "it is default response (callback message)"
    }

    // receive case 2
    @JavascriptInterface
    fun submitFromWeb(data: String, callbackId: String) {
        Log.e(
            TAG, "[submitFromWeb] " + data + ", callbackId= " + callbackId +
                    ", thread name= " + Thread.currentThread().name
        )
        mWebView?.sendResponse("submitFromWeb response", callbackId)
    }

    // Request contacts
    @JavascriptInterface
    fun requestContactsFromWeb(data: String, callbackId: String) {

        Log.e(
            TAG, "[requestContactsFromWeb] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )
        //  To do
//        mWebView.sendResponse(resultJson("ok"), callbackId)
//        LoaderManager.getInstance(this@MainActivity).initLoader<Cursor>(0, null, mContactsLoader)
    }

    // Get photo file path/uri
    @JavascriptInterface
    fun requestImageFileFromWeb(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestImageFileFromWeb] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )
//        mWebView.sendResponse(resultJson("ok"), callbackId)
//        pickPhoto(true) To do
    }

    // Request version info
    @JavascriptInterface
    fun requestVersionInfoFromWeb(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestVersionInfoFromWeb] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        // To do
//        val vi = VersionInfo()
//        vi.osType = "Android"
//        vi.appVersionCode = BuildConfig.VERSION_CODE + ""
//        vi.appVersionName = BuildConfig.VERSION_NAME + ""
//        vi.splashID = "1234567890"
//        val ft: String = ""//P.getFcmToken(this@MainActivity)
//        if (ft == null) vi.fcmToken = "" else vi.fcmToken = ft
//        val re = Gson().toJson(vi)
//
//        mWebView?.sendResponse(re, callbackId)
    }

    // Get Tmap launch
    @JavascriptInterface
    fun requestTmapLaunchFromWeb(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestTmapLaunchFromWeb] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        // To do
//        try {
//            val g = Gson()
//            val cd: TmapLaunch = g.fromJson(data, TmapLaunch::class.java)
//            if (cd.goalLat == null || cd.goalLat.isEmpty() || cd.goalLong == null || cd.goalLong.isEmpty()) {
//                addLog(false, "goalLat is empty OR goalLong is empty")
//                mWebView.sendResponse(resultJson("fail"), callbackId)
//                return
//            }
//            if (cd.startLat != null && cd.startLong != null) {
//                val uri = "tmap://route?startx=" + cd.startLong.toString() +
//                        "&starty=" + cd.startLat.toString() +
//                        "&goalx=" + cd.goalLong.toString() +
//                        "&goaly" + cd.goalLat
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//                mWebView.sendResponse(resultJson("ok"), callbackId)
//            } else {
//                val uri = "tmap://route?goalx=" + cd.goalLong.toString() + "&goaly" + cd.goalLat
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//                mWebView.sendResponse(resultJson("ok"), callbackId)
//            }
//        } catch (ae: ActivityNotFoundException) {
//            addLog(false, "ActivityNotFoundException: " + ae.message.toString())
//            mWebView.sendResponse(resultJson("fail"), callbackId)
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.skt.tmap.ku")
//            intent.setPackage("com.android.vending")
//            startActivity(intent)
//        } catch (je: JsonSyntaxException) {
//            addLog(false, "JSON parsing error: " + je.message.toString())
//            mWebView.sendResponse(resultJson("fail"), callbackId)
//        }
    }

    // NFC On/Off
    @JavascriptInterface
    fun requestNfcLaunchFromWeb(data: String, callbackId: String) {
        // To do
//        Log.e(TAG, "[requestNfcLaunchFromWeb] " + data + ", callbackId= " +
//                    callbackId + ", thread= " + Thread.currentThread().name
//        )
//        try {
//            val intent = Intent(Settings.ACTION_NFC_SETTINGS)
//            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent)
//            mWebView.sendResponse(resultJson("ok"), callbackId)
//        } catch (ae: ActivityNotFoundException) {
//            addLog(false, "ActivityNotFoundException: " + ae.message.toString())
//            mWebView.sendResponse(resultJson("fail"), callbackId)
//        }
    }
}