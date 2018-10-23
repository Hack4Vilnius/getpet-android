package lt.getpet.getpet

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*
import lt.getpet.getpet.managers.ManageFavourites
import io.reactivex.disposables.Disposable
import lt.getpet.getpet.fragments.PetFavoritesActivityFragment
import lt.getpet.getpet.fragments.ProfileFragment
import lt.getpet.getpet.fragments.SwipeFragment
import lt.getpet.getpet.fragments.UserLoginFragment
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity(), UserLoginFragment.UserLoginCallback {

    private var isLoggedIn: Boolean = false
    private var subscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ManageFavourites(context = applicationContext).clear()

        view_pager.adapter = TabsPagerAdapter(supportFragmentManager)

        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view_pager))
        view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        view_pager.currentItem = 1
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.dispose()
    }

    override fun onUserLoggedIn() {
        isLoggedIn = true
        view_pager.adapter!!.notifyDataSetChanged()
    }

    inner class TabsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    if (isLoggedIn) {
                        ProfileFragment.newInstance()
                    } else {
                        UserLoginFragment.newInstance()
                    }
                }
                1 -> SwipeFragment.newInstance()
                2 -> PetFavoritesActivityFragment.newInstance()
                else -> throw IllegalArgumentException("Tab for position $position doesn't exist")

            }
        }

        override fun getCount(): Int {
            return 3
        }

    }

}
