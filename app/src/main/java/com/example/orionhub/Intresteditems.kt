package com.example.orionhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.orionhub.databinding.ActivityMainBinding
import com.example.orionhub.databinding.FragmentIntresteditemsBinding
import com.google.android.material.card.MaterialCardView
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels


class Intresteditems : Fragment() {


    private  val sharedviewmodel: SharedViewModel by activityViewModels()
    private var _binding: FragmentIntresteditemsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIntresteditemsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val titles = arrayOf(
            "Science",
            "Horror",
            "Modi",
            "UFOs",
            "Mystery",
            "Space",
            "AI",
            "Crypto",
            "Future"
        )





        titles.forEach { title ->
            binding.cardsLayout.addView(createCard(requireContext(), title))
        }


        binding.intresteditemsbtn.setOnClickListener {
            binding.intresteditemsbtn.findNavController().navigate(R.id.action_intresteditems_to_mainPage)
        }
        return binding.root
    }


    private fun Int.toDp(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    private fun createCard(context: Context, title: String): MaterialCardView {
        val cardView = MaterialCardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                100.toDp(context), 40.toDp(context)
            ).also { params ->
                params.gravity = Gravity.CENTER
                val margin = 8.toDp(context)
                params.setMargins(margin, margin, margin, margin) // Set margin
            }
            radius = 10.toDp(context).toFloat() // Set corner radius
            isCheckable = true
            isClickable = true
            // Other card properties


            //this is it
            setOnClickListener {
                isChecked = !isChecked
                    sharedviewmodel.addInterestedItem(title);
            }
        }

        val textView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER
            text = title
            setPadding(20.toDp(context), 0, 0, 0) // Set left padding
        }

        cardView.addView(textView)
        return cardView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}