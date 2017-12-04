package datastore

import java.util.concurrent.ConcurrentHashMap
import com.google.inject.Singleton
import datatypes.{GameResult, Player}

@Singleton
class SimpleDatastore {
  val gameTable = new ConcurrentHashMap[String, GameResult]()
  val playerTable = new ConcurrentHashMap[String, Player]()
}
