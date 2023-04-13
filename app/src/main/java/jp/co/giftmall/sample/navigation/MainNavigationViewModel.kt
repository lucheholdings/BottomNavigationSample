package jp.co.giftmall.sample.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// MainActivity がもつ BottomNavigationView のタブ切り替えを Fragment から実行するための Facade を提供する
// Navigation 用の ViewModel
class MainNavigationViewModel : ViewModel() {
    private val navigationIdMutation: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)
    val navigationIdFlow: SharedFlow<Int> = navigationIdMutation.asSharedFlow()

    fun openHome() {
        navigationIdMutation.tryEmit(R.id.tab_home)
    }

    fun openDashboard() {
        navigationIdMutation.tryEmit(R.id.tab_dashboard)
    }

    fun openNotifications() {
        navigationIdMutation.tryEmit(R.id.tab_notifications)
    }
}