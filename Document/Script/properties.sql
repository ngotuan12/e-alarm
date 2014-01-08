CREATE TABLE IF NOT EXISTS `device_properties` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    code varchar(20) not null,
    name   varchar(50) not null,
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8