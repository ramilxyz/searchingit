package xyz.ramil.searchingit.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import retrofit2.http.Query
import xyz.ramil.searchingit.App
import xyz.ramil.searchingit.R
import xyz.ramil.searchingit.data.db.DataManager
import xyz.ramil.searchingit.ui.signin.SignInActivity
import xyz.ramil.searchingit.utils.CircularTransformation
const val DELAY = 600L //задержка
class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel by viewModels<MainActivityViewModel> {
        MainActivityViewModel.MainActivityViewModelFactory(this)
    }

    var searchTollbar:Toolbar? = null

    var searchMenu: Menu? = null

    var itemSearch: MenuItem? = null

    val handler: Handler = Handler()

    lateinit var query: String

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(mainActivityViewModel.account.value == null) {
            val intent = Intent(this, SignInActivity()::class.java)
            startActivity(intent)
            finishAffinity()
        }

        val toolbar: Toolbar = findViewById(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        setSearchtollbar()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        findViewById<MaterialButton>(R.id.sign_out_button).setOnClickListener {
            App.instance?.signInClient?.signOut()?.addOnCompleteListener(this) {
                val intent = Intent(this, SignInActivity()::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }

        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.nav_search
                ), drawerLayout
        )


        mainActivityViewModel.account.observe(this, {
            it.let {
                navView.getHeaderView(0).findViewById<TextView>(R.id.tvName).text = it.displayName
                navView.getHeaderView(0).findViewById<TextView>(R.id.tvGmail).text = it.email
                Picasso.get().load(it.photoUrl)
                        .transform(CircularTransformation())
                        .into(navView.getHeaderView(0).findViewById<ImageView>(R.id.ivIcon))

            }
        })

        mainActivityViewModel.navigateToDetails.observe(this, {
            val toast: Toast = Toast.makeText(this, getString(R.string.err403), Toast.LENGTH_LONG)
            toast.show()
        })

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) circleReveal(
                        R.id.searchToolbar,
                        1,
                        false,
                        true
                ) else searchTollbar?.visibility = View.VISIBLE
                itemSearch?.expandActionView()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setSearchtollbar() {
        searchTollbar = findViewById<MaterialToolbar>(R.id.searchToolbar)
        searchTollbar?.inflateMenu(R.menu.search_menu)
        searchMenu = searchTollbar?.menu
        searchTollbar?.setNavigationOnClickListener {
            circleReveal(R.id.searchToolbar, 1, false, false)
        }

        itemSearch = searchMenu?.findItem(R.id.action_filter_search)

        MenuItemCompat.setOnActionExpandListener(
                itemSearch,
                object : MenuItemCompat.OnActionExpandListener {
                    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            circleReveal(R.id.searchToolbar, 1, false, false)
                        } else searchTollbar?.visibility = View.GONE
                        return true
                    }

                    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                        return true
                    }
                })
        initSearchView()

    }

    private fun initSearchView() {
        val searchView = searchMenu?.findItem(R.id.action_filter_search)?.actionView as SearchView

        searchView.maxWidth = Int.MAX_VALUE
        searchView.isSubmitButtonEnabled = false
        val closeButton = searchView.findViewById(R.id.search_close_btn) as ImageView
        closeButton.setImageResource(R.drawable.ic_close)

        val txtSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        txtSearch.setHintTextColor(getResources().getColor(android.R.color.transparent))
        txtSearch.setTextColor(resources.getColor(R.color.black))

        val searchTextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as AutoCompleteTextView

        try {
            val mCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            mCursorDrawableRes.isAccessible = true
            mCursorDrawableRes[searchTextView] = R.drawable.shape_search_cursor
        } catch (e: Exception) {
            e.printStackTrace()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                callSearch(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                       callSearch(newText)

                }, DELAY)
                return false
            }

            fun callSearch(query: String) {
                if(query.isEmpty()) {
                    DataManager.insertData(mutableListOf())
                }

                this@MainActivity.query = query
                mainActivityViewModel.currentPage = 1
                mainActivityViewModel.search(query)
            }
        })
    }

    fun circleReveal(viewID: Int, posFromRight: Int, containsOverflow: Boolean, isShow: Boolean) {
        val view: View = findViewById(viewID)
        var width = view.width
        if (posFromRight > 0) width -= posFromRight * resources.getDimensionPixelSize(R.dimen.action_button) - resources.getDimensionPixelSize(
                R.dimen.action_button
        ) / 2
        if (containsOverflow) width -= resources.getDimensionPixelSize(R.dimen.action_button)
        val cx = width
        val cy = view.height / 2
        val anim =
            if (isShow)
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, width.toFloat())
            else
                ViewAnimationUtils.createCircularReveal(view, cx, cy, width.toFloat(), 0f)
        anim.duration = 250.toLong()
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!isShow) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.INVISIBLE
                }
            }
        })
        if (isShow)
            view.visibility = View.VISIBLE
        anim.start()
    }

    fun pagination() {
        mainActivityViewModel.search(query)
    }
}