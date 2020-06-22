import java.util

import com.amazonaws.services.dynamodbv2.model.AttributeValue

case class Transaction(id: Long,
                       userId: Long,
                       amount: Long,
                       processedTime: util.Date)

def toTransaction(item: util.Map[String, AttributeValue]): Transaction = {
  Transaction(
    id = item.get("id"),
    userId = item.get("userId"),
    amount = item.get("amount"),
    processedTime = item.get("processedTime")
  )
}

object App {

  def main(args: Array[String]): Unit = {

  }
}
