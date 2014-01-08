CREATE TABLE IF NOT EXISTS `map_pro_cmd` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    pro_id      int(11) not null,
    command_param   varchar(50) not null,
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8