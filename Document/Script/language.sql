DROP table `language`;
CREATE TABLE IF NOT EXISTS `language` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(15) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `status` varchar(1) DEFAULT '1',
  `icon` varchar(200) DEFAULT NULL,
  `order` varchar(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ap_param_uk` (`code`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;