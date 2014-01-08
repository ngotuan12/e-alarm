CREATE TABLE IF NOT EXISTS `user` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    dept_id     int(11) NULL,
    owner_id    int(11) NULL,
    username   	varchar(15) NULL,
    `password`   	varchar(15) NULL,
    `status`  	varchar(1) NULL,
    `sex` varchar(1) NOT NULL DEFAULT '1',
    `email` varchar(200) NULL,
    phone varchar(30) NULL,
    id_no varchar(30) NULL,
    address varchar(200) null,
    birth_day date null,
    create_date	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8