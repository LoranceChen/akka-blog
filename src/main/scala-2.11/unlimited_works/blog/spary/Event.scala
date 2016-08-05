package unlimited_works.blog.spary

/**
  *
  */

case class SigninReq(account: String, password: String)
case class SigninRsp(result: Int, msg: String)