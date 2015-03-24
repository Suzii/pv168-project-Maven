CREATE TABLE "GUEST" (
    "ID" BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "NAME" VARCHAR(255) CONSTRAINT "GUEST_NAME_NOT_NULL" NOT NULL
                        CONSTRAINT "GUEST_NAME_NOT_EMPTY" CHECK("NAME" = ''),
    "PASSPORT_NO" VARCHAR(255),
    "EMAIL" VARCHAR(255),
    "PHONE" VARCHAR(255),
    "DATE_OF_BIRTH" DATE
);

CREATE TABLE "ROOM" (
    "ID" BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "NUMBER" VARCHAR(10)    CONSTRAINT "ROOM_NUMBER_NOT_NULL" NOT NULL
                            CONSTRAINT "ROOM_NUMBER_UPPERCASE" CHECK("NUMBER" = UPPER("NUMBER"),
    "CAPACITY" INTEGER  CONSTRAINT "ROOM_CAPACITY_NOT_NULL" NOT NULL
                        CONSTRAINT "ROOM_CAPACITY_POSITIVE" CHECK("CAPACITY" > 0),
    "PRICE_PER_NIGHT" DECIMAL(12,2) CONSTRAINT "PRICE_PER_NIGHT_NOT_NULL" NOT NULL
                            CONSTRAINT "PRICE_PER_NIGHT_POSITIVE" CHECK("PRICE_PER_NIGHT" > 0),
    "BATHROOM" BOOLEAN CONSTRAINT "BATHROOM_NOT_NULL" NOT NULL,
    "ROOM_TYPE" VARCHAR(255), -- staci pouzit string alebo to musi mat aj idcko ,,ciarka????
    CONSTRAINT FK_ROOM_TYPE FOREIGN KEY("ROOM_TYPE") REFERENCES "ROOM_TYPE"("TYPE")
);

CREATE TABLE "STAY" (
    "ID" BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "START_DATE" DATE CONSTRAINT "START_DATE_NOT_NULL" NOT NULL,
    "EXPECTED_END_DATE" DATE CONSTRAINT "EXPECTED_END_AFTER_START" CHECK("START_DATE" < "EXPECTED_END_DATE"),
    "REAL_END_DATE" DATE CONSTRAINT "REAL_END_AFTER_START" CHECK("START_DATE" < "REAL_END_DATE"),
    "GUEST_ID" BIGINT CONSTRAINT "GUEST_ID_NOT_NULL" NOT NULL,
    "ROOM_ID" BIGINT CONSTRAINT "ROOM_ID_NOT_NULL" NOT NULL,
    "MINIBAR_COSTS" DECIMAL(12,2) CONSTRAINT "MINIBAR_COSTS_POSITIVE" CHECK("MINIBAR_COSTS" > 0),
    CONSTRAINT "FK_GUEST_ID" FOREIGN KET("GUEST_ID") REFERENCES "GUEST"("ID"), --preklepy ,ciarka
    CONSTRAINT "FK_ROOM_ID" FOREIGN KET("ROOM_ID") REFERENCES "ROOM"("ID")
);

CREATE TABLE "ROOM_TYPE" (
    --"ID" BIGINT 
    "TYPE" VARCHAR(255) PRIMARY KEY
);

INSERT INTO "ROOM_TYPE" VALUES('STANDARD');
INSERT INTO "ROOM_TYPE" VALUES('SUPERIOR');
INSERT INTO "ROOM_TYPE" VALUES('APARTMENT');
INSERT INTO "ROOM_TYPE" VALUES('STUDIO');