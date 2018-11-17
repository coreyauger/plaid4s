package io.surfkit.plaid.flows

import java.io.{File, PrintWriter}
import java.net.URL

import akka.actor.{ActorSystem, Cancellable}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json.{Json, Reads, Writes}
import io.surfkit.plaid.data._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

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
  /*

  @POST("/item/create")
  Call<ItemCreateResponse> itemCreate(@Body ItemCreateRequest request);

  @POST("/item/mfa")
  Call<ItemMfaResponse> itemMfa(@Body ItemMfaRequest request);

  @POST("/item/get")
  Call<ItemGetResponse> itemGet(@Body ItemGetRequest request);

  @POST("/item/credentials/update")
  Call<ItemCredentialsUpdateResponse> itemCredentialsUpdate(@Body ItemCredentialsUpdateRequest request);

  @POST("/item/credentials/encrypt")
  Call<ItemCredentialsEncryptResponse> itemCredentialsEncrypt(@Body ItemCredentialsEncryptRequest request);

  @POST("/item/mfa/encrypt")
  Call<ItemMfaEncryptResponse> itemMfaEncrypt(@Body ItemMfaEncryptRequest request);

  @POST("/item/public_token/exchange")
  Call<ItemPublicTokenExchangeResponse> itemPublicTokenExchange(@Body ItemPublicTokenExchangeRequest request);

  @POST("/item/public_token/create")
  Call<ItemPublicTokenCreateResponse> itemPublicTokenCreate(@Body ItemPublicTokenCreateRequest request);

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

  @POST("/item/webhook/update")
  Call<ItemWebhookUpdateResponse> itemWebhookUpdate(@Body ItemWebhookUpdateRequest request);

  // sandbox-only endpoints
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  @POST("/sandbox/item/reset_login")
  Call<SandboxItemResetLoginResponse> sandboxItemResetLogin(@Body SandboxItemResetLoginRequest request);

  @POST("/sandbox/public_token/create")
  Call<SandboxPublicTokenCreateResponse> sandboxPublicTokenCreate(@Body SandboxPublicTokenCreateRequest request);

  // products
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  @POST("/accounts/get")
  Call<AccountsGetResponse> accountsGet(@Body AccountsGetRequest request);

  @POST("/accounts/balance/get")
  Call<AccountsBalanceGetResponse> accountsBalanceGet(@Body AccountsBalanceGetRequest request);

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

  @POST("/auth/get")
  Call<AuthGetResponse> authGet(@Body AuthGetRequest request);

  @POST("/identity/get")
  Call<IdentityGetResponse> identityGet(@Body IdentityGetRequest request);

  @POST("/income/get")
  Call<IncomeGetResponse> incomeGet(@Body IncomeGetRequest request);

  @POST("/transactions/get")
  Call<TransactionsGetResponse> transactionsGet(@Body TransactionsGetRequest request);

  @POST("/credit_details/get")
  Call<CreditDetailsGetResponse> creditDetailsGet(@Body CreditDetailsGetRequest request);

  @POST("/categories/get")
  Call<CategoriesGetResponse> categoriesGet(@Body CategoriesGetRequest request);
   */





/*
  object httpApi extends PlaidSignedRequester(getCreds _)

  def unmarshal[T <: Plaid](response: HttpResponse)(implicit um: Reads[T]):Future[T] = Unmarshal(response.entity).to[T]

  def publicTokenExchange(account: String, replace: Questrade.ReplaceOrder)(implicit um: Reads[Questrade.OrderResponse],uw: Writes[Questrade.ReplaceOrder]) =
    httpApi.post[Questrade.ReplaceOrder](s"/item/public_token/exchange", replace).flatMap(x => unmarshal(x))

  def login()(implicit um: Reads[Questrade.Login]) = {
    println(s"curl -XGET '${loginUrl}'")
    Http().singleRequest(HttpRequest(uri = loginUrl)).flatMap(x => unmarshal(x)).map(storeLogin)
  }

  def accounts()(implicit um: Reads[Questrade.Accounts]) =
    httpApi.get("accounts").flatMap(x => unmarshal(x) )

  def quote(ids: Set[Int])(implicit um: Reads[Questrade.Quotes]) =
    httpApi.get(s"markets/quotes?ids=${ids.mkString("",",","")}").flatMap(x => unmarshal(x))

  def order(account: String, post: Questrade.PostOrder)(implicit um: Reads[Questrade.OrderResponse],uw: Writes[Questrade.PostOrder]) =
    httpApi.post[Questrade.PostOrder](s"accounts/${account}/orders", post).flatMap(x => unmarshal(x))

  def bracket(account: String, post: Questrade.PostBracket)(implicit um: Reads[Questrade.OrderResponse],uw1: Writes[Questrade.BracketOrder]) =
    httpApi.post[Questrade.PostBracket](s"accounts/${account}/orders/bracket", post).flatMap(x => unmarshal(x))

  def cancel(account: String, order: String)(implicit um: Reads[Questrade.OrderCancelConfirm]) =
    httpApi.delete(s"accounts/${account}/orders/${order}").flatMap(x => unmarshal(x))

  private[this] def notificationStreamPort()(implicit um: Reads[Questrade.StreamPort]) =
    httpApi.get("notifications?mode=WebSocket").flatMap(x => unmarshal(x) )

  private[this] def l1StreamPort(ids: Set[Int])(implicit um: Reads[Questrade.StreamPort]) =
    httpApi.get(s"markets/quotes?ids=${ids.mkString("",",","")}&stream=true&mode=WebSocket").flatMap(x => unmarshal(x) )


  def sendSlack(webhookUrl: String, attachments: Questrade.SlackAttachments) = post(webhookUrl, attachments)
*/
}
