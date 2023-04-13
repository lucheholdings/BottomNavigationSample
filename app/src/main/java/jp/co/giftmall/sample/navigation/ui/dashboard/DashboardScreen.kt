package jp.co.giftmall.sample.navigation.ui.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun DashboardScreen(
    onClickOpenSettings: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Button(
                onClick = onClickOpenSettings,
            ) {
                Text(text = "Open Settings")
            }
        }
    }
}