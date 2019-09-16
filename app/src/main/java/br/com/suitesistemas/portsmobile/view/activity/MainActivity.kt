package br.com.suitesistemas.portsmobile.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.view.fragment.CustomerFragment
import br.com.suitesistemas.portsmobile.view.fragment.FinancialFragment
import br.com.suitesistemas.portsmobile.view.fragment.ResourcesFragment
import br.com.suitesistemas.portsmobile.view.fragment.SaleFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ResourcesFragment.Delegate {

    private var doubleBackToExit: Boolean = false
    private var selectedMenuId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()
        configureNavigationView()
        addRootFragment()
        subscribeToFirebaseMessagingTopic()
        initFirebaseToken()

        if (savedInstanceState != null) {
            selectedMenuId = savedInstanceState.getInt("menuItemId")
            val menuItem = nav_view.menu.findItem(selectedMenuId)
            menuItem.isChecked = true
            drawer_layout.closeDrawers()
        }
    }

    override fun onStart() {
        super.onStart()
        with (IntentFilter()) {
            addAction("br.com.suitesistemas.portsmobile_TARGET_CLIENTE")
            addAction("br.com.suitesistemas.portsmobile_TARGET_VENDA")
            LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver((notificationMessageReceiver), this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationMessageReceiver)
    }

    private val notificationMessageReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Snackbar.make(drawer_layout, "Registros atualizados", Snackbar.LENGTH_LONG)
                .setAction("ATUALIZAR") {
                    onNewIntent(intent)
                }.show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.putInt("menuItem", selectedMenuId)
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(getColorBy(android.R.color.white))
        configureMenu()
    }

    private fun configureMenu() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = getColorBy(android.R.color.white)
        toggle.syncState()
    }

    private fun configureNavigationView() {
        nav_view.setNavigationItemSelectedListener(this)
        drawer_layout.closeDrawers()
    }

    private fun addRootFragment() {
        replaceFragment(ResourcesFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_left, 0, R.anim.enter_from_left, R.anim.exit_to_left)
        transaction.replace(R.id.main_frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun subscribeToFirebaseMessagingTopic() {
        val sharedPref = getSharedPreferences("userResponse", Context.MODE_PRIVATE)
        sharedPref?.let { pref ->
            val company = pref.getString("empresa", "")!!
            FirebaseMessaging
                .getInstance()
                .subscribeToTopic("app-$company")
                .addOnCompleteListener { task ->
                    when (task.isSuccessful) {
                        true -> Log.i("FIREBASE:", "Subscribed topic: app-$company")
                        false -> Log.i("FIREBASE:", "Failed to subscribe in topic: app-$company")
                    }
                }
        }
    }

    private fun initFirebaseToken() {
        val instanceId = FirebaseInstanceId.getInstance().instanceId
        if (instanceId.isSuccessful)
            instanceId.addOnCompleteListener { FirebaseUtils.storeToken(it, this) }
    }

    override fun onNewIntent(intent: Intent?) {
        when (intent?.action) {
            "br.com.suitesistemas.portsmobile_TARGET_CLIENTE" -> Handler().postDelayed({onResourceSelected(0)}, 1000)
            "br.com.suitesistemas.portsmobile_TARGET_VENDA" -> Handler().postDelayed({onResourceSelected(1)}, 1000)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_customer -> closeAllFragmentsAndReplaceTo(item, CustomerFragment())
            R.id.menu_sale -> closeAllFragmentsAndReplaceTo(item, SaleFragment())
//            R.id.menu_order -> closeAllFragmentsAndReplaceTo(OrderFragment())
            R.id.menu_financial -> closeAllFragmentsAndReplaceTo(item, FinancialFragment())
//            R.id.menu_task -> closeAllFragmentsAndReplaceTo(TaskFragment())
//            R.id.menu_crm -> closeAllFragmentsAndReplaceTo(CRMFragment())
            R.id.menu_logout -> logout()
        }
        drawer_layout.closeDrawers()
        return true
    }

    private fun closeAllFragmentsAndReplaceTo(item: MenuItem, fragment: Fragment) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        selectedMenuId = item.itemId
        item.isChecked = true
        addRootFragment()
        replaceFragment(fragment)
    }

    override fun onResourceSelected(itemPosition: Int) {
        return when (itemPosition) {
            0 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_customer), CustomerFragment())
            1 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_sale), SaleFragment())
            2 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_financial), FinancialFragment())
//            3 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_customer), OrderFragment())
//            4 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_customer), TaskFragment())
//            5 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_customer), CRMFragment())
            else -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_customer), CustomerFragment())
        }
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("userResponse", Context.MODE_PRIVATE)
        sharedPref?.let { pref ->
            unsubscribeFirebaseMessagingTopic(pref.getString("empresa", ""))
        }
        with (sharedPref.edit()) {
            clear()
            apply()
            commit()
        }
        finish()
    }

    private fun unsubscribeFirebaseMessagingTopic(company: String?) {
        company?.let {
            FirebaseMessaging
                .getInstance()
                .unsubscribeFromTopic("app-$it")
                .addOnCompleteListener { task ->
                    when (task.isSuccessful) {
                        true -> Log.i("FIREBASE:", "Topic unsubscribed: app-$it")
                        false -> Log.i("FIREBASE:", "Failed to unsubscribe topic: app-$it")
                    }
                }
        }
    }

    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount > 1 -> returnToRoot()
            else -> checkDoubleBackToExit()
        }
    }

    private fun returnToRoot() {
        super.onBackPressed()
        uncheckAllNavigationViewItems()
    }

    private fun uncheckAllNavigationViewItems() {
        val size = nav_view.menu.size()
        for (i in 0 until size)
            nav_view.menu.getItem(i).isChecked = false
    }

    private fun checkDoubleBackToExit() {
        if (doubleBackToExit) {
            finish()
        } else {
            doubleBackToExit = true
            Snackbar.make(drawer_layout, getString(R.string.pressione_voltar_novamente), Snackbar.LENGTH_LONG).show()
            Handler().postDelayed({ doubleBackToExit = false }, 2000)
        }
    }

    private fun getColorBy(colorResource: Int): Int {
        return ContextCompat.getColor(this@MainActivity, colorResource)
    }

}