*	Run MySQL container
docker run --detach --name ez-mysql -p 6605:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=quartz_demo -e MYSQL_USER=asanka -e MYSQL_PASSWORD=matrix -d asankawpa/mysql-quartz:v1

* Copy db table creation script to container:
cp /Users/asanka.perera/dev/my-grand-project/quartz-scheduler/quartz_tables.sql <container-id>:/

* Run the script in container:
docker exec -it <container-id> /bin/sh -c 'mysql -u root -ppassword --database=quartz_demo </quartz_tables.sql'

* Optionally login to container:
docker exec -it <container-id>  mysql -uroot -ppassword
  
* ..
