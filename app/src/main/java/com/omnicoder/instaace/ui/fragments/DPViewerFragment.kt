package com.omnicoder.instaace.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.omnicoder.instaace.adapters.DPRecentViewAdapter
import com.omnicoder.instaace.adapters.StorySearchViewAdapter
import com.omnicoder.instaace.databinding.DpViewerFragmentBinding
import com.omnicoder.instaace.ui.activities.ViewDPActivity
import com.omnicoder.instaace.viewmodels.DPViewerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DPViewerFragment : Fragment() {
    private lateinit var binding: DpViewerFragmentBinding
    private lateinit var viewModel: DPViewerViewModel
    private lateinit var cookies: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= DpViewerFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this)[DPViewerViewModel::class.java]
        val args: StoryFragmentArgs by navArgs()
        cookies= args.cookie
        viewModel.getRecentSearches()
        binding.recentView.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        observeData()
        setOnClickListeners()

        binding.editText.doOnTextChanged { text, _, _, count ->
            if(count>0){
                viewModel.searchUser(text.toString(),cookies)
                binding.progressBar.visibility=View.VISIBLE
                binding.searching.visibility=View.VISIBLE
                binding.cancelButton.visibility=View.VISIBLE
            }else{
                binding.cancelButton.visibility=View.GONE
            }
        }
    }

    private fun observeData(){
        viewModel.searchResult.observe(viewLifecycleOwner){
            binding.downloadView.layoutManager=LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.downloadView.adapter = StorySearchViewAdapter(it){ user->
                val intent= Intent(context, ViewDPActivity::class.java)
                intent.putExtra("username",user.username)
                intent.putExtra("cookies",cookies)
                context?.startActivity(intent)
                viewModel.insertRecent(user)
            }
            binding.searching.visibility=View.GONE
            binding.progressBar.visibility=View.GONE
            if(it.isEmpty()){
                Toast.makeText(context,"No Account Found!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.resents.observe(viewLifecycleOwner){
            binding.recentView.adapter= DPRecentViewAdapter(context,it,cookies)
        }

    }

    private fun setOnClickListeners() {
        binding.fetchButton.setOnClickListener{
            binding.progressBar.visibility=View.VISIBLE
            hideKeyboard()
            binding.editText.text.clear()
        }

        binding.backButton.setOnClickListener{
            NavHostFragment.findNavController(this@DPViewerFragment).navigateUp()
        }

        binding.cancelButton.setOnClickListener{
            binding.editText.text.clear()
        }
    }


    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }



}