package gmads.it.gmads_lab1.util

import android.content.Context
import android.util.Log
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.xwray.groupie.kotlinandroidextensions.Item
import gmads.it.gmads_lab1.FirebaseManagement
import gmads.it.gmads_lab1.model.*
import gmads.it.gmads_lab1.reciclerview.item.ImageMessageItem
import gmads.it.gmads_lab1.reciclerview.item.PersonItem
import gmads.it.gmads_lab1.reciclerview.item.TextMessageItem
import org.w3c.dom.Text


object FirebaseChat {

    private val firebaseInstance: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    private val currentUserRef: DatabaseReference
        get() = firebaseInstance.reference.child("users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}")

    private val chatChannelsCollectionRef = firebaseInstance.reference.child("chatChannels")

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ValueEventListener {
        return firebaseInstance.reference.child("users")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {

                        var items = mutableListOf<Item>()
                        var notifiedChatNumber : Int = 0

                        dataSnapshot!!.children.mapNotNull {

                            val user = it.getValue<Profile>(Profile::class.java)

                            if (user?.id != FirebaseAuth.getInstance().currentUser?.uid) {
                                currentUserRef
                                        .child("engagedChatChannels")
                                        .child(user?.id)
                                        .child("channelId")
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError?) {
                                                //To change body of created functions use File | Settings | File Templates.
                                                return
                                            }

                                            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                                                var channelId = dataSnapshot?.getValue(String::class.java)

                                                if(channelId != null) {
                                                    chatChannelsCollectionRef
                                                            .child(channelId)
                                                            .child("notificationNumber")
                                                            .child(FirebaseAuth.getInstance().currentUser?.uid)
                                                            .addValueEventListener(object : ValueEventListener {
                                                                override fun onCancelled(p0: DatabaseError?) {
                                                                    //To change body of created functions use File | Settings | File Templates.
                                                                    items.clear()
                                                                    onListen(items)
                                                                }

                                                                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                                                                    var notificationNumber = dataSnapshot?.getValue(Int::class.java)

                                                                    var pToRemove = items.firstOrNull { p -> p as PersonItem; p.userId == user?.id }
                                                                    if (pToRemove != null) {
                                                                        pToRemove as PersonItem
                                                                        if (pToRemove.notificationNumber > 0) {
                                                                            notifiedChatNumber--
                                                                        }
                                                                        items.remove(pToRemove)

                                                                    }

                                                                    user?.let {
                                                                        if (notificationNumber == 0) {
                                                                            items.add(notifiedChatNumber, PersonItem(user, user.id, 0, context))
                                                                        } else {
                                                                            notifiedChatNumber++
                                                                            items.add(0, PersonItem(user, user.id, notificationNumber
                                                                                    ?: 0, context))
                                                                        }
                                                                        onListen(items)
                                                                    }
                                                                }

                                                            })
                                                } else {
                                                    var pToRemove = items.firstOrNull { p -> p as PersonItem ; p.userId == user?.id }
                                                    if(pToRemove != null)
                                                        items.remove(pToRemove)
                                                    user?.let {
                                                        items.add(notifiedChatNumber, PersonItem(user, user.id, 0, context))

                                                        onListen(items)
                                                    }

                                                }
                                            }

                                        })


                            }

                        }

                   }

                    override fun onCancelled(p0: DatabaseError?) {
                        /*
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        */
                    }
                })
    }

    fun removeListener(registration: ValueEventListener) = FirebaseManagement
            .getDatabase()
            .getReference()
            .removeEventListener(registration)

    fun getOrCreateChatChannel(otherUserId: String,
                               onComplete: (channelId: String) -> Unit) {

        currentUserRef
                .child("engagedChatChannels")
                .child(otherUserId)
                .child("channelId")
                .addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        val chatChannelId = dataSnapshot?.value
                        if (chatChannelId != null) {
                            onComplete(chatChannelId as String)
                            return
                        }
                        /*
                        quando si scaricano messaggi solo per quei due utenti
                        prima si prende il corrente identificativo e si scarica la lsita di messaggi
                        TODO qua lettura messaggi...in caso bisognasse tenere conto dei lessaggi letti e non
                         */
                        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                        val newChannelKey = chatChannelsCollectionRef.push().key
                        val newChatChannel = ChatChannel(mutableListOf(currentUserId, otherUserId),
                                mapOf(Pair(currentUserId, 0), Pair(otherUserId, 0)))
/*
salvataggio dati sufirebase
 */
                        chatChannelsCollectionRef.child(newChannelKey).setValue(newChatChannel)
                        currentUserRef
                                .child("engagedChatChannels")
                                .child(otherUserId)
                                .setValue(mapOf("channelId" to newChannelKey))

                        firebaseInstance.reference
                                .child("users")
                                .child(otherUserId)
                                .child("engagedChatChannels")
                                .child(currentUserId)
                                .setValue(mapOf("channelId" to newChannelKey))

                        onComplete(newChannelKey as String)

                    }

                    override fun onCancelled(error: DatabaseError?) {
                        // Failed to read value
                    }
                })
    }
