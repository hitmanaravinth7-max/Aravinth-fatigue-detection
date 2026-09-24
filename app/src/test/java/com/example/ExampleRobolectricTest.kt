package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BusinessProfile
import com.example.data.repository.BusinessRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AI Business Consultant", appName)
  }

  @Test
  fun `verify business profile metrics calculation`() {
    val profile = BusinessProfile(
      monthlyRevenue = 50000.0,
      monthlyExpenses = 30000.0,
      customerCount = 1000,
      marketingBudget = 5000.0
    )

    assertEquals(20000.0, profile.netProfit, 0.01)
    assertEquals(40.0, profile.profitMarginPercentage, 0.01)
    assertEquals(50.0, profile.averageRevenuePerCustomer, 0.01)
    assertTrue(profile.healthScore > 50)
  }

  @Test
  fun `verify repository recommendations and forecast generation`() = runTest {
    val repository = BusinessRepository()
    val recommendations = repository.recommendations.value
    assertTrue("Recommendations should not be empty", recommendations.isNotEmpty())

    val forecast = repository.calculatePrediction(marketingMultiplier = 1.2f, conversionBoostPercentage = 10.0f)
    assertTrue("Forecasted sales should exceed current sales", forecast.predictedSales12M > forecast.currentSales)
    assertTrue("Growth percentage should be positive", forecast.growthPercentage > 0.0)

    val qnaResult = repository.analyzeBusinessQuestion("How can we increase sales?")
    assertTrue(qnaResult.isRelevant)
    assertNotNull(qnaResult.directAnswer)
    assertTrue(qnaResult.recommendedActions.isNotEmpty())
  }
}

