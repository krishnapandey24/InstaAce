package com.omnicoder.instaace.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.adapters.ReelTrayViewAdapter
import com.omnicoder.instaace.adapters.StorySearchViewAdapter
import com.omnicoder.instaace.databinding.StoryFragmentBinding
import com.omnicoder.instaace.model.ReelTray
import com.omnicoder.instaace.viewmodels.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@SuppressLint("NotifyDataSetChanged")
class StoryFragment : Fragment() {
    private lateinit var binding: StoryFragmentBinding
    private lateinit var viewModel: StoryViewModel
    private lateinit var cookies: String
    private lateinit var reelTrays: MutableList<ReelTray>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= StoryFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        val args: StoryFragmentArgs by navArgs()
        cookies= args.cookie
        viewModel.fetchReelTray(cookies)
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
            binding.downloadView.adapter = StorySearchViewAdapter(context,it,cookies)
            binding.searching.visibility=View.GONE
            binding.progressBar.visibility=View.GONE
            if(it.isEmpty()){
                Toast.makeText(context,"No Account Found!",Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.reelTray.observe(viewLifecycleOwner) {
            reelTrays=it.toMutableList()
            if(reelTrays[0].user==null){
                reelTrays.removeAt(0)
            }
            setReelTrayRecyclerView()
        }

    }

    private fun setOnClickListeners() {
        binding.fetchButton.setOnClickListener{
            binding.progressBar.visibility=View.VISIBLE
            hideKeyboard()
            binding.editText.text.clear()
        }

        binding.backButton.setOnClickListener{
            NavHostFragment.findNavController(this@StoryFragment).navigateUp()
        }

        binding.cancelButton.setOnClickListener{
            binding.editText.text.clear()
            setReelTrayRecyclerView()
        }
    }


    private fun setReelTrayRecyclerView() {
        val recyclerView: RecyclerView = binding.downloadView
        recyclerView.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerView.adapter = ReelTrayViewAdapter(context,reelTrays,cookies)
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}