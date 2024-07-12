package com.noatnoat.pianoapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.ui.base.BaseDialogFragment

class RateDialogFragment : BaseDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_rate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stars = arrayOf(
            view.findViewById<ImageView>(R.id.img_star1),
            view.findViewById<ImageView>(R.id.img_star2),
            view.findViewById<ImageView>(R.id.img_star3),
            view.findViewById<ImageView>(R.id.img_star4),
            view.findViewById<ImageView>(R.id.img_star5)
        )

        val txtRate = view.findViewById<TextView>(R.id.txt_rate)

        stars.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                updateStars(stars, index)
                updateRateMessage(txtRate, index + 1)
            }
        }
    }

    private fun updateStars(stars: Array<ImageView>, selectedIndex: Int) {
        stars.forEachIndexed { index, imageView ->
            imageView.setImageResource(
                if (index <= selectedIndex) R.drawable.img_star_selected
                else R.drawable.img_starstar_unselected
            )
        }
    }

    private fun updateRateMessage(txtRate: TextView, selectedStars: Int) {
        val message = when (selectedStars) {
            1 -> "We value your feedback."
            2 -> "We'll improve your experience."
            3 -> "We're committed to making it even better."
            4 -> "We're delighted that you enjoy using our app."
            5 -> "We are thrilled to hear that you love our app."
            else -> ""
        }
        txtRate.text = message
    }
}