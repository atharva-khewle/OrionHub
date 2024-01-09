package com.example.orionhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private var _username= MutableLiveData("default")
    val username : LiveData<String> = _username

    fun saveusername(a:String){
        _username.value=a
    }

    // For Email
    private var _email = MutableLiveData("default email")
    val email: LiveData<String> = _email

    fun saveEmail(newEmail: String) {
        _email.value = newEmail
    }

    // For Password
    private var _password = MutableLiveData("default pass")
    val password: LiveData<String> = _password

    fun savePassword(newPassword: String) {
        _password.value = newPassword
    }

    // For Interested Items
    private var _interestedItems = MutableLiveData<List<String>>(listOf())
    val interestedItems: LiveData<List<String>> = _interestedItems

    fun saveInterestedItems(items: List<String>) {
        _interestedItems.value = items
    }

    // To add a single item to the interested items list
    fun addInterestedItem(item: String) {
        val currentItems = _interestedItems.value ?: listOf()
        _interestedItems.value = currentItems + item
    }

    private var _isregister= MutableLiveData<Int>()
    val isregister : LiveData<Int> = _isregister

    fun saveisregister(a:Int){
        _isregister.value=a
    }
}