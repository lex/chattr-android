package com.lex.chattr

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.lex.chattr.databinding.FragmentWelcomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableNextIfUsernameIsNotEmpty()

        binding.edittextUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                enableNextIfUsernameIsNotEmpty()
            }
        })

        binding.buttonUsernameNext.setOnClickListener {
            val username = binding.edittextUsername.text.toString()
            val action =
                WelcomeFragmentDirections.actionWelcomeFragmentToChannelListFragment(username)
            findNavController().navigate(action)
        }
    }

    private fun enableNextIfUsernameIsNotEmpty() {
        binding.buttonUsernameNext.isEnabled = binding.edittextUsername.text.toString().isNotBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}