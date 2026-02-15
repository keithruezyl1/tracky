package com.tracky.app.ui.screens.entrydetail

import androidx.lifecycle.SavedStateHandle
import com.tracky.app.data.repository.FoodLoggingRepository
import com.tracky.app.data.repository.FoodsRepository
import com.tracky.app.data.repository.ResolvedFoodResult
import com.tracky.app.domain.logic.NutritionConflictDetector
import com.tracky.app.domain.model.*
import com.tracky.app.test.rules.MainDispatcherRule
import com.tracky.app.util.CanonicalKeyGenerator
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EntryDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EntryDetailViewModel
    private val loggingRepository: FoodLoggingRepository = mockk(relaxed = true)
    private val foodsRepository: FoodsRepository = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
    private val nutritionConflictDetector: NutritionConflictDetector = mockk(relaxed = true)
    // mock backendApi if needed, but it seems only used for exercise currently in the implementation shown
    // wait, reanalyzeExerciseItem uses backendApi directly in the code I saw earlier? 
    // Yes: val response = backendApi.resolveExercise(...) 
    // I need to mock BackendApi too if I want to test exercise reanalysis.
    // However, let's focus on Food first as it's the primary complex logic with resolving.
    
    // Actually, looking at EntryDetailViewModel constructor:
    // @Inject constructor(
    //    savedStateHandle: SavedStateHandle,
    //    private val loggingRepository: FoodLoggingRepository,
    //    private val foodsRepository: FoodsRepository,
    //    private val backendApi: TrackyBackendApi, // Need this
    //    private val nutritionConflictDetector: NutritionConflictDetector,
    //    private val canonicalKeyGenerator: CanonicalKeyGenerator
    // )

    private val backendApi: com.tracky.app.data.api.TrackyBackendApi = mockk(relaxed = true)
    private val canonicalKeyGenerator: CanonicalKeyGenerator = mockk(relaxed = true)

    @Before
    fun setup() {
        savedStateHandle["entryId"] = 1L
        savedStateHandle["entryType"] = "food"
        
        viewModel = EntryDetailViewModel(
            savedStateHandle = savedStateHandle,
            loggingRepository = loggingRepository,
            foodsRepository = foodsRepository,
            backendApi = backendApi,
            nutritionConflictDetector = nutritionConflictDetector,
            canonicalKeyGenerator = canonicalKeyGenerator
        )
    }

    @Test
    fun `reanalyzeFoodItem - Manual Source + Material Diff - Creates Pending Suggestion`() = runTest {
        // Arrange
        val itemId = 100L
        val revision = 1L
        val currentItem = createFoodItem(itemId).copy(
            isManualMacros = true, 
            analysisRevision = revision,
            calories = 500f // Manual override
        )
        val entry = createFoodEntry(items = listOf(currentItem))
        
        val newFoodItem = createFoodItem(itemId).copy(calories = 100f) // API Result
        val resolveResult = ResolvedFoodResult.Success(newFoodItem)

        // Mocks
        coEvery { loggingRepository.getFoodEntryById(1L) } returns entry // Initial fetch
        coEvery { foodsRepository.resolveFood(any(), any(), any()) } returns resolveResult
        every { nutritionConflictDetector.isMaterialDifference(any<FoodItem>(), any<FoodItem>()) } returns true
        
        // We need to return the entry again when re-fetched inside reanalyzeFoodItem
        // Logic: 
        // 1. Fetch current (mocked above)
        // 2. Resolve (mocked above)
        // 3. Fetch fresh (needs to be mocked again potentially or same)
        
        // Act
        // Accessing private method via reflection or just triggering the public trigger? 
        // triggerEntryReanalysis calls it.
        // Or if there is a public way? 
        // Actually `reanalyzeFoodItem` is private. 
        // But `updateFoodEntry` calls it if name changed.
        // Or `triggerEntryReanalysis` calls it.
        
        // Let's use `triggerEntryReanalysis`
        viewModel.triggerEntryReanalysis()
        
        // Assert
        coVerify { loggingRepository.updateFoodEntry(any()) } 
        // We need to capture the update in step 3 (final update with suggestion)
        // The first update in triggerEntryReanalysis sets isAnalyzing = true.
        // The second update (in reanalyzeFoodItem) sets the result.
        
        // Best to capture all updates
        val slot = slot<FoodEntry>()
        coVerify(atLeast = 1) { loggingRepository.updateFoodEntry(capture(slot)) }
        
        // The last captured value should have the suggestion
        val updatedItem = slot.captured.items.find { it.id == itemId }
        assertNotNull(updatedItem?.pendingSuggestion)
        assertEquals(100f, updatedItem?.pendingSuggestion?.calories)
        assertEquals(500f, updatedItem?.calories) // Original kept
    }

    @Test
    fun `reanalyzeFoodItem - AI Source + No Manual Edit - Overwrites`() = runTest {
        // Arrange
        val itemId = 101L
        val revision = 1L
        val currentItem = createFoodItem(itemId).copy(
            isManualMacros = false,
            provenance = Provenance(ProvenanceSource.DATASET, null, 1f),
            calories = 500f
        )
        val entry = createFoodEntry(items = listOf(currentItem))
        
        val newFoodItem = createFoodItem(itemId).copy(calories = 100f)
        val resolveResult = ResolvedFoodResult.Success(newFoodItem)

        coEvery { loggingRepository.getFoodEntryById(1L) } returns entry
        coEvery { foodsRepository.resolveFood(any(), any(), any()) } returns resolveResult
        
        // Act
        viewModel.triggerEntryReanalysis()
        
        // Assert
        val slot = slot<FoodEntry>()
        coVerify(atLeast = 1) { loggingRepository.updateFoodEntry(capture(slot)) }
        
        val updatedItem = slot.captured.items.find { it.id == itemId }
        assertNull(updatedItem?.pendingSuggestion)
        assertEquals(100f, updatedItem?.calories) // Overwritten
    }
    
    private fun createFoodItem(id: Long): FoodItem {
        return FoodItem(
            id = id,
            name = "Test Food",
            quantity = 1f,
            unit = "serving",
            calories = 0f,
            carbsG = 0f,
            proteinG = 0f,
            fatG = 0f,
            provenance = Provenance(ProvenanceSource.DATASET, null, 1f),
            displayOrder = 0,
            canonicalKey = "test",
            isManualMacros = false,
            isAnalyzing = false
        )
    }

    private fun createFoodEntry(items: List<FoodItem>): FoodEntry {
        return FoodEntry(
            id = 1L,
            items = items,
            date = "2023-01-01",
            time = "12:00",
            totalCalories = 0f,
            totalCarbsG = 0f,
            totalProteinG = 0f,
            totalFatG = 0f,
            originalInput = null,
            updatedAt = System.currentTimeMillis()
        )
    }
}
