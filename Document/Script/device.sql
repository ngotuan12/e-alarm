CREATE TABLE IF NOT EXISTS `device` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    code        varchar(11),
    area_id     int(11) not null, 
    area_code   varchar(20),
    address     varchar(200),
    position_x  float,
    position_y  float,
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8

