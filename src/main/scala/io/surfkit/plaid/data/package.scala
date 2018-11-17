package io.surfkit.plaid

import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json._

package object data {

  sealed trait Plaid{}



  case class Item(
                   available_products: Seq[String],
                   billed_products: Seq[String],
                   error: Option[String] = None,
                   institution_id: String,
                   item_id: String,
                   webhook: String) extends Plaid
  implicit val ItemWrites = Json.writes[Item]
  implicit val ItemReads = Json.reads[Item]

  case class Balances(
                 available: Option[Double],
                 current: Option[Double],
                 limit: Option[Double],
                 iso_currency_code: String,
                 unofficial_currency_code: Option[String]) extends Plaid
  implicit val BalancesWrites = Json.writes[Balances]
  implicit val BalancesReads = Json.reads[Balances]

  trait AccountType
  object AccountType{
    case object Brokerage extends AccountType
    case object Credit extends AccountType
    case object Depository extends AccountType
    case object Loan extends AccountType
    case object Mortgage extends AccountType
    case object Other extends AccountType
  }
  implicit val AccountTypeWrites = new Writes[AccountType] {
    def writes(at : AccountType): JsValue = JsString(at.getClass.getSimpleName.toLowerCase)
  }
  implicit val AccountTypeReads: Reads[AccountType] = (
    (JsPath).read[String].map{
      case "brokerage" => AccountType.Brokerage
      case "credit" => AccountType.Credit
      case "depository" => AccountType.Depository
      case "loan" => AccountType.Loan
      case "mortgage" => AccountType.Mortgage
      case _ => AccountType.Other
    })


  trait AccountSubType
  object AccountSubType{
    case object Brokerage extends AccountSubType
    case object Credit extends AccountSubType
    case object Depository extends AccountSubType
    case object Loan extends AccountSubType
    case object Mortgage extends AccountSubType
    case object Other extends AccountSubType
  }
  implicit val AccountSubTypeWrites = new Writes[AccountSubType] {
    def writes(at : AccountSubType): JsValue = JsString(at.getClass.getSimpleName.toLowerCase)
  }
  implicit val AccountSubTypeReads: Reads[AccountSubType] = (
    (JsPath).read[String].map{
      case "brokerage" => AccountSubType.Brokerage
      case "credit" => AccountSubType.Credit
      case "depository" => AccountSubType.Depository
      case "loan" => AccountSubType.Loan
      case "mortgage" => AccountSubType.Mortgage
      case _ => AccountSubType.Other
    })

  case class Account(
               account_id: String,
               item: Item,
               balances: Balances,
               name: String,
               mask: String,
               official_name: String,
               `type`: AccountType,
               subtype: AccountSubType) extends Plaid
  implicit val AccountWrites = Json.writes[Account]
  implicit val AccountReads = Json.reads[Account]





  case class AccountMeta(
                      id: String,
                      name: String,
                      mask: String,
                      official_name: String,
                      `type`: AccountType,
                      subtype: AccountSubType) extends Plaid
  implicit val AccountMetaWrites = Json.writes[AccountMeta]
  implicit val AccountMetaReads = Json.reads[AccountMeta]

  case class InstitutionMeta(
                      name: String,
                      institution_id: String) extends Plaid
  implicit val InstitutionMetaWrites = Json.writes[InstitutionMeta]
  implicit val InstitutionMetaReads = Json.reads[InstitutionMeta]



  case class MetaData(
                      link_session_id: String,
                      iteminstitution: Seq[InstitutionMeta],
                      accounts: Seq[AccountMeta]) extends Plaid
  implicit val MetaDataWrites = Json.writes[MetaData]
  implicit val MetaDataReads = Json.reads[MetaData]


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

}

