package com.mtis.gowith.widget.utils.webview

import android.bluetooth.BluetoothManager
import android.content.*
import android.location.Location
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.tech.NfcA
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.mtis.gowith.BuildConfig
import com.mtis.gowith.R
import com.mtis.gowith.common.BleBeaconService
import com.mtis.gowith.common.GsonConverter
import com.mtis.gowith.common.NfcHceService
import com.mtis.gowith.domain.model.TMapLaunch
import com.mtis.gowith.domain.model.webinterface.NotiInterface
import com.mtis.gowith.domain.model.webinterface.call.VersionInfoInterface
import com.mtis.gowith.domain.model.webinterface.call.CommonInterface
import com.mtis.gowith.domain.model.webinterface.response.CommonResponseInterface
import com.mtis.gowith.domain.model.webinterface.call.LocationInterface
import com.mtis.gowith.domain.model.webinterface.call.StateInterface
import com.mtis.gowith.domain.model.webinterface.response.CallPhoneResponseInterface
import com.mtis.gowith.domain.model.webinterface.response.MemberIdResponseInterface
import com.mtis.gowith.domain.model.webinterface.response.TMapResponseInterface
import com.mtis.gowith.widget.utils.P
import com.mtis.gowith.widget.utils.webview.jsbridge.BridgeWebView
import com.mtis.gowith.widget.utils.webview.jsbridge.OnBridgeCallback

class MainJavascriptInterface : BridgeWebView.BaseJavascriptInterface {
    private val TAG: String = "MainInterface"

    private var mContext: Context? = null

    private var mWebView: BridgeWebView? = null

    private var mCurLocation: Location? = null

    interface MainJavaScriptInterfaceListener {
        fun singlePickPhoto()
        fun multiPickPhoto()
    }

    private lateinit var mListener: MainJavaScriptInterfaceListener

    constructor(
        callbacks: Map<String?, OnBridgeCallback?>?,
        webView: BridgeWebView?,
        listener: MainJavaScriptInterfaceListener,
        context: Context
    ) : super(
        callbacks
    ) {
        mWebView = webView
        mContext = context
        mListener = listener
    }


    constructor(callbacks: Map<String?, OnBridgeCallback?>?) : super(callbacks) {}


    // location 전달
    fun recivedLocation(location: Location?) {
        mCurLocation = location
    }

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

        val obj = CommonInterface(true, null)
        mWebView?.sendResponse(
            Gson().toJson(obj),
            callbackId
        )

//        LoaderManager.getInstance(this@MainActivity).initLoader<Cursor>(0, null, mContactsLoader)
    }

