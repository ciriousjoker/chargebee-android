package com.chargebee.android.billingservice

import android.content.Context
import android.os.Build
import com.android.billingclient.api.*
import com.chargebee.android.Chargebee
import com.chargebee.android.ErrorDetail
import com.chargebee.android.billingservice.CBCallback.ListProductsCallback
import com.chargebee.android.exceptions.CBException
import com.chargebee.android.exceptions.CBProductIDResult
import com.chargebee.android.exceptions.ChargebeeResult
import com.chargebee.android.models.Products
import com.chargebee.android.network.CBReceiptRequestBody
import com.chargebee.android.network.CBReceiptResponse
import com.chargebee.android.network.Params
import com.chargebee.android.network.ReceiptDetail
import com.chargebee.android.resources.ReceiptResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class BillingClientManagerTest  {

    var billingClient: BillingClientManager? = null
    var mContext: Context? = null
    private var callBack : ListProductsCallback<ArrayList<Products>>? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mContext = RuntimeEnvironment.application.applicationContext

        Chargebee.configure(
            site = "cb-imay-test",
            publishableApiKey = "test_EojsGoGFeHoc3VpGPQDOZGAxYy3d0FF3",
            sdkKey = "cb-j53yhbfmtfhfhkmhow3ramecom"
        )
    }
    @After
    fun tearDown(){
        mContext = null
    }

    @Test
    fun test_retrieveProducts_success(){
        val productIdList = arrayListOf("merchant.pro.android", "merchant.premium.android")

        val lock = CountDownLatch(1)
        mContext?.let {
            CBPurchase.retrieveProducts(
                it,
                productIdList,
                object : ListProductsCallback<ArrayList<Products>> {
                    override fun onSuccess(productDetails: ArrayList<Products>) {
                        lock.countDown()
                        println("List products :$productDetails")
                        assertThat(
                            productDetails,
                            instanceOf(Products::class.java)
                        )
                    }

                    override fun onError(error: CBException) {
                        lock.countDown()
                        println("Error in retrieving all items :${error.message}")
                    }
                })
        }

        lock.await()
    }

    @Test
    fun test_loadProducts_success(){
        val productIdList = arrayListOf("merchant.pro.android", "merchant.premium.android")

        billingClient = callBack?.let {
            BillingClientManager(
                RuntimeEnvironment.application.applicationContext,
                BillingClient.SkuType.SUBS,
                productIdList, it
            )
        }

        billingClient?.startBillingServiceConnection()
        billingClient?.queryAllPurchases()

        //Mockito.verify(billingClient, times(1))?.queryAllPurchases()
    }

    @Test
    fun test_retrieveProducts_error(){
        val productIdList = arrayListOf("merchant.pro.android.test", "merchant.premium.android.test")
        val lock = CountDownLatch(1)
        mContext?.let {
            CBPurchase.retrieveProducts(
                it,
                productIdList,
                object : ListProductsCallback<ArrayList<Products>> {
                    override fun onSuccess(productDetails: ArrayList<Products>) {
                        lock.countDown()
                        println("List products :$productDetails")
                        assertThat(
                            productDetails,
                            instanceOf(Products::class.java)
                        )
                    }

                    override fun onError(error: CBException) {
                        lock.countDown()
                        println("Error in retrieving products from play store")
                    }
                })
        }
        lock.await()
    }

    @Test
    fun test_retrieveProductIds_success(){
        val queryParam = arrayOf("100")
        val lock = CountDownLatch(1)
        CBPurchase.retrieveProductIDs(queryParam) {
            when (it) {
                is CBProductIDResult.ProductIds -> {
                    lock.countDown()
                    assertThat(it,instanceOf(CBProductIDResult::class.java))
                }
                is CBProductIDResult.Error -> {
                    lock.countDown()
                    println(" Error ${it.exp.message}")
                }
            }
        }
        lock.await()
    }

    @Test
    fun test_retrieveProductIds_error(){
        val queryParam = arrayOf("100")
        val lock = CountDownLatch(1)
        CBPurchase.retrieveProductIDs(queryParam) {
            when (it) {
                is CBProductIDResult.ProductIds -> {
                    lock.countDown()
                    assertThat(it,instanceOf(CBProductIDResult::class.java))
                }
                is CBProductIDResult.Error -> {
                    lock.countDown()
                    println(" Error ${it.exp.message}")
                }
            }
        }
        lock.await()
    }
    @Test
    fun test_purchaseProduct_success(){
        val jsonDetails = "{\"productId\":\"merchant.premium.android\",\"type\":\"subs\",\"title\":\"Premium Plan (Chargebee Example)\",\"name\":\"Premium Plan\",\"price\":\"₹2,650.00\",\"price_amount_micros\":2650000000,\"price_currency_code\":\"INR\",\"description\":\"Every 6 Months\",\"subscriptionPeriod\":\"P6M\",\"skuDetailsToken\":\"AEuhp4J0KiD1Bsj3Yq2mHPBRNHUBdzs4nTJY3PWRR8neE-22MJNssuDzH2VLFKv35Ov8\"}"
        val skuDetails = SkuDetails(jsonDetails)
        val products = Products("","","",skuDetails,true)
        val lock = CountDownLatch(1)
        CBPurchase.purchaseProduct(products, object : CBCallback.PurchaseCallback<PurchaseModel>{
            override fun onSuccess(success: PurchaseModel) {
                lock.countDown()
                assertThat(success,instanceOf(PurchaseModel::class.java))
            }
            override fun onError(error: CBException) {
                lock.countDown()
                println(" Error :  ${error.message}")
            }
        })
        lock.await()
    }
    @Test
    fun test_purchaseProduct_error(){
        val jsonDetails = "{\"productId\":\"merchant.premium.android\",\"type\":\"subs\",\"title\":\"Premium Plan (Chargebee Example)\",\"name\":\"Premium Plan\",\"price\":\"₹2,650.00\",\"price_amount_micros\":2650000000,\"price_currency_code\":\"INR\",\"description\":\"Every 6 Months\",\"subscriptionPeriod\":\"P6M\",\"skuDetailsToken\":\"AEuhp4J0KiD1Bsj3Yq2mHPBRNHUBdzs4nTJY3PWRR8neE-22MJNssuDzH2VLFKv35Ov8\"}"
        val skuDetails = SkuDetails(jsonDetails)
        val products = Products("","","",skuDetails,true)
        val lock = CountDownLatch(1)
        CBPurchase.purchaseProduct(products, object : CBCallback.PurchaseCallback<PurchaseModel>{
            override fun onSuccess(success: PurchaseModel) {
                lock.countDown()
                assertThat(success,instanceOf(PurchaseModel::class.java))
            }
            override fun onError(error: CBException) {
                lock.countDown()
                println(" Error :  ${error.message}")
            }
        })
        lock.await()
    }
    @Test
    fun test_validateReceipt_success(){
        val purchaseToken = "56sadmnagdjsd"
        val jsonDetails = "{\"productId\":\"merchant.premium.android\",\"type\":\"subs\",\"title\":\"Premium Plan (Chargebee Example)\",\"name\":\"Premium Plan\",\"price\":\"₹2,650.00\",\"price_amount_micros\":2650000000,\"price_currency_code\":\"INR\",\"description\":\"Every 6 Months\",\"subscriptionPeriod\":\"P6M\",\"skuDetailsToken\":\"AEuhp4J0KiD1Bsj3Yq2mHPBRNHUBdzs4nTJY3PWRR8neE-22MJNssuDzH2VLFKv35Ov8\"}"
        val skuDetails = SkuDetails(jsonDetails)
        val products = Products("merchant.premium.android","Premium Plan (Chargebee Example)","₹2,650.00",skuDetails,true)
        val lock = CountDownLatch(1)
        CBPurchase.validateReceipt(purchaseToken, products){
            when(it){
                is ChargebeeResult.Success ->{
                    lock.countDown()
                    assertThat(it,instanceOf(CBReceiptResponse::class.java))
                }
                is ChargebeeResult.Error ->{
                    lock.countDown()
                    println(" Error :  ${it.exp.message}")
                }
            }
        }
        lock.await()

        val params = Params(
            purchaseToken,
            products.productId,
            CBPurchase.price,
            products.skuDetails.priceCurrencyCode,
            Chargebee.site,
            Chargebee.channel
        )
        val receiptDetail = ReceiptDetail("subscriptionId","customerId","planId")
        val response = CBReceiptResponse(receiptDetail)

        CoroutineScope(Dispatchers.IO).launch {
            Mockito.`when`(ReceiptResource().validateReceipt(params)).thenReturn(
                ChargebeeResult.Success(
                    response
                )
            )
            Mockito.verify(ReceiptResource(), times(1)).validateReceipt(params)
            Mockito.verify(CBReceiptRequestBody("receipt","","","","",""), times(1)).toCBReceiptReqBody()
        }
    }
    @Test
    fun test_validateReceipt_error(){
        val purchaseToken = "56sadmnagdjsd"
        val jsonDetails = "{\"productId\":\"merchant.premium.test.android\",\"type\":\"subs\",\"title\":\"Premium Plan (Chargebee Example)\",\"name\":\"Premium Plan\",\"price\":\"₹2,650.00\",\"price_amount_micros\":2650000000,\"price_currency_code\":\"INR\",\"description\":\"Every 6 Months\",\"subscriptionPeriod\":\"P6M\",\"skuDetailsToken\":\"AEuhp4J0KiD1Bsj3Yq2mHPBRNHUBdzs4nTJY3PWRR8neE-22MJNssuDzH2VLFKv35Ov8\"}"
        val skuDetails = SkuDetails(jsonDetails)
        val products = Products("merchant.premium.test.android","Premium Plan (Chargebee Example)","₹2,650.00",skuDetails,true)
        val lock = CountDownLatch(1)
        CBPurchase.validateReceipt(purchaseToken, products){
            when(it){
                is ChargebeeResult.Success ->{
                    lock.countDown()
                    assertThat(it,instanceOf(CBReceiptResponse::class.java))
                }
                is ChargebeeResult.Error ->{
                    lock.countDown()
                    println(" Error :  ${it.exp.message}")
                }
            }
        }
        lock.await()

        val params = Params(
            purchaseToken,
            products.productId,
            CBPurchase.price,
            products.skuDetails.priceCurrencyCode,
            Chargebee.site,
            Chargebee.channel
        )
        val exception = CBException(ErrorDetail("Error"))
        CoroutineScope(Dispatchers.IO).launch {
            Mockito.`when`(ReceiptResource().validateReceipt(params)).thenReturn(
                ChargebeeResult.Error(
                    exception
                )
            )
            Mockito.verify(ReceiptResource(), times(1)).validateReceipt(params)
            Mockito.verify(CBReceiptRequestBody("receipt","","","","",""), times(1)).toCBReceiptReqBody()
        }
    }
}