package noface.db.postgres

import java.sql.{ResultSet, Connection, DriverManager}

/**
  * communicate with pg server, abstract to c,r,u,d
  *
  */
class DbDriver(connUrl: String = "jdbc:postgresql://10.1.0.117/first_db?user=nobody&password= &ssl=false") {
//  val url = "jdbc:postgresql://192.168.1.101:5432/first_db"
//  val url = "jdbc:postgresql://10.1.0.117:5432/first_db"
//  val props = new Properties()
//  props.setProperty("user","nobody")
//  props.setProperty("password"," ")
//  props.setProperty("ssl","false")
//  val conn = DriverManager.getConnection(url, props)

//  val url = "jdbc:postgresql://192.168.1.101/first_db?user=nobody&password= &ssl=false"
//  val url = "jdbc:postgresql://10.1.0.117/first_db?user=nobody&password= &ssl=false"
  val conn = DriverManager.getConnection(connUrl)

  //CUD
  private def cud(sql: String) = {
    val stmt = conn.createStatement()
    val rst = stmt.executeUpdate(sql)

    stmt.close()
    rst
  }

  def insert(sql: String) = {
    cud(sql)
  }

  def delete(sql: String) = {
    cud(sql)
  }

  def update(sql: String) = {
    cud(sql)
  }

  // eg. sql = "select * from ..."
  def query[T](sql: String, f: ResultSet => T): T = {
    val stmt = conn.createStatement()
    val queryRst = stmt.executeQuery(sql)

    val rst = f(queryRst)

    stmt.close()
    queryRst.close()

    rst
  }

  def close(): Unit = {
    conn.close()
  }
}

object DbDriver {
  //maybe we should access with other db
  val getInstance = new DbDriver()
}