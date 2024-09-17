CREATE TABLE "drug" (
	"id"	INTEGER,
	"name"	TEXT,
	"description"	TEXT DEFAULT NULL,
	"shelfQuantity"	INTEGER NOT NULL DEFAULT 0,
	"minShelfQuantity"	INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY("id")
);