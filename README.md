# CSV to DynamoDB
DynamoDBに、ローカルのCSVファイルから簡単にデータをインポートしたいなぁ…  
ググってもなかなか見つからないぞ…ん？プログラム書かなきゃいけないのか？それは面倒。

というわけで、シンプルなやつを作ってみました。

Import data from a local CSV file into a DynamoDB table

# Prerequisites
- JDK 8+
- sbt

# Installation
```bash
git clone https://github.com/ymotchi/csv-to-dynamodb.git
```

# Usage
```bash
# Under "csv-to-dynamodb" directory
sbt
> run <CSV file path> <DynamoDB table name>
```

# CSV format

The first line must be header,  
and each item must represent attribute name with the type separated by ":", like;
```text
"ID:number","attr1:string","attr2:boolean"
```
Available types are "number", "string" and "boolean"

So the entire file looks like this;
```text
"ID:number","attr1:string","attr2:boolean"
1,hello,true
2,"Hello, world",false
```

# License
See ./LICENSE

This software includes the work that is distributed in the Apache License 2.0.
