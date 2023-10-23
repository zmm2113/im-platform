/*
SQLyog Ultimate v11.25 (64 bit)
MySQL - 5.7.21-log : Database - boot-im
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`boot-im` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `boot-im`;

/*Table structure for table `chat_apply` */

DROP TABLE IF EXISTS `chat_apply`;

CREATE TABLE `chat_apply` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `from_id` bigint(20) DEFAULT NULL COMMENT '发起id',
  `to_id` bigint(20) DEFAULT NULL COMMENT '接收id',
  `target_id` bigint(20) DEFAULT NULL COMMENT '目标id',
  `apply_type` char(1) DEFAULT NULL COMMENT '申请类型1好友2群组',
  `apply_status` char(1) DEFAULT NULL COMMENT '申请状态0无1同意2拒绝3忽略',
  `apply_source` char(1) DEFAULT NULL COMMENT '申请来源',
  `reason` varchar(200) DEFAULT NULL COMMENT '理由',
  `create_time` datetime DEFAULT NULL COMMENT '申请时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友申请表';

/*Table structure for table `chat_collect` */

DROP TABLE IF EXISTS `chat_collect`;

CREATE TABLE `chat_collect` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `collect_type` varchar(20) DEFAULT NULL COMMENT '收藏类型',
  `content` longtext COMMENT '内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

/*Table structure for table `chat_feedback` */

DROP TABLE IF EXISTS `chat_feedback`;

CREATE TABLE `chat_feedback` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `images` varchar(2000) DEFAULT NULL COMMENT '图片',
  `content` longtext COMMENT '内容',
  `version` varchar(20) DEFAULT '1.0.0' COMMENT '提交版本',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='建议反馈';

/*Table structure for table `chat_friend` */

DROP TABLE IF EXISTS `chat_friend`;

CREATE TABLE `chat_friend` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `from_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `to_id` bigint(20) DEFAULT NULL COMMENT '好友id',
  `remark` varchar(32) DEFAULT NULL COMMENT '备注',
  `black` char(1) DEFAULT 'N' COMMENT '黑名单',
  `source` char(1) DEFAULT NULL COMMENT '好友来源',
  `top` char(1) DEFAULT 'N' COMMENT '是否置顶',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user` (`from_id`,`to_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友表';

/*Table structure for table `chat_group` */

DROP TABLE IF EXISTS `chat_group`;

CREATE TABLE `chat_group` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `name` varchar(50) DEFAULT NULL COMMENT '群名',
  `notice` varchar(200) DEFAULT NULL COMMENT '公告',
  `portrait` longtext COMMENT '头像',
  `master` bigint(20) DEFAULT NULL COMMENT '群主',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群组';

/*Table structure for table `chat_group_info` */

DROP TABLE IF EXISTS `chat_group_info`;

CREATE TABLE `chat_group_info` (
  `info_id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `group_id` bigint(20) DEFAULT NULL COMMENT '群组id',
  `top` char(1) DEFAULT 'N' COMMENT '是否置顶',
  `disturb` char(1) DEFAULT 'N' COMMENT '是否免打扰',
  `keep_group` char(1) DEFAULT 'N' COMMENT '是否保存群组',
  `create_time` datetime DEFAULT NULL COMMENT '加入时间',
  PRIMARY KEY (`info_id`),
  UNIQUE KEY `idx_group` (`user_id`,`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `chat_msg` */

DROP TABLE IF EXISTS `chat_msg`;

CREATE TABLE `chat_msg` (
  `id` bigint(20) NOT NULL COMMENT '消息主键',
  `from_id` bigint(20) DEFAULT NULL COMMENT '发送人',
  `to_id` bigint(20) DEFAULT NULL COMMENT '接收人',
  `msg_type` varchar(20) DEFAULT NULL COMMENT '消息类型',
  `talk_type` varchar(20) DEFAULT NULL COMMENT '聊天类型',
  `content` longtext COMMENT '消息内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息';

/*Table structure for table `chat_topic` */

DROP TABLE IF EXISTS `chat_topic`;

CREATE TABLE `chat_topic` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `topic_type` varchar(20) DEFAULT NULL COMMENT '类型',
  `content` varchar(2000) DEFAULT NULL COMMENT '内容',
  `location` varchar(200) DEFAULT NULL COMMENT '经纬度',
  `create_time` datetime DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主题';

/*Table structure for table `chat_topic_like` */

DROP TABLE IF EXISTS `chat_topic_like`;

CREATE TABLE `chat_topic_like` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `topic_id` bigint(20) DEFAULT NULL COMMENT '帖子id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `has_like` char(1) DEFAULT 'Y' COMMENT '是否点赞',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞';

/*Table structure for table `chat_topic_reply` */

DROP TABLE IF EXISTS `chat_topic_reply`;

CREATE TABLE `chat_topic_reply` (
  `reply_id` bigint(20) NOT NULL COMMENT '主键',
  `reply_type` char(1) DEFAULT NULL COMMENT '回复类型1帖子2用户',
  `reply_status` char(1) DEFAULT NULL COMMENT '回复状态',
  `content` longtext COMMENT '回复内容',
  `topic_id` bigint(20) DEFAULT NULL COMMENT '帖子id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `target_id` bigint(20) DEFAULT NULL COMMENT '目标id',
  `create_time` datetime DEFAULT NULL COMMENT '回复时间',
  PRIMARY KEY (`reply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子回复表';

/*Table structure for table `chat_user` */

DROP TABLE IF EXISTS `chat_user`;

CREATE TABLE `chat_user` (
  `user_id` bigint(20) NOT NULL COMMENT '主键',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `chat_no` varchar(32) DEFAULT NULL COMMENT '微聊号码',
  `nick_name` varchar(32) DEFAULT NULL COMMENT '昵称',
  `token` varchar(32) DEFAULT NULL COMMENT '用户token',
  `gender` varchar(1) DEFAULT '1' COMMENT '性别1男0女',
  `portrait` varchar(2000) DEFAULT NULL COMMENT '头像',
  `intro` varchar(200) DEFAULT NULL COMMENT '介绍',
  `cover` varchar(2000) DEFAULT NULL COMMENT '封面',
  `provinces` varchar(20) DEFAULT NULL COMMENT '省份',
  `city` varchar(20) DEFAULT NULL COMMENT '城市',
  `salt` varchar(4) DEFAULT NULL COMMENT '盐',
  `password` varchar(32) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NULL COMMENT '注册时间',
  `version` varchar(20) DEFAULT '1.0.0' COMMENT '版本信息',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '注销0正常null注销',
  `deleted_time` datetime DEFAULT NULL COMMENT '注销时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_phone` (`phone`,`deleted`),
  UNIQUE KEY `idx_no` (`chat_no`,`deleted_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

/*Table structure for table `chat_version` */

DROP TABLE IF EXISTS `chat_version`;

CREATE TABLE `chat_version` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `version` varchar(20) DEFAULT NULL COMMENT '版本',
  `url` varchar(2000) DEFAULT NULL COMMENT '地址',
  `content` longtext COMMENT '内容',
  `descr` varchar(200) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='版本';

/*Data for the table `chat_version` */

insert  into `chat_version`(`id`,`version`,`url`,`content`,`descr`) values (1,'1.0.0','http://im.q3z3.com/public/am.html','我是用户协议','用户协议'),(2,'1.0.0','http://im.q3z3.com/public/im.apk','我是安卓包','安卓升级包'),(3,'1.0.0','https://www.baidu.com/3','我是iOS包','iOS升级包');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
