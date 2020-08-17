package com.keremk.instakotlinapp.Models

class UserPosts {
    var userID:String? = null
    var userName:String?=null
    var postURL:String?=null
    var userPhotoURL:String?=null
    var postID:String?=null
    var postAciklama:String?=null
    var postYuklenmeTarih:Long?=null

    constructor(userID: String?, userName: String?, postURL: String?, userPhotoURL: String?, postID: String?, postAciklama: String?, postYuklenmeTarih: Long?) {
        this.userID = userID
        this.userName = userName
        this.postURL = postURL
        this.userPhotoURL = userPhotoURL
        this.postID = postID
        this.postAciklama = postAciklama
        this.postYuklenmeTarih = postYuklenmeTarih
    }

    constructor(){}

    override fun toString(): String {
        return "UserPosts(userID=$userID, userName=$userName, postURL=$postURL, userPhotoURL=$userPhotoURL, postID=$postID, postAciklama=$postAciklama, postYuklenmeTarih=$postYuklenmeTarih)"
    }

}