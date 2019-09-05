package br.com.suitesistemas.portsmobile.view.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.view.fragment.CustomerFragment
import br.com.suitesistemas.portsmobile.view.fragment.FinancialFragment
import br.com.suitesistemas.portsmobile.view.fragment.ResourcesFragment
import br.com.suitesistemas.portsmobile.view.fragment.SaleFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_bar.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ResourcesFragment.Delegate {

    private var doubleBackToExit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureActionBar()
        configureNavigationView()
        addRootFragment()
    }

    private fun configureActionBar() {
        configureToolbar()
        configureMenu()
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(getColorBy(android.R.color.white))
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

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_left, 0, R.anim.enter_from_left, R.anim.exit_to_left)
        transaction.replace(R.id.main_frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun getColorBy(colorResource: Int): Int {
        return ContextCompat.getColor(this@MainActivity, colorResource)
    }

    override fun onResourceSelected(fragment: androidx.fragment.app.Fragment) {
        closeAllFragmentsAndReplaceTo(fragment)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_customer -> closeAllFragmentsAndReplaceTo(CustomerFragment())
            R.id.menu_sale -> closeAllFragmentsAndReplaceTo(SaleFragment())
//            R.id.menu_order -> closeAllFragmentsAndReplaceTo(OrderFragment())
            R.id.menu_financial -> closeAllFragmentsAndReplaceTo(FinancialFragment())
//            R.id.menu_task -> closeAllFragmentsAndReplaceTo(TaskFragment())
//            R.id.menu_crm -> closeAllFragmentsAndReplaceTo(CRMFragment())
            R.id.menu_logout -> logout()
        }
        drawer_layout.closeDrawers()
        return true
    }

    private fun closeAllFragmentsAndReplaceTo(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        addRootFragment()
        replaceFragment(fragment)
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("userResponse", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.commit()
        finish()
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
}

