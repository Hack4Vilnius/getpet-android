package lt.getpet.getpet.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_user_profile.*
import lt.getpet.getpet.R
import lt.getpet.getpet.authentication.AuthStateChangedListener
import lt.getpet.getpet.authentication.AuthenticationManager
import lt.getpet.getpet.data.User
import lt.getpet.getpet.navigation.NavigationManager
import timber.log.Timber
import javax.inject.Inject


class UserProfileFragment : BaseFragment(), AuthStateChangedListener {

    private val PRIVACY_POLICY_URL = "https://www.getpet.lt/privatumo-politika"

    @Inject
    lateinit var authenticationManager: AuthenticationManager

    @Inject
    lateinit var navigationManager: NavigationManager


    private var subscriptions: CompositeDisposable = CompositeDisposable()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onStart() {
        super.onStart()

        authenticationManager.addAuthStateChangeListener(this)

        val user = authenticationManager.getCurrentUser()
        when (user) {
            null -> showAnonymousProfile()
            else -> showUserProfile(user)
        }

        button_login.setOnClickListener {
            navigationManager.navigateToUserLoginActivity(activity!!)
        }

        button_signout.setOnClickListener {
            onUserSignedOut()
        }

        button_privacy_policy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL)))
        }
    }

    override fun onStop() {
        authenticationManager.removeAuthStateChangeListener(this)
        super.onStop()
    }

    private fun showAnonymousProfile() {
        user_name.setText(R.string.anonymous_user_name)
        button_login.visibility = View.VISIBLE
        button_signout.visibility = View.GONE
        Glide.with(this).load(R.drawable.anonymous_avatar)
                .circleCrop()
                .into(user_photo)
    }

    private fun showUserProfile(user: User) {
        user_name.text = user.name
        button_login.visibility = View.GONE
        button_signout.visibility = View.VISIBLE

        if (user.largePhotoUrl != null) {
            Glide.with(this).load(user.largePhotoUrl)
                    .circleCrop()
                    .into(user_photo)
        }
    }

    private fun onUserSignedOut() {
        val disposable = authenticationManager.signOutUser(context!!)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    showAnonymousProfile()
                }, {
                    Timber.w(it)
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                })

        subscriptions.add(disposable)
    }

    override fun onUserLoggedIn(user: User) {
        val disposable = authenticationManager.refreshAndGetApiToken()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    showUserProfile(user)
                }, {
                    Timber.w(it)
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                })

        subscriptions.add(disposable)
    }

    override fun onAuthError(throwable: Throwable) {
        Toast.makeText(context, throwable.localizedMessage, Toast.LENGTH_LONG).show()
    }

    override fun onDetach() {
        super.onDetach()
        subscriptions.clear()
    }

    companion object {

        @JvmStatic
        fun newInstance(): UserProfileFragment {
            return UserProfileFragment()
        }
    }

}