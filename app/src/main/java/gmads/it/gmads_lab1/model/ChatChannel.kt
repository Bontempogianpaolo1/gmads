package gmads.it.gmads_lab1.model

data class ChatChannel(val userIds: MutableList<String>) {

    constructor() : this(mutableListOf())
}
/*
e la lista dei messaggi...immagino che quando si scarica da firebase questa lista si riempa
 */