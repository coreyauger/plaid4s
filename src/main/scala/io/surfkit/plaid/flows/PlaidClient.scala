package io.surfkit.plaid.flows

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.joda.time.DateTime
import play.api.libs.json.{Json, Reads, Writes}
import io.surfkit.plaid.data._

import scala.concurrent.{ExecutionContext, Future}

class PlaidClient(clientId: String, clientSecret: String, publicKey: String, endpoint: String = "https://sandbox.plaid.com/")(implicit system: ActorSystem, materializer: Materializer, ex: ExecutionContext) extends PlayJsonSupport {

  val config = ConfigFactory.load()

  def post[T <: Plaid : Writes ](path: String, post: T)(implicit uw: Writes[T]) = {
    val url = endpoint + path
    val json = Json.stringify(uw.writes(post))
    val jsonEntity = HttpEntity(ContentTypes.`application/json`, json)
    println(s"curl -XPOST '${url}' -H 'Content-Type: application/json' -d '${json}'")
    Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"${url}", entity = jsonEntity))
  }

  def unmarshal[T <: Plaid : Reads](response: HttpResponse):Future[T] = Unmarshal(response.entity).to[T]

  // institutions
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  def institutionsGet(count: Int = 200, offset: Int = 0): Future[InstitutionsResponse]  =
    post("institutions/get", InstitutionsGetRequest(clientId, clientSecret, count, offset)).flatMap(x => unmarshal[InstitutionsResponse](x))

  def institutionsGet(institutionId: String): Future[InstitutionResponse]  =
    post("institutions/get_by_id", InstitutionsGetByIdRequest(publicKey, institutionId)).flatMap(x => unmarshal[InstitutionResponse](x))

  def institutionsSearch(query: String, products: Seq[ProductType]): Future[InstitutionsSearchResponse]  =
    post("institutions/search", InstitutionsSearchRequest(publicKey, query, products)).flatMap(x => unmarshal[InstitutionsSearchResponse](x))


  // item, link, and credentials calls
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////

  def itemGet(access_token: String): Future[ItemGetResponse]  =
    post("item/get", ItemGetRequest(access_token)).flatMap(x => unmarshal[ItemGetResponse](x))

  def itemCreate(institution_id: String,
                 initial_products: Seq[ProductType],
                 credentials: Map[String, String] = Map.empty,
                 webhook: Option[String] = None,
                 credentials_token: Option[String] = None,
                 start_date: Option[DateTime] = None,
                 end_date: Option[DateTime] = None): Future[ItemCreateResponse]  =
    post("item/create", ItemCreateRequest(clientId, clientSecret, institution_id, initial_products, credentials, webhook, credentials_token, start_date, end_date)).flatMap(x => unmarshal[ItemCreateResponse](x))


  def itemGet(access_token: String, webhook: String): Future[ItemWebhookUpdateResponse]  =
    post("item/webhook/update", ItemWebhookUpdateRequest(access_token, webhook)).flatMap(x => unmarshal[ItemWebhookUpdateResponse](x))

  def itemPublicTokenCreate(access_token: String): Future[ItemPublicTokenCreateResponse]  =
    post("tem/public_token/create", ItemPublicTokenCreateRequest(access_token)).flatMap(x => unmarshal[ItemPublicTokenCreateResponse](x))

  def itemPublicTokenExchange: Future[ItemPublicTokenExchangeResponse]  =
    post("item/public_token/exchange", ItemPublicTokenExchangeRequest(publicKey)).flatMap(x => unmarshal[ItemPublicTokenExchangeResponse](x))
  /*

  @POST("/item/mfa")
  Call<ItemMfaResponse> itemMfa(@Body ItemMfaRequest request);

  @POST("/item/credentials/update")
  Call<ItemCredentialsUpdateResponse> itemCredentialsUpdate(@Body ItemCredentialsUpdateRequest request);

  @POST("/item/credentials/encrypt")
  Call<ItemCredentialsEncryptResponse> itemCredentialsEncrypt(@Body ItemCredentialsEncryptRequest request);

  @POST("/item/mfa/encrypt")
  Call<ItemMfaEncryptResponse> itemMfaEncrypt(@Body ItemMfaEncryptRequest request);

  @POST("/processor/stripe/bank_account_token/create")
  Call<ItemStripeTokenCreateResponse> itemStripeTokenCreate(@Body ItemStripeTokenCreateRequest request);

  @POST("/processor/apex/processor_token/create")
  Call<ItemApexProcessorTokenCreateResponse> itemApexProcessorTokenCreate(@Body ItemApexProcessorTokenCreateRequest request);

  @POST("/processor/dwolla/processor_token/create")
  Call<ItemDwollaProcessorTokenCreateResponse> itemDwollaProcessorTokenCreate(@Body ItemDwollaProcessorTokenCreateRequest request);

  @POST("/item/access_token/invalidate")
  Call<ItemAccessTokenInvalidateResponse> itemAccessTokenInvalidate(@Body ItemAccessTokenInvalidateRequest request);

  @POST("/item/access_token/update_version")
  Call<ItemAccessTokenUpdateVersionResponse> itemAccessTokenUpdateVersion(@Body ItemAccessTokenUpdateVersionRequest request);

  @POST("/item/delete")
  Call<ItemDeleteResponse> itemDelete(@Body ItemDeleteRequest request);

  @POST("/item/remove")
  Call<ItemRemoveResponse> itemRemove(@Body ItemRemoveRequest request);
*/


  // sandbox-only endpoints
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////\

  /*
  @POST("/sandbox/item/reset_login")
  Call<SandboxItemResetLoginResponse> sandboxItemResetLogin(@Body SandboxItemResetLoginRequest request);

  @POST("/sandbox/public_token/create")
  Call<SandboxPublicTokenCreateResponse> sandboxPublicTokenCreate(@Body SandboxPublicTokenCreateRequest request);
  */

  // products
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////

  def authGet(access_token: String, account_ids: Option[Seq[String]] = None): Future[AuthGetResponse]  =
    post("auth/get", AuthGetRequest(access_token, account_ids)).flatMap(x => unmarshal[AuthGetResponse](x))

  def accountsGet(access_token: String, account_ids: Option[Seq[String]] = None): Future[AccountsGetResponse]  =
    post("accounts/get", AccountsGetRequest(access_token, account_ids)).flatMap(x => unmarshal[AccountsGetResponse](x))

  def accountsBalanceGet(access_token: String, account_ids: Option[Seq[String]] = None): Future[AccountsBalanceGetResponse]  =
    post("accounts/balance/get", AccountsBalanceGetRequest(access_token, account_ids)).flatMap(x => unmarshal[AccountsBalanceGetResponse](x))

  def identityGet(access_token: String): Future[IdentityGetResponse]  =
    post("identity/get", IdentityGetRequest(access_token)).flatMap(x => unmarshal[IdentityGetResponse](x))

  def incomeGet(access_token: String): Future[IncomeGetResponse]  =
    post("income/get", IncomeGetRequest(access_token)).flatMap(x => unmarshal[IncomeGetResponse](x))

  def transactionsGet(access_token: String, start_date: DateTime, end_date: DateTime, account_ids: Option[Seq[String]] = None, count: Option[Int] = None, offset: Option[Int] = None): Future[TransactionsGetResponse]  =
    post("transactions/get", TransactionsGetRequest(access_token: String, start_date, end_date, account_ids, count, offset)).flatMap(x => unmarshal[TransactionsGetResponse](x))

  def creditDetailsGet(access_token: String): Future[CreditDetailsGetResponse]  =
    post("credit_details/get", CreditDetailsGetRequest(access_token)).flatMap(x => unmarshal[CreditDetailsGetResponse](x))
  /*


  ----

  @POST("/asset_report/create")
  Call<AssetReportCreateResponse> assetReportCreate(@Body AssetReportCreateRequest request);

  @POST("/asset_report/get")
  Call<AssetReportGetResponse> assetReportGet(@Body AssetReportGetRequest request);

  // This returns raw bytes so we don't have a wrapper class
  @POST("/asset_report/pdf/get")
  Call<ResponseBody> assetReportPdfGet(@Body AssetReportPdfGetRequest request);

  @POST("/asset_report/remove")
  Call<AssetReportRemoveResponse> assetReportRemove(@Body AssetReportRemoveRequest request);

  @POST("/asset_report/audit_copy/create")
  Call<AssetReportAuditCopyCreateResponse> assetReportAuditCopyCreate(@Body AssetReportAuditCopyCreateRequest request);

  @POST("/asset_report/audit_copy/remove")
  Call<AssetReportAuditCopyRemoveResponse> assetReportAuditCopyRemove(@Body AssetReportAuditCopyRemoveRequest request);

  @POST("/asset_report/audit_copy/get")
  Call<AssetReportGetResponse> assetReportAuditCopyGet(@Body AssetReportAuditCopyGetRequest request);

  @POST("/asset_report/refresh")
  Call<AssetReportCreateResponse> assetReportRefresh(@Body AssetReportRefreshRequest assetReportRefreshRequest);

  @POST("/asset_report/filter")
  Call<AssetReportCreateResponse> assetReportFilter(@Body AssetReportFilterRequest assetReportFilterRequest);


  @POST("/categories/get")
  Call<CategoriesGetResponse> categoriesGet(@Body CategoriesGetRequest request);
   */

}
