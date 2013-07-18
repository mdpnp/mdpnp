delimiter $$

CREATE TABLE `hits` (
  `tm` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `remote_addr` varchar(255) DEFAULT NULL,
  `remote_host` varchar(255) DEFAULT NULL,
  `query_string` varchar(255) DEFAULT NULL,
  `context_path` varchar(255) DEFAULT NULL,
  `path_info` varchar(255) DEFAULT NULL,
  `domain_name` varchar(255) DEFAULT NULL,
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3225 DEFAULT CHARSET=latin1$$

