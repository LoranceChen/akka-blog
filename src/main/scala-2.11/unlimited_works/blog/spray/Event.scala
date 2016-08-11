package unlimited_works.blog.spray

case class SigninReq(account: String, password: String)
case class SigninRsp(result: Int, msg: String)