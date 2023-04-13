package jp.co.giftmall.sample.navigation.ui.home

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import jp.co.giftmall.sample.navigation.MainActivity
import jp.co.giftmall.sample.navigation.MainNavigationViewModel
import jp.co.giftmall.sample.navigation.R

class HomeFragment : Fragment() {
    private val navigationViewModel: MainNavigationViewModel by activityViewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showNotification()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            MaterialTheme {
                HomeScreen(
                    onClickOpenDashboard = {
                        // BottomNavigationView の tab_dashboard に切り替える
                        navigationViewModel.openDashboard()
                    },
                    onClickOpenNotification = {
                        // BottomNavigationView の tab_notifications に切り替える
                        navigationViewModel.openNotifications()
                    },
                    onClickOpenSettings = {
                        // 現在の NavGraph で navigation_settings に移動する
                        findNavController().navigate(R.id.navigation_settings)
                    },
                    onClickdispatchNotification = {
                        showNotification()
                    }
                )
            }
        }
    }

    private fun showNotification() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        val manager = NotificationManagerCompat.from(requireContext())
        if (manager.getNotificationChannel(NOTIFICATION_CHANNEL) == null) {
            manager.createNotificationChannel(
                NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL, NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setName("sample notification")
                    .setDescription("sample notification")
                    .build()
            )
        }
        // tab_dashboard をバックスタックに積む DeepLink を作成する。
        // この場合アプリ起動後の NavController が持つバックスタックには
        // 1. トップレベルの NavGraph の Start Destination (このサンプルアプリなら tab_home)
        // 2. tab_dashboard
        // の 2 つが存在することになる。
        val dashboardIntent = NavDeepLinkBuilder(requireContext())
            .setGraph(R.navigation.mobile_navigation)
            .addDestination(R.id.tab_dashboard)
            .setComponentName(ComponentName(requireContext().packageName, MainActivity::class.java.name))
            .createPendingIntent()
        // tab_dashboard と navigation_settings をバックスタックに積む DeepLink を作成する。
        // この場合アプリ起動後の NavController が持つバックスタックには
        // 1. トップレベルの NavGraph の Start Destination (このサンプルアプリなら tab_home)
        // 2. tab_dashboard
        // 3. navigation_settings
        // の 3 つが存在することになる。
        val settingsIntent = NavDeepLinkBuilder(requireContext())
            .setGraph(R.navigation.mobile_navigation)
            .addDestination(R.id.tab_dashboard)
            .addDestination(R.id.navigation_settings)
            .setComponentName(ComponentName(requireContext().packageName, MainActivity::class.java.name))
            .createPendingIntent()
        val notification = NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL)
            .setContentTitle("test")
            .setContentText("this is a test notification with deep linking")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(0, "Dashboard", dashboardIntent)
            .addAction(0, "Settings", settingsIntent)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroyView() {
        notificationPermissionLauncher.unregister()
        super.onDestroyView()
    }

    companion object {
        private const val NOTIFICATION_ID = 234
        private const val NOTIFICATION_CHANNEL = "sample_channel"
    }
}
