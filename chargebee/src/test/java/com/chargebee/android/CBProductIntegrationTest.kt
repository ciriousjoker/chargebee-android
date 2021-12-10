package com.chargebee.android

import android.os.Build
import android.util.Log
import com.android.billingclient.api.SkuDetails
import com.chargebee.android.billingservice.CBPurchase
import com.chargebee.android.exceptions.ChargebeeResult
import com.chargebee.android.models.*
import com.chargebee.android.models.SubscriptionDetail.Companion.retrieveSubscription
import com.chargebee.android.network.Auth
import com.chargebee.android.network.CBAuthResponse
import com.chargebee.android.network.CBAuthentication
import com.chargebee.android.network.CBAuthenticationBody.Companion.fromCBAuthBody
import com.chargebee.android.network.CBReceiptResponse
import com.chargebee.android.resources.CatalogVersion
import com.google.gson.JsonObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class CBProductIntegrationTest  {

    @Before
    fun setUp() {
        //Chargebee.configure(site = "omni1-test.integrations", publishableApiKey = "test_rpKneFyplowONFtdHgnlpxh6ccdcQXNUcu", sdkKey = "cb-pte6d5ltebfrnpxcnw4s5kcl2m")
        Chargebee.configure(
            site = "cb-imay-test",
            publishableApiKey = "test_EojsGoGFeHoc3VpGPQDOZGAxYy3d0FF3",
            sdkKey = "cb-j53yhbfmtfhfhkmhow3ramecom"
        )
    }

    @After
    fun afterTest() {
    }

    @Test
    fun test_authentication_success(){
        val lock = CountDownLatch(1)
        val auth = Auth(
            Chargebee.sdkKey,
            Chargebee.applicationId,
            Chargebee.appName,
            Chargebee.channel
        )
        CBAuthentication.authenticate(auth) {
            when (it) {
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("response :"+it.data)
                    assertThat((it.data), instanceOf(CBAuthResponse::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Error :"+it.exp.message)
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }
    @Test
    fun test_authentication_error(){
        val lock = CountDownLatch(1)
        val auth = Auth(
            "Chargebee.sdkKey",
            Chargebee.applicationId,
            Chargebee.appName,
            Chargebee.channel
        )
        CBAuthentication.authenticate(auth) {
            when (it) {
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    assertThat((it.data), instanceOf(CBAuthResponse::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Error :"+it.exp.message)
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }

    @Test
    fun test_version1_productID_success(){
        val queryParam1 = arrayOf("Standard", "app_store")

        if (CatalogVersion.V1.value == Chargebee.version) {
            val lock = CountDownLatch(1)
            Plan.retrieveAllPlans(queryParam1) {
                when (it) {
                    is ChargebeeResult.Success -> {
                        lock.countDown()
                        System.out.println("List plans :"+it.data)
                        assertThat((it.data), instanceOf(PlansWrapper::class.java))
                    }
                    is ChargebeeResult.Error -> {
                        lock.countDown()
                        System.out.println("Error :"+it.exp.message)
                    }
                }
            }
            lock.await(3000, TimeUnit.SECONDS)
        }

    }

    @Test
    fun test_version2_productID_success(){
        val queryParam = arrayOf("Standard", "app_store")
        val lock = CountDownLatch(1)
        if (CatalogVersion.V2.value == Chargebee.version) {
            Items.retrieveAllItems(queryParam){
                when (it) {
                    is ChargebeeResult.Success -> {
                        lock.countDown()
                        System.out.println("List items :"+it.data)
                        assertThat(it.data,instanceOf(ItemsWrapper::class.java))
                    }
                    is ChargebeeResult.Error -> {
                        lock.countDown()
                        System.out.println("Error retrieving all items :"+it.exp.message)
                    }
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }

 /*   @Test
    fun test_unknown_productID_success(){
        val lock = CountDownLatch(1)
        if (CatalogVersion.Unknown.value == Chargebee.version) {
            val auth = Auth(
                Chargebee.sdkKey,
                Chargebee.applicationId,
                Chargebee.appName,
                Chargebee.channel
            )
            CBAuthentication.authenticate(auth) {
                when (it) {
                    is ChargebeeResult.Success -> {
                        lock.countDown()
                        System.out.println("response :"+it.data)
                        assertThat((it.data), instanceOf(CBAuthResponse::class.java))
                    }
                    is ChargebeeResult.Error -> {
                        lock.countDown()
                        System.out.println("Error :"+it.exp.message)
                    }
                }
            }
        }
        lock.await(5000, TimeUnit.SECONDS)
    }*/
    @Test
    fun test_getAllPlans_success(){
        val queryParam1 = arrayOf("Standard", "app_store")
        val lock = CountDownLatch(1)
        Plan.retrieveAllPlans(queryParam1) {
            when (it) {
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("List plans : ${it.data}")
                    assertThat((it.data), instanceOf(PlansWrapper::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Error :"+it.exp.message)
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)

    }
    @Test
    fun test_getAllPlans_error(){
        val queryParam1 = arrayOf("Standard", "app_store")
        val lock = CountDownLatch(1)
        Plan.retrieveAllPlans(queryParam1) {
            when (it) {
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("List plans :${it.data}")
                    assertThat((it.data), instanceOf(PlansWrapper::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Error :"+it.exp.message)
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }

    @Test
    fun test_getItems_success(){
        val queryParam = arrayOf("Standard", "app_store")
        val lock = CountDownLatch(1)
        Items.retrieveAllItems(queryParam) {
            when (it) {
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("List items :{it.data}")
                    assertThat(it.data,instanceOf(ItemsWrapper::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Error retrieving all items :"+it.exp.message)
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }
    @Test
    fun test_getItems_error(){
        val queryParam = arrayOf("Standard", "app_store")
        val lock = CountDownLatch(1)
        Items.retrieveAllItems(queryParam) {
            when (it) {
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("List items : ${it.data}")
                    assertThat(it.data,instanceOf(ItemsWrapper::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Error retrieving all items :"+it.exp.message)
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }

   /* @Test
    fun test_getProductsFromPlayStore_success(){
        val productIdList = arrayListOf("merchant.pro.android", "merchant.premium.android")
        mContext?.let {
            CBPurchase.retrieveProducts(
                it,
                productIdList,
                object : CBCallback.ListProductsCallback<ArrayList<Products>> {
                    override fun onSuccess(productDetails: ArrayList<Products>) {
                        Log.i(javaClass.simpleName, "list products :  $productDetails")
                    }

                    override fun onError(error: CBException) {
                        Log.e(
                            javaClass.simpleName,
                            "Error in retrieving all items :  ${error.message}"
                        )
                    }
                })
        }
    }
    @Test
    fun test_getProductsFromPlayStore_error(){
        val productIdList = arrayListOf("merchant.pro.android.test", "merchant.premium.android.test")
        mContext?.let {
            CBPurchase.retrieveProducts(
                it,
                productIdList,
                object : CBCallback.ListProductsCallback<ArrayList<Products>> {
                    override fun onSuccess(productDetails: ArrayList<Products>) {
                        Log.i(javaClass.simpleName, "list products :  $productDetails")
                    }

                    override fun onError(error: CBException) {
                        Log.e(
                            javaClass.simpleName,
                            "Error in retrieving products from play store :  ${error.message}"
                        )
                    }
                })
        }
    }*/
    @Test
    fun test_getSubscriptionStatus_success(){
        val subscriptionId = "1000000894110088"
       val lock = CountDownLatch(1)
        retrieveSubscription(subscriptionId) {
            when(it){
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("subscription status: ${it.data}")
                    assertThat(it.data,instanceOf(SubscriptionDetailsWrapper::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Exception from server- retrieveSubscription() :  ${it.exp.message}")
                }
            }
        }
       lock.await(3000, TimeUnit.SECONDS)
    }
    @Test
    fun test_getSubscriptionStatus_error(){
        val subscriptionId = "10000890"
        val lock = CountDownLatch(1)
        retrieveSubscription(subscriptionId) {
            when(it){
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("subscription status: ${it.data}")
                    assertThat(it.data,instanceOf(SubscriptionDetailsWrapper::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Exception from server- retrieveSubscription() :  ${it.exp.message}")
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }
    @Test
    fun test_retrievePlan_Success() {
        val planId = "demo"
        val lock = CountDownLatch(1)
        Plan.retrievePlan(planId) {
            when(it){
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("Plan details: ${it.data}")
                    assertThat(it.data,instanceOf(PlanWrapper::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Exception from server- test_retrievePlan_Success(): ${it.exp.message}")
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }
    @Test
    fun test_retrievePlan_Error() {
        val planId = "demo"
        val lock = CountDownLatch(1)
        Plan.retrievePlan(planId) {
            when(it){
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("Plan details: ${it.data}")
                    assertThat(it.data,instanceOf(PlanWrapper::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Exception from server- test_retrievePlan_error(): ${it.exp.message}")
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }
    @Test
    fun test_retrieveItem_Success() {
        val itemId = "demo"
        val lock = CountDownLatch(1)
        Items.retrieveItem(itemId) {
            when(it){
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("Plan details: ${it.data}")
                    assertThat(it.data,instanceOf(PlanWrapper::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Exception from server- test_retrieveItem_Success(): ${it.exp.message}")
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }
    @Test
    fun test_retrieveItem_Error() {
        val itemId = "demo"
        val lock = CountDownLatch(1)
        Items.retrieveItem(itemId) {
            when(it){
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("Plan details: ${it.data}")
                    assertThat(it.data,instanceOf(PlanWrapper::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Exception from server- test_retrieveItem_Success(): ${it.exp.message}")
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }
    @Test
    fun test_validateReceipt_Success(){
        val lock = CountDownLatch(1)
        val plan = Items("", "", "", "")
        val purchaseToken = "89hndmdbsfksdjfsfsvfmfsdf"
        val jsonObject = JsonObject()
        val data = "{\"productId\":\"merchant.pro.android\",\"purchaseTime\":1637579709637,\"purchaseToken\":\"lggjdfegkeggmihablmgddkl.AO-J1OzRbdGGlLJiync-PQTLD2iu8e2Ovh9Oqd_xCPnWgf_n8RMCutX6_WCkV0sQXcsuoWD091J42wRpz5ACofrv6-wDXyX6ymqmCZifNneXzF36OxUwrbc\",\"quantity\":1," +
                "type : SKUTYPE}"
        val skuDetails = SkuDetails(data)
        val products = Products("1234", "", "", skuDetails, true)
        CBPurchase.validateReceipt(purchaseToken, products){
            when(it){
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    val subscriptionId =
                        (it.data as CBReceiptResponse).in_app_subscription.subscription_id
                    retrieveSubscription(
                        subscriptionId,
                        (ChargebeeResult.Companion.Success) as (ChargebeeResult<Any>) -> Unit
                    )
                    assertThat(it.data,instanceOf(CBReceiptResponse::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Exception from server- test_validateReceipt_Success(): ${it.exp.message}")
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }
    @Test
    fun test_validateReceipt_Error(){
        val lock = CountDownLatch(1)
        val purchaseToken = "89hndmdbsfksdjfsfsvfmfsdf"
        val data = "{\"productId\":\"merchant.pro.android\",\"purchaseTime\":1637579709637,\"purchaseToken\":\"lggjdfegkeggmihablmgddkl.AO-J1OzRbdGGlLJiync-PQTLD2iu8e2Ovh9Oqd_xCPnWgf_n8RMCutX6_WCkV0sQXcsuoWD091J42wRpz5ACofrv6-wDXyX6ymqmCZifNneXzF36OxUwrbc\",\"quantity\":1," +
                "type : SKUTYPE}"
        val skuDetails = SkuDetails(data)
        val products = Products("1234", "", "", skuDetails, true)
        CBPurchase.validateReceipt(purchaseToken, products){
            when(it){
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    val subscriptionId =
                        (it.data as CBReceiptResponse).in_app_subscription.subscription_id
                    retrieveSubscription(
                        subscriptionId,
                        (ChargebeeResult.Companion.Success) as (ChargebeeResult<Any>) -> Unit
                    )
                    assertThat(it.data,instanceOf(CBReceiptResponse::class.java))
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Exception from server- test_validateReceipt_Success(): ${it.exp.message}")
                }
            }
        }
        lock.await(3000, TimeUnit.SECONDS)
    }

    @Test
    fun test_authBody(){
        val auth = Auth(
            Chargebee.sdkKey,
            Chargebee.applicationId,
            Chargebee.appName,
            Chargebee.channel
        )
        fromCBAuthBody(auth)
    }

}