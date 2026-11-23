package com.Korgaga1.Zonkow.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Korgaga1.Zonkow.R
import com.Korgaga1.Zonkow.core.di.ServiceLocator
import com.Korgaga1.Zonkow.core.theme.AccentCyan
import com.Korgaga1.Zonkow.core.theme.AccentViolet
import com.Korgaga1.Zonkow.core.theme.InertLane
import com.Korgaga1.Zonkow.core.theme.SurfaceElevated
import com.Korgaga1.Zonkow.core.theme.TextMuted
import com.Korgaga1.Zonkow.core.theme.TextPrimary
import com.Korgaga1.Zonkow.core.theme.TextSecondary
import com.Korgaga1.Zonkow.core.ui.CircuitBackground
import com.Korgaga1.Zonkow.core.ui.HudCard
import com.Korgaga1.Zonkow.core.ui.PrimaryButton
import com.Korgaga1.Zonkow.core.ui.SecondaryButton
import com.Korgaga1.Zonkow.data.sample.SampleData
import com.Korgaga1.Zonkow.domain.model.Tier

private val SettingsOverlay = listOf(
    Color(0xD90A0014),
    Color(0xB31A0038),
    Color(0xE60D1B2A)
)

@Composable
fun SettingsScreen(onMenu: () -> Unit) {
    val viewModel: SettingsViewModel = viewModel(factory = ServiceLocator.factory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    SettingsContent(
        state = state,
        onTierSelected = viewModel::onTierSelected,
        onHighContrastChanged = viewModel::onHighContrastChanged,
        onReducedMotionChanged = viewModel::onReducedMotionChanged,
        onResetRequested = viewModel::onResetRequested,
        onResetCancelled = viewModel::onResetCancelled,
        onResetConfirmed = viewModel::onResetConfirmed,
        onMenu = onMenu
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onTierSelected: (Tier) -> Unit,
    onHighContrastChanged: (Boolean) -> Unit,
    onReducedMotionChanged: (Boolean) -> Unit,
    onResetRequested: () -> Unit,
    onResetCancelled: () -> Unit,
    onResetConfirmed: () -> Unit,
    onMenu: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CircuitBackground(overlay = SettingsOverlay)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(top = 24.dp)
            )
            TierSection(
                tier = state.tier,
                onTierSelected = onTierSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
            ToggleRow(
                title = stringResource(R.string.high_contrast),
                hint = stringResource(R.string.hint_contrast),
                checked = state.highContrastMarks,
                onCheckedChange = onHighContrastChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            ToggleRow(
                title = stringResource(R.string.reduced_motion),
                hint = stringResource(R.string.hint_motion),
                checked = state.reducedMotion,
                onCheckedChange = onReducedMotionChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            ResetSection(
                hasHistory = state.hasHistory,
                confirming = state.confirmingReset,
                onResetRequested = onResetRequested,
                onResetCancelled = onResetCancelled,
                onResetConfirmed = onResetConfirmed
            )
            SecondaryButton(
                label = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun TierSection(
    tier: Tier,
    onTierSelected: (Tier) -> Unit,
    modifier: Modifier = Modifier
) {
    HudCard(modifier = modifier, corner = 18.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.setting_tier),
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.hint_tier),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Tier.entries.forEach { option ->
                    val selected = option == tier
                    val label = stringResource(SampleData.tierLabel(option))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .background(
                                color = if (selected) {
                                    AccentViolet.copy(alpha = 0.55f)
                                } else {
                                    SurfaceElevated.copy(alpha = 0.6f)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onTierSelected(option) }
                            .semantics { stateDescription = label },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) TextPrimary else TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
            Text(
                text = stringResource(SampleData.tierDetail(tier)),
                style = MaterialTheme.typography.bodySmall,
                color = AccentCyan,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    hint: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val stateLabel = stringResource(
        if (checked) R.string.state_on else R.string.state_off
    )
    HudCard(modifier = modifier, corner = 18.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TextPrimary,
                    checkedTrackColor = AccentViolet,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = InertLane
                ),
                modifier = Modifier.semantics { stateDescription = stateLabel }
            )
        }
    }
}

@Composable
private fun ResetSection(
    hasHistory: Boolean,
    confirming: Boolean,
    onResetRequested: () -> Unit,
    onResetCancelled: () -> Unit,
    onResetConfirmed: () -> Unit
) {
    if (!hasHistory) {
        Text(
            text = stringResource(R.string.nothing_recorded),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        return
    }
    if (confirming) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryButton(
                label = stringResource(R.string.confirm),
                onClick = onResetConfirmed,
                height = 50.dp,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                label = stringResource(R.string.cancel),
                onClick = onResetCancelled,
                height = 50.dp,
                modifier = Modifier.weight(1f)
            )
        }
        return
    }
    TextButton(
        onClick = onResetRequested,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
    ) {
        Text(
            text = stringResource(R.string.reset_progress),
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted
        )
    }
}
