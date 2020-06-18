# Spark & DynamoDB sandbox

## How to use

docker-compose -d up

## For DynamoDB

access `http://localhost:50727`

## For Spark

run `docker-compose exec master /spark/bin/spark-shell --master spark://localhost:7077`

### example

```
scala> sc
res0: org.apache.spark.SparkContext = org.apache.spark.SparkContext@4fa91d5b

scala> val ds = Seq(1, 2, 3).toDS()
ds: org.apache.spark.sql.Dataset[Int] = [value: int]

scala> ds.show()
+-----+
|value|
+-----+
|    1|
|    2|
|    3|
+-----+

scala> ds.map(_ * 2).show()
+-----+
|value|
+-----+
|    2|
|    4|
|    6|
+-----+
```

access `http://localhost:4040/`

can confirm job status

