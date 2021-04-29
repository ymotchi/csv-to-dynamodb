package main

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, PutItemRequest}
import com.github.tototoshi.csv.CSVReader

import java.io.File
import scala.jdk.CollectionConverters._
import scala.util.Using

object Main extends App {

  try {
    if (args.length < 2) {
      throw new Exception("""Usage: sbt "run <CSV file path> <DynamoDB table name>"""")
    }

    val filePath = new File(args(0))
    if (!filePath.isFile) {
      throw new Exception(s"ファイルが存在しません: $filePath")
    }

    val tableName = args(1)

    val dynamoDb = AmazonDynamoDBClientBuilder.standard().build()

    Using(CSVReader.open(filePath)) { reader =>
      val iterator = reader.iterator
      val header = iterator.next().map { headerItem =>
        headerItem.split(':') match {
          case Array(name, dynamoType) =>
            (name, DynamoType.valueOf(dynamoType))
          case _ =>
            throw new Exception(s"""ヘッダーは"属性名:型名"のフォーマットにしてください: $headerItem""")
        }
      }

      iterator.map(_.zip(header).map {
        case (value, (attributeName, attributeType)) =>
          attributeName -> attributeType.typed(value)
      }.toMap).foreach {
        item =>
          println(item)
          dynamoDb.putItem(new PutItemRequest(tableName, item.asJava))
      }
    }
  } catch {
    case e: Exception =>
      e.printStackTrace()
  }

}

sealed trait DynamoType {
  def typed(value: String): AttributeValue
}

object DynamoType {
  def valueOf(value: String): DynamoType = value match {
    case "string" => DynamoString
    case "number" => DynamoNumber
    case t => throw new Exception(s"無効な型名です: $t")
  }
}

object DynamoString extends DynamoType {
  override def typed(value: String): AttributeValue = new AttributeValue().withS(value)
}

object DynamoNumber extends DynamoType {
  override def typed(value: String): AttributeValue = new AttributeValue().withN(value)
}

object DynamoBoolean extends DynamoType {
  override def typed(value: String): AttributeValue = new AttributeValue().withBOOL(value match {
    case "1" => true
    case "0" => false
    case v => v.toBoolean
  })
}