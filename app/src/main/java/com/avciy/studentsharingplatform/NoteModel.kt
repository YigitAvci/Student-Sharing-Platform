package com.avciy.studentsharingplatform

data class NoteModel (
    var likes: String,
    var postId: String,
    var userEmail: String,
    var classNameOrCode: String,
    var classDate: String,
    var downloadUrl: String,
    var updateDate: String
        )