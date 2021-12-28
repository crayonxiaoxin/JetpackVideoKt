package com.github.crayonxiaoxin.ppjoke_kt.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.databinding.FragmentMyBinding
import com.github.crayonxiaoxin.ppjoke_kt.ui.InteractionPresenter
import com.github.crayonxiaoxin.ppjoke_kt.utils.UserManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@FragmentDestination("main/tabs/my", true)
class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        StatusBar.fitSystemBar(requireActivity(), false, false)
        binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.goDetail.setOnClickListener {
            context?.startActivity(
                ProfileActivity.intentStartActivity(
                    requireContext(),
                    ProfileActivity.TAB_TYPE_ALL
                )
            )
        }
        lifecycleScope.launch {
            binding.user = UserManager.get()
            UserManager.refresh().collectLatest {
                binding.user = it
            }
        }
        binding.actionLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.fragment_my_logout)
                .setPositiveButton(R.string.fragment_my_logout_ok) { dialog, _ ->
                    dialog.dismiss()
                    UserManager.logout()
                    activity?.onBackPressed()
                }
                .setNegativeButton(R.string.fragment_my_logout_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        StatusBar.fitSystemBar(requireActivity(), hidden, hidden)
    }
}