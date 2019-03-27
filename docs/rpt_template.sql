/*
Navicat MySQL Data Transfer

Source Server         : smartbidemo
Source Server Version : 50137
Source Host           : localhost:6688
Source Database       : bulu

Target Server Type    : MYSQL
Target Server Version : 50137
File Encoding         : 65001

Date: 2019-01-17 11:03:26
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `rpt_template`
-- ----------------------------
DROP TABLE IF EXISTS `rpt_template`;
CREATE TABLE `rpt_template` (
  `url` varchar(64) NOT NULL,
  `file_name` varchar(128) DEFAULT NULL,
  `excel_template` blob,
  PRIMARY KEY (`url`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;