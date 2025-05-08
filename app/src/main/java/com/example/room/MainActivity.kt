package com.example.room

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.room.databinding.ActivityMainBinding
import com.example.room.databinding.DialogPageBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ContactAdapter
    var lastCheckedId: Int? = null

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java, "Contacts.db"
        ).build()
    }

    private val viewModel by viewModels<ContactViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContactViewModel(db.dao) as T
                }
            }
        }
    )

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

         setOnClick()
        updateItems()
        sortItems()





        setContentView(binding.root)

        }

    private fun sortItems(){
        binding.sortByFirstName.setOnClickListener {
            handleSortToggle(R.id.sortByFirstName, SortType.FIRST_NAME)
        }

        binding.sortByLastName.setOnClickListener {
            handleSortToggle(R.id.sortByLastName, SortType.LAST_NAME)
        }

        binding.sortByPhoneNumber.setOnClickListener {
            handleSortToggle(R.id.sortByPhoneNumber, SortType.PHONE_NUMBER)
        }
    }






        fun handleSortToggle(checkedId: Int, sortType: SortType) {

            val radioButtons = listOf(binding.sortByFirstName, binding.sortByLastName, binding.sortByPhoneNumber)

            if (lastCheckedId == checkedId) {
                radioButtons.find { it.id == checkedId }?.isChecked = false
                lastCheckedId = null
                viewModel.onEvent(ContactEvent.SortContacts(SortType.UNSORTED))
            } else {
                radioButtons.forEach { it.isChecked = false }

                radioButtons.find { it.id == checkedId }?.isChecked = true
                viewModel.onEvent(ContactEvent.SortContacts(sortType))
                lastCheckedId = checkedId
            }
        }



    private fun updateItems(){

        val adapter = ContactAdapter { contact ->
            viewModel.onEvent(ContactEvent.DeleteContact(contact))
        }


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                adapter.submitList(state.contact)
            }
        }



    }


    private  fun setOnClick(){
        binding.plusButton.setOnClickListener{
            val dialogBinding = DialogPageBinding.inflate(layoutInflater)

                val dialog = AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .setCancelable(true)
                .create()

            lifecycleScope.launch {
                viewModel.state.collect { state ->
                    dialogBinding.enterFirstName.setHintTextColor(
                        if (state.highlightFirstName) Color.RED else Color.BLACK
                    )
                    dialogBinding.enterLastName.setHintTextColor(
                        if (state.highlightLastName) Color.RED else Color.BLACK
                    )
                    dialogBinding.enterNumber.setHintTextColor(
                        if (state.highlightPhoneNumber) Color.RED else Color.BLACK
                    )
                }
            }



//
            dialogBinding.enterFirstName.addTextChangedListener {
                    viewModel.onEvent(ContactEvent.SetFirstName(it?.toString() ?: ""))

            }

            dialogBinding.enterLastName.addTextChangedListener {
                viewModel.onEvent(ContactEvent.SetLastName(it?.toString() ?: ""))
            }

           dialogBinding.enterNumber.addTextChangedListener {
                viewModel.onEvent(ContactEvent.SetNumber(it?.toString() ?: ""))
            }



            dialogBinding.addBtn.setOnClickListener {
                val currentState = viewModel.state.value
                dialogBinding.enterNumber.inputType = InputType.TYPE_CLASS_NUMBER

                viewModel.onEvent(ContactEvent.SaveContact)

                if (currentState.firstName.isBlank() || currentState.lastName.isBlank() || currentState.phoneNumber.isBlank()) {
                    return@setOnClickListener

            }
                dialog.dismiss()

            }
            dialog.show()

            }


        }

    }


