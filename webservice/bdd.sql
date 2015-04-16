-- phpMyAdmin SQL Dump
-- version 4.1.9
-- http://www.phpmyadmin.net
--
-- Client :  mysql51-132.perso
-- Généré le :  Jeu 16 Avril 2015 à 23:24
-- Version du serveur :  5.1.73-2+squeeze+build1+1-log
-- Version de PHP :  5.3.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Base de données :  `kimsavinbdd`
--

-- --------------------------------------------------------

--
-- Structure de la table `qrc_users`
--

CREATE TABLE IF NOT EXISTS `qrc_users` (
  `id_user` int(11) NOT NULL AUTO_INCREMENT,
  `user_login` varchar(88) NOT NULL,
  `user_password` varchar(64) NOT NULL,
  `user_upd` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

INSERT INTO `kimsavinbdd`.`qrc_users` (`user_login`, `user_password`, `user_upd`) VALUES ('toto', 'keepcalm', CURRENT_TIMESTAMP);
