-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: rus_rullers
-- ------------------------------------------------------
-- Server version	8.0.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ruller`
--

DROP TABLE IF EXISTS `ruller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ruller` (
  `ruller_ID` int NOT NULL AUTO_INCREMENT,
  `ruller_firstname` varchar(15) NOT NULL,
  `ruller_patronomic` varchar(20) DEFAULT NULL,
  `ruller_title` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`ruller_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ruller`
--

LOCK TABLES `ruller` WRITE;
/*!40000 ALTER TABLE `ruller` DISABLE KEYS */;
INSERT INTO `ruller` VALUES (1,'Rurik','',''),(2,'Oleg','','Prophet'),(3,'Igor','Rurikid',''),(4,'Olga','',''),(5,'Sviatoslav','Igorevich','Brave');
/*!40000 ALTER TABLE `ruller` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ruller_town_relation`
--

DROP TABLE IF EXISTS `ruller_town_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ruller_town_relation` (
  `foreight_ruller_ID` int NOT NULL,
  `foreight_town_ID` int NOT NULL,
  `start_year` int DEFAULT NULL,
  `end_year` int DEFAULT NULL,
  KEY `foreight_ruller_ID` (`foreight_ruller_ID`),
  KEY `foreight_town_ID` (`foreight_town_ID`),
  CONSTRAINT `ruller_town_relation_ibfk_1` FOREIGN KEY (`foreight_ruller_ID`) REFERENCES `ruller` (`ruller_ID`),
  CONSTRAINT `ruller_town_relation_ibfk_2` FOREIGN KEY (`foreight_town_ID`) REFERENCES `town` (`town_ID`),
  CONSTRAINT `ruller_town_relation_chk_1` CHECK ((`start_year` > 0)),
  CONSTRAINT `ruller_town_relation_chk_2` CHECK ((`end_year` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ruller_town_relation`
--

LOCK TABLES `ruller_town_relation` WRITE;
/*!40000 ALTER TABLE `ruller_town_relation` DISABLE KEYS */;
INSERT INTO `ruller_town_relation` VALUES (1,1,862,879),(2,1,879,912),(2,2,882,912),(3,2,912,945),(4,2,945,960),(5,1,945,969),(5,2,945,972),(5,3,967,972);
/*!40000 ALTER TABLE `ruller_town_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ruller_years_of_life`
--

DROP TABLE IF EXISTS `ruller_years_of_life`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ruller_years_of_life` (
  `foreight_ruller_ID` int DEFAULT NULL,
  `year_of_birth` varchar(10) DEFAULT 'UNKNOWN',
  `year_of_death` varchar(10) DEFAULT 'UNKNOWN',
  KEY `foreight_ruller_ID` (`foreight_ruller_ID`),
  CONSTRAINT `ruller_years_of_life_ibfk_1` FOREIGN KEY (`foreight_ruller_ID`) REFERENCES `ruller` (`ruller_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ruller_years_of_life`
--

LOCK TABLES `ruller_years_of_life` WRITE;
/*!40000 ALTER TABLE `ruller_years_of_life` DISABLE KEYS */;
INSERT INTO `ruller_years_of_life` VALUES (1,'UNKNOWN','879'),(2,'UNKNOWN','912'),(3,'878','945'),(4,'920','969'),(5,'943','972');
/*!40000 ALTER TABLE `ruller_years_of_life` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `town`
--

DROP TABLE IF EXISTS `town`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `town` (
  `town_ID` int NOT NULL AUTO_INCREMENT,
  `town_name` varchar(20) NOT NULL,
  PRIMARY KEY (`town_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `town`
--

LOCK TABLES `town` WRITE;
/*!40000 ALTER TABLE `town` DISABLE KEYS */;
INSERT INTO `town` VALUES (1,'Novgorod'),(2,'Kiev'),(3,'Minsk');
/*!40000 ALTER TABLE `town` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-05-04 15:20:33
