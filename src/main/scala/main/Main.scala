package main

import com.github.tototoshi.csv._
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.{AttributeValue, PutItemRequest}

import java.io.File
import scala.jdk.CollectionConverters._
import scala.util.Using
import scala.util.control.NonFatal

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

    val dynamoDb = DynamoDbClient.builder().build()

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
          val request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item.asJava)
            .build()
          dynamoDb.putItem(request)
      }
    }
  } catch {
    case NonFatal(e) =>
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
    case "boolean" => DynamoBoolean
    case t => throw new Exception(s"無効な型名です: $t")
  }
}

object DynamoString extends DynamoType {
  override def typed(value: String): AttributeValue = AttributeValue.builder().s(value).build()
}

object DynamoNumber extends DynamoType {
  override def typed(value: String): AttributeValue = AttributeValue.builder().n(value).build()
}

object DynamoBoolean extends DynamoType {
  override def typed(value: String): AttributeValue = AttributeValue.builder().bool(value match {
    case "1" => true
    case "0" => false
    case v => v.toBoolean
  }).build()
}
