package com.noatnoat.pianoapp.admob.lfo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.noatnoat.pianoapp.databinding.ViewItemLanguageLfoBorderBinding

/**
 * Created by ViO on 17/03/2024.
 */
class LFOAdapter(
    private val context: Context,
    private val itemLimit: Int,
    private val listData: List<Language>
) : RecyclerView.Adapter<LFOAdapter.ViewHolder>() {
    private var lfoSelectLanguage: LFOSelectLanguage? = null
    fun registerListener(listener: LFOSelectLanguage) {
        this.lfoSelectLanguage = listener
    }

    class ViewHolder(val binding: ViewItemLanguageLfoBorderBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ViewItemLanguageLfoBorderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (itemLimit <= listData.size) {
            itemLimit
        } else {
            listData.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language: Language = listData[position]
        holder.binding.txtInternationalLanguage.text = language.internationalName
        holder.binding.txtNationalLanguage.isVisible = false
        holder.binding.imgIconLanguage.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                language.idIcon
            )
        )
        holder.binding.imgSelect.isSelected = language.isChoose
        holder.itemView.setOnClickListener {
            lfoSelectLanguage?.onSelectLanguage(language)
        }
    }

    fun getListData(): List<Language> {
        return listData
    }

    fun getLanguageSelected(): Language? {
        return listData.find { it.isChoose }
    }
}