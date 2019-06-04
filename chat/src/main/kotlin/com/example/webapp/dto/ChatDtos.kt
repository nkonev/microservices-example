package com.example.webapp.dto

data class ResponseChatDto(val name: String, val owner: String) {

}

data class EditChatDto(val name: String, val newName: String) {

}