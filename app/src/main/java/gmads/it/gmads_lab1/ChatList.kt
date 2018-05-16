package gmads.it.gmads_lab1

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import gmads.it.gmads_lab1.fragments.PeopleFragment
import kotlinx.android.synthetic.main.chat_list.*

class ChatList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_list)

        replaceFragment(PeopleFragment())

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_people -> {
                    replaceFragment(PeopleFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .commit()
    }
}
