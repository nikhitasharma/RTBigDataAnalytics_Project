name := "ImageClassification"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.0.0" % "provided",
  "org.apache.spark" %% "spark-streaming" % "2.0.0",
  "org.apache.spark" %% "spark-mllib" % "2.0.0",
  "org.apache.spark" % "spark-sql_2.11" % "2.0.0"
)
//libraryDependencies ++= Seq(
//    "org.apache.spark" % "spark-core" % "2.0.0" % "provided",
//    "org.apache.spark" % "spark-mllib" % "2.0.0",
//    "org.scala-lang" % "scala-library" % "2.12.0-M5",
//    "com.google.guava" % "guava" % "19.0",
//    "org.scalatest" % "scalatest_2.11" % "3.0.0",
//    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.2",
//    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.2",
//    "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.2"
//)