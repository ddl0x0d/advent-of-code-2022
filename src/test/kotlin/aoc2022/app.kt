package aoc2022

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class AppTest : StringSpec({
    "current year should be 2022" {
        LocalDate.now().year shouldBe 2022
    }
})
