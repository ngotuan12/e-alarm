-- phpMyAdmin SQL Dump
-- version 4.0.4.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Jan 08, 2014 at 05:44 AM
-- Server version: 5.5.32
-- PHP Version: 5.4.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `ealarm`
--
CREATE DATABASE IF NOT EXISTS `ealarm` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `ealarm`;

-- --------------------------------------------------------

--
-- Table structure for table `ap_param`
--

CREATE TABLE IF NOT EXISTS `ap_param` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `PAR_TYPE` varchar(15) DEFAULT NULL,
  `PAR_NAME` varchar(15) DEFAULT NULL,
  `PAR_VALUE` varchar(100) DEFAULT NULL,
  `DESCRIPTION` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ap_param_uk` (`PAR_TYPE`,`PAR_VALUE`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `ap_param`
--

INSERT INTO `ap_param` (`id`, `PAR_TYPE`, `PAR_NAME`, `PAR_VALUE`, `DESCRIPTION`) VALUES
(1, 'GATEWAY_TYPE', 'RMS', '1', 'Thiết bị phần cứng'),
(3, 'GATEWAY_TYPE', 'MONITOR', '2', 'Server monitor');

-- --------------------------------------------------------

--
-- Table structure for table `area`
--

CREATE TABLE IF NOT EXISTS `area` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL,
  `name` varchar(200) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `level` varchar(5) DEFAULT NULL,
  `status` varchar(1) DEFAULT '1',
  `woodenleg` varchar(50) DEFAULT NULL,
  `lat` double DEFAULT NULL,
  `lng` double DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL,
  `full_name` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=19 ;

--
-- Dumping data for table `area`
--

INSERT INTO `area` (`id`, `code`, `name`, `parent_id`, `level`, `status`, `woodenleg`, `lat`, `lng`, `type`, `full_name`) VALUES
(1, 'VN', 'Việt Nam', NULL, '1', '0', '001', 14.058324, 108.277199, '1', 'Việt Nam'),
(2, 'HN', 'Hà Nội', 1, '2', '1', '001002', 21.00803, 105.846756, '2', 'Hà Nội,Việt Nam'),
(3, 'HCM', 'TP Hồ Chí Minh', 1, '2', '1', '001003', 10.803462, 106.638794, '2', 'TP Hồ Chí Minh,Việt Nam'),
(8, 'DN', 'Đà Nẵng', 1, '2', '1', '001008', 16.031213, 108.190613, '2', 'Đà Nẵng,Việt Nam'),
(9, 'HP', 'Hải Phòng', 1, '2', '1', '001009', 20.857829, 106.682053, '2', 'Hải Phòng'),
(10, 'PT', 'Phú Thọ', 1, '2', '1', '001010', 21.319591, 105.283012, '2', 'Phú Thọ'),
(13, 'HBT', 'Hai Bà Trưng', 2, '3', '1', '001002013', 21.006718, 105.856262, '3', 'Hai Bà Trưng,Hà Nội,Việt Nam'),
(14, 'BD', 'Ba Đình', 2, '3', '1', '001002014', 21.035852, 105.826094, '3', 'Ba Đình,Hà Nội,Việt Nam'),
(15, 'TL', 'Từ Liêm', 2, '3', '1', '001002015', 21.052054, 105.746854, '3', 'Từ Liêm,Hà Nội,Việt Nam'),
(16, 'GL', 'Gia Lâm', 2, '3', '1', '001002016', 21.031939, 105.958237, '3', 'Gia Lâm,Hà Nội,Việt Nam'),
(17, 'Q1', 'Quận 1', 3, '3', '1', '001003017', 10.775659, 106.700424, '3', 'Quận 1,TP Hồ Chí Minh,Việt Nam'),
(18, 'Q2', 'Quận 2', 3, '3', '1', '001003018', 10.796795, 106.758849, '3', 'Quận 2,TP Hồ Chí Minh,Việt Nam');

--
-- Triggers `area`
--
DROP TRIGGER IF EXISTS `area_tg_bf_is`;
DELIMITER //
CREATE TRIGGER `area_tg_bf_is` BEFORE INSERT ON `area`
 FOR EACH ROW BEGIN

DECLARE new_level INT;
DECLARE parent_woodenleg VARCHAR(100);
DECLARE new_woodenleg VARCHAR(100);
DECLARE next_id VARCHAR(100);
DECLARE c_parent CURSOR FOR SELECT woodenleg,level+1 FROM `area` WHERE id = NEW.parent_id;
SET next_id = (SELECT CAST(AUTO_INCREMENT as BINARY) FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='area');
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
//
DELIMITER ;
DROP TRIGGER IF EXISTS `area_tg_bf_ud`;
DELIMITER //
CREATE TRIGGER `area_tg_bf_ud` BEFORE UPDATE ON `area`
 FOR EACH ROW BEGIN

DECLARE new_level INT;
DECLARE parent_woodenleg VARCHAR(100);
DECLARE new_woodenleg VARCHAR(100);
DECLARE cur_id VARCHAR(100);
DECLARE c_parent CURSOR FOR SELECT woodenleg,level+1 FROM `area` WHERE id = NEW.parent_id;

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
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `cmd`
--

CREATE TABLE IF NOT EXISTS `cmd` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cmd_name` varchar(50) NOT NULL,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `cmd`
--