/*
in caso in cui ci fossero nuovi messaggi
 */
    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ValueEventListener {
        return chatChannelsCollectionRef.child(channelId)
                .child("messages")
                .orderByChild("time")
                .addValueEventListener(object : ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        val items = mutableListOf<Item>()
                        dataSnapshot!!.children.mapNotNull {
                            if ((it.getValue<TextMessage>(TextMessage::class.java))?.type == MessageType.TEXT)
                                items.add(TextMessageItem(it.getValue<TextMessage>(TextMessage::class.java)!!, context))
                            else
                                items.add(ImageMessageItem(it.getValue<ImageMessage>(ImageMessage::class.java)!!, context))
                        }
                        onListen(items)
                    }

                    override fun onCancelled(error: DatabaseError?) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.")
                        return
                    }
                })

    }
/*
invio messaggi al token desiderato")
 */
    fun sendMessage(message: Message, channelId: String) {

        var mKey = chatChannelsCollectionRef.child(channelId)
                .child("messages")
                .push()
                .key

        chatChannelsCollectionRef.child(channelId)
                .child("messages")
                .child(mKey)
                .setValue(message)
                .addOnCompleteListener { task ->
                    if(task.isComplete && message.type == "TEXT") {

                        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {

                            override fun onDataChange(dataSnapshot: DataSnapshot?) {

                                val myUser = dataSnapshot?.getValue(Profile::class.java)!!

                                chatChannelsCollectionRef
                                        .child(channelId)
                                        .addListenerForSingleValueEvent(object : ValueEventListener {

                                            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                                                message as TextMessage
                                                var chatChannel = dataSnapshot!!.getValue(ChatChannel::class.java)
                                                var dataUsers = chatChannel!!.userIds

                                                for (dataUser in dataUsers){
                                                    if(dataUser != myUser.id){
                                                        FirebaseManagement.sendMessage(message.text, myUser.name, dataUser)
                                                        chatChannelsCollectionRef
                                                                .child(channelId)
                                                                .child("notificationNumber")
                                                                .child(dataUser)
                                                                .setValue(chatChannel.notificationNumber.get(dataUser)!!.toInt() + 1)
                                                    }
                                                }
                                            }

                                            override fun onCancelled(p0: DatabaseError?) {
                                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                            }

                                        })

                            }

                            override fun onCancelled(p0: DatabaseError?) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }
                        })
                    }
                }
    }

    //region FCM
    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val user = dataSnapshot?.getValue(Profile::class.java)!!
                onComplete(user.registrationTokens)
            }

            override fun onCancelled(p0: DatabaseError?) {

            }

        })
    }

    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserRef.child("registrationTokens").setValue(registrationTokens)
    }
}
