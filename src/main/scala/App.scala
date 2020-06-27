import java.text.SimpleDateFormat
import java.util

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import org.apache.hadoop.dynamodb.DynamoDBItemWritable
import org.apache.hadoop.dynamodb.read.DynamoDBInputFormat
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.{SparkConf, SparkContext}

case class Transaction(id: Long,
                       userId: Long,
                       amount: Long,
                       processedTime: util.Date)

object App {

  def main(args: Array[String]): Unit = {

    val region = "ap-northeast-1"
    val table = "Transaction"

    val conf = new SparkConf()
      .setAppName("Analyser")
      .setMaster("local[*]") // here local mode. And * means you will use as much as you have cores.

    val sc = SparkContext.getOrCreate(conf)

    val jobConf = new JobConf(sc.hadoopConfiguration)
    jobConf.set("dynamodb.servicename", "dynamodb")
    jobConf.set("dynamodb.input.tableName", table)
    jobConf.set("dynamodb.regionid", region)
    jobConf.set(
      "mapred.output.format.class",
      "org.apache.hadoop.dynamodb.write.DynamoDBOutputFormat"
    )
    jobConf.set(
      "mapred.input.format.class",
      "org.apache.hadoop.dynamodb.read.DynamoDBInputFormat"
    )

    val transactions = sc
      .hadoopRDD(
        jobConf,
        classOf[DynamoDBInputFormat],
        classOf[Text],
        classOf[DynamoDBItemWritable]
      )
      .map(t => t._2.getItem)
      .map(item => toTransaction(item))

    val countItems = transactions.count()

    println("- Count total :" + countItems)
  }

  def toTransaction(item: util.Map[String, AttributeValue]): Transaction = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    Transaction(
      id = item.get("id").getS.toLong,
      userId = item.get("userId").getS.toLong,
      amount = item.get("amount").getS.toLong,
      processedTime = sdf.parse(item.get("processedTime").getS)
    )
  }
}
