package com.example.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.room.databinding.ItemLayoutBinding

class ContactAdapter (private val onDeleteClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>()  {

    private var contacts = listOf<Contact>()

    fun submitList(list: List<Contact>) {
        contacts = list
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(
        private val binding: ItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            binding.nameView.text = contact.firstname
            binding.lastView.text =contact.lastname
            binding.numberView.text = contact.number

            binding.deleteBtn.setOnClickListener{
                onDeleteClick(contact)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

}