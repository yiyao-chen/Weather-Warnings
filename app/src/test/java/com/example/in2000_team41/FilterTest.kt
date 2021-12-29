package com.example.in2000_team41

import com.example.in2000_team41.api.metalerts.AlertInfo
import com.example.in2000_team41.api.metalerts.AlertModel
import com.example.in2000_team41.api.metalerts.Area
import com.example.in2000_team41.ui.metalerts.MetAlertsAdapter
import com.example.in2000_team41.ui.metalerts.MetAlertsFilter
import com.example.in2000_team41.ui.metalerts.MetAlertsFragment
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.Spy

class FilterTest {
    val a1 = AlertModel(
        0.001, "1", null, "1", null, null, null, AlertInfo(
            null, null, null, null, null, null, null, null, null, null, mapOf("awareness_level" to "10; m; m"),
            Area("Oslo", "", null, null)
        )
    )
    val a2 = AlertModel(
        1111.33, "2", null, "1", null, null, null, AlertInfo(
            null, null, null, null, null, null, null, null, null, null, mapOf("awareness_level" to "5; m; m"),
            Area("Sandefjord", "", null, null)
        )
    )
    val a3 = AlertModel(
        2222.2, "3", null, "1", null, null, null, AlertInfo(
            null, null, null, null, null, null, null, null, null, null, mapOf("awareness_level" to "8; m; m"),
            Area("Kronstad", "", null, null)
        )
    )
    val a4 = AlertModel(
        92.2, "4", null, "1", null, null, null, AlertInfo(
            null, null, null, null, null, null, null, null, null, null, mapOf("awareness_level" to "6; m; m"),
            Area("Åna", "", null, null)
        )
    )
    val a5 = AlertModel(
        52.2, "5", null, "1", null, null, null, AlertInfo(
            null, null, null, null, null, null, null, null, null, null, mapOf("awareness_level" to "4; m; m"),
            Area("Østafjells", "", null, null)
        )
    )


    var testList = mutableListOf<AlertModel>()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testList = mutableListOf<AlertModel>(a1, a2, a3, a4, a5)
    }

    // teste filtrering av metAlerts
    @Spy
    var spyAdapter = MetAlertsAdapter(MetAlertsFragment(), "oslo")
    var spyMetAlertsFilter = MetAlertsFilter(testList, spyAdapter)

    @Test
    fun test_performFiltering() {
        spyMetAlertsFilter = MetAlertsFilter(testList, spyAdapter)

        // constraint = "Oslo"
        var expectedSize = 1
        Assert.assertEquals(expectedSize, spyMetAlertsFilter.performFilteringTest("Oslo").size)

        // constraint = "ø"
        expectedSize = 1
        Assert.assertEquals(expectedSize, spyMetAlertsFilter.performFilteringTest("ø").size)

        // constraint = "s"
        expectedSize = 4
        Assert.assertEquals(expectedSize, spyMetAlertsFilter.performFilteringTest("s").size)

        // constraint = "S"
        expectedSize = 4
        Assert.assertEquals(expectedSize, spyMetAlertsFilter.performFilteringTest("S").size)

        // constraint = ""
        expectedSize = 5
        Assert.assertEquals(expectedSize, spyMetAlertsFilter.performFilteringTest("").size)
    }
}