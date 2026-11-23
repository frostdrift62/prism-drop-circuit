package com.Korgaga1.Zonkow.presentation.howto

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Korgaga1.Zonkow.R
import com.Korgaga1.Zonkow.core.di.ServiceLocator
import com.Korgaga1.Zonkow.core.theme.AccentCyan
import com.Korgaga1.Zonkow.core.theme.TextPrimary
import com.Korgaga1.Zonkow.core.theme.TextSecondary
import com.Korgaga1.Zonkow.core.ui.CircuitBackground
import com.Korgaga1.Zonkow.core.ui.HudCard
import com.Korgaga1.Zonkow.core.ui.SecondaryButton
import com.Korgaga1.Zonkow.domain.model.HowToStep

private val HowToOverlay = listOf(
    Color(0xD90A0014),
    Color(0xB30D1B2A),
    Color(0xE61A0038)
)

@Composable
fun HowToPlayScreen(onMenu: () -> Unit) {
    val viewModel: HowToPlayViewModel = viewModel(factory = ServiceLocator.factory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    HowToPlayContent(state = state, onMenu = onMenu)
}

@Composable
private fun HowToPlayContent(state: HowToPlayUiState, onMenu: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        CircuitBackground(overlay = HowToOverlay)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp)
        ) {
            Text(
                text = stringResource(R.string.how_to_play),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(top = 24.dp)
            )
            if (state.steps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.nothing_recorded),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.steps.forEach { step ->
                        HowToRow(
                            step = step,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }
            SecondaryButton(
                label = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun HowToRow(step: HowToStep, modifier: Modifier = Modifier) {
    HudCard(modifier = modifier, corner = 18.dp) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(step.imageRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(52.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = stringResource(step.titleRes),
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentCyan
                )
                Text(
                    text = stringResource(step.textRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
