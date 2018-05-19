package gmads.it.gmads_lab1.reciclerview.item;

import android.content.Context
import gmads.it.gmads_lab1.R
import gmads.it.gmads_lab1.glide.*
import gmads.it.gmads_lab1.model.Profile
import gmads.it.gmads_lab1.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*


class PersonItem(val person: Profile,
        val userId: String,
private val context: Context)
        : Item() {

        override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text = person.name
        viewHolder.textView_bio.text = person.description
        if (person.image != null)
                GlideApp.with(context)
                .load(person.image)
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.imageView_profile_picture)
        }

        override fun getLayout() = R.layout.item_person
        }