//    // Get photo file path/uri
//    @JavascriptInterface
//    fun requestImageFileFromWeb(data: String, callbackId: String) {
//        Log.e(
//            TAG, "[requestImageFileFromWeb] " + data + ", callbackId= " +
//                    callbackId + ", thread= " + Thread.currentThread().name
//        )
//        mWebView?.sendResponse(GsonConverter.resultJson("ok"), callbackId)
//        pickPhoto(true)
//    }

    @JavascriptInterface
    fun requestFCMToken(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestFCMToken] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        var ft: String? = P.getFcmToken(mContext)
        if (ft == null) ft = ""

        val obj = CommonInterface(true, ft)
        val re = Gson().toJson(obj)
        mWebView?.sendResponse(re, callbackId)
    }

    @JavascriptInterface
    fun recivedMemberId(data:String, callbackId: String) {
        Log.e(TAG, "[recivedMemberId] " + data + ", callbackId= " +
                callbackId + ", thread= " + Thread.currentThread().name)

        val convert = Gson().fromJson(data,MemberIdResponseInterface::class.java)
        P.setMemberId(mContext,convert.memberId)
        NfcHceService.setMemberId(Integer.getInteger(convert.memberId))
    }

    @JavascriptInterface
    fun requestLogoutEvent(data:String, callbackId: String) {
        Log.e(TAG, "[recivedLogoutEvent] " + data + ", callbackId= " +
                callbackId + ", thread= " + Thread.currentThread().name)

        P.setMemberId(mContext,"0")
        NfcHceService.setMemberId(0)
    }

    // Request version info
    @JavascriptInterface
    fun requestVersionInfoFromWeb(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestVersionInfoFromWeb] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        var ft: String? = P.getFcmToken(mContext)
        if (ft == null) ft = ""

        // To do
        var vi = VersionInfoInterface(
            osType = "Android",
            appVersionCode = "${BuildConfig.VERSION_CODE}",
            appVersionName = "${BuildConfig.VERSION_NAME}",
            splashID = "1234567890",
            fcmToken = ft
        )

        val obj = CommonInterface(true, vi)

        val re = Gson().toJson(obj)

        mWebView?.sendResponse(re, callbackId)
    }

    // Get Tmap launch
    @JavascriptInterface
    fun requestTmapLaunchFromWeb(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestTmapLaunchFromWeb] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        try {
            val g = Gson()
            val response: TMapResponseInterface =
                g.fromJson(data, TMapResponseInterface::class.java)
            val cd: TMapLaunch = response.data

            if (cd.goalLat == null || cd.goalLat.isEmpty() || cd.goalLong == null || cd.goalLong.isEmpty()) {
                mWebView?.sendResponse(GsonConverter.resultJson("fail"), callbackId)
                return
            }
            if (cd.startLat != null && cd.startLong != null) {
                val uri = "tmap://route?startx=" + cd.startLong.toString() +
                        "&starty=" + cd.startLat.toString() +
                        "&goalx=" + cd.goalLong.toString() +
                        "&goaly=" + cd.goalLat
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mContext?.startActivity(intent)

                val obj = CommonInterface(true, null)
                mWebView?.sendResponse(
                    Gson().toJson(obj),
                    callbackId
                )
            } else {
                val uri = "tmap://route?goalx=" + cd.goalLong.toString() + "&goaly=" + cd.goalLat
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mContext?.startActivity(intent)

                val obj = CommonInterface(true, null)
                mWebView?.sendResponse(
                    Gson().toJson(obj),
                    callbackId
                )
            }
        } catch (ae: ActivityNotFoundException) {
            val obj = CommonInterface(false, null)
            mWebView?.sendResponse(
                Gson().toJson(obj),
                callbackId
            )

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.skt.tmap.ku")
            intent.setPackage("com.android.vending")
            mContext?.startActivity(intent)
        } catch (je: JsonSyntaxException) {
            val obj = CommonInterface(false, null)
            mWebView?.sendResponse(
                Gson().toJson(obj),
                callbackId
            )
        }
    }

    // NFC On/Off
    @JavascriptInterface
    fun requestNfcLaunchFromWeb(data: String, callbackId: String) {
        // To do
        Log.e(
            TAG, "[requestNfcLaunchFromWeb] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )
        /*
         * Jiny
         * 여기서는 코드 수정은 없습니다. NFC가 On이든 Off이든, 버튼을 클릭하면,
         * ACTION_NFC_SETTINGS 설정 뷰로 진행합니다.
         *
         * 앱이 실행할때, 웹앱 파트에서, 현재 NFC 장치가 On or Off 인지 파악해서 전달해야겠습니다.
         * NFC On 이면 enable 디자인, Off 이면 현재 disable을 의미하는 디자인이 필요합니다.
         * 참고로, Java의 경우 다음과 같은 방식으로 확인할 수 있습니다.
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
            if (nfcAdapter != null)
                result = nfcAdapter.isEnabled();
         */

        val nfcAdapter: NfcAdapter = NfcAdapter.getDefaultAdapter(mContext)

        try {

            var obj = CommonInterface(true, null)

            if (nfcAdapter.isEnabled) {
                obj.data = StateInterface(true)
            } else {
                obj.data = StateInterface(false)
            }


            mWebView?.sendResponse(
                Gson().toJson(obj),
                callbackId
            )
        } catch (ae: ActivityNotFoundException) {
            val obj = CommonInterface(false, StateInterface(false))
            mWebView?.sendResponse(
                Gson().toJson(obj),
                callbackId
            )
        }
    }

    // NFC On/Off
    @JavascriptInterface
    fun requestNfcSettingOpenFromWeb(data: String, callbackId: String) {

        Log.e(
            TAG, "[requestNfcSettingOpenFromWeb] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        val intent = Intent(Settings.ACTION_NFC_SETTINGS)
        mContext?.startActivity(intent)
    }

    fun sendCurrentNfcState(state: String) {
        val javascriptCommand = "javascript:window.NativeInterface.currentNfcState(\"${state}\")"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView?.evaluateJavascript(javascriptCommand, null)
        } else {
            mWebView?.loadUrl(javascriptCommand)
        }
    }

    // BlueTooth On/Off 상태 전달
    @JavascriptInterface
    fun requestBleState(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestBleState] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        val bluetoothManager =
            mContext?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.getAdapter()

        when (bluetoothAdapter.isEnabled) {
            true -> {
                val obj = CommonInterface(true, StateInterface(true))
                mWebView?.sendResponse(
                    Gson().toJson(obj),
                    callbackId
                )
            }
            false -> {
                val obj = CommonInterface(false, StateInterface(false))
                mWebView?.sendResponse(
                    Gson().toJson(obj),
                    callbackId
                )
            }
        }

    }

    // BlueTooth 데이터 전달
    @JavascriptInterface
    fun requestBleBeaconTag(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestBleBeaconTag] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        /*
         * Jiny
         * 버튼 클릭시 로그인을 하지 않을 경우, 간단히 로그인을 진행한 다음 사용할 수 있다는 출력이 필요합니다.
         * 로그인된 상태에서 버튼을 클릭하면, 2초 동안 beacon이 전송됩니다. (2초 후 서비스 자동 종료)
         * BleBeaconService는 자체가 별개의 thread 이므로, 전체 성능에 영향을 주지 않습니다.
         * memberId는 한시적으로 0 < memberId <= 65535 사이의 값을 사용합니다.
         *
         * 앱이 실행할때, 웹앱 파트에서, 현재 BLE 장치가 On or Off 인지 파악해서 전달해야겠습니다.
         * BLE On 이면 enable 디자인, Off 이면 현재 disable을 의미하는 디자인이 필요합니다.
         * 참고로, Java의 경우 다음과 같은 방식으로 확인할 수 있습니다.
           BluetoothAdapter bleAdapter = BluetoothAdapter.getDefaultAdapter();
           if (bleAdapter != null)
                result = bleAdapter.isEnabled();

         * 아래 코드부터는 실제 사용되는 코드이므로, 주석을 풀고 적절히 사용하십시오.
            int memberId = 0;
            try {
                JSONObject jObject = new JSONObject(data);
                memberId = jObject.getInt("memberId");
            } catch (JSONException e) {};

            if (memberId > 0 && memberId <= 65535) {
                Intent intent = new Intent(this, BleBeaconService.class);
                intent.putExtra("memberId", memberId);
                startService(intent);
            } else if (memberId <= 0) {
                Toast.makeText(MainActivity.this, "먼저 로그인을 해야합니다.", Toast.LENGTH_SHORT).show();
            } else { // memberId > 65535
                Toast.makeText(MainActivity.this, "본 시연에서는 memberId가 65535 이상 값을 갖을 경우 진행될 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
         */

        // 구현부
        try {
            val convert = Gson().fromJson(data,MemberIdResponseInterface::class.java)

            val memberId = convert.memberId
            memberId.let { id ->
                val mBleService: Intent = Intent(mContext, BleBeaconService::class.java)
                mBleService.putExtra("memberId", id.toInt())
                mContext?.startService(mBleService)

                val obj = CommonInterface(true, null)
                mWebView?.sendResponse(
                    Gson().toJson(obj),
                    callbackId
                )
            }

        } catch (ae: ActivityNotFoundException) {
            val obj = CommonInterface(false, null)
            mWebView?.sendResponse(
                Gson().toJson(obj),
                callbackId
            )
        }
    }

    // 좌표 전달
    @JavascriptInterface
    fun requestCurrentLocation(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestCurrentLocation] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        // 구현부
        try {
            mCurLocation?.let {
                val obj = CommonInterface(true, LocationInterface(it.latitude, it.longitude))
                mWebView?.sendResponse(
                    Gson().toJson(obj),
                    callbackId
                )
            }

        } catch (ae: ActivityNotFoundException) {
            val obj = CommonInterface(false, null)
            mWebView?.sendResponse(
                Gson().toJson(obj),
                callbackId
            )
        }
    }

