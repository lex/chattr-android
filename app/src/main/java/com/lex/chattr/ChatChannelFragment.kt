package com.lex.chattr

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.navArgs
import com.lex.chattr.databinding.FragmentChatChannelBinding
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.*


class ChatChannelFragment : Fragment() {

    private var _binding: FragmentChatChannelBinding? = null

    private val binding get() = _binding!!
    private lateinit var webSocket: WebSocket
    private val args: ChatChannelFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = args.username
        val channelId = args.channelId

        val moshi: Moshi = Moshi.Builder().build()
        val chatMessageAdapter: JsonAdapter<ChatMessage> = moshi.adapter(ChatMessage::class.java)

        val client = OkHttpClient()
        val request = Request.Builder().url("${getString(R.string.API_URL)}/ws/channel/${channelId}/").build()

        binding.textviewChatMessages.movementMethod = ScrollingMovementMethod()
        binding.buttonSendMessage.setOnClickListener {
            val message = binding.edittextMessage.text.toString()
            val m = chatMessageAdapter.toJson(ChatMessage(username, message, ""))
            webSocket.send(m)
            binding.edittextMessage.text.clear()
        }
        binding.edittextMessage.addTextChangedListener( object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                enableSendIfMessageIsNotEmpty()
            }
        })

        enableSendIfMessageIsNotEmpty()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                val m = chatMessageAdapter.toJson(ChatMessage(username, getString(R.string.chat_join_message), ""))
                webSocket.send(m)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)

                val message = chatMessageAdapter.fromJson(text)
                activity?.runOnUiThread {
                    binding.textviewChatMessages.text =
                        "${binding.textviewChatMessages.text}${message?.timestamp} ${message?.username} ${message?.message}\n"
                    binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        })
    }

    private fun enableSendIfMessageIsNotEmpty() {
        binding.buttonSendMessage.isEnabled = binding.edittextMessage.text.toString().isNotBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // code 	Status code as defined by Section 7.4 of RFC 6455.
        // reason 	Reason for shutting down, no longer than 123 bytes of UTF-8 encoded data (not characters) or null.
        webSocket.close(1000, "leaving")
    }
}
