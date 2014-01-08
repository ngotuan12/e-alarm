CREATE TABLE IF NOT EXISTS `cmd` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    `cmd_name`      varchar(50) not null,
    `type`   varchar(50) not null,
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8