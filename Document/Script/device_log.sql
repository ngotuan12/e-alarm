CREATE TABLE IF NOT EXISTS `device_log` ( 
    id         	int(11) AUTO_INCREMENT NOT NULL,
    device_id        int(11) not null,
    reason_id        int(11) not null,
    device_infor_id     int(11),
    old_value        float,   
    new_value        float,
    PRIMARY KEY(id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


