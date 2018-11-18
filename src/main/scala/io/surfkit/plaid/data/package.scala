package io.surfkit.plaid

import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json._

package object data {

  sealed trait Plaid{}

  import play.api.libs.json.JodaWrites
  // TODO: correct date format?
  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
  import play.api.libs.json.JodaReads
  implicit val dateTimeJsReader = JodaReads.jodaDateReads("yyyyMMddHHmmss")

/*
  case class Item(
                   available_products: Seq[String],
                   billed_products: Seq[String],
                   error: Option[String] = None,
                   institution_id: String,
                   item_id: String,
                   webhook: String) extends Plaid
  implicit val ItemWrites = Json.writes[Item]
  implicit val ItemReads = Json.reads[Item]
*/




  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  trait PublicRequest{
    def public_key: String
  }

  trait ClientRequest{
    def client_id: String
    def secret: String
  }

  trait AccessTokenRequest{
    def access_token: String
  }

  //INVALID_REQUEST, INVALID_INPUT, RATE_LIMIT_EXCEEDED, API_ERROR, ITEM_ERROR, INSTITUTION_ERROR,
  //    ASSET_REPORT_ERROR
  trait ErrorType
  object ErrorType{
    case object INVALID_REQUEST extends ErrorType
    case object INVALID_INPUT extends ErrorType
    case object RATE_LIMIT_EXCEEDED extends ErrorType
    case object API_ERROR extends ErrorType
    case object ITEM_ERROR extends ErrorType
    case object INSTITUTION_ERROR extends ErrorType
    case object ASSET_REPORT_ERROR extends ErrorType
    case object Unknown extends ErrorType
  }
  implicit val ErrorTypeWrites = new Writes[ErrorType] {
    def writes(p : ErrorType): JsValue = JsString(p match{
      case ErrorType.INVALID_REQUEST => "INVALID_REQUEST"
      case ErrorType.INVALID_INPUT => "INVALID_INPUT"
      case ErrorType.RATE_LIMIT_EXCEEDED => "RATE_LIMIT_EXCEEDED"
      case ErrorType.API_ERROR => "API_ERROR"
      case ErrorType.INSTITUTION_ERROR => "INSTITUTION_ERROR"
      case ErrorType.ASSET_REPORT_ERROR => "ASSET_REPORT_ERROR"
    } )
  }
  implicit val ErrorTypeReads: Reads[ErrorType] = (
    (JsPath).read[String].map{
      case "INVALID_REQUEST" => ErrorType.INVALID_REQUEST
      case "auth" => ErrorType.INVALID_INPUT
      case "balance" => ErrorType.RATE_LIMIT_EXCEEDED
      case "transactions" => ErrorType.API_ERROR
      case "credit_details" => ErrorType.INSTITUTION_ERROR
      case "income" => ErrorType.ASSET_REPORT_ERROR
    })
  case class ErrorResponse(
                            display_message: String,
                            error_code: String,
                            error_message: String,
                            error_type: ErrorType
                          ) extends Plaid
  implicit val ErrorResponseWrites = Json.writes[ErrorResponse]
  implicit val ErrorResponseReads = Json.reads[ErrorResponse]

  case class Credential(label: String, name: String, `type`: String) extends Plaid
  implicit val CredentialWrites = Json.writes[Credential]
  implicit val CredentialReads = Json.reads[Credential]

  trait ProductType
  object ProductType{
    case object Assets extends ProductType
    case object Auth extends ProductType
    case object Balance extends ProductType
    case object Transactions extends ProductType
    case object Credit_Details extends ProductType
    case object Income extends ProductType
    case object Identity extends ProductType
    case object Unknown extends ProductType
  }
  implicit val ProductTypeWrites = new Writes[ProductType] {
    def writes(p : ProductType): JsValue = JsString(p match{
      case ProductType.Assets => "assets"
      case ProductType.Auth => "auth"
      case ProductType.Balance => "balance"
      case ProductType.Transactions => "transactions"
      case ProductType.Credit_Details => "credit_details"
      case ProductType.Income => "income"
      case ProductType.Identity => "identity"
    } )
  }
  implicit val ProductTypeReads: Reads[ProductType] = (
    (JsPath).read[String].map{
      case "assets" => ProductType.Assets
      case "auth" => ProductType.Auth
      case "balance" => ProductType.Balance
      case "transactions" => ProductType.Transactions
      case "credit_details" => ProductType.Credit_Details
      case "income" => ProductType.Income
      case "identity"=> ProductType.Identity
      case _ => ProductType.Unknown
    })

  case class Institution(
                        credentials: Seq[Credential],
                        has_mfa: Boolean,
                        institution_id: String,
                        mfa_code_type: String,
                        name: String,
                        products: Seq[ProductType]
                        ) extends Plaid
  implicit val InstitutionWrites = Json.writes[Institution]
  implicit val InstitutionReads = Json.reads[Institution]

  case class InstitutionsGetRequest(client_id: String, secret: String, count: Int, offset: Int ) extends Plaid with ClientRequest
  implicit val InstitutionRequestWrites = Json.writes[InstitutionsGetRequest]
  implicit val InstitutionRequestReads = Json.reads[InstitutionsGetRequest]

  case class InstitutionsResponse(
                           institutions: Seq[Institution],
                           request_id: String,
                           total: Int
                         ) extends Plaid
  implicit val InstitutionsResponseWrites = Json.writes[InstitutionsResponse]
  implicit val InstitutionsResponseReads = Json.reads[InstitutionsResponse]

  case class InstitutionsGetByIdRequest(public_key: String, institution_id: String ) extends Plaid with PublicRequest
  implicit val InstitutionsGetByIdRequestWrites = Json.writes[InstitutionsGetByIdRequest]
  implicit val InstitutionsGetByIdRequestReads = Json.reads[InstitutionsGetByIdRequest]

  case class InstitutionResponse(
                                   institution: Institution,
                                   request_id: String
                                 ) extends Plaid
  implicit val InstitutionResponseWrites = Json.writes[InstitutionResponse]
  implicit val InstitutionResponseReads = Json.reads[InstitutionResponse]


  case class InstitutionsSearchRequest(public_key: String, query: String, products: Seq[ProductType] ) extends Plaid with PublicRequest
  implicit val InstitutionsSearchRequestWrites = Json.writes[InstitutionsSearchRequest]
  implicit val InstitutionsSearchRequestReads = Json.reads[InstitutionsSearchRequest]

  case class InstitutionsSearchResponse(
                                   institutions: Seq[Institution],
                                   request_id: String
                                ) extends Plaid
  implicit val InstitutionsSearchResponseWrites = Json.writes[InstitutionsSearchResponse]
  implicit val InstitutionsSearchResponseReads = Json.reads[InstitutionsSearchResponse]


  case class ItemGetRequest(access_token: String) extends Plaid with AccessTokenRequest
  implicit val ItemGetRequestWrites = Json.writes[ItemGetRequest]
  implicit val ItemGetRequestReads = Json.reads[ItemGetRequest]


  case class ItemStatus(
                         available_products: Seq[ProductType],
                         billed_products: Seq[ProductType],
                         error: ErrorResponse,
                         institution_id: String,
                         item_id: String,
                         webhook: String
                        ) extends Plaid
  implicit val ItemStatusWrites = Json.writes[ItemStatus]
  implicit val ItemStatusReads = Json.reads[ItemStatus]

  case class ItemGetResponse(
                             item: ItemStatus,
                             request_id: String
                           ) extends Plaid
  implicit val ItemGetResponseWrites = Json.writes[ItemGetResponse]
  implicit val ItemGetResponseReads = Json.reads[ItemGetResponse]


  case class ItemCreateRequest(
                                client_id: String,
                                secret: String,
                                institution_id: String,
                                initial_products: Seq[ProductType],
                                credentials: Map[String, String] = Map.empty,
                                webhook: Option[String] = None,
                                credentials_token: Option[String] = None,
                                start_date: Option[DateTime] = None,
                                end_date: Option[DateTime] = None
                              ) extends Plaid with ClientRequest
  implicit val ItemCreateRequestWrites = Json.writes[ItemCreateRequest]
  implicit val ItemCreateRequestReads = Json.reads[ItemCreateRequest]


  case class MfaDevice(
                        device_id: String,
                        `type`: String,
                        mask: String
                      ) extends Plaid
  implicit val MfaDeviceWrites = Json.writes[MfaDevice]
  implicit val MfaDeviceReads = Json.reads[MfaDevice]


  case class MfaSelection(
                           question: String,
                           answers: Seq[String]
                         ) extends Plaid
  implicit val MfaSelectionWrites = Json.writes[MfaSelection]
  implicit val MfaSelectionReads = Json.reads[MfaSelection]


  trait MfaType
  object MfaType{
    case object device extends MfaType
    case object device_list extends MfaType
    case object questions extends MfaType
    case object selections extends MfaType

  }
  implicit val MfaTypeWrites = new Writes[MfaType] {
    def writes(p : MfaType): JsValue = JsString(p match{
      case MfaType.device => "device"
      case MfaType.device_list => "device_list"
      case MfaType.questions => "questions"
      case MfaType.selections => "selections"

    } )
  }
  implicit val MfaTypeReads: Reads[MfaType] = (
    (JsPath).read[String].map{
      case "device" => MfaType.device
      case "device_list" => MfaType.device_list
      case "questions" => MfaType.questions
      case "selections" => MfaType.selections
    })


  case class ItemCreateResponse(
                                 access_token: String,
                                 item: ItemStatus,
                                 device: String,
                                 device_list: Seq[MfaDevice],
                                 mfa_type: MfaType,
                                 questions: Seq[String],
                                 selections: Seq[MfaSelection]
                            ) extends Plaid
  implicit val ItemCreateResponseWrites = Json.writes[ItemCreateResponse]
  implicit val ItemCreateResponseReads = Json.reads[ItemCreateResponse]


  case class ItemWebhookUpdateRequest(
                                 access_token: String,
                                 webhook: String
                              ) extends Plaid with AccessTokenRequest
  implicit val ItemWebhookUpdateRequestWrites = Json.writes[ItemWebhookUpdateRequest]
  implicit val ItemWebhookUpdateRequestReads = Json.reads[ItemWebhookUpdateRequest]


  case class ItemWebhookUpdateResponse(item: ItemStatus) extends Plaid
  implicit val ItemWebhookUpdateResponseWrites = Json.writes[ItemWebhookUpdateResponse]
  implicit val ItemWebhookUpdateResponseReads = Json.reads[ItemWebhookUpdateResponse]


  case class ItemPublicTokenCreateRequest(access_token: String) extends Plaid with AccessTokenRequest
  implicit val ItemPublicTokenCreateRequestWrites = Json.writes[ItemPublicTokenCreateRequest]
  implicit val ItemPublicTokenCreateRequestReads = Json.reads[ItemPublicTokenCreateRequest]

  case class ItemPublicTokenCreateResponse(expiration: DateTime, public_token: String) extends Plaid
  implicit val ItemPublicTokenCreateResponseWrites = Json.writes[ItemPublicTokenCreateResponse]
  implicit val ItemPublicTokenCreateResponseReads = Json.reads[ItemPublicTokenCreateResponse]

  case class ItemPublicTokenExchangeRequest(public_key: String) extends Plaid with PublicRequest
  implicit val ItemPublicTokenExchangeRequestWrites = Json.writes[ItemPublicTokenExchangeRequest]
  implicit val ItemPublicTokenExchangeRequestReads = Json.reads[ItemPublicTokenExchangeRequest]

  case class ItemPublicTokenExchangeResponse(access_token: String, item_id: String) extends Plaid
  implicit val ItemPublicTokenExchangeResponseWrites = Json.writes[ItemPublicTokenExchangeResponse]
  implicit val ItemPublicTokenExchangeResponseReads = Json.reads[ItemPublicTokenExchangeResponse]


  case class AccountsGetRequest(access_token: String, account_ids: Option[Seq[String]] = None) extends Plaid with AccessTokenRequest
  implicit val AccountsGetRequestWrites = Json.writes[AccountsGetRequest]
  implicit val AccountsGetRequestReads = Json.reads[AccountsGetRequest]

  case class Balances(
                       available: Double,
                       current: Double,
                       limit: Double,
                       iso_currency_code: String,
                       unofficial_currency_code: Option[String]) extends Plaid
  implicit val BalancesWrites = Json.writes[Balances]
  implicit val BalancesReads = Json.reads[Balances]

  case class Account(
              account_id: String,
              `type`: String,
              subtype: String,
              balances: Balances,
              name: String,
              mask: String,
              official_name: String) extends Plaid
  implicit val AccountWrites = Json.writes[Account]
  implicit val AccountReads = Json.reads[Account]

  case class AccountsGetResponse(item: ItemStatus, account: Account) extends Plaid
  implicit val AccountsGetResponseWrites = Json.writes[AccountsGetResponse]
  implicit val AccountsGetResponseReads = Json.reads[AccountsGetResponse]


  case class AuthGetRequest(access_token: String, account_ids: Option[Seq[String]] = None) extends Plaid with AccessTokenRequest
  implicit val AuthGetRequestWrites = Json.writes[AuthGetRequest]
  implicit val AuthGetRequestReads = Json.reads[AuthGetRequest]

  case class NumberACH(
                        account_id: String,
                        account: String,
                        routing: String,
                        wire_routing: String) extends Plaid
  implicit val NumberACHWrites = Json.writes[NumberACH]
  implicit val NumberACHReads = Json.reads[NumberACH]

  case class NumberEFT(
                        account_id: String,
                        account: String,
                        institution: String,
                        branch: String) extends Plaid
  implicit val NumberEFTWrites = Json.writes[NumberEFT]
  implicit val NumberEFTReads = Json.reads[NumberEFT]

  case class Numbers(
                      ach: Seq[NumberACH],
                      eft: Seq[NumberEFT]) extends Plaid
  implicit val NumbersWrites = Json.writes[Numbers]
  implicit val NumbersReads = Json.reads[Numbers]

  case class AuthGetResponse(item: ItemStatus, accounts: Seq[Account], numbers: Numbers) extends Plaid
  implicit val AuthGetResponseWrites = Json.writes[AuthGetResponse]
  implicit val AuthGetResponseReads = Json.reads[AuthGetResponse]

  case class AccountsBalanceGetRequest(access_token: String, account_ids: Option[Seq[String]] = None) extends Plaid with AccessTokenRequest
  implicit val AccountsBalanceGetRequestWrites = Json.writes[AccountsBalanceGetRequest]
  implicit val AccountsBalanceGetRequestReads = Json.reads[AccountsBalanceGetRequest]

  case class AccountsBalanceGetResponse(item: ItemStatus, accounts: Seq[Account]) extends Plaid
  implicit val AccountsBalanceGetResponseWrites = Json.writes[AccountsBalanceGetResponse]
  implicit val AccountsBalanceGetResponseReads = Json.reads[AccountsBalanceGetResponse]

  case class IdentityGetRequest(access_token: String) extends Plaid with AccessTokenRequest
  implicit val IdentityGetRequestWrites = Json.writes[IdentityGetRequest]
  implicit val IdentityGetRequestReads = Json.reads[IdentityGetRequest]

  case class Email(
                primary: Boolean,
                emails: Seq[Email],
                data: String,
                `type`: String) extends Plaid
  implicit val EmailWrites = Json.writes[Email]
  implicit val EmailReads = Json.reads[Email]

  case class AddressData(
                          street: String,
                          city: String,
                          state: String,
                          zip: String) extends Plaid
  implicit val AddressDataWrites = Json.writes[AddressData]
  implicit val AddressDataReads = Json.reads[AddressData]

  case class Address(
                      accounts: Seq[String],
                      primary: Boolean,
                      data: AddressData) extends Plaid
  implicit val AddressWrites = Json.writes[Address]
  implicit val AddressReads = Json.reads[Address]

  case class PhoneNumber(
                          primary: Boolean,
                          emails: Seq[Email],
                          data: String,
                          `type`: String) extends Plaid
  implicit val PhoneNumberWrites = Json.writes[PhoneNumber]
  implicit val PhoneNumberReads = Json.reads[PhoneNumber]

  case class Identity(
               names: Seq[String],
               emails: Seq[Email],
               addresses: Seq[Address],
               phone_numbers: Seq[PhoneNumber]) extends Plaid
  implicit val IdentityWrites = Json.writes[Identity]
  implicit val IdentityReads = Json.reads[Identity]

  case class IdentityGetResponse(item: ItemStatus, accounts: Seq[Account], identity: Identity) extends Plaid
  implicit val IdentityGetResponseWrites = Json.writes[IdentityGetResponse]
  implicit val IdentityGetResponseReads = Json.reads[IdentityGetResponse]

  case class IncomeGetRequest(access_token: String) extends Plaid with AccessTokenRequest
  implicit val IncomeGetRequestWrites = Json.writes[IncomeGetRequest]
  implicit val IncomeGetRequestReads = Json.reads[IncomeGetRequest]

  case class IncomeStream(
               confidence: Double,
               days: Double,
               monthly_income: Double,
               name: String) extends Plaid
  implicit val IncomeStreamWrites = Json.writes[IncomeStream]
  implicit val IncomeStreamReads = Json.reads[IncomeStream]

  case class Income(
                     last_year_income: Double,
                     last_year_income_before_tax: Double,
                     projected_yearly_income: Double,
                     projected_yearly_income_before_tax: Double,
                     max_number_of_overlapping_income_sreams: Int,
                     number_of_income_streams: Int,
                     income_streams: Seq[IncomeStream]) extends Plaid
  implicit val IncomeWrites = Json.writes[Income]
  implicit val IncomeReads = Json.reads[Income]

  case class IncomeGetResponse(item: ItemStatus, income: Income) extends Plaid
  implicit val IncomeGetResponseWrites = Json.writes[IncomeGetResponse]
  implicit val IncomeGetResponseReads = Json.reads[IncomeGetResponse]


  case class TransactionsGetRequest(access_token: String, start_date: DateTime, end_date: DateTime, account_ids: Option[Seq[String]] = None, count: Option[Int] = None, offset: Option[Int] = None) extends Plaid with AccessTokenRequest
  implicit val TransactionsGetRequestWrites = Json.writes[TransactionsGetRequest]
  implicit val TransactionsGetRequestReads = Json.reads[TransactionsGetRequest]


  case class PaymentMeta(
                          by_order_of: String,
                          payee: String,
                          payer: String,
                          payment_method: String,
                          payment_processor: String,
                          ppd_id: String,
                          reason: String,
                          reference_number: String) extends Plaid
  implicit val PaymentMetaWrites = Json.writes[PaymentMeta]
  implicit val PaymentMetaReads = Json.reads[PaymentMeta]

  case class Location(
                       address: String,
                       city: String,
                       state: String,
                       zip: String,
                       lat: Double,
                       lon: Double,
                       store_number: String) extends Plaid
  implicit val LocationWrites = Json.writes[Location]
  implicit val LocationReads = Json.reads[Location]

  case class Transactions(
                           account_id: String,
                           amount: Double,
                           iso_currency_code: String,
                           unofficial_currency_code: String,
                           category: Seq[String],
                           category_id: String,
                           date: String,
                           location: Location,
                           name: String,
                           original_description: String,
                           payment_meta: PaymentMeta,
                           pending: Boolean,
                           pending_transaction_id: String,
                           transaction_id: String,
                           transaction_type: String,
                           account_owner: String) extends Plaid
  implicit val TransactionsWrites = Json.writes[Transactions]
  implicit val TransactionsReads = Json.reads[Transactions]

  case class TransactionsGetResponse(item: ItemStatus, accounts: Seq[Account], transactions: Seq[Transactions], total_transactions: Int) extends Plaid
  implicit val TransactionsGetResponseWrites = Json.writes[TransactionsGetResponse]
  implicit val TransactionsGetResponseReads = Json.reads[TransactionsGetResponse]

  case class CreditDetailsGetRequest(access_token: String) extends Plaid with AccessTokenRequest
  implicit val CreditDetailsGetRequestWrites = Json.writes[CreditDetailsGetRequest]
  implicit val CreditDetailsGetRequestReads = Json.reads[CreditDetailsGetRequest]

  case class Apr(
                  apr: Double,
                  balance_subject_to_apr: Double,
                  interest_charge_amount: Double) extends Plaid
  implicit val AprWrites = Json.writes[Apr]
  implicit val AprReads = Json.reads[Apr]

  case class Aprs(
               balance_transfers: Apr,
               cash_advances: Apr,
               purchases: Apr) extends Plaid
  implicit val AprsWrites = Json.writes[Aprs]
  implicit val AprsReads = Json.reads[Aprs]

  case class CreditDetail(
                           account_id: String,
                           aprs: Aprs,
                           last_payment_amount: Double,
                           last_payment_date: DateTime,
                           last_statement_balance: Double,
                           last_statement_date: DateTime,
                           minimum_payment_amount: Double,
                           next_bill_due_date: DateTime) extends Plaid
  implicit val CreditDetailWrites = Json.writes[CreditDetail]
  implicit val CreditDetailReads = Json.reads[CreditDetail]

  case class CreditDetailsGetResponse(item: ItemStatus, accounts: Seq[Account], creditDetails: Seq[CreditDetail]) extends Plaid
  implicit val CreditDetailsGetResponseWrites = Json.writes[CreditDetailsGetResponse]
  implicit val CreditDetailsGetResponseReads = Json.reads[CreditDetailsGetResponse]
}

