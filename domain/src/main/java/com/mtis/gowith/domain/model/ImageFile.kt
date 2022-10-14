package com.mtis.gowith.domain.model

import java.net.URI

data class ImageFile(
    var files: ArrayList<_File> = arrayListOf()
) {
    data class _File(
        var uri: String = "",
        var path: String = ""
    )
}
