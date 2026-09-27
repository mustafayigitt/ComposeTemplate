package com.ytapps.composetemplate.convention

import org.gradle.api.GradleException
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DataStrategyProjectorTest {
    @Test
    fun `required replacement updates exactly one occurrence`() {
        val file = Files.createTempFile("projector", ".txt").toFile().apply { writeText("before target after") }

        with(DataStrategyProjector) {
            file.replaceRequiredExactly("target", "updated")
        }

        assertEquals("before updated after", file.readText())
    }

    @Test
    fun `required replacement rejects missing content`() {
        val file = Files.createTempFile("projector", ".txt").toFile().apply { writeText("before after") }

        assertFailsWith<GradleException> {
            with(DataStrategyProjector) {
                file.replaceRequiredExactly("target", "updated")
            }
        }
    }

    @Test
    fun `required replacement rejects duplicate content`() {
        val file = Files.createTempFile("projector", ".txt").toFile().apply { writeText("target and target") }

        assertFailsWith<GradleException> {
            with(DataStrategyProjector) {
                file.replaceRequiredExactly("target", "updated")
            }
        }
    }
}
