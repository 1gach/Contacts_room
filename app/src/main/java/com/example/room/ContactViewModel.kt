package com.example.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactViewModel(private val dao: ContactDao): ViewModel() {
    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    private val _contacts= _sortType.flatMapLatest { sortType->
        when(sortType){
            SortType.FIRST_NAME -> dao.getContactOrderedByFirstname()
            SortType.LAST_NAME -> dao.getContactOrderedByLastname()
            SortType.PHONE_NUMBER -> dao.getContactOrderedByNumber()
            SortType.UNSORTED -> dao.getContactsUnsorted()
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(ContactState())
    val state = combine(_state,_sortType,_contacts){state, sortType, contacts->
       state.copy(
           contact = contacts,
           sortType = sortType,
       )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

     fun  onEvent(event: ContactEvent) {
         when (event) {
             ContactEvent.SaveContact ->{val firstName = state.value.firstName
             val lastName = state.value.lastName
             val phoneNumber = state.value.phoneNumber
                 if (firstName.isBlank()||lastName.isBlank() ||phoneNumber.isBlank()){
                      return
                 }
                 val contact = Contact(firstname = firstName, lastname = lastName, number =  phoneNumber)
                 viewModelScope.launch {
                     dao.upsertContact(contact)
                 }
                 _state.update { it.copy(
                     isAddingContact = false,
                     firstName = "",
                     lastName = "",
                     phoneNumber = ""
                 ) }
             }
             is ContactEvent.SetFirstName -> _state.update { it.copy(firstName = event.firstName )}

             is ContactEvent.SetLastName -> _state.update { it.copy(lastName = event.lastName )}

             is ContactEvent.SetNumber -> _state.update { it.copy(phoneNumber = event.number )}

             ContactEvent.ShowDialog -> _state.update { it.copy(isAddingContact =true ) }

             is ContactEvent.SortContacts ->_sortType.value = event.sortType

             is ContactEvent.HideDialog-> _state.update { it.copy(isAddingContact =true ) }

             is ContactEvent.DeleteContact ->viewModelScope.launch {
                 dao.deleteContact(event.contact)
             }

             is ContactEvent.Unsorted ->_sortType.value = SortType.UNSORTED
         }
     }
}