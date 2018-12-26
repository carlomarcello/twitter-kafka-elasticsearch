
1. Criar topic no Kafka para receber os dados retirados do Twitter:

	$ kafka-topics.bat --zookeeper localhost:2181 --create --topic tweets_by_subject --partitions 3 --replication-factor 1

2. Criar um index no Elasticsearch

	$ curl -X PUT http://localhost:9200/twitter

3. Criar um type/mapping no Elasticsearch

	$ curl -X PUT http://localhost:9200/twitter/_mapping/tweet -d '
	{
	    "tweet" : {
	        "properties" : {
	            "createdAt" : {"type" : "text"},
	            "user" : {"type" : "text"},
	            "message" : {"type" : "text"}
	        }
	    }
	}
	'