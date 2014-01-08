CREATE TABLE IF NOT EXISTS `gateway` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    mac_add   varchar(100) not null,
    connected_server   varchar(50),
    `status`     varchar(1),
    `type`      varchar(1),
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8