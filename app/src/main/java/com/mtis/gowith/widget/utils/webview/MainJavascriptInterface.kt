package com.mtis.gowith.widget.utils.webview

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.PickVisualMediaRequest.Builder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.mtis.gowith.BuildConfig
import com.mtis.gowith.base.BaseActivity
import com.mtis.gowith.common.GsonConverter
import com.mtis.gowith.domain.model.ImageFile
import com.mtis.gowith.domain.model.TMapLaunch
import com.mtis.gowith.domain.model.VersionInfo
import com.mtis.gowith.view.activity.MainActivity
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

    private lateinit var mListener:MainJavaScriptInterfaceListener

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
        //  To do
        mWebView?.sendResponse(GsonConverter.resultJson("ok"), callbackId)
//        LoaderManager.getInstance(this@MainActivity).initLoader<Cursor>(0, null, mContactsLoader)
    }

    // Get photo file path/uri
    @JavascriptInterface
    fun requestImageFileFromWeb(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestImageFileFromWeb] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )
        mWebView?.sendResponse(GsonConverter.resultJson("ok"), callbackId)
        pickPhoto(true)
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
        var vi = VersionInfo(
            osType = "Android",
            appVersionCode = "${BuildConfig.VERSION_CODE}",
            appVersionName = "${BuildConfig.VERSION_NAME}",
            splashID = "1234567890",
            fcmToken = ft
        )

        val re = Gson().toJson(vi)

        mWebView?.sendResponse(re, callbackId)
    }

    // Get Tmap launch
    @JavascriptInterface
    fun requestTmapLaunchFromWeb(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestTmapLaunchFromWeb] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        // To do
        try {
            val g = Gson()
            val cd: TMapLaunch = g.fromJson(data, TMapLaunch::class.java)
            if (cd.goalLat == null || cd.goalLat.isEmpty() || cd.goalLong == null || cd.goalLong.isEmpty()) {
                mWebView?.sendResponse(GsonConverter.resultJson("fail"), callbackId)
                return
            }
            if (cd.startLat != null && cd.startLong != null) {
                val uri = "tmap://route?startx=" + cd.startLong.toString() +
                        "&starty=" + cd.startLat.toString() +
                        "&goalx=" + cd.goalLong.toString() +
                        "&goaly" + cd.goalLat
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mContext?.startActivity(intent)
                mWebView?.sendResponse(GsonConverter.resultJson("ok"), callbackId)
            } else {
                val uri = "tmap://route?goalx=" + cd.goalLong.toString() + "&goaly" + cd.goalLat
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mContext?.startActivity(intent)
                mWebView?.sendResponse(GsonConverter.resultJson("ok"), callbackId)
            }
        } catch (ae: ActivityNotFoundException) {
            mWebView?.sendResponse(GsonConverter.resultJson("fail"), callbackId)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.skt.tmap.ku")
            intent.setPackage("com.android.vending")
            mContext?.startActivity(intent)
        } catch (je: JsonSyntaxException) {
            mWebView?.sendResponse(GsonConverter.resultJson("fail"), callbackId)
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
        try {
            val intent = Intent(Settings.ACTION_NFC_SETTINGS)
            mContext?.startActivity(intent)
            mWebView?.sendResponse(GsonConverter.resultJson("ok"), callbackId)
        } catch (ae: ActivityNotFoundException) {
            mWebView?.sendResponse(GsonConverter.resultJson("fail"), callbackId)
        }
    }

    // BlueTooth 데이터 전달
    @JavascriptInterface
    fun requestBleBeaconTag(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestBleBeaconTag] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        // 구현부
        try {
            mWebView?.sendResponse(GsonConverter.resultJson("ok"), callbackId)
        } catch (ae: ActivityNotFoundException) {
            mWebView?.sendResponse(GsonConverter.resultJson("fail"), callbackId)
        }
    }

    // 좌표 전달
    @JavascriptInterface
    fun requestCurrentLocation(data: String, callbackId: String) {
        Log.e(
            TAG, "[requestBleBeaconTag] " + data + ", callbackId= " +
                    callbackId + ", thread= " + Thread.currentThread().name
        )

        // 구현부

        try {
            mWebView?.sendResponse(
                GsonConverter.resultJson("ok,lat:${mCurLocation?.latitude},lng:${mCurLocation?.longitude}"),
                callbackId
            )
        } catch (ae: ActivityNotFoundException) {
            mWebView?.sendResponse(GsonConverter.resultJson("fail"), callbackId)
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

}


