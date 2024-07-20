package com.example.chatapp.models

class Messege {

    var text:String? = null
    var fromUserUid:String? = null
    var toUserUid:String? = null
    var date:String? = null
    var imageLink:String? = null
    var videoLink:String? = null

    constructor()

    constructor(text: String?, fromUserUid: String?, toUserUid: String?, date: String?) {
        this.text = text
        this.fromUserUid = fromUserUid
        this.toUserUid = toUserUid
        this.date = date
    }

    constructor(
        text: String?,
        fromUserUid: String?,
        toUserUid: String?,
        date: String?,
        imageLink: String?)
    {
        this.text = text
        this.fromUserUid = fromUserUid
        this.toUserUid = toUserUid
        this.date = date
        this.imageLink = imageLink
    }




}