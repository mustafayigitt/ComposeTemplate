package com.ytapps.composetemplate.feature.onboarding.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.feature.auth.navigation.LoginRoute
import com.ytapps.composetemplate.feature.onboarding.navigation.OnboardingRoute
import com.ytapps.composetemplate.feature.onboarding.presentation.R
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(navigationManager: INavigationManager) {
    OnboardingScreenInternal(navigationManager = navigationManager)
}

@Composable
internal fun OnboardingScreenInternal(
    navigationManager: INavigationManager,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.totalPages })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is OnboardingEvent.NavigateToLogin -> {
                    navigationManager.navigateOver(
                        route = LoginRoute,
                        over = OnboardingRoute,
                    )
                }
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page -> viewModel.onPageChanged(page) }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) { page ->
            OnboardingPage(page = page)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (pagerState.currentPage == uiState.totalPages - 1) {
            Button(
                onClick = { viewModel.complete() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.onboarding_button_finish))
            }
        } else {
            Button(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.onboarding_button_next))
            }
        }
    }
}

@Composable
private fun OnboardingPage(page: Int) {
    val titleRes =
        when (page) {
            0 -> R.string.onboarding_step_1_title
            1 -> R.string.onboarding_step_2_title
            else -> R.string.onboarding_step_3_title
        }
    val descRes =
        when (page) {
            0 -> R.string.onboarding_step_1_desc
            1 -> R.string.onboarding_step_2_desc
            else -> R.string.onboarding_step_3_desc
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(descRes),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}
