package com.mtis.gowith.widget.utils

import android.content.Context
import android.util.Log
import java.io.File

object Utils {
    /**
     * 앱 캐시 지우기
     * @param context
     */
    fun clearApplicationData(context: Context) {
        val cache: File = context.getCacheDir()
        val appDir = File(cache.getParent())
        if (appDir.exists()) {
            val children: Array<String> = appDir.list()
            for (s in children) {
                //다운로드 파일은 지우지 않도록 설정
                //if(s.equals("lib") || s.equals("files")) continue;
                deleteDir(File(appDir, s))
                Log.d(
                    "test",
                    "File /data/data/" + context.getPackageName().toString() + "/" + s + " DELETED"
                )
            }
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory()) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir?.delete() ?: false
    }
}