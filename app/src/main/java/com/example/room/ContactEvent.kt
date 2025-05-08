package com.example.room

sealed interface ContactEvent {
    object SaveContact : ContactEvent
    data class SetFirstName(val firstName: String) : ContactEvent
    data class SetLastName(val lastName: String) : ContactEvent
    data class SetNumber(val number: String) : ContactEvent
    data class SortContacts(val sortType: SortType) : ContactEvent
    data class Unsorted(val sortType: SortType):ContactEvent
    data class DeleteContact(val contact: Contact):ContactEvent
    object ShowDialog : ContactEvent
    object HideDialog: ContactEvent


}