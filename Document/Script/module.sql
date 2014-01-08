DROP TABLE `module`
GO
CREATE TABLE IF NOT EXISTS `module` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `module_type` varchar(2) NOT NULL,
  `module_name` varchar(100) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `order` varchar(5) DEFAULT NULL,
  `include_menu` varchar(1) DEFAULT NULL,
  `app_id` int(11) DEFAULT NULL,
  `level` varchar(5) DEFAULT NULL,
  `woodenleg` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7
GO
DROP TRIGGER IF EXISTS `module_tg_bf_is`;
GO
CREATE TRIGGER `module_tg_bf_is` BEFORE INSERT ON `module`
 FOR EACH ROW BEGIN

DECLARE new_level INT;
DECLARE parent_woodenleg VARCHAR(100);
DECLARE new_woodenleg VARCHAR(100);
DECLARE next_id VARCHAR(100);
DECLARE c_parent CURSOR FOR SELECT woodenleg,level+1 FROM `module` WHERE id = NEW.parent_id;
SET next_id = (SELECT CAST(AUTO_INCREMENT as BINARY) FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='module');
IF NEW.parent_id is null THEN
SET NEW.woodenleg := SUBSTRING(CONCAT('00',next_id),LENGTH(next_id),3);
SET NEW.level := 1;
ELSE 
OPEN c_parent;
FETCH c_parent INTO parent_woodenleg,new_level;
SET new_woodenleg := CONCAT(parent_woodenleg,SUBSTRING(CONCAT('00',next_id),LENGTH(next_id),3));
SET NEW.woodenleg := new_woodenleg;
SET NEW.level := new_level;
END IF;

END
GO
DROP TRIGGER IF EXISTS `module_tg_bf_ud`;
GO
CREATE TRIGGER `module_tg_bf_ud` BEFORE UPDATE ON `module`
 FOR EACH ROW BEGIN

DECLARE new_level INT;
DECLARE parent_woodenleg VARCHAR(100);
DECLARE new_woodenleg VARCHAR(100);
DECLARE cur_id VARCHAR(100);
DECLARE c_parent CURSOR FOR SELECT woodenleg,level+1 FROM `module` WHERE id = NEW.parent_id;

SET cur_id = CAST(OLD.id as BINARY);

IF NEW.parent_id is null THEN
SET NEW.woodenleg := SUBSTRING(CONCAT('00',cur_id),LENGTH(cur_id),3);
SET NEW.level := 1;
ELSE 
OPEN c_parent;
FETCH c_parent INTO parent_woodenleg,new_level;
SET new_woodenleg := CONCAT(parent_woodenleg,SUBSTRING(CONCAT('00',cur_id),LENGTH(cur_id),3));
SET NEW.woodenleg := new_woodenleg;
SET NEW.level := new_level;
END IF;

END