INSERT INTO `cmd` (`id`, `cmd_name`, `type`) VALUES
(1, 'get_data', 'request');

-- --------------------------------------------------------

--
-- Table structure for table `cmd_param`
--

CREATE TABLE IF NOT EXISTS `cmd_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cmd_id` int(11) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `level` varchar(5) DEFAULT NULL,
  `status` varchar(1) DEFAULT '1',
  `woodenleg` varchar(50) DEFAULT NULL,
  `cmd_name` varchar(50) NOT NULL,
  `cmd_value` varchar(50) DEFAULT NULL,
  `cmd_type` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `cmd_param`
--

INSERT INTO `cmd_param` (`id`, `cmd_id`, `parent_id`, `level`, `status`, `woodenleg`, `cmd_name`, `cmd_value`, `cmd_type`) VALUES
(1, 1, NULL, '1', '1', '001', 'get_data', NULL, 'request');

--
-- Triggers `cmd_param`
--
DROP TRIGGER IF EXISTS `cmd_param_tg_bf_is`;
DELIMITER //
CREATE TRIGGER `cmd_param_tg_bf_is` BEFORE INSERT ON `cmd_param`
 FOR EACH ROW BEGIN

DECLARE new_level INT;
DECLARE parent_woodenleg VARCHAR(100);
DECLARE new_woodenleg VARCHAR(100);
DECLARE next_id VARCHAR(100);
DECLARE c_parent CURSOR FOR SELECT woodenleg,level+1 FROM `cmd_param` WHERE id = NEW.parent_id;
SET next_id = (SELECT CAST(AUTO_INCREMENT as BINARY) FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='cmd_param');
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
//
DELIMITER ;
DROP TRIGGER IF EXISTS `cmd_param_tg_bf_ud`;
DELIMITER //
CREATE TRIGGER `cmd_param_tg_bf_ud` BEFORE UPDATE ON `cmd_param`
 FOR EACH ROW BEGIN

DECLARE new_level INT;
DECLARE parent_woodenleg VARCHAR(100);
DECLARE new_woodenleg VARCHAR(100);
DECLARE cur_id VARCHAR(100);
DECLARE c_parent CURSOR FOR SELECT woodenleg,level+1 FROM `cmd_param` WHERE id = NEW.parent_id;

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
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `device`
--

