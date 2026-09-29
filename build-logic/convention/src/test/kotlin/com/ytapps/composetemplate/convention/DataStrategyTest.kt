package com.ytapps.composetemplate.convention

import org.gradle.api.GradleException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DataStrategyTest {
    @Test
    fun `default strategy is remote`() {
        assertEquals(DataStrategy.REMOTE, DataStrategy.parse(null))
    }

    @Test
    fun `all documented strategy values parse strictly`() {
        DataStrategy.entries.forEach { strategy ->
            assertEquals(strategy, DataStrategy.parse(strategy.cliValue))
        }
    }

    @Test
    fun `unknown strategy is rejected`() {
        assertFailsWith<GradleException> {
            DataStrategy.parse("hybrid")
        }
    }
}
