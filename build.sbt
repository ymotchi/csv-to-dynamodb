name := "csv-to-dynamodb"

version := "0.1"

scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv" % "2.0.0",
  "software.amazon.awssdk" % "dynamodb" % "2.35.0"
)
