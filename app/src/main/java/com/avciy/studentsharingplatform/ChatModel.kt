package com.avciy.studentsharingplatform

data class ChatModel (
    val messageId: String,
    val message: String,
    val time: Long,
    val toStudentNumber: String,
    val fromStudentNumber: String,
    val userPair: String
        )