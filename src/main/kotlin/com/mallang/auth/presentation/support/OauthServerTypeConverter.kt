package com.mallang.auth.presentation.support

import com.mallang.auth.domain.OauthServerType
import org.springframework.core.convert.converter.Converter

class OauthServerTypeConverter : Converter<String, OauthServerType> {

    override fun convert(source: String): OauthServerType =
            OauthServerType.fromName(source)
}
