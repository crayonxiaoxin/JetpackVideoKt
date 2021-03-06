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
        binding.goDetail.setOnClickListener { toProfileActivity(ProfileActivity.TAB_TYPE_ALL) }
        binding.cardOverlap.setOnClickListener { toProfileActivity(ProfileActivity.TAB_TYPE_ALL) }
        binding.userFeed.setOnClickListener { toProfileActivity(ProfileActivity.TAB_TYPE_FEED) }
        binding.userComment.setOnClickListener { toProfileActivity(ProfileActivity.TAB_TYPE_COMMENT) }
        binding.userFavorite.setOnClickListener { toBehaviorActivity(UserBehaviorActivity.BEHAVIOR_FAVORITE) }
        binding.userHistory.setOnClickListener { toBehaviorActivity(UserBehaviorActivity.BEHAVIOR_HISTORY) }
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

    private fun toProfileActivity(type: String) {
        context?.let {
            it.startActivity(
                ProfileActivity.intentStartActivity(it, ProfileActivity.TAB_TYPE_ALL)
            )
        }
    }

    private fun toBehaviorActivity(behavior: Int) {
        context?.let {
            it.startActivity(
                UserBehaviorActivity.intentStartActivity(it, behavior)
            )
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        StatusBar.fitSystemBar(requireActivity(), hidden, hidden)
    }
}