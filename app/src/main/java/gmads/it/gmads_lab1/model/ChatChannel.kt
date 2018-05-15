package gmads.it.gmads_lab1.model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}
