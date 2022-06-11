name := "csv-to-dynamodb"

version := "0.1"

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv" % "1.3.10",
  "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.12.237"
)
