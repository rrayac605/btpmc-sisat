#Introduction 
Este proyecto contiene la funcionalidad batch para la ejecución de la carga del archivo SISAT. 

Orden de compilación

1.- btpmc-commons -> librería(al compilar se incluye en el jar ejecutable)
2.- btpmc-download -> librería(al compilar se incluye en el jar ejecutable)
3.- btpmc-services -> librería(al compilar se incluye en el jar ejecutable)
4.- btpmc-validation -> librería(al compilar se incluye en el jar ejecutable)
5.- btpmc-nssa -> ejecutable
6.- btpmc-sisat -> ejecutable
7.- btpmc-sui55 -> ejecutable

JDK 8
Maven 3
Spring Batch

---------Variables de entorno ambiente QA---------

pmcBatchAuthentication -> PMCQA01

pmcBatchUserName -> pmcbatch

pmcBatchPassword -> pmcbatch0

pmcBatchDatabase -> PMCQA01

pmcBatchPort -> 27017

pmcBatchHost -> 10.100.8.78

pmcBatchNssaLog -> /home/usr_pmc/files/mspmc-archivos/logs/sisat.log

cron.expression.sisat -> 0 0 5 * * *

portMSBatchSisat -> 9016

---------Variables de entorno ambiente UAT---------

pmcBatchAuthentication -> PMCUAT01

pmcBatchUserName -> pmcbatch

pmcBatchPassword -> pmcbatch0

pmcBatchDatabase -> PMCUAT01

pmcBatchPort -> 27017

pmcBatchHost -> 10.100.8.80

pmcBatchNssaLog -> /home/usr_pmc/files/mspmc-archivos/logs/sisat.log

cron.expression.sisat -> 0 0 5 * * *

portMSBatchSisat -> 9016

---------Variables de entorno ambiente Producción---------

pmcBatchAuthentication -> [pendiente]

pmcBatchUserName -> [pendiente]

pmcBatchPassword -> [pendiente]

pmcBatchDatabase -> [pendiente]

pmcBatchPort -> [pendiente]

pmcBatchHost -> [pendiente]

pmcBatchNssaLog -> /home/usr_pmc/files/mspmc-archivos/logs/nssa.log

cron.expression.nssa -> 0 0 5 * * *

portMSBatchNssa -> 9016

#Getting Started
1.	Installation process
mvn clean install -DskipTest

2.	Software dependencies
No tiene dependencias. 

3.	Latest releases

4.	API references

#Build and Test
mvn clean install

