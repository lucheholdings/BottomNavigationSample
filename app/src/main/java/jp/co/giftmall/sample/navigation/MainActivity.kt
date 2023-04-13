package jp.co.giftmall.sample.navigation

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.IdRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import jp.co.giftmall.sample.navigation.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainNavigationViewModel: MainNavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val weakRefToNavView: WeakReference<BottomNavigationView> = WeakReference(navView)

        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = fragment.navController

        val appBarConfiguration = AppBarConfiguration(TAB_IDS)
        setupActionBarWithNavController(navController, appBarConfiguration)
        // 下記の理由により BottomNavigationView#setupWithNavController は使わず自分で
        // BottomNavigationView#setupWithNavController と同じような実装を作る
        // 1. BottomNavigationView の選択状態と現在表示している画面とが一致しないことがある (setOnItemSelectedListener のコメントにて解説)
        // 2. DeepLink によるアプリ起動時、バックスタックの積み方に合わせた BottomNavigationView の選択状態とならない (addOnDestinationChangedListener のコメントにて解説)
        // navView.setupWithNavController(navController)
        navView.setOnItemSelectedListener { item ->
            Log.v(LOG_TAG, "Bottom navigation menu selected, doing the same as what the ext function does internally")
            NavigationUI.onNavDestinationSelected(item, navController)
            // onNavDestinationSelected は MenuItem に対応する child navigation graph のバックスタックから適切な Destination を呼び戻す。
            // Multiple Backstack をサポートする Jetpack Navigation 2.4.0 以後、child navigation graph のバックスタックから呼び戻される Destination は
            // child navigation graph が管理しているバックスタックの先頭にある Destination である。
            // ここでその Destination が Bottom Navigation のタブに対応する Destination でないとき、NavigationUI.onNavDestinationSelected の返り値は false となり
            // それをそのまま OnItemSelectedListener の返り値としてしまうとタブを選択して画面を切り替えたのにタブの選択状態が変わらない現象が発生してしまう。
            // この現象を回避するために setupWithNavController の内部で実行している BottomNavigationView#setOnItemSelectedListener を上書きし、
            // OnItemSelectedListener が常に true を返すことで呼び戻した Destination に関係なくタブの選択状態が変わるようにする。
            // BottomNavigationViwe#setupWithNavController でも BottomNavigationView#setOnItemSelectedListener を呼び出しているので、
            // この上書きは必ず BottomNavigationView#setupWithNavController のあとに実行する。
            true
        }
        // 選択状態にある Bottom Navigation のタブを再度選択したとき、そのタブの child navigation graph で設定している Start Destination まで戻す。
        // この処理は BottomNavigationViwe#setupWithNavController にはないので自分で実装する必要がある。
        navView.setOnItemReselectedListener { item ->
            Log.v(LOG_TAG, "Bottom navigation menu reselected")
            backToBottomNavigationMenuDestination(navController, item.itemId)
        }
        // BottomNavigationView のタブ切り替えではなく、Fragment 内部のイベント処理で Bottom Navigation のタブに対応する画面を開きたいとき（例えばホーム画面内のボタンからダッシュボード画面へ遷移する場合）
        // NavController#navigate で NavOptions を使わず直接画面遷移をしてしまうと、元いたタブを選択しても画面遷移が発生せず戻れなくなる（戻るボタンによる遷移は問題ない）。
        // これを回避するための方法として、タブに対応する Destination への遷移の場合のみ BototmNavigationView から MenuItem を取り出し、
        // BottomNavigationView のタブ切り替えと全くおなじ処理によって画面遷移を実行する。
        // 他の解決方法として、遷移先タブと遷移元の組み合わせを持った Navigation Action を XML で設定する方法も考えられるが、
        // 遷移後のバックキーの挙動が変わったり、設定内容によっては遷移元へ戻る操作で例外となることがあるため適切でない。
        mainNavigationViewModel.navigationIdFlow
            .onEach { navId ->
                if (navView.selectedItemId == navId) {
                    Log.v(LOG_TAG, "The same menu selected, behave similarly to Bottom navigation menu reselection")
                    backToBottomNavigationMenuDestination(navController, navId)
                } else {
                    Log.v(LOG_TAG, "Changing the tab selection, go to the new destination")
                    NavigationUI.onNavDestinationSelected(navView.menu.findItem(navId), navController)
                }
            }.launchIn(lifecycleScope)
        // DeepLink アプリを起動したとき、何もしないと常に BottomNavigationView は
        // トップレベルの NavGraph の Start Destination (このサンプルアプリなら tab_home) に対応するタブを選択状態にする。
        // たとえば NavDeepLinkBuilder の addDestination で tab_dashboard と navigation_settings の 2 つを
        // 設定してアプリを起動した場合、navigation_settings を開いた時点では tab_home が選択状態になっているが、
        // バックキー等で tab_dashboard に遷移すると BottomNavigationView の選択状態も tab_dashboard に変わる。
        // NavDeepLinkBuilder の設定で tab_dashboard を経由して navigation_settings を開いたとき
        // BottomNavigationViwe の tab_dashboard を選択状態としたい場合、OnDestinationChangedListener で
        // 現在の Destination を監視し、Back Queue から最も最近の BottomNavigationView に対応する Destination ID を
        // 探してきて selectedItemId とする。
        // NavDeepLinkBuilder で作成した PendingIntent でアプリを起動した場合、Activity#onNewIntent は呼ばれないため
        // OnDestinationChangedListener を使う。
        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    // 大まかな処理の流れ自体は BottomNavigationView#setupWithNavController と同じ
                    Log.v(LOG_TAG, "Destination changed: $destination")
                    val navigationView = weakRefToNavView.get()
                    if (navigationView == null) {
                        navController.removeOnDestinationChangedListener(this)
                    } else {
                        val latestTabDestination = controller.backQueue.lastOrNull { entry ->
                            TAB_IDS.contains(entry.destination.id)
                        }?.destination ?: return
                        // navView.selectedItemId = latestTabDestination.id では OnItemSelectedListener が発火してしまい
                        // NavigationUI.onNavDestinationSelected(item, navController) まで呼ばれて意図しない動作となるため
                        // 選択状態だけを切り替えたい (setupWithNavController でも同様に、selectedItemId を変えるのではなく MenuItem の isChecked を切り替えている)。
                        navigationView.menu.findItem(latestTabDestination.id).isChecked = true
                    }
                }
            }
        )
    }

    private fun backToBottomNavigationMenuDestination(
        navController: NavController,
        @IdRes id: Int,
    ) {
        val node = navController.currentDestination?.parent?.findNode(id) ?: return
        // 現在表示している Destination の親が NavGraph でないときは何もしない
        if (node !is NavGraph) {
            Log.v(LOG_TAG, "Not a nav graph, nothing we can do")
            return
        }
        // 現在表示している Destination と NavGraph の Start Destination が一致するので何もしなくてよい
        if (node.startDestinationId == navController.currentDestination?.id) {
            Log.v(LOG_TAG, "No need to change destination, we are already in the start destination")
            return
        }
        navController.popBackStack(node.startDestinationId, inclusive = false)
    }

    companion object {
        private const val LOG_TAG = "MainActivity"
        private val TAB_IDS = setOf(R.id.tab_home, R.id.tab_dashboard, R.id.tab_notifications)
    }
}