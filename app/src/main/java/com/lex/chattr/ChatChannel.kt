package com.lex.chattr

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatChannel(var id: Int, var name: String)
