name := "csv-to-dynamodb"

version := "0.1"

scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv" % "1.4.1",
  "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.12.765"
)
