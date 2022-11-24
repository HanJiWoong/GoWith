package com.mtis.gowith.view.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.*
import android.provider.MediaStore
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
import com.mtis.gowith.common.NfcHceService
import com.mtis.gowith.databinding.ActivityMainBinding
import com.mtis.gowith.domain.model.ImageFile
import com.mtis.gowith.viewmodel.MainViewModel
import com.mtis.gowith.widget.utils.P
import com.mtis.gowith.widget.utils.webview.MainJavascriptInterface
import com.mtis.gowith.widget.utils.webview.jsbridge.OnBridgeCallback
import dagger.hilt.android.AndroidEntryPoint
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val TAG: String = "MainActivity"
    private val mainViewModel by viewModels<MainViewModel>()

    private lateinit var mJSInterface: MainJavascriptInterface

    override fun onResume() {
        super.onResume()

        mainViewModel.startRealTimeLocationCheck()


        val nfcAdapter: NfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter.isEnabled) {
            mJSInterface.sendCurrentNfcState("true")
        } else {
            mJSInterface.sendCurrentNfcState("false")
        }
    }

    /*
     * Jiny
     *
     * NFC 모듈은 앱이 실행되고 있는 동안에는 계속 유지되어야 합니다.
     * 따라서, 가장 기본이 되는 액티비티에서 startService()를 실행해주고,
     * 앱 종료할때 stopService()을 호출합니다.
     * startService()가 실행되기 전에, 현재 접속한 사용자의 memberId의 값을 확보해야 합니다.
     * 만약 아직 로그인하기 전이고, 앱 구동 중 로그인했다면, NfcHceService에 memberId를 전달해야합니다.
     * 앱 구동 중 로그인했을때, memberId를 전달하는 방법은 현재 코드에 적용되어 있지 않지만, 적용 부탁합니다.
     * memberId는 한시적으로 0 < memberId <= 65535 사이의 값을 사용합니다.
     * 아래 코드를 참고하여, 적절한 곳에 위치해 주십시오.
     *
        private Intent mHceService;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            int memberId = 65535;
            mHceService = new Intent(this, NfcHceService.class);
            mHceService.putExtra("memberId", memberId);
            startService(mHceService);
        }

        @Override
        protected void onDestroy() {
            if (mHceService != null)
                stopService(mHceService);
            super.onDestroy();
        }

     */

    private lateinit var mHceService: Intent

    override fun onStart() {
        super.onStart()

        val memberId = 65535
        mHceService = Intent(this@MainActivity, NfcHceService::class.java)
        mHceService.putExtra("memberId", memberId)
        startService(mHceService)

        registerReceiver(nfcReceiver, IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED))
        registerReceiver(
            pushNotiReciver,
            IntentFilter(getString(R.string.str_intent_filer_action_noti))
        )
    }

    override fun onStop() {
        super.onStop()

        stopService(mHceService)

        mainViewModel.stopRealTimeLocationCheck()

        unregisterReceiver(nfcReceiver)
        unregisterReceiver(pushNotiReciver)
    }

    // foreground push noti data reciver
    private val pushNotiReciver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val noti_data: HashMap<String, String?>? = intent.getSerializableExtra(
                    getString(R.string.str_intent_extra_noti_data)
                ) as HashMap<String, String?>?



                noti_data?.let {

                    val name = noti_data.get(getString(R.string.noti_param_interface_name))

                    if (name != null) {
                        collectNotiData(it)
                        intent.putExtra(
                            getString(R.string.str_intent_extra_noti_data),
                            kotlin.collections.HashMap<String, String?>()
                        )
                    }
                }
            }
        }
    }

    fun collectNotiData(notiData: HashMap<String, String?>) {
        mJSInterface.sendNotiData(notiData)
    }

    val nfcReceiver = object : BroadcastReceiver() {
        override fun onReceive(con: Context?, intent: Intent) {
            val action = intent.action

            if (action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) {
                val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
                when (state) {
                    NfcAdapter.STATE_OFF -> {
                        Log.e(TAG, "비활성화")


                        mJSInterface.sendCurrentNfcState("false")
                    }
                    NfcAdapter.STATE_TURNING_OFF -> {
                        Log.e(TAG, "비활성화 전환")
                    }
                    NfcAdapter.STATE_ON -> {
                        Log.e(TAG, "활성화")

                        mJSInterface.sendCurrentNfcState("true")
                    }
                    NfcAdapter.STATE_TURNING_ON -> {
                        Log.e(TAG, "활성화 전환")
                    }
                    else -> { /* NFC 카드모드 활성화 등 */
                        if (state == 5) {
                            Log.e(TAG, "카드모드 활성화")

                            mJSInterface.sendCurrentNfcState("true")
                        }
                    }
                }
            }
        }
    }

    val imgPickLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data

                if (intent == null) { //바로 사진을 찍어서 올리는 경우
                    val results = arrayOf(Uri.parse(cameraPath))
                    mWebViewImageUpload!!.onReceiveValue(results!!)
                } else { //사진 앱을 통해 사진을 가져온 경우
                    val results = intent!!.data!!
                    mWebViewImageUpload!!.onReceiveValue(arrayOf(results!!))
                }
            } else { //취소 한 경우 초기화
                mWebViewImageUpload!!.onReceiveValue(null)
                mWebViewImageUpload = null
            }
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
            MainJavascriptInterface(
                binding.mainWebView.callbacks,
                binding.mainWebView,
                jsInterfaceListener,
                this
            )
        binding.mainWebView.addJavascriptInterface(mJSInterface, "WebViewJavascriptBridge")
        binding.mainWebView.setGson(Gson())

        binding.mainWebView.loadUrl("file:///android_asset/demo.html")

        val device: MutableMap<String, String> = HashMap()
        device["gwithappkey"] = "dpaxltm1@#"

        val serverUrl: String = P.getServerUrl(this)
        if (serverUrl == null || serverUrl == "" || !serverUrl.startsWith("http")) shortShowToast("서버 주소가 적합하게 설정되더 있지 않습니다. 현재 서버 주소: $serverUrl")
        else binding.mainWebView.loadUrl(
            serverUrl, device
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

            Log.e(TAG, "********** Start Page Loading => " + url.toString())
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            Log.e(TAG, "********** End Page Loading => " + url.toString())

            if (url != null && url.equals("https://app.gwith.co.kr/")) {
                val noti_data: HashMap<String, String?>? = intent.getSerializableExtra(
                    getString(R.string.str_intent_extra_noti_data)
                ) as HashMap<String, String?>?

                noti_data?.let {

                    val name = noti_data.get(getString(R.string.noti_param_interface_name))

                    if (name != null) {
                        collectNotiData(it)
                        intent.putExtra(
                            getString(R.string.str_intent_extra_noti_data),
                            kotlin.collections.HashMap<String, String?>()
                        )
                    }
                }
            }
        }
    }

    var cameraPath = ""
    var mWebViewImageUpload: ValueCallback<Array<Uri>>? = null

    private val chromeClient = object : WebChromeClient() {

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            consoleMessage?.let {
                Log.e(TAG, consoleMessage.message())
            }
            return true
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            try {
                mWebViewImageUpload = filePathCallback!!
                var takePictureIntent: Intent?
                takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    var photoFile: File?

                    photoFile = createImageFile()
                    takePictureIntent.putExtra("PhotoPath", cameraPath)

                    if (photoFile != null) {
                        cameraPath = "file:${photoFile.absolutePath}"
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                    } else takePictureIntent = null
                }
                val contentSelectionIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                contentSelectionIntent.type = "image/*"

                var intentArray: Array<Intent?>

                if (takePictureIntent != null) intentArray = arrayOf(takePictureIntent)
                else intentArray = takePictureIntent?.get(0)!!

                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "사용할 앱을 선택해주세요.")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                imgPickLauncher.launch(chooserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
    }

    fun createImageFile(): File? {
        @SuppressLint("SimpleDateFormat")
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "img_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }


    private val handler: DefaultHandler = DefaultHandler()


    /**
     * Javascript Interface Listener 구현
     * 기능 중 Activity와 연결 되어야 하는 기능을 구현하기 위해 Listener 구현
     */

    private val jsInterfaceListener: MainJavascriptInterface.MainJavaScriptInterfaceListener =
        object : MainJavascriptInterface.MainJavaScriptInterfaceListener {
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
//        Log.e(TAG, "[responseImageFileInJs] $re")
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
//        Log.e(TAG, "[responseImageFileInJs] $re")
        binding.mainWebView.callHandler("responseImageFileInJs", re, object : OnBridgeCallback {
            override fun onCallBack(data: String) {
                Log.e(TAG, "[responseImageFileInJs] $data")
            }
        })
    }
}

