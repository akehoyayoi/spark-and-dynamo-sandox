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
| Id | number(Hash Attribute Name) |
| UserId | number(Range Attribute Name) |
| Amount | number |
| ProcessedTime | string |

example input after creating table
```
{
  "ProcessedTime": "2020-06-01 00:00:00",
  "Id": 0,
  "UserId": 0,
  "Amount": 100
}
```

#### Process:Spark

summarize by userId

#### Output:S3

fileName: [YYYYMMDD]/[userId].json
```
{"amount":1000}
```

#### execute



```
sbt package

docker-compose run master /spark/bin/spark-submit  \
--class App \
--master spark://localhost:7077 \
--deploy-mode cluster \
 target/scala-2.11/example-service_2.11-1.0.jar
-> NG
20/06/27 07:37:03 INFO Utils: Successfully started service 'driverClient' on port 45333.
Exception in thread "main" org.apache.spark.SparkException: Exception thrown in awaitResult:
	at org.apache.spark.util.ThreadUtils$.awaitResult(ThreadUtils.scala:226)
	at org.apache.spark.rpc.RpcTimeout.awaitResult(RpcTimeout.scala:75)
	at org.apache.spark.rpc.RpcEnv.setupEndpointRefByURI(RpcEnv.scala:101)
	at org.apache.spark.rpc.RpcEnv.setupEndpointRef(RpcEnv.scala:109)
	at org.apache.spark.deploy.ClientApp$$anonfun$7.apply(Client.scala:243)
	at org.apache.spark.deploy.ClientApp$$anonfun$7.apply(Client.scala:243)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:234)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:234)
	at scala.collection.IndexedSeqOptimized$class.foreach(IndexedSeqOptimized.scala:33)
	at scala.collection.mutable.ArrayOps$ofRef.foreach(ArrayOps.scala:186)
	at scala.collection.TraversableLike$class.map(TraversableLike.scala:234)
	at scala.collection.mutable.ArrayOps$ofRef.map(ArrayOps.scala:186)
	at org.apache.spark.deploy.ClientApp.start(Client.scala:243)
	at org.apache.spark.deploy.SparkSubmit.org$apache$spark$deploy$SparkSubmit$$runMain(SparkSubmit.scala:845)
	at org.apache.spark.deploy.SparkSubmit.doRunMain$1(SparkSubmit.scala:161)
	at org.apache.spark.deploy.SparkSubmit.submit(SparkSubmit.scala:184)
	at org.apache.spark.deploy.SparkSubmit.doSubmit(SparkSubmit.scala:86)
	at org.apache.spark.deploy.SparkSubmit$$anon$2.doSubmit(SparkSubmit.scala:920)
	at org.apache.spark.deploy.SparkSubmit$.main(SparkSubmit.scala:929)
	at org.apache.spark.deploy.SparkSubmit.main(SparkSubmit.scala)
Caused by: java.io.IOException: Failed to connect to localhost/127.0.0.1:7077
	at org.apache.spark.network.client.TransportClientFactory.createClient(TransportClientFactory.java:245)
	at org.apache.spark.network.client.TransportClientFactory.createClient(TransportClientFactory.java:187)
	at org.apache.spark.rpc.netty.NettyRpcEnv.createClient(NettyRpcEnv.scala:198)
	at org.apache.spark.rpc.netty.Outbox$$anon$1.call(Outbox.scala:194)
	at org.apache.spark.rpc.netty.Outbox$$anon$1.call(Outbox.scala:190)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
Caused by: io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: localhost/127.0.0.1:7077
Caused by: java.net.ConnectException: Connection refused
	at sun.nio.ch.SocketChannelImpl.checkConnect(Native Method)
	at sun.nio.ch.SocketChannelImpl.finishConnect(SocketChannelImpl.java:717)
	at io.netty.channel.socket.nio.NioSocketChannel.doFinishConnect(NioSocketChannel.java:327)
	at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.finishConnect(AbstractNioChannel.java:334)
	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:688)
	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:635)
	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:552)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:514)
	at io.netty.util.concurrent.SingleThreadEventExecutor$6.run(SingleThreadEventExecutor.java:1044)
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)
20/06/27 07:37:03 INFO ShutdownHookManager: Shutdown hook called
20/06/27 07:37:03 INFO ShutdownHookManager: Deleting directory /tmp/spark-2c0040f8-679a-4fac-8f7d-152e88aa0928

docker cp target/scala-2.11/example-service_2.11-1.0.jar 18_master_1:example-service_2.11-1.0.jar
docker-compose exec master /bin/bash
spark/bin/spark-submit  --class App  --master spark://localhost:7077  --deploy-mode cluster example-service_2.11-1.0.jar
-> NG
20/06/27 07:40:15 ERROR ClientEndpoint: Exception from cluster was: java.nio.file.NoSuchFileException: /example-service_2.11-1.0.jar
java.nio.file.NoSuchFileException: /example-service_2.11-1.0.jar
	at sun.nio.fs.UnixException.translateToIOException(UnixException.java:86)
	at sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:102)
	at sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:107)
	at sun.nio.fs.UnixCopyFile.copy(UnixCopyFile.java:526)
	at sun.nio.fs.UnixFileSystemProvider.copy(UnixFileSystemProvider.java:253)
	at java.nio.file.Files.copy(Files.java:1274)
	at org.apache.spark.util.Utils$.org$apache$spark$util$Utils$$copyRecursive(Utils.scala:664)
	at org.apache.spark.util.Utils$.copyFile(Utils.scala:635)
	at org.apache.spark.util.Utils$.doFetchFile(Utils.scala:719)
	at org.apache.spark.util.Utils$.fetchFile(Utils.scala:509)
	at org.apache.spark.deploy.worker.DriverRunner.downloadUserJar(DriverRunner.scala:155)
	at org.apache.spark.deploy.worker.DriverRunner.prepareAndRunDriver(DriverRunner.scala:173)
	at org.apache.spark.deploy.worker.DriverRunner$$anon$1.run(DriverRunner.scala:92)
20/06/27 07:40:15 INFO ShutdownHookManager: Shutdown hook called
20/06/27 07:40:15 INFO ShutdownHookManager: Deleting directory /tmp/spark-08f093f3-c6d0-4450-86bb-966e500c527a

docker-compose exec master /spark/bin/spark-shell --master spark://localhost:7077
-> OK

```






