package com.example.room

data class ContactState(
    val contact: List<Contact> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String= "",
    val isAddingContact: Boolean = false,
    val highlightFirstName: Boolean = false,
    val highlightLastName: Boolean = false,
    val highlightPhoneNumber: Boolean = false,
    val sortType: SortType = SortType.FIRST_NAME
)
