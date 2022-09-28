package com.lex.chattr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.lex.chattr.databinding.FragmentChannelListBinding
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.*
import java.io.IOException


class ChannelListFragment : Fragment() {

    private var _binding: FragmentChannelListBinding? = null

    private val chatChannels: MutableList<ChatChannel> = mutableListOf()
    private val binding get() = _binding!!
    private lateinit var channelAdapter: ChannelAdapter
    private val args: ChannelListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChannelListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // otherwise they just keep piling up on back and forth navigation
        chatChannels.clear()

        channelAdapter = ChannelAdapter(chatChannels) { chatChannel ->
            val username = args.username
            val action =
                ChannelListFragmentDirections.actionChannelListFragmentToChatChannelFragment(
                    username,
                    chatChannel.name,
                    chatChannel.id
                )
            findNavController().navigate(action)
        }

        val layoutManager = LinearLayoutManager(activity!!.applicationContext)
        binding.recyclerViewChannels.layoutManager = layoutManager
        binding.recyclerViewChannels.adapter = channelAdapter

        val client = OkHttpClient()
        val moshi = Moshi.Builder().build()

        val listType = Types.newParameterizedType(List::class.java, ChatChannel::class.java)
        val adapter: JsonAdapter<List<ChatChannel>> = moshi.adapter(listType)

        val request = Request.Builder()
            .url("http://10.0.2.2:8000/chat/list/")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val response = response.body!!.string()
                    val channels: List<ChatChannel> = adapter.fromJson(response)!!

                    chatChannels.addAll(channels)
                    activity?.runOnUiThread {
                        channelAdapter.notifyDataSetChanged()
                    }

                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}