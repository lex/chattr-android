package com.lex.chattr

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessage(var username: String, var message: String, var timestamp: String)
