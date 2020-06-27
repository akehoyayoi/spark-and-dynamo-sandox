# Spark & DynamoDB sandbox

## How to use

run `docker-compose -d up`

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

you can confirm job status during running spark-shell

## For S3(minio)

### example

```
$ aws configure --profile minio
AWS Access Key ID [None]: hogehoge
AWS Secret Access Key [None]: hogehoge
Default region name [None]:
Default output format [None]:

# create bucket
$ aws \
>   --endpoint-url http://127.0.0.1:9000 \
>   --profile minio s3 mb s3://hoge
make_bucket: hoge

# upload file
$ aws \
>   --endpoint-url http://127.0.0.1:9000 \
>   --profile minio s3 cp ./docker-compose.yml \
>   s3://hoge
upload: ./docker-compose.yml to s3://hoge/docker-compose.yml

# get list on minio
$ aws \
>   --endpoint-url http://127.0.0.1:9000 \
>   --profile minio s3 ls s3://hoge
2020-06-20 11:23:05       1080 docker-compose.yml
```

access `http://127.0.0.1:9000/minio`

### Process

Input:DynamoDB -> Process:Spark -> Output:S3

The process summarize the amount in the trasaction by userId.

#### Input:DynamoDB

TableName: Transaction

| column | type |
| ---- | ---- |
| id | number |
| userId | number |
| amount | number |
| processedTime | string |

#### Process:Spark

summarize by userId

#### Output:S3

fileName: [YYYYMMDD]/[userId].json
```
{"amount":1000}
```
