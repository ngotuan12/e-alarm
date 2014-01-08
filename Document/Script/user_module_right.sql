CREATE TABLE IF NOT EXISTS `user_module_right` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    user_id     int(11) NOT NULL,
    module_id    int(11) NOT NULL,
    right_code   varchar(1),
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8