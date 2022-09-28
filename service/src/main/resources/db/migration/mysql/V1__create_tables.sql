CREATE TABLE `room` (
  `id`      BIGINT      NOT NULL AUTO_INCREMENT,
  `name`    VARCHAR(25) NOT NULL,
  `seats`   INT         NOT NULL,
  `active`  TINYINT     NOT NULL,
  PRIMARY KEY (`id`)
 );
