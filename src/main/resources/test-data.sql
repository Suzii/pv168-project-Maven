INSERT INTO "GUEST" (NAME, PASSPORT_NO, EMAIL, PHONE, DATE_OF_BIRTH) 
    VALUES ('James Bond', '007', 'james.bond@queen-service.uk', '0907007007', '1970-01-01');

INSERT INTO "GUEST" (NAME, PASSPORT_NO, EMAIL, PHONE, DATE_OF_BIRTH) 
    VALUES ('James Clark', '123', 'james.clark@superman.com', '0905123123', '1971-01-01');

INSERT INTO "GUEST" (NAME, PASSPORT_NO, EMAIL, PHONE, DATE_OF_BIRTH) 
    VALUES ('Tom Kingsman', '911', 'kingsman@secret-service.uk', '0907911911', '1975-02-01');

INSERT INTO "GUEST" (NAME, PASSPORT_NO, EMAIL, PHONE, DATE_OF_BIRTH) 
    VALUES ('Peter Parkson', '555', 'peter.parkson@spiderman.com', '0901456456', '1977-01-12');

INSERT INTO "ROOM" (NUMBER, CAPACITY , PRICE_PER_NIGHT, BATHROOM , ROOM_TYPE) 
    VALUES ('A001',2,12,0,'STANDARD');

INSERT INTO "ROOM" (NUMBER, CAPACITY , PRICE_PER_NIGHT, BATHROOM , ROOM_TYPE) 
    VALUES ('A012',3,29,1,'SUPERIOR');

INSERT INTO "ROOM" (NUMBER, CAPACITY , PRICE_PER_NIGHT, BATHROOM , ROOM_TYPE) 
    VALUES ('B001',4,38.5,1,'STUDIO');

INSERT INTO "ROOM" (NUMBER, CAPACITY , PRICE_PER_NIGHT, BATHROOM , ROOM_TYPE) 
    VALUES ('B021',1,20,1,'APARTMENT');


INSERT INTO stay("GUEST_ID", "ROOM_ID", "START_DATE", "EXPECTED_END_DATE", "REAL_END_DATE", "MINIBAR_COSTS")
    VALUES ( 1, 1, DATE('2015-01-01'), DATE('2015-01-03'), DATE('2015-01-03'), 1000);

INSERT INTO stay(guest_id, room_id, start_date, expected_end_date, real_end_date, minibar_costs)
    VALUES (5, 4, '2015-01-08', '2015-01-10', '2015-01-10', 1560);

INSERT INTO stay(guest_id, room_id, start_date, expected_end_date, real_end_date, minibar_costs)
    VALUES (2, 1, '2015-01-01', '2015-01-05', '2015-01-05', 10);

INSERT INTO stay(id, guest_id, room_id, start_date, expected_end_date, real_end_date, minibar_costs)
    VALUES (3, 2, '2015-01-01', '2015-01-03', '2015-01-03', 1000);

INSERT INTO stay(id, guest_id, room_id, start_date, expected_end_date, real_end_date, minibar_costs)
    VALUES (4, 4, '2015-01-03', '2015-01-07', '2015-01-07', 120);
 
INSERT INTO stay(id, guest_id, room_id, start_date, expected_end_date, real_end_date, minibar_costs)
    VALUES (3, '2015-01-10', '2015-01-12', '2015-01-12', 14);
