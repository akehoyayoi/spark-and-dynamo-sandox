import java.util

import com.amazonaws.services.dynamodbv2.model.AttributeValue

case class Transaction(id: Long,
                       userId: Long,
                       amount: Long,
                       processedTime: util.Date)

def toTransaction(item: util.Map[String, AttributeValue]): Transaction = {
  Transaction(
    id = item.get("id").getS.toLong,
    userId = item.get("userId").getS.toLong,
    amount = item.get("amount").getS.toLong,
    processedTime = item.get("processedTime")
  )
}

object App {

  def main(args: Array[String]): Unit = {

  }
}
