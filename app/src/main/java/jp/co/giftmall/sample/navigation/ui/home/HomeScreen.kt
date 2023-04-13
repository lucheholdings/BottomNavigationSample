package jp.co.giftmall.sample.navigation.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun HomeScreen(
    onClickOpenDashboard: () -> Unit = {},
    onClickOpenNotification: () -> Unit = {},
    onClickOpenSettings: () -> Unit = {},
    onClickdispatchNotification: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Button(
                onClick = onClickOpenDashboard,
            ) {
                Text(text = "Open Dashboard")
            }
        }
        item {
            Button(
                onClick = onClickOpenNotification,
            ) {
                Text(text = "Open Notifications")
            }
        }
        item {
            Button(
                onClick = onClickOpenSettings,
            ) {
                Text(text = "Open Settings")
            }
        }
        item {
            Button(
                onClick = onClickdispatchNotification,
            ) {
                Text(text = "Dispatch a notification")
            }
        }
    }
}
