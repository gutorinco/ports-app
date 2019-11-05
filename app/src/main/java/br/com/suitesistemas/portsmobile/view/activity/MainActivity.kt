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
import br.com.suitesistemas.portsmobile.custom.extensions.getLoggedUser
import br.com.suitesistemas.portsmobile.model.enums.EYesNo
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.utils.IconUtils
import br.com.suitesistemas.portsmobile.view.fragment.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ResourcesFragment.Delegate {

    private var showFinancialRelease: Boolean = false
    private var doubleBackToExit: Boolean = false
    private var selectedMenuId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showFinancialRelease = getLoggedUser().permissoes.flg_visualizar_financeiro == EYesNo.S

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
        outState.putInt("menuItem", selectedMenuId)
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(getColorBy(android.R.color.white))
        configureMenu()
    }

    private fun configureMenu() {
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = getColorBy(android.R.color.white)
        toggle.syncState()
        configureCustomIcon(R.id.menu_product, R.string.fa_boxes_solid)
        configureCustomIcon(R.id.menu_order, R.string.fa_dolly_solid)
        configureCustomIcon(R.id.menu_crm, R.string.fa_handshake_solid)
        if (!showFinancialRelease) {
            val financialItem = nav_view.menu.findItem(R.id.menu_financial)
            financialItem.isVisible = false
        }
    }

    private fun configureCustomIcon(itemId: Int, iconId: Int) {
        val item = nav_view.menu.findItem(itemId)
        val icon = IconUtils.get(this, iconId, R.color.icons_custom)
        item.icon = icon
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
        super.onNewIntent(intent)
        when (intent?.action) {
            "br.com.suitesistemas.portsmobile_TARGET_CLIENTE" -> Handler().postDelayed({onResourceSelected(0)}, 1250)
            "br.com.suitesistemas.portsmobile_TARGET_VENDA" -> Handler().postDelayed({onResourceSelected(1)}, 1250)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> closeAllFragments(item)
            R.id.menu_customer -> closeAllFragmentsAndReplaceTo(item, CustomerFragment())
            R.id.menu_sale -> closeAllFragmentsAndReplaceTo(item, SaleFragment())
            R.id.menu_product -> closeAllFragmentsAndReplaceTo(item, ProductFragment())
            R.id.menu_color -> closeAllFragmentsAndReplaceTo(item, ColorFragment())
            R.id.menu_order -> closeAllFragmentsAndReplaceTo(item, OrderFragment())
            R.id.menu_model -> closeAllFragmentsAndReplaceTo(item, ModelFragment())
            R.id.menu_crm -> closeAllFragmentsAndReplaceTo(item, CRMFragment())
            R.id.menu_financial -> closeAllFragmentsAndReplaceTo(item, FinancialFragment())
//            R.id.menu_task -> closeAllFragmentsAndReplaceTo(item, TaskFragment())
            R.id.menu_config -> closeAllFragmentsAndReplaceTo(item, ConfigFragment())
            R.id.menu_logout -> logout()
//            R.id.menu_privacy -> {
//                val intent = Intent(this, PrivacyPolicyActivity::class.java)
//                startActivity(intent)
//            }
        }
        drawer_layout.closeDrawers()
        return true
    }

    private fun closeAllFragments(item: MenuItem) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        selectedMenuId = item.itemId
        item.isChecked = true
        addRootFragment()
    }

    private fun closeAllFragmentsAndReplaceTo(item: MenuItem, fragment: Fragment) {
        closeAllFragments(item)
        replaceFragment(fragment)
    }

    override fun onResourceSelected(itemPosition: Int) {
        return when (itemPosition) {
            0 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_customer), CustomerFragment())
            1 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_sale), SaleFragment())
            2 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_product), ProductFragment())
            3 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_color), ColorFragment())
            4 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_order), OrderFragment())
            5 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_model), ModelFragment())
            6 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_crm), CRMFragment())
            7 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_financial), FinancialFragment())
//            3 -> closeAllFragmentsAndReplaceTo(nav_view.menu.findItem(R.id.menu_customer), TaskFragment())
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