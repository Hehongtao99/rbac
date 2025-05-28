/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : ad

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 28/05/2025 12:07:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for advertisement_application
-- ----------------------------
DROP TABLE IF EXISTS `advertisement_application`;
CREATE TABLE `advertisement_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请编号',
  `position_id` bigint NOT NULL COMMENT '广告位ID',
  `region_id` bigint NOT NULL COMMENT '区域ID',
  `position_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '广告位置',
  `ad_setting_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '户外广告设置类型',
  `ad_nature` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '广告性质',
  `area` decimal(10, 2) NOT NULL COMMENT '面积（平方米）',
  `total_area` decimal(10, 2) NOT NULL COMMENT '总面积（平方米）',
  `specification` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `quantity` int NULL DEFAULT 1 COMMENT '数量（块）',
  `audit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核,APPROVED-已通过,REJECTED-已拒绝',
  `application_form_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '重庆市大型户外广告设置申请表',
  `safety_commitment_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '户外广告设施施工、运行安全承诺书',
  `site_agreement_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '大型户外广告场地协议',
  `effect_drawing_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '大型户外广告设置实景效果图',
  `structure_design_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '广告设施结构设计图',
  `original_drawing_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '户外广告设置依托的建筑物原貌图',
  `property_certificate_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设置场地建筑物设施等的产权证书',
  `apply_user_id` bigint NULL DEFAULT NULL COMMENT '申请人ID',
  `apply_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `audit_user_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_application_code`(`application_code` ASC) USING BTREE,
  INDEX `idx_position_id`(`position_id` ASC) USING BTREE,
  INDEX `idx_region_id`(`region_id` ASC) USING BTREE,
  INDEX `idx_audit_status`(`audit_status` ASC) USING BTREE,
  INDEX `idx_apply_user_id`(`apply_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '广告申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of advertisement_application
-- ----------------------------
INSERT INTO `advertisement_application` VALUES (1, 'APP2024001', 1, 11, '王府井大街东侧', '大型立柱广告', '商业广告', 20.50, 20.50, '4m×5m', 1, 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2025-05-28 02:58:25', NULL, NULL, NULL, '2025-05-28 02:58:25', '2025-05-28 02:58:25');
INSERT INTO `advertisement_application` VALUES (2, 'APP2024002', 2, 11, '长安街西单路口', '楼体广告', '公益广告', 35.80, 71.60, '8m×4.5m', 2, 'APPROVED', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2025-05-28 02:58:25', NULL, NULL, NULL, '2025-05-28 02:58:25', '2025-05-28 02:58:25');
INSERT INTO `advertisement_application` VALUES (3, 'APP2024003', 3, 12, '景山公园南门', '候车亭广告', '商业广告', 15.20, 15.20, '3m×5m', 1, 'REJECTED', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2025-05-28 02:58:25', NULL, NULL, NULL, '2025-05-28 02:58:25', '2025-05-28 02:58:25');

-- ----------------------------
-- Table structure for advertisement_position
-- ----------------------------
DROP TABLE IF EXISTS `advertisement_position`;
CREATE TABLE `advertisement_position`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '广告位编号',
  `position_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '广告位置',
  `area` decimal(10, 2) NOT NULL COMMENT '面积（平方米）',
  `ad_nature` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '广告性质',
  `region_id` bigint NOT NULL COMMENT '区域ID（关联organization表）',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '地图点位经度',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '地图点位纬度',
  `position_image` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '广告位实图URL',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `position_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '广告位多图URLs，JSON格式存储',
  `application_status` VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT '申请状态：AVAILABLE-可申请,APPLIED-已申请,APPROVED-已通过,REJECTED-已拒绝',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_code`(`code` ASC) USING BTREE,
  INDEX `idx_region_id`(`region_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '广告位基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of advertisement_position
-- ----------------------------
INSERT INTO `advertisement_position` VALUES (1, 'AD001', '王府井大街东侧', 20.50, '商业广告', 11, 116.403414, 39.918695, '/uploads/positions/ad001.jpg', 1, '2025-05-28 02:58:25', '2025-05-28 02:58:25', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (2, 'AD002', '长安街西单路口', 35.80, '公益广告', 11, 116.375833, 39.906944, '/uploads/positions/ad002.jpg', 1, '2025-05-28 02:58:25', '2025-05-28 02:58:25', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (3, 'AD003', '景山公园南门', 15.20, '商业广告', 12, 116.395833, 39.922500, '/uploads/positions/ad003.jpg', 1, '2025-05-28 02:58:25', '2025-05-28 02:58:25', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (4, 'AD004', '什刹海西岸', 28.60, '旅游宣传', 13, 116.383333, 39.936111, '/uploads/positions/ad004.jpg', 1, '2025-05-28 02:58:25', '2025-05-28 02:58:25', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (5, 'AD005', '西长安街政府大楼', 45.00, '政府公告', 14, 116.366667, 39.906667, '/uploads/positions/ad005.jpg', 1, '2025-05-28 02:58:25', '2025-05-28 02:58:25', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (6, 'AD001', '王府井大街东侧', 20.50, '商业广告', 11, 116.403414, 39.918695, '/uploads/positions/ad001.jpg', 1, '2025-05-28 03:05:35', '2025-05-28 03:05:35', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (7, 'AD002', '长安街西单路口', 35.80, '公益广告', 11, 116.375833, 39.906944, '/uploads/positions/ad002.jpg', 1, '2025-05-28 03:05:35', '2025-05-28 03:05:35', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (8, 'AD003', '景山公园南门', 15.20, '商业广告', 12, 116.395833, 39.922500, '/uploads/positions/ad003.jpg', 1, '2025-05-28 03:05:35', '2025-05-28 03:05:35', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (9, 'AD004', '什刹海西岸', 28.60, '旅游宣传', 13, 116.383333, 39.936111, '/uploads/positions/ad004.jpg', 1, '2025-05-28 03:05:35', '2025-05-28 03:05:35', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (10, 'AD005', '西长安街政府大楼', 45.00, '政府公告', 14, 116.366667, 39.906667, '/uploads/positions/ad005.jpg', 1, '2025-05-28 03:05:35', '2025-05-28 03:05:35', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (11, 'AD001', '东华门地铁站出口广告位', 12.50, '商业广告', 11, 116.403500, 39.918700, '[\"/uploads/2025/05/28/063133d0-f76a-46cc-ac60-3212286a5ec7.jpg\",\"/uploads/2025/05/28/fc25ebe2-401f-412a-a890-d7c157a2135a.jpg\",\"/uploads/2025/05/28/ed6d660f-97f3-4652-b4c3-7b69c1f7f215.jpg\"]', 1, '2025-05-28 03:28:04', '2025-05-28 11:57:26', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (12, 'AD002', '东华门商业街LED屏', 25.00, '商业广告', 11, 116.403200, 39.918500, '/uploads/positions/donghuamen_ad2.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (13, 'AD003', '景山公园南门宣传栏', 8.00, '公益广告', 12, 116.395700, 39.928200, '/uploads/positions/jingshan_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (14, 'AD004', '什刹海酒吧街广告牌', 15.00, '旅游宣传', 13, 116.385600, 39.937600, '/uploads/positions/shichahai_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (15, 'AD005', '西长安街政府公告栏', 6.00, '政府公告', 14, 116.366500, 39.906300, '/uploads/positions/xichanganjie_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (16, 'AD006', '三里屯太古里户外屏', 30.00, '商业广告', 15, 116.447500, 39.936200, '/uploads/positions/sanlitun_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (17, 'AD007', '建国门外CBD广告塔', 45.00, '商业广告', 16, 116.436200, 39.909900, '/uploads/positions/jianguomenwai_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (18, 'AD008', '南京路步行街大屏', 40.00, '商业广告', 17, 121.485400, 31.240100, '/uploads/positions/nanjingdonglu_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL, 'AVAILABLE');
INSERT INTO `advertisement_position` VALUES (1, 'AD001', '王府井大街东侧', 20.50, '商业广告', 11, 116.403414, 39.918695, '/uploads/positions/ad001.jpg', 1, '2025-05-28 02:58:25', '2025-05-28 02:58:25', NULL);
INSERT INTO `advertisement_position` VALUES (2, 'AD002', '长安街西单路口', 35.80, '公益广告', 11, 116.375833, 39.906944, '/uploads/positions/ad002.jpg', 1, '2025-05-28 02:58:25', '2025-05-28 02:58:25', NULL);
INSERT INTO `advertisement_position` VALUES (3, 'AD003', '景山公园南门', 15.20, '商业广告', 12, 116.395833, 39.922500, '/uploads/positions/ad003.jpg', 1, '2025-05-28 02:58:25', '2025-05-28 02:58:25', NULL);
INSERT INTO `advertisement_position` VALUES (4, 'AD004', '什刹海西岸', 28.60, '旅游宣传', 13, 116.383333, 39.936111, '/uploads/positions/ad004.jpg', 1, '2025-05-28 02:58:25', '2025-05-28 02:58:25', NULL);
INSERT INTO `advertisement_position` VALUES (5, 'AD005', '西长安街政府大楼', 45.00, '政府公告', 14, 116.366667, 39.906667, '/uploads/positions/ad005.jpg', 1, '2025-05-28 02:58:25', '2025-05-28 02:58:25', NULL);
INSERT INTO `advertisement_position` VALUES (6, 'AD001', '王府井大街东侧', 20.50, '商业广告', 11, 116.403414, 39.918695, '/uploads/positions/ad001.jpg', 1, '2025-05-28 03:05:35', '2025-05-28 03:05:35', NULL);
INSERT INTO `advertisement_position` VALUES (7, 'AD002', '长安街西单路口', 35.80, '公益广告', 11, 116.375833, 39.906944, '/uploads/positions/ad002.jpg', 1, '2025-05-28 03:05:35', '2025-05-28 03:05:35', NULL);
INSERT INTO `advertisement_position` VALUES (8, 'AD003', '景山公园南门', 15.20, '商业广告', 12, 116.395833, 39.922500, '/uploads/positions/ad003.jpg', 1, '2025-05-28 03:05:35', '2025-05-28 03:05:35', NULL);
INSERT INTO `advertisement_position` VALUES (9, 'AD004', '什刹海西岸', 28.60, '旅游宣传', 13, 116.383333, 39.936111, '/uploads/positions/ad004.jpg', 1, '2025-05-28 03:05:35', '2025-05-28 03:05:35', NULL);
INSERT INTO `advertisement_position` VALUES (10, 'AD005', '西长安街政府大楼', 45.00, '政府公告', 14, 116.366667, 39.906667, '/uploads/positions/ad005.jpg', 1, '2025-05-28 03:05:35', '2025-05-28 03:05:35', NULL);
INSERT INTO `advertisement_position` VALUES (11, 'AD001', '东华门地铁站出口广告位', 12.50, '商业广告', 11, 116.403500, 39.918700, '[\"/uploads/2025/05/28/063133d0-f76a-46cc-ac60-3212286a5ec7.jpg\",\"/uploads/2025/05/28/fc25ebe2-401f-412a-a890-d7c157a2135a.jpg\",\"/uploads/2025/05/28/ed6d660f-97f3-4652-b4c3-7b69c1f7f215.jpg\"]', 1, '2025-05-28 03:28:04', '2025-05-28 11:57:26', NULL);
INSERT INTO `advertisement_position` VALUES (12, 'AD002', '东华门商业街LED屏', 25.00, '商业广告', 11, 116.403200, 39.918500, '/uploads/positions/donghuamen_ad2.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL);
INSERT INTO `advertisement_position` VALUES (13, 'AD003', '景山公园南门宣传栏', 8.00, '公益广告', 12, 116.395700, 39.928200, '/uploads/positions/jingshan_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL);
INSERT INTO `advertisement_position` VALUES (14, 'AD004', '什刹海酒吧街广告牌', 15.00, '旅游宣传', 13, 116.385600, 39.937600, '/uploads/positions/shichahai_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL);
INSERT INTO `advertisement_position` VALUES (15, 'AD005', '西长安街政府公告栏', 6.00, '政府公告', 14, 116.366500, 39.906300, '/uploads/positions/xichanganjie_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL);
INSERT INTO `advertisement_position` VALUES (16, 'AD006', '三里屯太古里户外屏', 30.00, '商业广告', 15, 116.447500, 39.936200, '/uploads/positions/sanlitun_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL);
INSERT INTO `advertisement_position` VALUES (17, 'AD007', '建国门外CBD广告塔', 45.00, '商业广告', 16, 116.436200, 39.909900, '/uploads/positions/jianguomenwai_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL);
INSERT INTO `advertisement_position` VALUES (18, 'AD008', '南京路步行街大屏', 40.00, '商业广告', 17, 121.485400, 31.240100, '/uploads/positions/nanjingdonglu_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL);
INSERT INTO `advertisement_position` VALUES (19, 'AD009', '豫园景区指示牌', 10.00, '旅游宣传', 18, 121.492700, 31.227900, '/uploads/positions/yuyuan_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL);
INSERT INTO `advertisement_position` VALUES (20, 'AD010', '徐家汇商圈LED墙', 35.00, '商业广告', 19, 121.435400, 31.196300, '/uploads/positions/xujiahui_ad1.jpg', 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', NULL);

-- ----------------------------
-- Table structure for organization
-- ----------------------------
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组织名称',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级ID',
  `level` int NOT NULL COMMENT '级别：1省 2市 3区 4街道',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织编码',
  `full_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织全称',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `region_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区域图片URL',
  `region_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区域类型：省、市、区、街道',
  `image_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图片URLs，JSON格式存储多张图片',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述信息',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用：1启用 0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE,
  INDEX `idx_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '组织管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of organization
-- ----------------------------
INSERT INTO `organization` VALUES (1, '北京市', NULL, 1, '110000', '北京市', NULL, NULL, NULL, '省', NULL, NULL, '中华人民共和国首都', 1, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (2, '上海市', NULL, 1, '310000', '上海市', NULL, NULL, NULL, '省', NULL, NULL, '中华人民共和国直辖市', 2, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (3, '广东省', NULL, 1, '440000', '广东省', NULL, NULL, NULL, '省', NULL, NULL, '中华人民共和国省级行政区', 3, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (4, '东城区', 1, 2, '110101', '北京市东城区', NULL, NULL, NULL, '市', NULL, NULL, '北京市核心城区', 1, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (5, '西城区', 1, 2, '110102', '北京市西城区', NULL, NULL, NULL, '市', NULL, NULL, '北京市核心城区', 2, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (6, '朝阳区', 1, 2, '110105', '北京市朝阳区', NULL, NULL, NULL, '市', NULL, NULL, '北京市主要城区', 3, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (7, '黄浦区', 2, 2, '310101', '上海市黄浦区', NULL, NULL, NULL, '市', NULL, NULL, '上海市核心城区', 1, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (8, '徐汇区', 2, 2, '310104', '上海市徐汇区', NULL, NULL, NULL, '市', NULL, NULL, '上海市主要城区', 2, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (9, '广州市', 3, 2, '440100', '广东省广州市', NULL, NULL, NULL, '市', NULL, NULL, '广东省省会城市', 1, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (10, '深圳市', 3, 2, '440300', '广东省深圳市', NULL, NULL, NULL, '市', NULL, NULL, '广东省经济特区', 2, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (11, '东华门街道', 4, 3, '11010100100', '北京市东城区东华门街道', 116.403414, 39.918625, '/uploads/images/donghuamen.jpg', '街道', '/uploads/2025/05/28/25c3836b-93db-4777-9468-163753de9b40.jpg', NULL, '东城区核心街道，历史悠久，文化底蕴深厚', 1, 1, '2025-05-28 03:28:04', '2025-05-28 11:42:56', 1);
INSERT INTO `organization` VALUES (12, '景山街道', 4, 3, '11010100200', '北京市东城区景山街道', 116.395643, 39.928168, '/uploads/images/jingshan.jpg', '街道', NULL, NULL, '景山公园所在街道，旅游资源丰富', 2, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (13, '什刹海街道', 5, 3, '11010200100', '北京市西城区什刹海街道', 116.385564, 39.937501, '/uploads/images/shichahai.jpg', '街道', NULL, NULL, '历史文化街区，胡同文化浓厚', 1, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (14, '西长安街街道', 5, 3, '11010200200', '北京市西城区西长安街街道', 116.366430, 39.906217, '/uploads/images/xichanganjie.jpg', '街道', NULL, NULL, '中央政府所在地，政治中心', 2, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (15, '三里屯街道', 6, 3, '11010500100', '北京市朝阳区三里屯街道', 116.447454, 39.936172, '/uploads/images/sanlitun.jpg', '街道', NULL, NULL, '时尚潮流街区，国际化程度高', 1, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (16, '建国门外街道', 6, 3, '11010500200', '北京市朝阳区建国门外街道', 116.436173, 39.909801, '/uploads/images/jianguomenwai.jpg', '街道', NULL, NULL, '商务中心区，高端写字楼集中', 2, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (17, '南京东路街道', 7, 3, '31010100100', '上海市黄浦区南京东路街道', 121.485349, 31.240078, '/uploads/images/nanjingdonglu.jpg', '街道', NULL, NULL, '上海最繁华的商业街区', 1, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (18, '豫园街道', 7, 3, '31010100200', '上海市黄浦区豫园街道', 121.492644, 31.227829, '/uploads/images/yuyuan.jpg', '街道', NULL, NULL, '传统文化与现代商业并存', 2, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (19, '徐家汇街道', 8, 3, '31010400100', '上海市徐汇区徐家汇街道', 121.435346, 31.196234, '/uploads/images/xujiahui.jpg', '街道', NULL, NULL, '上海重要商业中心之一', 1, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (20, '天河路街道', 9, 3, '44010100100', '广东省广州市天河路街道', 113.326196, 23.135305, '/uploads/images/tianhelu.jpg', '街道', NULL, NULL, '广州CBD核心区域', 1, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);
INSERT INTO `organization` VALUES (21, '福田街道', 10, 3, '44030000100', '广东省深圳市福田街道', 114.064665, 22.526134, '/uploads/images/futian.jpg', '街道', NULL, NULL, '深圳市中心区域', 1, 1, '2025-05-28 03:28:04', '2025-05-28 03:28:04', 1);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `menu_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单编码',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
  `menu_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单类型：MENU-菜单，BUTTON-按钮',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `menu_code`(`menu_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 'system', 0, 'MENU', '/system', '', 'Setting', 1, 1, '系统管理模块', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (2, '用户管理', 'user-management', 1, 'MENU', '/system/user', 'UserManagement', 'User', 1, 1, '用户管理页面', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (3, '角色管理', 'role-management', 1, 'MENU', '/system/role', 'RoleManagement', 'UserFilled', 2, 1, '角色管理页面', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (4, '新增用户', 'user:add', 2, 'BUTTON', '', '', '', 1, 1, '新增用户按钮', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (5, '编辑用户', 'user:edit', 2, 'BUTTON', '', '', '', 2, 1, '编辑用户按钮', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (6, '删除用户', 'user:delete', 2, 'BUTTON', '', '', '', 3, 1, '删除用户按钮', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (7, '分配角色', 'user:assign-role', 2, 'BUTTON', '', '', '', 4, 1, '分配角色按钮', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (8, '新增角色', 'role:add', 3, 'BUTTON', '', '', '', 1, 1, '新增角色按钮', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (9, '编辑角色', 'role:edit', 3, 'BUTTON', '', '', '', 2, 1, '编辑角色按钮', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (10, '删除角色', 'role:delete', 3, 'BUTTON', '', '', '', 3, 1, '删除角色按钮', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (11, '分配权限', 'role:assign-menu', 3, 'BUTTON', '', '', '', 4, 1, '分配权限按钮', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (12, '个人中心', 'profile', 0, 'MENU', '/profile', 'Profile', 'Avatar', 2, 1, '个人中心页面', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (13, '修改密码', 'change-password', 12, 'BUTTON', '', '', '', 1, 1, '修改密码按钮', '2025-05-28 02:15:40', '2025-05-28 02:15:40', 0);
INSERT INTO `sys_menu` VALUES (14, '组织管理', 'organization-management', 1, 'MENU', '/system/organization', 'OrganizationManagement', 'OfficeBuilding', 3, 1, '组织架构管理页面', '2025-05-28 02:24:50', '2025-05-28 02:24:50', 0);
INSERT INTO `sys_menu` VALUES (19, '新增组织', 'organization:add', NULL, 'BUTTON', '', '', '', 1, 1, '新增组织按钮', '2025-05-28 02:29:50', '2025-05-28 02:29:50', 0);
INSERT INTO `sys_menu` VALUES (20, '编辑组织', 'organization:edit', NULL, 'BUTTON', '', '', '', 2, 1, '编辑组织按钮', '2025-05-28 02:29:50', '2025-05-28 02:29:50', 0);
INSERT INTO `sys_menu` VALUES (21, '删除组织', 'organization:delete', NULL, 'BUTTON', '', '', '', 3, 1, '删除组织按钮', '2025-05-28 02:29:50', '2025-05-28 02:29:50', 0);
INSERT INTO `sys_menu` VALUES (27, '广告管理', 'advertisement', 0, 'MENU', '/advertisement', '', 'Document', 3, 1, '广告申请管理模块', '2025-05-28 03:05:35', '2025-05-28 03:05:35', 0);
INSERT INTO `sys_menu` VALUES (28, '申请列表', 'advertisement-list', 27, 'MENU', '/advertisement/list', 'AdvertisementManagement', 'List', 1, 1, '广告申请列表页面', '2025-05-28 03:05:35', '2025-05-28 03:05:35', 0);
INSERT INTO `sys_menu` VALUES (29, '新增申请', 'advertisement:add', 28, 'BUTTON', '', '', '', 1, 1, '新增广告申请按钮', '2025-05-28 03:05:35', '2025-05-28 03:05:35', 0);
INSERT INTO `sys_menu` VALUES (30, '查看详情', 'advertisement:view', 28, 'BUTTON', '', '', '', 2, 1, '查看申请详情按钮', '2025-05-28 03:05:35', '2025-05-28 03:05:35', 0);
INSERT INTO `sys_menu` VALUES (31, '审核申请', 'advertisement:audit', 28, 'BUTTON', '', '', '', 3, 1, '审核申请按钮', '2025-05-28 03:05:35', '2025-05-28 03:05:35', 0);
INSERT INTO `sys_menu` VALUES (32, '申请管理', 'advertisement-application', 27, 'MENU', '/advertisement/applications', 'advertisement/AdvertisementManagement', 'form', 1, 1, '广告申请管理', '2025-05-28 03:35:04', '2025-05-28 03:35:04', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '管理员', 'ADMIN', '系统管理员，拥有所有权限', 1, '2025-05-28 02:15:39', '2025-05-28 02:15:39', 0);
INSERT INTO `sys_role` VALUES (2, '用户', 'USER', '普通用户，拥有基本权限', 1, '2025-05-28 02:15:39', '2025-05-28 02:15:39', 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 1, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (2, 1, 2, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (3, 1, 3, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (4, 1, 4, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (5, 1, 5, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (6, 1, 6, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (7, 1, 7, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (8, 1, 8, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (9, 1, 9, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (10, 1, 10, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (11, 1, 11, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (12, 1, 12, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (13, 1, 13, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (14, 2, 12, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (15, 2, 13, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_role_menu` VALUES (16, 1, 14, '2025-05-28 02:31:42', 0);
INSERT INTO `sys_role_menu` VALUES (17, 1, 19, '2025-05-28 02:31:42', 0);
INSERT INTO `sys_role_menu` VALUES (18, 1, 21, '2025-05-28 02:31:42', 0);
INSERT INTO `sys_role_menu` VALUES (19, 1, 20, '2025-05-28 02:31:42', 0);
INSERT INTO `sys_role_menu` VALUES (23, 1, 27, '2025-05-28 03:05:35', 0);
INSERT INTO `sys_role_menu` VALUES (24, 1, 28, '2025-05-28 03:05:35', 0);
INSERT INTO `sys_role_menu` VALUES (25, 1, 29, '2025-05-28 03:05:35', 0);
INSERT INTO `sys_role_menu` VALUES (26, 1, 31, '2025-05-28 03:05:35', 0);
INSERT INTO `sys_role_menu` VALUES (27, 1, 30, '2025-05-28 03:05:35', 0);
INSERT INTO `sys_role_menu` VALUES (30, 1, 32, '2025-05-28 03:35:04', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '系统管理员', 'admin@example.com', '13800138000', 1, '2025-05-28 02:15:39', '2025-05-28 02:15:39', 0);
INSERT INTO `sys_user` VALUES (2, 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZFzUNjKUFVFDaWdKyUgdEKDOy', '普通用户', 'user@example.com', '13800138001', 1, '2025-05-28 02:15:39', '2025-05-28 02:15:39', 0);
INSERT INTO `sys_user` VALUES (3, '1', '$2a$10$I6pTnkL.WMsR.8ZEXy2tzOOhen3id7VW3e5sIIpGIi512YrqXIhCG', '1', '1', '1', 1, '2025-05-28 10:17:01', '2025-05-28 10:17:01', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_user_role` VALUES (2, 1, 2, '2025-05-28 02:15:40', 0);
INSERT INTO `sys_user_role` VALUES (3, 3, 1, '2025-05-28 10:17:01', 0);

SET FOREIGN_KEY_CHECKS = 1;
