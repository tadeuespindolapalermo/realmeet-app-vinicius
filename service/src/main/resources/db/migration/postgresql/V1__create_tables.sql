CREATE TABLE room (
  id      SERIAL      NOT NULL,
  name    VARCHAR(25) NOT NULL,
  seats   INT         NOT NULL,
  active  BOOLEAN     NOT NULL,
  PRIMARY KEY (id)
 );
