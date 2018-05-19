package gmads.it.gmads_lab1

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import gmads.it.gmads_lab1.R.id.navImage
import gmads.it.gmads_lab1.R.id.navMail
import gmads.it.gmads_lab1.constants.AppConstants
import gmads.it.gmads_lab1.fragments.PeopleFragment
import gmads.it.gmads_lab1.model.Profile
import kotlinx.android.synthetic.main.activity_add_book.view.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.chat_list.*
import kotlinx.android.synthetic.main.nav_header.*

class ChatList : AppCompatActivity() {
    internal var navName: TextView? =null
    internal var navMail: TextView? =null
    internal var navImage: ImageView? =null
    internal var navigationView: NavigationView? =null
    private var profile: Profile? = null
    private val myProfileBitImage: Bitmap? = null
    internal var headerView: View? =null
    internal var drawer: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        setSupportActionBar(toolbarList)
        //supportActionBar?.title = "Chat"
        supportActionBar?.title= "Chat"
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        setNavViews()
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        replaceFragment(PeopleFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .commit()
    }

    fun setNavViews() {
        drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener()
        navigationView.setNavigationItemSelectedListener(this)
        headerView = navigationView.getHeaderView(0)
        navName = headerView.findViewById(R.id.navName)
        navMail = headerView.findViewById(R.id.navMail)
        navImage = headerView.findViewById(R.id.navImage)
        headerView.setBackgroundResource(R.color.colorPrimaryDark)

        if (profile != null) {
            navName.setText()
            navName.setText(profile.getName())
            navName.append(" " + profile.getSurname())
            navMail.setText(profile.getEmail())

            if (myProfileBitImage != null) {
                navImage.setImageBitmap(myProfileBitImage)
            } else {
                navImage.setImageDrawable(getDrawable(R.drawable.default_picture))
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_showProfile) {
            val intentMod = Intent(this, ShowProfile::class.java)
            startActivity(intentMod)
            finish()
            return true
        } else if (id == R.id.nav_addBook) {
            val intentMod = Intent(this, AddBook::class.java)
            startActivity(intentMod)
            finish()
            return true
        } else if (id == R.id.nav_home) {
            //deve solo chiudersi la navbar
            //drawer.closeDrawers();
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            return true
        } else if (id == R.id.nav_chat) {
            val intent = Intent(this, ChatList::class.java)
            startActivity(intent)
            return true
        } else if (id == R.id.nav_logout) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener { v ->
                startActivity(Intent(this, Login::class.java))
                finish()
            }
            return true
        } else if (id == R.id.nav_mylibrary) {
            startActivity(Intent(this, MyLibrary::class.java))
            finish()

            return true
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
