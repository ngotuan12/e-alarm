DROP TABLE IF EXISTS `ap_param`;
CREATE TABLE IF NOT EXISTS `ap_param` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `PAR_TYPE` varchar(15) DEFAULT NULL,
  `PAR_NAME` varchar(15) DEFAULT NULL,
  `PAR_VALUE` varchar(100) DEFAULT NULL,
  `DESCRIPTION` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ap_param_uk` (`PAR_TYPE`,`PAR_VALUE`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;