//    /*
//     *
//     * Read Contact
//     *
//     */
//    // https://developer.android.com/guide/topics/providers/contacts-provider?hl=ko
//    // Defines the asynchronous callback for the contacts data loader
//    private val mContactsLoader: LoaderManager.LoaderCallbacks<Cursor> =
//        object : LoaderManager.LoaderCallbacks<Cursor> {
//            // Create and return the actual cursor loader for the contacts data
//            override fun onCreateLoader(id: Int, arg: Bundle?): Loader<Cursor> {
//                val projection = arrayOf(
//                    ContactsContract.Data.CONTACT_ID,
//                    ContactsContract.Data.DISPLAY_NAME,
//                    ContactsContract.Data.MIMETYPE,
//                    ContactsContract.Contacts.Data.DATA1,
//                    ContactsContract.Contacts.Data.DATA2,
//                    ContactsContract.Contacts.Data.DATA3,
//                    ContactsContract.Data.PHOTO_THUMBNAIL_URI
//                )
//                val selection = (ContactsContract.Data.MIMETYPE + " IN ('"
//                        + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "')")
//                val sortOrder = ContactsContract.Data.DISPLAY_NAME_PRIMARY + " ASC" // " DESC";
//                //                    String sortOrder = ContactsContract.Data.CONTACT_ID + " ASC"; // " DESC";
//                return CursorLoader(
//                    mContext,
//                    ContactsContract.Data.CONTENT_URI,
//                    projection, selection, null, sortOrder
//                )
//            }
//
//            // When the system finishes retrieving the Cursor through the CursorLoader,
//            // a call to the onLoadFinished() method takes place.
//            override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
//                val cl = ContactList()
//                cl.contacts = ArrayList<Any>()
//                val DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
//                val PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER
//                val THUMBNAI_URI = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
//                if (cursor.moveToFirst()) {
//                    do {
//                        val nameIdx = cursor.getColumnIndex(DISPLAY_NAME)
//                        val phoneIdx = cursor.getColumnIndex(PHONE_NUMBER)
//                        val thumbnailIdx = cursor.getColumnIndex(THUMBNAI_URI)
//                        val name = if (nameIdx >= 0) cursor.getString(nameIdx) else null
//                        val phone = if (phoneIdx >= 0) cursor.getString(phoneIdx) else null
//                        val thumbnailUri =
//                            if (thumbnailIdx >= 0) cursor.getString(thumbnailIdx) else null
//                        Log.e(
//                            TAG, "id= " + ContactsContract.Contacts.Data._ID +
//                                    ", name= " + name +
//                                    ", phone= " + phone +
//                                    ", thumbnailUri= " + thumbnailUri +
//                                    ", thumbnailPath= " + if (thumbnailUri != null) Uri.parse(
//                                thumbnailUri
//                            ).path else null
//                        )
//                        if (name != null && !name.isEmpty() && phone != null && !phone.isEmpty()) {
//                            if (phone.startsWith("010") || phone.contains("010")) {
//                                var isAlready = false
//                                for (c in cl.contacts) {
//                                    if (c != null && c.name != null && !c.name.isEmpty() && c.name.equals(
//                                            name
//                                        )
//                                    ) {
//                                        isAlready = true
//                                        break
//                                    }
//                                }
//                                if (isAlready == false) {
//                                    val ct: ContactList._Contact = _Contact()
//                                    ct.name = name
//                                    ct.phoneNo = phone
//                                    ct.uri = thumbnailUri
//                                    ct.path =
//                                        if (thumbnailUri != null) Uri.parse(thumbnailUri).path else null
//                                    cl.contacts.add(ct)
//                                }
//                            }
//                        }
//                    } while (cursor.moveToNext())
//                }
//                val re = Gson().toJson(cl)
//                Log.e(TAG, "[responseContactsInJs] $re")
//                mWebView!!.callHandler(
//                    "responseContactsInJs", re
//                ) { data -> Log.e(TAG, "[responseContactsInJs] $data") }
//            }
//
//            // This method is triggered when the loader is being reset
//            // and the loader data is no longer available. Called if the data
//            // in the provider changes and the Cursor becomes stale.
//            override fun onLoaderReset(loader: Loader<Cursor>) {
//                Log.e(TAG, "[requestContactsFromWeb] error: onLoaderReset")
//            }
//        }
//
//
    /**
     * pickPhoto
     * 이전 코드에서 가져온 Photo Library 여는 코드
     */
    fun pickPhoto(isMultiSelect: Boolean) {
        Log.e(TAG, "click: __pickPhotosNew")
        if (isMultiSelect == false) {
            mListener.singlePickPhoto()
        } else {
            mListener.multiPickPhoto()
        }
    }

    /**
     * Noti 처리
     */
    fun sendNotiData(data: HashMap<String, String?>) {
        try {
            mContext?.let { context ->
                val interfaceName: String? =
                    data.get(context.getString(R.string.noti_param_interface_name))
                val alarmType = data.get(context.getString(R.string.noti_param_alarm_type))
                val location = data.get(context.getString(R.string.noti_param_location))
//                val memberId = data.get(context.getString(R.string.noti_param_member_id))
                val memberName = data.get(context.getString(R.string.noti_param_member_name))
//                val sharedManagerId = data.get(context.getString(R.string.noti_param_shared_maneger_id))
//                val sharedManagerName = data.get(context.getString(R.string.noti_param_shared_manager_name))
                val rideManagerPhone = data.get(context.getString(R.string.noti_param_ride_manager_phone))
                val lineResultId = data.get(context.getString(R.string.noti_param_line_result_id))

                if(alarmType == null || location == null) {
                    throw Exception("알람 타입 또는 로케이션이 들어오지 않았습니다.")
                }

                var notiInterface: NotiInterface = NotiInterface(
                    alaramType = alarmType,
                    location = location
                )

                when (interfaceName) {
                    context.getString(R.string.web_interface_noti_get_riding_pu) -> {
                        if(memberName == null || rideManagerPhone == null || location == null) {
                            throw Exception("${interfaceName}의 파라미터에 문제가 있습니다.")
                        }

                        notiInterface.memberName = memberName
                        notiInterface.rideManagerPhone = rideManagerPhone
                        notiInterface.location = location

                        val convert = Gson().toJson(notiInterface)

                        Log.e(TAG, "json Convert result : ${convert}")

                        commonJSCall(funcName = interfaceName, jsonParam = convert)
                    }
                    context.getString(R.string.web_interface_noti_get_not_riding_pu) -> {
                        if(memberName == null || rideManagerPhone == null) {
                            throw Exception("${interfaceName}의 파라미터에 문제가 있습니다.")
                        }

                        notiInterface.memberName = memberName
                        notiInterface.rideManagerPhone = rideManagerPhone

                        val convert = Gson().toJson(notiInterface)

                        Log.e(TAG, "json Convert result : ${convert}")

                        commonJSCall(funcName = interfaceName, jsonParam = convert)
                    }
                    context.getString(R.string.web_interface_noti_get_not_getting_off_pu) -> {
                        if(memberName == null || rideManagerPhone == null) {
                            throw Exception("${interfaceName}의 파라미터에 문제가 있습니다.")
                        }

                        notiInterface.memberName = memberName
                        notiInterface.rideManagerPhone = rideManagerPhone

                        val convert = Gson().toJson(notiInterface)
                        Log.e(TAG, "json Convert result : ${convert}")

                        commonJSCall(funcName = interfaceName, jsonParam = convert)
                    }
                    context.getString(R.string.web_interface_noti_get_not_riding_mg_dv) -> {
                        if(lineResultId == null) {
                            throw Exception("${interfaceName}의 파라미터에 문제가 있습니다.")
                        }

                        notiInterface.lineResultId = lineResultId

                        val convert = Gson().toJson(notiInterface)
                        Log.e(TAG, "json Convert result : ${convert}")

                        commonJSCall(funcName = interfaceName, jsonParam = convert)
                    }
                    context.getString(R.string.web_interface_noti_get_not_getting_off_mg_dv) -> {
                        if(lineResultId == null) {
                            throw Exception("${interfaceName}의 파라미터에 문제가 있습니다.")
                        }

                        notiInterface.lineResultId = lineResultId

                        val convert = Gson().toJson(notiInterface)
                        Log.e(TAG, "json Convert result : ${convert}")

                        commonJSCall(funcName = interfaceName, jsonParam = convert)
                    }
                    else -> {
                        throw Exception("인터페이스 명이 들어오지 않았습니다.")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun commonJSCall(funcName:String, jsonParam:String) {
        val javascriptCommand = "javascript:window.PushAlarm.${funcName}(\'${jsonParam}\')"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView?.evaluateJavascript(javascriptCommand, null)
        } else {
            mWebView?.loadUrl(javascriptCommand)
        }
    }

    @JavascriptInterface
    fun requestPhoneCall(data: String,callbackId: String) {
        Log.e(
            TAG, "[RequestPhoneCall] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        val convert = Gson().fromJson(data,CallPhoneResponseInterface::class.java)

        val myUri = Uri.parse("tel:${convert.phone}")
        val myIntent = Intent(Intent.ACTION_CALL, myUri)
        mContext?.startActivity(myIntent)
    }

}


