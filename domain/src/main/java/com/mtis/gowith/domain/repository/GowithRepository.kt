package com.mtis.gowith.domain.repository

import com.mtis.gowith.domain.model.VersionInfo

interface GowithRepository {
    fun getVersionInfo(versionInfo: VersionInfo)
}