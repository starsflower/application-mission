package com.starsflower.task_application

import android.Manifest
import android.os.Bundle
import android.util.JsonReader
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.starsflower.task_application.databinding.FragmentLoginBinding
import com.starsflower.task_application.databinding.FragmentTaskListBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val dataViewModel: MainDataViewModel by activityViewModels()
    private val client = OkHttpClient()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions(
            Array<String>(1) { Manifest.permission.INTERNET },
            1
        )

        // LOGIN!
        binding.loginButton.setOnClickListener {
            // Get input
            val email = binding.emailAddressInput.text.toString()
            val password = binding.passwordInput.text.toString()

            // Create URL
            val url = URL(dataViewModel.createURL(arrayOf("users", "login")))

            // Try login
            var formBody = FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build()

            var request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            this.client.newCall(request).execute().use { it
                val response = it.body!!.string()

                if (!it.isSuccessful) {
                    // Show error
                    var data = Json.decodeFromString<Error>(response.toString());
                    Snackbar.make(view, data.error, Snackbar.LENGTH_SHORT).show()
                } else {
                    var data = Json.decodeFromString<JWTResponse>(response.toString());
                    dataViewModel.setJWT(data.jwt)
                    dataViewModel.setUserID(data.user_id)
                    Snackbar.make(view, "Logged in successfully", Snackbar.LENGTH_SHORT).show()

                    findNavController().navigate(R.id.action_LoginFragment_to_ListFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}