CREATE TABLE IF NOT EXISTS `device` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(11) DEFAULT NULL,
  `area_id` int(11) NOT NULL,
  `area_code` varchar(20) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `lat` double DEFAULT NULL,
  `lng` double DEFAULT NULL,
  `status` varchar(1) NOT NULL DEFAULT '0',
  `gateway_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=38 ;

--
-- Dumping data for table `device`
--

INSERT INTO `device` (`id`, `code`, `area_id`, `area_code`, `address`, `lat`, `lng`, `status`, `gateway_id`) VALUES
(1, 'ATM001', 13, 'HN', '177 Bùi Thị Xuân, P. Bùi Thị Xuân, Quận Hai Bà Trưng, Hà Nội', 21.012076, 105.849817, '1', NULL),
(2, 'ATM002', 13, 'HN', '47 - 49 Mai Hắc Đế, P. Bùi Thị Xuân, Quận Hai Bà Trưng, Hà Nội', 21.0148, 105.850879, '1', NULL),
(3, 'ATM003', 13, 'HN', '15 Tô Hiến Thành, Quận Hai Bà Trưng, Hà Nội', 21.013478, 105.850922, '1', NULL),
(4, 'ATM004', 15, 'HN', 'kiều mai,Phú Diễn,Từ Liêm,Hà Nội', 21.043875, 105.749054, '0', NULL),
(5, 'ATM005', 2, 'HN', '18 Phan Bội Châu, Quận Hoàn Kiếm, Hà Nội', 21.026498, 105.843873, '1', NULL),
(6, 'ATM006', 2, 'HN', '66 Tô Hiến Thành, Quận Hai Bà Trưng, Hà Nội', 21.013383, 105.849844, '1', NULL),
(7, 'ATM001', 17, 'HCM', '99 Võ Văn Tần, P.6, Quận 3, TP. HCM', 10.776229, 106.690003, '1', NULL),
(8, 'ATM003', 17, 'HCM', '190 Nguyễn Văn Thủ, Quận 1, TP. HCM', 10.785725, 106.695067, '0', NULL),
(9, 'ATM002', 17, 'HCM', '84 Đặng Văn Ngữ, P. 10, Quận Phú Nhuận, TP. HCM', 10.794736, 106.669564, '1', NULL),
(10, 'ATM04', 17, 'HCM', '94 Đặng Văn Ngữ, P. 10, Quận Phú Nhuận, TP. HCM', 10.79936, 106.81231, '1', NULL),
(11, 'ATM007', 13, 'HN', '31 Lò Đúc, Quận Hai Bà Trưng, Hà Nội', 21.017139, 105.855718, '1', NULL),
(12, 'ATM008', 13, 'HN', '81 Đại Cồ Việt, Quận Hai Bà Trưng, Hà Nội', 21.00815, 105.847342, '1', NULL),
(13, 'ATM009', 13, 'HN', '26 Trần Xuân Soạn, Quận Hai Bà Trưng, Hà Nội', 21.017259, 105.854918, '1', NULL),
(14, 'ATM0010', 13, 'HN', '88 - 89 Trương Định, Quận Hai Bà Trưng, Hà Nội', 20.993526, 105.849629, '1', NULL),
(15, 'ATM0011', 13, 'HN', '67 Tuệ Tĩnh, Quận Hai Bà Trưng, Hà Nội', 21.015163, 105.848839, '1', NULL),
(16, 'ATM0012', 13, 'HN', '8 Lê Ngọc Hân, Quận Hai Bà Trưng, Hà Nội', 21.016885, 105.855331, '1', NULL),
(17, 'ATM0013', 13, 'HN', '101 Bà Triệu, Quận Hai Bà Trưng, Hà Nội', 21.019194, 105.849467, '1', NULL),
(18, 'ATM0014', 13, 'HN', '51 Trần Xuân Soạn , Quận Hai Bà Trưng, Hà Nội', 21.016823, 105.853674, '1', NULL),
(19, 'ATM0015', 13, 'HN', '22C Đại La, Quận Hai Bà Trưng, Hà Nội', 20.968059, 105.82657, '0', NULL),
(20, 'ATM0016', 13, 'HN', '109 Tạ Quang Bửu, Quận Hai Bà Trưng, Hà Nội', 21.001655, 105.849355, '1', NULL),
(21, 'ATM0017', 13, 'HN', '3 Phù Đổng Thiên Vương, Quận Hai Bà Trưng, Hà Nội', 21.016743, 105.853797, '1', NULL),
(22, 'ATM0018', 13, 'HN', '39 Nguyễn Du, Quận Hai Bà Trưng, Hà Nội', 21.018586, 105.850214, '1', NULL),
(24, 'ATM0019', 13, 'HN', '57 Tô Hiến Thành, Quận Hai Bà Trưng, Hà Nội', 21.013343, 105.848717, '0', NULL),
(25, 'ATM0020', 13, 'HN', '35B Nguyễn Bỉnh Khiêm, P. Lê Đại Hành, Quận Hai Bà Trưng, Hà Nội', 21.014685, 105.848406, '0', NULL),
(26, 'ATM0021', 13, 'HN', '90 Tô Hiến Thành, Quận Hai Bà Trưng, Hà Nội', 21.013278, 105.848497, '1', NULL),
(27, 'ATM0022', 13, 'HN', '42 Võ Thị Sáu, Quận Hai Bà Trưng, Hà Nội', 21.006197, 105.855031, '1', NULL),
(28, 'ATM0023', 13, 'HN', '434 Trần Khát Chân, Quận Hai Bà Trưng, Hà Nội', 21.009197, 105.855058, '1', NULL),
(29, 'ATM0024', 13, 'HN', 'Phố Đoàn Trần Nghiệp, Quận Hai Bà Trưng, Hà Nội', 21.011823, 105.849804, '1', NULL),
(30, 'ATM0025', 13, 'HN', '19 Nguyễn Bỉnh Khiêm, Quận Hai Bà Trưng, Hà Nội', 21.016403, 105.848234, '0', NULL),
(31, 'ATM0026', 13, 'HN', '250 Bạch Mai, Quận Hai Bà Trưng, Hà Nội', 21.002997, 105.850874, '0', NULL),
(32, 'ATM0027', 13, 'HN', '23 Lò Đúc, Quận Hai Bà Trưng, Hà Nội', 21.017434, 105.855771, '0', NULL),
(33, 'ATM0028', 13, 'HN', '25 Ngô Thì Nhậm, Quận Hai Bà Trưng, Hà Nội', 21.01749, 105.852918, '0', NULL),
(34, 'ATM0029', 13, 'HN', '1 Phố Huế, Quận Hai Bà Trưng, Hà Nội', 21.019457, 105.851769, '0', NULL),
(35, 'ATM0030', 13, 'HN', '37 Trần Nhân Tông, Quận Hai Bà Trưng, Hà Nội', 21.016893, 105.850224, '1', NULL),
(36, 'ATM0031', 13, 'HN', 'Phố Tô Hiến Thành, Quận Hai Bà Trưng, Hà Nội', 21.013341, 105.849149, '1', NULL),
(37, 'ATM0032', 13, 'HN', '61 Nguyễn Du, Quận Hai Bà Trưng, Hà Nội', 21.020038, 105.843707, '1', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `device_infor`
--

CREATE TABLE IF NOT EXISTS `device_infor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device_id` int(11) NOT NULL,
  `device_pro_id` int(11) NOT NULL,
  `value` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=81 ;

--
-- Dumping data for table `device_infor`
--

INSERT INTO `device_infor` (`id`, `device_id`, `device_pro_id`, `value`) VALUES
(1, 1, 1, 31),
(2, 1, 2, 12),
(3, 1, 3, 12),
(4, 1, 4, 32),
(5, 2, 1, 12),
(6, 2, 2, 12),
(7, 2, 4, 12),
(8, 2, 3, 23),
(9, 3, 1, 21),
(10, 3, 2, 43),
(11, 3, 4, 12),
(12, 3, 3, 43),
(13, 4, 1, 39),
(14, 4, 2, 10),
(15, 5, 1, 23),
(16, 5, 2, 33),
(17, 6, 1, 37),
(18, 6, 2, 27),
(19, 7, 1, 28),
(20, 7, 2, 18),
(21, 8, 1, 18),
(22, 8, 2, 38),
(23, 9, 1, 34),
(24, 9, 2, 34),
(25, 11, 1, 35),
(26, 11, 2, 40),
(27, 12, 1, 28),
(28, 12, 2, 68),
(29, 13, 1, 22),
(30, 13, 2, 42),
(31, 14, 1, 10),
(32, 14, 2, 30),
(33, 15, 1, 42),
(34, 15, 2, 32),
(35, 16, 1, 32),
(36, 16, 2, 52),
(37, 17, 1, 33),
(38, 17, 2, 43),
(39, 18, 1, 34),
(40, 18, 2, 84),
(41, 19, 1, 33),
(42, 19, 2, 33),
(43, 20, 1, 32),
(44, 20, 2, 47),
(45, 21, 1, 24),
(46, 21, 2, 53),
(47, 22, 1, 42),
(48, 22, 2, 82),
(49, 23, 1, 38),
(50, 23, 2, 53),
(51, 24, 1, 24),
(52, 24, 2, 24),
(53, 25, 1, 36),
(54, 25, 2, 46),
(55, 25, 1, 33),
(56, 25, 2, 43),
(57, 26, 1, 22),
(58, 26, 2, 25),
(59, 27, 1, 20),
(60, 27, 2, 83),
(61, 28, 1, 32),
(62, 28, 2, 42),
(63, 29, 1, 24),
(64, 29, 2, 24),
(65, 30, 1, 33),
(66, 30, 2, 33),
(67, 31, 1, 24),
(68, 31, 2, 76),
(69, 32, 1, 42),
(70, 32, 2, 42),
(71, 33, 1, 24),
(72, 33, 2, 42),
(73, 34, 1, 35),
(74, 34, 2, 44),
(75, 35, 1, 43),
(76, 35, 2, 25),
(77, 36, 1, 24),
(78, 36, 2, 24),
(79, 37, 1, 35),
(80, 37, 2, 35);

-- --------------------------------------------------------

--
-- Table structure for table `device_log`
--

CREATE TABLE IF NOT EXISTS `device_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device_id` int(11) NOT NULL,
  `reason_id` int(11) NOT NULL,
  `device_infor_id` int(11) DEFAULT NULL,
  `old_value` float DEFAULT NULL,
  `new_value` float DEFAULT NULL,
  `Log_Time` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `device_properties`
--

CREATE TABLE IF NOT EXISTS `device_properties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `device_properties`
--

