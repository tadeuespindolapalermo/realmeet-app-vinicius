CREATE TABLE IF NOT EXISTS allocation (
  id              SERIAL NOT NULL,
  room_id         INT NOT NULL,
  employee_name   VARCHAR(20) NOT NULL,
  employee_email  VARCHAR(30) NOT NULL,
  subject         VARCHAR(60) NOT NULL,
  start_at        TIMESTAMP WITH TIME ZONE NOT NULL,
  end_at          TIMESTAMP WITH TIME ZONE NOT NULL,
  created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,

  PRIMARY KEY (id),
  CONSTRAINT fk_allocation_room_id_room_id FOREIGN KEY (room_id) REFERENCES room (id)
);