package jp.co.giftmall.sample.navigation.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun SettingsScreen(
    onClickBackToHome: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Button(
                onClick = onClickBackToHome,
            ) {
                Text(text = "Back to Home")
            }
        }
    }
}