INSERT INTO `device_properties` (`id`, `code`, `name`) VALUES
(1, 'TEMP', 'Nhiệt độ'),
(2, 'DA', 'Độ ẩm'),
(5, 'LIGHT', 'Độ sáng'),
(6, 'RICHTE', 'Độ rung');

-- --------------------------------------------------------

--
-- Table structure for table `gateway`
--

CREATE TABLE IF NOT EXISTS `gateway` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mac_add` varchar(100) NOT NULL,
  `connected_server` varchar(50) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `type` varchar(1) DEFAULT NULL,
  `ip_add` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `gateway`
--

INSERT INTO `gateway` (`id`, `mac_add`, `connected_server`, `status`, `type`, `ip_add`) VALUES
(2, 'C4:2C:03:01:04:76', NULL, '0', '2', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `gateway_log`
--

CREATE TABLE IF NOT EXISTS `gateway_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gateway_id` int(11) NOT NULL,
  `issue_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `command` varchar(500) DEFAULT NULL,
  `type` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=19 ;

--
-- Dumping data for table `gateway_log`
--

INSERT INTO `gateway_log` (`id`, `gateway_id`, `issue_date`, `command`, `type`) VALUES
(1, 1, '2013-12-26 10:29:28', 'connect', 'M'),
(2, 1, '2013-12-26 10:35:30', 'connect', 'M'),
(3, 1, '2013-12-26 10:37:37', 'connect', 'M'),
(4, 1, '2013-12-26 10:39:13', 'connect', 'M'),
(5, 1, '2013-12-26 10:42:01', 'connect', 'M'),
(6, 1, '2013-12-26 10:43:21', 'connect', 'M'),
(7, 2, '2013-12-26 10:45:25', 'connect', 'M'),
(8, 2, '2013-12-26 10:52:51', 'connect', 'M'),
(9, 2, '2013-12-26 10:56:41', 'connect', 'M'),
(10, 2, '2013-12-26 10:58:36', 'connect', 'M'),
(11, 2, '2013-12-26 10:59:39', 'connect', 'M'),
(12, 2, '2013-12-26 11:01:16', 'connect', 'M'),
(13, 2, '2013-12-26 11:07:41', 'connect', 'M'),
(14, 2, '2014-01-02 03:49:33', 'connect', 'M'),
(15, 2, '2014-01-02 04:02:34', 'connect', 'M'),
(16, 2, '2014-01-02 06:27:49', 'connect', 'M'),
(17, 2, '2014-01-02 06:42:58', 'connect', 'M'),
(18, 2, '2014-01-02 06:59:30', 'connect', 'M');

