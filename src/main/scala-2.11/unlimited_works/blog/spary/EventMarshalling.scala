package unlimited_works.blog.spary

import spray.json.DefaultJsonProtocol

/**
  *
  */
trait EventMarshalling  extends DefaultJsonProtocol {

  implicit val signinRsp = jsonFormat2(SigninRsp)
  implicit val signinReq = jsonFormat2(SigninReq)
//  implicit val eventFormat = jsonFormat2(Event)
//  implicit val eventsFormat = jsonFormat1(Events)
//  implicit val ticketRequestFormat = jsonFormat1(TicketRequest)
//  implicit val ticketFormat = jsonFormat1(TicketSeller.Ticket)
//  implicit val ticketsFormat = jsonFormat2(TicketSeller.Tickets)
//  implicit val errorFormat = jsonFormat1(Error)
}
