package com.trialbot.trainyapplication.presentation.screen.createChat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentCreateChatBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreateChatFragment : Fragment(R.layout.fragment_create_chat), HasCustomAppbarIcon, HasCustomTitle {

    private lateinit var binding: FragmentCreateChatBinding
    private val viewModel by viewModel<CreateChatViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateChatBinding.bind(view)
    }

    override fun getIconRes(): Int? {
        return null
    }

    override fun getTitle(): String {
        return " Creating"
    }
}