-- --------------------------------------------------------

--
-- Table structure for table `language`
--

CREATE TABLE IF NOT EXISTS `language` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(15) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `status` varchar(1) DEFAULT '1',
  `icon` varchar(200) DEFAULT NULL,
  `order` varchar(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ap_param_uk` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `map_pro_cmd`
--

CREATE TABLE IF NOT EXISTS `map_pro_cmd` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pro_id` int(11) NOT NULL,
  `command_param` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `module`
--

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
  `module_action` varchar(100) DEFAULT NULL,
  `module_icon` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=15 ;

--
-- Dumping data for table `module`
--

INSERT INTO `module` (`id`, `parent_id`, `module_type`, `module_name`, `status`, `description`, `order`, `include_menu`, `app_id`, `level`, `woodenleg`, `module_action`, `module_icon`) VALUES
(7, NULL, 'G', 'Admin', '1', 'Quản trị ', '1', '1', 1, '1', '008', 'NONE', 'cogwheels'),
(8, 7, 'M', 'UserManagement', '1', 'Quản lý tài khoản', '1', '1', 1, '2', '008008', 'NONE', NULL),
(9, 7, 'M', 'DepartmentManagement', '1', 'Quản lý phòng ban', '1', '1', 1, '2', '008009', 'NONE', NULL),
(10, NULL, 'G', 'ATM', '1', 'Quản lý ATM', '1', '1', 1, '1', '010', 'NONE', 'stats'),
(11, 10, 'M', 'AreaManagement', '1', 'Quản lý thiết bị', '1', '1', 1, '2', '010011', 'FORM_DEVICE', NULL),
(12, 10, 'M', 'ATMManagement', '1', 'Quản lý ATM', '1', '1', 1, '2', '010012', 'NONE', NULL),
(13, 10, 'M', 'IssueManagement', '1', 'Quản lý sự cố', '1', '1', 1, '2', '010013', 'NONE', NULL),
(14, 10, 'M', 'AreaManagement', '1', 'Quản lý địa bàn', '1', '1', 1, '2', '010014', 'FORM_AREA', NULL);

--
-- Triggers `module`
--
DROP TRIGGER IF EXISTS `module_tg_bf_is`;
DELIMITER //
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
//
DELIMITER ;
DROP TRIGGER IF EXISTS `module_tg_bf_ud`;
DELIMITER //
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
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `reason`
--

CREATE TABLE IF NOT EXISTS `reason` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `reason`
--

INSERT INTO `reason` (`id`, `code`, `name`) VALUES
(1, 'DVRS001', 'Thay đổi nhiệt độ'),
(2, 'DVRS002', 'Thay đổi độ ẩm'),
(3, 'DVRS003', 'Thay đổi độ sáng'),
(4, 'DVRS004', 'Thay đổi độ rung');

-- --------------------------------------------------------

--
-- Table structure for table `server`
--

CREATE TABLE IF NOT EXISTS `server` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip_add` varchar(100) NOT NULL,
  `port` varchar(50) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` int(11) DEFAULT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `username` varchar(15) DEFAULT NULL,
  `password` varchar(500) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `sex` varchar(1) NOT NULL DEFAULT '1',
  `email` varchar(200) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `id_no` varchar(30) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `birth_day` date DEFAULT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fullname` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_uk_1` (`username`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `dept_id`, `owner_id`, `username`, `password`, `status`, `sex`, `email`, `phone`, `id_no`, `address`, `birth_day`, `create_date`, `fullname`) VALUES
(1, NULL, NULL, 'fsdfs', 'fdsf', '1', '', NULL, NULL, NULL, NULL, NULL, '2013-11-28 07:49:43', ''),
(2, NULL, NULL, 'TUANNA', '698d51a19d8a121ce581499d7b701668', '1', '1', NULL, NULL, NULL, NULL, NULL, '2014-01-07 08:14:42', 'Ngô Anh Tuấn');

-- --------------------------------------------------------

--
-- Table structure for table `user_module_right`
--

CREATE TABLE IF NOT EXISTS `user_module_right` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `module_id` int(11) NOT NULL,
  `right_code` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
