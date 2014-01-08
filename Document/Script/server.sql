CREATE TABLE IF NOT EXISTS `server` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    ip_add   varchar(100) not null,
    port     varchar(50),
    status     varchar(1),
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8