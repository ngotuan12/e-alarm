CREATE TABLE IF NOT EXISTS `devices_infor` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    device_id   int(11) not null,
    infor_id    int(11) not null,
    `value`     int(11),
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8