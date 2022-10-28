package com.mtis.gowith.domain.repository

import com.mtis.gowith.domain.model.webinterface.call.VersionInfoInterface

interface GowithRepository {
    fun getVersionInfo(versionInfoInterface: VersionInfoInterface)
}