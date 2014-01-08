CREATE TABLE IF NOT EXISTS `reason` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    code        varchar(20),
    `name`      varchar(100),
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8