package com.lex.chattr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lex.chattr.databinding.FragmentChatChannelBinding
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.*

class ChatChannelFragment: Fragment() {

    private var _binding: FragmentChatChannelBinding? = null

    private val binding get() = _binding!!
    private lateinit var webSocket: WebSocket

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moshi: Moshi = Moshi.Builder().build()
        val chatMessageAdapter: JsonAdapter<ChatMessage> = moshi.adapter(ChatMessage::class.java)

        val client = OkHttpClient()
        val request = Request.Builder().url("ws://10.0.2.2:8000/ws/channel/1/").build()

        webSocket = client.newWebSocket(request, object: WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                webSocket.send("{\"username\": \"pertti\", \"message\": \"hello i just joined\"}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)

                val message = chatMessageAdapter.fromJson(text)
                activity?.runOnUiThread {
                    binding.textviewChatMessages.text = "${binding.textviewChatMessages.text}${message?.timestamp} ${message?.username} ${message?.message}\n"
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // code 	Status code as defined by Section 7.4 of RFC 6455.
        // reason 	Reason for shutting down, no longer than 123 bytes of UTF-8 encoded data (not characters) or null.
        webSocket.close(1000, "leaving")
    }
}
