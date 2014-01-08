CREATE TABLE IF NOT EXISTS `gateway_log` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    gateway_id   int(11) not null,
    issue_date   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    command      varchar(500),
    `type`      varchar(1),
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8