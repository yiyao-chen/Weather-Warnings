package com.example.in2000_team41

import com.example.in2000_team41.api.avalanche.AvalancheModel
import com.example.in2000_team41.api.avalanche.AvalancheWarning
import com.example.in2000_team41.api.forestfire.ForestfireModel
import com.example.in2000_team41.api.forestfire.LocationFireWarning
import com.example.in2000_team41.api.metalerts.AlertInfo
import com.example.in2000_team41.api.metalerts.AlertModel
import com.example.in2000_team41.api.metalerts.Area
import com.example.in2000_team41.ui.forestfire.ForestfireViewModel
import com.example.in2000_team41.ui.metalerts.MetAlertsAdapter
import com.example.in2000_team41.ui.metalerts.MetAlertsFilter
import com.example.in2000_team41.ui.metalerts.MetAlertsFragment
import com.example.in2000_team41.ui.metalerts.MetAlertsViewModel
import com.example.in2000_team41.ui.winter.WinterViewModel
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import java.util.logging.Filter

class SortingTest {

    // executed before each test
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

    }

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


    // teste sortering av metAlerts
    @Spy
    var spyMetAlertsViewModel = MetAlertsViewModel()

    @Test
    fun test_sortMetAlerts() {
        testList = mutableListOf<AlertModel>(a1, a2, a3, a4, a5)

        // expected results
        val alphabetical = mutableListOf<AlertModel>(a3, a1, a2, a5, a4)
        val nearMe = mutableListOf<AlertModel>(a1, a5,a4,a2, a3)
        val descending = mutableListOf<AlertModel>(a1, a3, a4, a2, a5)
        val ascending = mutableListOf<AlertModel>(a5, a2, a4, a3, a1)

        spyMetAlertsViewModel.updateMetFullList(testList)
        `when`(spyMetAlertsViewModel.setMetFullList()).thenReturn(testList)

        // alphabetical order
        assertEquals(alphabetical, spyMetAlertsViewModel.sortMetAlerts(1))
        // closest to user location
        assertEquals(nearMe, spyMetAlertsViewModel.sortMetAlerts(2))
        // descending order
        assertEquals(descending, spyMetAlertsViewModel.sortMetAlerts(3))
        // ascending order
        assertEquals(ascending, spyMetAlertsViewModel.sortMetAlerts(4))
    }





    // teste sortering av forestfire
    // forestfire parameters
    val l1 = LocationFireWarning(null, "Agder", null, "7")
    val l2 = LocationFireWarning(null, "Åna", null, "10")
    val l3 = LocationFireWarning(null, "Østafjells", null, "3")

    var testListFire = mutableListOf<ForestfireModel>()

    @Spy
    var spyForestfireViewModel = ForestfireViewModel()
    @Test
    fun test_sortForestfire() {
        testListFire = mutableListOf(ForestfireModel(null, mutableListOf(l1, l2, l3)))

        // expected results
        val alphabetical = mutableListOf(ForestfireModel(null, mutableListOf(l1, l3, l2)))
        val descending = mutableListOf(ForestfireModel(null, mutableListOf(l2, l1, l3)))
        val ascending = mutableListOf(ForestfireModel(null, mutableListOf(l3, l1, l2)))

        spyForestfireViewModel.updateFullList(testListFire)
        `when`(spyForestfireViewModel.setforestfireFullList()).thenReturn(testListFire)

        // alphabetical order
        assertEquals(alphabetical, spyForestfireViewModel.sortForestfire(1))
        // descending order
        assertEquals(descending, spyForestfireViewModel.sortForestfire(2))
        // ascending order
        assertEquals(ascending, spyForestfireViewModel.sortForestfire(3))
    }


    // teste sortering av avalanche
    val aw1 = AvalancheWarning("Oslo", null, null, "3", null, null,null,null,null)
    val aw2 = AvalancheWarning("Agder", null, null, "1", null, null,null,null,null)
    val aw3 = AvalancheWarning("Åna", null, null, "4", null, null,null,null,null)
    val aw4 = AvalancheWarning("Østafjells", null, null, "2", null, null,null,null,null)

    val av1 = AvalancheModel(1, null, null, null, listOf(aw1,aw2,aw3)) //dangerlevel: 3,1,4
    val av2 = AvalancheModel(2, null, null, null, listOf(aw1,aw2,aw4)) // 3,1,2
    val av3 = AvalancheModel(2, null, null, null, listOf(aw2,aw4,aw1)) // 1,2,3
    val av4 = AvalancheModel(2, null, null, null, listOf(aw2,aw1,aw4)) // 1,3,2

    @Spy
    val spyWinterViewModel = WinterViewModel()

    @Test
    fun test_sortAvalanche(){
        val testList = mutableListOf<AvalancheModel>(av1, av2, av3, av4)

        // expected results
        val alphabetical = mutableListOf<AvalancheModel>(av2, av1, av4,av3)
        val descending = mutableListOf<AvalancheModel>(av1, av2, av4,av3)
        val ascending = mutableListOf<AvalancheModel>(av3,av4,av2, av1)

        spyWinterViewModel.updateFullList(testList)
        `when`(spyWinterViewModel.setFullList()).thenReturn(testList)

        // descending order
        assertEquals(descending, spyWinterViewModel.sortAvalanche(2))
        // ascending order
        assertEquals(ascending, spyWinterViewModel.sortAvalanche(3))
        // alphabeticalnding order
        assertEquals(ascending, spyWinterViewModel.sortAvalanche(1))


    }
}