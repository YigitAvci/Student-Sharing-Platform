package com.avciy.studentsharingplatform.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avciy.studentsharingplatform.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val arg1 = DetailsFragmentArgs.fromBundle(it).userEmail
            textView_postOwner_details.text = arg1
            val arg2 = DetailsFragmentArgs.fromBundle(it).classNameOrCode
            textview_className_details.text = arg2
            val arg3 = DetailsFragmentArgs.fromBundle(it).classDate
            textview_classDate_details.text = arg3
            val arg4 = DetailsFragmentArgs.fromBundle(it).updateDate
            textView_updateDate_details.text = arg4
            val arg5 = DetailsFragmentArgs.fromBundle(it).downloadUrl
            Picasso.get().load(arg5).into(imageView_details)
        }
    }

}